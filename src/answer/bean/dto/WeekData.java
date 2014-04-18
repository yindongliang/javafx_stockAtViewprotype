//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class WeekData {
    private String stock_cd;
    private String record_date;
    private BigDecimal open_price;
    private BigDecimal close_price;
    private BigDecimal deal_lots;
    private BigDecimal high_price;
    private BigDecimal low_price;
    private BigDecimal last_week_price;
    private List<String> order_by;
    private String order;
    private int limit;
    
    public WeekData() { }

    public BigDecimal getLow_price() {
        return low_price;
    }

    public BigDecimal getLast_week_price() {
        return last_week_price;
    }

    public void setLast_week_price(BigDecimal last_week_price) {
        this.last_week_price = last_week_price;
    }
    
    public void setLow_price(BigDecimal low_price) {
        this.low_price = low_price;
    }
    
    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public BigDecimal getOpen_price() {
        return open_price;
    }

    public void setOpen_price(BigDecimal open_price) {
        this.open_price = open_price;
    }

    public BigDecimal getClose_price() {
        return close_price;
    }

    public void setClose_price(BigDecimal close_price) {
        this.close_price = close_price;
    }

    public BigDecimal getDeal_lots() {
        return deal_lots;
    }

    public void setDeal_lots(BigDecimal deal_lots) {
        this.deal_lots = deal_lots;
    }

    public BigDecimal getHigh_price() {
        return high_price;
    }

    public void setHigh_price(BigDecimal high_price) {
        this.high_price = high_price;
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
