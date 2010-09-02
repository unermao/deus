package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

public class GeoLocation {

	private double latitude = 0.0;
	private double longitude = 0.0;
	private float timeStamp = 0;
	
	public GeoLocation(double latitude, double longitude, float timeStamp) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.timeStamp = timeStamp;
	}

	@Override
	public boolean equals(Object obj) {
	
		GeoLocation locObj = (GeoLocation)obj;
		
		if(this.latitude == locObj.latitude && this.longitude == locObj.longitude)
			return true;
		else
			return false;
		
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
	public float getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
