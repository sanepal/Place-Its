package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;

public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private int sWidth;
    private int sHeight;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        sHeight = displaymetrics.heightPixels;
        sWidth = displaymetrics.widthPixels;
    }

    /*
     * Given that the user is in our main activity 
     * And they click on the map
     * Then a prompt to create a place it should display 
     * And if they click yes
     * Then a prompt to enter details should show up 
     * And if they click save then
     * The dialog disappears and a marker appears at the location
     */
    public void testCreatePlaceIt() {
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2 + 5;
        int toY = sHeight/2 + 5;
        solo.drag(fromX, toX, fromY, toY, 1);
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        solo.waitForDialogToOpen();
        assertTrue(solo.searchText("Create Place It here?"));
        solo.clickOnText("YES");
        solo.waitForDialogToOpen();
        assertTrue(solo.searchText("Enter your details"));
        solo.enterText(0, "Place It 1");
        solo.enterText(1, "Reminder 1");
        solo.clickOnText("Save");
        // test the marker is there by clicking on the place it and searching
        // for the text
        solo.clickOnScreen(sWidth / 2, sHeight / 2); 
        assertFalse(solo.waitForDialogToOpen());
    }
    
    /*
     * Given that the user is in our main activity 
     * And they click on the map
     * Then a prompt to create a place it should display 
     * And if they click no
     * then the dialog box is dismissed
     * and no place it is created
     */
    public void testClickMapDismiss() {
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2 + 5;
        int toY = sHeight/2 + 5;
        solo.drag(fromX, toX, fromY, toY, 1);
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        solo.waitForDialogToOpen();
        assertTrue(solo.searchText("Create Place It here?"));
        solo.clickOnText("NO");
        assertFalse(solo.searchText("Create Place It here?"));
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        solo.waitForDialogToOpen();
        assertTrue(solo.searchText("Create Place It here?"));        
    }

    /*
     * Given that the user is in the MainActivity
     * and he clicks on the search button
     * then the search item expands to display the text box
     * and when he types in a good location and clicks enter
     * then a dialog box with the results shows up
     * and when the user clicks on a result
     * then the map goes to the search result
     * and when the user clicks on the marker
     * then they are prompted to create a place it
     */
    public void testClickOnSearch() {
        solo.clickOnActionBarItem(R.id.action_search);
        assertTrue(solo.waitForText("Search an address"));
        solo.enterText(0, "UCSD");
        assertTrue(solo.searchText("UCSD"));
        solo.sendKey(Solo.ENTER);
        solo.waitForDialogToOpen();
        assertTrue(solo.searchText("Select the location"));
        solo.clickInList(0);
        solo.clickOnScreen(sWidth / 2, sHeight / 2); 
        assertTrue(solo.searchText("Create Place"));        
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

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
