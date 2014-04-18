/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wmain;

import answer.logic.Stock2DB;
import answer.logic.Strategy;
import answer.util.Canlendar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author doyin
 */
public class KdjmainBigIntelegenceFile {
     public static void main(String[] args) throws InterruptedException {
        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});
        if (args==null){
            return;
        }
        String[] dateArr = new String[]{
       
            
            "2013-04-09",
            "2013-04-10",
            "2013-04-11",
            "2013-04-12",
            "2013-04-15",
            "2013-04-16",
            "2013-04-17",
            "2013-04-18",
            "2013-04-19",
            "2013-04-22",
            "2013-04-23",
            "2013-04-24",
            "2013-04-25",
            "2013-04-26",
            "2013-05-02",
            "2013-05-03"
            
        };
//        for (int i=0;i<dateArr.length;i++){
            args=new String[]{"2013-05-06","2013-05-06"};
//             args=new String[]{dateArr[i],dateArr[i]};
        // 下载指定区间的数据
        ((Stock2DB) ac.getBean("Stock2DB")).insert2dbFromFile(args[0], args[1],false);
        
        // 下载f10的数据
//        ((Stock2DB) ac.getBean("Stock2DB")).insertf10FromFile();    
        // 截至指定日期30日，10日，5日平均换手率
        ((Stock2DB) ac.getBean("Stock2DB")).lankactivty(Canlendar.getSystemdate());
        // 生成指定日期的kdj数据
        ((Strategy) ac.getBean("Strategy")).setNodeofkdj(Canlendar.getSystemdate(),true);
        // 生成指定日期的macd数据
        ((Strategy) ac.getBean("Strategy")).setNodeOfMacd(Canlendar.getSystemdate(),true);
        // 生成macd周线数据
        ((Strategy) ac.getBean("Strategy")).setNodeOfMacdWeek(true);
         // 生成周线数据
        ((Strategy) ac.getBean("Strategy")).generateWeekData(true);
        // 生成指定日期的kdj+macd数据
//        ((Strategy) ac.getBean("Strategy")).kdjMacd(Canlendar.getSystemdate(),true);
        // 生成指定日期的bankuai数据
        ((Strategy) ac.getBean("Strategy")).generateBankuaiData(args[0],true);
        // 板块数据生成文件
        ((Strategy) ac.getBean("Strategy")).generateBankuaiDataToFile("");
            
//        }
        //
         // 生成macd周线数据
       // ((Strategy) ac.getBean("Strategy")).setNodeOfMacdWeek(false);
         // 生成周线数据
       // ((Strategy) ac.getBean("Strategy")).generateWeekData(false);
    }
}
