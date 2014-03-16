package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.LoginActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;

public class AccountTest extends
        ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;
    private int sWidth;
    private int sHeight;
    
    public AccountTest() {
        super(LoginActivity.class);
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
    
    @Override
    public void tearDown() throws Exception {
         solo.finishOpenedActivities();
   }
    
    /*
     * Given that the user logs in on one account
     * and creates place its
     * then they log in in another account
     * then the place its list should be empty
     */
    public void testFirstAccount() {
        solo.enterText(0, "account1@test.com");
        solo.enterText(1, "accountstest");
        solo.clickOnButton("Sign in or register");
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
        assertTrue(solo.searchText("Place It 1"));
        
    }
    public void testSecondAccount() {
        solo.enterText(0, "account2@test.com");
        solo.enterText(1, "accountstest");
        solo.clickOnButton("Sign in or register");
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        assertFalse(solo.searchText("Place It 1"));
    }
  
}
