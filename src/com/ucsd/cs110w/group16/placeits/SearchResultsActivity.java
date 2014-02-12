/**
 * 
 */
package com.ucsd.cs110w.group16.placeits;

import java.io.IOException;
import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultsActivity extends FragmentActivity {
    private Geocoder geoCoder;
    private List<Address> results;
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        listView = (ListView) findViewById(R.id.list);
        handleIntent(getIntent());        
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            geoCoder = new Geocoder(getApplicationContext());
            try {
                results = geoCoder.getFromLocationName(query,5);
                if (results.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "no results to display", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (results.size() > 0) {
                        String arrResult[] = new String[results.size()];
                        for (int j=0; j<results.size(); j++){
                            Address result = results.get(j);
                            StringBuilder strResult = new StringBuilder();
                            for(int i=0; i<result.getMaxAddressLineIndex(); i++) {
                                strResult.append(result.getAddressLine(i)).append("\n");
                            }
                            arrResult[j] = strResult.toString();
                        }
                        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.basic_textview, arrResult));

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
