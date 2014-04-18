/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview;


import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import stockatview.handle.Download;
import stockatview.util.ViewUtils;

/**
 *
 * @author doyin
 */
public class StockATView extends Application {
    
    public static ApplicationContext ac;
    public static HostServices hs;

    public static void main(String[] args) {
         
        Application.launch(StockATView.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
         
        ac = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml", "dataAccessContext-local.xml"});
       
        hs = getHostServices();
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/main.fxml"));
        
        Scene sc = new Scene(root);
//        sc.getStylesheets().add("stockatview/styles.css");
        sc.getStylesheets().add(StockATView.class.getResource("/com/sai/javafx/calendar/styles/calendar_styles.css").toExternalForm());
        stage.setScene(sc);
        stage.show();
        
    }
    @Override
     public void stop() throws Exception {
        System.exit(0);
    }
     
    
}
