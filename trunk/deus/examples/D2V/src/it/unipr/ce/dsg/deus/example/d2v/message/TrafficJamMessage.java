package it.unipr.ce.dsg.deus.example.d2v.message;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class TrafficJamMessage extends Message{
	
	public static String typeName = "TRAFFIC_JAM_MESSAGE";
	
	private GeoLocation location = null;
	private float time = 0;
	private double range = 0.0;
	
	/**
	 * Payload ---> lat#lon#time#range
	 * 
	 * @param senderNodeId
	 * @param destinationNodeId
	 * @param payload
	 */
	public TrafficJamMessage(int senderNodeId, byte[] payload) {
		
		super(TrafficJamMessage.typeName, senderNodeId, payload);	
		
		String[] messageInfo = (new String(payload)).split("#");
		
		if(messageInfo.length == 4)
		{
			double lat = Double.parseDouble(messageInfo[0]);
			double lon = Double.parseDouble(messageInfo[1]);
			float t = Float.parseFloat(messageInfo[2]);
			double range = Double.parseDouble(messageInfo[3]);
			
			this.location = new GeoLocation(lat, lon);
			this.time = t;
			this.range = range;
		}
		else
		{
			System.err.println(this.getClass().getName()+" PAYLOAD ERROR !!!");
		}
	}
	
	@Override
	public String getMessageHash() {
		return this.location.getLatitude()+"#"+this.location.getLatitude();
	}
	
	@Override
	public boolean equals(Object obj) {
		TrafficJamMessage tfObj = (TrafficJamMessage)obj;
		if(this.location.equals(tfObj.getLocation()))
			return true;
		else
			return false;
	}
	
	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

}
