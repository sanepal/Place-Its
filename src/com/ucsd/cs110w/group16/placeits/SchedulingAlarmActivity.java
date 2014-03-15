package com.ucsd.cs110w.group16.placeits;

import java.util.Calendar;
import java.lang.Math;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;

public class SchedulingAlarmActivity extends Activity implements OnClickListener{

	final static private long ONE_SECOND = 1000;
	//final static private long TEN_SECONDS = ONE_SECOND * 10;
	//final static private long TWENTY_SECONDS = ONE_SECOND * 20;
	//final static private long THIRTY_SECONDS = ONE_SECOND * 30;
	final static private long ONE_MINUTE = ONE_SECOND * 60;
	final static private long ONE_HOUR = ONE_MINUTE * 60;
	final static private long DAILY = ONE_HOUR * 24;
	//final static private long DAILY_2 = DAILY * 2;
	final static private long WEEKLY = DAILY * 7;
	//final static private long WEEKLY_2 = WEEKLY * 2;
	
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;
	Calendar calendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedulingalarm_activity);
		setup();
		//findViewById(R.id.sunday).setOnClickListener(this);
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

	public void sunday(View v){
		int days = Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), WEEKLY, pi );
		finish();
	}
	
	public void monday(View v){
		int days = Calendar.MONDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),WEEKLY,pi);
		finish();
	}
	
	public void tuesday(View v){
		int days = Calendar.TUESDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),WEEKLY,pi);
		finish();
	}
	
	public void wednesday(View v){
		int days = Calendar.WEDNESDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),WEEKLY,pi);
		finish();
	}
	
	public void thursday(View v){
		int days = Calendar.THURSDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), WEEKLY, pi );
		finish();
	}
	
	public void friday(View v){
		int days = Calendar.FRIDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), WEEKLY, pi );
		finish();
	}
	
	public void saturday(View v){
		int days = Calendar.SATURDAY - calendar.get(Calendar.DAY_OF_WEEK);
		if (days > 0)
		{
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else if (days == 0)
		{
			days = 7;
			int setDay = days - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		else
		{
			days = Math.abs(days);
			int temp = 7 - days;
			int setDay = temp - 1;
			calendar.add(Calendar.DATE,setDay);
		}
		am.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), WEEKLY, pi );
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}