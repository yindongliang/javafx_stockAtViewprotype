package answer.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.Operator;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

import answer.util.BeanPrinter;
import answer.util.Convertor;
import answer.ax.input.EntrustRequest;
import answer.ax.input.QueryEntrustRequest;
import answer.ax.output.EntrustRecord;
import answer.ax.output.EntrustResponse;
import answer.bean.dto.Chosendata;
import answer.exception.Axexception;
import answer.exception.Axinfo;

@Service(value = "StockSailer")
public class StockSailer {

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
    private static Logger log = Logger.getLogger(StockSailer.class);

    public String sell(List<Chosendata> cdls) throws NativeException, Axinfo, Axexception {

        log.info("sell command is starting");
        Axinfo axinfo = null;

        Pointer output = new Pointer(MemoryBlockFactory.createMemoryBlock(200));
        if (!axoperator.islogin) {
            axoperator.intitAndlogin();
        }


        for (int i = 0; i < cdls.size(); i++) {

            Chosendata chosendata = cdls.get(i);

            if (!chosendata.isTelecommuter()) {
                continue;
            }
            if (!chosendata.getStatus().equals("买成")) {
                continue;
            }

            String stockcd = chosendata.getStock_cd();

            Map<String, String> condition = new HashMap<String, String>();

            condition.put("stock_cd", stockcd);
            // get the buy information of the stock
            List<Chosendata> sellstock = queryDAO.executeForObjectList("sailer.searchBeforSell", condition);

            if (sellstock.size() == 0) {
                log.info(stockcd + " sell command has been ignored, because this stock is exsiting");
                continue;
            }
            List<String> stock_detail = httpHelper.sendRequest(stockcd, popertiesHelper.getStocksProperties());

            float curprice = Float.parseFloat(stock_detail.get(3));

            EntrustRequest ereq = new EntrustRequest();

            ereq.setCurrency("1");// 人民币
            ereq.setPrice(curprice);
            ereq.setQuantity(sellstock.get(0).getBuy_lots().intValue());
            ereq.setSecuCode(stockcd);

            if (stockcd.startsWith("60")) {
                ereq.setMarket("1");//登陆账户的  交易所代码
                ereq.setShareHolderCode(popertiesHelper.getStocksProperties().getString("sh_market_code"));//股东账号
            }
            if (stockcd.startsWith("30") || stockcd.startsWith("00")) {
                ereq.setMarket("2");//登陆账户的  交易所代码
                ereq.setShareHolderCode(popertiesHelper.getStocksProperties().getString("sz_market_code"));//股东账号
            }
            ereq.setTransactionType("2");//卖出
            log.info(BeanPrinter.dump(ereq));

            output = new Pointer(MemoryBlockFactory.createMemoryBlock(100));
            // 委托提交
            int ret = axoperator.axS_Entrust(ereq.getPointer(), output);

            if (ret != 0) {//委托失败
                log.info("operation failed" + " return code " + ret + " because :" + axoperator.axS_GetErrorDesc());
                if (axinfo == null) {
                    axinfo = new Axinfo();
                }
                axinfo.put(stockcd, axoperator.axS_GetErrorDesc());
                continue;
            }
            EntrustResponse entrustResponse = new EntrustResponse(output, popertiesHelper.getStocksProperties().getString("ax_encode"));
            log.info("entrustResponse'data as following----------------");
            log.info(BeanPrinter.dump(entrustResponse));

            Map<String, String> condition2 = new HashMap<String, String>();

            condition2.put("stock_cd", stockcd);
            condition2.put("status", "3");
            condition2.put("buy_date", chosendata.getBuy_date());

            updateDAO.execute("sailer.updateSailstockStatus", condition);
            log.info(stockcd + " has been entrusted for sell at price: " + curprice);
            chosendata.setStatus("委卖");// update the bindproperty
        }
        if (axinfo != null) {
            throw axinfo;
        }
        log.info("sell command is end sucessfully");
        return "0";


//		int[] idxArr = new int[cdls.size()];
//        // get the detail of the stocks
//		int j =0;
//		for (int i=0;i<cdls.size();i++){
//			Chosendata csdata=cdls.get(i);
//			if (csdata.isTelecommuter()){
//				
//				String stockcd =csdata.getStock_cd();
//				Map<String,String> condition = new HashMap<String,String>();
//				List<String> stock_detail = httpHelper.sendRequest(stockcd,popertiesHelper.getStocksProperties());
//				
//				condition.put("stock_cd", stockcd);
//				condition.put("sell_price", stock_detail.get(3));
//				condition.put("sell_date", stock_detail.get(30));
//				condition.put("sell_time", stock_detail.get(31));
//				condition.put("buy_date", csdata.getBuy_date());
//				double curprice = Double.parseDouble(stock_detail.get(3));
//				double ydboughtprice = csdata.getBuy_price().doubleValue();
//				
//				double incr = Caculator.keepRound((curprice-ydboughtprice)*100/ydboughtprice,2);
//				double purebenifit=LogicHelper.caculatePurebenifit(curprice,
//						csdata.getBuy_price().doubleValue(),
//						csdata.getBuy_lots().intValue(),
//						popertiesHelper.getStocksProperties());
//				condition.put("pure_benifit", purebenifit+"");
//				condition.put("incr_range_compare_bought", incr+"");
//				condition.put("sell_complete_flg", "1");
//				updateDAO.execute("sailer.updateSailstock", condition);
//				log.info( stockcd+ " has been selt on "+ stock_detail.get(30));
//				
//				idxArr[j]=i;
//				j++;
//			}
//		}
//		for (int x=0;x<idxArr.length;x++){
//			if(x!=0&&idxArr[x]==0){
//				break;
//			}
//			cdls.remove(idxArr[x]-x);
//		}

//		return "0";
    }

