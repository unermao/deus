package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;

public class ServedPeer {

	private StreamingPeer peer;
	private int layer;
	
	
	public ServedPeer (){
		super();
	}
	
	public ServedPeer (StreamingPeer peer, int l){
		this.peer = peer;
		this.layer = l;
	}
	
	public StreamingPeer getPeer() {
		return peer;
	}
	public void setPeer(StreamingPeer peer) {
		this.peer = peer;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	
}
