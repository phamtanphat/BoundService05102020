package com.example.boundservice05102020;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class MyBoundService extends Service {

    NotificationManager mNotificationManager;
    Notification mNotification;
    String CHANNEL_ID = "MY_CHANNEL";
    int mProgress = 0;
    Random mRandom;
    IBinder mMyBind;
    OnListenProgress mOnListenProgress;
    boolean isRunning = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyBind = new MyBind();
        mRandom = new Random();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = createNotification(this, CHANNEL_ID, mProgress);
        startForeground(1, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning == false){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mProgress < 100) {
                        isRunning = true;
                        try {
                            mProgress += mRandom.nextInt(10) + 1;
                            if (mOnListenProgress != null){
                                mOnListenProgress.onProgressChange(mProgress);
                            }
                            mNotification = createNotification(getApplicationContext(), CHANNEL_ID, mProgress);
                            mNotificationManager.notify(1, mNotification);
                        } catch (Exception e) {
                            Log.d("BBB", e.getMessage());
                        }
                        handler.postDelayed(this::run, 1000);
                    } else {
                        isRunning = false;
                        stopSelf();
                    }
                }
            }, 1000);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    private Notification createNotification(Context context, String channelId, int progress) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        123,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder builder =
                new NotificationCompat
                        .Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setShowWhen(true)
                        .setContentTitle("Down load")
                        .addAction(R.mipmap.ic_launcher, "Open app", pendingIntent)
                        .setProgress(100, progress, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    "MY_APP",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        return builder.build();
    }

    class MyBind extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }

    public void getProgress(OnListenProgress mOnListenProgress) {
        this.mOnListenProgress = mOnListenProgress;
    }
}
