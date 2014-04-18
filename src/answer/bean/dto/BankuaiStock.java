//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;

public class BankuaiStock {
    private String stock_cd;
    private String market_cd;
    private String bankuai_cd;

    public String getBankuai_cd() {
        return bankuai_cd;
    }

    public void setBankuai_cd(String bankuai_cd) {
        this.bankuai_cd = bankuai_cd;
    }
    private List<String> order_by;
    private String order;
    private int limit;

    public BankuaiStock() { }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

    public String getMarket_cd() {
        return market_cd;
    }

    public void setMarket_cd(String market_cd) {
        this.market_cd = market_cd;
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
