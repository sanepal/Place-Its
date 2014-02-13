package com.ucsd.cs110w.group16.placeits;
/*
public class PlaceIt
{
	String name, description;
	boolean status;
	String alarmID, geofenceID;
	Long ID;
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
	public void setID(long id)
	{
		this.ID = id;
	}
	
	//Get the ID of the PlaceIt
	public Long getID()
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
	
}*/

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center (latitude and longitude position) and radius.
 */
public class PlaceIt {
    // Instance variables
    
    private final String mId;
    private final Integer intId;
    private final String title;
    private final String desc;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius = (float) 804.5;
    private long mExpirationDuration = Geofence.NEVER_EXPIRE;
    private int mTransitionType = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;
    private boolean isActive;

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public PlaceIt(
            Integer geofenceId,
            String title,
            String desc,
            double latitude,
            double longitude,
            boolean isActive) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        this.mId = geofenceId.toString();
        this.intId = geofenceId;
        // Center of the geofence
        this.mLatitude = latitude;
        this.mLongitude = longitude;

        this.title = title;
        this.desc = desc;
        this.isActive = isActive;
    }
    // Instance field getters

    /**
     * Get the geofence ID
     * @return A PlaceIt ID
     */
    public String getId() {
        return mId;
    }
    
    public Integer getIntId() {
        return intId;
    }
    
    /**
     * Get the geofence latitude
     * @return A latitude value
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Get the geofence longitude
     * @return A longitude value
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Get the geofence radius
     * @return A radius value
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Get the geofence expiration duration
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    /**
     * Get the geofence transition type
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return mTransitionType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setStatus(boolean status){
    	isActive = status;
    }

    /**
     * Creates a Location Services Geofence object from a
     * PlaceIt.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                       .setRequestId(getId())
                       .setTransitionTypes(mTransitionType)
                       .setCircularRegion(
                               getLatitude(),
                               getLongitude(),
                               getRadius())
                       .setExpirationDuration(mExpirationDuration)
                       .build();
    }
}
