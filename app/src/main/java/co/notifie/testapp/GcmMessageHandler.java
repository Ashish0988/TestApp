package co.notifie.testapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by thunder on 04.04.15.
 */

public class GcmMessageHandler extends IntentService {
    String mes;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title");
        showToast();
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                //Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();

                Context context = getApplicationContext();
                Intent intent =   new Intent(context, MainActivity.class);
                PendingIntent contentIntent =  PendingIntent.getActivity(context, 0, intent, 0);

                DisplayMessageActivity.reloadComments();

                Notification noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Notifie")
                        .setContentText(mes)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, noti);
            }
        });

    }
}
