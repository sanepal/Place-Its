package com.ucsd.cs110w.group16.placeits;

public class PlaceIt
{
	String name, description;
	boolean status;
	String alarmID, geofenceID;
	int ID;
	double longitude, latitude;
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
	public void setDescription(String desc)
	{
		this.description = desc;
	}
	public String getDescription()
	{
		return description;
	}
	
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	public boolean isActive() 
	{
		return status;
	}
	
	public void setAlarmID(String id)
	{
		this.alarmID = id;
	}
	public String getAlarmID()
	{
		return alarmID;
	}
	
	public void setGeofenceID(String id)
	{
		this.geofenceID = id;
	}
	public String getGeofenceID() 
	{
		return geofenceID;
	}
	
	public void setID(int id)
	{
		this.ID = id;
	}
	public int getID()
	{
		return ID;
	}
	
	public void setPosition(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public double getLatitude()
	{
		return longitude;
	}
	public double getLongitude() 
	{
		return latitude;
	}
	
}
