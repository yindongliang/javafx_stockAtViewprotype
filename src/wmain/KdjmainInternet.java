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
public class KdjmainInternet {
     public static void main(String[] args) throws InterruptedException {
        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});
        // 下载最近营业日的数据
        ((Stock2DB) ac.getBean("Stock2DB")).getAlldata().equals("0");
        
        // 截至指定日期30日，10日，5日平均换手率
        ((Stock2DB) ac.getBean("Stock2DB")).lankactivty(Canlendar.getSystemdate());
        // 生成指定日期的kdj数据
        ((Strategy) ac.getBean("Strategy")).setNodeofkdj(Canlendar.getSystemdate(),true);

    }
}
