package answer.logic.helper;

import answer.ax.output.UserInfo;

public class  MemoCach {

	private static UserInfo usf= null;
	
	
	
	public static UserInfo getUsf() {
		return usf;
	}
	
	public static void setUsf(UserInfo UserInfo) {
		usf = UserInfo;
	}
	
}
