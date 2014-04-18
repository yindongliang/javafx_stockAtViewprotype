package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BalanceRecord {
	
	String	m_cCurrency;			// 货币代码, 其值应该是枚举CURRENCYCODE中的一个值
	double	m_nBailBalance;			// 保证金/银行资金 余额  (元)
	double	m_nBailUsable;			// 保证金/银行资金 可用数(元)
	double	m_nBailMovable;			// 保证金/银行资金 可取数(元)
	double	m_nBailDisable;			// 保证金/银行资金 冻结数(元)

	//-------2007-8-7日加
	double	m_dBailFROZEN;			//异常冻结金额
	double	m_dBailTRD_FRZ;			//交易冻结金额
	double	m_dBailDRAW_AVL_CASH;	//可提现金金额
	double	m_dBailDRAW_AVL_CHEQUE;	//可提支票金额
	double	m_dBailTRANSFER_AVL;	//可转账金额	
	double	m_dBailOUTSTANDING;		//在途资金金额
	double	m_dBailOTD_AVL;			//在途可用金额
	double	m_dBailCR_AMT;			//债权金额
	double	m_dBailDR_AMT;			//负债金额
	double	m_dBailINTEREST;		//利息
	double	m_dBailINT_TAX;			//利息税

        double m_dBailMKT_VAL;
        double m_dBailBB_AMT;
        double m_dBailASSERT_VAL;
	public int datalength=145;
	
	
	public BalanceRecord(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            
            m_cCurrency=(new String(Convertor.cut(org, 0, 1),charset)).trim();
            m_nBailBalance=pointer.getAsDouble(1);
            m_nBailUsable=pointer.getAsDouble(9);
            m_nBailMovable=pointer.getAsDouble(17);
            m_nBailDisable=pointer.getAsDouble(25);

            m_dBailFROZEN=pointer.getAsDouble(33);
            m_dBailTRD_FRZ=pointer.getAsDouble(41);
            m_dBailDRAW_AVL_CASH=pointer.getAsDouble(49);
            m_dBailDRAW_AVL_CHEQUE=pointer.getAsDouble(57);
            m_dBailTRANSFER_AVL=pointer.getAsDouble(65);
            m_dBailOUTSTANDING=pointer.getAsDouble(73);
            m_dBailOTD_AVL=pointer.getAsDouble(81);
            m_dBailCR_AMT=pointer.getAsDouble(89);
            m_dBailDR_AMT=pointer.getAsDouble(97);
            m_dBailINTEREST=pointer.getAsDouble(105);
            m_dBailINT_TAX=pointer.getAsDouble(113);
            
            m_dBailMKT_VAL=pointer.getAsDouble(121);
            m_dBailBB_AMT=pointer.getAsDouble(129);
            m_dBailASSERT_VAL=pointer.getAsDouble(137);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BalanceRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
		 
		 
	}

	public String getM_cCurrency() {
		return m_cCurrency;
	}

	public double getM_nBailBalance() {
		return m_nBailBalance;
	}

	public double getM_nBailUsable() {
		return m_nBailUsable;
	}

	public double getM_nBailMovable() {
		return m_nBailMovable;
	}

	public double getM_nBailDisable() {
		return m_nBailDisable;
	}

	public double getM_dBailFROZEN() {
		return m_dBailFROZEN;
	}

	public double getM_dBailTRD_FRZ() {
		return m_dBailTRD_FRZ;
	}

	public double getM_dBailDRAW_AVL_CASH() {
		return m_dBailDRAW_AVL_CASH;
	}

	public double getM_dBailDRAW_AVL_CHEQUE() {
		return m_dBailDRAW_AVL_CHEQUE;
	}

	public double getM_dBailTRANSFER_AVL() {
		return m_dBailTRANSFER_AVL;
	}

	public double getM_dBailOUTSTANDING() {
		return m_dBailOUTSTANDING;
	}

	public double getM_dBailOTD_AVL() {
		return m_dBailOTD_AVL;
	}

	public double getM_dBailCR_AMT() {
		return m_dBailCR_AMT;
	}

	public double getM_dBailDR_AMT() {
		return m_dBailDR_AMT;
	}

	public double getM_dBailINTEREST() {
		return m_dBailINTEREST;
	}

	public double getM_dBailINT_TAX() {
		return m_dBailINT_TAX;
	}

	public double getM_dBailMKT_VAL() {
		return m_dBailMKT_VAL;
	}

	public double getM_dBailBB_AMT() {
		return m_dBailBB_AMT;
	}

	public double getM_dBailASSERT_VAL() {
		return m_dBailASSERT_VAL;
	}
	
	
}
