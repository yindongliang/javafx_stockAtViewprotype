package answer.logic;

import java.io.IOException;
import java.util.List;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.bean.dto.Alldata;
import answer.bean.dto.Codenamematch;
import answer.util.Caculator;
import answer.util.Canlendar;
import answer.util.Convertor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Service(value = "StockGaoKai")
public class StockGaoKai {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(StockGaoKai.class);

    public String getAlldata() {
        // get all stock's code
        List<String> ls_shsz = popertiesHelper.getStockCds();
        // add the shanghai indices
        ls_shsz.add(popertiesHelper.getStocksProperties().getString("shindice_code"));
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {

            stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

            if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                continue;
            }

            Alldata ad = Formater.editCriteria(stock_detail);
            int cn = queryDAO.executeForObject("download.checkBeforeInsert", ad, Integer.class);

            if (cn == 0) {

                if (stock_detail.size() > 32) {
                    for (int b = 32; b < stock_detail.size(); b++) {
                        stock_detail.remove(b);
                    }
                }
                updateDAO.execute("download.insert2Alldata", stock_detail);
                log.info("data has been inserted :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());
            } else {
                log.info("data is exsiting :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());
            }

        }

        return "0";
    }

    public String insert2dbFromFile(String datefrom, String dateto, boolean del) {
        List<String> ls_shsz = popertiesHelper.getStockCds();
        FileInputStream fis = null;
        String datafile_path_sh = popertiesHelper.getStocksProperties().getString("datafile_path_sh");
        String datafile_path_sz = popertiesHelper.getStocksProperties().getString("datafile_path_sz");

        try {
            for (String stockcd : ls_shsz) {
                if (stockcd.startsWith("60")){
                    fis = new FileInputStream(datafile_path_sh + stockcd + ".day");
                }else if (stockcd.startsWith("30")||stockcd.startsWith("00")){
                    fis = new FileInputStream(datafile_path_sz + stockcd + ".day");
                }
                
                byte[] b = new byte[40];
                byte[] byestoday = new byte[40];

                while ((fis.read(b)) != -1) {

                    int date = Convertor.bytes2int(Convertor.cut(b, 0, 4));
                    String stockdate = Canlendar.formaterString("" + date);
                    if (stockdate.compareTo(datefrom) >= 0 && stockdate.compareTo(dateto) <= 0) {
                        List<String> stock_detail = new ArrayList<String>();
                        stock_detail.add(stockcd);// 0:股票代码
                        for (int i = 1; i < 10; i++) {
                            double data = (double) Convertor.bytes2int(Convertor.cut(b, i * 4, 4));
                            
                            if (i == 1) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                stock_detail.add(dt + "");// 1:今日开盘价格
                                double yestdayprice = (double) Convertor.bytes2int(Convertor.cut(byestoday, 16, 4));
                                double dty = Caculator.keepRound(yestdayprice / 1000, 2);
                                stock_detail.add(dty + "");// 2:昨日收盘价格
                                stock_detail.add("3");// 3:当前价格

                            }
                            if (i == 2) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                stock_detail.add(dt + "");// 4:今日最高价格
                            }
                            if (i == 3) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                stock_detail.add(dt + "");// 5:今日最低价格
                            }
                            if (i == 4) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                stock_detail.set(3, dt + "");// 今日收盘价格设置成当前价格
                                stock_detail.add("0");// 6:竞买价买一
                                stock_detail.add("0");//7: 竞卖价卖一
                            }
                            if (i == 5) {
                                stock_detail.add("0"); // 8:成交量 
                                double dt = Caculator.keepRound(data / 10, 2);
                                stock_detail.add(dt * 10000 + "");//9: 成交额


                            }
                            if (i == 6) {
                                stock_detail.set(8, data * 100 + "");//8:成交量 更新

                                stock_detail.add("0");// 10:申请买一
                                stock_detail.add("0");// 11:报价买一
                                stock_detail.add("0");// 12:申请买2
                                stock_detail.add("0");// 13:报价买2
                                stock_detail.add("0");// 14:申请买3
                                stock_detail.add("0");// 15:报价买3
                                stock_detail.add("0");// 16:申请买4
                                stock_detail.add("0");// 17:报价买4
                                stock_detail.add("0");// 18:申请买5
                                stock_detail.add("0");// 19:报价买5
                                stock_detail.add("0");// 20:申请卖1
                                stock_detail.add("0");// 21:报价卖1
                                stock_detail.add("0");// 22:申请卖2
                                stock_detail.add("0");// 23:报价卖2
                                stock_detail.add("0");// 24:申请卖3
                                stock_detail.add("0");// 25:报价卖3
                                stock_detail.add("0");// 26:申请卖4
                                stock_detail.add("0");// 27:报价卖4
                                stock_detail.add("0");// 28:申请卖5
                                stock_detail.add("0");// 29:报价卖5
                                stock_detail.add(stockdate);//30:日期
                                stock_detail.add("15:00:00");//31: 时间

                            }
                        }
                        
                        Alldata ad = Formater.editCriteria(stock_detail);
                        int cn = queryDAO.executeForObject("download.checkBeforeInsert", ad, Integer.class);

                        if (cn == 0) {

                            if (stock_detail.size() > 32) {
                                for (int t = 32; t < stock_detail.size(); t++) {
                                    stock_detail.remove(t);
                                }
                            }
                            updateDAO.execute("download.insert2Alldata", stock_detail);
                            log.info("data has been inserted :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());
                        } else {
                            if (del) {
                                updateDAO.execute("download.deleteExsitingdata", ad);
                                log.info("data has been deleted :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());
                                updateDAO.execute("download.insert2Alldata", stock_detail);
                                log.info("data has been inserted :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());

                            } else {
                                log.info("data is exsiting :" + ad.getStock_cd() + "-" + ad.getRecord_date() + "-" + ad.getRecord_time());
                            }

                        }
                    } else if (stockdate.compareTo(dateto) > 0) {
                        break;
                    }
                    byestoday = b.clone();


                }

                fis.close();

            }
            return "done";
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(StockGaoKai.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(StockGaoKai.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
