package com.ucsd.cs110w.group16.placeits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.ucsd.cs110w.group16.placeits.PlaceItUtils.REMOVE_TYPE;
import com.ucsd.cs110w.group16.placeits.PlaceItUtils.REQUEST_TYPE;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            Log.d(PlaceItUtils.APPTAG, mContext.getString(R.string.play_services_available));

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(mActivity.getFragmentManager(), PlaceItUtils.APPTAG);
            }
            return false;
        }
    }
    
    public void clearDB()
    {
    	mDb.clear();
    }
    
    
    public void removePlaceItIntent(PlaceIt placeIt) {
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
        mRemoveType = PlaceItUtils.REMOVE_TYPE.LIST;

        // Create a List of 1 Geofence with the ID "2" and store it in the global list
        mGeofenceIdsToRemove = Collections.singletonList(placeIt.getId());

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
	

    public PlaceIt createPlaceIt(PlaceIt placeIt) {
        return createPlaceIt(placeIt.getTitle(), placeIt.getDesc(), new LatLng(placeIt.getLatitude(), placeIt.getLongitude()));
        
    }
    
    
    public PlaceIt createPlaceIt(String title, String desc, LatLng arg0) {        

        //create the place it in our database
        return mDb.createPlaceIt(arg0.latitude, arg0.longitude, title, desc, true);               
    }
    
    public PlaceIt createPlaceIt(String title, String desc, LatLng arg0, Boolean status) {        

        //create the place it in our database
        return mDb.createPlaceIt(arg0.latitude, arg0.longitude, title, desc, status);               
    }
    
    /*public PlaceIt createCategoryPlaceIt(String title) {
        return mDb.createPlaceIt(0, 0, title, "", true, true);
    }*/
    
    /*public PlaceIt createCategoryPlaceIt(String title, String desc, String categories){
    	return mDb.createPlaceIt(title, desc, true, categories);
    }*/
    
    public PlaceIt createCategoryPlaceIt(String title, String categories){
    	return mDb.createPlaceIt(title, " ", true, categories);
    }
    
    public PlaceIt createCategoryPlaceIt(String title, String categories, Boolean status){
    	return mDb.createPlaceIt(title, " ", status, categories);
    }
    
    public void updatePlaceIt(PlaceIt mPlaceIt) {
        mDb.updatePlaceIt(mPlaceIt);
    }
    
    /**
     * Get the geofence parameters for each geofence and add them to
     * a List. Create the PendingIntent containing an Intent that
     * Location Services sends to this app's broadcast receiver when
     * Location Services detects a geofence transition. Send the List
     * and the PendingIntent to Location Services.
     * @param desc 
     * @param title 
     */
    public void registerGeofence(PlaceIt mPlaceIt) {
        /*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
        mRequestType = PlaceItUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }
        /*
         * Add PlaceIt objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        mCurrentGeofences.add(mPlaceIt.toGeofence());
        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
            mCurrentGeofences.remove(mPlaceIt);
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(mContext, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
    
    
    public List<PlaceIt> getActivePlaceIts()
    {
        List<PlaceIt> activePlaceIts = mDb.getAllActive();
    	return activePlaceIts;
    }
    
    public List<PlaceIt> getInActivePlaceIts()
    {
        List<PlaceIt> inactivePlaceIts = mDb.getAllInactive();
        return inactivePlaceIts;
    }
    
    public List<PlaceIt> getCategoryPlaceIts() {
        List<PlaceIt> categoryPlaceIts = mDb.getAllCategory();
        return categoryPlaceIts;
    }
    public PlaceIt getPlaceIt(Long id)
    {
        PlaceIt placeIt = mDb.getPlaceIt(id);
        return placeIt;
    }
    
    public void setInActive(PlaceIt p)
    {
    	p.setStatus(false);
    	mDb.updatePlaceIt(p);
    	removePlaceItIntent(p);
    }
    
    public void setActive(PlaceIt p)
    {
    	p.setStatus(true);
    	mDb.updatePlaceIt(p);
    }
    
    public void removePlaceIt(PlaceIt p)
    {
    	// Do we need to remove from the geofence here?
    	mDb.deletePlaceIt(p);
    }

    public void handleActivityResult(int requestCode, int resultCode,
            Intent intent) {
     // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case PlaceItUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (PlaceItUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (PlaceItUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (PlaceItUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(PlaceItUtils.APPTAG, mContext.getString(R.string.no_resolution));
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(PlaceItUtils.APPTAG,
                       mContext.getString(R.string.unknown_activity_request_code, requestCode));

               break;
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

}
