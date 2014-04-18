package answer.util;

public class Caculator {
	
	public static double keepRound(double in,int preb){
		
		double precison = Math.pow(0.1, preb);
		double precisondivison = Math.pow(10, preb);
			
		long temp = (long) ((in+precison*0.5)*precisondivison);
		
		return (double)temp/precisondivison;
	}
	
	

}
