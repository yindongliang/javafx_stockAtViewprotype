package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntrustRecord {
	
	String  m_cExchange;				// 交易所
	String  m_cHolderCode;		// 股东代码
	String  m_cStockCode;		// 股票代码
	String  m_cStockName;		// 股票名称
	String  m_cTransaction;			// 交易类型, 为枚举TRANSACTIONTYPE中的一个值
	String  m_cTransactionDes;	// 交易类型文字说明
	int  m_nEntrustAmount;		// 委托数量(股)
	int  m_nDealAmount;			// 成交数量(股)
	int  m_nCancelAmount;			// 撤单成交数量(股)
	double	m_nEntrustPrice;		// 委托价格(元)
	double	m_nDealPrice;			// 成交价格(元)
	String	m_cEntrustNo;		// 委托序号
	String	m_nEntrustDate;	// 委托日期(YYYYMMDD)
	String	m_nEntrustTime;	// 委托时间(HH:MM:SS)
	String	m_nDealTime;		// 成交时间(HH:MM:SS)
	String  m_cStatus;				// 状态描述
	String  m_cStatusDes;		// 状态描述文字说明
	
	String	m_szACCOUNT;

	// 2007-8-23日增加
	int		m_nBRANCH;
	String	m_szSECU_NAME;
	double  m_fORDER_AMT;
	double	m_fORDER_FRZ_AMT;
	String	m_cIS_WITHDRAW;
	String	m_cIS_WITHDRAWN;
	String	m_cCAN_WITHDRAW;
	String	m_cDCL_FLAG;
	String	m_cVALID_FLAG;
	double	m_fMATCHED_AMT;

	//以下字段在文档中未提及
	int		m_nEXT_INST;
	int		m_nEXT_REC_NUM;
	String	m_szEXT_BIZ_NO;
	String	m_szEXT_ORDER_ID;
	String	m_szOP_REMARK;
	
	public int datalength=299;
	 
	public EntrustRecord(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            m_cExchange=(new String(Convertor.cut(org, 0, 1),charset)).trim();
            m_cHolderCode=(new String(Convertor.cut(org, 1, 11),charset)).trim();
            m_cStockCode=(new String(Convertor.cut(org, 12, 7),charset)).trim();
            m_cStockName=(new String(Convertor.cut(org, 19, 9),charset)).trim();
            m_cTransaction=(new String(Convertor.cut(org, 28, 1),charset)).trim();
            m_cTransactionDes=(new String(Convertor.cut(org, 29, 21),charset)).trim();
            m_nEntrustAmount=pointer.getAsInt(50);
            m_nDealAmount=pointer.getAsInt(54);
            m_nCancelAmount=pointer.getAsInt(58);
            m_nEntrustPrice=pointer.getAsDouble(62);
            m_nDealPrice=pointer.getAsDouble(70);
            m_cEntrustNo=(new String(Convertor.cut(org, 78, 11),charset)).trim();
            m_nEntrustDate=(new String(Convertor.cut(org, 89, 11),charset)).trim();
            m_nEntrustTime=(new String(Convertor.cut(org, 100, 11),charset)).trim();
            m_nDealTime=(new String(Convertor.cut(org, 111, 11),charset)).trim();
            m_cStatus=(new String(Convertor.cut(org, 122, 1),charset)).trim();
            m_cStatusDes=(new String(Convertor.cut(org, 123, 11),charset)).trim();

            m_szACCOUNT=(new String(Convertor.cut(org, 134, 20),charset)).trim();


            m_nBRANCH=pointer.getAsInt(154);
            m_szSECU_NAME=(new String(Convertor.cut(org, 158, 17),charset)).trim();
            m_fORDER_AMT=pointer.getAsDouble(175);
            m_fORDER_FRZ_AMT=pointer.getAsDouble(183);
            m_cIS_WITHDRAW=(new String(Convertor.cut(org, 191, 1),charset)).trim();
            m_cIS_WITHDRAWN=(new String(Convertor.cut(org, 192, 1),charset)).trim();
            m_cCAN_WITHDRAW=(new String(Convertor.cut(org, 193, 1),charset)).trim();
            m_cDCL_FLAG=(new String(Convertor.cut(org, 194, 1),charset)).trim();
            m_cVALID_FLAG=(new String(Convertor.cut(org, 195, 1),charset)).trim();
            m_fMATCHED_AMT=pointer.getAsDouble(196);


            m_nEXT_INST=pointer.getAsInt(204);
            m_nEXT_REC_NUM=pointer.getAsInt(208);
            m_szEXT_BIZ_NO=(new String(Convertor.cut(org, 212, 11),charset)).trim();
            m_szEXT_ORDER_ID=(new String(Convertor.cut(org, 223, 11),charset)).trim();
            m_szOP_REMARK=(new String(Convertor.cut(org, 234, 65),charset)).trim();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EntrustRecord.class.getName()).log(Level.SEVERE, null, ex);
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

	public String getM_cTransaction() {
		return m_cTransaction;
	}

	public String getM_cTransactionDes() {
		return m_cTransactionDes;
	}

	public long getM_nEntrustAmount() {
		return m_nEntrustAmount;
	}

	public long getM_nDealAmount() {
		return m_nDealAmount;
	}

	public long getM_nCancelAmount() {
		return m_nCancelAmount;
	}

	public double getM_nEntrustPrice() {
		return m_nEntrustPrice;
	}

	public double getM_nDealPrice() {
		return m_nDealPrice;
	}

	public String getM_cEntrustNo() {
		return m_cEntrustNo;
	}

	public String getM_nEntrustDate() {
		return m_nEntrustDate;
	}

	public String getM_nEntrustTime() {
		return m_nEntrustTime;
	}

	public String getM_nDealTime() {
		return m_nDealTime;
	}

	public String getM_cStatus() {
		return m_cStatus;
	}

	public String getM_cStatusDes() {
		return m_cStatusDes;
	}

	public String getM_szACCOUNT() {
		return m_szACCOUNT;
	}

	public int getM_nBRANCH() {
		return m_nBRANCH;
	}

	public String getM_szSECU_NAME() {
		return m_szSECU_NAME;
	}

	public double getM_fORDER_AMT() {
		return m_fORDER_AMT;
	}

	public double getM_fORDER_FRZ_AMT() {
		return m_fORDER_FRZ_AMT;
	}

	public String getM_cIS_WITHDRAW() {
		return m_cIS_WITHDRAW;
	}

	public String getM_cIS_WITHDRAWN() {
		return m_cIS_WITHDRAWN;
	}

	public String getM_cCAN_WITHDRAW() {
		return m_cCAN_WITHDRAW;
	}

	public String getM_cDCL_FLAG() {
		return m_cDCL_FLAG;
	}

	public String getM_cVALID_FLAG() {
		return m_cVALID_FLAG;
	}

	public double getM_fMATCHED_AMT() {
		return m_fMATCHED_AMT;
	}

	public int getM_nEXT_INST() {
		return m_nEXT_INST;
	}

	public int getM_nEXT_REC_NUM() {
		return m_nEXT_REC_NUM;
	}

	public String getM_szEXT_BIZ_NO() {
		return m_szEXT_BIZ_NO;
	}

	public String getM_szEXT_ORDER_ID() {
		return m_szEXT_ORDER_ID;
	}

	public String getM_szOP_REMARK() {
		return m_szOP_REMARK;
	}

	public int getDatalength() {
		return datalength;
	}
	
	
}
