/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author doyin
 */
public class CustomizeDialog {
    
    public static void showMessage(){
        Stage dialogStage = new Stage(); 
            
            dialogStage.initModality(Modality.APPLICATION_MODAL); 
//            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text("输入有错，请重新输入！"), 
//                    new Button("Ok.")).alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text("输入有错，请重新输入！")).alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();
    }
    
    public static void showCustominfo(String msg){
        Stage dialogStage = new Stage(); 
        dialogStage.initModality(Modality.APPLICATION_MODAL); 
        dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text(msg)).alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.show();
    }
}
