package com.agento.mmcleaner.util.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocalSharedUtil {
    public static final String SHARED_FIRST = "mmcleaner_first";
    public static final String SHARED_SECOND = "mmcleaner_second";
    public static final String SHARED_THIRD = "mmcleaner_third";
    public static final String SHARED_LUMUS = "mmcleaner_lumus";
    public static final String SHARED_TIME = "mmcleaner_time";
    private static final String SHARED_FIRST_MAIN = "mmcleaner_main_first";
    private static final String SHARED_Second_MAIN = "mmcleaner_main_second";
    private static final String SHARED_NOTIFICATION = "cleanerguard_push";

    public static void setParameter(SharedData value, String key, Context context) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        long firstTimestampInSec = cal.getTimeInMillis() / 1000;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.putLong(SHARED_TIME, firstTimestampInSec);
        editor.apply();
    }

    public static int getParameterInt(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }
    public static long getParameterTime(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        long firstTimestampInSec = cal.getTimeInMillis() / 1000;
        return preferences.getLong(key, firstTimestampInSec+172800);
    }

    public static void setParameterInt(int value, String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static SharedData getParameter(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        return gson.fromJson(preferences.getString(key, ""), SharedData.class);
    }

    public static void setSharedFirstMain(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHARED_FIRST_MAIN, true);
        editor.apply();
    }

    public static boolean isFirstMainShared(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHARED_FIRST_MAIN, false);
    }

    public static void setSharedSecondMain(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHARED_Second_MAIN, true);
        editor.apply();
    }

    public static boolean isSecondMainShared(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHARED_Second_MAIN, false);
    }

    public static boolean isStepOptimized(Context context, String sharedKey) {
        SharedData data = LocalSharedUtil.getParameter(sharedKey, context);

        if(data == null)
            return false;
        return (Long.parseLong(data.date) + 43_200_000) > new Date().getTime();
    }

    public static void setNotificationOn(boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHARED_NOTIFICATION, value);
        editor.apply();
    }

    public static boolean isNotificationOn(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHARED_NOTIFICATION, true);
    }
}
