package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

public class D2VNodeLookUpRecursiveEvent extends D2VDiscoveryEvent {

	private D2VPeerDescriptor closerElement = null;
	
	//private boolean findNodeK = false;

	public D2VNodeLookUpRecursiveEvent(String id, Properties params,
			Process parentProcess, D2VPeerDescriptor closerElem)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		closerElement = closerElem;
		initialize();
	}

	public Object clone() {
		D2VNodeLookUpRecursiveEvent clone = (D2VNodeLookUpRecursiveEvent) super
				.clone();
		//clone.findNodeK = false;
		clone.closerElement = null;
	
		return clone;
	}

	private void initialize() {
	}

	/*
	public boolean isFindNodeK() {
		return findNodeK;
	}
	*/
	
	public void run() throws RunException {
		
		D2VPeer currNode = (D2VPeer) this.getAssociatedNode();		
	
		//DebugLog log = new DebugLog();
		//log.printStart(currNode.getKey(),this.getClass().getName(),triggeringTime);
		
		currNode.setAvDiscoveryStepCounter(currNode.getAvDiscoveryStepCounter()+1);
		
		currNode.setSentFindNode(0);
		
		//Increment number of sent messages
		//currNode.setSentMessages(currNode.getSentMessages() + 1);

		D2VPeerDescriptor first = null;

		if (currNode.nlResults.get(currNode.getKey()).size() != 0) {
			//first = currNode.nlResults.get(currNode.getKey()).getFoundNodes().first();
			first = currNode.nlResults.get(currNode.getKey()).first();
		}

		if (closerElement == first) {
			
			//if (this.isFindNodeK()) { 		
			if (currNode.isFindNodeK()) {
				
				//Store Step Counter
				currNode.setDiscoveryCounter(currNode.getDiscoveryCounter()+1);
				
				// no new result even from the first k nodes
				Object[] foundNodes = currNode.nlResults.get(currNode.getKey()).getFoundNodes().toArray();
				
				//Add founded nodes to peerList
				//System.out.println("LOOKUP END ! ---> ReqPeer:" + currNode.getPeerDescriptor().getKey() + " Found Nodes="+(foundNodes.length));
				//System.out.println("####################################### NEW NODES: " + foundNodes.length);
				int newNodeCount = 0;
				for (int j = 0; j < currNode.nlResults.get(currNode.getKey()).size(); j++) {
					//System.out.println("Peer:"+((D2VPeerDescriptor) foundNodes[j]).getKey());
					if(currNode.insertPeer("D2VNodeLookUpRecursiveEvent",(D2VPeerDescriptor) foundNodes[j]) == true)
						newNodeCount ++;
				}
				
				//EVALUATE DISCOVERY PERIOD VALUE
				double k1 = currNode.getDiscoveryMaxPeriod();
				double k2 = (k1 - (double)currNode.getDiscoveryMinPeriod())/(double)(currNode.getDiscoveryMaxPeerNumber()*currNode.getDiscoveryMinPeriod());
				
				float discoveryValue = (float) (k1 / (double)((k2*(double)newNodeCount)+1));
				
				currNode.setDiscoveryPeriod(discoveryValue);
				
				//System.out.println("NewNodes: " +  newNodeCount);
				//System.out.println("K1: " +  k1);
				//System.out.println("K2: " +  k2);
				//System.out.println("LOOKUP END ! ---> ReqPeer:" + currNode.getPeerDescriptor().getKey() + " Found Nodes="+(foundNodes.length));
				//System.out.println("LOOKUP END ! ---> ReqPeer:" + currNode.getPeerDescriptor().getKey() + " New Nodes="+newNodeCount + " Discovery Value: " + discoveryValue);
				
				//System.out.println("#########################################################################");
				
				//currNode.nlResults.get(currNode.getKey()).getFoundNodes().clear();
				currNode.nlResults.get(currNode.getKey()).clearAll();
				currNode.nlContactedNodes.clear();
				
				//Schedule a new Discovery Event
				currNode.scheduleDiscovery(triggeringTime);
				
				return;
			}
			
			// No closer elements found! find_node-ing k not-already-contacted
			// closer nodes
			//this.setFindNodeK(true);
			currNode.setFindNodeK(true);
			scheduleFindNodeEvent(currNode, currNode.getGb().getK_VALUE());
		} else
			{
				//this.setFindNodeK(false);
				currNode.setFindNodeK(false);
			}
		//if (!this.isFindNodeK()) {
		if (!currNode.isFindNodeK()) {
			scheduleFindNodeEvent(currNode, currNode.getAlpha());
		}

		/*
		try {
			D2VNodeLookUpRecursiveEvent nlk = (D2VNodeLookUpRecursiveEvent) new D2VNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first)
					.createInstance(triggeringTime + 25);

			nlk.setCloserElement(first);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			nlk.setFindNodeK(this.findNodeK);
			nlk.setStepCounter(stepCounter);
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		*/
		
		//log.printEnd(currNode.getKey(),this.getClass().getName(),triggeringTime);
	}

	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
	}
	
	private void scheduleFindNodeEvent(D2VPeer currNode, int numElements) {
		D2VPeerDescriptor currNodeInfo = new D2VPeerDescriptor(currNode.getPeerDescriptor().getGeoLocation(),currNode.getPeerDescriptor().getKey(),currNode.getPeerDescriptor().getTimeStamp());
		D2VFindNodeEvent fn = null;
		int i = 0;
		int contactedNodes = 0;
		Object[] node = currNode.nlResults.get(currNode.getKey()).getFoundNodes()
				.toArray();
		
		if(currNode.nlResults.get(currNode.getKey()).size() < numElements)
			currNode.setFindNodeLimit(currNode.nlResults.get(currNode.getKey()).size());
		else
			currNode.setFindNodeLimit(numElements);
		
		for (i = 0; i < currNode.nlResults.get(currNode.getKey()).size()
				&& contactedNodes <= numElements; i++) {
			if (!currNode.nlContactedNodes.contains(node[i])) {
				try {
					
					float delay = expRandom(Engine.getDefault().getSimulationRandom(),(float) 10.0);
					
					//if (delay > discoveryMaxWait)
						//continue;
					
					fn = (D2VFindNodeEvent) new D2VFindNodeEvent(
							"find_node", new Properties(), null, currNodeInfo)
							.createInstance(triggeringTime+delay);

					fn.setRequestingNode(currNodeInfo);
					fn.setOneShot(true);
					fn.setAssociatedNode((D2VPeer)Engine.getDefault().getNodeByKey(((D2VPeerDescriptor) node[i]).getKey()));
					Engine.getDefault().insertIntoEventsList(fn);
					currNode.nlContactedNodes.add((D2VPeerDescriptor) node[i]);
					contactedNodes++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	public void setFindNodeK(boolean findNodeK) {
		this.findNodeK = findNodeK;
	}
	*/

//	public void setResourceKey(int resourceKey) {
//		this.resourceKey = resourceKey;
//	}

	public D2VPeerDescriptor getCloserElement() {
		return closerElement;
	}

	public void setCloserElement(D2VPeerDescriptor closerElement) {
		this.closerElement = closerElement;
	}

}
