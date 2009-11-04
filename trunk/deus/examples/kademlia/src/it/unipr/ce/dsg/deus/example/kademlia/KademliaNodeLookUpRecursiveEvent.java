package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class KademliaNodeLookUpRecursiveEvent extends KademliaNodeLookUpEvent {

	private KademliaPeer closerElement = null;
	private KademliaResourceType res = null;

	private float discoveryMaxWait = 500;

	private boolean findNodeK = false;
	private int resourceKey = 0;

	public KademliaNodeLookUpRecursiveEvent(String id, Properties params,
			Process parentProcess, KademliaPeer closerElem, float maxWait)
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
		KademliaNodeLookUpRecursiveEvent clone = (KademliaNodeLookUpRecursiveEvent) super
				.clone();
		clone.discoveryMaxWait = this.discoveryMaxWait;
		clone.findNodeK = false;
		clone.closerElement = null;
		clone.resourceKey = 0;

		return clone;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	private void initialize() {
	}

	public boolean isFindNodeK() {
		return findNodeK;
	}

	public void run() throws RunException {
		KademliaPeer currNode = (KademliaPeer) this.getAssociatedNode();

		if (resourceKey == 0) {
			throw new RunException("The resourceKey should really be set in "
					+ this);
		}
		KademliaPeer first = null;

		if (currNode.nlResults.get(resourceKey).size() != 0) {
			first = currNode.nlResults.get(resourceKey).getFoundNodes().first();
		}

		if (closerElement == first) {
			if (this.isFindNodeK()) { 
				// no new result even from the first k nodes
				Object[] foundNodes = currNode.nlResults.get(resourceKey)
						.getFoundNodes().toArray();
				for (int j = 0; j < currNode.nlResults.get(resourceKey).size()
						&& j < currNode.getKBucketDim(); j++) {
					currNode.insertPeer((KademliaPeer) foundNodes[j]);
					if (res != null )
						((KademliaPeer) foundNodes[j]).store(res);
				}

				currNode.nlResults.get(resourceKey).getFoundNodes().clear();
				currNode.nlContactedNodes.clear();
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
			KademliaNodeLookUpRecursiveEvent nlk = (KademliaNodeLookUpRecursiveEvent) new KademliaNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);

			nlk.setCloserElement(first);
			nlk.setDiscoveryMaxWait(discoveryMaxWait);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			nlk.setResourceKey(resourceKey);
			nlk.setFindNodeK(this.findNodeK);
			nlk.setRes(res);
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void scheduleFindNodeEvent(KademliaPeer currNode, int numElements) {
		KademliaFindNodeEvent fn = null;
		int i = 0;
		int contactedNodes = 0;
		Object[] node = currNode.nlResults.get(resourceKey).getFoundNodes()
				.toArray();
		for (i = 0; i < currNode.nlResults.get(resourceKey).size()
				&& contactedNodes <= numElements; i++) {
			if (!currNode.nlContactedNodes.contains(node[i])) {
				try {
					float delay = expRandom((float) 300.0);
					if (delay > discoveryMaxWait)
						continue;
					fn = (KademliaFindNodeEvent) new KademliaFindNodeEvent(
							"find_node", new Properties(), null, currNode)
							.createInstance(triggeringTime + delay);

					fn.setRequestingNode(currNode);
					fn.setOneShot(true);
					fn.setAssociatedNode((KademliaPeer) node[i]);
					fn.setResourceKey(resourceKey);
					Engine.getDefault().insertIntoEventsList(fn);
					currNode.nlContactedNodes.add((KademliaPeer) node[i]);
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

	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

	public KademliaPeer getCloserElement() {
		return closerElement;
	}

	public void setCloserElement(KademliaPeer closerElement) {
		this.closerElement = closerElement;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
	}

	public KademliaResourceType getRes() {
		return res;
	}

	public void setRes(KademliaResourceType res) {
		this.res = res;
	}

}
