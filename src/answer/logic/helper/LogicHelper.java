package answer.logic.helper;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;

import answer.util.Caculator;

public class LogicHelper {

    public static boolean isQuit(List<String> stock_detail) {
        if (stock_detail == null) {
            return true;
        }
        return false;
    }

    public static boolean isOpening(List<String> stock_detail) {
        if (stock_detail == null) {//quit from the market
            return false;
        }
        if (("0.00").equals(stock_detail.get(1)) || ("0.000").equals(stock_detail.get(1))) {
            return false;
        }
        return true;
    }

    public static boolean isStock(List<String> stock_detail) {
        if (stock_detail.size() < 32) {
            return false;
        }
        return true;
    }

    public static double caculatePurebenifit(double curprice, double bought_price, int bought_lots,
            PropertiesConfiguration stocksProperties) {

        double handlecharge = caculatehandCharge(bought_price, bought_lots, stocksProperties);

        double purebenifit = Caculator.keepRound(
                (curprice - bought_price) * bought_lots - handlecharge, 2);

        return purebenifit;
    }

    public static double caculatehandCharge(double bought_price, int bought_lots, PropertiesConfiguration stocksProperties) {
        double commission_rate = stocksProperties.getDouble("commission_rate");
        double charge_rate = stocksProperties.getDouble("charge_rate");


        double handlecharge = bought_price * bought_lots * commission_rate;

        if (bought_price * bought_lots * charge_rate < 5) {
            handlecharge = handlecharge + 10;
        } else {
            handlecharge = handlecharge + bought_price * bought_lots * charge_rate * 2;
        }
        return Caculator.keepRound(handlecharge, 2);
    }

    public static double caculateIncrease(double org, double now) {

        return Caculator.keepRound((now - org) * 100 / org, 2);
    }
}
