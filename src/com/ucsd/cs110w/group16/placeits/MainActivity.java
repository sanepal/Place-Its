package com.ucsd.cs110w.group16.placeits;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMapClickListener {
    private GoogleMap map;
    static Handler handler = new Handler();
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
    
    private void showInputDialog(final LatLng location) {
        //Preparing views
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //the edit text's are part of the relative layout viewgroup in the layout file
        View layout = inflater.inflate(R.layout.input_details, (ViewGroup) findViewById(R.id.input_root));
        final EditText inputTitle = (EditText) layout.findViewById(R.id.input_title);
        final EditText inputDesc = (EditText) layout.findViewById(R.id.input_desc);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();                
                map.addMarker(new MarkerOptions()
                .position(location)
                .title(inputTitle.getText().toString())
                .snippet(inputDesc.getText().toString()));
                //TODO check value from switch, get values from drop downs if on, pass them along
                //TODO check inputs for empty values
                registerPlaceit(inputTitle.getText().toString(), inputDesc.getText().toString(), location);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();     
    }

    protected void registerPlaceit(String title, String desc, LatLng location) {
        Toast.makeText(getApplicationContext(), "Place it created: " + title + " " + desc, Toast.LENGTH_LONG).show();     
        //TODO implement registerPlaceit
        //register the place it 
    }

}
