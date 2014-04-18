/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package answer.logic;

import answer.bean.dto.Codenamematch;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.PopertiesHelper;
import answer.util.Canlendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author doyin
 */
@Service(value = "TableMaintain")
public class TableMaintain {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(answer.logic.TableMaintain.class);

    public String clearAlldata(Integer beforndays) {
        // get shanghai's detail
        List<String> stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
                popertiesHelper.getStocksProperties());

        String date = Canlendar.getBeforday(stock_detail.get(30), beforndays);
        updateDAO.execute("tablemaintain.clearAlldataWithspecificdate", date);
        log.info("the data before " + date + " in Alldata has been deleted");
        return "done";
    }

    public String clearRuledata() {
        updateDAO.execute("tablemaintain.clearruledata", null);
        log.info("the data in rules has been deleted");
        insertSPrule();
        return "done";
    }
    private void insertSPrule(){
        updateDAO.execute("tablemaintain.insert1rule", null);
        log.info("1 rule has been inserted");
    }
    public String clearAnalyzeddata(Integer beforndays) {
        // get shanghai's detail
        List<String> stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
                popertiesHelper.getStocksProperties());

        String date = Canlendar.getBeforday(stock_detail.get(30), beforndays);
        updateDAO.execute("tablemaintain.clearAnaylyzeDataWithspecificdate", date);
        log.info("the data before " + date + " in Anaylyzedreusltdata has been deleted");
        return "done";
    }

    public String updateStockname() {
        // get all stock's code
        List<String> ls_shsz = popertiesHelper.getStockCds();
        // add the shanghai indices
        ls_shsz.add(popertiesHelper.getStocksProperties().getString("shindice_code"));
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {

            stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

            if (stock_detail == null) {
                Map<String, String> cond = new HashMap<String, String>();
                String stockcd = ls_shsz.get(j);
                cond.put("code", stockcd);
                cond.put("more_info", "quit");
                updateDAO.execute("tablemaintain.updateCodeNameTable", cond);
                log.info("the stock is quit from market :" + stockcd);
                continue;
            }
            Map<String, String> cond = new HashMap<String, String>();
            String stockcd = ls_shsz.get(j);
            String stockname = stock_detail.get(32);
            cond.put("code", stockcd);
            Codenamematch codenamematch = queryDAO.executeForObject("tablemaintain.checkCodeNameTableBeforeInsert", cond, Codenamematch.class);

            if (codenamematch != null && !codenamematch.getName().equals(stockname)) {
                cond = new HashMap<String, String>();
                cond.put("name", stockname);
                cond.put("code", stockcd);
                updateDAO.execute("tablemaintain.updateCodeNameTable", cond);
                log.info("CodeNameTable has been updated :" + stockcd + "-" + stockname);
            } else if (codenamematch == null) {

                List<String> insertdata = new ArrayList<String>();
                String dis = "";
                if (stockcd.startsWith("60")) {
                    dis = "s";
                } else if (stockcd.startsWith("30") || stockcd.startsWith("00")) {
                    dis = "z";
                }
                insertdata.add(dis);
                insertdata.add(stockcd);
                insertdata.add(stockname);
                insertdata.add("");
                updateDAO.execute("tablemaintain.insert2CodeNameTable", insertdata);
                log.info("CodeNameTable has been inserted :" + stockcd + "-" + stockname);
            }
        }


        return "done";
    }
}
