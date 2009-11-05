package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Comparator;

public class GeoKadDistanceComparator implements Comparator<GeoKadPeer>{

	private double lat = 0.0;
	private double lon = 0.0;

	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public GeoKadDistanceComparator(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	/**
	 * Calculate the distance between two different peers
	 * 
	 */
	public double distance(GeoKadPeer peer1, GeoKadPeer peer2) {
		
		double lon1 = peer1.getLongitude();
		double lon2 = peer2.getLongitude();
		double lat1 = peer1.getLatitude();
		double lat2 = peer2.getLatitude();
		
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

	@Override
	public int compare(GeoKadPeer o1, GeoKadPeer o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
