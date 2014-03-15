package com.ucsd.cs110w.group16.placeits;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

class GetAddressResults extends AsyncTask<String, Void, List<Address>> {
    private Activity mActivity;
    private Geocoder geoCoder;
    private List<Address> results;

    public GetAddressResults(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mActivity, "Getting results", Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<Address> doInBackground(String... query) {
        String searchTerm = query[0];
        geoCoder = new Geocoder(mActivity.getApplicationContext());
        try {
            results = geoCoder.getFromLocationName(searchTerm, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<Address> results) {
        if (results == null)
            Toast.makeText(mActivity.getApplicationContext(), "Sorry,  no results were found",
                    Toast.LENGTH_LONG).show();
        else 
            ((MainActivity) mActivity).receiveSearchResults(results);
    }
}
