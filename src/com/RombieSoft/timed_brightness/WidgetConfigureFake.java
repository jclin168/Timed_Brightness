package com.RombieSoft.timed_brightness;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/25/12
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class WidgetConfigureFake extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    static private String LOG_NAME = "TimedBrightness" + WidgetConfigureFake.class.getSimpleName();
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(LOG_NAME, "onCreate()");

        if (AdChecker.isAdsDisabled()) {
            finish();
            return;
        }

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            mAppWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.v(LOG_NAME, "mAppWidgetId="+mAppWidgetId);

            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                final Context context = WidgetConfigureFake.this;

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                TimedBrightness.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
            }
        }
        finish();
    }
}
