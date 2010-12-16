package it.unipr.ce.dsg.deus.example.mobilityexample;

/**
 * Base Message
 * @author erind
 *
 */
public class MyMessage {

	int senderKey = -1;
	byte[] payload = null;
	float ttl = 0;
	float sentTime = -1;
	
	public MyMessage(int senderKey, byte[] payload, float ttl, float sentTime) {
		super();
		this.senderKey = senderKey;
		this.payload = payload;
		this.ttl = ttl;
		this.sentTime = sentTime;
	}

	public int getSenderKey() {
		return senderKey;
	}

	public void setSenderKey(int senderKey) {
		this.senderKey = senderKey;
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

	public float getSentTime() {
		return sentTime;
	}

	public void setSentTime(float sentTime) {
		this.sentTime = sentTime;
	}
	
	/**
	 * Return messages size in byte
	 * @return
	 */
	public double getMessageSize()
	{
		double size = 0.0;
		
		size += (double)this.payload.length + ((double)Integer.SIZE + 2.0*(double)Float.SIZE)/8.0; 
		
		return size;
	}
	
}
