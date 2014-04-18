//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class MacdBankuai {
    private String bankuai_cd;
    private String record_date;
    private BigDecimal dif;
    private BigDecimal dea;
    private BigDecimal sema;
    private BigDecimal lema;
    private List<String> order_by;
    private String order;
    private int limit;

    public MacdBankuai() { }

    public String getBankuai_cd() {
        return bankuai_cd;
    }

    public void setBankuai_cd(String bankuai_cd) {
        this.bankuai_cd = bankuai_cd;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public BigDecimal getDif() {
        return dif;
    }

    public void setDif(BigDecimal dif) {
        this.dif = dif;
    }

    public BigDecimal getDea() {
        return dea;
    }

    public void setDea(BigDecimal dea) {
        this.dea = dea;
    }

    public BigDecimal getSema() {
        return sema;
    }

    public void setSema(BigDecimal sema) {
        this.sema = sema;
    }

    public BigDecimal getLema() {
        return lema;
    }

    public void setLema(BigDecimal lema) {
        this.lema = lema;
    }

    public List<String> getOrder_by() {
        return order_by;
    }

    public void setOrder_by(List<String> order_by) {
        this.order_by = order_by;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
//////////AUTOGEN END////////////
}
