package com.example.nnroh.moneycontrol;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    private Calendar myCalendar = Calendar.getInstance();

    public String getCurrentDate(){
        Date date = myCalendar.getTime();
        return formatDate(date);
    }

    public String formatDate(Date date){
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    public String longToString(long date){
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    public String stringToLong(String date){
        return String.valueOf(date);
    }
}
