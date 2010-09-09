package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

public class D2VPeerDescriptor {
	
	private GeoLocation geoLocation = null;
	private int key = 0;
	
	public D2VPeerDescriptor(int key) {
		super();
		this.key = key;
	}
	
	public D2VPeerDescriptor(GeoLocation geoLocation, int key) {
		super();
		this.geoLocation = geoLocation;
		this.key = key;
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
	
	
	
}
