package com.RombieSoft.timed_brightness;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/25/12
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrefMgr {
    static SharedPreferences prefs = null;
    
    public static SharedPreferences getPref(Context context) {
        if (prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs;
    }

    public static SharedPreferences getPref() {
        return prefs;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Preferences Store/Retrieve

    private final static String KEY_RECORD_ITEM = "pref_schedule_record_item";
    private final static String KEY_RECORD_NAME = "pref_schedule_record_data";
    private final static String KEY_WIDGET_AUTO_HIDE = "pref_widget_autohide";

    static public ScheduleRecord getScheduleRecord(int n) {
        if (prefs == null)
            return null;

        int h = prefs.getInt(KEY_RECORD_NAME + n + "_HOUR", -1);
        int m = prefs.getInt(KEY_RECORD_NAME + n + "_MIN", -1);
        int b = prefs.getInt(KEY_RECORD_NAME + n + "_LVL", -1);
        if (h == -1 || m == -1)
            return null;

        return new ScheduleRecord(h, m, b);
    }

    static public int getMaxRecord() {
        if (prefs == null) return 0;

        int x = prefs.getInt(KEY_RECORD_ITEM, -1);
        return ( x <= 0 ? 0 : x);
    }

    static public void setMaxRecord(int n) {
        if (prefs == null) return;
        prefs.edit().putInt(KEY_RECORD_ITEM, n).commit();

    }

    static public void setScheduleRecord(int n, ScheduleRecord record) {
        if (prefs == null) return;
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(KEY_RECORD_NAME + n + "_HOUR", record.getHour());
        edit.putInt(KEY_RECORD_NAME + n + "_MIN", record.getMinute());
        edit.putInt(KEY_RECORD_NAME + n + "_LVL", record.getBrightness());
        edit.commit();
    }

    static public void setWidgetAutoHide(boolean hide) {
        if (prefs == null) return;
        prefs.edit().putInt(KEY_WIDGET_AUTO_HIDE, hide ? 1 : 0).commit();
    }

    static public boolean getWidgetAutoHide() {
        if (prefs == null) return false;
        int n = prefs.getInt(KEY_WIDGET_AUTO_HIDE, -1);
        if (n == 1)
            return true;
        return false;
    }

    static public ArrayList<ScheduleRecord> getScheduleRecordList() {
        int maxRecord = PrefMgr.getMaxRecord();
        ArrayList<ScheduleRecord> list = new ArrayList<ScheduleRecord>();
        for (int i = 0; i < maxRecord; ++i) {
            ScheduleRecord item = PrefMgr.getScheduleRecord(i);
            if (item != null)
                list.add(item);
        }
        Collections.sort(list);
        return list;
    }


}
