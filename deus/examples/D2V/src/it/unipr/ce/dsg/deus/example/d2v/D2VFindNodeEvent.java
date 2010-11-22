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
		
			//Add a new sent message of sender 
			D2VPeer senderPeer = ((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey()));
			senderPeer.incrementSentMessages();
			double kbValue = 0.0;
			String messageString = "FIND_NODE#48.0000000#76.0000000";
			kbValue = (double)messageString.getBytes().length / 1000.0;
			senderPeer.addSentKbAmountForDGT(kbValue+D2VPeerDescriptor.getStructureKbLenght());
			
			ArrayList<D2VPeerDescriptor> findNodeResult = currentNode.getGb().find_node(currentNode.createPeerInfo(),reqNode);
			
			//Increment sent message and Kb amount for receiver of FindNode that answer with the list of founded nodes
			currentNode.incrementSentMessages();
			messageString = "FIND_NODE_RESULT#";
			currentNode.addSentKbAmountForDGT((findNodeResult.size())*D2VPeerDescriptor.getStructureKbLenght());
			
			currentNode.insertPeer("D2VFindNodeEvent",reqNode,triggeringTime);
			((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey())).nlResults.get(reqNode.getKey()).addAll(findNodeResult);
			//((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey())).nlResults.get(reqNode.getKey()).addAll(currentNode.getGb().find_node(currentNode.createPeerInfo(),reqNode));
			
			D2VPeer reqPeer = ((D2VPeer)Engine.getDefault().getNodeByKey(reqNode.getKey()));
			
			reqPeer.setSentFindNode(reqPeer.getSentFindNode()+1);
			
			//System.out.println("ReqPeer:" + reqPeer.getPeerDescriptor().getKey() + " Find Node Count="+(reqPeer.getSentFindNode()) + " Limit:" + reqPeer.getFindNodeLimit());
			
			//Check Status of waited FIND_NODE for current Node 
			if(reqPeer.getSentFindNode() == reqPeer.getFindNodeLimit())
			{
				//System.out.println("ReqPeer:" + reqPeer.getPeerDescriptor().getKey() + " Starting LookUp Recursive Procedure ! ");
				
				/////////////////////// FORMAL ANALYSIS TEST //////////////////////////////
				/*
				Object[] foundNodes = reqPeer.nlResults.get(reqPeer.getKey()).getFoundNodes().toArray();
					
				int newNodeCount =0;
				//Add founded nodes to peerList
				for (int j = 0; j < reqPeer.nlResults.get(reqPeer.getKey()).size(); j++) {
					//System.out.println("Peer:"+((D2VPeerDescriptor) foundNodes[j]).getKey());
					if(reqPeer.insertPeer("D2VNodeLookUpRecursiveEvent",(D2VPeerDescriptor) foundNodes[j],triggeringTime) == true)
						newNodeCount  ++;
				}
					
				float discoveryValue = reqPeer.getDiscoveryMaxPeriod();
				if(newNodeCount != 0)
				{
				
					double k1 = reqPeer.getDiscoveryMaxPeriod();
					double k2 = (k1 - (double)reqPeer.getDiscoveryMinPeriod())/(double)(reqPeer.getDiscoveryPeriodPeerLimit()*reqPeer.getDiscoveryMinPeriod());
						
					discoveryValue = (float) (k1 / (double)((k2*(double)newNodeCount)+1));
				}
				reqPeer.setDiscoveryPeriod(discoveryValue);
				
				//At the beginning of a new discovery evaluate and save information about percentage of missing nodes
				if(reqPeer.getDiscoveryStatistics().size() < 100)
				{	
					double perMissing = reqPeer.getGb().evaluatePerMissingNodes(reqPeer.createPeerInfo());
					reqPeer.getDiscoveryStatistics().add(perMissing);
				}
				*/
				/////////////////////// FORMAL ANALYSIS TEST //////////////////////////////
				
				D2VPeerDescriptor first = null;

				if (reqPeer.nlResults.get(reqPeer.getKey()).size() != 0) {
					//first = reqPeer.nlResults.get(reqPeer.getKey()).getFoundNodes().first();
					first = reqPeer.nlResults.get(reqPeer.getKey()).first();
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
