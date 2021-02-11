package com.example.addabazi.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.addabazi.MainActivity;
import com.example.addabazi.R;
import com.example.addabazi.chat.ChatListFragment;
import com.example.addabazi.chat.ChatsActivity;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Util.updateToken(this,s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("tag"," On Received Notification method");
        String notificationTitle = remoteMessage.getData().get(Constants.NOTIFICATION_TITLE);
        String notificationMessage = remoteMessage.getData().get(Constants.NOTIFICATION_MESSAGE);
        Uri notificationRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_ID,Constants.NOTIFICATION_NAME,NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(Constants.NOTIFICATION_DESC);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder = new NotificationCompat.Builder(this,Constants.NOTIFICATION_ID);
        }
        else{
            notificationBuilder = new NotificationCompat.Builder(this);
        }


            notificationBuilder.setContentTitle(notificationTitle);
            notificationBuilder.setSmallIcon(R.drawable.ic_app_logo);
            notificationBuilder.setColor(getColor(R.color.yellow));
            notificationBuilder.setSound(notificationRingtoneUri);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setContentIntent(pendingIntent);
        if(notificationMessage.startsWith("https://firebasestorage.")){
            try{
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                Glide.with(this).asBitmap().load(notificationMessage)
                        .into(new CustomTarget<Bitmap>(200,100) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bigPictureStyle.bigPicture(resource);
                                notificationBuilder.setStyle(bigPictureStyle);
                                notificationManager.notify(1,notificationBuilder.build());
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
            catch (Exception e){

            }
        }
        else{
            notificationBuilder.setContentText(notificationMessage);
            notificationManager.notify(1,notificationBuilder.build());
        }




    }
}