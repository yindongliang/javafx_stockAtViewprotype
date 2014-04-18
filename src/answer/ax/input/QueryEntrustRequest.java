package answer.ax.input;


import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

public class QueryEntrustRequest {
	
	String 	beginDate;		//开始日期
	String 	endDate;		//终止日期
	String 	getOrdersMode;			//查询委托模式
	int 	UserCode;				//客户代码
	String	Market;			//交易市场
	String 	SecuAccount;	//股东代码
	String	SecuCode;		//证券代码
	String 	TradID;			//交易行为
	String 	BizNo;			//业务序号
	String 	OrderID;		//合同序号
	int 	Branch;					//分支机构
	String 	Account;		//资产账户
	int 	ExtInst;				//外部机构
	Pointer pointer;
	int datalength=104;
	public Pointer getPointer() {
		return pointer;
	}
	
	public QueryEntrustRequest() throws NativeException{
		pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(datalength));
	}
	
	public void setBeginDate(String beginDate) throws NativeException {
		pointer.setStringAt(0, beginDate);
	}
	public void setEndDate(String endDate) throws NativeException {
		pointer.setStringAt(11, endDate);
	}
	public void setGetOrdersMode(String getOrdersMode) throws NativeException {
		pointer.setStringAt(22, getOrdersMode);
	}

	public void setUserCode(int userCode) throws NativeException {
		pointer.setIntAt(23, userCode);
	}

	public void setMarket(String market) throws NativeException {
		pointer.setStringAt(27, market);
	}

	public void setSecuAccount(String secuAccount) throws NativeException {
		pointer.setStringAt(30, secuAccount);
	}

	public void setSecuCode(String secuCode) throws NativeException {
		pointer.setStringAt(41, secuCode);
	}

	public void setTradID(String tradID) throws NativeException {
		pointer.setStringAt(50, tradID);
	}

	public void setBizNo(String bizNo) throws NativeException {
		pointer.setStringAt(53, bizNo);
	}

	public void setOrderID(String orderID) throws NativeException {
		pointer.setStringAt(64, orderID);
	}

	public void setBranch(int branch) throws NativeException {
		pointer.setIntAt(75, branch);
	}

	public void setAccount(String account) throws NativeException {
		pointer.setStringAt(79, account);
	}

	public void setExtInst(int extInst) throws NativeException {
		pointer.setIntAt(100, extInst);
	}
	
}
