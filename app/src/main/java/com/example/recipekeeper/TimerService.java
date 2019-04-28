package com.example.recipekeeper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimerService extends Service {
    public TimerService () {
    }

    private boolean isRunning = false;

    private Notification.Builder builder;
    private NotificationManagerCompat notificationManager;
    private boolean firstTime = true;

    RemoteViews controls;

    int secondsRemaining;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        builder = new Notification.Builder(this);
        notificationManager = NotificationManagerCompat.from(this);
        controls = new RemoteViews(this.getPackageName(), R.layout.timer_notification);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (isRunning)
            return;

        isRunning = true;
        secondsRemaining = intent.getExtras().getInt("SECONDS");

        // Delete previous notification
        notificationManager.cancel(0);
        controls.setTextViewText(R.id.step_text, "Current Step: blah blah");
        controls.setTextViewText(R.id.time_text, "Time Remaining: 01:12");

        controls.setImageViewResource(R.id.previous_step, R.drawable.ic_baseline_skip_previous_24px);
        controls.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        controls.setImageViewResource(R.id.next_step, R.drawable.ic_baseline_skip_next_24px);


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

    public void sendTimerNotification(int seconds) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (firstTime) {
                builder
                        .setChannelId("channel")
                        .setCustomContentView(controls)
                        .setStyle(new Notification.DecoratedCustomViewStyle())
                        .setContentTitle("Time Remaining")
                        .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24px)
                        .setOnlyAlertOnce(true);
                firstTime = false;
            }

            // Set current step string

            controls.setTextViewText(R.id.step_text, "Current Step: " + "test");

            // Set time remaining string
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = new Date((long) (seconds*1000));
            String formattedTime = dateFormat.format(date);

            controls.setTextViewText(R.id.time_text, "Time Remaining: " + formattedTime);

            Notification notification = builder
                    .build();

            notificationManager.notify(0, notification);
        }
    }

    private void sendFinishedNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification notification = builder
                        .setChannelId("channel")
                        .setCustomContentView(null)
                        .setContentTitle("Recipe Storer")
                        .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24px)
                        .setOnlyAlertOnce(false)
                        .setContentText("Timer Complete!")
                        .build();

            notificationManager.notify(0, notification);
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        sendFinishedNotification();
    }
}