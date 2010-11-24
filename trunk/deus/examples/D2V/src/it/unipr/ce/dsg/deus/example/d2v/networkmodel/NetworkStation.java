package it.unipr.ce.dsg.deus.example.d2v.networkmodel;

import java.util.ArrayList;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

public class NetworkStation extends GeoLocation {
	
	private double radius = 0.0;
	private double maxUplink = 0.0;
	private double maxDownlink = 0.0;
	
	private ArrayList<Integer> connectedUsers = null;

	public NetworkStation(double latitude, double longitude, double radius,double maxUplink, double maxDownlink) {
		super(latitude, longitude);
		
		this.radius = radius;
		this.maxUplink = maxUplink;
		this.maxDownlink = maxDownlink;
	
		connectedUsers = new ArrayList<Integer>();
	}

	public String getNSString()
	{
		return this.getClass().getName()+";"+this.getLatitude()+";"+this.getLongitude()+";"+this.radius+";"+this.maxUplink+";"+this.maxDownlink;
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getMaxUplink() {
		return maxUplink;
	}

	public void setMaxUplink(double maxUplink) {
		this.maxUplink = maxUplink;
	}

	public double getMaxDownlink() {
		return maxDownlink;
	}

	public void setMaxDownlink(double maxDownlink) {
		this.maxDownlink = maxDownlink;
	}

	public ArrayList<Integer> getConnectedUsers() {
		return connectedUsers;
	}

	public void setConnectedUsers(ArrayList<Integer> connectedUsers) {
		this.connectedUsers = connectedUsers;
	}

}
