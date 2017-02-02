package com.pingala.parkeasy.models;

/**
 * Created by Habeeb on 2/1/2017.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateTime {


    public String timeDifference(String enterTime,String currentTime) {
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(enterTime);
            d2 = format.parse(currentTime);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            String caltime = diffDays + " days, "+diffHours + " hours, "+diffMinutes + " minutes, "+diffSeconds + " seconds.";


            return caltime;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return "";
    }
    }

