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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.util.Caculator;
import answer.bean.dto.Alldata;
import answer.bean.dto.Analyzedresultdata;
import answer.bean.dto.Rules;
import answer.util.Canlendar;
import java.util.Arrays;
import java.util.Collections;
import stockatview.handle.ChooseStock;

/**
 * How to send a request via proxy using {@link HttpClient}.
 *
 * @since 4.0
 */
@Service(value = "StockCandidate")
public class StockCandidate {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(StockCandidate.class);

    public String getCandidate(String selectplan, int limit_rec, String leijiamount, List<String> ls_shsz) {


        List<String> stock_detail = null;

        double shangHaiHistoryIncres;

        if (leijiamount.equals("")) {

//			stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),popertiesHelper.getStocksProperties());
//	        
//	        double curprice_si = Double.parseDouble(stock_detail.get(3));
//			double ydprice_si = Double.parseDouble(stock_detail.get(2));
//			
//			double incr_si = Caculator.keepRound((curprice_si-ydprice_si)*100/ydprice_si,2);	
//					
//			shangHaiHistoryIncres = shangHaiHistoryIncres(stock_detail,incr_si);
//			log.info("shanghai's indices is  "+shangHaiHistoryIncres+"%");
            shangHaiHistoryIncres = -100;
        } else {
            shangHaiHistoryIncres = Double.parseDouble(leijiamount);
        }
        List<Rules> al = null;

        Map<String, Object> condition = new HashMap<String, Object>();
        // select the rules
        if (limit_rec == 0) {
            limit_rec = popertiesHelper.getStocksProperties().getInt("plan.followedrules");
        }
        if (selectplan.equals("msttoday")) {
            condition.put("limit_rec", limit_rec);
            condition.put("shanghai_history_incr", shangHaiHistoryIncres);
            al = queryDAO.executeForObjectList("candidate.strongSelectrules", condition);

        }
        if (selectplan.equals("todaynot0")) {
            condition.put("limit_rec", limit_rec);

            al = queryDAO.executeForObjectList("candidate.followrules", condition);

        }
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {

            stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

            if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                continue;
            }

            double curprice = Double.parseDouble(stock_detail.get(3));
            double ydprice = Double.parseDouble(stock_detail.get(2));

            double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);
            log.info(stock_detail.get(0) + ": is processing and it's current increase is " + incr + "%");

            pickupcandidates(al, stock_detail, incr);

        }
        return "done";
    }

