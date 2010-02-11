package it.unipr.ce.dsg.deus.example.geokad;

public class GeoKadPeerInfo {
	
	private int key = 0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	
	public GeoKadPeerInfo(int key, double latitude, double longitude) {
		super();
		this.key = key;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
	
}
