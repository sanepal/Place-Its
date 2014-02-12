package com.ucsd.cs110w.group16.placeits;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        handleIntent(getIntent());
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
                        registerPlaceit(inputTitle.getText().toString(),
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

}
