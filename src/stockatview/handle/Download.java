/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.handle;

import answer.bean.dto.Alldata;
import com.sai.javafx.calendar.FXCalendar;
import com.sai.javafx.calendar.FXCalendarUtility;



import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import answer.logic.Stock2DB;
import answer.logic.StockSearch;
import org.apache.http.client.ClientProtocolException;
import stockatview.StockATView;
import stockatview.checker.InputChecker;
import stockatview.dialog.CustomizeDialog;
import stockatview.util.ViewUtils;

/**
 *
 * @author doyin
 */
public class Download implements Initializable {

    @FXML
    private ProgressBar compgr;
    @FXML
    private ProgressIndicator proindic;
    @FXML
    private TableView resulttb;
    @FXML
    private TableColumn present_price;
    @FXML
    private TableColumn record_date;
    @FXML
    private TableColumn record_time;
    @FXML
    private TableColumn td_open_price;
    @FXML
    private TableColumn yt_close_price;
    @FXML
    private TableColumn stock_cd;
    @FXML
    private TableColumn incr_range;
    @FXML
    private TextField stockcd;
//    @FXML
//    private TextField startdate;
    @FXML
    private FXCalendar startdate;
    @FXML
    private FXCalendar enddate;
    @FXML
    private Label recordscnt;
    @FXML
    private Label timer;
    @FXML
    private FXCalendar startdatec;
    private boolean completeflg = false;
    private Thread thr1;
    private Thread thr2;
    private Map<String, String> ischeckok = new HashMap<String, String>();

    @FXML
    private void handleDownloadAction(ActionEvent event) {

        thr1 = new Thread() {

            public void run() {
                long startTime = System.currentTimeMillis();
                while (compgr.getProgress() < 1) {
                    final String time = ViewUtils.timer(startTime);
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            timer.setText("用时:" + time);
                        }
                    });
                    try {
                        sleep(1000);
                        if ((!completeflg && compgr.getProgress() <= 0.5F) || completeflg) {
                            compgr.setProgress(compgr.getProgress() + 0.01);
                            proindic.setProgress(proindic.getProgress() + 0.01);
                        }
                        if (completeflg) {
                            compgr.setProgress(1);
                            proindic.setProgress(1);
                            break;
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        ;
        };   
        thr2 = new Thread() {

            public void run() {


                // pb.incrementProgressBy(1);
                if (((Stock2DB) StockATView.ac.getBean("Stock2DB")).getAlldata().equals("0")) {
                    completeflg = true;
                }

            }
        ;
        };
        thr1.start();
        thr2.start();

    }

    @FXML
    private void handleAbortAction(ActionEvent event) {
        thr1.stop();
        thr2.stop();
        Thread thr3 = new Thread() {

            public void run() {

                while (compgr.getProgress() > 0) {
                    try {
                        sleep(30);
                        if (compgr.getProgress() - 0.01 > 0) {
                            compgr.setProgress(compgr.getProgress() - 0.01);
                            proindic.setProgress(proindic.getProgress() - 0.01);
                        } else {
                            compgr.setProgress(0);
                            proindic.setProgress(0);
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        ;
        }; 
        thr3.start();

    }

    @FXML
    private void handleSearchAction(ActionEvent event) {

        if (ischeckok.containsValue("0")) {
            CustomizeDialog.showMessage();
            return;
        }

        if (present_price.getCellValueFactory() == null) {
            present_price.setCellValueFactory(new PropertyValueFactory<Alldata, String>("present_price"));
            record_date.setCellValueFactory(new PropertyValueFactory<Alldata, String>("record_date"));
            record_time.setCellValueFactory(new PropertyValueFactory<Alldata, String>("record_time"));
            td_open_price.setCellValueFactory(new PropertyValueFactory<Alldata, String>("td_open_price"));
            yt_close_price.setCellValueFactory(new PropertyValueFactory<Alldata, String>("yt_close_price"));
            yt_close_price.setCellValueFactory(new PropertyValueFactory<Alldata, String>("yt_close_price"));
            stock_cd.setCellValueFactory(new PropertyValueFactory<Alldata, String>("stock_cd"));
            incr_range.setCellValueFactory(new PropertyValueFactory<Alldata, String>("incr_range"));
        }

        Map<String, String> mp = new HashMap();

        mp.put("stock_cd", stockcd.getText());
        mp.put("record_date_from", startdate.getTextField().getText());
        mp.put("record_date_to", enddate.getTextField().getText());
        List<Alldata> adl = ((StockSearch) StockATView.ac.getBean("StockSearch")).getdatas(mp);


        ObservableList<Alldata> data = FXCollections.observableArrayList(adl);

        resulttb.setItems(data);
        recordscnt.setText("共" + adl.size() + "条记录");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String fmt = "yyyy-MM-dd";
        String date = ViewUtils.dateFormater(fmt);

        enddate.setShowWeekNumber(true);
        enddate.setValue(new FXCalendarUtility().convertStringtoDate(date));


        startdate.setShowWeekNumber(true);
        startdate.setValue(new FXCalendarUtility().convertStringtoDate(date));

        stockcd.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (InputChecker.checkStocknumber(stockcd, false)) {
                    ischeckok.put("stockcd", "1");
                } else {
                    ischeckok.put("stockcd", "0");
                }
            }
        });
//        enddate.focusedProperty().addListener(new ChangeListener<Boolean>(){
//
//            @Override
//            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
//                if ( InputChecker.checkDate(enddate, false)){
//                    ischeckok.put("enddate", "1");
//                }else{
//                    ischeckok.put("enddate", "0");
//                }
//            }
//
//           
//        });


    }
}
