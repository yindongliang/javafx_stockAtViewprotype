package answer.logic;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.util.Caculator;
import answer.bean.PerformanceBean;
import answer.bean.dto.Chosendata;
import answer.bean.dto.ChosendataPF;
import answer.exception.Axexception;
import answer.exception.Axinfo;
import java.util.ArrayList;
import org.xvolks.jnative.exceptions.NativeException;

@Service(value = "StockMonitor")
public class StockMonitor {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(StockMonitor.class);
    @Autowired
    protected StockSailer stockSailer = null;

    /*
     * this method is for getting initial data in the tableview.
     *
     */
    public Map<String, Object> getSellableStocks() {

        Map<String, Object> resultmp = new HashMap<String, Object>();
        List<Chosendata> chosendatalist = queryDAO.executeForObjectList("performances.selectSellableStock", null);
        double commission_rate = popertiesHelper.getStocksProperties().getDouble("commission_rate");
        double charge_rate = popertiesHelper.getStocksProperties().getDouble("charge_rate");
        int win = 0;
        int lost = 0;
        int noincr = 0;
        int stopopen = 0;
        double totinr = 0;
        int datacnt = chosendatalist.size();

        double purebenifit_tot = 0;
        double handlecharge_tot = 0;
        if (datacnt == 0) {
            return resultmp;
        }
        for (Chosendata cd : chosendatalist) {

            double curprice = getCurrentpriceOfstock(cd);
            log.info(cd.getStock_cd() + " current price: " + curprice);
            double buyprice = cd.getBuy_price().doubleValue();
            double buylots = cd.getBuy_lots().doubleValue();

            double handlecharge = buyprice * buylots * commission_rate;
            double incr = Caculator.keepRound((curprice - buyprice) * 100 / buyprice, 2);
            if (buyprice * buylots * charge_rate < 5) {
                handlecharge = handlecharge + 10;
            } else {
                handlecharge = handlecharge + buyprice * buylots * charge_rate * 2;
            }

            double purebenifit = Caculator.keepRound(
                    (curprice - buyprice) * buylots - handlecharge, 2);

            cd.setCurrent_price(BigDecimal.valueOf(curprice));
            cd.setIncr_range_compare_bought(BigDecimal.valueOf(incr));



            // the total hand charge
            handlecharge_tot = handlecharge_tot + LogicHelper.caculatehandCharge(cd.getBuy_price().doubleValue(),
                    cd.getBuy_lots().intValue(),
                    popertiesHelper.getStocksProperties());
            if (incr > -99) {
                cd.setPure_benifit(purebenifit + "");
                // the total pure benifit
                purebenifit_tot = purebenifit_tot + purebenifit;
            } else {
                cd.setPure_benifit("停牌");
            }

            // count win
            if (incr > 0) {
                win++;
            }
            // count lost
            if (incr < 0 && incr != -99.99) {
                lost++;
            }
            // count no increase
            if (incr == 0) {
                noincr++;
            }
            // count stop stocks
            if (incr == -99.99) {
                stopopen++;
            }
            // count the whole increase
            if (incr != -99.99) {
                totinr = totinr + incr;
            }


        }
        // get shanghai's detail
        List<String> stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
                popertiesHelper.getStocksProperties());


        double curprice = Double.parseDouble(stock_detail.get(3));
        // compare to yesterday's price caculate the increment
        double ydboughtprice = chosendatalist.get(0).getBought_shanghai_price().doubleValue();
        double incr = LogicHelper.caculateIncrease(ydboughtprice, curprice);

        PerformanceBean pfbean = getWholePF(stock_detail, totinr, datacnt, stopopen, noincr, win, lost, incr, purebenifit_tot, handlecharge_tot);

        resultmp.put("chosendatalist", chosendatalist);
        resultmp.put("pfbean", pfbean);

