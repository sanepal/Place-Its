package com.ucsd.cs110w.group16.placeits;

import java.util.ArrayList;
import java.util.List;

public class PlaceItManager 
{
    List<PlaceIt> placeits = new ArrayList<PlaceIt>();
	
	public void addUnscheduledPlaceIt(PlaceIt p)
	{
		placeits.add(p);
	}
	
	public void addScheduledPlaceIt(int dayOfWeek, int WeekPeriod, PlaceIt p)
	{
		
	}
	
	public List getActivePlaceIts()
	{
		List<PlaceIt> list = new ArrayList<PlaceIt>();
		
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (current.isActive())
			{
				list.add(current);
			}
		}
		
		return list;
	}
	
	public List getInActivePlaceIts()
	{
		List<PlaceIt> list = new ArrayList<PlaceIt>();
		
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (!current.isActive())
			{
				list.add(current);
			}
		}
		
		return list;
	}
	
	public PlaceIt getPlaceIt(int ID)
	{
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (current.getID().equals(ID))
			{
				return current;
			}
		}
		return null;
	}
	
	public void removePlaceIt(PlaceIt p)
	{
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (current.equals(p))
			{
				placeits.remove(i);
				break;
			}
		}
	}
	
	public void setInActive(PlaceIt p)
	{
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (current.equals(p))
			{
				current.setStatus(true);
				break;
			}
		}
	}
	
	public void setActive(PlaceIt p)
	{
		for (int i = 0; i < placeits.size(); i++)
		{
			PlaceIt current = placeits.get(i);
			if (current.equals(p))
			{
				current.setStatus(false);
				break;
			}
		}
	}
}
