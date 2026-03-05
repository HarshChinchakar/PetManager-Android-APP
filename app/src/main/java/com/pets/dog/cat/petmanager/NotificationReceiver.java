package com.pets.dog.cat.petmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Get the pet's name
        String petName = intent.getStringExtra("petName");
        if (petName == null) petName = "Your Pet";

        // 2. Our Premade Templates
        String[] templates = {
                "It's time for " + petName + "'s meal! 🍲",
                "Did " + petName + " get their medication today? 💊",
                "Time for a walk or some playtime with " + petName + "! 🎾",
                "Don't forget to check " + petName + "'s water bowl! 💧",
                "Just a quick reminder to check in on " + petName + " today. 🐾"
        };

        // 3. Pick a random template
        String message = templates[new Random().nextInt(templates.length)];

        // 4. Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "pet_alerts")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("🐾 " + petName + "'s Reminder")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 5. Safely show it (Android 13+ permission check)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {

            // Use pet's name hash as ID so different pets get their own separate notifications
            notificationManager.notify(petName.hashCode(), builder.build());
        }
    }
}