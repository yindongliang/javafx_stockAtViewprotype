package answer.logic;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.Operator;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

import answer.exception.Axexception;
import answer.exception.Axinfo;

import answer.util.BeanPrinter;
import answer.util.Caculator;
import answer.util.Convertor;

import answer.ax.input.EntrustRequest;
import answer.ax.input.QueryEntrustRequest;
import answer.ax.output.BalanceRecord;
import answer.ax.output.EntrustRecord;
import answer.ax.output.EntrustResponse;
import answer.bean.dto.Analyzedresultdata;
import answer.bean.dto.Chosendata;
import java.io.UnsupportedEncodingException;

@Service(value = "StockBuyer")
public class StockBuyer {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    @Autowired
    protected Operator axoperator = null;
    private static Logger log = Logger.getLogger(StockBuyer.class);

    public String buy(List<Analyzedresultdata> analyzedresultdatals, int averagemoney) throws  NativeException, Axexception, Axinfo{
        
        log.info("buy command is starting");
        Axinfo axinfo = null;
        List<String> stock_detail_sh = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"), popertiesHelper.getStocksProperties());

        Pointer output = new Pointer(MemoryBlockFactory.createMemoryBlock(300));
        // todo 
//        if (!axoperator.islogin) {
//            axoperator.intitAndlogin();
//        }
//
//        Pointer len = new Pointer(MemoryBlockFactory.createMemoryBlock(8));
//        len.setIntAt(0, 1);
//
//        int ret_QueryBalance = axoperator.axS_QueryBalance('R', len, output);
//        if (ret_QueryBalance != 0) {
//
//            log.info("operation failed" + "return code " + ret_QueryBalance + " because :" + axoperator.axS_GetErrorDesc());
//            throw new Axexception(axoperator.axS_GetErrorDesc());
//        }
//        BalanceRecord brecord = new BalanceRecord(output, popertiesHelper.getStocksProperties().getString("ax_encode"));
//
//        log.info(brecord.getM_nBailUsable());
//        // 可用金额
//        double moneyusable = brecord.getM_nBailUsable();
//		double moneyusable= 10000;
        for (int i = 0; i < analyzedresultdatals.size(); i++) {

            Analyzedresultdata analyzeddata = analyzedresultdatals.get(i);

//          todo  if (!analyzeddata.isTelecommuter()) {
//                continue;
//            }
            String stockcd = analyzeddata.getStock_cd();
            // get the detail of the stocks
            Map<String, String> condition = new HashMap<String, String>();

            condition.put("stock_cd", stockcd);

            int cn = queryDAO.executeForObject("buyer.searchBeforBuy", condition, Integer.class);

            if (cn != 0) {
                log.info(stockcd + " buy command has been ignored, because this stock is exsiting");
                continue;
            }
            List<String> stock_detail = httpHelper.sendRequest(stockcd, popertiesHelper.getStocksProperties());

            float curprice = Float.parseFloat(stock_detail.get(3));

         // todo 
//            if (moneyusable < averagemoney) {//金额不够
//                break;
//            }
            int lots = (int) (averagemoney / curprice);

            int hundred_lots = (lots + 50) / 100;

//            EntrustRequest ereq = new EntrustRequest();
//
//            ereq.setCurrency("1");// 人民币
//            ereq.setPrice(curprice);
//            ereq.setQuantity(hundred_lots * 100);
//            ereq.setSecuCode(stockcd);
//            if (stockcd.startsWith("60")) {
//                ereq.setMarket("1");//登陆账户的  交易所代码
//                ereq.setShareHolderCode(popertiesHelper.getStocksProperties().getString("sh_market_code"));//股东账号
//            }
//            if (stockcd.startsWith("30") || stockcd.startsWith("00")) {
//                ereq.setMarket("2");//登陆账户的  交易所代码
//                ereq.setShareHolderCode(popertiesHelper.getStocksProperties().getString("sz_market_code"));//股东账号
//            }
//
//
//            ereq.setTransactionType("1");//买入
//
//
//            output = new Pointer(MemoryBlockFactory.createMemoryBlock(80));
//            // 委托提交
//            int ret = axoperator.axS_Entrust(ereq.getPointer(), output);
//
//            if (ret != 0) {//委托失败
//                log.info("operation failed" + " return code " + ret + " because :" + axoperator.axS_GetErrorDesc());
//                if (axinfo == null) {
//                    axinfo = new Axinfo();
//                }
//                axinfo.put(stockcd, axoperator.axS_GetErrorDesc());
//                continue;
//            }
//            EntrustResponse entrustResponse = new EntrustResponse(output, popertiesHelper.getStocksProperties().getString("ax_encode"));
//            log.info("entrustResponse'data as following----------------");
//            log.info(BeanPrinter.dump(entrustResponse));
            // 可用金额减少
         // todo    moneyusable = moneyusable - hundred_lots * curprice;
           
            Chosendata chosendata = new Chosendata();
            chosendata.setStock_cd(stockcd);
            chosendata.setRest_sq(1);


            chosendata.setBuy_lots(new BigDecimal(hundred_lots * 100));
            chosendata.setBuy_price(new BigDecimal(curprice));//buy_price
            chosendata.setBuy_date(stock_detail.get(30));//buy_date
            chosendata.setBuy_time(stock_detail.get(31));//buy_time
            chosendata.setHandlecharge(new BigDecimal(LogicHelper.caculatehandCharge(curprice, lots, popertiesHelper.getStocksProperties())));
            chosendata.setBought_shanghai_price(new BigDecimal(stock_detail_sh.get(3)));//bought_shanghai_price
            chosendata.setStatus("1");// 委托买入中

            updateDAO.execute("buyer.insert2chosendata", chosendata);
            analyzeddata.setStatus("委卖");
            log.info(stockcd + " has been bought " + " lot: " + hundred_lots * 100 + " price: " + curprice);
        }
//        if (axinfo != null) {
//            throw axinfo;
//        }
        log.info("buy command is end sucessfully");
        return "0";
    }

