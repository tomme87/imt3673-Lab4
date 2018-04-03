package no.ntnu.tomme87.imt3673.lab4;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Tomme on 03.04.2018.
 */

public class DataUpdateService extends JobService {

    private static final String TAG = "DataUpdateService";
    private static final String CHANNEL_ID = "DataUpdated";
    public static final String PREF_LAST = "LastCheck";
    private int notificationId = 1;

    /**
     * This job checks for new messages since last time we checked.
     *
     * @param params
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters params) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastCheck = sharedPreferences.getLong(PREF_LAST, -1);
        if (lastCheck == -1) { // Should not happen..
            lastCheck = System.currentTimeMillis() / 1000L;
            sharedPreferences.edit().putLong(PREF_LAST, lastCheck).apply();
        }

        Log.d(TAG, "Last check is: " + lastCheck);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Message.DOCUMENT)
                .whereGreaterThan("time", lastCheck)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Task success");
                    if (task.getResult().getDocuments().size() > 0) {
                        notifyUser();
                    }
                }
            }
        });

        sharedPreferences.edit().putLong(PREF_LAST, System.currentTimeMillis() / 1000L).apply();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    /**
     * Sends a notification.
     */
    private void notifyUser() {
        Log.d(TAG, "Notifying user");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId++, mBuilder.build());
    }
}
