package it.unipr.ce.dsg.deus.example.nsam;


public class CompositionElement {
	
	private NsamService service = null;
	private NsamPeer peer = null;
//	private String peerId = null;

	public CompositionElement(NsamService serv, NsamPeer peer){
		this.service=serv;
		this.peer=peer;  
	}

	public NsamService getService() {
		return service;
	}

	public void setService(NsamService service) {
		this.service = service;
	}

	public NsamPeer getPeer() {
		return peer;
	}

	public void setPeer(NsamPeer peer) {
		this.peer = peer;
	}

	
/*	public String getPeerId() {
		return peerId;
	}   

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}   */

	
}
