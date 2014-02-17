package com.ucsd.cs110w.group16.placeits.test;

import com.robotium.solo.Solo;
import com.ucsd.cs110w.group16.placeits.ListActivity;
import android.test.ActivityInstrumentationTestCase2;

public class ListActivityTest extends
        ActivityInstrumentationTestCase2<ListActivity> {
    private Solo solo;
    public ListActivityTest() {
        super(ListActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
}
