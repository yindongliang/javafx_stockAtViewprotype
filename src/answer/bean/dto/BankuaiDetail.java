//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class BankuaiDetail {
    private String bankuai_cd;
    private String record_date;
    private BigDecimal deal_lots;
    private BigDecimal over_line_cnt;
    private BigDecimal stock_number;
    private BigDecimal av_over_cnt;
    
    private List<String> order_by;
    private String order;
    private int limit;

    public BankuaiDetail() { }

    public BigDecimal getAv_over_cnt() {
        return av_over_cnt;
    }

    public void setAv_over_cnt(BigDecimal av_over_cnt) {
        this.av_over_cnt = av_over_cnt;
    }
    
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

    public BigDecimal getDeal_lots() {
        return deal_lots;
    }

    public void setDeal_lots(BigDecimal deal_lots) {
        this.deal_lots = deal_lots;
    }

    public BigDecimal getOver_line_cnt() {
        return over_line_cnt;
    }

    public void setOver_line_cnt(BigDecimal over_line_cnt) {
        this.over_line_cnt = over_line_cnt;
    }

    public BigDecimal getStock_number() {
        return stock_number;
    }

    public void setStock_number(BigDecimal stock_number) {
        this.stock_number = stock_number;
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
