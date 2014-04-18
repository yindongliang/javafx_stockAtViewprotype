/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.handle;

import answer.bean.PerformanceBean;
import answer.bean.dto.Alldata;
import answer.bean.dto.Analyzedresultdata;
import answer.bean.dto.Chosendata;
import answer.bean.dto.ChosendataPF;
import answer.exception.Axexception;
import answer.exception.Axinfo;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import answer.logic.StockBuyer;
import answer.logic.StockMonitor;
import answer.logic.StockSailer;
import com.sai.javafx.calendar.FXCalendar;
import com.sai.javafx.calendar.FXCalendarUtility;
import java.math.BigDecimal;
import java.util.*;
import javafx.beans.value.ChangeListener;
import org.apache.http.client.ClientProtocolException;
import org.javafxdata.control.cell.CheckBoxCellFactory;
import org.xvolks.jnative.exceptions.NativeException;
import stockatview.StockATView;
import stockatview.checker.InputChecker;
import stockatview.dialog.CustomizeDialog;
import stockatview.util.ViewUtils;

/**
 *
 * @author doyin
 */
public class Performance implements Initializable {

    @FXML
    private Label highest_tot;
    @FXML
    private Label lowest_tot;
    @FXML
    private Label current_tot;
    @FXML
    private Label highest_ave;
    @FXML
    private Label lowest_ave;
    @FXML
    private Label current_ave;
    @FXML
    private Label win_rate;
    @FXML
    private Label draw_rate;
    @FXML
    private Label lost_rate;
    @FXML
    private Label stop_rate;
    @FXML
    private Label watch_status;
    @FXML
    private Label cntmessage;
    @FXML
    private Label shanghai_incr;
    @FXML
    private Label pure_benifit_tot;
    @FXML
    private Label handcharge_tot;
    @FXML
    private TextField watch_interval;
    @FXML
    private TextField lost_stop;
    @FXML
    private TextField win_stop;
    @FXML
    private TableView resulttb;
    @FXML
    private TableColumn buy_price;
    @FXML
    private TableColumn buy_date;
    @FXML
    private TableColumn current_price;
    @FXML
    private TableColumn pure_benifit;
    @FXML
    private TableColumn stock_cd;
    @FXML
    private TableColumn buy_lots;
    @FXML
    private TableColumn chosensail;
    @FXML
    private TableColumn incr_range;
    @FXML
    private TableColumn status;
    @FXML
    private TableColumn stockname;
    private boolean completeflg;
    private Thread thr1;
    private Thread thr2;
    private ObservableList<Chosendata> dataFortable;
    @FXML
    private LineChart increaseChart;
    @FXML
    private CategoryAxis time;
    @FXML
    private NumberAxis range;
    private XYChart.Series seriesAve = new XYChart.Series();
    private XYChart.Series seriesShangHai;
    @FXML
    private FXCalendar chartviewdate;
    @FXML
    private Button startbtn;
    @FXML
    private Button stopbtn;
    private Map<String, String> ischeckok = new HashMap<String, String>();

