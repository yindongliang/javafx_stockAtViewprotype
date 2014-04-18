/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.handle;

import answer.bean.dto.Rules;
import answer.logic.CommonLogic;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import answer.logic.StockAnalysis;
import answer.logic.StockCandidate;
import answer.logic.StockSearch;
import org.apache.http.client.ClientProtocolException;
import stockatview.StockATView;
import stockatview.checker.InputChecker;
import stockatview.dialog.CustomizeDialog;
import stockatview.thread.AnaylysisThr;
import stockatview.thread.GetCandidateThr;
import stockatview.util.ComparatorStrA;
import stockatview.util.ViewUtils;

/**
 *
 * @author doyin
 */
public class Analysis implements Initializable {

    @FXML
    private ProgressBar compgr;
    @FXML
    private ProgressIndicator proindic;
    @FXML
    private RadioButton todayfarword;
    @FXML
    private RadioButton todaynot0;
    @FXML
    private TableView resulttb;
    @FXML
    private TableColumn rule_id;
    @FXML
    private TableColumn con_incr_range_from;
    @FXML
    private TableColumn con_incr_range_to;
    @FXML
    private TableColumn con_research_days;
    @FXML
    private TableColumn con_hsrangeammount_accuracy;
    @FXML
    private TableColumn con_ydincrange_accuracy;
    @FXML
    private TableColumn res_up_down;
    @FXML
    private TableColumn res_yd_increase_range;
    @FXML
    private TableColumn res_tot_increase_range;
    @FXML
    private TableColumn hs_apply_ammout;
    @FXML
    private TableColumn td_apply_counts;
    @FXML
    private TableColumn td_apply_count_d;
    @FXML
    private TableColumn totc;
    @FXML
    private TableColumn totc_down;
    @FXML
    private TextField limit_forchart;
    @FXML
    private BarChart rulechart;
    @FXML
    private CategoryAxis incrrange;
    @FXML
    private NumberAxis stockcnt;
    @FXML
    private Label shanghai_history_indices;
    @FXML
    private TextField search_con_amount_incr;
    @FXML
    private Label recordscnt;
    @FXML
    private Label timer;
    private boolean completeflg = false;
    private Thread thr1;
    private Thread thr2;
    private Map<String, String> ischeckok = new HashMap<String, String>();
    private List<AnaylysisThr> ls;

