/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.thread;

import answer.logic.StockAnalysis;
import java.util.List;

/**
 *
 * @author doyin
 */
public class AnaylysisThr extends Thread {

    StockAnalysis sc;
    boolean rbtn = false;
    boolean copflg = false;
    List<String> ls_shsz = null;

    public boolean isCopflg() throws InterruptedException {
        while (this.isAlive()) {
            sleep(1000);
        }

        return copflg;
    }

    public AnaylysisThr(StockAnalysis sc, List<String> ls_shsz, boolean rbtn) {
        this.sc = sc;
        this.ls_shsz = ls_shsz;
        this.rbtn = rbtn;
        this.start();

    }

    public void run() {

        if (sc.analyzedata(ls_shsz).equals("done")) {
            copflg = true;
        }

    }
}
