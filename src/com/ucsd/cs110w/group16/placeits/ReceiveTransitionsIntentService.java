package com.ucsd.cs110w.group16.placeits;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class ReceiveTransitionsIntentService extends IntentService {
    
    private PlaceItManager placeItManager;
	private static final String PLACEIT_URI = "http://actraiin.appspot.com/item";
    private PlaceIt r;

    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
        
    }

    /**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This Intent is provided
     * to Location Services (inside a PendingIntent) when you call addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent();        

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent.addCategory(PlaceItUtils.CATEGORY_LOCATION_SERVICES);

        // First check for errors
        if (LocationClient.hasError(intent)) {

            // Get the error code
            int errorCode = LocationClient.getErrorCode(intent);

            // Get the error message
            String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);

            // Log the error
            Log.e(PlaceItUtils.APPTAG, errorMessage);

            // Set the action and error message for the broadcast intent
            broadcastIntent.setAction(PlaceItUtils.ACTION_GEOFENCE_ERROR)
                           .putExtra(PlaceItUtils.EXTRA_GEOFENCE_STATUS, errorMessage);

            // Broadcast the error *locally* to other components in this app
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        // If there's no error, get the transition type and create a notification
        } else {

            // Get the type of transition (entry or exit)
            int transition = LocationClient.getGeofenceTransition(intent);

            // Test that a valid transition was reported
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {
                String transitionType = getTransitionString(transition);

                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size() ; index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                    sendNotification(transitionType, geofenceIds[index]);
                }
                String ids = TextUtils.join(PlaceItUtils.GEOFENCE_ID_DELIMITER,geofenceIds);
                

                //sendNotification(transitionType, ids);

                // Log the transition type and a message
                Log.d(PlaceItUtils.APPTAG,
                        getString(
                                R.string.geofence_transition_notification_title,
                                transitionType,
                                ids));
                Log.d(PlaceItUtils.APPTAG,
                        getString(R.string.geofence_transition_notification_text));

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(PlaceItUtils.APPTAG,
                        getString(R.string.geofence_transition_invalid_type, transition));
            }
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity.
     * @param transitionType The type of transition that occurred.
     *
     */
    private void sendNotification(String transitionType, String ids) {

        // Create an explicit content Intent that starts the main Activity
        Intent notificationIntent =
                new Intent(getApplicationContext(),MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the main Activity to the task stack as the parent
        stackBuilder.addParentStack(MainActivity.class);
        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);
        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        //two intents for dismiss and snooze function receivers
        Intent dismissReceive = new Intent();  
        dismissReceive.setAction("com.ucsd.cs110w.group16.placeits.dismiss");
        PendingIntent pendingIntentDismiss = PendingIntent.getBroadcast(this, 0, dismissReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        
        placeItManager = new PlaceItManager(getApplicationContext());
        PlaceIt activatedPlaceit = placeItManager.getPlaceIt((long) Integer.parseInt(ids));
        placeItManager.setInActive(activatedPlaceit);
        updatePlaceIt(activatedPlaceit);
        // Set the notification contents
        builder.setSmallIcon(R.drawable.ic_placeit)
               .setContentTitle(activatedPlaceit.getTitle())
               .setContentText(activatedPlaceit.getDesc())
               .setContentIntent(notificationPendingIntent)
               //.addAction(R.drawable.ic_snooze, "Snooze", pendingIntentSnooze)
               .addAction(R.drawable.ic_dismiss, "Dismiss", pendingIntentDismiss)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setAutoCancel(true);
        
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
    
	private void updatePlaceIt(PlaceIt p) {
		r = p;
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(PLACEIT_URI);
 
			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			      nameValuePairs.add(new BasicNameValuePair("name",
			    		  r.getTitle() +"; " + MainActivity.mEmail));
			      nameValuePairs.add(new BasicNameValuePair("description",
			    		  r.getDesc()));
			      nameValuePairs.add(new BasicNameValuePair("price",
			    		  "; "+ r.getDesc() + "; " + 
	    			    		  r.getLatitude() + "; " + r.getLongitude() + "; " +
	    			              r.isActive() + "; " + r.isCategory() + "; "+ r.getCategories()+
	    			              "; " + MainActivity.mEmail));
			      nameValuePairs.add(new BasicNameValuePair("product",
			    		  MainActivity.mEmail));
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "put"));
			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			  
			      HttpResponse response = client.execute(post);
			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			      while ((rd.readLine()) != null) {
			      }

			    } catch (IOException e) {
			    }
			}
		};

		t.start();
			
	}
}
