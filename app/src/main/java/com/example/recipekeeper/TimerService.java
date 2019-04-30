package com.example.recipekeeper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class TimerService extends Service {
    public TimerService () {
    }

    private boolean isRunning = false;

    private Notification.Builder builder;
    private NotificationManagerCompat notificationManager;
    private boolean firstTime = true;

    private boolean paused;

    RemoteViews controlsSmall;
    RemoteViews controlsBig;

    private BroadcastReceiver receiver;

    int recipeID;
    List<Method> steps;
    int currentStep;
    int currentRemaining;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        builder = new Notification.Builder(this);
        notificationManager = NotificationManagerCompat.from(this);
        controlsSmall = new RemoteViews(this.getPackageName(), R.layout.timer_notification_small);
        controlsBig = new RemoteViews(this.getPackageName(), R.layout.timer_notification_big);

        IntentFilter filter = new IntentFilter();

        filter.addAction("com.example.recipekeeper.PREV");
        filter.addAction("com.example.recipekeeper.PAUSE");
        filter.addAction("com.example.recipekeeper.NEXT");
        filter.addAction("com.example.recipekeeper.STOP");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "com.example.recipekeeper.PREV":
                        TimerService.this.previousStep();
                        break;
                    case "com.example.recipekeeper.PAUSE":
                        TimerService.this.playPause();
                        break;
                    case "com.example.recipekeeper.NEXT":
                        TimerService.this.nextStep();
                        break;
                    case "com.example.recipekeeper.STOP":
                        TimerService.this.closeTimer();
                        break;
                    default:
                        break;
                }
            }
        };

        registerReceiver(receiver, filter);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent == null) {
            stopSelf();
            return;
        }
        if (isRunning)
            return;

        isRunning = true;
        if (intent.hasExtra("RECIPE_ID")) {
            recipeID = intent.getExtras().getInt("RECIPE_ID");
        }
        else {
            stopSelf();
            return;
        }

        paused = false;

        steps = Method.getMethodList(recipeID);
        if (steps.size() == 0) {
            stopSelf();
            return;
        }

        currentStep = 0;
        currentRemaining = (int) (steps.get(currentStep).getTime() * 60);

        // Delete previous notification
        notificationManager.cancel(0);

        controlsSmall.setImageViewResource(R.id.previous_step, R.drawable.ic_baseline_skip_previous_24px);
        controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        controlsSmall.setImageViewResource(R.id.next_step, R.drawable.ic_baseline_skip_next_24px);


        Intent prevIntent = new Intent("com.example.recipekeeper.PREV");
        Intent pauseIntent = new Intent("com.example.recipekeeper.PAUSE");
        Intent nextIntent = new Intent("com.example.recipekeeper.NEXT");
        Intent stopIntent = new Intent("com.example.recipekeeper.STOP");

        PendingIntent prevPendingIntent =
                PendingIntent.getBroadcast(this, 0, prevIntent, 0);
        PendingIntent pausePendingIntent =
                PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        PendingIntent stopPendingIntent =
                PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        controlsSmall.setOnClickPendingIntent(R.id.previous_step, prevPendingIntent);
        controlsSmall.setOnClickPendingIntent(R.id.play_pause, pausePendingIntent);
        controlsSmall.setOnClickPendingIntent(R.id.next_step, nextPendingIntent);


        controlsBig.setImageViewResource(R.id.previous_step, R.drawable.ic_baseline_skip_previous_24px);
        controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        controlsBig.setImageViewResource(R.id.next_step, R.drawable.ic_baseline_skip_next_24px);
        controlsBig.setImageViewResource(R.id.stop, R.drawable.ic_baseline_close_24px);

        controlsBig.setOnClickPendingIntent(R.id.previous_step, prevPendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.play_pause, pausePendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.next_step, nextPendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.stop, stopPendingIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    sendTimerNotification();
                    if (currentRemaining <= 0) {
                        currentStep++;
                        if (currentStep < steps.size())
                        {
                            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
                            firstTime = true;

                        }
                        else
                        {
                            break;
                        }
                    }
                    if (!paused) {
                        currentRemaining--;
                    }
                    SystemClock.sleep(1000);
                }
                stopSelf();
            }
        }).start();
    }

    public void sendTimerNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (firstTime) {
                builder
                        .setChannelId("channel")
                        .setCustomContentView(controlsSmall)
                        .setCustomBigContentView(controlsBig)
                        .setStyle(new Notification.DecoratedCustomViewStyle())
                        .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24px)
                        .setOnlyAlertOnce(false);
                firstTime = false;
            }
            else
            {
                builder.setOnlyAlertOnce(true);
            }

            // Set current step string
            controlsSmall.setTextViewText(R.id.step_text, steps.get(currentStep).getStep());

            controlsBig.setTextViewText(R.id.step_text, steps.get(currentStep).getStep());

            // Set time remaining string
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = new Date((long) (currentRemaining*1000));
            String formattedTime = dateFormat.format(date);

            controlsSmall.setTextViewText(R.id.time_text, formattedTime);

            controlsBig.setTextViewText(R.id.time_text, formattedTime);

            Notification notification = builder
                    .build();

            notificationManager.notify(1, notification);
        }
    }

    private void sendFinishedNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification notification = builder
                        .setChannelId("channel")
                        .setCustomContentView(null)
                        .setCustomBigContentView(null)
                        .setContentTitle("Recipe Storer")
                        .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24px)
                        .setOnlyAlertOnce(false)
                        .setContentText("Timer Complete!")
                        .build();

            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        sendFinishedNotification();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }


    void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
        }
    }

    void playPause() {
        paused = !paused;
        if (paused) {
            controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_play_arrow_24px);
            controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_play_arrow_24px);
        }
        else {
            controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
            controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        }
    }

    void nextStep() {
        if (currentStep < steps.size() - 1) {
            currentStep++;
            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
        }

    }

    void closeTimer() {
        currentRemaining = -1;
        stopSelf();
    }
}