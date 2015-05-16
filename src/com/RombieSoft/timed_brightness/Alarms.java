package com.RombieSoft.timed_brightness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/25/12
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Alarms {
    static final private String LOG_NAME = "ATimedBrightness" + Alarms.class.getSimpleName();

    public static void setNextAlert(final Context context) {
        ScheduleRecord next = getNextSchedule();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        if (next == null) {
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.cancel(sender);
            Log.d(LOG_NAME, "Cancel pending alarm");
            return;
        }

        intent.putExtra("brightness", next.getBrightness());
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long time = getTimeInMillis(next);
        am.set(AlarmManager.RTC, time, sender);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        long time2 = time - c.getTimeInMillis();
        time2 /= 1000;
        long s = time2 % 60;
        time2 /= 60;
        long m = time2 % 60;
        time2 /= 60;
        long h = time2 % 24;

        Log.v(LOG_NAME, "Next schedule from now: "+h+":"+m+":"+s);
    }

    static public ScheduleRecord getNextSchedule() {
        ArrayList<ScheduleRecord> list = PrefMgr.getScheduleRecordList();
        if (list.size()==0)
            return null;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMin = c.get(Calendar.MINUTE);

        ScheduleRecord now = new ScheduleRecord(nowHour,  nowMin, 0);
        int n = ScheduleRecord.getNearestFromList(list, now);
        if (n == -1)
            return null;

        return list.get(n);
    }

    static public long getTimeInMillis(ScheduleRecord next) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMin = c.get(Calendar.MINUTE);
        if (next.getHour() < nowHour ||
                (next.getHour() == nowHour && next.getMinute() <= nowMin)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, next.getHour());
        c.set(Calendar.MINUTE, next.getMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }


}
