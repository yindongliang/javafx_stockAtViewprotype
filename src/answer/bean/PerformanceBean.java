package answer.bean;

public class PerformanceBean {

    private String highest_tot = "-";
    private String lowest_tot = "-";
    private double current_tot;
    private double highest_ave;
    private double lowest_ave;
    private double current_ave;
    private double win_rate;
    private double lost_rate;
    private double draw_rate;
    private double stop_rate;
    private double shanghai_incr;
    private double purebenifit_tot;
    private double handlecharge_tot;
    
    private double got_pbenifit;
    private double tot_pbf_fornow;

    public double getGot_pbenifit() {
        return got_pbenifit;
    }

    public void setGot_pbenifit(double got_pbenifit) {
        this.got_pbenifit = got_pbenifit;
    }

    public double getTot_pbf_fornow() {
        return tot_pbf_fornow;
    }

    public void setTot_pbf_fornow(double tot_pbf_fornow) {
        this.tot_pbf_fornow = tot_pbf_fornow;
    }
    
    public String getHighest_tot() {
        return highest_tot;
    }

    public void setHighest_tot(String highest_tot) {
        this.highest_tot = highest_tot;
    }

    public String getLowest_tot() {
        return lowest_tot;
    }

    public void setLowest_tot(String lowest_tot) {
        this.lowest_tot = lowest_tot;
    }

    public double getPurebenifit_tot() {
        return purebenifit_tot;
    }

    public void setPurebenifit_tot(double purebenifit_tot) {
        this.purebenifit_tot = purebenifit_tot;
    }

    public double getHandlecharge_tot() {
        return handlecharge_tot;
    }

    public void setHandlecharge_tot(double handlecharge_tot) {
        this.handlecharge_tot = handlecharge_tot;
    }

    public double getCurrent_tot() {
        return current_tot;
    }

    public void setCurrent_tot(double current_tot) {
        this.current_tot = current_tot;
    }

    public double getHighest_ave() {
        return highest_ave;
    }

    public void setHighest_ave(double highest_ave) {
        this.highest_ave = highest_ave;
    }

    public double getLowest_ave() {
        return lowest_ave;
    }

    public void setLowest_ave(double lowest_ave) {
        this.lowest_ave = lowest_ave;
    }

    public double getCurrent_ave() {
        return current_ave;
    }

    public void setCurrent_ave(double current_ave) {
        this.current_ave = current_ave;
    }

    public double getWin_rate() {
        return win_rate;
    }

    public void setWin_rate(double win_rate) {
        this.win_rate = win_rate;
    }

    public double getLost_rate() {
        return lost_rate;
    }

    public void setLost_rate(double lost_rate) {
        this.lost_rate = lost_rate;
    }

    public double getDraw_rate() {
        return draw_rate;
    }

    public void setDraw_rate(double draw_rate) {
        this.draw_rate = draw_rate;
    }

    public double getStop_rate() {
        return stop_rate;
    }

    public void setStop_rate(double stop_rate) {
        this.stop_rate = stop_rate;
    }

    public double getShanghai_incr() {
        return shanghai_incr;
    }

    public void setShanghai_incr(double shanghai_incr) {
        this.shanghai_incr = shanghai_incr;
    }
}
