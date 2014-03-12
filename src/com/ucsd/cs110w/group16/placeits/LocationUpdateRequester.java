package com.ucsd.cs110w.group16.placeits;

/*
 * Copyright 2011 Google Inc.
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

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;

/**
 * Provides support for initiating active and passive location updates optimized
 * for the Gingerbread release. Includes use of the Passive Location Provider.
 * 
 * Uses broadcast Intents to notify the app of location changes.
 */
public class LocationUpdateRequester {

    protected LocationManager locationManager;

    public LocationUpdateRequester(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * Request active location updates. These updates will be triggered by a
     * direct request from the Location Manager.
     */
    public void requestLocationUpdates(long minTime, long minDistance,
            Criteria criteria, PendingIntent pendingIntent) {
        // Note that we aren't monitoring this provider to check if it becomes
        // disabled - this is handled by the calling Activity.
        locationManager.requestLocationUpdates(minTime, minDistance, criteria,
                pendingIntent);
    }

    /**
     * Request passive location updates. These updates will be triggered by
     * locations received by 3rd party apps that have requested location
     * updates. The miniumim time and distance for passive updates will
     * typically be longer than for active updates. The trick is to balance the
     * difference to minimize battery drain by maximize freshness.
     */
    public void requestPassiveLocationUpdates(long minTime, long minDistance,
            PendingIntent pendingIntent) {
        locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER, PlaceItUtils.MAX_TIME,
                PlaceItUtils.MAX_DISTANCE, pendingIntent);
    }
}
