package it.unipr.ce.dsg.deus.example.geokad;

public class GeoKadPeerInfo {
	
	private int key = 0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private int peerCounter = 0;
	private float timeStamp = 0;
	
	
	public GeoKadPeerInfo(int key, double latitude, double longitude, int peerCounter,float timeStamp) {
		super();
		this.key = key;
		this.latitude = latitude;
		this.longitude = longitude;
		this.peerCounter = peerCounter;
		this.timeStamp = timeStamp;
		
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

	public int getPeerCounter() {
		return peerCounter;
	}

	public void setPeerCounter(int peerCounter) {
		this.peerCounter = peerCounter;
	}



	@Override
	public boolean equals(Object obj) {
		if(((GeoKadPeerInfo) obj).getKey() == this.getKey())
			return true;
		else
			return false;
	}



	public float getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
}
