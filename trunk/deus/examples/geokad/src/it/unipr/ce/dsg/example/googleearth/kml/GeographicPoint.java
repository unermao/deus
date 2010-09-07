package it.unipr.ce.dsg.example.googleearth.kml;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class GeographicPoint {

	private double lat = 0.0;
	private double lon = 0.0;
	private double altitude = 0.0;
	
	public GeographicPoint(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	
}
