package com.exilz.calendarevents;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

public class CalendarEventsModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private Callback addEventSuccessCallback;
    private Callback addEventErrorCallback;

    private static final String ACTIVITY_NAME = "CalendarEventsModule";

    private static final Integer RESULT_CODE_CREATE = 0;
    private static final Integer RESULT_CODE_OPENCAL = 1;

    private static final Integer DEFAULT_END_OFFSET = 3600 * 1000 * 48;

    public CalendarEventsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "CalendarEvents";
    }

    @ReactMethod
    public void addEvent(ReadableMap config, Callback successCallback, Callback cancelCallback) {
        Activity currentActivity = getCurrentActivity();
    
        if (currentActivity == null) {
            cancelCallback.invoke("Activity doesn't exist");
            return;
        }

        addEventSuccessCallback = successCallback;
        addEventErrorCallback = cancelCallback;

        if (!permissionsCheck(currentActivity)) {
          return;
        }

        try {
            final Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
            calendarIntent
                .setType("vnd.android.cursor.item/event")
                .putExtra("title", config.getString("title"))
                .putExtra("beginTime", (long) config.getDouble("startDate"));

            if (config.hasKey("endDate")) {
                calendarIntent.putExtra("endTime", (long) config.getDouble("endDate"));
            }

            if (config.hasKey("location")
                    && config.getString("location") != null
                    && !config.getString("location").isEmpty()) {
                calendarIntent.putExtra("eventLocation", config.getString("location"));
            }

            if (config.hasKey("description")
                    && config.getString("description") != null
                    && !config.getString("description").isEmpty()) {
                calendarIntent.putExtra("description", config.getString("description"));
            }

            currentActivity.startActivityForResult(calendarIntent, RESULT_CODE_CREATE);
        } catch (Exception e) {
            addEventErrorCallback.invoke(e);
        }

    }

    // Adapted from https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin/blob/master/src/android/nl/xservices/plugins/Calendar.java#L630
    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
        // Adapt code and invoke callback in here
        if (addEventSuccessCallback != null) {
            if (requestCode == RESULT_CODE_CREATE) {
                if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                    // resultCode may be 0 (RESULT_CANCELED) even when it was created,
                    // so passing nothing is the clearest option here
                    addEventSuccessCallback.invoke();
                } else {
                    // odd case
                    addEventSuccessCallback.invoke();
                }
            } else if (requestCode == RESULT_CODE_OPENCAL) {
                addEventSuccessCallback.invoke();
            } else {
                addEventErrorCallback.invoke("Unable to add event (" + resultCode + ").");
            }
        }
    }

    private boolean permissionsCheck(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR
            };
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
            return false;
        }
        return true;
    }

    // RN 0.30+ : this needs to be overriden
    public void onNewIntent(Intent intent) {

    }
}