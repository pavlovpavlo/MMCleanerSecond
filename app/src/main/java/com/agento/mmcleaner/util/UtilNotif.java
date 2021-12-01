package com.agento.mmcleaner.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.agento.mmcleaner.R;
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity;
import com.agento.mmcleaner.ui.clean.second_clean.SecondCleanActivity;
import com.agento.mmcleaner.ui.splash.SplashActivity;
import com.agento.mmcleaner.util.shared.LocalSharedUtil;

import static android.content.Context.NOTIFICATION_SERVICE;

public class UtilNotif {
    public static void showScheduleNotification(Context context) {

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        setListeners(contentView, context);
        int notificationId = 9075;
        String channelId = "9075";
        String channelName = "cleaner-mm";
        Intent intent = new Intent(context, SplashActivity.class);

        //notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, options);

        //channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel =
                    new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelName);
            channel.enableLights(true);
            channel.setLightColor(context.getResources().getColor(R.color.black));
            channel.enableVibration(false);
            channel.setSound(alarmSound, att);
            notificationManager.createNotificationChannel(channel);
        }

        //build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContent(contentView)
                .setColor(context.getResources().getColor(R.color.black))
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT);

        //show notification
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        //int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        notificationManager.notify(notificationId, notification);
    }

    private static void setListeners(RemoteViews view, Context ctx) {

        Intent radio = new Intent(ctx, FirstScanActivity.class);
        radio.putExtra("notif", "scan");
        PendingIntent pRadio = PendingIntent.getActivity(ctx, 0, radio, 0);
        if (!LocalSharedUtil.isStepOptimized(ctx, LocalSharedUtil.SHARED_FIRST)) {
            view.setImageViewResource(R.id.clear, R.drawable.ic_junk_pass);
        } else {
            view.setImageViewResource(R.id.clear, R.drawable.ic_junk_active);
        }
        view.setOnClickPendingIntent(R.id.clear, pRadio);


        Intent volume = new Intent(ctx, SecondCleanActivity.class);
        volume.putExtra("notif", "speed");
        PendingIntent pVolume = PendingIntent.getActivity(ctx, 1, volume, 0);
        if (!LocalSharedUtil.isStepOptimized(ctx, LocalSharedUtil.SHARED_SECOND)) {
            view.setImageViewResource(R.id.batteru, R.drawable.ic_battery_pass);
        } else {
            view.setImageViewResource(R.id.batteru, R.drawable.ic_battery_active);
        }
        view.setOnClickPendingIntent(R.id.batteru, pVolume);


        Intent reboot = new Intent(ctx, FirstScanActivity.class);
        reboot.putExtra("notif", "battery");
        PendingIntent pReboot = PendingIntent.getActivity(ctx, 5, reboot, 0);
        if (!LocalSharedUtil.isStepOptimized(ctx, LocalSharedUtil.SHARED_THIRD)) {
            view.setImageViewResource(R.id.speed, R.drawable.ic_booster_pass);
        } else {
            view.setImageViewResource(R.id.speed, R.drawable.ic_booster_active);
        }
        view.setOnClickPendingIntent(R.id.speed, pReboot);


        Intent top = new Intent(ctx, SplashActivity.class);
        top.putExtra("notif", "cpu");
        PendingIntent pTop = PendingIntent.getActivity(ctx, 3, top, 0);

        if (Util.cpuTemperature() > 40) {
            view.setImageViewResource(R.id.temperature, R.drawable.ic_temperature_act);
        } else {
            view.setImageViewResource(R.id.temperature, R.drawable.ic_temperature);
        }
        //view.setOnClickPendingIntent(R.id.temperature, pTop);


        Intent lum = new Intent(ctx, SplashActivity.class);
        if (LocalSharedUtil.getParameterInt(LocalSharedUtil.SHARED_LUMUS, ctx) == 0) {
            view.setImageViewResource(R.id.lantern, R.drawable.ic_lantern);
            lum.putExtra("notif", "lum");
            lum.setAction("0");
        } else {
            view.setImageViewResource(R.id.lantern, R.drawable.ic_lantern_active);
            lum.putExtra("notif", "lum_act");
            lum.setAction("1");
        }
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pLum = PendingIntent.getActivity(ctx, 0, lum, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.lantern_l, pLum);

    }
}
