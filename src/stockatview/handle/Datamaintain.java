/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.handle;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import answer.logic.TableMaintain;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import stockatview.StockATView;
import stockatview.dialog.CustomizeDialog;
import stockatview.util.ViewUtils;

/**
 *
 * @author doyin
 */
public class Datamaintain implements Initializable {

    @FXML
    private ProgressBar compgr;
    @FXML
    private ProgressIndicator proindic;
    @FXML
    private ListView allmissionLV;
    @FXML
    private ListView targetmissionLV;
    @FXML
    private PasswordField excutepassword;
    @FXML
    private Label timer;
    private boolean completeflg;
    private Thread thr1;
    private Thread thr2;
    private ObservableList<String> allmissionls = null;
    private ObservableList<String> targetmissionls = null;

    @FXML
    private void handleExcuteAction(ActionEvent event) {
        if (!excutepassword.getText().equals("88888888")) {
            CustomizeDialog.showCustominfo("密码不正确");
            return;
        }
        excutepassword.setText("");
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
                        Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        thr2 = new Thread() {

            public void run() {

                TableMaintain tableMaintain = (TableMaintain) StockATView.ac.getBean("TableMaintain");
                int cnt = 0;

                try {
                    for (String str : targetmissionls) {
                        String[] arr = str.split(":");
                        try {
                            if (str.split(":")[1].equals("")) {
                                Method method = tableMaintain.getClass().getMethod(str.split(":")[0]);
                                if (method.invoke(tableMaintain).equals("done")) {
                                    cnt++;
                                }
                            } else {
                                Method method = tableMaintain.getClass().getMethod(str.split(":")[0], Integer.class);
                                if (method.invoke(tableMaintain, Integer.parseInt(str.split(":")[1])).equals("done")) {
                                    cnt++;
                                }
                            }


                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                }
                // pb.incrementProgressBy(1);
                if (cnt == targetmissionls.size()) {

                    completeflg = true;
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
                        Logger.getLogger(Datamaintain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        ;
        }; 
        thr3.start();

    }

    @FXML
    private void selectOneAction(ActionEvent event) {
        if (allmissionls.size() == 0) {
            return;
        }
        if (allmissionLV.getSelectionModel().selectedItemProperty().get() == null) {
            return;
        }

        String str = allmissionLV.getSelectionModel().selectedItemProperty().get().toString();

        targetmissionls.add(str);
        allmissionLV.getSelectionModel().clearSelection();
        allmissionls.remove(str);



    }

    @FXML
    private void releaseOneAction(ActionEvent event) {
        if (targetmissionls.size() == 0) {
            return;
        }
        if (targetmissionLV.getSelectionModel().selectedItemProperty().get() == null) {
            return;
        }

        String str = targetmissionLV.getSelectionModel().selectedItemProperty().get().toString();

        allmissionls.add(str);
        targetmissionLV.getSelectionModel().clearSelection();
        targetmissionls.remove(str);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> lsall = new ArrayList<String>();
        lsall.add("clearAlldata:30:删除30个交易日前的alldata数据");
        lsall.add("clearRuledata::删除规则数据");
        lsall.add("clearAnalyzeddata:30:删除30个交易日前的分析数据");
        lsall.add("updateStockname::更新股票代码和名称数据");
        List<String> lstarget = new ArrayList<String>();
        allmissionls = FXCollections.observableArrayList(lsall);
        targetmissionls = FXCollections.observableArrayList(lstarget);

        allmissionLV.setItems(allmissionls);
        targetmissionLV.setItems(targetmissionls);


    }
}
