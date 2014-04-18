package answer.ax.input;


import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

public class EntrustRequest {

	String	market;						//交易市场
	String	transactionType;			//交易类型 1 买入 2 卖出	
	String	currency;					// 1 人民币 2  美金  3  港币
	String 	shareHolderCode;	//股东代码
	String 	secuCode;			//证券代码
	float	price;						//价格
	int 	quantity;					//数量
	String	tradeType;					//交易类型
	
	Pointer pointer;
	public Pointer getPointer() {
		return pointer;
	}

	int datalength=80;
	public EntrustRequest() throws NativeException{
		pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(datalength));
		 
	}
	public void setMarket(String market) throws NativeException {
		pointer.setStringAt(0, market);
		
	}
	public void setTransactionType(String transactionType) throws NativeException {
		pointer.setStringAt(1, transactionType);
		
	}
	public void setCurrency(String currency) throws NativeException {
		pointer.setStringAt(2, currency);
		
	}
	public void setShareHolderCode(String shareHolderCode) throws NativeException {
		pointer.setStringAt(3, shareHolderCode);
	}
	public void setSecuCode(String secuCode) throws NativeException {
		pointer.setStringAt(14, secuCode);
		
	}
	public void setPrice(float price) throws NativeException {
		pointer.setFloatAt(23, price);
	}
	public void setQuantity(int quantity) throws NativeException {
		pointer.setIntAt(27, quantity);
	}
	
	public void setTradeType(String tradeType) throws NativeException {
		pointer.setStringAt(31, tradeType);
	}
	
}
