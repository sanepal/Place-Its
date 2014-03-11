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
    
    public CameraPositionStore(Context context) {
        mPrefs = context.getSharedPreferences(PlaceItUtils.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }
    
    /*
     * Retrieves the stored values and returns a camera position object with them
     * TODO set defaults instead of 0 
     */
    public CameraPosition getCameraPosition() {
        double lat = mPrefs.getFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_latitude", (float) 32.0);
        double lng = mPrefs.getFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_longitude", (float)-117.0);
        float zoom = mPrefs.getFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_zoom", 1.0f);
        return new CameraPosition(new LatLng(lat, lng), zoom, 0, 0);
    }
    
    /*
     * @param position the camera position you want saved
     * 
     */
    public void setCameraPosition(CameraPosition position) {
        Editor editor = mPrefs.edit();
        editor.putFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_latitude", (float) position.target.latitude);
        editor.putFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_longitude", (float) position.target.longitude);
        editor.putFloat(PlaceItUtils.SHARED_PREFERENCE_FILE+"_zoom", position.zoom);
        editor.commit();
    }
}
