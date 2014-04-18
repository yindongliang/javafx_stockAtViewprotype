package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoneyChangedRecord {
	String    m_cBusinessDes;  	//业务名称
	String    m_cHolderCode;	//股东代码
	String    m_cCurrency;			//货币代码, 其值应该是枚举CURRENCYCODE中的一个值
	String	m_nDealDate;		//发生日期(YYYYMMDD)
	String	m_nDealTime;		//发生时间(HH:MM:SS)
	double	m_nTransferAmount;		//发生金额(元)
	double	m_nBailBalance;			//本次余额(元)
	double	m_nDealPrice;			//成交价格（单位：元）
	int	m_nDealAmount;			//成交数量（单位：股）
	String	m_cDealNo;		//成交编号
	String    m_cStockCode;		//股票代码
	String	m_cDescription;	//概要:DoAsk应将有关信息处理一下，如：买入 四川长虹 1000 股，20.09 元
	double	m_nSxf;					//手续费
	double	m_nYhs;					//印花税
	double	m_nGhf;					//过户费
	
	public int datalength=158;
	public MoneyChangedRecord(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            m_cBusinessDes=(new String(Convertor.cut(org, 0, 11),charset)).trim();
            m_cHolderCode=(new String(Convertor.cut(org, 11, 11),charset)).trim();
            m_cCurrency=(new String(Convertor.cut(org, 22, 1),charset)).trim();
            m_nDealDate=(new String(Convertor.cut(org, 23, 11),charset)).trim();
            m_nDealTime=(new String(Convertor.cut(org, 34, 11),charset)).trim();
            m_nTransferAmount=pointer.getAsDouble(45);
            m_nBailBalance=pointer.getAsDouble(53);
            m_nDealPrice=pointer.getAsDouble(61);
            m_nDealAmount=pointer.getAsInt(69);
            m_cDealNo=(new String(Convertor.cut(org, 75, 11),charset)).trim();
            m_cStockCode=(new String(Convertor.cut(org, 86, 7),charset)).trim();
            m_cDescription=(new String(Convertor.cut(org, 93, 41),charset)).trim();
            m_nSxf=pointer.getAsDouble(134);
            m_nYhs=pointer.getAsDouble(142);
            m_nGhf=pointer.getAsDouble(150);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MoneyChangedRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
	}
	public String getM_cBusinessDes() {
		return m_cBusinessDes;
	}
	public String getM_cHolderCode() {
		return m_cHolderCode;
	}
	public String getM_cCurrency() {
		return m_cCurrency;
	}
	public String getM_nDealDate() {
		return m_nDealDate;
	}
	public String getM_nDealTime() {
		return m_nDealTime;
	}
	public double getM_nTransferAmount() {
		return m_nTransferAmount;
	}
	public double getM_nBailBalance() {
		return m_nBailBalance;
	}
	public double getM_nDealPrice() {
		return m_nDealPrice;
	}
	public long getM_nDealAmount() {
		return m_nDealAmount;
	}
	public String getM_cDealNo() {
		return m_cDealNo;
	}
	public String getM_cStockCode() {
		return m_cStockCode;
	}
	public String getM_cDescription() {
		return m_cDescription;
	}
	public double getM_nSxf() {
		return m_nSxf;
	}
	public double getM_nYhs() {
		return m_nYhs;
	}
	public double getM_nGhf() {
		return m_nGhf;
	}
	public int getDatalength() {
		return datalength;
	}
	
	
}
