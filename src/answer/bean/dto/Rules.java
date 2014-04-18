package answer.bean.dto;

import java.math.BigDecimal;


public class Rules {
	private int rule_id;
	private BigDecimal con_incr_range_from;
	private BigDecimal con_incr_range_to;
	private int con_research_days;
	private BigDecimal con_hsrangeammount_accuracy;
	private BigDecimal con_ydincrange_accuracy;
	private String res_up_down;
	private BigDecimal  res_yd_increase_range;
	private BigDecimal  res_tot_increase_range;
	private int hs_apply_ammout;
	private int td_apply_counts;
	private int td_apply_down_counts;
	private String totc = "";
	private String totc_down = "";
	
	public String getTotc_down() {
		return totc_down;
	}
	public void setTotc_down(String totc_down) {
		this.totc_down = totc_down;
	}
	public String getTotc() {
		return totc;
	}
	public void setTotc(String totc) {
		this.totc = totc;
	}
	public int getTd_apply_down_counts() {
		return td_apply_down_counts;
	}
	public void setTd_apply_down_counts(int td_apply_down_counts) {
		this.td_apply_down_counts = td_apply_down_counts;
	}
	
	public int getRule_id() {
		return rule_id;
	}
	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}
	public BigDecimal getCon_incr_range_from() {
		return con_incr_range_from;
	}
	public void setCon_incr_range_from(BigDecimal con_incr_range_from) {
		this.con_incr_range_from = con_incr_range_from;
	}
	public BigDecimal getCon_incr_range_to() {
		return con_incr_range_to;
	}
	public void setCon_incr_range_to(BigDecimal con_incr_range_to) {
		this.con_incr_range_to = con_incr_range_to;
	}
	public int getCon_research_days() {
		return con_research_days;
	}
	public void setCon_research_days(int con_research_days) {
		this.con_research_days = con_research_days;
	}
	public BigDecimal getCon_hsrangeammount_accuracy() {
		return con_hsrangeammount_accuracy;
	}
	public void setCon_hsrangeammount_accuracy(
			BigDecimal con_hsrangeammount_accuracy) {
		this.con_hsrangeammount_accuracy = con_hsrangeammount_accuracy;
	}
	public BigDecimal getCon_ydincrange_accuracy() {
		return con_ydincrange_accuracy;
	}
	public void setCon_ydincrange_accuracy(BigDecimal con_ydincrange_accuracy) {
		this.con_ydincrange_accuracy = con_ydincrange_accuracy;
	}
	public String getRes_up_down() {
		return res_up_down;
	}
	public void setRes_up_down(String res_up_down) {
		this.res_up_down = res_up_down;
	}
	public BigDecimal getRes_yd_increase_range() {
		return res_yd_increase_range;
	}
	public void setRes_yd_increase_range(BigDecimal res_yd_increase_range) {
		this.res_yd_increase_range = res_yd_increase_range;
	}
	public BigDecimal getRes_tot_increase_range() {
		return res_tot_increase_range;
	}
	public void setRes_tot_increase_range(BigDecimal res_tot_increase_range) {
		this.res_tot_increase_range = res_tot_increase_range;
	}
	public int getHs_apply_ammout() {
		return hs_apply_ammout;
	}
	public void setHs_apply_ammout(int hs_apply_ammout) {
		this.hs_apply_ammout = hs_apply_ammout;
	}
	public int getTd_apply_counts() {
		return td_apply_counts;
	}
	public void setTd_apply_counts(int td_apply_counts) {
		this.td_apply_counts = td_apply_counts;
	}
	
}
