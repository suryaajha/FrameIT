package pict.s2k.frameit.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kshitij on 10/9/17.
 */

public class TimeDifference {
    public static String getTimeDifference(String postTime){
        //SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date notifyDate = new Date(Long.valueOf(postTime));

        Calendar c = Calendar.getInstance();
        Date curDate = c.getTime();
        long different = curDate.getTime() - notifyDate.getTime();
        //long diffSecs= TimeUnit.MILLISECONDS.toSeconds(diffMillis);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        String timeNotification = new String();
        if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0)
            timeNotification = "Moments ago";
        else if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes != 0){
            if(elapsedMinutes==1)
                timeNotification = elapsedMinutes + " minute ago";
            else
                timeNotification = elapsedMinutes + " minutes ago";
        }

        else if (elapsedDays == 0 && elapsedHours != 0){
            if(elapsedHours==1)
                timeNotification = elapsedHours + " hour ago";
            else
                timeNotification = elapsedHours + " hours ago";
        }

        else if (elapsedDays != 0){
            if(elapsedDays==1)
                timeNotification = elapsedDays + " day ago";
            else
                timeNotification = elapsedDays + " days ago";
        }


        return timeNotification;
    }
}
