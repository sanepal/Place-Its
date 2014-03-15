package com.ucsd.cs110w.group16.placeits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListActivity extends FragmentActivity implements
        ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private static PlaceItManager placeItManager;
    private static List<PlaceIt> activePlaceIts;
    private static List<PlaceIt> inActivePlaceIts;
    private static ArrayAdapter<PlaceIt> listOfActives;
    private static ArrayAdapter<PlaceIt> listOfInActives;
    
	private static final String PLACEIT_URI = "http://actraiin.appspot.com/item";
    private static PlaceIt r;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        
        // Update all these static members when the ListView is created.
        placeItManager = new PlaceItManager(this);
        activePlaceIts = placeItManager.getActivePlaceIts();
        inActivePlaceIts = placeItManager.getInActivePlaceIts();       
        listOfActives = new ArrayAdapter<PlaceIt>(this, R.layout.list_repost, R.id.text, activePlaceIts);
        listOfInActives = new ArrayAdapter<PlaceIt>(this, R.layout.list_delete, R.id.text, inActivePlaceIts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.list_section1).toUpperCase(l);
            case 1:
                return getString(R.string.list_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy,
                    container, false);
            
            ListView dummyListView = (ListView) rootView
                    .findViewById(R.id.section_label);
            
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {

                dummyListView.setAdapter(listOfActives);
                dummyListView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                            final View arg1, int arg2, long arg3) {
                    	// Set place it to inactive and update the lists.
                    	PlaceIt removedPlaceIt = activePlaceIts.get(arg2);
                        placeItManager.setInActive(removedPlaceIt);
                        updatePlaceIt(removedPlaceIt);
                    	activePlaceIts.remove(removedPlaceIt);
                    	inActivePlaceIts.add(removedPlaceIt);
                    	
                        arg1.animate().setDuration(5).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        listOfInActives.notifyDataSetChanged();
                                        listOfActives.notifyDataSetChanged();
                                        arg1.setAlpha(1);
                                    }
                                });
                        Toast.makeText(getActivity(),
                                "Place It has been removed", Toast.LENGTH_SHORT)
                                .show();

                    }

                });
            } else {
                dummyListView.setAdapter(listOfInActives);
                dummyListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                            final View arg1, int arg2, long arg3) {
                    	
                    	// Repost place it and update lists.
                    	PlaceIt repostedPlaceIt = inActivePlaceIts.get(arg2);
                    	placeItManager.setActive(repostedPlaceIt);
                        placeItManager.registerGeofence(repostedPlaceIt);
                        updatePlaceIt(repostedPlaceIt);
                        activePlaceIts.add(repostedPlaceIt);
                        inActivePlaceIts.remove(repostedPlaceIt);
                        
                        arg1.animate().setDuration(5).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        listOfInActives.notifyDataSetChanged();
                                        listOfActives.notifyDataSetChanged();
                                        arg1.setAlpha(1);
                                    }
                                });
                        Toast.makeText(getActivity(),
                                "Place It has been reposted", Toast.LENGTH_SHORT)
                                .show();
                    }

                });
                dummyListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                	public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                			int pos, long id) {
                		
                		// Delete place it and update list
                		PlaceIt deletedPlaceIt = inActivePlaceIts.get(pos);
                		placeItManager.removePlaceIt(deletedPlaceIt);
                        inActivePlaceIts.remove(deletedPlaceIt);
                        
                        arg1.animate().setDuration(5).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        listOfInActives.notifyDataSetChanged();
                                        arg1.setAlpha(1);
                                    }
                                });
                		
                		Toast.makeText(getActivity(), "Place It has been deleted", Toast.LENGTH_SHORT).show();
                		
                		return true;
                	}
                });
            }
            return rootView;
        }
        
    	private void updatePlaceIt(PlaceIt p) {
    		r = p;
    		Thread t = new Thread() {

    			public void run() {
    				HttpClient client = new DefaultHttpClient();
    				HttpPost post = new HttpPost(PLACEIT_URI);
     
    			    try {
    			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
    			      nameValuePairs.add(new BasicNameValuePair("name",
    			    		  r.getTitle()));
    			      nameValuePairs.add(new BasicNameValuePair("description",
    			    		  r.getDesc()));
    			      nameValuePairs.add(new BasicNameValuePair("price",
    			    		  r.getDesc() + "; " + 
    			    		  r.getLatitude() + "; " + r.getLongitude() + "; " +
    			              r.isActive() + "; " + r.isCategory() + "; "+ r.getCategories()));
    			      nameValuePairs.add(new BasicNameValuePair("product",
    			    		  MainActivity.mEmail));
    			      nameValuePairs.add(new BasicNameValuePair("action",
    				          "put"));
    			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    			  
    			      HttpResponse response = client.execute(post);
    			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    			      String line = "";
    			      while ((line = rd.readLine()) != null) {
    			      }

    			    } catch (IOException e) {
    			    }
    			}
    		};

    		t.start();
    			
    	}
    	

    }

}
