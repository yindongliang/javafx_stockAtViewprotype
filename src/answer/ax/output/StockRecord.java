package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;
//余股查询结果
public class StockRecord {
	
	String  m_cExchange;			// 交易所
	String 	m_cHolderCode;	// 股东代码
	String	m_cStockCode;		// 股票代码
	String	m_cStockName;		// 股票名称
	int		m_nStockBalance;		// 总股数(股)
	int		m_nStockUsable;			// 可用(可卖)数(股)
	int		m_nStockDisable;		// 冻结数
	double	m_nValue;				// 市值(元)
	double  m_nProfitLoss;			// 浮动盈亏
	double  m_nPrice;				// 持仓价

	//-------2007-8-7日加
	int		m_nUSER_CODE;
	String	m_szACCOUNT;
	String	m_szSEAT;
	int		m_nBRANCH;
	int		m_nEXT_INST;
	int		m_nSHARE_TRD_FRZ;
	int		m_nSHARE_OTD;			//股份在途数量
	int		m_nSHARE_FRZ;
	double	m_fCURRENT_COST;
	double	m_fMKT_VAL;
	double	m_fCOST_PRICE;
	int		m_nSHARE_OTD_AVL;
	double	m_fCURR_PRICE;
	double	m_fCOST2_PRICE;
	int		m_nMKT_QTY;
	public int datalength=161;
	
	public StockRecord(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            
            m_cExchange=(new String(Convertor.cut(org, 0, 1),charset)).trim();
            m_cHolderCode=(new String(Convertor.cut(org, 1, 11),charset)).trim();
            m_cStockCode=(new String(Convertor.cut(org, 12, 7),charset)).trim();
            m_cStockName=(new String(Convertor.cut(org, 19, 9),charset)).trim();
            m_nStockBalance=pointer.getAsInt(28);
            m_nStockUsable=pointer.getAsInt(32);
            m_nStockDisable=pointer.getAsInt(34);
            m_nValue=pointer.getAsDouble(38);
            m_nProfitLoss=pointer.getAsDouble(46);
            m_nPrice=pointer.getAsDouble(54);

            m_nUSER_CODE=pointer.getAsInt(62);
            m_szACCOUNT=(new String(Convertor.cut(org, 66, 20),charset)).trim();
            m_szSEAT=(new String(Convertor.cut(org, 86, 7),charset)).trim();
            m_nBRANCH=pointer.getAsInt(93);
            m_nEXT_INST=pointer.getAsInt(85+12);
            m_nSHARE_TRD_FRZ=pointer.getAsInt(89+12);
            m_nSHARE_OTD=pointer.getAsInt(93+12);
            m_nSHARE_FRZ=pointer.getAsInt(97+12);
            m_fCURRENT_COST=pointer.getAsDouble(101+12);
            m_fMKT_VAL=pointer.getAsDouble(109+12);
            m_fCOST_PRICE=pointer.getAsDouble(117+12);
            m_nSHARE_OTD_AVL=pointer.getAsInt(137);
            m_fCURR_PRICE=pointer.getAsDouble(141);
            m_fCOST2_PRICE=pointer.getAsDouble(149);
            m_nMKT_QTY=pointer.getAsInt(157);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(StockRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		 
	}

	public String getM_cExchange() {
		return m_cExchange;
	}

	public String getM_cHolderCode() {
		return m_cHolderCode;
	}

	public String getM_cStockCode() {
		return m_cStockCode;
	}

	public String getM_cStockName() {
		return m_cStockName;
	}

	public int getM_nStockBalance() {
		return m_nStockBalance;
	}

	public int getM_nStockUsable() {
		return m_nStockUsable;
	}

	public int getM_nStockDisable() {
		return m_nStockDisable;
	}

	public double getM_nValue() {
		return m_nValue;
	}

	public double getM_nProfitLoss() {
		return m_nProfitLoss;
	}

	public double getM_nPrice() {
		return m_nPrice;
	}

	public int getM_nUSER_CODE() {
		return m_nUSER_CODE;
	}

	public String getM_szACCOUNT() {
		return m_szACCOUNT;
	}

	public String getM_szSEAT() {
		return m_szSEAT;
	}

	public int getM_nBRANCH() {
		return m_nBRANCH;
	}

	public int getM_nEXT_INST() {
		return m_nEXT_INST;
	}

	public int getM_nSHARE_TRD_FRZ() {
		return m_nSHARE_TRD_FRZ;
	}

	public int getM_nSHARE_OTD() {
		return m_nSHARE_OTD;
	}

	public int getM_nSHARE_FRZ() {
		return m_nSHARE_FRZ;
	}

	public double getM_fCURRENT_COST() {
		return m_fCURRENT_COST;
	}

	public double getM_fMKT_VAL() {
		return m_fMKT_VAL;
	}

	public double getM_fCOST_PRICE() {
		return m_fCOST_PRICE;
	}

	public int getM_nSHARE_OTD_AVL() {
		return m_nSHARE_OTD_AVL;
	}

	public double getM_fCURR_PRICE() {
		return m_fCURR_PRICE;
	}

	public double getM_fCOST2_PRICE() {
		return m_fCOST2_PRICE;
	}

	public int getM_nMKT_QTY() {
		return m_nMKT_QTY;
	}
	
	
}
