package wmain;

import answer.logic.Stock2DB;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import answer.logic.Stock2DBbk;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Stock2DBMain {

    /**
     * @param args
     * @throws IOException
     * @throws ClientProtocolException
     * @throws BeansException
     * @throws InterruptedException
     * @throws SQLException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub

        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});

        while (true) {


            if (((Stock2DB) ac.getBean("Stock2DB")).getAlldata().equals("0")) {
                break;
            }


        }

    }
}
