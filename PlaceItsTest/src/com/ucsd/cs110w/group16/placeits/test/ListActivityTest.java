package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;

public class ListActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private int sWidth;
    private int sHeight;
    
    public ListActivityTest() {
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
     * Given that the user has created a Place It
     * and they want to remove it
     * then when they view the list of active Place It's
     * and click on remove
     * the place it should not show up on the map
     */
    public void testListRemove() {
        //make the place it
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2 - 5;
        int toY = sHeight/2 - 5;
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
        //go into List Activity
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        //remove place it
        solo.clickInList(0);
        solo.goBack();
        //place it is not on map anymore because the dialog box to make a new 
        //one popped up
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        assertTrue(solo.waitForDialogToOpen());
    }
    
    /*
     * Given that a Place It was pulled down 
     * and the user wants to repost it
     * then when they view the list of pulled down Place It's
     * and click on repost 
     * the place it should show back up on the map
     */
    public void testListRepost() {
        //make the place it
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2 - 5;
        int toY = sHeight/2 - 5;
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
        //go into List Activity
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        //remove place it
        solo.clickInList(0);
        solo.goBack();
        //place it is not on map anymore because the dialog box to make a new 
        //one popped up
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        assertTrue(solo.waitForDialogToOpen());
        solo.clickOnText("NO");
        //go back to lists activity
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        //click on reposts tab
        solo.clickOnText("PULLED DOWN");
        //repost place it
        solo.clickOnText("Repost");
        //go back to main activity and try to make new place it
        solo.goBackToActivity("MainActivity");
        solo.clickOnScreen(sWidth / 2, sHeight / 2);
        assertFalse(solo.waitForDialogToOpen());        
    }
  
}
