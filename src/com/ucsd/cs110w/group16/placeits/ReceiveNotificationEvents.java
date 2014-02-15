package com.ucsd.cs110w.group16.placeits;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiveNotificationEvents extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlaceItManager placeItManager = new PlaceItManager(context);
        String action = intent.getAction();
        Log.d("l", "in broadcast");
        if (action.equals("com.ucsd.cs110w.group16.placeits.dismiss"))
            placeItManager.setInActive(placeItManager.getPlaceIt((long) intent
                    .getExtras().getInt("PlaceItId")));

        else if (action.equals("com.ucsd.cs110w.group16.placeits.snooze")) {
            //TODO implement snooze
        }
    }

}