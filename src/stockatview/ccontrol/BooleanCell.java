/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.ccontrol;

import answer.bean.dto.Analyzedresultdata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 *
 * @author doyin
 */
public class BooleanCell extends TableCell<Analyzedresultdata, Boolean> {         
    private CheckBox checkBox; 
    final BooleanCell bc = this;
     
    public BooleanCell(final TableColumn col) { 
        checkBox = new CheckBox();             
        checkBox.setDisable(false); 
        System.out.println(bc.getIndex());
        
//        checkBox.selectedProperty().bind(col.getCellObservableValue(bc.getIndex()));
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean> () {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {   
//                if(isEditing()) commitEdit(newValue == null ? false : newValue);  
//                if(checkBox.isSelected()){
//                   ((BooleanProperty)col.getCellObservableValue(bc.getIndex())).set(true);
////                   checkBox.selectedProperty().bind(col.getCellObservableValue(bc.getIndex()));
//                }else{
//                    ((BooleanProperty)col.getCellObservableValue(bc.getIndex())).set(false);
//                }
            }             
        }); 
        
        this.setGraphic(checkBox); 
        this.setAlignment(Pos.CENTER);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);   
        this.setEditable(true);         
    }         
//    @Override        
//    public void startEdit() {    
//        super.startEdit();      
//        if (isEmpty()) {    
//            return;       
//        }           
//        checkBox.setDisable(false);  
//        checkBox.requestFocus();  
//    }        
//    @Override     
//    public void cancelEdit() {   
//        super.cancelEdit();   
//        checkBox.setDisable(true);  
//    }       
//    @Override
//    public void commitEdit(Boolean value) {    
//        super.commitEdit(value);  
//        checkBox.setDisable(true);     
//    }        
//    @Override     
//    public void updateItem(Boolean item, boolean empty) {     
//        super.updateItem(item, empty);       
//        if (!isEmpty()) {        
//            checkBox.setSelected(item);    
//        }      
//    }   
} 