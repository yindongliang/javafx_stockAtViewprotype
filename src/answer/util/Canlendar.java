package answer.util;

import java.util.Date;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Canlendar {

    public static String getSystemdate() {

        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

    }

    public static String getSystemtime() {

        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());

    }

    public static String getBeforday(String d1, int n) {

        Format formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");

        Date targetdate = null;
        try {
            targetdate = (Date) formatter.parseObject(d1);
        } catch (ParseException ex) {
            Logger.getLogger(Canlendar.class.getName()).log(Level.SEVERE, null, ex);
        }


        long afterTime = (targetdate.getTime() / 1000) - 60 * 60 * 24 * n;

        targetdate.setTime(afterTime * 1000);

        String destinedate = formatter.format(targetdate);

        return destinedate;
    }

    public static String formaterString(String target) {
        Format formatter1 = new java.text.SimpleDateFormat("yyyyMMdd");
        Date targetdate = null;
        try {
            targetdate = (Date) formatter1.parseObject(target);
        } catch (ParseException ex) {
            Logger.getLogger(Canlendar.class.getName()).log(Level.SEVERE, null, ex);
        }
        Format formatter2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return formatter2.format(targetdate);
    }

    public static List<String> getAllfridaybynow(String startday) {

        Calendar calendarend = Calendar.getInstance();//当前日期
        calendarend.add(Calendar.DAY_OF_YEAR, 7);
        calendarend.set(Calendar.DAY_OF_WEEK, 6);
        Calendar calendarstart = Calendar.getInstance();
        calendarstart.setTime(StrToDate(startday));
        calendarstart.add(Calendar.DAY_OF_YEAR, -7);
        calendarstart.set(Calendar.DAY_OF_WEEK, 6);
        List<String> ls = new ArrayList<String>();
        for (; calendarstart.before(calendarend); calendarstart.add(Calendar.DAY_OF_YEAR, 7)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ls.add(sdf.format(calendarstart.getTime()));
        }
        return ls;
    }

    public static List<String> getAllMondaybynow(String startday) {

        Calendar calendarend = Calendar.getInstance();//当前日期
//        calendarend.add(Calendar.DAY_OF_YEAR, 7);
        calendarend.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);


        Calendar calendarstart = Calendar.getInstance();
        calendarstart.setTime(StrToDate(startday));
        calendarstart.add(Calendar.DAY_OF_YEAR, -7);
        calendarstart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<String> ls = new ArrayList<String>();
        for (; calendarstart.before(calendarend); calendarstart.add(Calendar.DAY_OF_YEAR, 7)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ls.add(sdf.format(calendarstart.getTime()));
        }
        return ls;
    }

    public static String getMondayOfSpeciWeek(String specday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(StrToDate(specday));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());

    }

    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean isCurrentWeek(String str) {

        Calendar calendarstart = Calendar.getInstance();//当前日期
        calendarstart.set(Calendar.DAY_OF_WEEK, 2);


        Calendar calendarend = Calendar.getInstance();//当前日期
        calendarend.set(Calendar.DAY_OF_WEEK, 7);
        calendarend.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (str.compareTo(sdf.format(calendarstart.getTime())) >= 0 && str.compareTo(sdf.format(calendarend.getTime())) <= 0) {
            return true;
        }
        return false;
    }
    public static boolean isCurrentWeekandFr(String str) {

        Calendar calendarstart = Calendar.getInstance();//当前日期
        if (calendarstart.get(Calendar.DAY_OF_WEEK)==1){
            calendarstart.set(Calendar.DAY_OF_YEAR, -1);
        };
        calendarstart.set(Calendar.DAY_OF_WEEK, 6);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (str.compareTo(sdf.format(calendarstart.getTime()))<0) {
            return true;
        }
        return false;
    }
}
