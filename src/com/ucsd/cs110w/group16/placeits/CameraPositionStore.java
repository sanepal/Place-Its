package com.ucsd.cs110w.group16.placeits;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * Saves the latitude, longitude and zoom of the camera when the Main Activity 
 * is destroyed so that the map is restored to this state when it is remade
 */
public class CameraPositionStore {
    
    private final SharedPreferences mPrefs;
    
    private static final String SHARED_PREFERENCE_NAME = MainActivity.class.getSimpleName();
    
    public CameraPositionStore(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    
    /*
     * Retrieves the stored values and returns a camera position object with them
     * TODO set defaults instead of 0 
     */
    public CameraPosition getCameraPosition() {
        double lat = mPrefs.getFloat(SHARED_PREFERENCE_NAME+"_latitude", (float) 32.0);
        double lng = mPrefs.getFloat(SHARED_PREFERENCE_NAME+"_longitude", (float)-117.0);
        float zoom = mPrefs.getFloat(SHARED_PREFERENCE_NAME+"_zoom", 1.0f);
        return new CameraPosition(new LatLng(lat, lng), zoom, 0, 0);
    }
    
    /*
     * @param position the camera position you want saved
     * 
     */
    public void setCameraPosition(CameraPosition position) {
        Editor editor = mPrefs.edit();
        editor.putFloat(SHARED_PREFERENCE_NAME+"_latitude", (float) position.target.latitude);
        editor.putFloat(SHARED_PREFERENCE_NAME+"_longitude", (float) position.target.longitude);
        editor.putFloat(SHARED_PREFERENCE_NAME+"_zoom", position.zoom);
        editor.commit();
    }
}
