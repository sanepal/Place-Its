package com.ucsd.cs110w.group16.placeits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnMapClickListener,
        OnMarkerClickListener, OnConnectionFailedListener {
    private GoogleMap map;
    private Marker searchResult = null;
    private MenuItem searchItem;
    // gets the camera preferences from shared preferences
    private CameraPositionStore cPrefs;
    private PlaceItManager placeItManager;

    private SharedPreferences prefs;
    private Editor prefsEditor;

    private LocationManager locationManager;

    private Criteria criteria;
    private LocationUpdateRequester locationUpdateRequester;
    private LastLocationFinder lastLocationFinder;
    private PendingIntent locationListenerPendingIntent;
    private PendingIntent locationListenerPassivePendingIntent;

    private int selected = 0;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static String mEmail;
    private static boolean firstStart = true;
    
    /*
     * An instance of an inner class that receives broadcasts from listeners and
     * from the IntentService that receives geofence transition events
     */
    private GeofenceSampleReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;
    
	private static final String PLACEIT_URI = "http://actraiin.appspot.com/item";
    private String cName;
    private String cDesc;
    private LatLng cLoc;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a new broadcast receiver to receive updates from the listeners
        // and service
        mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of
        // geofences
        mIntentFilter.addAction(PlaceItUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of
        // geofences
        mIntentFilter.addAction(PlaceItUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing
        // errors
        mIntentFilter.addAction(PlaceItUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(PlaceItUtils.CATEGORY_LOCATION_SERVICES);

        prefs = getSharedPreferences(PlaceItUtils.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        prefsEditor.putBoolean(PlaceItUtils.SP_KEY_RUN_ONCE, true).commit();

        // get reference to manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // specify crteria for requesting updates
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // location update intents
        Intent activeIntent = new Intent(this, LocationChangedReceiver.class);
        locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0,
                activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent passiveIntent = new Intent(this,
                PassiveLocationChangedReceiver.class);
        locationListenerPassivePendingIntent = PendingIntent.getBroadcast(this,
                0, passiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // instantiate last location finder and location update requester

        lastLocationFinder = new LastLocationFinder(this);
        lastLocationFinder
                .setChangedLocationListener(oneShotLastLocationUpdateListener);

        locationUpdateRequester = new LocationUpdateRequester(locationManager);

        cPrefs = new CameraPositionStore(this);
        placeItManager = new PlaceItManager(this);
        setUpMapIfNeeded();
        displayActivePlaceIts();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        handleIntent(getIntent());
        
		new getPlaceIts().execute(PLACEIT_URI);

    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed()
     * in GeofenceRemover and GeofenceRequester may call
     * startResolutionForResult() to start an Activity that handles Google Play
     * services problems. The result of this call returns here, to
     * onActivityResult. calls
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {

        placeItManager.handleActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the user with
             * the error.
             */
            Toast.makeText(this, "FAILURE!", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        prefsEditor.putBoolean(PlaceItUtils.EXTRA_KEY_IN_BACKGROUND, false)
                .commit();
        // Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBroadcastReceiver, mIntentFilter);
        // animate camera to last location
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cPrefs
                .getCameraPosition()), 18, null);
		new getPlaceIts().execute(PLACEIT_URI);

        // out place its one map again
        displayActivePlaceIts();
        getLocationAndUpdatePlaces(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        prefsEditor.putBoolean(PlaceItUtils.EXTRA_KEY_IN_BACKGROUND, true).commit();
        cPrefs.setCameraPosition(map.getCameraPosition());
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onNewIntent(android.content.Intent) Required so
     * that the search intent does not start a new MainActivity
     */
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /*
     * handle the search intent
     */
    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            GetAddressResults loadplaces = new GetAddressResults(this);
            if (searchItem != null)
                searchItem.collapseActionView();
            loadplaces.execute(query);
        } else if (intent.getAction().equals(
                "com.ucsd.cs110w.group16.placeits.launchAppWithPlaceIt")) {
            PlaceIt placeIt = placeItManager.getPlaceIt((long) intent
                    .getExtras().getInt("PlaceItId"));
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(
                            new LatLng(placeIt.getLatitude(), placeIt
                                    .getLongitude())).title(placeIt.getTitle())
                    .snippet(placeIt.getDesc()));
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition(marker.getPosition(),
                            5, 0, 0)), 5, null);
            marker.showInfoWindow();
        }
    }

    private void getLocationAndUpdatePlaces(boolean updateWhenLocationChanges) {
        // This isn't directly affecting the UI, so put it on a worker thread.
        AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Find the last known location, specifying a required accuracy
                // of within the min distance between updates
                // and a required latency of the minimum time required between
                // updates.
                Location lastKnownLocation = lastLocationFinder
                        .getLastBestLocation(PlaceItUtils.MAX_DISTANCE,
                                System.currentTimeMillis()
                                        - PlaceItUtils.MAX_TIME);

                // Update the place list based on the last known location within
                // a defined radius.
                // Note that this is *not* a forced update. The Place List
                // Service has settings to
                // determine how frequently the underlying web service should be
                // pinged. This function
                // is called everytime the Activity becomes active, so we don't
                // want to flood the server
                // unless the location has changed or a minimum latency or
                // distance has been covered.
                // TODO Modify the search radius based on user settings?
                updatePlaces(lastKnownLocation,
                        (int) PlaceItUtils.DEFAULT_RADIUS, false);
                return null;
            }
        };
        findLastLocationTask.execute();

        // If we have requested location updates, turn them on here.
        // toggleUpdatesWhenLocationChanges(updateWhenLocationChanges);
    }

    /**
     * Start listening for location updates.
     */
    protected void requestLocationUpdates() {
        // Normal updates while activity is visible.
        locationUpdateRequester.requestLocationUpdates(PlaceItUtils.MAX_TIME,
                (long) PlaceItUtils.MAX_DISTANCE, criteria,
                locationListenerPendingIntent);

        // Passive location updates from 3rd party apps when the Activity isn't
        // visible.
        locationUpdateRequester.requestPassiveLocationUpdates(
                PlaceItUtils.PASSIVE_MAX_TIME,
                (long) PlaceItUtils.PASSIVE_MAX_DISTANCE,
                locationListenerPassivePendingIntent);

        // Register a receiver that listens for when the provider I'm using has
        // been disabled.
        IntentFilter intentFilter = new IntentFilter(
                PlaceItUtils.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
        registerReceiver(locProviderDisabledReceiver, intentFilter);

        // Register a receiver that listens for when a better provider than I'm
        // using becomes available.
        String bestProvider = locationManager.getBestProvider(criteria, false);
        String bestAvailableProvider = locationManager.getBestProvider(
                criteria, true);
        if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
            locationManager.requestLocationUpdates(bestProvider, 0, 0,
                    bestInactiveLocationProviderListener, getMainLooper());
        }
    }

    /**
     * One-off location listener that receives updates from the
     * {@link LastLocationFinder}. This is triggered where the last known
     * location is outside the bounds of our maximum distance and latency.
     */
    protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
        public void onLocationChanged(Location l) {
            updatePlaces(l, PlaceItUtils.DEFAULT_RADIUS, true);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }
    };

    /**
     * If the best Location Provider (usually GPS) is not available when we
     * request location updates, this listener will be notified if / when it
     * becomes available. It calls requestLocationUpdates to re-register the
     * location listeners using the better Location Provider.
     */
    protected LocationListener bestInactiveLocationProviderListener = new LocationListener() {
        public void onLocationChanged(Location l) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
            // Re-register the location listeners using the better Location
            // Provider.
            requestLocationUpdates();
        }
    };

    /**
     * If the Location Provider we're using to receive location updates is
     * disabled while the app is running, this Receiver will be notified,
     * allowing us to re-register our Location Receivers using the best
     * available Location Provider is still available.
     */
    protected BroadcastReceiver locProviderDisabledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean providerDisabled = !intent.getBooleanExtra(
                    LocationManager.KEY_PROVIDER_ENABLED, false);
            // Re-register the location listeners using the best available
            // Location Provider.
            if (providerDisabled)
                requestLocationUpdates();
        }
    };

    /**
     * Update the list of nearby places centered on the specified Location,
     * within the specified radius. This will start the
     * {@link PlacesUpdateService} that will poll the underlying web service.
     * 
     * @param location
     *            Location
     * @param radius
     *            Radius (meters)
     * @param forceRefresh
     *            Force Refresh
     */
    protected void updatePlaces(Location location, float radius,
            boolean forceRefresh) {
        if (location != null) {
            Log.d(PlaceItUtils.APPTAG, "Updating place list.");
            // Start the PlacesUpdateService. Note that we use an action rather
            // than specifying the
            // class directly. That's because we have different variations of
            // the Service for different
            // platform versions.
            Intent updateServiceIntent = new Intent(this,
                    PlacesUpdateService.class);
            updateServiceIntent.putExtra(PlaceItUtils.EXTRA_KEY_LOCATION,
                    location);
            updateServiceIntent.putExtra(PlaceItUtils.EXTRA_KEY_RADIUS, radius);
            updateServiceIntent.putExtra(PlaceItUtils.EXTRA_KEY_FORCEREFRESH,
                    forceRefresh);
            startService(updateServiceIntent);            
        } else
            Log.d(PlaceItUtils.APPTAG,
                    "Updating place list for: No Previous Location Found");
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
        // builds an alert dialog to display search results and handle clicking
        // on search results
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.basic_textview, arrResult);
        builder.setTitle("Select the location");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveCamera(results.get(which).getLatitude(), results.get(which)
                        .getLongitude());
                if (!(searchResult == null))
                    searchResult.remove();
                searchResult = map.addMarker(new MarkerOptions().position(
                        new LatLng(results.get(which).getLatitude(), results
                                .get(which).getLongitude())).title(
                        results.get(which).getFeatureName() != null ? results
                                .get(which).getFeatureName() : results.get(
                                which).getAddressLine(0)));
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
    	showSingleInputDialog(location);
        /*selected = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.prompt);
        builder.setSingleChoiceItems(R.array.choices, selected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;

                    }
                });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (selected == 0)
                    showSingleInputDialog(location);
                else
                    showCategoricalInputDialog();
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
        alert.show();*/
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gms.maps.GoogleMap.OnMarkerClickListener#onMarkerClick
     * (com.google.android.gms.maps.model.Marker) Override the onMarkerClick so
     * that we can show our own views for when the user clicks on a marker
     * Clicking on a marker that was the result of a search brings up new
     * Place-It creation prompt TODO need to implement showing PlaceIt info with
     * Delete/Repost options
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(searchResult)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Create Place It for " + marker.getTitle() + "?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showSingleInputDialog(marker.getPosition());
                        }
                    });
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
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
        } else
            return false;
    }

    /*
     * Shows the input dialog for Place it creation with regards to details
     * about the PLace it
     */
    private void showSingleInputDialog(final LatLng location) {
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
        builder.setTitle("Enter your details");
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        map.addMarker(new MarkerOptions()
                                .position(location)
                                .title(inputTitle.getText().toString())
                                .snippet(inputDesc.getText().toString())
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_placeit)));
                        placeItManager.registerGeofence((placeItManager
                                .createPlaceIt(inputTitle.getText().toString(),
                                        inputDesc.getText().toString(),
                                        location)));
                        postSinglePlaceIt(inputTitle.getText().toString(),
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
    
	private void postSinglePlaceIt(String name, String desc, LatLng arg) {
		cName = name;
		cDesc = desc;
		cLoc = arg;
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(PLACEIT_URI);
 
			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			      nameValuePairs.add(new BasicNameValuePair("name",
			    		  cName));
			      nameValuePairs.add(new BasicNameValuePair("description",
			    		  cDesc));
			      nameValuePairs.add(new BasicNameValuePair("price",
			    		  cDesc + "; " + 
			    		  cLoc.latitude + "; " + cLoc.longitude + "; " +
			              true + "; " + false + "; "+ "null"));
			      nameValuePairs.add(new BasicNameValuePair("product",
			    		  mEmail));
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "put"));
			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			  
			      HttpResponse response = client.execute(post);
			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			      String line = "";
			      while ((line = rd.readLine()) != null) {
			      }

			    } catch (IOException e) {
			    }
			}
		};

		t.start();
			
	}

    /*
     * Shows the input dialog for Place it creation with regards to details
     * about the PLace it
     */
    private void showCategoricalInputDialog() {
        // Preparing views
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // the edit text's are part of the relative layout viewgroup in the
        // layout file
        View layout = inflater.inflate(R.layout.input_category,
                (ViewGroup) findViewById(R.id.category_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputTitle = (EditText) layout
                .findViewById(R.id.input_title);
        /*final EditText inputDesc = (EditText) layout
                .findViewById(R.id.input_desc);*/
        final Spinner option = (Spinner) layout.findViewById(R.id.option_1);
        final Spinner option2 = (Spinner) layout.findViewById(R.id.option_2);
        final Spinner option3 = (Spinner) layout.findViewById(R.id.option_3);
        builder.setView(layout);
        builder.setTitle("Enter your details");
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = option.getSelectedItem().toString();
                        String input2 = option2.getSelectedItem().toString();
                        String input3 = option3.getSelectedItem().toString();
                        if (input.isEmpty() && input2.isEmpty()
                                && input3.isEmpty()) {
                            Log.d(PlaceItUtils.APPTAG, "none");
                            Toast.makeText(getBaseContext(),
                                    "Select at least 1 category",
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCategoricalInputDialog();
                                }
                            });
                        } else {
                            String categories = input;
                            if (!input2.isEmpty())
                                categories += ", "+input2;
                            if (!input3.isEmpty())
                                categories += ", "+input3;
                            placeItManager.createCategoryPlaceIt(inputTitle.getText().toString(),
                            		 /*inputDesc.getText().toString(),*/categories);
                            postCategoryPlaceIt(inputTitle.getText().toString(), categories);
                            getLocationAndUpdatePlaces(true);
                        }
                        dialog.dismiss();
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
    
	private void postCategoryPlaceIt(String name, String categories) {
		cName = name;
		cDesc = categories;
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(PLACEIT_URI);
 
			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			      nameValuePairs.add(new BasicNameValuePair("name",
			    		  cName));
			      nameValuePairs.add(new BasicNameValuePair("description",
			    		  cDesc));
			      nameValuePairs.add(new BasicNameValuePair("price",
			    		  "none; " + 
			    		  "0; 0; " +
			              true + "; " + true + "; "+ cDesc));
			      nameValuePairs.add(new BasicNameValuePair("product",
			    		  mEmail));
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "put"));
			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			  
			      HttpResponse response = client.execute(post);
			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			      String line = "";
			      while ((line = rd.readLine()) != null) {
			      }

			    } catch (IOException e) {
			    }
			}
		};

		t.start();
			
	}

    /*
     * Called when the "My Place It's" button is clicked, opens up activity to
     * view place its
     */
    private void displayPlaceitsList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
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
        case R.id.category_placeits:
            showCategoricalInputDialog();
            return true;
        }
        return false;
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
        }
    }

    private void displayActivePlaceIts() {
        map.clear();
        List<PlaceIt> activePlaceIts = placeItManager.getActivePlaceIts();
        for (PlaceIt placeIt : activePlaceIts) {
            if (placeIt.isCategory())
                continue;
            map.addMarker(
                    new MarkerOptions()
                            .position(
                                    new LatLng(placeIt.getLatitude(), placeIt
                                            .getLongitude()))
                            .title(placeIt.getTitle())
                            .snippet(placeIt.getDesc()))
                    .setIcon(
                            BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_placeit));
        }

    }

    /**
     * Define a Broadcast receiver that receives updates from connection
     * listeners and the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers This method is
         * invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing
            // geofences
            if (TextUtils.equals(action, PlaceItUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

                // Intent contains information about successful addition or
                // removal of geofences
            } else if (TextUtils.equals(action,
                    PlaceItUtils.ACTION_GEOFENCES_ADDED)
                    || TextUtils.equals(action,
                            PlaceItUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

                // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action,
                    PlaceItUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

                // The Intent contained an invalid action
            } else {
                Log.e(PlaceItUtils.APPTAG,
                        getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action,
                        Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing
         * geofences, put it here.
         * 
         * @param context
         *            A Context for this component
         * @param intent
         *            The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {

        }

        /**
         * Report geofence transitions to the UI
         * 
         * @param context
         *            A Context for this component
         * @param intent
         *            The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the
             * code here. The current design of the app uses a notification to
             * inform the user that a transition has occurred.
             */
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         * 
         * @param intent
         *            A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent
                    .getStringExtra(PlaceItUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(PlaceItUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    
	 private class getPlaceIts extends AsyncTask<String, Void, List<String>> {
		 @Override
	     protected List<String> doInBackground(String... url) {
			 
	    	    HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url[0]);
				List<String> list = new ArrayList<String>();
				try {
					HttpResponse response = client.execute(request);
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity);
					JSONObject myjson;
					

					try {
						myjson = new JSONObject(data);
						JSONArray array = myjson.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							if (obj.get("product").toString().equals(
									mEmail))
							   list.add(obj.get("name").toString() + 
									   "; " + 
									   obj.get("price").toString());
						}
					} catch (JSONException e) {

					}
					
				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				}
	         return list;
	     }

	     protected void onPostExecute(List<String> list) {
	    	 
	    	 String[] placeIt;
	    	 List<String> inDB = new ArrayList<String>();
	    	 List<PlaceIt> allPlace = new ArrayList<PlaceIt>();
	    	 allPlace.addAll(placeItManager.getActivePlaceIts());
	    	 allPlace.addAll(placeItManager.getInActivePlaceIts());
	    	 List<String> mPlaceItNames = new ArrayList<String>(); 
	    	 for (int i = 0; i < list.size(); i++)
	    	 {
	    		 placeIt = list.get(i).split("; ");
	    		 mPlaceItNames.add(placeIt[0]);
	         } 	    	  	    	 
	    	 for (int i = 0; i < allPlace.size(); i++)
	    	 {
	    		 if (!mPlaceItNames.contains(allPlace.get(i).getTitle()))
	    		 {
	    			 placeItManager.removePlaceIt(allPlace.get(i));
	    		 }
	    		 else 
	    		 {
	    			 for (int x = 0; x < list.size(); x++)
	    			 {
	    				 placeIt = list.get(x).split("; ");
	    				 if (placeIt[0].equals(allPlace.get(i).getTitle())) 
	    				 {
	    					 allPlace.get(i).setDesc(placeIt[1]);
	    					 allPlace.get(i).setLocation(Double.parseDouble(placeIt[2]),
	    							 Double.parseDouble(placeIt[3]));
	    					 allPlace.get(i).setStatus(Boolean.parseBoolean(placeIt[4]));
	    					 allPlace.get(i).setCategory(Boolean.parseBoolean(placeIt[5]));
	    					 allPlace.get(i).setCategories(placeIt[6]);
	    					 inDB.add(placeIt[0]);
	    					 placeItManager.updatePlaceIt(allPlace.get(i));
	    				 }
	    			 } 
	    		 }
	    	 }
	    	 for (int i  = 0; i < list.size(); i++)
	    	 {
	    		 placeIt = list.get(i).split("; ");
	    		 if (!inDB.contains(placeIt[0]))
	    		 {
	    			 if (placeIt[5].equals("false"))
	    			 {
	    				 if (placeIt[4].equals("true"))
	    				 {
	    					 map.addMarker(new MarkerOptions()
	    					 .position(new LatLng(Double.parseDouble(placeIt[2]),
	    							 Double.parseDouble(placeIt[3])))
	    							 .title(placeIt[0])
	    							 .snippet(placeIt[1])
	    							 .icon(BitmapDescriptorFactory 
	    									 .fromResource(R.drawable.ic_placeit)));
	    				}
	    				 PlaceIt p = (placeItManager.createPlaceIt(placeIt[0],
	    						 placeIt[1],
	    						 new LatLng(Double.parseDouble(placeIt[2]),
	    								 Double.parseDouble(placeIt[3])),
	    								 Boolean.parseBoolean(placeIt[4])));
	    				 if (placeIt[4].equals("true"))
	    					 placeItManager.registerGeofence(p);
	    			}
	    			else if (placeIt[4].equals("true"))
	    			{
	    				placeItManager.createCategoryPlaceIt(placeIt[0],
	    						placeIt[6],
	    						Boolean.parseBoolean(placeIt[4]));
	    			}
	    		}
	    	}
	     
	    	 /*String[] placeIt;
	    	 PlaceIt mPLaceIt;
	    	 for (int i = 0; i < list.size(); i++)
	    	 {
	  
	    		 placeIt = list.get(i).split("; ");
	    		 if (placeIt[5].equals("false"))
	    		 {
	    		     mPLaceIt=placeItManager.createPlaceIt(placeIt[0],
                                     placeIt[1],
                                     new LatLng(Double.parseDouble(placeIt[2]),
                                             Double.parseDouble(placeIt[3])), 
                                             Boolean.parseBoolean(placeIt[4]));
	    			 if (placeIt[4].equals("true"))
	    			 {
	    				 map.addMarker(new MarkerOptions()
	    				 .position(new LatLng(Double.parseDouble(placeIt[2]),
	    						 Double.parseDouble(placeIt[3])))
	    						 .title(placeIt[0])
	    						 .snippet(placeIt[1])
	    						 .icon(BitmapDescriptorFactory
	    						 .fromResource(R.drawable.ic_placeit)));
	    				 placeItManager.registerGeofence(mPLaceIt);
	    			 }
                  
	    		 }
	    		 else if (placeIt[4].equals("true"))
	    		 {
	    			 placeItManager.createCategoryPlaceIt(placeIt[0],
                    		 placeIt[6],
                    		 Boolean.parseBoolean(placeIt[4]));
	    		 }
	    	 }*/
	     }

	 }

    
    /**
     * Sets up an alarm for scheduling weekly events
     */

}
