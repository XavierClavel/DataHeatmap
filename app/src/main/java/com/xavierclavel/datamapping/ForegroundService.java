package com.xavierclavel.datamapping;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ForegroundService extends Service {

    public static JobScheduler scheduler;
    public static ForegroundService instance;
    static int wiFiJobId = 112;
    static int locationJobId = 113;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    static boolean isjobLocationOver = false;
    static boolean isMobileNetworkJobOver = false;
    public static boolean shouldReschedule = false;
    static PendingIntent pendingIntent;

    public static String notificationTitle = "Scanning in progress...";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("foreground service", "start command");
        instance = this;
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Network Scanner")
                .setContentText("Scan will begin shortly")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread

        notificationTitle = "Scanning in progress...";
        updateNotification();

        scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        shouldReschedule = true;

        Log.d("foreground service", "here");
        // Lancer ici le job de monitoring dans une async task
        scheduleJobs();
        return START_NOT_STICKY;
    }

    public static void updateNotification() {
        Notification notification = new NotificationCompat.Builder(instance, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(notificationTitle)
                .setContentText(HeatmapManager.nbPoints + " measurements")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(instance);
        notificationManager.notify(1, notification);
    }

    public static void scheduleJobs(){
        MainActivity.Animate();
        scheduleJobWiFi();
        scheduleJobLocation();
    }

    public static void scheduleJobWiFi() {  //TODO : rename
        Log.d("foreground service", "about to start job");
        ComponentName serviceName = new ComponentName(instance, MobileNetworkJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(wiFiJobId, serviceName)
                .setMinimumLatency(5000)
                .build();
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("foreground service", "success");
        }
    }

    public static void scheduleJobLocation() {
        Log.d("foreground service", "about to start location job");
        ComponentName serviceName = new ComponentName(instance, LocationJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(locationJobId, serviceName)
                .setMinimumLatency(5000)
                .build();
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("foreground service", "success");
        }
    }

    public static void JobMobileNetworkOver() {
        if (isjobLocationOver) {
            isjobLocationOver = false;
            if (shouldReschedule) {
                scheduleJobs();
            }
        }
        else {
            isMobileNetworkJobOver = true;
        }
    }

    public static void JobLocationOver() {
        if (isMobileNetworkJobOver) {
            isMobileNetworkJobOver = false;
            if (shouldReschedule) {
                scheduleJobs();
            }
        }
        else {
            isjobLocationOver = true;
        }
    }

    public static void displayToast() {
        Toast.makeText(instance, "scan successful", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    public static void stopService() {
        instance.stopForeground(true);
        shouldReschedule = false;

        instance.stopSelf();
    }

    public static void cancelNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(instance);
        notificationManager.cancel(1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //unregisterReceiver(BluetoothJobService.receiver);   //avoid leaking
        Log.d("foreground service", "foreground service destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
