package com.example.mszip.ui.foglal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BootReceiver extends BroadcastReceiver {
    public static void scheduleReminder(Context context, String serviceName, String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = sdf.parse(dateString);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            long triggerTime = cal.getTimeInMillis();
            if (triggerTime < System.currentTimeMillis()) return;

            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("date", dateString);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
            if (userId == null) return;

            db.collection("Foglalas")
                    .whereEqualTo("userid", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String idopontId = doc.getString("idopontid");
                            if (idopontId == null) continue;

                            db.collection("Idopont")
                                    .document(idopontId)
                                    .get()
                                    .addOnSuccessListener(idopontDoc -> {
                                        String serviceId = idopontDoc.getString("serviceid");
                                        String date = idopontDoc.getString("date");

                                        db.collection("Service")
                                                .document(serviceId)
                                                .get()
                                                .addOnSuccessListener(serviceDoc -> {
                                                    String serviceName = serviceDoc.getString("name");

                                                    scheduleReminder(context, serviceName, date);
                                                });
                                    });
                        }
                    });
        }
    }
}

