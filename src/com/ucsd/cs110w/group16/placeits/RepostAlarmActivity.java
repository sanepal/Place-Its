package com.ucsd.cs110w.group16.placeits;

import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;

public class RepostAlarmActivity extends Activity implements OnClickListener{

	final static private long ONE_SECOND = 1000;
	final static private long TEN_SECONDS = ONE_SECOND * 10;
	//final static private long TWENTY_SECONDS = ONE_SECOND * 20;
	//final static private long THIRTY_SECONDS = ONE_SECOND * 30;
	final static private long ONE_MINUTE = ONE_SECOND * 60;
	final static private long ONE_HOUR = ONE_MINUTE * 60;
	final static private long DAILY = ONE_HOUR * 24;
	//final static private long DAILY_2 = DAILY * 2;
	final static private long WEEKLY = DAILY * 6;
	final static private long WEEKLY_2 = DAILY * 13;
	
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;
	Calendar calendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repostalarm_activity);
		setup();
		//findViewById(R.id.one_day).setOnClickListener(this);
	}
	
	private void setup() {
		
		Intent myInt = getIntent();
		int i = myInt.getIntExtra("com.ucsd.cs110w.group16.placeits.Id",0);
        pi = PendingIntent.getBroadcast( this, i, new Intent("com.ucsd.cs110w.group16.placeits"), 0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    
        calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59 );
		calendar.set(Calendar.SECOND,59);
	}

	public void one_day(View v){
		
		am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
		finish();
	}
	
	public void two_days(View v){
		am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() + DAILY,pi);
		finish();
	}
	
	public void one_week(View v){
		am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() + WEEKLY,pi);
		finish();
	}
	
	public void two_weeks(View v){
		am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() + WEEKLY_2,pi);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}