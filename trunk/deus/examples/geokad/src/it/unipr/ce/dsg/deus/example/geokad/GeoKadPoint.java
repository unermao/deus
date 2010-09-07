package it.unipr.ce.dsg.deus.example.geokad;

public class GeoKadPoint {

	private double lat = 0.0;
	private double lon = 0.0;
	
	public GeoKadPoint(double lat, double lon) {
		super();
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
	
}
