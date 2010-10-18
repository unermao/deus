package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import it.unipr.ce.dsg.deus.core.Engine;

import java.util.ArrayList;

public class CityPath {

	private ArrayList<CityPathPoint> pathPoints = null;
	private boolean hasTrafficJam = false;
	private double lenght = 0.0;
	private int numOfCars = 0;
	private double speedLimit = 0.0;
	private boolean hasBadSurfaceCondition = false;
	private int badSurfaceIndex = -1;
	
	/**
	 * 
	 */
	public CityPath() {
		this.pathPoints = new ArrayList<CityPathPoint>();
		this.lenght = 0.0;
		this.numOfCars = 0;
		
		//Random select a speed limit
		int randomCase =  Engine.getDefault().getSimulationRandom().nextInt(4);
		
		switch (randomCase) {
		case 0:
			this.speedLimit = 30.0;
			break;
		case 1:
			this.speedLimit = 40.0;
			break;
		case 2:
			this.speedLimit = 50.0;
			break;
		case 3:
			this.speedLimit = 60.0;
			break;

			
		default:
			this.speedLimit = 30.0;
			break;
		}
		
	}
	
	public void addCityPathPoint(CityPathPoint point)
	{
		this.pathPoints.add(point);
	}
	
	public void incrementNumOfCars()
	{
		this.numOfCars++;
	}
	
	public void decrementNumOfCars()
	{
		if(this.numOfCars > 0)
			this.numOfCars--;
		else
			System.err.println(this.getClass().getName()+" --> decrementNumOfCars: ERROR NUM OF CARS = 0 !!!");
	}
	
	public void incrementPathLength(double len)
	{
		this.lenght += len;
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

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double lenght) {
		this.lenght = lenght;
	}

	public int getNumOfCars() {
		return numOfCars;
	}

	public void setNumOfCars(int numOfCars) {
		this.numOfCars = numOfCars;
	}

	public double getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = speedLimit;
	}

	public boolean isBadSurfaceCondition() {
		return hasBadSurfaceCondition;
	}

	public void setHasBadSurfaceCondition(boolean hadBadSurfaceCondition) {
		this.hasBadSurfaceCondition = hadBadSurfaceCondition;
	}

	public int getBadSurfaceIndex() {
		return badSurfaceIndex;
	}

	public void setBadSurfaceIndex(int badSurfaceIndex) {
		this.badSurfaceIndex = badSurfaceIndex;
	}

	public boolean isHasBadSurfaceCondition() {
		return hasBadSurfaceCondition;
	}
	
}