        log.info("the sellable stocks have been selected");
        return resultmp;

    }

    public List<ChosendataPF> getSingleStockPerform(String stock_cd, String viewdate) {
//		List<String> stock_detail = null;
//		if (popertiesHelper.getStocksProperties().getString("shindice_code").equals(stock_cd)
//				||popertiesHelper.getStocksProperties().getString("bought_stock_ave").equals(stock_cd)){
//			stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),popertiesHelper.getStocksProperties());
//			
//		}
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("stock_cd", stock_cd);

        condition.put("record_date", viewdate);
        List<ChosendataPF> averagPFlist = queryDAO.executeForObjectList("performances.selectTargetStockPF", condition);
        return averagPFlist;
    }

    /*
     * this method is for update overview and tableview,and insert the PF data
     * by the way.
     *
     *
     *
     */
    public PerformanceBean getoverviewPFAndUpdatePF(List<Chosendata> cdls, double winstop, double loststop) throws NativeException, Axinfo, Axexception {
        int bkp = 0;
        int win = 0;
        int lost = 0;
        int noincr = 0;
        int stopopen = 0;
        double totinr = 0;
        int datacnt = cdls.size();

        List<String> stock_detail = null;
        ChosendataPF stockPF;
        double purebenifit_tot = 0;
        double handlecharge_tot = 0;
        for (int j = bkp; j < datacnt; j++) {
            Chosendata chosendata = cdls.get(j);

            stock_detail = httpHelper.sendRequest(chosendata.getStock_cd(), popertiesHelper.getStocksProperties());
            double curprice = Double.parseDouble(stock_detail.get(3));
            double ydboughtprice = chosendata.getBuy_price().doubleValue();

            double incr = Caculator.keepRound((curprice - ydboughtprice) * 100 / ydboughtprice, 2);

            stockPF = new ChosendataPF();
            stockPF.setStock_cd(chosendata.getStock_cd());
            stockPF.setBought_date(chosendata.getBuy_date());
            stockPF.setBought_price(chosendata.getBuy_price().doubleValue());
            stockPF.setBought_date(chosendata.getBuy_date());
            stockPF.setIncr_range_compare_bought(BigDecimal.valueOf(incr));
            stockPF.setRecord_date(stock_detail.get(30));
            stockPF.setRecord_time(stock_detail.get(31));
            // insert the data to chosendataPF for history show
            insertStocksPF(stockPF, stock_detail);

            // 更新显示表数据
            double purebenifit = LogicHelper.caculatePurebenifit(curprice,
                    chosendata.getBuy_price().doubleValue(),
                    chosendata.getBuy_lots().intValue(),
                    popertiesHelper.getStocksProperties());
            // 总手续费
            handlecharge_tot = handlecharge_tot + LogicHelper.caculatehandCharge(chosendata.getBuy_price().doubleValue(),
                    chosendata.getBuy_lots().intValue(),
                    popertiesHelper.getStocksProperties());
            // 总利润
            chosendata.setCurrent_price(BigDecimal.valueOf(curprice));
            if (incr > -99) {
                chosendata.setPure_benifit(purebenifit + "");
                purebenifit_tot = purebenifit_tot + purebenifit;
            }

            if (incr > winstop && chosendata.getStatus().equals("买成")) {// 自动卖出无条件止盈
                List<Chosendata> ls = new ArrayList<Chosendata>();
                ls.add(chosendata);
                stockSailer.sell(ls);
            }

            if (incr < loststop && incr > -99 && chosendata.getStatus().equals("买成")) {// 自动止损
                List<Chosendata> ls = new ArrayList<Chosendata>();
                ls.add(chosendata);
                stockSailer.sell(ls);

            }

            BigDecimal ic = new BigDecimal(incr);
            ic.setScale(2, BigDecimal.ROUND_HALF_UP);
            chosendata.setIncr_range_compare_bought(ic);
            // count win
            if (incr > 0) {
                win++;
            }
            // count lost
            if (incr < 0 && incr != -99.99) {
                lost++;
            }
            // count no increase
            if (incr == 0) {
                noincr++;
            }
            // count stop stocks
            if (incr == -99.99) {
                stopopen++;
            }
            // count the whole increase
            if (incr != -99.99) {
                totinr = totinr + incr;
            }

            log.info(chosendata.getStock_cd() + "\t" + incr);

        }

        // insert shanghai indice minite data
        stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
                popertiesHelper.getStocksProperties());


        double curprice = Double.parseDouble(stock_detail.get(3));
        double ydboughtprice = cdls.get(0).getBought_shanghai_price().doubleValue();

        double incr = LogicHelper.caculateIncrease(ydboughtprice, curprice);

        stockPF = new ChosendataPF();
        stockPF.setStock_cd(popertiesHelper.getStocksProperties().getString("shindice_code"));
        stockPF.setBought_date(cdls.get(0).getBuy_date());
        stockPF.setBought_price(ydboughtprice);
        stockPF.setIncr_range_compare_bought(BigDecimal.valueOf(incr));
        stockPF.setRecord_date(stock_detail.get(30));
        stockPF.setRecord_time(stock_detail.get(31));

        // insert the data to chosendataPF for history show
        insertStocksPF(stockPF, stock_detail);

        log.info("shanghai indices'increase is  " + incr + "%");

        // insert the whole stocks average performance except the stopping one,si000009 is representive for average increase
        stockPF = new ChosendataPF();
        stockPF.setStock_cd(popertiesHelper.getStocksProperties().getString("bought_stock_ave"));
        stockPF.setBought_date(cdls.get(0).getBuy_date());
        stockPF.setBought_price(0);
        stockPF.setIncr_range_compare_bought(BigDecimal.valueOf(Caculator.keepRound(totinr / (datacnt - stopopen), 2)));
        stockPF.setRecord_date(stock_detail.get(30));
        stockPF.setRecord_time(stock_detail.get(31));

        insertStocksPF(stockPF, stock_detail);


        if (datacnt != 0) {

            log.info("the win rate is " + (long) win * 100 / datacnt + "%");
            log.info("the lost rate is " + (long) lost * 100 / datacnt + "%");
            log.info("the tie rate is " + (long) noincr * 100 / datacnt + "%");
            log.info("the stop rate is " + (long) stopopen * 100 / datacnt + "%");
            log.info("total " + datacnt + " stocks");
            log.info("total increase is " + Caculator.keepRound(totinr, 2) + "%");
            log.info("average increase is " + Caculator.keepRound(totinr / (datacnt - stopopen), 2) + "%");
        }



        PerformanceBean pfbean = getWholePF(stock_detail, totinr, datacnt, stopopen, noincr, win, lost, incr, purebenifit_tot, handlecharge_tot);
        // insert the whole stocks average performance except the stopping one,this is for chart view
