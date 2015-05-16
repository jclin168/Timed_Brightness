package com.RombieSoft.timed_brightness;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class TimedBrightness extends AppWidgetProvider
{
    private final static String LOG_NAME = TimedBrightness.class.getSimpleName();
    public final static String ACTION_WIDGET_CONFIGURE = "WidgetConfigure";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(LOG_NAME, "onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i < N; ++i) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    static private int updateCount = 0;
    static public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        updateCount++;
        Log.v(LOG_NAME, "updateAppWidget" + updateCount + "appWigetId=" + appWidgetId);
        Intent intent = new Intent(context, WidgetConfigure.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timed_brightness);
        views.setOnClickPendingIntent(R.id.widget, pi);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}
