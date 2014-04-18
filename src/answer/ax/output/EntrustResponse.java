package answer.ax.output;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import answer.util.Convertor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntrustResponse {

	String 	holderCode;		//股东卡号
	String    stockCode;      //证券代码
	double 	price;					//价格
	int 	quantity;				//数量
	String    entrustNo;      //委托号
    byte    buySell;                //买卖标记   0:买   1：卖
    public int datalength=100;
    
    public EntrustResponse(Pointer pointer,String charset) throws NativeException{
        try {
            byte[] org= pointer.getMemory();
            holderCode=(new String(Convertor.cut(org, 0, 11),charset)).trim();
            stockCode = (new String(Convertor.cut(org, 11, 11),charset)).trim();
            
            price= pointer.getAsDouble(22);
            quantity=pointer.getAsInt(30);
            entrustNo=(new String(Convertor.cut(org, 34, 11),charset)).trim();
            buySell=pointer.getAsByte(45);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EntrustResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
	}


	public String getHolderCode() {
		return holderCode;
	}


	public String getStockCode() {
		return stockCode;
	}


	public double getPrice() {
		return price;
	}


	public int getQuantity() {
		return quantity;
	}


	public String getEntrustNo() {
		return entrustNo;
	}


	public byte getBuySell() {
		return buySell;
	}
    
}
