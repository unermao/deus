package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import java.util.ArrayList;

public class CityPath {

	private ArrayList<GeoLocation> pathPoints = null;
	private int index = 0;

	/**
	 * 
	 */
	public CityPath() {
		this.pathPoints = new ArrayList<GeoLocation>();
		this.index = 0;
	}

	/**
	 * 
	 */
	public CityPath(CityPath path) {
		this.pathPoints = new ArrayList<GeoLocation>(path.getPathPoints());
		this.index = 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int nextStep()
	{
		if(this.index < this.pathPoints.size() -1)
		{
			this.index ++;
			return (this.pathPoints.size()-this.index-1);		
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public GeoLocation getCurrentLocation()
	{
		return this.pathPoints.get(index);
	}
	
	public void addGeoLocation(GeoLocation point)
	{
		this.pathPoints.add(point);
	}
	
	public GeoLocation getStartPoint()
	{
		if(this.pathPoints.size() > 0)				
			return this.pathPoints.get(0);
		else 
			return null;
	}
	
	public GeoLocation getEndPoint()
	{
		if(this.pathPoints.size() > 0)
			return this.pathPoints.get(this.pathPoints.size()-1);
		else 
			return null;
	}
	
	public ArrayList<GeoLocation> getPathPoints() {
		return pathPoints;
	}

	public void setPathPoints(ArrayList<GeoLocation> pathPoints) {
		this.pathPoints = pathPoints;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	
	
}
