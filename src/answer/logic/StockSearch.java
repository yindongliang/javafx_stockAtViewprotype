package answer.logic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.PopertiesHelper;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import answer.bean.dto.Alldata;
import answer.bean.dto.Analyzedresultdata;
import answer.bean.dto.Rules;

@Service(value="StockSearch")
public class StockSearch {
	
	@Autowired
	protected HttpHelper httpHelper = null;
	
	@Autowired
    protected UpdateDAO updateDAO = null;

	@Autowired
    protected QueryDAO queryDAO = null;
	
	@Autowired
	protected  PopertiesHelper popertiesHelper=null;
	
   // private static Logger log = Logger.getLogger(StockSearch.class);   
	public List<Alldata> getdatas(Map<String,String> mp) {
		
		List<Alldata> lsdta=queryDAO.executeForObjectList("download.searchdownloadResult",mp);
		return lsdta;
	}
	
	public List<Rules> getRules(Map<String,String> mp) {
		List<Rules> lsdta;
			
		lsdta=queryDAO.executeForObjectList("rules.searchRulesForlook",mp);

		return lsdta;
	}
	
	public List<Analyzedresultdata> getAnalyzedData(Map<String,String> mp) {
		List<Analyzedresultdata> lsdta;
			
		lsdta=queryDAO.executeForObjectList("candidate.searchAnalyzedData",mp);

		return lsdta;
	}

}
