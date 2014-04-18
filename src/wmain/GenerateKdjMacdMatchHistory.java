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
public class GenerateKdjMacdMatchHistory {
     public static void main(String[] args) throws InterruptedException {
        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});
        
        // 生成指定日期的kdj+macd数据
        ((Strategy) ac.getBean("Strategy")).kdjMacd(Canlendar.getSystemdate(),false);

    }
}
