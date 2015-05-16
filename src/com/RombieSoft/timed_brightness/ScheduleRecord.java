package com.RombieSoft.timed_brightness;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/24/12
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleRecord implements Comparable<ScheduleRecord> {
    protected int mHour;
    protected int mMinute;
    protected int mBrightness;

    public int getHour() { return mHour; }
    public int getMinute() { return mMinute; }
    public int getBrightness() { return mBrightness; }
    public void setHour(int n) { mHour = n; }
    public void setMinute(int n) { mMinute = n; }
    public void setBrightness(int n) { mBrightness = n; }

    public ScheduleRecord(int h, int m, int b) {
        mHour = h;
        mMinute = m;
        mBrightness = b;
    }
    
    public String getTime() {
        boolean PM = (mHour >= 12) ? true : false;
        int h = PM ? mHour - 12 : mHour;
        String t = "";
        if (h < 10)
            t = "0";
        t += Integer.toString(h);
        t += ':';
        if (mMinute < 10)
            t += '0';
        t += Integer.toString(mMinute);
        t += (PM ? " PM" : " AM");
        return t;
    }

    public String getBrightnessLevel() {
        if (mBrightness >= 0 && mBrightness < 100)
            return "" + mBrightness + "%";
        return "AUTO";
    }

    public int compareTo(ScheduleRecord rhs) {
        if (mHour > rhs.mHour)
            return 1;
        else if (mHour < rhs.mHour)
            return -1;

        if (mMinute > rhs.mMinute)
            return 1;
        else if (mMinute < rhs.mMinute)
            return -1;
        return 0;
    }
    
    static public int getNearestFromList(ArrayList<ScheduleRecord> list, ScheduleRecord r) {
        int count = list.size();
        if (count <= 0)
            return -1;
        for (int i = 0; i < count; i++) {
            if (r.compareTo(list.get(i)) < 0) {
                return i;
            }
        }
        return 0;
    }
}
