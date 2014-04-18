package answer.logic;



import java.io.IOException;
import java.util.List;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.util.Caculator;
import answer.bean.dto.Analyzedresultdata;


@Service(value="PerformanceCheck")
public class PerformanceCheck {
	@Autowired
	protected HttpHelper httpHelper = null;
	
	@Autowired
    protected UpdateDAO updateDAO = null;

	@Autowired
    protected QueryDAO queryDAO = null;
	
	@Autowired
	protected  PopertiesHelper popertiesHelper=null;
	
	
    private static Logger log = Logger.getLogger(PerformanceCheck.class);  
    
    public  String checkPerformances()  {
    	
    	int bkp =0;
		int win =0;
		int lost= 0;
		int noincr= 0;
		int stopopen=0;
		double totinr=0;
		List<String> stock_detail = null;
		List<Analyzedresultdata> rbl =queryDAO.executeForObjectList("performance.checkPerformance", null);
//                List<Analyzedresultdata> rbl =queryDAO.executeForObjectList("performance.checkPerformancebydate", null);
                
		//List<Analyzedresultdata> rbl =queryDAO.executeForObjectList("temp.checkPerformance", null);
		
		for (int j =bkp;j<rbl.size();j++) {
			
			stock_detail = httpHelper.sendRequest(rbl.get(j).getStock_cd(),popertiesHelper.getStocksProperties());
			
			double curprice = Double.parseDouble(stock_detail.get(3));
			double ydprice = Double.parseDouble(stock_detail.get(2));
			
			double incr = Caculator.keepRound((curprice-ydprice)*100/ydprice,2);
			
			if (incr>0){
 				win++;
 				
 			}
     		
     		if (incr<0&&incr!=-99.99){
     			lost++;
     		}
     		if (incr==0){
 				noincr++;
 				
 			}
     		if (incr==-99.99){
     			stopopen++;
     		}
     		if (incr!=-99.99){
     			
     			totinr = totinr + incr;
     		}
     		log.info(rbl.get(j).getStock_cd() + "\t" +rbl.get(j).getRule_id()+ "\t" +incr);         
			
		}
		stock_detail = httpHelper.sendRequest(popertiesHelper.getStocksProperties().getString("shindice_code"),
											  popertiesHelper.getStocksProperties());
		double curprice_sh = Double.parseDouble(stock_detail.get(3));
		double ydprice_sh = Double.parseDouble(stock_detail.get(2));
		
		double incr_sh = Caculator.keepRound((curprice_sh-ydprice_sh)*100/ydprice_sh,2);
		
		if (rbl.size()!=0) {
			
			log.info("the win rate is "+(long) win*100/rbl.size()+"%");
			log.info("the lost rate is "+(long) lost*100/rbl.size()+"%");
			log.info("the tie rate is "+(long) noincr*100/rbl.size()+"%");
			log.info("the stop rate is "+(long) stopopen*100/rbl.size()+"%");
			log.info("total "+rbl.size()+ " stocks");
			log.info("total increase is "+Caculator.keepRound(totinr,2)+"%");
			log.info("average increase is "+Caculator.keepRound(totinr/(rbl.size()-stopopen),2)+"%");
		}
		log.info("shanghai indices is  "+incr_sh+"%");
		return "9";
	}
	
	
}
