package answer.bean.dto;

import java.math.BigDecimal;

import answer.util.Caculator;

public class Alldata {

    private String stock_cd = null;
    private BigDecimal yt_close_price;
    private BigDecimal present_price;
    private String record_date;
    private String record_time;
    private String td_open_price;
    private double incr_range;
    private long deal_lots;
    private BigDecimal td_highest_price;
    private BigDecimal td_lowest_price;

    public BigDecimal getTd_highest_price() {
        return td_highest_price;
    }

    public void setTd_highest_price(BigDecimal td_highest_price) {
        this.td_highest_price = td_highest_price;
    }

    public BigDecimal getTd_lowest_price() {
        return td_lowest_price;
    }

    public void setTd_lowest_price(BigDecimal td_lowest_price) {
        this.td_lowest_price = td_lowest_price;
    }

    public void setDeal_lots(long deal_lots) {
        this.deal_lots = deal_lots;
    }

    public long getDeal_lots() {
        return deal_lots;
    }

    public double getIncr_range() {
        this.incr_range = Caculator.keepRound((present_price.doubleValue()
                - yt_close_price.doubleValue()) * 100 / yt_close_price.doubleValue(), 2);
        return this.incr_range;
    }

    public void setIncr_range(double incr_range) {
        this.incr_range = incr_range;
    }

    public String getTd_open_price() {
        return td_open_price;
    }

    public void setTd_open_price(String td_open_price) {
        this.td_open_price = td_open_price;
    }
    private int limit_rec;

    public int getLimit_rec() {
        return limit_rec;
    }

    public void setLimit_rec(int limit_rec) {
        this.limit_rec = limit_rec;
    }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

    public BigDecimal getYt_close_price() {
        return yt_close_price;
    }

    public void setYt_close_price(BigDecimal yt_close_price) {
        this.yt_close_price = yt_close_price;
    }

    public BigDecimal getPresent_price() {
        return present_price;
    }

    public void setPresent_price(BigDecimal present_price) {
        this.present_price = present_price;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }
}
