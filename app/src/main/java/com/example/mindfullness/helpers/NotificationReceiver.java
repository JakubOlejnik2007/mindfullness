package com.example.mindfullness.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.mindfullness.R;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Tutaj możesz wyświetlić powiadomienie za pomocą NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Utwórz kanał powiadomień (dla Androida 8.0 i nowszych)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Mindfull", "Nazwa kanału", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Mindfull")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Tytuł powiadomienia")
                .setContentText("Treść powiadomienia")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Log.d("Notification", "Executed");
        notificationManager.notify(2137, builder.build());
    }
}
