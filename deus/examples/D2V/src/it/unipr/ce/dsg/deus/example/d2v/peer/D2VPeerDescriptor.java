package it.unipr.ce.dsg.deus.example.d2v.peer;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

public class D2VPeerDescriptor {
	
	private GeoLocation geoLocation = null;
	private int key = 0;
	private float timeStamp = 0;
	
	public D2VPeerDescriptor(int key) {
		super();
		this.key = key;
	}
	
	public D2VPeerDescriptor(GeoLocation geoLocation, int key) {
		super();
		this.geoLocation = geoLocation;
		this.key = key;
	}
	
	public D2VPeerDescriptor(GeoLocation geoLocation, int key, float timeStamp) {
		super();
		this.geoLocation = geoLocation;
		this.key = key;
		this.timeStamp = timeStamp;
	}

	@Override
	public boolean equals(Object obj) {
		D2VPeerDescriptor descrObj = (D2VPeerDescriptor)obj;
		if(descrObj.key == this.key)
			return true;
		else
			return false;
	}
	
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}
	
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}

	public float getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}	
}
