package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    public MainActivityTest() {
        super( MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    
    public void testClickOnSearch() {
        solo.clickOnActionBarItem(R.id.action_search);
        assertTrue(solo.waitForText("Search an address"));
        solo.enterText(0, "UCSD");
        assertTrue(solo.searchText("UCSD"));
        solo.sendKey(Solo.ENTER);
        solo.waitForDialogToOpen();
    }
    
    /*
     * Given that the user is in or main activity
     * When the user clicks on "My Place Its"
     * Then the ListActivity should start with a list of all Place It's
     */
    public void testClickOnPlaceItsList() {
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        solo.goBack();
    }
}
