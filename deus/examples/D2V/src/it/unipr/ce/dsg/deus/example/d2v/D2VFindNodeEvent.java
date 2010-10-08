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
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

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
		
		//DebugLog log = new DebugLog();
		//log.printStart(currentNode.getKey(),this.getClass().getName(),triggeringTime);
		
		if (currentNode.getKey() != reqNode.getKey()) {
		
			//add a new sent message
			//currentNode.setSentMessages(currentNode.getSentMessages() + 1);
			currentNode.incrementSentMessages();
			
			currentNode.insertPeer("D2VFindNodeEvent",reqNode);
			((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey())).nlResults.get(reqNode.getKey()).addAll(currentNode.getGb().find_node(currentNode.createPeerInfo(),reqNode));
			
			D2VPeer reqPeer = ((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey()));
			
			reqPeer.setSentFindNode(reqPeer.getSentFindNode()+1);
			
			//System.out.println("ReqPeer:" + reqPeer.getPeerDescriptor().getKey() + " Find Node Count="+(reqPeer.getSentFindNode()) + " Limit:" + reqPeer.getFindNodeLimit());
			
			//Check Status of waited FIND_NODE for current Node 
			if(reqPeer.getSentFindNode() == reqPeer.getFindNodeLimit())
			{
				//System.out.println("ReqPeer:" + reqPeer.getPeerDescriptor().getKey() + " Starting LookUp Recursive Procedure ! ");
				
				D2VPeerDescriptor first = null;

				if (reqPeer.nlResults.get(reqPeer.getKey()).size() != 0) {
					first = reqPeer.nlResults.get(reqPeer.getKey()).getFoundNodes().first();
				}
				
				try {
					
					D2VNodeLookUpRecursiveEvent nlk = (D2VNodeLookUpRecursiveEvent) new D2VNodeLookUpRecursiveEvent("node_lookup", params, null, first).createInstance(triggeringTime+1);
					nlk.setCloserElement(first);
					nlk.setOneShot(true);
					nlk.setAssociatedNode(reqPeer);					
					Engine.getDefault().insertIntoEventsList(nlk);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			//else //Increment sent Find Node
				//System.out.println("ReqPeer:" + reqPeer.getPeerDescriptor().getKey() + " Waiting other FIND_NODE_RPC");
		}
			
		//log.printEnd(currentNode.getKey(),this.getClass().getName(),triggeringTime);
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
