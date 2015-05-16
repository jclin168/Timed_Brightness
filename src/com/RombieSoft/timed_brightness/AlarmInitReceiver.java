package com.RombieSoft.timed_brightness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/25/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmInitReceiver extends BroadcastReceiver {
    static private final String LOG_NAME = "ATimedBrightness" + AlarmInitReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getContentResolver() == null) {
            Log.v(LOG_NAME,  "FAILURE unable to get content resolver.  Alarms inactive.");
            return;
        }

        PrefMgr.getPref(context);
        Alarms.setNextAlert(context);
    }
}
