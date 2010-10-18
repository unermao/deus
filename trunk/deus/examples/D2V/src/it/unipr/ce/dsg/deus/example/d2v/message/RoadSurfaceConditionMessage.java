package it.unipr.ce.dsg.deus.example.d2v.message;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

public class RoadSurfaceConditionMessage extends TrafficInformationMessage {

	public static String typeName = "ROAD_SURFACE_MESSAGE";
	
	public RoadSurfaceConditionMessage(String type, int senderNodeId,
			GeoLocation location, float time, double range,float ttl, byte[] payload) {
		super(type, senderNodeId, location, time, range,ttl, payload);
	}

}