    public String checkEntrustStatus(List<Chosendata> chosendatals) throws NativeException, Axexception {

        if (!axoperator.islogin) {
            axoperator.intitAndlogin();

        }
        String dt = Formater.dateFormater("yyyyMMdd");
        for (Chosendata chosendata : chosendatals) {
            if (chosendata.getStatus() != null && chosendata.getStatus().equals("委卖")) {
                QueryEntrustRequest ereq = new QueryEntrustRequest();

                ereq.setBeginDate(dt);
                ereq.setEndDate(dt);
                ereq.setSecuCode(chosendata.getStock_cd());
                Pointer stockcntp = new Pointer(MemoryBlockFactory.createMemoryBlock(4));
                int stocklen = 4;
                stockcntp.setIntAt(0, stocklen);
                Pointer output = new Pointer(MemoryBlockFactory.createMemoryBlock(215 * stocklen));
                // 委托提交
                int ret = axoperator.axS_QueryEntrust(ereq.getPointer(), stockcntp, output);

                if (ret != 0) {

                    log.info("for stock" + chosendata.getStock_cd() + "axS_QueryEntrust operation failed" + "return code " + ret + " because :" + axoperator.axS_GetErrorDesc());
                    throw new Axexception(chosendata.getStock_cd() + ":" + axoperator.axS_GetErrorDesc());
                }
                byte[] contentbyte = output.getMemory();
                for (int i = 0; i < stocklen; i++) {

                    Pointer resultdatapointer = new Pointer(MemoryBlockFactory.createMemoryBlock(215));
                    resultdatapointer.setMemory(Convertor.cut(contentbyte, i * 215, 215));
                    EntrustRecord entrustRecord = new EntrustRecord(resultdatapointer, popertiesHelper.getStocksProperties().getString("ax_encode"));

                    if (!entrustRecord.getM_nDealTime().equals("") && entrustRecord.getM_cTransaction().equals("2")) {
                        Map<String, Object> condition = new HashMap<String, Object>();
                        condition.put("status", "4");//卖出成交
                        condition.put("stock_cd", entrustRecord.getM_cStockCode());
                        condition.put("buy_date", Formater.stringdateChange(entrustRecord.getM_nEntrustDate().trim()));
                        log.info(entrustRecord.getM_cStockCode() + " has been done for sell");
                        updateDAO.execute("buyer.updateChosendata", condition);
                        chosendata.setStatus("卖成");
                        break;
                    }
                }
            }
        }


        return "0";
    }
}
