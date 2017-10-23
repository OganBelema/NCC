package com.compunetlimited.ogan.ncc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.compunetlimited.ogan.ncc.actvities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by belema on 9/24/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // DONE: Handle FCM messages here.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        final NotificationCompat.Builder noficationBuilder = new NotificationCompat.Builder(this);
        noficationBuilder.setContentIntent(pendingIntent);
        noficationBuilder.setSmallIcon(R.mipmap.logo);
        noficationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        noficationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        noficationBuilder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (remoteMessage.getData().size() > 0) {

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String imageUrl = remoteMessage.getData().get("image_url");

            noficationBuilder.setContentTitle(title);
            noficationBuilder.setContentText(body);

            Bitmap bitmap_result = getBitmapfromUrl(imageUrl);
            if (bitmap_result != null){
                noficationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap_result).setBigContentTitle(title).setSummaryText(body));
            } else {
                noficationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            }
            notificationManager.notify(0, noficationBuilder.build());
        } else {
            noficationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            noficationBuilder.setContentText(remoteMessage.getNotification().getBody());
            noficationBuilder.setContentIntent(pendingIntent);
            noficationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
            notificationManager.notify(0, noficationBuilder.build());
        }
    }

    private Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}
