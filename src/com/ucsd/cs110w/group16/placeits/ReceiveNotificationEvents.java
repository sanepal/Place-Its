package com.ucsd.cs110w.group16.placeits;

import java.util.List;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiveNotificationEvents extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlaceItManager placeItManager = new PlaceItManager(context);
        String action = intent.getAction();

        if (action.equals("com.ucsd.cs110w.group16.placeits.snooze")) {
            Log.d("broadcast", "snooze");
            //TODO implement snooze
        }
        
        else if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            List<PlaceIt> activePlaceIts = placeItManager.getActivePlaceIts();
            for(PlaceIt placeIt:activePlaceIts) {
                placeItManager.registerGeofence(placeIt);
            }
        }
           
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

}