package wmain;


import answer.bean.PerformanceBean;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Sysinfo {

    public static void main(String[] args) throws IOException {
        PerformanceBean pb = new PerformanceBean();
//      String t=  getClass().getResource("fxmls/main.fxml").getPath();
      
       String url=  pb.getClass().getResource("/").getPath();
       
//       System.out.println(url);
//        Properties pro = System.getProperties();
//        pro.list(System.out);
       
       
        
    }
    
    public static List<String>getList(){
        
        return null;
    }
}
