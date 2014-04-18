/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.thread;

import answer.logic.StockCandidate;
import answer.logic.Strategy;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.RadioButton;

/**
 *
 * @author doyin
 */
public class StratedgeCheckThr extends Thread {

    Strategy stra;
    String date;
    
    List<String> ls_shsz = null;
   
    
   

    public StratedgeCheckThr(Strategy stra, List<String> ls_shsz,String date) {
        this.stra = stra;
        this.ls_shsz = ls_shsz;
        this.date=date;
       
        this.start();
       
    }
    

    public void run() {
        while(true){
            stra.checkKdj(ls_shsz,  date, true);
            try {
                this.sleep(20*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(StratedgeCheckThr.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
