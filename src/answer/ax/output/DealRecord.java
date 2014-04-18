package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DealRecord {

	String  m_cExchange;				// 交易所
	String  m_cHolderCode;		// 股东代码
	String  m_cEntrustNo;		// 委托序号
	String  m_cStockCode;		// 股票代码
	String  m_cStockName;		// 股票名称
	String  m_cTransaction;			// 交易类型, 1 表示买，2表示卖
	String  m_cTransactionDes;	// 交易类型文字说明
	int  m_nDealAmount;			// 成交数量（单位：股）
	double	m_nDealPrice;			// 成交价格（单位：元）
	double	m_nDealMoney;			// 清算金额（单位：元）
	String  m_nDealDate;	    // 成交日期
	String  m_nDealTime;		// 成交时间
	String  m_cDealNo;			// 成交编号

	//2007-8-7日增加
	String	m_szORDER_DATE;
	String	m_szTRD_DATE;	//文档未提及
	int		m_nUSER_CODE;
	String	m_szACCOUNT;
	float	m_fORDER_FRZ_AMT;
	//////////////////此字段这文档中未提及//////////////////////////
	String	m_szMATCHED_TIME;
	float	m_fMATCHED_PRICE;
	int		m_nMATCHED_QTY;
	//////////////////////////////////////////////////////////
	float	m_nMATCHED_AMT;
	float	m_nRLT_SETT_AMT;
	
	 public int datalength=199;
	 
	 public DealRecord(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            m_cExchange=(new String(Convertor.cut(org, 0, 1),charset)).trim();
            m_cHolderCode=(new String(Convertor.cut(org, 1, 11),charset)).trim();
            m_cEntrustNo=(new String(Convertor.cut(org, 12, 11),charset)).trim();
            m_cStockCode=(new String(Convertor.cut(org, 23, 7),charset)).trim();
            m_cStockName=(new String(Convertor.cut(org, 30, 9),charset)).trim();
            m_cTransaction=(new String(Convertor.cut(org, 39, 1),charset)).trim();
            m_cTransactionDes=(new String(Convertor.cut(org, 40, 21),charset)).trim();
            m_nDealAmount=pointer.getAsInt(61);
            m_nDealPrice=pointer.getAsDouble(65);
            m_nDealMoney=pointer.getAsDouble(73);
            m_nDealDate=(new String(Convertor.cut(org, 81, 11),charset)).trim();
            m_nDealTime=(new String(Convertor.cut(org, 92, 11),charset)).trim();
            m_cDealNo=(new String(Convertor.cut(org, 103, 21),charset)).trim();
            m_szORDER_DATE=(new String(Convertor.cut(org, 124, 11),charset)).trim();
            m_szTRD_DATE=(new String(Convertor.cut(org, 135, 11),charset)).trim();
            m_nUSER_CODE=pointer.getAsInt(146);
            m_szACCOUNT=(new String(Convertor.cut(org, 150, 20),charset)).trim();
            m_fORDER_FRZ_AMT=pointer.getAsFloat(170);
            //////////此字段这文档中未提及//////////////////////////=
            m_szMATCHED_TIME=(new String(Convertor.cut(org, 174, 9),charset)).trim();
            m_fMATCHED_PRICE=pointer.getAsFloat(183);
            m_nMATCHED_QTY=pointer.getAsInt(187);
            //////////////////////////////////////////////////=
            m_nMATCHED_AMT=pointer.getAsFloat(191);
            m_nRLT_SETT_AMT=pointer.getAsFloat(195);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DealRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
	 }

	public String getM_cExchange() {
		return m_cExchange;
	}

	public String getM_cHolderCode() {
		return m_cHolderCode;
	}

	public String getM_cEntrustNo() {
		return m_cEntrustNo;
	}

	public String getM_cStockCode() {
		return m_cStockCode;
	}

	public String getM_cStockName() {
		return m_cStockName;
	}

	public String getM_cTransaction() {
		return m_cTransaction;
	}

	public String getM_cTransactionDes() {
		return m_cTransactionDes;
	}

	public double getM_nDealAmount() {
		return m_nDealAmount;
	}

	public double getM_nDealPrice() {
		return m_nDealPrice;
	}

	public double getM_nDealMoney() {
		return m_nDealMoney;
	}

	public String getM_nDealDate() {
		return m_nDealDate;
	}

	public String getM_nDealTime() {
		return m_nDealTime;
	}

	public String getM_cDealNo() {
		return m_cDealNo;
	}

	public String getM_szORDER_DATE() {
		return m_szORDER_DATE;
	}

	public String getM_szTRD_DATE() {
		return m_szTRD_DATE;
	}

	public int getM_nUSER_CODE() {
		return m_nUSER_CODE;
	}

	public String getM_szACCOUNT() {
		return m_szACCOUNT;
	}

	public float getM_fORDER_FRZ_AMT() {
		return m_fORDER_FRZ_AMT;
	}

	public String getM_szMATCHED_TIME() {
		return m_szMATCHED_TIME;
	}

	public float getM_fMATCHED_PRICE() {
		return m_fMATCHED_PRICE;
	}

	public int getM_nMATCHED_QTY() {
		return m_nMATCHED_QTY;
	}

	public float getM_nMATCHED_AMT() {
		return m_nMATCHED_AMT;
	}

	public float getM_nRLT_SETT_AMT() {
		return m_nRLT_SETT_AMT;
	}
	 
	 
}
