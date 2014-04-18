package answer.bean.dto;

import java.math.BigDecimal;

public class ChosendataPF {
	
	private String stock_cd;
	private BigDecimal incr_range_compare_bought;
	private String record_date;
	private String record_time;
	private String bought_date;
	private double bought_price;
	private BigDecimal mxincr;
	private BigDecimal minincr;
	public BigDecimal getMxincr() {
		return mxincr;
	}
	public void setMxincr(BigDecimal mxincr) {
		this.mxincr = mxincr;
	}
	public BigDecimal getMinincr() {
		return minincr;
	}
	public void setMinincr(BigDecimal minincr) {
		this.minincr = minincr;
	}
	public double getBought_price() {
		return bought_price;
	}
	public void setBought_price(double bought_price) {
		this.bought_price = bought_price;
	}
	public BigDecimal getIncr_range_compare_bought() {
		return incr_range_compare_bought;
	}
	public void setIncr_range_compare_bought(BigDecimal incr_range_compare_bought) {
		this.incr_range_compare_bought = incr_range_compare_bought;
	}
	public String getBought_date() {
		return bought_date;
	}
	public void setBought_date(String bought_date) {
		this.bought_date = bought_date;
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
	public String getRecord_time() {
		return record_time;
	}
	public void setRecord_time(String record_time) {
		this.record_time = record_time;
	}

}
