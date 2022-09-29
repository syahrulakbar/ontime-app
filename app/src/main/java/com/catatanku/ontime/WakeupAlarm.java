package com.catatanku.ontime;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class WakeupAlarm extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer() {
        Intent refIntent = new Intent(this,MainActivity.class);
        int notificationId = refIntent.getIntExtra("notificationId", 0);
        String massage = refIntent.getStringExtra("todo");

        // Ketika notifikasi diklik, maka akan mengarah ke mainactivity
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,mainIntent, 0);



        //  persiapan Notif

        refIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"CH1")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("It's Time!")
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFAULT)
                .setContentText(massage)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CH1", "Notifikasi", importance);
            NotificationManager notify = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            notify.createNotificationChannel(channel);

        }


        // notif
        notificationManager.notify(notificationId,notificationBuilder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
