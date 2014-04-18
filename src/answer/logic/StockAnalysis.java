package answer.logic;

/*
 * ==================================================================== Licensed
 * to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The ASF licenses this file to you
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Apache Software Foundation. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 *
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.util.Caculator;
import answer.bean.dto.Alldata;
import answer.bean.dto.Rules;

/**
 * How to send a request via proxy using {@link HttpClient}.
 *
 * @since 4.0
 */
@Service(value = "StockAnalysis")
public class StockAnalysis {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(StockAnalysis.class);

    public String analyzedata(List<String> ls_shsz)  {
//        initRules();
       
        List<String> stock_detail = null;
        // shanghai increase
//        double shanghaiincr=getShangHaiHistoryIncr();
        for (int j = 0; j < ls_shsz.size(); j++) {
            // get the detail of the stocks
            stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

            if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                continue;
            }
            double curprice = Double.parseDouble(stock_detail.get(3));
            double ydprice = Double.parseDouble(stock_detail.get(2));

            double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);

            log.info(stock_detail.get(0) + ": increase " + incr + "%");
            // the increase is over the setting
            if (incr >= popertiesHelper.getStocksProperties().getDouble("plan.incr") && incr < 99.99) {
                doAnalysis(stock_detail, incr, true);
            } else if (-incr > 0 && -incr < 99.99) {
                doAnalysis(stock_detail, incr, false);
            }
        }
        return "done";

    }

    public void initRules() {
        int cnt = updateDAO.execute("rules.initTodayRules", null);
        log.info(cnt + " rules have been initialed");
    }

    private void doAnalysis(List<String> stock_detail, double incr, boolean up) {
        log.info(stock_detail.get(0) + " ============================will be analyzed================== ");
        Alldata ad = Formater.editCriteriaForules(stock_detail, popertiesHelper.getStocksProperties().getInt("plan.historydays"));

        List<Alldata> al = queryDAO.executeForObjectList("rules.searchHistory", ad);

        // history total increase range
        double historytotalincrrange = 0.00;
        double yesterdayincrrange = 0.00;
        // the up and down forward
        String[] updown = new String[al.size()];
        Alldata alb;
        log.info(stock_detail.get(0) + " :analyzing " + al.size() + " history records");
        for (int m = 0; m < al.size(); m++) {
            alb = al.get(m);
            double temp =
                    Caculator.keepRound((alb.getPresent_price().doubleValue()
                    - alb.getYt_close_price().doubleValue()) * 100 / alb.getYt_close_price().doubleValue(), 2);
            if (m == 0) {
                // get the  increase range of yesterday
                yesterdayincrrange = temp;
            }
            updown[al.size() - 1 - m] = "9";
            if (temp < 0) {
                updown[al.size() - 1 - m] = "0";
            }
            if (temp > 0) {
                updown[al.size() - 1 - m] = "1";
            }
            historytotalincrrange = historytotalincrrange + temp;
        }

        yesterdayincrrange = Caculator.keepRound(yesterdayincrrange, 2);
        historytotalincrrange = Caculator.keepRound(historytotalincrrange, 2);

        StringBuffer sb = new StringBuffer();
        for (int l = 0; l < updown.length; l++) {
            sb.append(updown[l]);
        }
        String updownstr = sb.toString();

        double rangefrom = 0;
        double rangeto = 0;
        double hsrangeaccuracy = 0;
        double ydrangeaccuracy = 0;
        // region judge
        if (incr < 5) {

            rangefrom = popertiesHelper.getStocksProperties().getDouble("plan.incr");
            rangeto = 5;
            hsrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region1.hsrangeaccuracy");
            ydrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region1.ydrangeaccuracy");
        } else if (incr < 8) {

            rangefrom = 5;
            rangeto = 8;
            hsrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region2.hsrangeaccuracy");
            ydrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region2.ydrangeaccuracy");
        } else {

            rangefrom = 8;
            rangeto = 99;
            hsrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region3.hsrangeaccuracy");
            ydrangeaccuracy = popertiesHelper.getStocksProperties().getDouble("region3.ydrangeaccuracy");
        }
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("inc", incr);
        condition.put("historydasy", popertiesHelper.getStocksProperties().getInt("plan.historydays"));
        condition.put("updown", updownstr);
        condition.put("yesterdayincrrange", yesterdayincrrange);
        condition.put("historytotalincrrange", historytotalincrrange);

        List<Rules> rls = queryDAO.executeForObjectList("rules.serchExsitingrule", condition);
        if (rls.size() == 0) {
            condition = new HashMap<String, Object>();

            condition.put("rangefrom", rangefrom);
            condition.put("historydasy", popertiesHelper.getStocksProperties().getString("plan.historydays"));
            condition.put("updown", updownstr);
            condition.put("rangeto", rangeto);
            condition.put("hsrangeaccuracy", hsrangeaccuracy);
            condition.put("ydrangeaccuracy", ydrangeaccuracy);
            condition.put("yesterdayincrrange", yesterdayincrrange);
            condition.put("historytotalincrrange", historytotalincrrange);
            if (up) {

                condition.put("td_apply_counts", "1");
                log.info(stock_detail.get(0) + " :inserted " + rangefrom + "-" + rangeto);
            } else {

                condition.put("td_apply_down_counts", "1");
                log.info(stock_detail.get(0) + " :inserted " + -rangefrom + "-" + -rangeto);
            }
            updateDAO.execute("rules.insert2rules", condition);

        } else {

            condition.put("hs_apply_ammout", rls.get(0).getHs_apply_ammout() + 1);
            condition.put("rule_id", rls.get(0).getRule_id());
            if (up) {

                condition.put("td_apply_counts", rls.get(0).getTd_apply_counts() + 1);

            } else {

                condition.put("td_apply_down_counts", rls.get(0).getTd_apply_down_counts() + 1);
            }
            updateDAO.execute("rules.updateRules", condition);
            log.info(stock_detail.get(0) + " :updated No. " + rls.get(0).getRule_id() + " rule");
        }

        log.info(stock_detail.get(0) + " ============================ analysis over================== ");
    }

    public double getShangHaiHistoryIncr() {

        List<String> stock_detail = new ArrayList<String>();
        stock_detail.add("si000001");

        Alldata ad = Formater.editCriteriaForules(stock_detail, popertiesHelper.getStocksProperties().getInt("plan.historydays"));

        List<Alldata> al = queryDAO.executeForObjectList("candidate.searchHistory", ad);

        Alldata alb;
        double historytotalincrrange = 0.00;
        for (int m = 0; m < al.size(); m++) {
            alb = al.get(m);
            double temp =
                    Caculator.keepRound((alb.getPresent_price().doubleValue()
                    - alb.getYt_close_price().doubleValue()) * 100 / alb.getYt_close_price().doubleValue(), 2);

            historytotalincrrange = historytotalincrrange + temp;
        }


        return Caculator.keepRound(historytotalincrrange, 2);
    }
}