package com.example.mszip.ui.foglal;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mszip.R;

public class ReminderReceiver extends BroadcastReceiver {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        String serviceName = intent.getStringExtra("serviceName");
        String date = intent.getStringExtra("date");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "FOGLALAS_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_date)
                .setContentTitle("Holnapi foglalás")
                .setContentText("Ne felejtsd: holnap van a(z) " + serviceName + " foglalásod! (" + date + ")")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}