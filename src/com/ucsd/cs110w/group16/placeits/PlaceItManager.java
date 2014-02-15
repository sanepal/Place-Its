package com.ucsd.cs110w.group16.placeits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.ucsd.cs110w.group16.placeits.GeofenceUtils.REMOVE_TYPE;
import com.ucsd.cs110w.group16.placeits.GeofenceUtils.REQUEST_TYPE;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class PlaceItManager {
    // Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;
    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    private Context mContext;
    private Activity mActivity;
    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
    private PlaceItDatabase mDb;
    List<Geofence> mCurrentGeofences;

    public PlaceItManager(Context mContext) {
        this.mContext = mContext;
        //mActivity = (Activity) mContext;
        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(mContext);
        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(mContext);
        mCurrentGeofences = new ArrayList<Geofence>();
        mDb = new PlaceItDatabase(mContext);
    }
    
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(GeofenceUtils.APPTAG, mContext.getString(R.string.play_services_available));

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(mActivity.getFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }
    
    /**
     * Called when the user clicks the "Remove geofences" button
     *
     * @param view The view that triggered this callback
     */
    public void onUnregisterByPendingIntentClicked(View view) {
        /*
         * Remove all geofences set by this app. To do this, get the
         * PendingIntent that was added when the geofences were added
         * and use it as an argument to removeGeofences(). The removal
         * happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done
         */

        /*
         * Record the removal as remove by Intent. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        // Record the type of removal
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        // Try to make a removal request
        try {
        /*
         * Remove the geofences represented by the currently-active PendingIntent. If the
         * PendingIntent was removed for some reason, re-create it; since it's always
         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
         */
        mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());

        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(mContext, R.string.remove_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }

    }
    
    /**
     * Called when the user clicks the "Remove geofence 2" button
     * @param view The view that triggered this callback
     */
    public void onUnregisterGeofenceClicked(View view) {
        /*
         * Remove the geofence by creating a List of geofences to
         * remove and sending it to Location Services. The List
         * contains the id of geofence 2, which is "2".
         * The removal happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done.
         */

        /*
         * Record the removal as remove by list. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        mRemoveType = GeofenceUtils.REMOVE_TYPE.LIST;

        // Create a List of 1 Geofence with the ID "2" and store it in the global list
        mGeofenceIdsToRemove = Collections.singletonList("2");

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        // Try to remove the geofence
        try {
            mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);

        // Catch errors with the provided geofence IDs
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(mContext, R.string.remove_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
	
    /**
     * Called when the user clicks the "Register geofences" button.
     * Get the geofence parameters for each geofence and add them to
     * a List. Create the PendingIntent containing an Intent that
     * Location Services sends to this app's broadcast receiver when
     * Location Services detects a geofence transition. Send the List
     * and the PendingIntent to Location Services.
     * @param desc 
     * @param title 
     */
    public void registerPlaceIt(String title, String desc, LatLng arg0) {

        /*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        //create the place it in our database
        mDb.open();
        PlaceIt mPlaceIt = mDb.createPlaceIt(arg0.latitude, arg0.longitude, title, desc, true);
        mDb.close();

        /*
         * Add Geofence objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        mCurrentGeofences.add(mPlaceIt.toGeofence());

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(mContext, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    public List<PlaceIt> getActivePlaceIts()
    {
        mDb.open();
        List<PlaceIt> activePlaceIts = mDb.getAllActive();
        mDb.close();
    	return activePlaceIts;
    }
    
    public List<PlaceIt> getInActivePlaceIts()
    {
        mDb.open();
        List<PlaceIt> inactivePlaceIts = mDb.getAllInactive();
        mDb.close();
        return inactivePlaceIts;
    }
    
    
    public PlaceIt getPlaceIt(Long id)
    {
        mDb.open();
        PlaceIt placeIt = mDb.getPlaceIt(id);
        mDb.close();
        return placeIt;
    }
    
    public void setInActive(PlaceIt p)
    {
    	p.setStatus(false);
    	mDb.open();
    	mDb.updatePlaceIt(p);
    	mDb.close();
    }
    
    public void setActive(PlaceIt p)
    {
    	p.setStatus(true);
    	mDb.open();
    	mDb.updatePlaceIt(p);
    	mDb.close();
    }
    
    public void removePlaceIt(PlaceIt p)
    {
    	// Do we need to remove from the geofence here?
        mDb.open();
    	mDb.deletePlaceIt(p);
    	mDb.close();
    }
}
