/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.util;

import answer.bean.dto.Alldata;
import java.util.Comparator;

/**
 *
 * @author doyin
 */
public class ComparatorAlldata implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Alldata a1 = (Alldata) o1;
        Alldata a2 = (Alldata) o2;
        return a1.getRecord_date().compareTo(a2.getRecord_date());
    }
}