//		insertStocksAVEPF(Caculator.keepRound(totinr/(datacnt-stopopen),2),cdls.get(0).getBuy_date(),stock_detail.get(30),stock_detail.get(31));

        return pfbean;
    }

    private PerformanceBean getWholePF(List<String> stock_detail,
            double totinr,
            int datacnt,
            int stopopen,
            int noincr,
            int win,
            int lost,
            double incr_sh,
            double purebenifit_tot,
            double handlecharge_tot) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("stock_cd", popertiesHelper.getStocksProperties().getString("bought_stock_ave"));
        condition.put("record_date", stock_detail.get(30));
        ChosendataPF averegpf = queryDAO.executeForObject("performances.selectTargetStockPFMM", condition, ChosendataPF.class);


        PerformanceBean pfbean = new PerformanceBean();
        pfbean.setCurrent_ave(Caculator.keepRound(totinr / (datacnt - stopopen), 2));
        pfbean.setCurrent_tot(Caculator.keepRound(totinr, 2));
        pfbean.setDraw_rate(Caculator.keepRound(noincr * 100 / datacnt, 2));
        pfbean.setWin_rate(Caculator.keepRound(win * 100 / datacnt, 2));
        pfbean.setLost_rate(Caculator.keepRound(lost * 100 / datacnt, 2));
        pfbean.setStop_rate(Caculator.keepRound(stopopen * 100 / datacnt, 2));
        if (averegpf == null || averegpf.getMxincr() == null) {
            pfbean.setHighest_ave(0);
        } else {

            pfbean.setHighest_ave(averegpf.getMxincr().doubleValue());
        }
        if (averegpf == null || averegpf.getMinincr() == null) {
            pfbean.setLowest_ave(0);
        } else {

            pfbean.setLowest_ave(averegpf.getMinincr().doubleValue());
        }
        pfbean.setShanghai_incr(incr_sh);
        pfbean.setPurebenifit_tot(Caculator.keepRound(purebenifit_tot, 2));
        pfbean.setHandlecharge_tot(Caculator.keepRound(handlecharge_tot, 2));
        return pfbean;
    }

    private void insertStocksPF(ChosendataPF stock, List<String> stock_detail) {

        if (stock_detail == null) {

            stock_detail = httpHelper.sendRequest(stock.getStock_cd(), popertiesHelper.getStocksProperties());
            double curprice = Double.parseDouble(stock_detail.get(3));
            double boughtprice = stock.getBought_price();

            double incr = Caculator.keepRound((curprice - boughtprice) * 100 / boughtprice, 2);

            stock.setIncr_range_compare_bought(BigDecimal.valueOf(incr));
            log.info(stock.getStock_cd() + " stock compare to bought price the increase is " + incr);
        }
        updateDAO.execute("performances.insert2ChosendataPF", stock);

    }

//	private  void insertStocksAVEPF(double incr_ave, String bought_date, String record_date,String record_time) {
//		
//		Map<String,String> condition = new HashMap<String,String>();
//		
//		condition.put("incr_ave", incr_ave+"");
//		condition.put("bought_date", bought_date);
//		condition.put("record_date", record_date);
//		condition.put("record_time", record_time);
//	
//		updateDAO.execute("performances.insert2ChosendatasAVEPF", condition);
//		
//	}
    private double getCurrentpriceOfstock(Chosendata stock) {

        List<String> stock_detail = httpHelper.sendRequest(stock.getStock_cd(), popertiesHelper.getStocksProperties());
        double curprice = Double.parseDouble(stock_detail.get(3));

        return curprice;
    }

    public String getCurrentTradedateOfstock(Chosendata stock) {

        List<String> stock_detail = httpHelper.sendRequest(stock.getStock_cd(), popertiesHelper.getStocksProperties());


        return stock_detail.get(30);
    }
//	/*
//	 * this is for update the single data exsited in tableView 
//	 * 
//	 * */
//	public void updateSingleStockFortable(Chosendata cd){
//			
//		double curprice=0;
//		try {
//			curprice = getCurrentpriceOfstock(cd);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		double buyprice = cd.getBuy_price().doubleValue();
//		int buylots=cd.getBuy_lots().intValue();
//		
//		double purebenifit=LogicHelper.caculatePurebenifit(curprice,buyprice,buylots,popertiesHelper.getStocksProperties());
//		
//		cd.setCurrent_price(BigDecimal.valueOf(curprice));
//		
//		cd.setPure_benifit(purebenifit+"");
//		
//		
//		
//	}
}