    @FXML
    private void handleWatchStartAction(ActionEvent event) {

        if (ischeckok.containsValue("0")) {
            CustomizeDialog.showMessage();
            return;
        }

        startbtn.setDisable(true);
        stopbtn.setDisable(false);
        watch_status.setText("监视中...");

        if (stock_cd.getCellValueFactory() == null) {
            stock_cd.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("stock_cd"));
            pure_benifit.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("pure_benifit"));
            current_price.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("current_price"));
            buy_date.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("buy_date"));
            buy_price.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("buy_price"));
            buy_lots.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("buy_lots"));
            chosensail.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Chosendata, Boolean>, ObservableValue<Boolean>>() {

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Chosendata, Boolean> cdf) {
                    return cdf.getValue().telecommuterProperty();
                }
            });
            chosensail.setCellFactory(CheckBoxCellFactory.forTableColumn(chosensail));
            incr_range.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("incr_range_compare_bought"));
            status.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("status"));
            stockname.setCellValueFactory(new PropertyValueFactory<Chosendata, String>("name"));
        }
        try {
            seriesAve.getData().clear();
        } catch (IndexOutOfBoundsException e) {
        }
        // for initial data
        thr1 = new Thread() {

            public void run() {
                long startTime = System.currentTimeMillis();
                List<Chosendata> chosendatalist = null;
                Map<String, Object> resultmp = null;
                if (resulttb.getItems().size() == 0) {

                    resultmp = ((StockMonitor) StockATView.ac.getBean("StockMonitor")).getSellableStocks();
                    chosendatalist = (List<Chosendata>) resultmp.get("chosendatalist");

                    dataFortable = FXCollections.observableArrayList(chosendatalist);

                }
                if (resultmp != null) {
                    Platform.runLater(new UpdateOverviewThread((PerformanceBean) resultmp.get("pfbean"), false));

                }

                final List<ChosendataPF> chosendataPFlist = ((StockMonitor) StockATView.ac.getBean("StockMonitor")).getSingleStockPerform("si000009", chartviewdate.getTextField().getText());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        resulttb.setItems(dataFortable);
                        cntmessage.setText("共计：" + dataFortable.size() + "只");
                        for (ChosendataPF averagePF : chosendataPFlist) {
                            seriesAve.getData().add(new XYChart.Data(averagePF.getRecord_time(), averagePF.getIncr_range_compare_bought().doubleValue()));
                        }

                    }
                });


            }
        };


        thr1.start();
        // for updating data       
        thr2 = new Thread() {

            public void run() {
                // chartline图设置
                int interval = Integer.parseInt(watch_interval.getText());

                while (true) {

                    try {
                        Thread.sleep(interval * 1000 * 60);
                        if (!ViewUtils.isOpening(dataFortable)) {
                            continue;
                        }


                        PerformanceBean pfbean = null;
                        try {
                            pfbean = ((StockMonitor) StockATView.ac.getBean("StockMonitor")).getoverviewPFAndUpdatePF(dataFortable,Double.parseDouble(win_stop.getText()),Double.parseDouble(lost_stop.getText()));
                        } catch (NativeException ex) {
                            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Axexception ex) {
                            CustomizeDialog.showCustominfo(ex.getMsg());
                            Logger.getLogger(ChooseStock.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Axinfo ex) {
                            Map<String, String> mp = ex.getMapinfo();
                            Set<String> keys = mp.keySet();
                            StringBuilder sb = new StringBuilder();
                            for (String key : keys) {
                                sb.append(key + " : " + mp.get(key) + "\r\n");
                            }
                            CustomizeDialog.showCustominfo(sb.toString());
                            Logger.getLogger(ChooseStock.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Platform.runLater(new UpdateOverviewThread(pfbean, true));
                        try {
                            ((StockSailer) StockATView.ac.getBean("StockSailer")).checkEntrustStatus(dataFortable);
                        } catch (NativeException ex) {
                            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Axexception ex) {
                            CustomizeDialog.showCustominfo(ex.getMsg());
                            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
                        }



                    } catch (InterruptedException ex) {
                        Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        };

        thr2.start();


    }

    @FXML
    private void handleAbortAction(ActionEvent event) {
        thr1.stop();
        thr2.stop();
        watch_status.setText("监视停止");
        startbtn.setDisable(false);
        stopbtn.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        startbtn.setDisable(false);
        stopbtn.setDisable(true);
        watch_interval.setText("1");
        watch_interval.setDisable(true);
        increaseChart.setTitle("平均涨幅追踪图");
        time.setLabel("时间");

        seriesAve.setName("平均每股涨幅");
        increaseChart.getData().add(seriesAve);
        resulttb.setEditable(true);
        String fmt = "yyyy-MM-dd";
        String date = ViewUtils.dateFormater(fmt);

//        chartviewdate.setShowWeekNumber(true);
        chartviewdate.setValue(new FXCalendarUtility().convertStringtoDate(date));
//        chartviewdate.setText(ViewUtils.dateFormater(fmt));
        lost_stop.setText("-1.5");
        win_stop.setText("9.9");
        lost_stop.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (InputChecker.checkDouble(lost_stop, true)) {
                    ischeckok.put("lost_stop", "1");
                } else {
                    ischeckok.put("lost_stop", "0");
                }
            }
        });
        win_stop.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (InputChecker.checkDouble(win_stop, true)) {
                    ischeckok.put("win_stop", "1");
                } else {
                    ischeckok.put("win_stop", "0");
                }
            }
        });
    }
    private boolean isallselected = false;

    @FXML
    private void handleSelectAllAction(ActionEvent event) {
        ObservableList<Chosendata> datals = resulttb.getItems();
        if (!isallselected) {
            for (Chosendata ard : datals) {
                ard.setTelecommuter(true);
                isallselected = true;

            }
        } else {
            for (Chosendata ard : datals) {
                ard.setTelecommuter(false);
                isallselected = false;

            }
        }


    }

    @FXML
    private void handleSellSelectedStockction(ActionEvent event) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                try {

                    ((StockSailer) StockATView.ac.getBean("StockSailer")).sell(dataFortable);
                } catch (NativeException ex) {
                    Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Axexception ex) {
                    CustomizeDialog.showCustominfo(ex.getMsg());
                    Logger.getLogger(ChooseStock.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Axinfo ex) {
                    Map<String, String> mp = ex.getMapinfo();
                    Set<String> keys = mp.keySet();
                    StringBuilder sb = new StringBuilder();
                    for (String key : keys) {
                        sb.append(key + " : " + mp.get(key) + "\r\n");
                    }
                    CustomizeDialog.showCustominfo(sb.toString());
                    Logger.getLogger(ChooseStock.class.getName()).log(Level.SEVERE, null, ex);
                }

                cntmessage.setText("共计：" + dataFortable.size() + "只");
            }
        });


    }

    class UpdateOverviewThread extends Thread {

        private PerformanceBean pfbean;
        private boolean chartflg;

        UpdateOverviewThread(PerformanceBean pfbean, boolean chartflg) {
            this.pfbean = pfbean;
            this.chartflg = chartflg;
        }

        @Override
        public void run() {
            // labels update
            highest_tot.setText(pfbean.getHighest_tot() + "%");
            lowest_tot.setText(pfbean.getLowest_tot() + "%");
            current_tot.setText(pfbean.getCurrent_tot() + "%");
            highest_ave.setText(pfbean.getHighest_ave() + "%");
            lowest_ave.setText(pfbean.getLowest_ave() + "%");
            current_ave.setText(pfbean.getCurrent_ave() + "%");
            win_rate.setText(pfbean.getWin_rate() + "%");
            draw_rate.setText(pfbean.getDraw_rate() + "%");
            lost_rate.setText(pfbean.getLost_rate() + "%");
            stop_rate.setText(pfbean.getStop_rate() + "%");
            shanghai_incr.setText(pfbean.getShanghai_incr() + "%");
            pure_benifit_tot.setText(pfbean.getPurebenifit_tot() + "元");
            handcharge_tot.setText(pfbean.getHandlecharge_tot() + "元");
            if (chartflg) {
                ViewUtils.dateFormater("HH:mm:ss");
                seriesAve.getData().add(new XYChart.Data(ViewUtils.dateFormater("HH:mm:ss"), pfbean.getCurrent_ave()));
            }

        }
    }
}
