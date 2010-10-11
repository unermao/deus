package it.unipr.ce.dsg.deus.example.d2v.util;

import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;

import java.util.Comparator;

public class GeoDistanceComparator implements Comparator<D2VPeerDescriptor>{

	private double lat = 0.0;
	private double lon = 0.0;
	private D2VPeerDescriptor peer = null;

	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public GeoDistanceComparator(D2VPeerDescriptor peer) {
		this.peer  = peer;
		this.lat = peer.getGeoLocation().getLatitude();
		this.lon = peer.getGeoLocation().getLongitude();
	}
	
	/**
	 * Calculate the distance between two different peers
	 * 
	 */
	public double distance(D2VPeerDescriptor peer1, D2VPeerDescriptor peer2) {
		
		double lon1 = peer1.getGeoLocation().getLongitude();
		double lon2 = peer2.getGeoLocation().getLongitude();
		double lat1 = peer1.getGeoLocation().getLatitude();
		double lat2 = peer2.getGeoLocation().getLatitude();
		
		double theta = lon1 - lon2;

		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		dist = dist * 1.609344;
		
//		if (unit == "K") {
//		    dist = dist * 1.609344;
//		} else if (unit == "N") {
//			dist = dist * 0.8684;
//		    }

		return (dist);

	}
	
	/**
	 *   This function converts decimal degrees to radians        
	 */
	private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	/**
	 * This function converts radians to decimal degrees     
	 */
	private double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}

	public int compare(D2VPeerDescriptor o1, D2VPeerDescriptor o2) {
		
		double dist1 = distance(peer, o1);
		double dist2 = distance(peer, o2);
		
		if(o1.getKey() == o2.getKey())
			return 0;
		
		if(dist1 < dist2)
			return -1;
	
		if(dist1 > dist2)
			return 1;
		
		return -1;
	}
}
