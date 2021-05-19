package uz.koinot.stadion.data.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;

public class ApiResponseModel {

//    private double getPriceStadium(Stadium stadium, Date start, Date end){
//        DecimalFormat df = new DecimalFormat("#.##");
//        df.setRoundingMode(RoundingMode.CEILING);
//        SimpleDateFormat h = new SimpleDateFormat("HH");
//        SimpleDateFormat m = new SimpleDateFormat("mm");
//        int st= (Integer.parseInt(h.format(start))*60)+Integer.parseInt(m.format(start));
//        int et= (Integer.parseInt(h.format(end))*60)+Integer.parseInt(m.format(end));
//        int ss= (Integer.parseInt(h.format(stadium.getChange_price_time()))*60)+Integer.parseInt(m.format(stadium.getChange_price_time()));
//        double price_day_time = stadium.getPrice_day_time()/60;
//        double price_night_time = stadium.getPrice_night_time()/60;
//        double better=getBetter(start,end);
//        if (st<ss&&et<ss){
//            return Double.parseDouble(df.format( price_day_time*better));
//        }else if (st>ss&&et>ss){
//            return Double.parseDouble(df.format( price_night_time*better));
//        }else {
////            return  Double.parseDouble(df.format(getBetter(start,stadium.getChange_price_time())*price_day_time + getBetter(stadium.getChange_price_time(),end)*price_night_time));
//        }
//
//    }

    private double getBetter(Date start,Date end){
        Calendar s=getDate();
        Calendar e=getDate();
        s.setTime(start);
        e.setTime(end);
        Calendar ss=getDate();
        Calendar ee=getDate();
        ss.set(Calendar.HOUR_OF_DAY,s.get(Calendar.HOUR_OF_DAY));
        ss.set(Calendar.MINUTE,s.get(Calendar.MINUTE));
        ee.set(Calendar.HOUR_OF_DAY,e.get(Calendar.HOUR_OF_DAY));
        ee.set(Calendar.MINUTE,e.get(Calendar.MINUTE));

        return (double) (ee.getTimeInMillis()-ss.getTimeInMillis())/60000;
    }

    private Calendar getDate(){
        Calendar calendar=new GregorianCalendar();
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
            return calendar;
        }catch (Exception e){
            return calendar;
        }
    }

    private Date getDate(String date){
        if (date.length()==5&&(date.contains(":")||date.contains(" ")||date.contains("-")||date.contains("."))){
            int clock;
            int minute;
            if (date.startsWith("0")){
                clock=Integer.parseInt(date.substring(1,2));
                if (date.substring(3).startsWith("0")) {
                    minute = Integer.parseInt(date.substring(4));
                }else {
                    minute=Integer.parseInt(date.substring(3));
                }
            }else {
                clock=Integer.parseInt(date.substring(0,2));
                if (date.substring(3).startsWith("0")) {
                    minute = Integer.parseInt(date.substring(4));
                }else {
                    minute=Integer.parseInt(date.substring(3));
                }            }
            if (clock>=0&&clock<25&&minute>=0&&minute<61){
                Calendar calendar = getDate();
                calendar.set(Calendar.HOUR_OF_DAY,clock);
                calendar.set(Calendar.MINUTE,minute);
                return calendar.getTime();
            }
        }
        return null;
    }
}
