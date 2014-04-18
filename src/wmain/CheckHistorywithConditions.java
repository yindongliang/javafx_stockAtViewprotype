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
public class CheckHistorywithConditions {
     public static void main(String[] args) throws InterruptedException {
        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});
        
        int[] arr =  new int[]{8,12,13,20,21};
        // 特定条件
        ((Strategy) ac.getBean("Strategy")).checkhistorywithconditions(Canlendar.getSystemdate(),false,arr);

    }
}
