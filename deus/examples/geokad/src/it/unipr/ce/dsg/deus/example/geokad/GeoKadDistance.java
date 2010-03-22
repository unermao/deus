package it.unipr.ce.dsg.deus.example.geokad;

public class GeoKadDistance {

	private double lat = 0.0;
	private double lon = 0.0;

	/**
	 * Calculate the distance between two different peers
	 * 
	 */
	public static double distance(GeoKadPeerInfo peerInfo1, GeoKadPeerInfo peerInfo2) {
		
		double lon1 = peerInfo1.getLongitude();
		double lon2 = peerInfo2.getLongitude();
		double lat1 = peerInfo1.getLatitude();
		double lat2 = peerInfo2.getLatitude();
		
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
	 * Calculate the distance between two different peers
	 * 
	 */
	public static double distance(GeoKadPeer peer1, GeoKadPeer peer2) {
		
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
	
	public static double distance(double lon1,double lat1,double lon2,double lat2) {
		
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
	private static double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	/**
	 * This function converts radians to decimal degrees     
	 */
	private static double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}
}
