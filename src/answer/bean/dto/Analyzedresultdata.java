package answer.bean.dto;

import java.math.BigDecimal;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Analyzedresultdata {

    private String stock_cd;
    private String record_date;
    private String record_time;
    private String res_up_down;
    private String buy_advisor;
    private int rule_id;
    private int stockcnt;
    private StringProperty status;//1 委托买入,2成交买,3委托卖出,4成交卖
    private String name;
    private String lotsbili;
    private BigDecimal con_hsrangeammount_accuracy;
    private BigDecimal con_ydincrange_accuracy;
    private BigDecimal res_yd_increase_range;
    private BigDecimal res_tot_increase_range;
    private BigDecimal con_incr_range_from;
    private BigDecimal con_incr_range_to;

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
    public BigDecimal getCon_hsrangeammount_accuracy() {
        return con_hsrangeammount_accuracy;
    }

    public void setCon_hsrangeammount_accuracy(BigDecimal con_hsrangeammount_accuracy) {
        this.con_hsrangeammount_accuracy = con_hsrangeammount_accuracy;
    }

    public BigDecimal getCon_ydincrange_accuracy() {
        return con_ydincrange_accuracy;
    }

    public void setCon_ydincrange_accuracy(BigDecimal con_ydincrange_accuracy) {
        this.con_ydincrange_accuracy = con_ydincrange_accuracy;
    }

    public BigDecimal getRes_tot_increase_range() {
        return res_tot_increase_range;
    }

    public void setRes_tot_increase_range(BigDecimal res_tot_increase_range) {
        this.res_tot_increase_range = res_tot_increase_range;
    }

    public BigDecimal getRes_yd_increase_range() {
        return res_yd_increase_range;
    }

    public void setRes_yd_increase_range(BigDecimal res_yd_increase_range) {
        this.res_yd_increase_range = res_yd_increase_range;
    }

    public String getLotsbili() {
        return lotsbili;
    }

    public void setLotsbili(String lotsbili) {
        this.lotsbili = lotsbili;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuy_advisor(String buy_advisor) {
        this.buy_advisor = buy_advisor;
    }

    public String getBuy_advisor() {
        return buy_advisor;
    }

    public String getStatus() {
        return statusProperty().get();
    }

    public void setStatus(String status) {
        statusProperty().set(status);
    }

    public StringProperty statusProperty() {
        if (status == null) {
            status = new SimpleStringProperty(this, "status");
        }
        return status;
    }
    private final BooleanProperty telecommuterProperty = new SimpleBooleanProperty(this, "telecommuter", false);

    public final BooleanProperty telecommuterProperty() {
        return telecommuterProperty;
    }

    public boolean isTelecommuter() {
        return telecommuterProperty.get();
    }

    public void setTelecommuter(boolean v) {
        telecommuterProperty().set(v);
    }

    public Analyzedresultdata() {
        this.telecommuterProperty.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Boolean> arg0, Boolean ov, Boolean nv) {
            }
        ;
    }

    );
    }
    
	public String getRes_up_down() {
        return res_up_down;
    }

    public void setRes_up_down(String res_up_down) {
        this.res_up_down = res_up_down;
    }

    public int getStockcnt() {
        return stockcnt;
    }

    public void setStockcnt(int stockcnt) {
        this.stockcnt = stockcnt;
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

    public int getRule_id() {
        return rule_id;
    }

    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }
}
