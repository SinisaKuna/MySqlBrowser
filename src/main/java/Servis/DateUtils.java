package Servis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String dateCroFormatting (String sqlDate) {

        String croDate = "";
        String godina = sqlDate.substring(0, 4);
        String mjesec = sqlDate.substring(5, 7);
        String dan = sqlDate.substring(8);
        croDate = dan + "." + mjesec + "." + godina;

        return croDate;
    }
    public static String getFirstDayOfMonth (String dateString) throws ParseException {
        // Parse the date string using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(dateString);

        // Calculate the first and last days of the month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();

        return sdf.format(firstDayOfMonth);
    }
    public static String getLastDayOfMonth (String dateString) throws ParseException {
        // Parse the date string using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(dateString);

        // Calculate the first and last days of the month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfMonth = calendar.getTime();

        return sdf.format(lastDayOfMonth);
    }
    public static void main(String[] args) {
        String primjer = dateCroFormatting("2023-04-14");
        System.out.println(primjer);
    }
}
