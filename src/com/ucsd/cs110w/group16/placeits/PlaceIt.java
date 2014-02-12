package com.ucsd.cs110w.group16.placeits;

public class PlaceIt
{
	String name, description;
	boolean status;
	String alarmID, geofenceID;
	Integer ID;
	double longitude, latitude;
	
	//Sets the name of the PlaceIt to be displayed on map
	public void setName(String name)
	{
		this.name = name;
	}

	//Gets the name of the PlaceIt
	public String getName()
	{
		return name;
	}
	
	//Sets the description of the PlaceIt
	public void setDescription(String desc)
	{
		this.description = desc;
	}
	
	//Gets the description of the PlaceIt
	public String getDescription()
	{
		return description;
	}
	
	//Set the status of the PlaceIt
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	
	//Get whether the PlaceIt is active or not
	public boolean isActive() 
	{
		return status;
	}
	
	//Set the Alarm ID for the PlaceIt
	public void setAlarmID(String id)
	{
		this.alarmID = id;
	}
	
	//Gets the Alarm ID for the PlaceIt
	public String getAlarmID()
	{
		return alarmID;
	}
	
	//Sets the GeoFence ID for the PlaceIt
	public void setGeofenceID(String id)
	{
		this.geofenceID = id;
	}
	
	//Gets the Geofence ID for the PlaceIt
	public String getGeofenceID() 
	{
		return geofenceID;
	}
	
	//Set the ID of the PlaceIt
	public void setID(int id)
	{
		this.ID = id;
	}
	
	//Get the ID of the PlaceIt
	public Integer getID()
	{
		return ID;
	}
	
	//Sets the position of the PlaceIt
	public void setPosition(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	//Gets the Latitude of the PlaceIt
	public double getLatitude()
	{
		return longitude;
	}
	
	//Gets the Longitude of the PlaceIt
	public double getLongitude() 
	{
		return latitude;
	}
	
	public boolean equals(PlaceIt p)
	{
		if (this.ID.equals(p.ID))
			return true;
		return false;
	}
	
}
