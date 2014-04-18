//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class KdjMacdMatchHistory {
    private String stock_cd;
    private String record_date;
    private String kdj_type;
    private String macd_type;
    private List<String> order_by;
    private String order;
    private int limit;

    public KdjMacdMatchHistory() { }

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

    public String getKdj_type() {
        return kdj_type;
    }

    public void setKdj_type(String kdj_type) {
        this.kdj_type = kdj_type;
    }

    public String getMacd_type() {
        return macd_type;
    }

    public void setMacd_type(String macd_type) {
        this.macd_type = macd_type;
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
