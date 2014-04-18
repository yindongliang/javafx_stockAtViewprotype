/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package answer.bean.dto;

import java.math.BigDecimal;

/**
 *
 * @author doyin
 */
public class Kdj {

    private String stock_cd;
    private String record_date;
    private BigDecimal k;
    private BigDecimal d;
    private BigDecimal j;
    private String name;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

    public BigDecimal getD() {
        return d;
    }

    public void setD(BigDecimal d) {
        this.d = d;
    }

    public BigDecimal getJ() {
        return j;
    }

    public void setJ(BigDecimal j) {
        this.j = j;
    }

    public BigDecimal getK() {
        return k;
    }

    public void setK(BigDecimal k) {
        this.k = k;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }
}
