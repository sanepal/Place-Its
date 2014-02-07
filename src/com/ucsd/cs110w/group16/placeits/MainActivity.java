package com.ucsd.cs110w.group16.placeits;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends Activity implements OnMapClickListener {
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onMapClick(final LatLng arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    
        builder.setMessage("Create Place-It here?");                
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {         
           @Override
           public void onClick(DialogInterface dialog, int which) {                 
               map.addMarker(new MarkerOptions()
               .position(arg0)
               .title("Hello world"));      
               //
                dialog.dismiss();
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
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_placeits:
            displayPlaceitsList();
            return true;
        }
        return false;
    }

    private void displayPlaceitsList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);        
    }

}
