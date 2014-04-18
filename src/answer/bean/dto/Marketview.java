package answer.bean.dto;

import java.math.BigDecimal;

public class Marketview {
	int count;
	String interval;
	String update_date;
	BigDecimal shmarketinc_range;
	BigDecimal szmarketinc_range;
	public String getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public BigDecimal getShmarketinc_range() {
		return shmarketinc_range;
	}
	public void setShmarketinc_range(BigDecimal shmarketinc_range) {
		this.shmarketinc_range = shmarketinc_range;
	}
	public BigDecimal getSzmarketinc_range() {
		return szmarketinc_range;
	}
	public void setSzmarketinc_range(BigDecimal szmarketinc_range) {
		this.szmarketinc_range = szmarketinc_range;
	}
}
