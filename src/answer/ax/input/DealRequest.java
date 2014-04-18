package answer.ax.input;

import java.io.UnsupportedEncodingException;

import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

public class DealRequest {

	String BeginDate;
	String EndDate;

	Pointer pointer;
	int datalength=22;
	
	public DealRequest() throws NativeException{
		pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(datalength));
		 
	}
	
	public void setBeginDate(String beginDate) throws NativeException {
		pointer.setStringAt(0, beginDate);
	}
	
	public void setEndDate(String EndDate) throws NativeException {
		pointer.setStringAt(11, EndDate);
	}
	
	
}
