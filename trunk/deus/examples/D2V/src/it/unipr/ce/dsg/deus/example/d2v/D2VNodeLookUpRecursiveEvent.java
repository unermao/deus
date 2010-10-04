package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;

public class D2VNodeLookUpRecursiveEvent extends D2VDiscoveryEvent {

	private D2VPeerDescriptor closerElement = null;

	private float discoveryMaxWait = 25;
	
	private int stepCounter = 0 ;
	
	private boolean findNodeK = false;

	public D2VNodeLookUpRecursiveEvent(String id, Properties params,
			Process parentProcess, D2VPeerDescriptor closerElem, float maxWait)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		discoveryMaxWait = maxWait;
		closerElement = closerElem;
		initialize();
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public Object clone() {
		D2VNodeLookUpRecursiveEvent clone = (D2VNodeLookUpRecursiveEvent) super
				.clone();
		clone.discoveryMaxWait = this.discoveryMaxWait;
		clone.findNodeK = false;
		clone.closerElement = null;
		clone.stepCounter = 0;
	//	clone.resourceKey = 0;

		return clone;
	}

//	public int getResourceKey() {
//		return resourceKey;
//	}

	private void initialize() {
	}

	public boolean isFindNodeK() {
		return findNodeK;
	}

	public void run() throws RunException {
		
		this.stepCounter ++;
		
		D2VPeer currNode = (D2VPeer) this.getAssociatedNode();		
		//Increment number of sent messages
		//currNode.setSentMessages(currNode.getSentMessages() + 1);
		
//		if (resourceKey == 0) {
//			throw new RunException("The resourceKey should really be set in "
//					+ this);
//		}

		D2VPeerDescriptor first = null;

		if (currNode.nlResults.get(currNode.getKey()).size() != 0) {
			first = currNode.nlResults.get(currNode.getKey()).getFoundNodes().first();
		}

		if (closerElement == first) {
			
			if (this.isFindNodeK()) { 
				
				if(stepCounter > 20)
					System.out.println("Key: "+currNode.getKey()+" STEP COUNTER : " + stepCounter);
				
				//Set Discovery Status False
				currNode.setDiscoveryActive(false);
				
				//Store Step Counter
				currNode.setAvDiscoveryStepCounter(currNode.getAvDiscoveryStepCounter()+stepCounter);
				currNode.setDiscoveryCounter(currNode.getDiscoveryCounter()+1);
				
				
				// no new result even from the first k nodes
				Object[] foundNodes = currNode.nlResults.get(currNode.getKey())
						.getFoundNodes().toArray();
				
				//System.out.println(currNode.nlResults.get(currNode.getKey()).size());
				
				for (int j = 0; j < currNode.nlResults.get(currNode.getKey()).size(); j++) {
				//for (int j = 0; j < currNode.nlResults.get(currNode.getKey()).size()
					//	&& j < currNode.getKBucketDim(); j++) {
					currNode.insertPeer((D2VPeerDescriptor) foundNodes[j]);
					//if (res != null )
						//((GeoKadPeer) foundNodes[j]).store(res);
				}

				
				//System.out.println("################### CLOSING LOOKUP Recursive !!!! Step Counter: " + stepCounter + " PEERS:  " + currNode.nlResults.get(currNode.getKey()).size());
				
				currNode.nlResults.get(currNode.getKey()).getFoundNodes().clear();
				currNode.nlContactedNodes.clear();
				
				
				
				return;
			}
			// No closer elements found! find_node-ing k not-already-contacted
			// closer nodes
			this.setFindNodeK(true);
			scheduleFindNodeEvent(currNode, currNode.getGb().getK_VALUE());
		} else
			{
				this.setFindNodeK(false);
			}
		if (!this.isFindNodeK()) {
			scheduleFindNodeEvent(currNode, currNode.getAlpha());
		}

		try {
			D2VNodeLookUpRecursiveEvent nlk = (D2VNodeLookUpRecursiveEvent) new D2VNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);

			nlk.setCloserElement(first);
			nlk.setDiscoveryMaxWait(discoveryMaxWait);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			nlk.setFindNodeK(this.findNodeK);
			nlk.setStepCounter(stepCounter);
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void scheduleFindNodeEvent(D2VPeer currNode, int numElements) {
		D2VPeerDescriptor currNodeInfo = new D2VPeerDescriptor(currNode.getPeerDescriptor().getGeoLocation(),currNode.getPeerDescriptor().getKey(),currNode.getPeerDescriptor().getTimeStamp());
		D2VFindNodeEvent fn = null;
		int i = 0;
		int contactedNodes = 0;
		Object[] node = currNode.nlResults.get(currNode.getKey()).getFoundNodes()
				.toArray();
		for (i = 0; i < currNode.nlResults.get(currNode.getKey()).size()
				&& contactedNodes <= numElements; i++) {
			if (!currNode.nlContactedNodes.contains(node[i])) {
				try {
					float delay = expRandom((float) 300.0);
					if (delay > discoveryMaxWait)
						continue;
					fn = (D2VFindNodeEvent) new D2VFindNodeEvent(
							"find_node", new Properties(), null, currNodeInfo)
							.createInstance(triggeringTime + delay);

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

	public void setFindNodeK(boolean findNodeK) {
		this.findNodeK = findNodeK;
	}

//	public void setResourceKey(int resourceKey) {
//		this.resourceKey = resourceKey;
//	}

	public D2VPeerDescriptor getCloserElement() {
		return closerElement;
	}

	public void setCloserElement(D2VPeerDescriptor closerElement) {
		this.closerElement = closerElement;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
	}

	public int getStepCounter() {
		return stepCounter;
	}

	public void setStepCounter(int stepCounter) {
		this.stepCounter = stepCounter;
	}

}
