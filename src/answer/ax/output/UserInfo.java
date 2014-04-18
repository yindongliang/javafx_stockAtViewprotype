package answer.ax.output;


import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserInfo  {
      
	String  m_cHolderCode;	// 股东代码EXCHANGECODE
	String  m_cExchange;			// 交易所标志, 为枚举EXCHANGECODE的组合
	String  m_cCurrency;			// 货币代码,   其值应该是枚举CURRENCYCODE中的其中一个
	int	  m_nUserCode;			// 客户代码	USER_CODE	INT	
	String  m_szUserName;		// 股东姓名	SECU_ACC_NAME	VARCHAR(16)	
	String  m_szDftAcc;		// 缺省资产账户	DFT_ACC	VARCHAR(20)	
	String  m_cMainFlag;			// 主股东标志	MAIN_FLAG	CHAR(1)	
	String  m_szBindSeat;		// 指定席位	BIND_SEAT	CHAR(8)	
	String  m_szBindStatus;	// 指定交易状态	BIND_STATUS	CHAR(8)	
    
	public int datalength=148;

	public UserInfo(Pointer userInfo,String charset) throws NativeException{
        try {
            byte[] org= userInfo.getMemory();
            m_cHolderCode=new String(Convertor.cut(org, 0, 11),charset);
            m_cExchange = new String(Convertor.cut(org, 11, 1),charset);
            m_cCurrency = new String(Convertor.cut(org, 12, 1),charset);
            m_nUserCode= userInfo.getAsInt(13);
            m_szUserName=new String(Convertor.cut(org, 17, 17),charset);
            m_szDftAcc=new String(Convertor.cut(org, 34, 21),charset);
            m_cMainFlag=new String(Convertor.cut(org, 55, 1),charset);
            m_szBindSeat=new String(Convertor.cut(org, 56, 9),charset);
            m_szBindSeat=new String(Convertor.cut(org, 65, 9),charset);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
	}


	public String getM_cHolderCode() {
		return m_cHolderCode.trim();
	}


	public String getM_cExchange() {
		return m_cExchange.trim();
	}


	public String getM_cCurrency() {
		return m_cCurrency.trim();
	}


	public int getM_nUserCode() {
		return m_nUserCode;
	}


	public String getM_szUserName() {
		return m_szUserName.trim();
	}


	public String getM_szDftAcc() {
		return m_szDftAcc.trim();
	}


	public String getM_cMainFlag() {
		return m_cMainFlag.trim();
	}


	public String getM_szBindSeat() {
		return m_szBindSeat.trim();
	}


	public String getM_szBindStatus() {
		return m_szBindStatus.trim();
	}
    
	
        
}
