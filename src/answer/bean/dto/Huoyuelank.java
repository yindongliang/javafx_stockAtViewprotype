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
public class Huoyuelank {

    private String stock_cd;

    private BigDecimal fivedayturnover;
    private BigDecimal tendayturnover;
    private BigDecimal thirtydayturnover;

    public BigDecimal getFivedayturnover() {
        return fivedayturnover;
    }

    public void setFivedayturnover(BigDecimal fivedayturnover) {
        this.fivedayturnover = fivedayturnover;
    }

   
    public BigDecimal getTendayturnover() {
        return tendayturnover;
    }

    public void setTendayturnover(BigDecimal tendayturnover) {
        this.tendayturnover = tendayturnover;
    }

    public BigDecimal getThirtydayturnover() {
        return thirtydayturnover;
    }

    public void setThirtydayturnover(BigDecimal thirtydayturnover) {
        this.thirtydayturnover = thirtydayturnover;
    }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }
}
