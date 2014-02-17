package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;

public class ListActivityTest extends
        ActivityInstrumentationTestCase2<ListActivity> {
    private Solo solo;
    private int sWidth;
    private int sHeight;
    
    public ListActivityTest() {
        super(ListActivity.class);
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
    public void testCreatePlaceIt() throws InterruptedException {
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2;
        int toY = sHeight/2;
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
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        assertTrue(solo.searchText("Place It 1"));
        solo.clickOnText("Place It 1");
        solo.wait(20);
        assertFalse(solo.searchText("Place It 1"));
        solo.clickOnText("PULLED DOWN");
        assertTrue(solo.searchText("Place It 1"));
        solo.clickOnText("Place It 1");
        solo.wait(20);
        assertFalse(solo.searchText("Place It 1"));
        solo.clickOnText("ACTIVE");
        assertTrue(solo.searchText("Place It 1"));
        solo.clickOnText("Place It 1");
        solo.wait(20);
        assertFalse(solo.searchText("Place It 1"));
        solo.clickOnText("PULLED DOWN");
        assertTrue(solo.searchText("Place It 1"));
        solo.clickLongOnText("Place It 1");
        solo.wait(20);
        assertFalse(solo.searchText("Place It 1"));
        solo.clickOnText("ACTIVE");
        assertFalse(solo.searchText("Place It 1"));
    }
}
