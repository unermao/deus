package it.unipr.ce.dsg.deus.example.d2v.message;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class TrafficJamMessage extends TrafficInformationMessage{
	
	public static String typeName = "TRAFFIC_JAM_MESSAGE";

	
	/**
	 * Payload ---> lat#lon#time#range
	 * 
	 * @param senderNodeId
	 * @param destinationNodeId
	 * @param payload
	 */
	public TrafficJamMessage(int senderNodeId, GeoLocation location,float time, double range, byte[] payload) {
		
		super(TrafficJamMessage.typeName, senderNodeId, location,time,range,payload);	
	}
	
}
