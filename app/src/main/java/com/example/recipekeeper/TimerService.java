package com.example.recipekeeper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimerService extends Service {
    public TimerService () {
    }

    private Notification.Builder builder;
    private boolean firstTime = true;

    int secondsRemaining;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        builder = new Notification.Builder(this);
    }

    public void sendTimerNotification(int seconds) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (firstTime) {
                builder
                        .setChannelId("channel")
                        .setContentTitle("Time Remaining")
                        .setSmallIcon(R.drawable.ic_baseline_star_24px) // possibly replace with hourglass?
                        .setOnlyAlertOnce(true);
                firstTime = false;
            }

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = new Date((long) (seconds*1000));
            String formattedTime = dateFormat.format(date);

            Notification notification = builder
                    .setContentText(formattedTime)
                    .build();

            notificationManager.notify(0, notification);
        }
    }

    private void sendFinishedNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification notification = builder
                        .setChannelId("channel")
                        .setContentTitle("Recipe Storer")
                        .setSmallIcon(R.drawable.ic_baseline_star_24px) // possibly replace with hourglass?
                        .setOnlyAlertOnce(false)
                        .setContentText("Timer Complete!")
                        .build();

            notificationManager.notify(0, notification);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        secondsRemaining = intent.getExtras().getInt("SECONDS");
        // For time consuming an long tasks you can launch a new thread here...
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (secondsRemaining > 0) {
                    sendTimerNotification(--secondsRemaining);
                    SystemClock.sleep(1000);
                }
                stopSelf();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        sendFinishedNotification();
    }
}