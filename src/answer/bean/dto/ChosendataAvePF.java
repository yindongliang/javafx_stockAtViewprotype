package answer.bean.dto;

import java.math.BigDecimal;

public class ChosendataAvePF {
	
	private BigDecimal current_ave;
	private String record_date;
	private String record_time;
	private String bought_date;
	private BigDecimal lowest_ave;
	private BigDecimal highest_ave;
	
	
	public BigDecimal getCurrent_ave() {
		return current_ave;
	}
	public void setCurrent_ave(BigDecimal current_ave) {
		this.current_ave = current_ave;
	}
	public String getBought_date() {
		return bought_date;
	}
	public void setBought_date(String bought_date) {
		this.bought_date = bought_date;
	}
	public BigDecimal getLowest_ave() {
		return lowest_ave;
	}
	public void setLowest_ave(BigDecimal lowest_ave) {
		this.lowest_ave = lowest_ave;
	}
	public BigDecimal getHighest_ave() {
		return highest_ave;
	}
	public void setHighest_ave(BigDecimal highest_ave) {
		this.highest_ave = highest_ave;
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
