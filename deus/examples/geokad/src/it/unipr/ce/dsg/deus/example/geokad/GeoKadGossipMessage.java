package it.unipr.ce.dsg.deus.example.geokad;

public class GeoKadGossipMessage {

	private GeoKadPeer peer = null;
	private int counter = 0;
	
	public GeoKadGossipMessage(GeoKadPeer peer, int counter) {
		super();
		this.peer = peer;
		this.counter = counter;
	}
	
	public void increaseCounter()
	{
		this.counter ++;
	}
	
	public void decreaseCounter()
	{
		this.counter--;
	}
	
	public GeoKadPeer getPeer() {
		return peer;
	}
	
	public void setPeer(GeoKadPeer peer) {
		this.peer = peer;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	
	
}
