package answer.util;

public class Convertor {
	
	public static byte[] cut(byte[] org,int start, int len){
		byte[] bytres= new byte[len];
		int j =0;
		
		for (int i=start;i<start+len;i++){
			bytres[j]=org[i];
			j++;
		}
		
		return bytres;
		
	}
	
	

	//字节数组转换整型
    public static int bytes2int(byte[] b)
    {
        int mask=0xff;
        int temp=0;
        int res=0;
        for(int i=3;i>-1;i--){
            res<<=8;
            temp=b[i]&mask;
            res|=temp;
        }
       return res;
    } 
   
    

    //整型转换字节数组
    public static byte[] int2bytes(int num)
    {
           byte[] b=new byte[4];
           
           for(int i=0;i<4;i++){
                b[i]=(byte)(num>>>(24-i*8));
               
           }
          return b;
    }

}
