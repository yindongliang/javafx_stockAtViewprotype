/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.checker;


import java.text.SimpleDateFormat;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 *
 * @author doyin
 */
public class InputChecker {
    
    public static boolean checkDate(TextField txtf,boolean must){
        
        if (!(!must&&txtf.getText().equals(""))&&!ValidDateStr(txtf.getText())){
            Tooltip tooltip = new Tooltip();
            txtf.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");  
            tooltip.setText("日期必须正确输入，格式如:2012-05-31");
            txtf.setTooltip(tooltip);
            return false;
        } else {
            txtf.setStyle("");
            txtf.setTooltip(null);
        }

        return true;
    }
    
    public static boolean checkStocknumber(TextField txtf,boolean must){
        
       
        if (!(!must&&txtf.getText().equals(""))&&!isstockNumber(txtf.getText())){
            Tooltip tooltip = new Tooltip();
            txtf.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");  
            tooltip.setText("必须正确输入6位股票代码，如:000001");
            txtf.setTooltip(tooltip);
            return false;
        } else {
            txtf.setStyle("");
            txtf.setTooltip(null);
        }

        return true;
    }
    
    public static boolean checkPlusNumber(TextField txtf,boolean must){
        
       
        if (!(!must&&txtf.getText().equals(""))&&!vidateIsPlusInt(txtf.getText())){
            Tooltip tooltip = new Tooltip();
            txtf.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");  
            tooltip.setText("必须输入正整数，如:10");
            txtf.setTooltip(tooltip);
            return false;
        } else {
            txtf.setStyle("");
            txtf.setTooltip(null);
        }

        return true;
    }
    
    public static boolean checkBuymoney(TextField txtf){
        String money =txtf.getText();
       
        if(!vidateIsPlusInt(money)||Integer.parseInt(money)<7000){
            Tooltip tooltip = new Tooltip();
            txtf.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");  
            tooltip.setText("必须输入7000以上正整数");
            txtf.setTooltip(tooltip);
            return false;
        }else {
            txtf.setStyle("");
            txtf.setTooltip(null);
        }
        return true;
        
    }
    
    public static boolean checkDouble(TextField txtf,boolean must){
        
       
        if (!(!must&&txtf.getText().equals(""))&&!vidateIsDouble(txtf.getText())){
            Tooltip tooltip = new Tooltip();
            txtf.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");  
            tooltip.setText("必须输入数字，如:-0.41");
            txtf.setTooltip(tooltip);
            return false;
        } else {
            txtf.setStyle("");
            txtf.setTooltip(null);
        }

        return true;
    }
    
    private static boolean vidateIsPlusInt(String str) { 
        try { 
            long nb = Long.parseLong(str); 
            if (nb<0){
                return false; 
            }
            return true; 

        } catch (Exception e) { 
        
            return false; 
        } 
    }
    private static boolean vidateIsInt(String str) { 
        try { 
            long nb = Long.parseLong(str); 
            if (nb<0){
                return false; 
            }
            return true; 

        } catch (Exception e) { 
        
            return false; 
        } 
    }
    
    private static boolean vidateIsDouble(String str) { 
        try { 
            double nb = Double.parseDouble(str); 
            
            return true; 

        } catch (Exception e) { 
        
            return false; 
        } 
    }
    
    private static boolean ValidDateStr(String rStr) {
  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        formatter.setLenient(false);

        try {

            formatter.format(formatter.parse(rStr));
        } catch (Exception e) {

        return false;
        }
        return true;
    }

     private static boolean isstockNumber(String str) 
    { 
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]{6}"); 
        java.util.regex.Matcher match=pattern.matcher(str); 
        if(match.matches()==false) 
        { 
           return false; 
        } 
        else 
        { 
           return true; 
        } 
    }


     

}
