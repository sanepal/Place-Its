package com.ucsd.cs110w.group16.placeits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMapClickListener, OnMarkerClickListener {
    private GoogleMap map;
    private Marker searchResult = null;
    private MenuItem searchItem;    
    private CameraPositionStore mPrefs;
    private PlaceItManager placeItManager;
    
    ArrayList<Integer> alarmCancelList;	// List to hold Id's of alarms to cancel.
    BroadcastReceiver alarmReceiver;	// Receiver to receive alarms.
    
    /*
     * An instance of an inner class that receives broadcasts from listeners and from the
     * IntentService that receives geofence transition events
     */
    private GeofenceSampleReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        mPrefs = new CameraPositionStore(this);
        placeItManager = new PlaceItManager(this);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        handleIntent(getIntent());
        
        /**
         * BroadcastReceiver to receive alarms and determine what to do when an alarm is received
         * Add what should be done when the alarm is received in here.
         */
        alarmReceiver = new BroadcastReceiver() {
            @Override
			public void onReceive(Context c, Intent i) {
            	boolean flag = true;
            	int alarmToCancel = i.getIntExtra("myId",0);	//Gets the ID from the intent
            	Iterator<Integer> myIterator = alarmCancelList.iterator();
            	while(myIterator.hasNext())						//Goes through list to see if ID from
            	{												//intent received matches any of the Integers
            		Integer IntCancel = myIterator.next();		//in the alarms to cancel list
            		if( (int) IntCancel == alarmToCancel)
            		{
            			flag = false;							//cancel this alarm.
            		}
            	}
            	if (flag)
            	{	
            		/* Put command to trigger when alarm goes off here */
            	}
			}

        };
        registerReceiver(alarmReceiver, new IntentFilter("com.ucsd.cs110w.group16.placeits") );
    }
    
    /**
     * Calls the activity to set up a scheduling alarm, which is the weekly alarm. Sets the PlaceIt
     * at 11:59:59 PM the day before the PlaceIt should appear.
     * @param Id the Id of the place it to set a schedule for.
     */
    public void setupSchedulingAlarm(Integer Id)
    {
    	Intent alarmActivity = new Intent(this, SchedulingAlarmActivity.class);
		alarmActivity.putExtra("com.ucsd.cs110w.group16.placeits.Id",Id);
		startActivity(alarmActivity);
    }
    
    /**
     * Calls the activity to set up a repost alarm, which has 4 options: 1 day, 2 days, 1 week, 2 weeks.
     * @param Id the Id of the place it to repost.
     */
    public void setupRepostAlarm(Integer Id)
    {
    	Intent alarmActivity = new Intent(this, RepostAlarmActivity.class);
		alarmActivity.putExtra("com.ucsd.cs110w.group16.placeits.Id",Id);
		startActivity(alarmActivity);
    }
    
    /**
     * Adds the id to cancel to the list of alarms to cancel, so that when the broadcast with that Id
     * is received, it will ignore it.
     * @param Id the Id of the alarm to cancel.
     */
    public void cancelAlarm(Integer Id)
	{
		alarmCancelList.add(Id);
	}
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(mPrefs.getCameraPosition()));
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPrefs.setCameraPosition(map.getCameraPosition());
    }
    
    @Override
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     * Required so that the search intent does not start a new MainActivity
     */
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    
    /*
     * handle the search intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            GetAddressResults loadplaces = new GetAddressResults(this);
            searchItem.collapseActionView();
            loadplaces.execute(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_placeits:
            displayPlaceitsList();
            return true;
        }
        return false;
    }    
    
    /*
     * Receives search results from our async task and displays them
     */
    public void receiveSearchResults(final List<Address> results) {
        String arrResult[] = new String[results.size()];
        for (int j = 0; j < results.size(); j++) {
            Address result = results.get(j);
            StringBuilder strResult = new StringBuilder();
            for (int i = 0; i < result.getMaxAddressLineIndex(); i++) {
                strResult.append(result.getAddressLine(i)).append("\n");
            }
            arrResult[j] = strResult.toString();
        }
        //builds an alert dialog to display search results and handle clicking on search results
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.basic_textview, arrResult);
        builder.setTitle("Select the location");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveCamera(results.get(which).getLatitude(), results.get(which)
                        .getLongitude());
                if (!(searchResult == null)) searchResult.remove();
                searchResult = map.addMarker(new MarkerOptions()
                                .position(
                                        new LatLng(results.get(which)
                                                .getLatitude(), results.get(
                                                which).getLongitude()))
                                .title(results.get(which).getFeatureName() != null ? results
                                        .get(which).getFeatureName() : results
                                        .get(which).getAddressLine(0)));
                searchResult.showInfoWindow();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    /*
     * Move camera to the passed in latitude, longitude
     */
    protected void moveCamera(double latitude, double longitude) {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,
                longitude)));
    }

    @Override
    public void onMapClick(final LatLng location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showInputDialog(location);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code that is executed when clicking NO
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    /*
     * (non-Javadoc)
     * @see com.google.android.gms.maps.GoogleMap.OnMarkerClickListener#onMarkerClick(com.google.android.gms.maps.model.Marker)
     * Override the onMarkerClick so that we can show our own views for when the user clicks on a marker
     * Clicking on a marker that was the result of a search brings up new Place-It creation prompt
     * TODO need to implement showing PlaceIt info with Delete/Repost options
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(searchResult)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Create Place It for " + marker.getTitle() + "?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showInputDialog(marker.getPosition());
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code that is executed when clicking NO
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            marker.remove();
            searchResult = null;
            return true;
        }
        else
            return false;
    }    
    
    /*
     * Shows the input dialog for Place it creation with regards to details about the PLace it
     */
    private void showInputDialog(final LatLng location) {
        // Preparing views
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // the edit text's are part of the relative layout viewgroup in the
        // layout file
        View layout = inflater.inflate(R.layout.input_details,
                (ViewGroup) findViewById(R.id.input_root));
        final EditText inputTitle = (EditText) layout
                .findViewById(R.id.input_title);
        final EditText inputDesc = (EditText) layout
                .findViewById(R.id.input_desc);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        map.addMarker(new MarkerOptions().position(location)
                                .title(inputTitle.getText().toString())
                                .snippet(inputDesc.getText().toString()));
                        // TODO get values from dropdown boxes
                        // TODO check inputs for empty values
                        placeItManager.registerPlaceIt(inputTitle.getText().toString(),
                                inputDesc.getText().toString(), location);
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void registerPlaceit(String title, String desc, LatLng location) {
        Toast.makeText(getApplicationContext(),
                "Place it created: " + title + " " + desc, Toast.LENGTH_LONG)
                .show();
        // TODO implement registerPlaceit
        // register the place it
    }
    
    
    
    /*
     * Called when the "My Place It's" button is clicked, opens up activity to view place its
     */
    private void displayPlaceitsList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
    
    /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Sets up an alarm for scheduling weekly events
     */

}
