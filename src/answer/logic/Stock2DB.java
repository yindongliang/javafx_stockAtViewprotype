package answer.logic;

import answer.bean.dto.*;
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

import answer.util.Caculator;
import answer.util.Canlendar;
import answer.util.Convertor;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.dao.DataIntegrityViolationException;

@Service(value = "Stock2DB")
public class Stock2DB {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(Stock2DB.class);

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

                if (stockcd.startsWith("60")) {
                    fis = new FileInputStream(datafile_path_sh + stockcd + ".day");
                } else if (stockcd.startsWith("30") || stockcd.startsWith("00")) {
                    fis = new FileInputStream(datafile_path_sz + stockcd + ".day");
                } else if (stockcd.equals("si000001")) {
                    fis = new FileInputStream(datafile_path_sh + "000001.day");
                }



                byte[] b = new byte[40];
                byte[] byestoday = new byte[40];

                while ((fis.read(b)) != -1) {
                    boolean ignorfag = false;
                    int date = Convertor.bytes2int(Convertor.cut(b, 0, 4));
                    String stockdate = Canlendar.formaterString("" + date);
                    if (stockdate.compareTo(datefrom) >= 0 && stockdate.compareTo(dateto) <= 0) {
                        List<String> stock_detail = new ArrayList<String>();

                        stock_detail.add(stockcd);// 0:股票代码
                        for (int i = 1; i < 10; i++) {
                            double data = (double) Convertor.bytes2int(Convertor.cut(b, i * 4, 4));

                            if (i == 1) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                if (dt == 0 || dt > 9000) {
                                    ignorfag = true;
                                    break;
                                }
                                stock_detail.add(dt + "");// 1:今日开盘价格
                                double yestdayprice = (double) Convertor.bytes2int(Convertor.cut(byestoday, 16, 4));
                                double dty = Caculator.keepRound(yestdayprice / 1000, 2);
                                stock_detail.add(dty + "");// 2:昨日收盘价格
                                stock_detail.add("3");// 3:当前价格

                            }
                            if (i == 2) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                if (dt == 0 || dt > 9000) {
                                    ignorfag = true;
                                    break;
                                }
                                stock_detail.add(dt + "");// 4:今日最高价格
                            }
                            if (i == 3) {

                                double dt = Caculator.keepRound(data / 1000, 2);
                                if (dt == 0 || dt > 9000) {
                                    ignorfag = true;
                                    break;
                                }
                                stock_detail.add(dt + "");// 5:今日最低价格
                            }
                            if (i == 4) {
                                double dt = Caculator.keepRound(data / 1000, 2);
                                if (dt == 0 || dt > 9000) {
                                    ignorfag = true;
                                    break;
                                }
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
                        if (ignorfag) {
                            continue;
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
            java.util.logging.Logger.getLogger(Stock2DB.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Stock2DB.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public String insertf10FromFile() {
        List<String> ls_shsz = popertiesHelper.getStockCds();
        FileInputStream fis = null;
        String f10_file_path_sh = popertiesHelper.getStocksProperties().getString("f10_file_path_sh");
        String f10_file_path_sz = popertiesHelper.getStocksProperties().getString("f10_file_path_sz");
        updateDAO.execute("ften.deleteFten", null);
        log.info("ften has been deleteed");
        for (String stockcd : ls_shsz) {

            List<String> stock_detail = httpHelper.sendRequest(stockcd, popertiesHelper.getStocksProperties());
            if (stock_detail == null) {
                continue;
            }
            String stock_name = stock_detail.get(stock_detail.size() - 1);
            if (stock_name.contains("ST")) {
                continue;
            }

            Map<String, Object> mpfile001 = null;
            Map<String, Object> mpfile013 = null;
            Map<String, Object> mpfile010 = null;
            if (stockcd.startsWith("60")) {
                mpfile001 = readFileByLines(f10_file_path_sh + stockcd + ".001");
                if (mpfile001 != null) {
                    mpfile013 = readFileByLines(f10_file_path_sh + stockcd + ".013");
                    mpfile010 = readFileByLines(f10_file_path_sh + stockcd + ".010");
                }


            } else if (stockcd.startsWith("30") || stockcd.startsWith("00")) {
                mpfile001 = readFileByLines(f10_file_path_sz + stockcd + ".001");
                if (mpfile001 != null) {
                    mpfile013 = readFileByLines(f10_file_path_sz + stockcd + ".013");
                    mpfile010 = readFileByLines(f10_file_path_sz + stockcd + ".010");
                }
            }
            if (mpfile001 == null || mpfile013 == null || mpfile001.keySet().size() < 4) {
                continue;
            }
            double meigusy = (Double) mpfile001.get("meigusy");
            double meigujzc = (Double) mpfile001.get("meigujzc");
            double meigugjj = (Double) mpfile001.get("meigugjj");
            double meiguwfplr = (Double) mpfile001.get("meiguwfplr");

            double liutonggu = (Double) mpfile001.get("liutonggu");
            double tongbiincr = (Double) mpfile001.get("tongbiincr");
            double jingzcbenifitrate = (Double) mpfile001.get("jingzcbenifitrate");
            String update_date = (String) mpfile001.get("update_date");
            String suosuhangye = (String) mpfile013.get("suosuhangye");
            double liutonggudongchigubili = (Double) mpfile010.get("liutonggudongchigubili");

            log.info(stockcd + " " + stock_name + "：流通股 " + liutonggu + "，净利润同比增长 " + tongbiincr
                    + "，净资产收益率 " + jingzcbenifitrate
                    + "，所属行业 " + suosuhangye
                    + "，更新时间 " + update_date);


            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("stock_cd", stockcd);
            mp.put("stock_name", stock_name);

            mp.put("meigusy", meigusy);
            mp.put("meigujzc", meigujzc);
            mp.put("meigugjj", meigugjj);
            mp.put("meiguwfplr", meiguwfplr);

            mp.put("liutonggu", liutonggu);
            mp.put("tongbiincr", tongbiincr);
            mp.put("jingzcbenifitrate", jingzcbenifitrate);
            mp.put("update_date", update_date);
            mp.put("suosuhangye", suosuhangye);
            mp.put("liutonggudongchigubili", liutonggudongchigubili);
            try {
                updateDAO.execute("ften.insertFten", mp);
            } catch (Exception e) {
                if (e instanceof DataIntegrityViolationException) {
                    log.info(stockcd + " is existing");
                } else {
                    throw e;
                }
            }

        }
        return "done";
    }

    public void deleteBankStocks() {
        int cnt = updateDAO.execute("bankuaiStock.deleteExsitingdata", null);
        if (cnt > 0) {
            log.info("bankuai stocks  has been deleted:" + cnt + "条数据");
        }
    }

    public String insertBankFromSina(String bankuai_cd, String bankuai_name, List<String> stock_cd_with_market, boolean delbk) {

        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("bankuai_cd", bankuai_cd);

        Bankuai bk = queryDAO.executeForObject("bankuai.selectAllCond", mp, Bankuai.class);

        if (bk == null) {

            mp.put("bankuai_name", bankuai_name);
            updateDAO.execute("bankuai.insertAll", mp);
            log.info("data has been inserted :" + bankuai_cd + "-" + bankuai_name);
        } else {
            if (delbk) {
                updateDAO.execute("bankuai.deleteExsitingdata", mp);
                mp.put("bankuai_name", bankuai_name);
                log.info("data has been deleted :" + bankuai_cd + "-" + bankuai_name);
                updateDAO.execute("bankuai.insertAll", mp);
                log.info("data has been inserted :" + bankuai_cd + "-" + bankuai_name);

            } else {
                log.info("data is exsiting :" + bankuai_cd + "-" + bankuai_name);
            }

        }


        for (String stcd : stock_cd_with_market) {

            String stock_cd = stcd.substring(2);
            List<String> stock_detail = httpHelper.sendRequest(stock_cd, popertiesHelper.getStocksProperties());
            if (stock_detail == null) {
                log.info("no longer exsit:" + stock_cd);
                continue;
            }
            String market_cd = stcd.substring(0, 2);
            Map<String, Object> mp2 = new HashMap<String, Object>();
            mp2.put("stock_cd", stock_cd);
            mp2.put("market_cd", market_cd);


            BankuaiStock bkstock = queryDAO.executeForObject("bankuaiStock.selectAllCond", mp2, BankuaiStock.class);
            if (bkstock == null) {
                mp2.put("bankuai_cd", bankuai_cd);
                updateDAO.execute("bankuaiStock.insertAll", mp2);
                log.info("data has been inserted :" + stcd);
            } else if (bkstock.getBankuai_cd().contains(bankuai_cd)) {
                log.info(stcd + " is exsisting");
            } else {
                mp2.put("bankuai_cd", bkstock.getBankuai_cd() + "," + bankuai_cd);
                updateDAO.execute("bankuaiStock.updateData", mp2);
                log.info("data has been updated :" + stcd);

            }


            try {
            } catch (DataIntegrityViolationException e) {
                log.info(stcd + " is exsisting");
            }


        }


        return "done";
    }

    public void lankactivty(String dateto) {

        updateDAO.execute("huoyuelank.deleteHuoyuelank", null);
        log.info("huoyuelank has been deleted");
        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {
            Map<String, Object> condition = new HashMap<String, Object>();
            String stockcd = ls_shsz.get(j);
            condition.put("stock_cd", stockcd);

            Ften ften = queryDAO.executeForObject("ften.selectFten", condition, Ften.class);
            if (ften == null) {
                log.info(stockcd + " dosen't exist");
                continue;
            }
            condition.put("record_date_to", dateto);
            condition.put("orderby", "desc");
            condition.put("limit", 30);
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);

            if (adls == null || adls.size() == 0) {
                continue;
            }
            int i = 1;
            double totdeal = 0;
            double fiveturnover = 0;
            double tenturnover = 0;
            double thirtyturnover = 0;

            for (Alldata ad : adls) {

                totdeal = totdeal + ad.getDeal_lots();

                if (i == 5) {
                    fiveturnover = Caculator.keepRound(totdeal / (ften.getLiutonggu().doubleValue() * 100 * 5), 2);
                }
                if (i == 10) {
                    tenturnover = Caculator.keepRound(totdeal / (ften.getLiutonggu().doubleValue() * 100 * 10), 2);
                }
                if (i == 30) {
                    thirtyturnover = Caculator.keepRound(totdeal / (ften.getLiutonggu().doubleValue() * 100 * 30), 2);
                }
                i++;
            }
            log.info(stockcd + " fiveturnover=" + fiveturnover + " tenturnover=" + tenturnover + " thirtyturnover=" + thirtyturnover);
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("stock_cd", stockcd);
            mp.put("fivedayturnover", fiveturnover);
            mp.put("tendayturnover", tenturnover);
            mp.put("thirtydayturnover", thirtyturnover);

            updateDAO.execute("huoyuelank.insertHuoyuelank", mp);
            log.info(stockcd + " has been inserted");


        }


    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    private Map<String, Object> readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        Map<String, Object> mp = new HashMap<String, Object>();
        try {

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));

            String tempString = null;
            String meigusy = "";
            String meigujzc = "";
            String meigugjj = "";
            String meiguwfplr = "";
            String liutonggu = "";
            String tongbiincr = "";
            String jingzcbenifitrate = "";
            String suosuhangye = "";
            String liutonggudongchigubili = "";
            String update_date = "";
            String stock_name = "";
            String tempUsedName = "";

            boolean wuxianshoutiaojiangudong = false;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号

                if (tempString.contains("更新时间")) {
                    update_date = tempString.substring(tempString.lastIndexOf(":") + 1, tempString.lastIndexOf(":") + 11);
                    mp.put("update_date", update_date);
                    continue;
                }


                if (tempString.contains("每股收益 ")) {
                    meigusy = tempString.substring(tempString.indexOf(":") + 1).substring(0, 10).trim();
                    if (meigusy.contains("--")) {
                        return null;
                    }
                    mp.put("meigusy", Double.parseDouble(meigusy));

                }
                if (tempString.contains("目前流通")) {
                    liutonggu = tempString.substring(tempString.lastIndexOf(":") + 1);
                    if (liutonggu.contains("--")) {
                        return null;
                    }
                    mp.put("liutonggu", Double.parseDouble(liutonggu));
                    continue;
                }
                if (tempString.contains("每股净资产")) {
                    meigujzc = tempString.substring(tempString.indexOf(":") + 1).substring(0, 10).trim();
                    if (meigujzc.contains("--")) {
                        return null;
                    }
                    mp.put("meigujzc", Double.parseDouble(meigujzc));
                    continue;
                }
                if (tempString.contains("每股公积金")) {
                    meigugjj = tempString.substring(tempString.indexOf(":") + 1).substring(0, 10).trim();
                    if (meigugjj.contains("--")) {
                        return null;
                    }
                    mp.put("meigugjj", Double.parseDouble(meigugjj));
                    continue;
                }
                if (tempString.contains("每股未分配利润")) {
                    meiguwfplr = tempString.substring(tempString.indexOf(":") + 1).substring(0, 10).trim();
                    if (meiguwfplr.contains("--")) {
                        return null;
                    }
                    mp.put("meiguwfplr", Double.parseDouble(meiguwfplr));

                }


                if (tempString.contains("净利润同比增长")) {
                    if (mp.containsKey("tongbiincr")) {
                        continue;
                    }
                    tongbiincr = tempString.substring(tempString.lastIndexOf(":") + 1);
                    if (tongbiincr.contains("--")) {
                        mp.put("tongbiincr", new Double(-10000));
                    } else {
                        mp.put("tongbiincr", Double.parseDouble(tongbiincr));
                    }

                    continue;

                }
                if (tempString.contains("净资产收益率")) {
                    if (mp.containsKey("jingzcbenifitrate")) {
                        continue;
                    }
                    jingzcbenifitrate = tempString.substring(tempString.lastIndexOf(":") + 1);
                    if (jingzcbenifitrate.contains("--")) {
                        mp.put("jingzcbenifitrate", new Double(-10000));
                    } else {
                        mp.put("jingzcbenifitrate", Double.parseDouble(jingzcbenifitrate));
                    }

                    continue;
                }

                if (tempString.contains("曾用名")) {
                    tempUsedName = tempUsedName + tempString;
                    continue;
                }
                if (!tempUsedName.equals("")) {
                    tempUsedName = tempUsedName + tempString;
                }
                if (tempString.contains("大智慧金融交易")) {

                    if (!tempUsedName.equals("")) {

                        if (tempUsedName.contains("Sk")) {
                            if (tempUsedName.lastIndexOf("Sk") > tempUsedName.lastIndexOf("->")) {

                                return null;
                            }
                        }
                    }

                    break;
                }

                if (tempString.contains("所属行业")) {
                    stock_name = tempString.substring(0, tempString.indexOf("("));

                    mp.put("stock_name", stock_name);
                    continue;
                }

                if (tempString.contains("证监会行业")) {
                    suosuhangye = tempString.substring(tempString.indexOf(":") + 1);
                    suosuhangye = suosuhangye.substring(0, suosuhangye.indexOf(" "));
                    mp.put("suosuhangye", suosuhangye);
                    break;
                }
                if (tempString.contains("前十名无限售条件")) {
                    wuxianshoutiaojiangudong = true;
                    continue;
                }
                if (wuxianshoutiaojiangudong == true
                        && tempString.contains("总    计")) {
                   
                    int idx = tempString.indexOf("%");
                    if (idx < 0) {
                        mp.put("liutonggudongchigubili", 0.0);
                        break;
                    }
                    liutonggudongchigubili = tempString.substring(idx - 5);
                    liutonggudongchigubili = liutonggudongchigubili.substring(0, liutonggudongchigubili.length() - 1);
                    mp.put("liutonggudongchigubili", Double.parseDouble(liutonggudongchigubili));
                    break;
                }


                line++;
            }
            reader.close();

            return mp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