    @FXML
    private void handleDownloadAction(ActionEvent event) {

        thr1 = new Thread() {

            public void run() {
                long startTime = System.currentTimeMillis();;

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
//        thr2 = new Thread() {
//
//            public void run() {
//
//                // pb.incrementProgressBy(1);
//                if (((StockAnalysis) StockATView.ac.getBean("StockAnalysis")).analyzedata().equals("1")) {
//                    completeflg = true;
//                }
//
//            }
//        ;
//        };
         thr2 = new Thread() {

            public void run() {

                CommonLogic cl = (CommonLogic) StockATView.ac.getBean("CommonLogic");
                StockAnalysis sa = (StockAnalysis) StockATView.ac.getBean("StockAnalysis");
                sa.initRules();
                int threadcount = 100;
                int t = cl.getLs_shsz().size() / threadcount;

                boolean flg = todaynot0.isSelected();

                ls = new ArrayList<AnaylysisThr>();
                for (int i = 0; i < threadcount; i++) {
                    if (i == threadcount - 1) {
                        ls.add(new AnaylysisThr(sa, cl.getLs_shsz().subList(i * t, cl.getLs_shsz().size()), flg));
                    } else {
                        ls.add(new AnaylysisThr(sa, cl.getLs_shsz().subList(i * t, (i + 1) * t), flg));
                    }
                }

                for (int j = 0; j < ls.size(); j++) {
                    try {
                        if (j == 0) {
                            completeflg = ls.get(j).isCopflg();
                        } else if (completeflg) {
                            completeflg = completeflg && ls.get(j).isCopflg();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChooseStock.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        };

        thr1.start();
        thr2.start();


    }

    @FXML
    private void handleAbortAction(ActionEvent event) {
        thr1.stop();
        thr2.stop();
        completeflg = false;
        for (AnaylysisThr gthr : ls) {
            gthr.stop();;
        }
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
        if (rule_id.getCellValueFactory() == null) {
            rule_id.setCellValueFactory(new PropertyValueFactory<Rules, String>("rule_id"));
            con_incr_range_from.setCellValueFactory(new PropertyValueFactory<Rules, String>("con_incr_range_from"));
            con_incr_range_to.setCellValueFactory(new PropertyValueFactory<Rules, String>("con_incr_range_to"));
            con_research_days.setCellValueFactory(new PropertyValueFactory<Rules, String>("con_research_days"));
            con_hsrangeammount_accuracy.setCellValueFactory(new PropertyValueFactory<Rules, String>("con_hsrangeammount_accuracy"));
            con_ydincrange_accuracy.setCellValueFactory(new PropertyValueFactory<Rules, String>("con_ydincrange_accuracy"));
            res_up_down.setCellValueFactory(new PropertyValueFactory<Rules, String>("res_up_down"));
            res_yd_increase_range.setCellValueFactory(new PropertyValueFactory<Rules, String>("res_yd_increase_range"));
            res_tot_increase_range.setCellValueFactory(new PropertyValueFactory<Rules, String>("res_tot_increase_range"));
            hs_apply_ammout.setCellValueFactory(new PropertyValueFactory<Rules, String>("hs_apply_ammout"));
            td_apply_counts.setCellValueFactory(new PropertyValueFactory<Rules, String>("td_apply_counts"));
            totc.setCellValueFactory(new PropertyValueFactory<Rules, String>("totc"));
            td_apply_count_d.setCellValueFactory(new PropertyValueFactory<Rules, String>("td_apply_down_counts"));
            totc_down.setCellValueFactory(new PropertyValueFactory<Rules, String>("totc_down"));
        }

        List<Rules> rulesdata;

        Map<String, String> condition = new HashMap<String, String>();

        condition.put("con_tot_increase_range", search_con_amount_incr.getText());
        rulesdata = ((StockSearch) StockATView.ac.getBean("StockSearch")).getRules(condition);

        ObservableList<Rules> data = FXCollections.observableArrayList(rulesdata);

        resulttb.setItems(data);
        recordscnt.setText("共" + rulesdata.size() + "条记录");
        setRuleChart(rulesdata);
    }

    private void setRuleChart(List<Rules> rulesdata) {

        Rules rule = null;

        Map<String, Map<String, Integer>> mpout = new HashMap<>();
        Map<String, Integer> mpin;
        HashSet hs = new HashSet();
        for (int i = 0; i < rulesdata.size(); i++) {

            rule = rulesdata.get(i);
            if (Integer.parseInt(rule.getTotc()) <= 1) {
                continue;
            }
            if (mpout.containsKey(rule.getRes_up_down())) {
                mpin = mpout.get(rule.getRes_up_down());


            } else {
                mpin = new HashMap<String, Integer>();
                mpin.put("totc", Integer.parseInt(rule.getTotc()));
                hs.add(new String[]{rule.getRes_up_down(), rule.getTotc()});
            }
            if (rule.getCon_incr_range_from().doubleValue() == 2.50) {

                if (mpin.containsKey("2.5")) {
                    Integer it = mpin.get("2.5");
                    mpin.put("2.5", it + rule.getTd_apply_counts());

                } else {
                    mpin.put("2.5", rule.getTd_apply_counts());

                }

            } else if (rule.getCon_incr_range_from().doubleValue() == 5) {
                if (mpin.containsKey("5")) {
                    Integer it = mpin.get("5");
                    mpin.put("5", it + rule.getTd_apply_counts());
                } else {
                    mpin.put("5", rule.getTd_apply_counts());
                }

            } else if (rule.getCon_incr_range_from().doubleValue() == 8) {
                if (mpin.containsKey("8")) {
                    Integer it = mpin.get("8");
                    mpin.put("8", it + rule.getTd_apply_counts());
                } else {
                    mpin.put("8", rule.getTd_apply_counts());
                }

            }
            mpout.put(rule.getRes_up_down(), mpin);


        }

        List<String[]> ls = new ArrayList<String[]>();
        ls.addAll(hs);
        ComparatorStrA comparator = new ComparatorStrA();
        Collections.sort(ls, comparator);

        rulechart.setTitle("股票向量分布图");
        incrrange.setLabel("规则向量");
        stockcnt.setLabel("股票数");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("2.5-5%");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("5-8%");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("8%-");
        int limt_show_chart = Integer.parseInt(limit_forchart.getText());
        int k = 0;
        for (int j = 0; j < ls.size(); j++) {
            if (j == limt_show_chart) {
                break;
            }
            Map<String, Integer> m = mpout.get(ls.get(j)[0]);


            int totc = m.get("totc").intValue();
            Map<String, Integer> map2p8 = m;
            int p2p = 0;
            int p5p = 0;
            int p8p = 0;
            if (map2p8.containsKey("2.5")) {
                p2p = map2p8.get("2.5").intValue();

            }
            if (map2p8.containsKey("5")) {
                p5p = map2p8.get("5").intValue();

            }
            if (map2p8.containsKey("8")) {
                p8p = map2p8.get("8").intValue();

            }

            series1.getData().add(new XYChart.Data(ls.get(j)[0] + "\r\ntot:" + totc, p2p));
            series2.getData().add(new XYChart.Data(ls.get(j)[0] + "\r\ntot:" + totc, p5p));
            series3.getData().add(new XYChart.Data(ls.get(j)[0] + "\r\ntot:" + totc, p8p));

        }

        rulechart.getData().clear();
        rulechart.getData().addAll(series1, series2, series3);


    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ToggleGroup group = new ToggleGroup();

        todayfarword.setToggleGroup(group);
        todayfarword.setSelected(true);
        todaynot0.setToggleGroup(group);
        todaynot0.setDisable(true);
        limit_forchart.setText("10");
        double sh_incr = ((StockAnalysis) StockATView.ac.getBean("StockAnalysis")).getShangHaiHistoryIncr();
        shanghai_history_indices.setText(sh_incr + "");

        limit_forchart.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (InputChecker.checkPlusNumber(limit_forchart, false)) {
                    ischeckok.put("limit_forchart", "1");
                } else {
                    ischeckok.put("limit_forchart", "0");
                }
            }
        });

        search_con_amount_incr.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (InputChecker.checkDouble(search_con_amount_incr, false)) {
                    ischeckok.put("search_con_amount_incr", "1");
                } else {
                    ischeckok.put("search_con_amount_incr", "0");
                }
            }
        });
    }
}