//	private double shangHaiHistoryIncres(List<String> stock_detail,double incr){
//		
//		Alldata ad = Formater.editCriteriaForules(stock_detail,popertiesHelper.getStocksProperties().getInt("plan.historydays")-1);
//		
//		List<Alldata> al=queryDAO.executeForObjectList("candidate.searchHistory",ad);
//		
//		Alldata alb;
//		double historytotalincrrange=0.00;
//		for(int m=0;m<al.size();m++){
//			alb=al.get(m);
//			double temp =
//					Caculator.keepRound((alb.getPresent_price().doubleValue()
//							-alb.getYt_close_price().doubleValue())*100/alb.getYt_close_price().doubleValue(),2);
//			
//			historytotalincrrange=historytotalincrrange+temp;
//		}
//		historytotalincrrange=historytotalincrrange+incr;
//		
//		return Caculator.keepRound(historytotalincrrange,2);
//	}
    private void pickupcandidates(List<Rules> rbl, List<String> stock_detail, double incr) {

        List<Alldata> al = null;

        Rules rbean;
        for (int i = 0; i < rbl.size(); i++) {

            rbean = rbl.get(i);

            if (incr >= (rbean.getRes_yd_increase_range().doubleValue() - rbean.getCon_ydincrange_accuracy().doubleValue())
                    && incr < (rbean.getRes_yd_increase_range().doubleValue() + rbean.getCon_ydincrange_accuracy().doubleValue())) {

                if (al == null) {

                    // search the history
                    Alldata ad = Formater.editCriteriaForules(stock_detail, popertiesHelper.getStocksProperties().getInt("plan.historydays"));

                    al = queryDAO.executeForObjectList("candidate.searchHistory", ad);
                }

                // history total increase range
                double historytotalincrrange = 0.00;
                int validcnt = al.size() - 1;
                log.info(stock_detail.get(0) + ": is checking  it's  " + validcnt + " histories to see if can apply this rule :" + rbean.getRule_id());

                double[] deatlotsincr = new double[5];

                double[] closepriceincr = new double[5];
                double[] closeprices = new double[5];
                double[] deallots = new double[5];

                Map res = setKeyPrams(al, validcnt, closepriceincr,
                        closeprices,
                        deatlotsincr,
                        deallots,
                        stock_detail,
                        incr);
                double td_deallots = (double) res.get("td_deallots");
                double historydeallots_amount = (double) res.get("historydeallots_amount");


                String updownstr = (String) res.get("updownstr");

                if (historytotalincrrange + incr >= rbean.getRes_tot_increase_range().doubleValue() - rbean.getCon_hsrangeammount_accuracy().doubleValue()
                        && historytotalincrrange + incr < rbean.getRes_tot_increase_range().doubleValue() + rbean.getCon_hsrangeammount_accuracy().doubleValue()) {

                    if (rbean.getRes_up_down().equals(updownstr)) {
                        String buy_advisor = "";

                        if (historytotalincrrange + incr <= -5) {// 跌幅超过-5%

                            if (isbottomchance(closepriceincr, closeprices, deallots,false)) {
                                buy_advisor = "5"; //极度推荐
                                log.info(stock_detail.get(0) + "下跌通道,已经开始反弹，此股极有可能已经触底.结论：极度建议买入");
                            }

                        }
                        if (historytotalincrrange + incr >= 0
                                && historytotalincrrange + incr < 5) {// 涨幅不超过5%
                            if (iszhuigaochance(closepriceincr, deallots,false)) {
                                buy_advisor = "4"; //极度推荐
                                log.info(stock_detail.get(0) + "上涨通道,量价齐升，说明有庄家介入.结论：极度建议买入");

                            }
                        }

                        if ("".equals(buy_advisor)) {
                            log.info(stock_detail.get(0) + " is not matching rule id " + rbean.getRule_id() + " skipped");
                            return;
                        }

                        insertCandidate(stock_detail,
                                buy_advisor,
                                rbean.getRule_id() + "",
                                historydeallots_amount,
                                td_deallots);
                        break;
                    }

                }
            }
        }
    }

    public String getCandidateNorules(List<String> ls_shsz) {

        // get all stock's code
//        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;

        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {

            stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

            if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                continue;
            }

            double curprice = Double.parseDouble(stock_detail.get(3));
            double ydprice = Double.parseDouble(stock_detail.get(2));

            double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);
            log.info(stock_detail.get(0) + ": is processing and it's current increase is " + incr + "%");

            pickupcandidatesNorules(stock_detail, incr);

        }
        return "done";
    }

    private void pickupcandidatesNorules(List<String> stock_detail, double incr) {

        List<Alldata> al = null;
        // search the history
        Alldata ad = Formater.editCriteriaForules(stock_detail, popertiesHelper.getStocksProperties().getInt("plan.historydays"));

        al = queryDAO.executeForObjectList("candidate.searchHistory", ad);

        // history total increase range
        double historytotalincrrange = 0.00;
        int validcnt = al.size() - 1;
        if (validcnt < 4) {
            log.info(stock_detail.get(0) + ": has been skipped,because  there's  at least 4 histories expected but only" + validcnt + " histories now");
            return;
        }
        log.info(stock_detail.get(0) + ": is checking  it's  " + validcnt + " histories to see if match 1");
        // the up and down forward


        double[] deatlotsincr = new double[5];

        double[] closepriceincr = new double[5];
        double[] closeprices = new double[5];
        double[] deallots = new double[5];

        Map res = setKeyPrams(al, validcnt, closepriceincr,
                closeprices,
                deatlotsincr,
                deallots,
                stock_detail,
                incr);
        double td_deallots = (double) res.get("td_deallots");
        double historydeallots_amount = (double) res.get("historydeallots_amount");


        String buy_advisor = "";

        if (incr > 0) {

            if (historytotalincrrange + incr <= -5) {// 跌幅超过-5%

                if (isbottomchance(closepriceincr, closeprices, deallots,true)) {
                    buy_advisor = "5"; //极度推荐
                    log.info(stock_detail.get(0) + "下跌通道,已经开始反弹，此股极有可能已经触底.结论：极度建议买入");
                }

            }
            if (historytotalincrrange + incr >= 0
                    && historytotalincrrange + incr < 5) {// 涨幅不超过5%
                if (iszhuigaochance(closepriceincr, deallots,true)) {
                    buy_advisor = "4"; //极度推荐
                    log.info(stock_detail.get(0) + "上涨通道,量价齐升，说明有庄家介入.结论：极度建议买入");

                }
            }

        }
        if ("".equals(buy_advisor)) {
            log.info(stock_detail.get(0) + " is not matching rule id 1,skipped");
            return;
        }
        insertCandidate(stock_detail,
                buy_advisor,
                "1",
                historydeallots_amount,
                td_deallots);

    }

    private void insertCandidate(List<String> stock_detail,
            String buy_advisor,
            String rule_id,
            double historydeallots_amount,
            double td_deallots) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("stock_cd", stock_detail.get(0));
        condition.put("record_date", stock_detail.get(30));
        condition.put("record_time", stock_detail.get(31));
        condition.put("rule_id", rule_id);

        int cnt = queryDAO.executeForObject("candidate.checkbeforeinsert", condition, Integer.class);

        if (cnt == 0) {

            condition = new HashMap<String, Object>();
            condition.put("buy_advisor", buy_advisor);
            condition.put("stock_cd", stock_detail.get(0));
            condition.put("rule_id", rule_id);
            condition.put("record_date", stock_detail.get(30));
            condition.put("record_time", stock_detail.get(31));
            condition.put("lotsbili", Caculator.keepRound(historydeallots_amount / td_deallots, 2));
            updateDAO.execute("candidate.insert2Result", condition);
            log.info(stock_detail.get(0) + " is a candidate matching rule id " + rule_id);
        } else {
            log.info(stock_detail.get(0) + " of " + stock_detail.get(30) + " already exist");
        }
    }

    private Map setKeyPrams(List<Alldata> al, int validcnt, double[] closepriceincr,
            double[] closeprices,
            double[] deatlotsincr,
            double[] deallots,
            List<String> stock_detail,
            double incr) {
        Alldata alb;
        Alldata albyest;
        Map rem = new HashMap();
        double historydeallots_amount = 0;
        double historytotalincrrange = 0;

        double td_deallots = Double.parseDouble(stock_detail.get(8));
        // 涨幅
        closepriceincr[4] = incr;
        // 成交量
        deallots[4] = td_deallots;
        // 收盘价格
        closeprices[4] = Double.parseDouble(stock_detail.get(3));
        String[] updown = new String[5];
        for (int m = 0; m < validcnt; m++) {

            alb = al.get(m);
            albyest = al.get(m + 1);
            if (m == 0) {

                historydeallots_amount = historydeallots_amount + alb.getDeal_lots() + td_deallots;
                deatlotsincr[4] = Caculator.keepRound((td_deallots - alb.getDeal_lots()) * 100 / alb.getDeal_lots(), 2);
            } else {
                // 成交量昨日比
                deatlotsincr[4 - m] = Caculator.keepRound((alb.getDeal_lots()
                        - albyest.getDeal_lots()) * 100 / albyest.getDeal_lots(), 2);
                // 总成交量
                historydeallots_amount = historydeallots_amount + alb.getDeal_lots();
            }
            // 每天收盘价格
            closeprices[validcnt - 1 - m] = alb.getPresent_price().doubleValue();
            // 每天成交量记录
            deallots[validcnt - 1 - m] = alb.getDeal_lots();


            double temp =
                    Caculator.keepRound((alb.getPresent_price().doubleValue()
                    - alb.getYt_close_price().doubleValue()) * 100 / alb.getYt_close_price().doubleValue(), 2);
            // 涨幅
            closepriceincr[validcnt - 1 - m] = temp;

            updown[validcnt - 1 - m] = "9";

            if (temp < 0) {

                updown[validcnt - 1 - m] = "0";
            } else if (temp > 0) {
                updown[validcnt - 1 - m] = "1";
            } else {
            }
            historytotalincrrange = historytotalincrrange + temp;
        }
        if (incr > 0) {
            updown[4] = "1";
        }
        if (incr < 0) {
            updown[4] = "0";
        }
        StringBuffer sb = new StringBuffer();
        for (int l = 0; l < updown.length; l++) {
            sb.append(updown[l]);
        }
        String updownstr = sb.toString();
        rem.put("historydeallots_amount", historydeallots_amount);
        rem.put("historytotalincrrange", historytotalincrrange);
        rem.put("td_deallots", td_deallots);
        rem.put("updownstr", updownstr);
        return rem;


    }

    private boolean isbottomchance(double[] closepriceincr,
            double[] closeprices,
            double[] deallots,
            boolean IsNo1rule) {
        double maxdeallot = 0;
        double sumlots = 0;
        int index = 0;


        double[] deallotsexcepttoday = new double[4];
        for (int i = 0; i < deallots.length - 1; i++) {

            if (deallots[i] > maxdeallot) {
                maxdeallot = deallots[i];
                index = i;
            }
            deallotsexcepttoday[i] = deallots[i];
            sumlots = sumlots + deallots[i];

        }
        for (int i = 0; i < deallotsexcepttoday.length; i++) {
            for (int j = i + 1; j < deallotsexcepttoday.length; j++) {
                if (deallotsexcepttoday[i] > deallotsexcepttoday[j]) {
                    double temp = deallotsexcepttoday[i];
                    deallotsexcepttoday[i] = deallotsexcepttoday[j];
                    deallotsexcepttoday[j] = temp;
                }
            }
        }
        if (IsNo1rule) {
            if (sumlots / (maxdeallot + deallotsexcepttoday[2]) < 1.5 && closepriceincr[index] < 0//放量跌过
                    && (sumlots + deallots[deallots.length - 1]) / deallots[deallots.length - 1] < 5 // 交易量大于5日平均交易量
                    && closepriceincr[closepriceincr.length - 1] > 0.25 //               
                    && closepriceincr[closepriceincr.length - 1] < 1) {
                return true;
            }
        } else {
            if (sumlots / (maxdeallot + deallotsexcepttoday[2]) < 1.5 && closepriceincr[index] < 0//放量跌过
                    && (sumlots + deallots[deallots.length - 1]) / deallots[deallots.length - 1] < 5 // 交易量大于5日平均交易量
                    ) {
                return true;
            }
        }


        return false;
    }

    private boolean iszhuigaochance(double[] closepriceincr,
            double[] deallots,
            boolean IsNo1rule) {

        double sumlots = 0;
        for (int i = 0; i < deallots.length; i++) {
            sumlots = sumlots + deallots[i];
        }
        if (IsNo1rule) {
            if (sumlots / deallots[deallots.length - 1] < 2.1
                    && closepriceincr[closepriceincr.length - 1] > 3
//                    && closepriceincr[closepriceincr.length - 1] < 2.5
                    ) {
                return true;
            }
        } else {
            if (sumlots / deallots[deallots.length - 1] < 5) {
                return true;
            }
        }


        return false;
    }

    public String deletetodayExistingCandidate() {
        List<String> stock_detail = null;
        stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
                popertiesHelper.getStocksProperties());

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("record_date", stock_detail.get(30));
//        condition.put("rule_id", "1");
        updateDAO.execute("candidate.deleteSPruleIddata", condition);
        log.info(" deleted today's existing 1 data");
        return "done";
    }
}