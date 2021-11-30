package com.agento.mmcleaner.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.agento.mmcleaner.scan_util.model.JunkInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class UStats {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();
    public static PackageManager pm;
    private static List<JunkInfo> usageStats = null;

    public static List<JunkInfo> getUsageStatsList(Context context, boolean isGenerateRandom) {
        if (pm == null)
            pm = context.getPackageManager();
        if (usageStats == null) {
            UsageStatsManager usm = getUsageStatsManager(context);
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.HOUR, -1);
            long startTime = calendar.getTimeInMillis();
            Log.d(TAG, "Range start:" + dateFormat.format(startTime));
            Log.d(TAG, "Range end:" + dateFormat.format(endTime));
            List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            List<JunkInfo> usageList = new ArrayList<>();
            for (UsageStats stats : usageStatsList) {
                if (!stats.getPackageName().equals(context.getPackageName())) {
                    JunkInfo junkInfo = new JunkInfo();
                    junkInfo.mPackageName = stats.getPackageName();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        try {
                            junkInfo.mSize = new File(
                                    pm.getApplicationInfo(
                                            junkInfo.mPackageName,
                                            PackageManager.GET_META_DATA
                                    ).publicSourceDir
                            ).length();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        long x = 10485760L;
                        long y = 41943040L;
                        Random r = new Random();
                        long number = x + ((long) (r.nextDouble() * (y - x)));
                        junkInfo.mSize = number;
                    }
                    usageList.add(junkInfo);
                }
            }
            usageStats = filter(usageList, context);
        }

        if (isGenerateRandom && usageStats.isEmpty()) {
            usageStats = filter(getRandomJunkApps(context), context);
        }

        return usageStats;
    }

    private static String getAppNameFromPackage(String packageName) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName == packageName)
                return app.activityInfo.loadLabel(pm).toString();
        }

        return "no name";
    }

    public static List<JunkInfo> getRandomJunkApps(Context context) {
        pm = context.getPackageManager();
        List<JunkInfo> usageList = new ArrayList<>();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (!packageInfo.packageName.equals(context.getPackageName())) {
                JunkInfo junkInfo = new JunkInfo();
                junkInfo.name = (packageInfo.name == null) ?
                        packageInfo.packageName : packageInfo.name;
                if (junkInfo.name.equals(""))
                    junkInfo.name = packageInfo.packageName;
                junkInfo.mPackageName = packageInfo.packageName;
                long x = 10485760L;
                long y = 41943040L;
                Random r = new Random();
                long number = x + ((long) (r.nextDouble() * (y - x)));
                junkInfo.mSize = number;
                usageList.add(junkInfo);
            }
        }

        return generateRandomData(filter(usageList, context));
    }

    private static List<JunkInfo> generateRandomData(List<JunkInfo> installApps) {
        List<JunkInfo> usageList = new ArrayList<>();

        int allSize = new Random().nextInt(installApps.size() / 2);
        if (allSize > 32)
            allSize = 32;
        for (int i = 0; i < allSize; i++) {
            try {
                usageList.add(installApps.get(new Random().nextInt(installApps.size())));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        usageList = new ArrayList(new HashSet(usageList));
        return usageList;
    }

    public static List<JunkInfo> filter(List<JunkInfo> list, Context context) {
        if (pm == null)
            pm = context.getPackageManager();
        List<JunkInfo> newlist = new ArrayList<>();

        for (JunkInfo stats : list) {
            if (!isSystemApp(stats.mPackageName) && stats.mSize > 0)
                if (!stats.mPackageName.equals("") && !stats.name.equals("") && stats.mSize != -1) {
                    try {
                        Drawable icon = pm.getApplicationIcon(stats.mPackageName);
                        newlist.add(stats);
                    } catch (Exception e) {
                    }

                }
        }
        return deleteTheSameApps(newlist);
    }

    private static List<JunkInfo> deleteTheSameApps(List<JunkInfo> list) {
        return new ArrayList(new HashSet(list));
    }

    public static boolean isSystemApp(String packageName) {
        try {
            // Get packageinfo for target application
            PackageInfo targetPkgInfo = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys = pm.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

}
