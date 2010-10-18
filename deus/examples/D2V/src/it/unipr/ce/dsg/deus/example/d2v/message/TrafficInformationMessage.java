package it.unipr.ce.dsg.deus.example.d2v.message;

import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;

/**
 * 
 * Message base class. Is is used to exchange data between two different nodes.
 * At the moment there are not the management of the type of used protocol like UDP 
 * or TCP. These features will be added in the future version.
 *
 * Message Structure: MSG_TYPE | SenderID | GeoLocation | time | range | PayLoad
 *  
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class TrafficInformationMessage {

	private String type = null;
	private int senderNodeId;
	private byte[] payload;
	private float ttl = 1000;
	private GeoLocation location = null;
	private float time = 0;
	private double range = 0.0;
	
	/**
	 * Build a message instance starting from input parameters
	 * 
	 * @param type
	 * @param senderNodeId
	 * @param destinationNodeId
	 * @param payload
	 */
	public TrafficInformationMessage(String type, int senderNodeId,GeoLocation location, float time, double range, byte[] payload) {
		super();
		this.type = type;
		this.senderNodeId = senderNodeId;
		this.payload = payload;
		this.location = location;
		this.time = time;
		this.range = range;
	}
	
	public String getMessageHash() {
		return this.type+"#"+this.location.getLatitude()+"#"+this.location.getLatitude();
	}
	
	@Override
	public boolean equals(Object obj) {
		TrafficInformationMessage tfObj = (TrafficInformationMessage)obj;
		if(this.location.equals(tfObj.getLocation()) && tfObj.getType().equals(this.type))
			return true;
		else
			return false;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSenderNodeId() {
		return senderNodeId;
	}
	public void setSenderNodeId(int senderNodeId) {
		this.senderNodeId = senderNodeId;
	}
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public float getTtl() {
		return ttl;
	}

	public void setTtl(float ttl) {
		this.ttl = ttl;
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
