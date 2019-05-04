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
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimerService extends Service {
    RemoteViews controlsSmall;
    RemoteViews controlsBig;
    int recipeID;
    List<Method> steps;
    int currentStep;
    int currentRemaining;
    private boolean isRunning = false;
    private Notification.Builder builder;
    private NotificationManagerCompat notificationManager;
    private boolean firstTime = true;
    private boolean paused;
    private BroadcastReceiver receiver;
    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not needed.
        return null;
    }

    /**
     * When service is created.
     */
    @Override
    public void onCreate() {
        // Instatiate member variables.
        builder = new Notification.Builder(this);
        notificationManager = NotificationManagerCompat.from(this);
        controlsSmall = new RemoteViews(this.getPackageName(), R.layout.timer_notification_small);
        controlsBig = new RemoteViews(this.getPackageName(), R.layout.timer_notification_big);

        // Dynamically define broadcast receiver for pending intents, allowing use of controls.
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
        // Register broadcast receiver.
        registerReceiver(receiver, filter);

    }

    /**
     * Called when this service is started (timer starts).
     * @param intent of creating.
     */
    @Override
    public void onStart(Intent intent, int startId) {
        // If no intent specified, stop.
        if (intent == null) {
            stopSelf();
            return;
        }
        // If already timing something else, stop.
        if (isRunning)
            return;

        // When started, set running to true.
        isRunning = true;
        // Retrieve recipe ID from intent.
        if (intent.hasExtra("RECIPE_ID")) {
            recipeID = intent.getExtras().getInt("RECIPE_ID");
        } else {
            stopSelf();
            return;
        }

        paused = false;

        // Get steps for this recipe.
        steps = Method.getMethodList(recipeID);
        // If no steps, stop.
        if (steps.size() == 0) {
            stopSelf();
            return;
        }

        // Start at 0th step, and calculate step time.
        currentStep = 0;
        currentRemaining = (int) (steps.get(currentStep).getTime() * 60);

        // Delete previous notification
        notificationManager.cancel(0);

        // Create intents for notification controls.
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

        // Set pending intents for each button.
        controlsSmall.setOnClickPendingIntent(R.id.previous_step, prevPendingIntent);
        controlsSmall.setOnClickPendingIntent(R.id.play_pause, pausePendingIntent);
        controlsSmall.setOnClickPendingIntent(R.id.next_step, nextPendingIntent);

        controlsBig.setOnClickPendingIntent(R.id.previous_step, prevPendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.play_pause, pausePendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.next_step, nextPendingIntent);
        controlsBig.setOnClickPendingIntent(R.id.stop, stopPendingIntent);

        // Set icons for each button.
        controlsSmall.setImageViewResource(R.id.previous_step, R.drawable.ic_baseline_skip_previous_24px);
        controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        controlsSmall.setImageViewResource(R.id.next_step, R.drawable.ic_baseline_skip_next_24px);

        controlsBig.setImageViewResource(R.id.previous_step, R.drawable.ic_baseline_skip_previous_24px);
        controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        controlsBig.setImageViewResource(R.id.next_step, R.drawable.ic_baseline_skip_next_24px);
        controlsBig.setImageViewResource(R.id.stop, R.drawable.ic_baseline_close_24px);

        // Create thread for timer (prevent hanging).
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Whilst timer is still allowed to run.
                while (isRunning) {
                    // Generate notification and send it.
                    sendTimerNotification();
                    // If current step time runs out.
                    if (currentRemaining <= 0) {
                        // Increment step number.
                        currentStep++;
                        // If step is still within range of steps.
                        if (currentStep < steps.size()) {
                            // Set timer (in seconds) to step time.
                            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
                            // Re-enable notification sound for 1st 'tick'.
                            firstTime = true;
                        } else {
                            break;
                        }
                    }
                    // As long as we aren't paused, decrease current time by 1 second.
                    if (!paused) {
                        currentRemaining--;
                    }
                    // Wait for a second.
                    SystemClock.sleep(1000);
                }
                // When loop ends, close this service gracefully.
                stopSelf();
            }
        }).start();
    }

    /**
     * Create notification and update it's info.
     */
    public void sendTimerNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // If this is the 1st notification.
            if (firstTime) {
                // Set all settings, and disable 'alert only once'.
                builder
                        .setChannelId("channel")
                        .setCustomContentView(controlsSmall)
                        .setCustomBigContentView(controlsBig)
                        .setStyle(new Notification.DecoratedCustomViewStyle())
                        .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24px)
                        .setOnlyAlertOnce(false);
                firstTime = false;
            } else {
                builder.setOnlyAlertOnce(true); // do not make sound each second.
            }

            // Set current step string.
            controlsSmall.setTextViewText(R.id.step_text, steps.get(currentStep).getStep());
            controlsBig.setTextViewText(R.id.step_text, steps.get(currentStep).getStep());

            // Calculate time remaining
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date((long) (currentRemaining * 1000));
            String formattedTime = dateFormat.format(date);

            // Set time remaining string (HH:mm:ss).
            controlsSmall.setTextViewText(R.id.time_text, formattedTime);
            controlsBig.setTextViewText(R.id.time_text, formattedTime);

            // Build notification and send it.
            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }

    /**
     * Send notification to show the timer has finished.
     */
    private void sendFinishedNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Disable alerting only once, and change view to normal notification.
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

    /**
     * When service is destroyed.
     */
    @Override
    public void onDestroy() {
        // Set running flag to false.
        isRunning = false;
        // Send notification for timer being over.
        sendFinishedNotification();
        // If receiver is set, unregister it.
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    /**
     * Set current step to previous step.
     */
    void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
        }
    }

    /**
     * Toggle the timer being paused/playing.
     */
    void playPause() {
        // Toggle boolean.
        paused = !paused;
        // Currently paused means show play icon.
        if (paused) {
            controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_play_arrow_24px);
            controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_play_arrow_24px);
        }
        // Currently playing means show pause icon.
        else {
            controlsSmall.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
            controlsBig.setImageViewResource(R.id.play_pause, R.drawable.ic_baseline_pause_24px);
        }
    }

    /**
     * Set current step to next step.
     */
    void nextStep() {
        if (currentStep < steps.size() - 1) {
            currentStep++;
            currentRemaining = (int) (steps.get(currentStep).getTime() * 60);
        }

    }

    /**
     * Stop timer.
     */
    void closeTimer() {
        currentRemaining = -1;
        stopSelf();
    }
}