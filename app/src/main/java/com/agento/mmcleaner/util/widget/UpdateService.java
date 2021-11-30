package com.agento.mmcleaner.util.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.agento.mmcleaner.R;
import com.agento.mmcleaner.util.shared.LocalSharedUtil;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Random rand = new Random();
        int  number = rand.nextInt(50) + 1;

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.simple_widget);
        view.setTextViewText(R.id.counter, String.valueOf(number));
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        long currentTimestampInSec = cal.getTimeInMillis() / 1000;
        int day = 86400;
        int two_day = 172800;

        long firstTimestampInSec = LocalSharedUtil.getParameterTime(LocalSharedUtil.SHARED_TIME, this);
        if(currentTimestampInSec <= firstTimestampInSec + day){
            view.setImageViewResource(R.id.imageView2,R.drawable.ic_4);
        }else if (currentTimestampInSec >= firstTimestampInSec + day&&currentTimestampInSec <= firstTimestampInSec + two_day){
            view.setImageViewResource(R.id.imageView2,R.drawable.ic_3);
        }else if (currentTimestampInSec <= firstTimestampInSec){
            view.setImageViewResource(R.id.imageView2,R.drawable.ic_2);
        }else if (currentTimestampInSec >= firstTimestampInSec + two_day){
            view.setImageViewResource(R.id.imageView2,R.drawable.ic_1);
        }else {
            view.setImageViewResource(R.id.imageView2,R.drawable.ic_1);
        }
        ComponentName theWidget = new ComponentName(this, SimpleWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(theWidget, view);

        return super.onStartCommand(intent, flags, startId);
    }
}
