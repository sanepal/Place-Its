package com.ucsd.cs110w.group16.placeits;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Service that requests a list of nearby locations from the underlying web
 * service. TODO Update the URL and XML parsing to correspond with your
 * underlying web service.
 */
public class PlacesUpdateService extends IntentService {

    protected static String TAG = "PlacesUpdateService";

    protected ContentResolver contentResolver;
    protected SharedPreferences prefs;
    protected Editor prefsEditor;
    protected ConnectivityManager cm;
    protected boolean lowBattery = false;
    protected boolean mobileData = false;
    protected int prefetchCount = 0;
    private PlaceItManager placeItManager;

    public PlacesUpdateService() {
        super(TAG);
        setIntentRedeliveryMode(false);
        placeItManager = new PlaceItManager(this);
    }

    /**
     * Set the Intent Redelivery mode to true to ensure the Service starts
     * "Sticky" Defaults to "true" on legacy devices.
     */
    protected void setIntentRedeliveryMode(boolean enable) {
    }

    /**
     * Returns battery status. True if less than 10% remaining.
     * 
     * @param battery
     *            Battery Intent
     * @return Battery is low
     */
    protected boolean getIsLowBattery(Intent battery) {
        /*
         * float pctLevel = (float) battery.getIntExtra(
         * BatteryManager.EXTRA_LEVEL, 1) /
         * battery.getIntExtra(BatteryManager.EXTRA_SCALE, 1); return pctLevel <
         * 0.15;
         */
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        contentResolver = getContentResolver();
        prefs = getSharedPreferences(PlaceItUtils.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    /**
     * {@inheritDoc} Checks the battery and connectivity state before removing
     * stale venues and initiating a server poll for new venues around the
     * specified location within the given radius.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Extract the location and radius around which to conduct our search.
        Location location = new Location(
                PlaceItUtils.CONSTRUCTED_LOCATION_PROVIDER);
        float radius = PlaceItUtils.DEFAULT_RADIUS;

        Bundle extras = intent.getExtras();
        if (intent.hasExtra(PlaceItUtils.EXTRA_KEY_LOCATION)) {
            location = (Location) (extras.get(PlaceItUtils.EXTRA_KEY_LOCATION));
            radius = extras.getFloat(PlaceItUtils.EXTRA_KEY_RADIUS,
                    PlaceItUtils.DEFAULT_RADIUS);
        }
        // Check if we're in a low battery situation.
        /*
         * IntentFilter batIntentFilter = new IntentFilter(
         * Intent.ACTION_BATTERY_CHANGED); Intent battery =
         * registerReceiver(null, batIntentFilter); lowBattery =
         * getIsLowBattery(battery);
         */

        // Check if we're connected to a data network, and if so - if it's a
        // mobile network.
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        mobileData = activeNetwork != null
                && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;

        // If we're not connected, enable the connectivity receiver and disable
        // the location receiver.
        // There's no point trying to poll the server for updates if we're not
        // connected, and the
        // connectivity receiver will turn the location-based updates back on
        // once we have a connection.
        if (!isConnected) {
            PackageManager pm = getPackageManager();

            ComponentName connectivityReceiver = new ComponentName(this,
                    ConnectivityChangedReceiver.class);
            ComponentName locationReceiver = new ComponentName(this,
                    LocationChangedReceiver.class);
            ComponentName passiveLocationReceiver = new ComponentName(this,
                    PassiveLocationChangedReceiver.class);

            pm.setComponentEnabledSetting(connectivityReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            pm.setComponentEnabledSetting(locationReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            pm.setComponentEnabledSetting(passiveLocationReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            // If we are connected check to see if this is a forced update
            // (typically triggered
            // when the location has changed).
            boolean doUpdate = intent.getBooleanExtra(
                    PlaceItUtils.EXTRA_KEY_FORCEREFRESH, false);

            // If it's not a forced update (for example from the Activity being
            // restarted) then
            // check to see if we've moved far enough, or there's been a long
            // enough delay since
            // the last update and if so, enforce a new update.
            if (!doUpdate) {
                // Retrieve the last update time and place.
                long lastTime = prefs.getLong(
                        PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_TIME,
                        Long.MIN_VALUE);
                long lastLat = prefs.getLong(
                        PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_LAT,
                        Long.MIN_VALUE);
                long lastLng = prefs.getLong(
                        PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_LNG,
                        Long.MIN_VALUE);
                Location lastLocation = new Location(
                        PlaceItUtils.CONSTRUCTED_LOCATION_PROVIDER);
                lastLocation.setLatitude(lastLat);
                lastLocation.setLongitude(lastLng);

                // If update time and distance bounds have been passed, do an
                // update.
                if ((lastTime < System.currentTimeMillis()-PlaceItUtils.MAX_TIME) ||(lastLocation.distanceTo(location) > PlaceItUtils.MAX_DISTANCE))
                    doUpdate = true;
            }

            if (doUpdate) {
                // Hit the server for new venues for the current location.
                refreshPlaces(location, radius);
            } else
                Log.d(TAG, "Place List is fresh: Not refreshing");

        }
        Log.d(TAG, "Place List Download Service Complete");
    }

    /**
     * Polls the underlying service to return a list of places within the
     * specified radius of the specified Location.
     * 
     * @param location
     *            Location
     * @param radius
     *            Radius
     */
    protected void refreshPlaces(Location location, float radius) {
        // Log to see if we'll be prefetching the details page of each new
        // place.
        if (mobileData) {
            Log.d(TAG, "Not prefetching due to being on mobile");
        } else if (lowBattery) {
            Log.d(TAG, "Not prefetching due to low battery");
        }

        long currentTime = System.currentTimeMillis();
        URL url;

        try {
            // iterate over all active category place its
            
            List<PlaceIt> categoryPlaceIts = placeItManager
                    .getCategoryPlaceIts();
            for (PlaceIt placeIt : categoryPlaceIts) {
                // add types from category place it to the url :
                // type1|type2|type3
                if (!placeIt.isActive())
                    continue;
                String categories = placeIt.getTitle();
                //categories.replace(" ", "");
                categories = categories.replace(", ", "|");
                String locationStr = location.getLatitude() + ","
                        + location.getLongitude();
                String baseURI = PlaceItUtils.PLACES_LIST_BASE_URI;
                String placesFeed = baseURI + "&location=" + locationStr
                        + "&radius=" + radius + PlaceItUtils.PLACES_API_KEY;
                placesFeed += "&types=" + categories;
                url = new URL(placesFeed);
                Log.d(TAG, ""+placesFeed);

                // Open the connection
                URLConnection connection = url.openConnection();
                HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    XmlPullParserFactory factory = XmlPullParserFactory
                            .newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(in, null);
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        // get the types from category place it
                        if (eventType == XmlPullParser.START_TAG
                                && xpp.getName().equals("result")) {
                            eventType = xpp.next();
                            String name = "";
                            String vicinity = "";
                            String types = "";
                            String locationLat = "";
                            String locationLng = "";
                            while (!(eventType == XmlPullParser.END_TAG && xpp
                                    .getName().equals("result"))) {
                                if (eventType == XmlPullParser.START_TAG
                                        && xpp.getName().equals("name"))
                                    name = xpp.nextText();
                                else if (eventType == XmlPullParser.START_TAG
                                        && xpp.getName().equals("vicinity"))
                                    vicinity = xpp.nextText();
                                else if (eventType == XmlPullParser.START_TAG
                                        && xpp.getName().equals("type"))
                                    types = types == "" ? xpp.nextText()
                                            : types + " " + xpp.nextText();
                                else if (eventType == XmlPullParser.START_TAG
                                        && xpp.getName().equals("lat"))
                                    locationLat = xpp.nextText();
                                else if (eventType == XmlPullParser.START_TAG
                                        && xpp.getName().equals("lng"))
                                    locationLng = xpp.nextText();
                                eventType = xpp.next();
                            }
                            // create a geofence out of the lat/long if the
                            // type exists so that it is immediately triggered
                            Location placeLocation = new Location(
                                    PlaceItUtils.CONSTRUCTED_LOCATION_PROVIDER);
                            placeLocation.setLatitude(Double
                                    .valueOf(locationLat));
                            placeLocation.setLongitude(Double
                                    .valueOf(locationLng));
                            
                            placeIt.setLocation(placeLocation);
                            placeIt.setDesc(name + ",\n " + vicinity);
                            placeItManager.updatePlaceIt(placeIt);
                            placeItManager.registerGeofence(placeIt);
                            break;
                            
                        }
                        eventType = xpp.next();
                    }

                    
                } else
                    Log.e(TAG,
                            responseCode + ": "
                                    + httpConnection.getResponseMessage());
            }
            // Save the last update time and place to the Shared
            // Preferences.
            prefsEditor.putLong(
                    PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_LAT,
                    (long) location.getLatitude());
            prefsEditor.putLong(
                    PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_LNG,
                    (long) location.getLongitude());
            prefsEditor.putLong(
                    PlaceItUtils.SP_KEY_LAST_LIST_UPDATE_TIME,
                    System.currentTimeMillis());
            prefsEditor.commit();

        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage());
        } finally {
        }
    }

}