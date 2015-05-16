package com.RombieSoft.timed_brightness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/25/12
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmReceiver extends BroadcastReceiver {
    static private final String LOG_NAME = "ATimedBrightness" + AlarmReceiver.class.getSimpleName();
    public static final String KEY_INTENT_EXTRA_BRIGHTNESS = "brightness",
                               KEY_INTENT_EXTRA_FROM_ALARM = "from_alarm";

    @Override
    public void onReceive(Context text, Intent intent) {
        Log.v(LOG_NAME, "Alarm Receiver activated");

        int l = intent.getIntExtra(KEY_INTENT_EXTRA_BRIGHTNESS, -2);
        WidgetConfigure.setSystemBrightnessLevel(text, l);
        PrefMgr.getPref(text);
        Alarms.setNextAlert(text);

        if (WidgetConfigure.isVisible) {
            Intent newIntent = new Intent(text, WidgetConfigure.class);
            newIntent.putExtra(KEY_INTENT_EXTRA_BRIGHTNESS, l);
            newIntent.putExtra(KEY_INTENT_EXTRA_FROM_ALARM, true);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            text.startActivity(newIntent);
        }
    }
}
