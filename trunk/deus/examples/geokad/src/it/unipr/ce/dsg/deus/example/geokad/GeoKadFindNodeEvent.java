package it.unipr.ce.dsg.deus.example.geokad;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

public class GeoKadFindNodeEvent extends NodeEvent {

	private GeoKadPeerInfo reqNode = null;
	private ArrayList<GeoKadPeerInfo> periodicPeerList = new ArrayList<GeoKadPeerInfo>();
	
	//private int resourceKey = -1;

	public GeoKadFindNodeEvent(String id, Properties params,
			Process parentProcess, GeoKadPeerInfo peer)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		reqNode = peer;

		initialize();
	}

	public Object clone() {
		GeoKadFindNodeEvent clone = (GeoKadFindNodeEvent) super.clone();
	//	clone.resourceKey = -1;
		clone.reqNode = null;
		periodicPeerList = new ArrayList<GeoKadPeerInfo>();
		
		return clone;
	}

//	public int getResourceKey() {
//		return resourceKey;
//	}

	public void initialize() {
	}

	public void run() throws RunException {

		GeoKadPeer currentNode = (GeoKadPeer) getAssociatedNode();

		if (currentNode.getKey() != reqNode.getKey()) {
			
			//System.out.println("Node: "+ currentNode.getKey() +" Adding Gossip Peer: " + this.periodicPeerList.size());
			
			for(int i=0; i<this.periodicPeerList.size(); i++)
				currentNode.insertPeer(this.periodicPeerList.get(i));
			
			
			//add a new sent message
			currentNode.setSentMessages(currentNode.getSentMessages() + 1);
			
			currentNode.insertPeer(reqNode);
			((GeoKadPeer)Engine.getDefault().getNodeByKey(reqNode.getKey())).nlResults.get(reqNode.getKey()).addAll(currentNode.find_node(reqNode));
		
		}
		
		//		if (resourceKey == -1) {
//			Random random = new Random();
//			resourceKey = random.nextInt(Engine.getDefault().getKeySpaceSize());
//		}
//
//		reqNode.nlResults.get(resourceKey).addAll(
//				currentNode.find_node(resourceKey));
//		if (currentNode.getKey() != reqNode.getKey()) {
//			currentNode.insertPeer(reqNode);
//		}

	}

//	public void setResourceKey(int resourceKey) {
//		this.resourceKey = resourceKey;
//	}

	public GeoKadPeerInfo getRequestingNode() {
		return reqNode;
	}

	public void setRequestingNode(GeoKadPeerInfo reqNode) {
		this.reqNode = reqNode;
	}

	public GeoKadPeerInfo getReqNode() {
		return reqNode;
	}

	public void setReqNode(GeoKadPeerInfo reqNode) {
		this.reqNode = reqNode;
	}

	public ArrayList<GeoKadPeerInfo> getPeriodicPeerList() {
		return periodicPeerList;
	}

	public void setPeriodicPeerList(ArrayList<GeoKadPeerInfo> periodicPeerList) {
		this.periodicPeerList = periodicPeerList;
	}
}
