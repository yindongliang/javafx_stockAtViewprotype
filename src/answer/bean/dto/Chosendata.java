package answer.bean.dto;

import answer.logic.helper.LogicHelper;
import java.math.BigDecimal;



import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Chosendata {

    private String stock_cd;
    private Integer rest_sq;
    private BigDecimal buy_lots;
    private BigDecimal buy_price;
    private BigDecimal sell_lots;
    private BigDecimal sell_price;
    private ObjectProperty<BigDecimal> incr_range_compare_bought;
    private ObjectProperty<BigDecimal> current_price;
    private StringProperty pure_benifit;
    private BigDecimal handlecharge;
    private BigDecimal bought_shanghai_price;
    private String buy_date;
    private String buy_time;
    private String sell_date;
    private String sell_time;
    private StringProperty status;//1 委托买入,2成交买,3委托卖出,4成交卖
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getCurrent_price() {
        return current_priceProperty().get();
    }

    public void setCurrent_price(BigDecimal current_price) {
        current_priceProperty().set(current_price);

    }

    public ObjectProperty<BigDecimal> current_priceProperty() {
        if (current_price == null) {
            current_price = new SimpleObjectProperty<BigDecimal>(this, "current_price");
        }
        return current_price;
    }

    public void setPure_benifit(String value) {
        pure_benifitProperty().set(value);
    }

    public String getPure_benifit() {
        return pure_benifitProperty().get();
    }

    public StringProperty pure_benifitProperty() {
        if (pure_benifit == null) {
            pure_benifit = new SimpleStringProperty(this, "pure_benifit");
        }
        return pure_benifit;
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

    public Chosendata() {
        this.telecommuterProperty.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Boolean> arg0, Boolean ov, Boolean nv) {
                // System.out.println(	  stock_cd + " is a remote worker: " + nv);
            }
        ;
    }

    );
    }
	
	public BigDecimal getSell_lots() {
        return sell_lots;
    }

    public void setSell_lots(BigDecimal sell_lots) {
        this.sell_lots = sell_lots;
    }

    public BigDecimal getSell_price() {
        return sell_price;
    }

    public void setSell_price(BigDecimal sell_price) {
        this.sell_price = sell_price;
    }

    public BigDecimal getIncr_range_compare_bought() {
        return incr_range_compare_boughtProperty().get();
    }

    public void setIncr_range_compare_bought(BigDecimal incr_range_compare_bought) {
        incr_range_compare_boughtProperty().set(incr_range_compare_bought);
    }

    public ObjectProperty<BigDecimal> incr_range_compare_boughtProperty() {
        if (incr_range_compare_bought == null) {
            incr_range_compare_bought = new SimpleObjectProperty<BigDecimal>(this, "incr_range_compare_bought");
        }
        return incr_range_compare_bought;
    }

    public String getBuy_time() {
        return buy_time;
    }

    public void setBuy_time(String buy_time) {
        this.buy_time = buy_time;
    }

    public String getSell_date() {
        return sell_date;
    }

    public void setSell_date(String sell_date) {
        this.sell_date = sell_date;
    }

    public String getSell_time() {
        return sell_time;
    }

    public void setSell_time(String sell_time) {
        this.sell_time = sell_time;
    }

    public BigDecimal getBought_shanghai_price() {
        return bought_shanghai_price;
    }

    public void setBought_shanghai_price(BigDecimal bought_shanghai_price) {
        this.bought_shanghai_price = bought_shanghai_price;
    }

    public BigDecimal getHandlecharge() {
        return handlecharge;
    }

    public void setHandlecharge(BigDecimal handlecharge) {
        this.handlecharge = handlecharge;
    }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }

    public Integer getRest_sq() {
        return rest_sq;
    }

    public void setRest_sq(Integer rest_sq) {
        this.rest_sq = rest_sq;
    }

    public BigDecimal getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(BigDecimal buy_price) {
        this.buy_price = buy_price;
    }

    public BigDecimal getBuy_lots() {
        return buy_lots;
    }

    public void setBuy_lots(BigDecimal buy_lots) {
        this.buy_lots = buy_lots;
    }

    public String getBuy_date() {
        return buy_date;
    }

    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }
}
