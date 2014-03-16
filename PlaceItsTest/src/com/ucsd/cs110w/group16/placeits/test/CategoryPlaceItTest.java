package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import com.ucsd.cs110w.group16.placeits.MainActivity;
import com.ucsd.cs110w.group16.placeits.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.widget.Spinner;

public class CategoryPlaceItTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private int sWidth;
    private int sHeight;

    public CategoryPlaceItTest() {
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
     * And they want to create a category place it
     * And they click on the menu item for it
     * Then a prompt to enter details should show up 
     * And they can input up to 3 categories for it
     * And if they click save then
     * The it appears in the active place its listview
     */
    public void testCreateCategoryPlaceIt() {
        solo.assertCurrentActivity("MainActivity", MainActivity.class);
        int fromX = (sWidth/2) - (sWidth/3);
        int toX = (sWidth/2) + (sWidth/3);
        int fromY = sHeight/2 + 5;
        int toY = sHeight/2 + 5;
        solo.drag(fromX, toX, fromY, toY, 1);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Create Category");
        assertTrue(solo.searchText("Enter your details"));
        solo.enterText(0, "Category Place It 1");
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Spinner spinner = (Spinner) solo.getView(R.id.option_1);
                spinner.setSelection(3, true);
            }
        });
        
        solo.clickOnText("Save");
        solo.clickOnActionBarItem(R.id.action_placeits);
        solo.assertCurrentActivity("ListActivity", ListActivity.class);
        assertTrue(solo.searchText("amusement_park"));
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
