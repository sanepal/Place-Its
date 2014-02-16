package com.ucsd.cs110w.group16.placeits;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiveNotificationEvents extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlaceItManager placeItManager = new PlaceItManager(context);
        String action = intent.getAction();

        if (action.equals("com.ucsd.cs110w.group16.placeits.snooze")) {
            //TODO implement snooze
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

}