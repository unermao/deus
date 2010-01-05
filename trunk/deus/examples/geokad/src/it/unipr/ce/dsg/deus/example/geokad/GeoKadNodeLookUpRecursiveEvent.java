package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class GeoKadNodeLookUpRecursiveEvent extends GeoKadNodeLookUpEvent {

	private GeoKadPeer closerElement = null;
	private GeoKadResourceType res = null;

	private float discoveryMaxWait = 500;

	private boolean findNodeK = false;

	public GeoKadNodeLookUpRecursiveEvent(String id, Properties params,
			Process parentProcess, GeoKadPeer closerElem, float maxWait)
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
		GeoKadNodeLookUpRecursiveEvent clone = (GeoKadNodeLookUpRecursiveEvent) super
				.clone();
		clone.discoveryMaxWait = this.discoveryMaxWait;
		clone.findNodeK = false;
		clone.closerElement = null;
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
		
		GeoKadPeer currNode = (GeoKadPeer) this.getAssociatedNode();

//		if (resourceKey == 0) {
//			throw new RunException("The resourceKey should really be set in "
//					+ this);
//		}

		GeoKadPeer first = null;

		if (currNode.nlResults.get(currNode.getKey()).size() != 0) {
			first = currNode.nlResults.get(currNode.getKey()).getFoundNodes().first();
		}

		if (closerElement == first) {
			if (this.isFindNodeK()) { 
				// no new result even from the first k nodes
				Object[] foundNodes = currNode.nlResults.get(currNode.getKey())
						.getFoundNodes().toArray();
				for (int j = 0; j < currNode.nlResults.get(currNode.getKey()).size()
						&& j < currNode.getKBucketDim(); j++) {
					currNode.insertPeer((GeoKadPeer) foundNodes[j]);
					if (res != null )
						((GeoKadPeer) foundNodes[j]).store(res);
				}

				currNode.nlResults.get(currNode.getKey()).getFoundNodes().clear();
				currNode.nlContactedNodes.clear();
				
				//System.out.println("################### CLOSING LOOKUP Recursive !!!!");
				
				return;
			}
			// No closer elements found! find_node-ing k not-already-contacted
			// closer nodes
			this.setFindNodeK(true);
			scheduleFindNodeEvent(currNode, currNode.getKBucketDim());
		} else
			this.setFindNodeK(false);

		if (!this.isFindNodeK()) {
			scheduleFindNodeEvent(currNode, currNode.getAlpha());
		}

		try {
			GeoKadNodeLookUpRecursiveEvent nlk = (GeoKadNodeLookUpRecursiveEvent) new GeoKadNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);

			nlk.setCloserElement(first);
			nlk.setDiscoveryMaxWait(discoveryMaxWait);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			nlk.setFindNodeK(this.findNodeK);
			nlk.setRes(res);
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void scheduleFindNodeEvent(GeoKadPeer currNode, int numElements) {
		GeoKadFindNodeEvent fn = null;
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
					fn = (GeoKadFindNodeEvent) new GeoKadFindNodeEvent(
							"find_node", new Properties(), null, currNode)
							.createInstance(triggeringTime + delay);

					fn.setRequestingNode(currNode);
					fn.setOneShot(true);
					fn.setAssociatedNode((GeoKadPeer) node[i]);
					Engine.getDefault().insertIntoEventsList(fn);
					currNode.nlContactedNodes.add((GeoKadPeer) node[i]);
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

	public GeoKadPeer getCloserElement() {
		return closerElement;
	}

	public void setCloserElement(GeoKadPeer closerElement) {
		this.closerElement = closerElement;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
	}

	public GeoKadResourceType getRes() {
		return res;
	}

	public void setRes(GeoKadResourceType res) {
		this.res = res;
	}

}