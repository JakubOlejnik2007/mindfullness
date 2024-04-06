package com.example.mindfullness.helpers;

import static java.lang.Math.abs;

import com.example.mindfullness.R;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;

public class NotificationWorker extends Worker {

    public static final String NOTIFICATION_WORK_TAG = "Mindfull";
    private static final String CHANNEL_ID = "notification_channel_id";
    private String[] powiadomienia = {
            "Przypomnij sobie, aby zanurzyć się w chwilę teraźniejszą. Co teraz czujesz? Co teraz widzisz? Przyjmij to z pełną świadomością.",
            "Zapraszam do głębokiego oddechu. Skoncentruj się na wdechu i wydechu. Niech to będzie moment spokoju w Twoim dniu.",
            "Zauważ otoczenie wokół siebie. Coś może Cię zaskoczyć, gdy spojrzysz bardziej uważnie.",
            "Czas na przerwę od ekranu. Zrelaksuj oczy i skup się na oddechu przez kilka chwil.",
            "Spróbuj praktykować wdzięczność. Coś, za co dzisiaj jesteś wdzięczny/a?",
            "Poczuj swoje ciało. Czy masz jakieś napięcia? Może warto wykonać kilka prostych ćwiczeń rozluźniających.",
            "Przypomnij sobie swoje cele i marzenia. Co możesz dzisiaj zrobić, aby się do nich zbliżyć?",
            "Zanurz się w swoich zmysłach. Co słyszysz, czujesz, widzisz, wąchasz, smakujesz?",
            "Zauważ swój stan umysłu. Czy jest spokojny, czy zaniepokojony? Pozwól sobie na chwilę akceptacji.",
            "Przypomnij sobie, że jesteś wystarczająco dobrym/a takim, jaki/a jesteś. Akceptuj siebie bezwarunkowo."
    };

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendNotification();
        return Result.success();
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Random random = new Random();
        int NOTIFICATION_ID = random.nextInt();

        String message = powiadomienia[abs(random.nextInt()) % 10];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Be Mindfull")
                .setContentText(String.format("%s", message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format("%s", message)));

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
