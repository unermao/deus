package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import java.util.ArrayList;

public class CityPath {

	private ArrayList<CityPathPoint> pathPoints = null;
	private boolean hasTrafficJam = false;
	
	/**
	 * 
	 */
	public CityPath() {
		this.pathPoints = new ArrayList<CityPathPoint>();
	}
	
	public void addCityPathPoint(CityPathPoint point)
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
	
	public ArrayList<CityPathPoint> getPathPoints() {
		return pathPoints;
	}

	public void setPathPoints(ArrayList<CityPathPoint> pathPoints) {
		this.pathPoints = pathPoints;
	}

	public boolean isHasTrafficJam() {
		return hasTrafficJam;
	}

	public void setHasTrafficJam(boolean hasTrafficJam) {
		this.hasTrafficJam = hasTrafficJam;
	}
	
}
