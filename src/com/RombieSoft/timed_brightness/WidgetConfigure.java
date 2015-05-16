package com.RombieSoft.timed_brightness;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: jclin
 * Date: 1/19/12
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WidgetConfigure extends Activity {
    static public boolean isVisible = false;
    final static String LOG_NAME = "ATimedBrightness" + WidgetConfigure.class.getSimpleName();

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final static int DIALOG_WIDGET_CONFIG = 1;
    private boolean bAutoHide = true;

    public WidgetConfigure () {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(LOG_NAME, "onCreate()");
        
        if (AdChecker.isAdsDisabled()) {
            finish();
            return;
        }

        Intent intent = getIntent();

        this.setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
        setContentView(R.layout.brightness_setting);

        findViewById(R.id.buttonSettingOK).setOnClickListener(mOnClickListener);
        findViewById(R.id.buttonLevel0).setOnClickListener(mOnButton0ClickListener);
        findViewById(R.id.buttonLevel15).setOnClickListener(mOnButton15ClickListener);
        findViewById(R.id.buttonLevel25).setOnClickListener(mOnButton25ClickListener);
        findViewById(R.id.buttonLevel35).setOnClickListener(mOnButton35ClickListener);
        findViewById(R.id.buttonLevel45).setOnClickListener(mOnButton45ClickListener);
        findViewById(R.id.buttonLevel65).setOnClickListener(mOnButton65ClickListener);
        findViewById(R.id.buttonLevel85).setOnClickListener(mOnButton85ClickListener);
        findViewById(R.id.buttonLevel100).setOnClickListener(mOnButton100ClickListener);
        findViewById(R.id.buttonScheduleConfig).setOnClickListener(mOnButtonScheduleConfigClickListener);
        findViewById(R.id.buttonScheduleCheck).setOnClickListener(mOnButtonScheduleCheckClickListener);
        findViewById(R.id.buttonScheduleAutoHide).setOnClickListener(mOnButtonScheduleAutoHideClickListener);

        bAutoHide = PrefMgr.getWidgetAutoHide();
        CheckBox checkbox = (CheckBox)findViewById(R.id.buttonScheduleAutoHide);
        checkbox.setChecked(bAutoHide);
        PrefMgr.getPref(this);

        Bundle extra = intent.getExtras();
        if (extra != null) {
            mAppWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.v(LOG_NAME, "mAppWidgetId="+mAppWidgetId);
        }

        Alarms.setNextAlert(this);

        doWork(intent);
        setResult(RESULT_CANCELED);
    }

    private void doWork(Intent intent) {
        int l = intent.getIntExtra(AlarmReceiver.KEY_INTENT_EXTRA_BRIGHTNESS, -2);
        if (l != -2)
            setBrightnessLevel(l);
    }

    @Override
    public void onNewIntent(Intent intent) {
        doWork(intent);
    }
    
    @Override public void onStart() {
        isVisible = true;
        Log.d(LOG_NAME, "OnStart()");
        super.onStart();
        //showDialog(DIALOG_WIDGET_CONFIG);
    }
    @Override public void onResume() {
        isVisible = true;
        Log.d(LOG_NAME, "onResume()");
        super.onResume();
    }

    @Override public void onPause() {
        isVisible = false;
        super.onPause();
    }

    @Override public void onStop() {
        isVisible = false;
        super.onStop();
    }

    @Override public void onDestroy() {
        isVisible = false;
        super.onDestroy();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                final Context context = WidgetConfigure.this;

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                TimedBrightness.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
            }
            finish();
        }
    };

    View.OnClickListener mOnButtonScheduleAutoHideClickListener = new View.OnClickListener() {
        public void onClick(View view) { onScheduleAutoHide();  }
    };
    View.OnClickListener mOnButtonScheduleCheckClickListener = new View.OnClickListener() {
        public void onClick(View v) { onScheduleCheck(); }  
    };
    View.OnClickListener mOnButtonScheduleConfigClickListener = new View.OnClickListener() {
        public void onClick(View v) { onScheduleConfig(); }
    };
    View.OnClickListener mOnButton0ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(0);  }
    };
    View.OnClickListener mOnButton15ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(15);  }
    };
    View.OnClickListener mOnButton25ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(25);  }
    };
    View.OnClickListener mOnButton35ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(35);  }
    };
    View.OnClickListener mOnButton45ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(45);  }
    };
    View.OnClickListener mOnButton65ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(65);  }
    };
    View.OnClickListener mOnButton85ClickListener = new View.OnClickListener() {
        public void onClick(View v) { onChangeLevel(85);  }
    };
    
    private static boolean isButtonAuto = false;
    View.OnClickListener mOnButton100ClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Button button = (Button)v;
            if (isButtonAuto) {
                onChangeLevel(-1);
                button.setText("100%");
            }
            else {
                onChangeLevel(100);
                button.setText("Auto");
            }
            isButtonAuto = !isButtonAuto;
        }
    };

    private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    private static final int SCREEN_MODE_AUTO = 1;
    private static final int SCREEN_MODE_MANUAL = 0;
    
    public static void setSystemBrightnessLevel(Context context, int n) {
        ContentResolver cr = context.getContentResolver();
        try {
            if (-1 == n) {
                Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_AUTO);
            }
            else {
                if (Settings.System.getInt(cr, SCREEN_BRIGHTNESS_MODE, -1) == SCREEN_MODE_AUTO)
                    Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_MANUAL);

                int x = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS, -1);
                int brightness = (int)(0.01f * n * 255.0f);  //Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);
                if (brightness < 10)
                    brightness = 10;
                else if (brightness > 255)
                    brightness = 255;
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
        }
        catch (Exception e) {
            Log.d(LOG_NAME, "setSystemBrightnessLevel:" + n);
        }

    }

    public void setBrightnessLevel(int n) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float f = (float)n / 100.0f;
        if (f < 0.05f)
            f = 0.05f;
        lp.screenBrightness = f;
        getWindow().setAttributes(lp);
    }

    protected void onChangeLevel(int n) {
        setSystemBrightnessLevel(this, n);
        setBrightnessLevel(n);
        if (bAutoHide)
            finish();
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_WIDGET_CONFIG:
                AlertDialog.Builder builder;
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.brightness_setting, (ViewGroup)findViewById(R.id.layout_root));
                builder = new AlertDialog.Builder(context);
                builder.setView(layout);
                dialog = builder.create();
                dialog.findViewById(R.id.buttonSettingOK).setOnClickListener(mOnClickListener);
                dialog.findViewById(R.id.buttonLevel0).setOnClickListener(mOnButton0ClickListener);
                break;
            default:
                break;
        }
        return dialog;
    }

    private void onScheduleConfig() {
        startActivity(new Intent(this, ScheduleConfigure.class));
    }

    private void onScheduleCheck() {
        CheckBox checkbox = (CheckBox)findViewById(R.id.buttonScheduleCheck);
        if (checkbox != null) {
            if (checkbox.isChecked() == false) {
                checkbox.setChecked(true);
                Toast.makeText(this, R.string.toastLimitation1, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void onScheduleAutoHide() {
        CheckBox checkbox = (CheckBox)findViewById(R.id.buttonScheduleAutoHide);
        if (checkbox != null) {
            bAutoHide = checkbox.isChecked();
            PrefMgr.setWidgetAutoHide(bAutoHide);
        }
    }

}
