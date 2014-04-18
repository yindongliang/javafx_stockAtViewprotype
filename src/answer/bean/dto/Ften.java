package answer.bean.dto;

import java.util.List;
import java.math.BigDecimal;

public class Ften {
    private String stock_cd;
    private String stock_name;

    private BigDecimal meigusy;
    private BigDecimal meigujzc;
    private BigDecimal meigugjj;
    private BigDecimal meiguwfplr;
    
    private BigDecimal liutonggu;
    private BigDecimal tongbiincr;
    private BigDecimal jingzcbenifitrate;
    private BigDecimal  liutonggudongchigubili;
    private String suosuhangye;
    private List<String> orderBy;
    private String order;
    private int limit;

    public Ften() { }

    public BigDecimal getLiutonggudongchigubili() {
        return liutonggudongchigubili;
    }

    public void setLiutonggudongchigubili(BigDecimal liutonggudongchigubili) {
        this.liutonggudongchigubili = liutonggudongchigubili;
    }

    public BigDecimal getMeigugjj() {
        return meigugjj;
    }

    public void setMeigugjj(BigDecimal meigugjj) {
        this.meigugjj = meigugjj;
    }

    public BigDecimal getMeigujzc() {
        return meigujzc;
    }

    public void setMeigujzc(BigDecimal meigujzc) {
        this.meigujzc = meigujzc;
    }

    public BigDecimal getMeigusy() {
        return meigusy;
    }

    public void setMeigusy(BigDecimal meigusy) {
        this.meigusy = meigusy;
    }

    public BigDecimal getMeiguwfplr() {
        return meiguwfplr;
    }

    public void setMeiguwfplr(BigDecimal meiguwfplr) {
        this.meiguwfplr = meiguwfplr;
    }
    
    
    public String getStock_name() {
        return stock_name;
    }

    public void setStock_name(String stock_name) {
        this.stock_name = stock_name;
    }
    
    public String getSuosuhangye() {
        return suosuhangye;
    }

    public void setSuosuhangye(String suosuhangye) {
        this.suosuhangye = suosuhangye;
    }
    
    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

   

    public BigDecimal getLiutonggu() {
        return liutonggu;
    }

    public void setLiutonggu(BigDecimal liutonggu) {
        this.liutonggu = liutonggu;
    }

    public BigDecimal getTongbiincr() {
        return tongbiincr;
    }

    public void setTongbiincr(BigDecimal tongbiincr) {
        this.tongbiincr = tongbiincr;
    }

    public BigDecimal getJingzcbenifitrate() {
        return jingzcbenifitrate;
    }

    public void setJingzcbenifitrate(BigDecimal jingzcbenifitrate) {
        this.jingzcbenifitrate = jingzcbenifitrate;
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
