/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.util;

import answer.bean.dto.Rules;
import java.util.Comparator;

/**
 *
 * @author doyin
 */
public class ComparatorStrA implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        String[] rule1=(String[]) o1;
        String[] rule2=(String[]) o2;
        int flag=Integer.parseInt(rule2[1])-Integer.parseInt(rule1[1]);

        return flag;
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