    public double getusablemoney() throws   Axexception, NativeException{
        Pointer output = new Pointer(MemoryBlockFactory.createMemoryBlock(300));

        if (!axoperator.islogin) {
            axoperator.intitAndlogin();
        }

        Pointer len = new Pointer(MemoryBlockFactory.createMemoryBlock(4));

        len.setIntAt(0, 1);

//        int ret_QueryBalance = axoperator.axS_QueryBalance('R', len, output);
//        if (ret_QueryBalance != 0) {
//
//            log.info("operation failed" + "return code " + ret_QueryBalance + " because :" + axoperator.axS_GetErrorDesc());
//            throw new Axexception(axoperator.axS_GetErrorDesc());
//        }
        BalanceRecord brecord = new BalanceRecord(output, popertiesHelper.getStocksProperties().getString("ax_encode"));

        log.info("BalanceRecord'data as following----------------");

        log.info(BeanPrinter.dump(brecord));
        return Caculator.keepRound(brecord.getM_nBailUsable(), 2);
//		return 1000;

    }

    public String checkEntrustStatus(List<Analyzedresultdata> analyzedresultdatals) throws Axexception, NativeException {

//        if (!axoperator.islogin) {
//            axoperator.intitAndlogin();
//        }

        String dt = Formater.dateFormater("yyyyMMdd");
        // TODO need real environment
        for (Analyzedresultdata ansdata : analyzedresultdatals) {
            if (ansdata.getStatus() != null && !ansdata.getStatus().equals("卖成")) {
                QueryEntrustRequest ereq = new QueryEntrustRequest();
                System.out.println(ansdata.getStock_cd());
                ereq.setBeginDate(dt);
                ereq.setEndDate(dt);
                ereq.setSecuCode(ansdata.getStock_cd());
                Pointer stockcntp = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
                int stocklen = 100;
                stockcntp.setIntAt(0, stocklen);
                Pointer output = new Pointer(MemoryBlockFactory.createMemoryBlock(430 * stocklen));
                // 委托提交
//                int ret = axoperator.axS_QueryEntrust(ereq.getPointer(), stockcntp, output);
//
//                if (ret != 0) {
//
//                    log.info("for stock" + ansdata.getStock_cd() + "axS_QueryEntrust operation failed" + "return code " + ret + " because :" + axoperator.axS_GetErrorDesc());
//                    throw new Axexception(ansdata.getStock_cd() + ":" + axoperator.axS_GetErrorDesc());
//                }
                byte[] contentbyte = output.getMemory();
                for (int i = 0; i < stocklen; i++) {

                    Pointer resultdatapointer = new Pointer(MemoryBlockFactory.createMemoryBlock(600));
                    resultdatapointer.setMemory(Convertor.cut(contentbyte, i * 299, 299));
                    EntrustRecord entrustRecord = new EntrustRecord(resultdatapointer, popertiesHelper.getStocksProperties().getString("ax_encode"));
                    if (entrustRecord.getM_cStockCode().equals("")){
                        break;
                    }
                    log.info(BeanPrinter.dump(entrustRecord));
                    
                    if (!entrustRecord.getM_nDealTime().equals("") ) {
                        Map<String, Object> condition = new HashMap<String, Object>();
                        if(entrustRecord.getM_cTransaction().equals("1")){
                            condition.put("status", "2");//买入成交
                            ansdata.setStatus("买成");
                        }else if (entrustRecord.getM_cTransaction().equals("2")){
                            condition.put("status", "4");//卖出成交
                            ansdata.setStatus("卖成");
                        }
                        
                        condition.put("stock_cd", entrustRecord.getM_cStockCode());
//                        condition.put("buy_date", Formater.stringdateChange(entrustRecord.getM_nEntrustDate().trim()));
                         condition.put("buy_date", ansdata.getRecord_date());
                        log.info(entrustRecord.getM_cStockCode() + " has been done for buy");
                        updateDAO.execute("buyer.updateChosendata", condition);
                        
//                        break;
                    }
                }

            }
        }
        return "0";
    }
}
