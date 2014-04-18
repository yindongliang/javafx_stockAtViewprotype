//////////AUTOGEN START//////////
package answer.bean.dto;

import java.util.List;

public class Bankuai {
    private String bankuai_cd;
    private String bankuai_name;

    public String getBankuai_name() {
        return bankuai_name;
    }

    public void setBankuai_name(String bankuai_name) {
        this.bankuai_name = bankuai_name;
    }
    
    private List<String> order_by;
    private String order;
    private int limit;

    public Bankuai() { }

    public String getBankuai_cd() {
        return bankuai_cd;
    }

    public void setBankuai_cd(String bankuai_cd) {
        this.bankuai_cd = bankuai_cd;
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
