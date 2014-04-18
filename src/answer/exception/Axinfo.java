package answer.exception;

import java.util.HashMap;
import java.util.Map;

public class Axinfo extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,String> mapinfo = new HashMap<String,String>();
	
	public void put(String key,String value ){
		mapinfo.put(key, value);
	}
	
	public Map<String,String> getMapinfo(){
		return mapinfo;
	}
}
