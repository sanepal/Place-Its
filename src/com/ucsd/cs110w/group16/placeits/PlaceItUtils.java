/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ucsd.cs110w.group16.placeits;

/**
 * This class defines constants used by location sample apps.
 */
public final class PlaceItUtils {

    // Used to track what type of geofence removal request was made.
    public enum REMOVE_TYPE {INTENT, LIST}

    // Used to track what type of request is in process
    public enum REQUEST_TYPE {ADD, REMOVE}
    
    /*
     * A log tag for the application
     */
    public static final String APPTAG = "Place It's";

    // Intent actions
    public static final String ACTION_CONNECTION_ERROR =
            "com.ucsd.cs110w.placeits.ACTION_CONNECTION_ERROR";

    public static final String ACTION_CONNECTION_SUCCESS =
            "com.ucsd.cs110w.placeits.ACTION_CONNECTION_SUCCESS";

    public static final String ACTION_GEOFENCES_ADDED =
            "com.ucsd.cs110w.placeits.ACTION_GEOFENCES_ADDED";

    public static final String ACTION_GEOFENCES_REMOVED =
            "com.ucsd.cs110w.placeits.ACTION_GEOFENCES_DELETED";

    public static final String ACTION_GEOFENCE_ERROR =
            "com.ucsd.cs110w.placeits.ACTION_GEOFENCES_ERROR";

    public static final String ACTION_GEOFENCE_TRANSITION =
            "com.ucsd.cs110w.placeits.ACTION_GEOFENCE_TRANSITION";

    public static final String ACTION_GEOFENCE_TRANSITION_ERROR =
                    "com.ucsd.cs110w.placeits.ACTION_GEOFENCE_TRANSITION_ERROR";

    // The Intent category used by all Location Services sample apps
    public static final String CATEGORY_LOCATION_SERVICES =
                    "com.ucsd.cs110w.placeits.CATEGORY_LOCATION_SERVICES";

    // Keys for extended data in Intents
    public static final String EXTRA_CONNECTION_CODE =
                    "com.ucsd.cs110w.placeits.EXTRA_CONNECTION_CODE";

    public static final String EXTRA_CONNECTION_ERROR_CODE =
            "com.ucsd.cs110w.placeits.EXTRA_CONNECTION_ERROR_CODE";

    public static final String EXTRA_CONNECTION_ERROR_MESSAGE =
            "com.ucsd.cs110w.placeits.EXTRA_CONNECTION_ERROR_MESSAGE";

    public static final String EXTRA_GEOFENCE_STATUS =
            "com.ucsd.cs110w.placeits.EXTRA_GEOFENCE_STATUS";

    /*
     * Keys for flattened geofences stored in SharedPreferences
     */
    public static final String KEY_LATITUDE = "com.gta0004.cs110w.cs110project.KEY_LATITUDE";

    public static final String KEY_LONGITUDE = "com.gta0004.cs110w.cs110project.KEY_LONGITUDE";

    public static final String KEY_RADIUS = "com.gta0004.cs110w.cs110project.KEY_RADIUS";
    
    public static final String SHARED_PREFERENCE_FILE = MainActivity.class.getSimpleName();
    
    public static final String KEY_EXPIRATION_DURATION =
            "com.ucsd.cs110w.placeits.KEY_EXPIRATION_DURATION";

    public static final String KEY_TRANSITION_TYPE =
            "com.ucsd.cs110w.placeits.KEY_TRANSITION_TYPE";

    // The prefix for flattened geofence keys
    public static final String KEY_PREFIX =
            "com.ucsd.cs110w.placeits.KEY";

    // Invalid values, used to test geofence storage when retrieving geofences
    public static final long INVALID_LONG_VALUE = -999l;

    public static final float INVALID_FLOAT_VALUE = -999.0f;

    public static final int INVALID_INT_VALUE = -999;

    /*
     * Constants used in verifying the correctness of input values
     */
    public static final double MAX_LATITUDE = 90.d;

    public static final double MIN_LATITUDE = -90.d;

    public static final double MAX_LONGITUDE = 180.d;

    public static final double MIN_LONGITUDE = -180.d;

    public static final float MIN_RADIUS = 1f;

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // A string of length 0, used to clear out input fields
    public static final String EMPTY_STRING = new String();

    public static final CharSequence GEOFENCE_ID_DELIMITER = ",";

    public static final float DEFAULT_RADIUS = 700f;    
    public static final float MAX_DISTANCE = 800f;
    public static final long MAX_TIME = 15000;

    public static final String CONSTRUCTED_LOCATION_PROVIDER = "CONSTRUCTED_LOCATION_PROVIDER";

    public static final String PLACES_API_KEY = "&key=AIzaSyCgLlnD_HaVJd4Cla4C9hRHd7ZIME86kZA";

    public static final String PLACES_LIST_BASE_URI = "https://maps.googleapis.com/maps/api/place/search/xml?sensor=true";
    
    // The location update distance for passive updates.
    public static float PASSIVE_MAX_DISTANCE = MAX_DISTANCE;
    // The location update time for passive updates
    public static long PASSIVE_MAX_TIME = MAX_TIME;
    
    public static String ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED = "com.ucsd.cs110w.group16.placeits.active_location_update_provider_disabled";
    public static final String EXTRA_KEY_FORCEREFRESH = "force_refresh";
    public static final String EXTRA_KEY_LOCATION = "location";
    public static final String EXTRA_KEY_RADIUS = "radius";
    public static final String EXTRA_KEY_IN_BACKGROUND = "EXTRA_KEY_IN_BACKGROUND";

    
    public static final String SP_KEY_LAST_LIST_UPDATE_TIME = "SP_KEY_LAST_LIST_UPDATE_TIME";
    public static final String SP_KEY_LAST_LIST_UPDATE_LAT = "SP_KEY_LAST_LIST_UPDATE_LAT";
    public static final String SP_KEY_LAST_LIST_UPDATE_LNG = "SP_KEY_LAST_LIST_UPDATE_LNG";
    public static final String SP_KEY_RUN_ONCE = "SP_KEY_RUN_ONCE";

    public static final String SP_KEY_FOLLOW_LOCATION_CHANGES = "SP_KEY_FOLLOW_LOCATION_CHANGES";

    
    

    

}
