/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package answer.bean.dto;

/**
 *
 * @author doyin
 */
public class Kdjrule {

    private String stock_cd;
    private String kqujian;
    private String first_action;
    private int max_cheat_num;
    private int shortest_inteval_days;
    private int longest_inteval_days;
    private String second_action;
    private int match_count;
    private int one_intervelday_count;
    private int gather_spread_count;

    public int getGather_spread_count() {
        return gather_spread_count;
    }

    public void setGather_spread_count(int gather_spread_count) {
        this.gather_spread_count = gather_spread_count;
    }

  

    public int getOne_intervelday_count() {
        return one_intervelday_count;
    }

    public void setOne_intervelday_count(int one_intervelday_count) {
        this.one_intervelday_count = one_intervelday_count;
    }

    public int getMax_cheat_num() {
        return max_cheat_num;
    }

    public void setMax_cheat_num(int max_cheat_num) {
        this.max_cheat_num = max_cheat_num;
    }

    public String getFirst_action() {
        return first_action;
    }

    public void setFirst_action(String first_action) {
        this.first_action = first_action;
    }

    public int getLongest_inteval_days() {
        return longest_inteval_days;
    }

    public void setLongest_inteval_days(int longest_inteval_days) {
        this.longest_inteval_days = longest_inteval_days;
    }

    public int getShortest_inteval_days() {
        return shortest_inteval_days;
    }

    public void setShortest_inteval_days(int shortest_inteval_days) {
        this.shortest_inteval_days = shortest_inteval_days;
    }

    public String getKqujian() {
        return kqujian;
    }

    public void setKqujian(String kqujian) {
        this.kqujian = kqujian;
    }

    public int getMatch_count() {
        return match_count;
    }

    public void setMatch_count(int match_count) {
        this.match_count = match_count;
    }

    public String getSecond_action() {
        return second_action;
    }

    public void setSecond_action(String second_action) {
        this.second_action = second_action;
    }

    public String getStock_cd() {
        return stock_cd;
    }

    public void setStock_cd(String stock_cd) {
        this.stock_cd = stock_cd;
    }
}
