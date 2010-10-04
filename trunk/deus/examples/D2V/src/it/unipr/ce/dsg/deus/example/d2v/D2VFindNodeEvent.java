package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;

public class D2VFindNodeEvent extends NodeEvent {

	private D2VPeerDescriptor reqNode = null;
		
	//private int resourceKey = -1;

	public D2VFindNodeEvent(String id, Properties params,
			Process parentProcess, D2VPeerDescriptor peer)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		reqNode = peer;

		initialize();
	}

	public Object clone() {
		D2VFindNodeEvent clone = (D2VFindNodeEvent) super.clone();
		clone.reqNode = null;
		return clone;
	}

	public void initialize() {
	}

	public void run() throws RunException {

		D2VPeer currentNode = (D2VPeer) getAssociatedNode();

		if (currentNode.getKey() != reqNode.getKey()) {
		
			//add a new sent message
			currentNode.setSentMessages(currentNode.getSentMessages() + 1);
			
			currentNode.insertPeer(reqNode);
			((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey())).nlResults.get(reqNode.getKey()).addAll(currentNode.getGb().find_node(currentNode.createPeerInfo(),reqNode));
		
		}
		
	}

//	public void setResourceKey(int resourceKey) {
//		this.resourceKey = resourceKey;
//	}

	public D2VPeerDescriptor getRequestingNode() {
		return reqNode;
	}

	public void setRequestingNode(D2VPeerDescriptor reqNode) {
		this.reqNode = reqNode;
	}

	public D2VPeerDescriptor getReqNode() {
		return reqNode;
	}

	public void setReqNode(D2VPeerDescriptor reqNode) {
		this.reqNode = reqNode;
	}

}
