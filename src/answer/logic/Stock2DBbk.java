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
import java.net.UnknownHostException;

@Service(value="Stock2DBbk")
public class Stock2DBbk {
	
	@Autowired
	protected HttpHelper httpHelper = null;
	
	@Autowired
    protected UpdateDAO updateDAO = null;

	@Autowired
    protected QueryDAO queryDAO = null;
	
	@Autowired
	protected  PopertiesHelper popertiesHelper=null;
	
	
    private static Logger log = Logger.getLogger(Stock2DB.class);   

	
	public String getAlldata() throws UnknownHostException{
		// get all stock's code
        List<String> ls_shsz = popertiesHelper.getStockCds();
        // add the shanghai indices
        ls_shsz.add(popertiesHelper.getStocksProperties().getString("shindice_code"));
        List<String> stock_detail = null;
        // get the detail of the stocks
		for (int j =0;j<ls_shsz.size();j++) {
			
			stock_detail = httpHelper.sendRequest(ls_shsz.get(j),popertiesHelper.getStocksProperties());
			
			if (!LogicHelper.isOpening(stock_detail)||!LogicHelper.isStock(stock_detail)){
				continue;
			}
			Alldata ad = Formater.editCriteria(stock_detail);
			int cn = queryDAO.executeForObject("download.checkBeforeInsertbk", ad, Integer.class);
			if (cn==0){
				updateDAO.execute("download.insert2Alldatabk", stock_detail);
				log.info("data has been inserted :" + ad.getStock_cd()+"-"+ad.getRecord_date()+"-"+ad.getRecord_time());
			}else{
				log.info("data is exsiting :" + ad.getStock_cd()+"-"+ad.getRecord_date()+"-"+ad.getRecord_time());
			}
			
			
			
		}
		
		return "0";
	}
	
	
}
