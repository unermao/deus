package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import java.util.ArrayList;

import it.unipr.ce.dsg.deus.example.d2v.D2VTrafficElement;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class CityPathPoint extends GeoLocation {

	private D2VTrafficElement te = null;
	private ArrayList<Double> logMonitoredSpeed = null;
	private String surfaceCondition = null;
	
	public CityPathPoint(double latitude, double longitude) {
		super(latitude, longitude);
		this.logMonitoredSpeed = new ArrayList<Double>();
	}

	public void addMonitoredSpeed(double speedValue)
	{
		this.logMonitoredSpeed.add(speedValue);
	}
	
	public D2VTrafficElement getTe() {
		return te;
	}

	public void setTe(D2VTrafficElement te) {
		this.te = te;
	}

	public ArrayList<Double> getLogMonitoredSpeed() {
		return logMonitoredSpeed;
	}

	public void setLogMonitoredSpeed(ArrayList<Double> logMonitoredSpeed) {
		this.logMonitoredSpeed = logMonitoredSpeed;
	}



	public String getSurfaceCondition() {
		return surfaceCondition;
	}



	public void setSurfaceCondition(String surfaceCondition) {
		this.surfaceCondition = surfaceCondition;
	}
	
	

}
