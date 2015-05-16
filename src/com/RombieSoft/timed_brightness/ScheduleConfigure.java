package com.RombieSoft.timed_brightness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.*;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/24/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleConfigure extends Activity {

    private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private ListView timerList = null;
    private static final int nFreeLimit = 999;

    private final String[] levels = new String[] {
            "Auto", "0%",
            "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%",
            "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%",
            "90%", "95%", "100%", "Auto"
    };
    private final int [] nlevel = new int[] {
            -1, 0,
            10, 15, 20, 25, 30, 35, 40, 45,
            50, 55, 60, 65, 70, 75, 80, 85,
            90, 95, 100, -1
    };

    private AdView adView = null;

    @Override
    public void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    protected void initAdView() {
        try {
            adView = new AdView(this, AdSize.BANNER, "a14f209e9aa76a4");
            LinearLayout layout = (LinearLayout)findViewById(R.id.adview_in_config);
            layout.addView(adView);
            AdRequest request = new AdRequest();
            request.addTestDevice(AdRequest.TEST_EMULATOR);
            request.addTestDevice("CF4273234667D95A0FD302374BFC9AF0");
            //request.setTesting(true);
            adView.loadAd(request);
        }
        catch (Exception e) {
            adView = null;
        }
    }

    private void buildList() {
        Collections.sort(listRecord);
        list = new ArrayList<HashMap<String,String>>();
        for (ScheduleRecord s : listRecord) {
            HashMap<String,String> item = new HashMap<String,String>();
            item.put("time", s.getTime());
            item.put("level", s.getBrightnessLevel());
            list.add(item);
        }
        ListAdapter timerListAdapter = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[] { "time","level" },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        timerList.setAdapter(timerListAdapter);
    }
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (AdChecker.isAdsDisabled()) {
            finish();
            return;
        }

        setContentView(R.layout.widget_configure);

        initAdView();

        findViewById(R.id.buttonConfigClose).setOnClickListener(mOnButtonConfigCloseListener);
        findViewById(R.id.buttonConfigClear).setOnClickListener(mOnButtonConfigClearListener);
        findViewById(R.id.buttonConfigAdd).setOnClickListener(mOnButtonConfigAddListener);
        timerList = (ListView)findViewById(R.id.timerList);

        initData();
        buildList();

        registerForContextMenu(timerList);

    }

    private final static int MENU_ID_ABOUT = Menu.FIRST;
    private final static int MENU_ID_HELP = MENU_ID_ABOUT + 1;
    private final static int MENU_ID_EXIT = MENU_ID_HELP + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(0, MENU_ID_HELP, 0, R.string.menuHelp);
        item.setIcon(android.R.drawable.ic_menu_help);
        item = menu.add(0, MENU_ID_ABOUT, 0, R.string.menuAbout);
        item.setIcon(android.R.drawable.ic_menu_info_details);
        item = menu.add(0, MENU_ID_EXIT, 0, R.string.menuExit);
        item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        AlertDialog.Builder builder;
        AlertDialog dialog;
        String s;
        switch(item.getItemId()) {
            case MENU_ID_ABOUT: {
                String versionName = "";
                try {
                    final String PACKAGE_NAME = "com.RombieSoft.timed_brightness";
                    PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
                    versionName = info.versionName;
                }
                catch (Exception e) {}
                builder = new AlertDialog.Builder(this);
                s = getResources().getString(R.string.dlgAboutMsg).replaceAll("\\\\n","\n")
                        .replaceAll("VERSION", "Version "+versionName);
                builder.setMessage(s)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton(R.string.menuWeb, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String url = "http://rombiesoft.blogspot.com/";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                dialog.cancel();
                                startActivity(i);
                            }
                        });
                dialog = builder.create();
                dialog.setOwnerActivity(this);
                dialog.show();
                break;
            }
            case MENU_ID_HELP:
                builder = new AlertDialog.Builder(this);
                s = getResources().getString(R.string.dlgHelpMsg).replaceAll("\\\\n","\n");
                builder.setMessage(s)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dialog = builder.create();
                dialog.setOwnerActivity(this);
                dialog.show();
                break;
            case MENU_ID_EXIT:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.timerList) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(R.string.menuAction);
            menu.add(Menu.NONE, 0, 0, R.string.menuModify);
            menu.add(Menu.NONE, 1, 1, R.string.menuDelete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (0 == menuItemIndex) {
            onModifyMenuSelected(info.position);
        }
        else {
            listRecord.remove(info.position);
            buildList();
        }
        return true;
    }

    private View.OnClickListener mOnButtonConfigCloseListener = new View.OnClickListener() {
        public void onClick(View v) { onConfigClose(); }
    };
    private View.OnClickListener mOnButtonConfigClearListener = new View.OnClickListener() {
        public void onClick(View v) { onConfigClearAll(); }
    };
    private View.OnClickListener mOnButtonConfigAddListener = new View.OnClickListener() {
        public void onClick(View v) { onConfigAdd(); }
    };

    private void onConfigClose() {
        int count = listRecord.size();
        int i;
        PrefMgr.setMaxRecord(count);
        for (i = 0; i < count; i++) {
            PrefMgr.setScheduleRecord(i, listRecord.get(i));
        }
        Toast.makeText(this, R.string.toastScheduleSaved, Toast.LENGTH_LONG).show();
        Alarms.setNextAlert(this);
        finish();
    }
    
    private void onConfigClearAll() {
        listRecord = new ArrayList<ScheduleRecord>();
        buildList();
        Alarms.setNextAlert(this);
    }

    private void onConfigAddLimit() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Brightness Level Scheduler")
                .setMessage(R.string.toastLimitation2)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    private void onConfigAdd() {
        if (timerList.getAdapter().getCount() >= nFreeLimit) {
            onConfigAddLimit();
            return;
        }
        
        if (mCurrentDialog == null)
            mCurrentDialog = createTimePickerDialog();

        mEditingEntry = -1;
        Date now = new Date();
        setTimePickerTime(now.getHours(), now.getMinutes());
        mCurrentDialog.show();
    }

    private void setTimePickerTime(int hour, int minute) {
        TimePicker tp = (TimePicker)mCurrentDialog.findViewById(R.id.setTimePicker);
        tp.setCurrentHour(hour);
        tp.setCurrentMinute(minute);
    }

    Dialog mCurrentDialog = null;
    int mEditingEntry = -1;
    
    private Dialog createTimePickerDialog() {
        /*
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.time_brightness_picker,
                (ViewGroup) findViewById(R.id.picker_root));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, levels);

        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setSingleChoiceItems(levels, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface i, int n) { }
        });
        alertDialog = builder.create();
        return alertDialog;
        */
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.time_brightness_picker);
        dialog.setTitle("Brightness Level");
        Spinner spinner = (Spinner)dialog.findViewById(R.id.setLevelPicker);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialog.setOwnerActivity(this);
        
        Button button = (Button)dialog.findViewById(R.id.setTimeAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                addDialogOK();
            }
        });
        button = (Button)dialog.findViewById(R.id.setTimeCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                addDialogCancel();
            }
        });

        return dialog;
    }

    private void addDialogCancel() {
        mEditingEntry = -1;
        if (mCurrentDialog != null)
            mCurrentDialog.dismiss();
    }

    private void addDialogOK() {
        if (mCurrentDialog != null)
            mCurrentDialog.dismiss();
        TimePicker tp = (TimePicker)mCurrentDialog.findViewById(R.id.setTimePicker);
        Spinner spinner = (Spinner)mCurrentDialog.findViewById(R.id.setLevelPicker);
        
        int h,m,b;
        h = tp.getCurrentHour();
        m = tp.getCurrentMinute();
        b = spinner.getSelectedItemPosition();
        b = nlevel[b];
        ScheduleRecord record = new ScheduleRecord(h,m,b);
        if (mEditingEntry == -1)
            listRecord.add(record);
        else
            listRecord.set(mEditingEntry, record);
        mEditingEntry = -1;
        buildList();
    }

    private void onModifyMenuSelected(int position) {
        if (mCurrentDialog == null)
            mCurrentDialog = createTimePickerDialog();
        ScheduleRecord item = listRecord.get(position);

        Spinner spinner = (Spinner)mCurrentDialog.findViewById(R.id.setLevelPicker);
        setTimePickerTime(item.getHour(), item.getMinute());
        int i = 0;
        for (int n : nlevel) {
            if (n == item.getBrightness()) {
                spinner.setSelection(i);
                break;
            }
            i++;
        }
        mEditingEntry = position;
        mCurrentDialog.show();
    }

    public ArrayList<ScheduleRecord> listRecord = null;

    private void initData() {
        listRecord = PrefMgr.getScheduleRecordList();
    }



}
