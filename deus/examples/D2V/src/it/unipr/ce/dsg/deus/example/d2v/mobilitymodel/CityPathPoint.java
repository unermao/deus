package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import it.unipr.ce.dsg.deus.example.d2v.D2VTrafficElement;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class CityPathPoint extends GeoLocation {

	private D2VTrafficElement te = null;
	
	public CityPathPoint(double latitude, double longitude) {
		super(latitude, longitude);
	}

	public D2VTrafficElement getTe() {
		return te;
	}

	public void setTe(D2VTrafficElement te) {
		this.te = te;
	}
	
	

}
