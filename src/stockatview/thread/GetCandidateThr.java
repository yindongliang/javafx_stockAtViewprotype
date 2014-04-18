/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.thread;

import answer.logic.StockCandidate;
import java.util.List;
import javafx.scene.control.RadioButton;

/**
 *
 * @author doyin
 */
public class GetCandidateThr extends Thread {

    StockCandidate sc;
    boolean rbtn = false;
    boolean copflg = false;
    List<String> ls_shsz = null;
    int limit_forword=10;
    
    public boolean isCopflg() throws InterruptedException {
        while (this.isAlive()) {
            sleep(1000);
        }

        return copflg;
    }

    public GetCandidateThr(StockCandidate sc, List<String> ls_shsz, boolean rbtn,int limit_forword) {
        this.sc = sc;
        this.ls_shsz = ls_shsz;
        this.rbtn = rbtn;
        this.limit_forword=limit_forword;
        this.start();
       
    }
    

    public void run() {
        if (rbtn) {
            if (sc.getCandidateNorules(ls_shsz).equals("done")) {
                copflg = true;
            }
        } else {
            if (sc.getCandidate("msttoday", limit_forword, "-100",ls_shsz).equals("done")) {
                copflg = true;
            }
        }
    }
}
