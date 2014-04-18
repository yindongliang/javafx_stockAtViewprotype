package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class Macd {
    private String stock_cd;
    private BigDecimal dif;
    private BigDecimal dea;
    private BigDecimal sema;
    private BigDecimal lema;
    private BigDecimal bar;
    private String record_date;
    private List<String> orderBy;
    private String order;
    private int limit;

    public Macd() { }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    
    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
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

    public BigDecimal getBar() {
        return bar;
    }

    public void setBar(BigDecimal bar) {
        this.bar = bar;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
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
}
