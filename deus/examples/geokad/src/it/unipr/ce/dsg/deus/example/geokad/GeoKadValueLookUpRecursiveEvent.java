package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This class contains the Kademlia Value Lookup Recursive procedure
 * 
 * @author Vittorio Sozzi
 * 
 */

public class GeoKadValueLookUpRecursiveEvent extends GeoKadValueLookUpEvent {

	public GeoKadValueLookUpRecursiveEvent(String id, Properties params,
			Process parentProcess, GeoKadPeer closerElem, float maxWait)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		discoveryMaxWait = maxWait;
		closerElement = closerElem;
		initialize();
	}

	private void initialize() {
	}

	private int resourceKey = 0;
	private GeoKadPeer closerElement = null;
	private boolean findNodeK = false;
	private float discoveryMaxWait = 500;

	public Object clone() {
		GeoKadValueLookUpRecursiveEvent clone = (GeoKadValueLookUpRecursiveEvent) super
				.clone();
		clone.discoveryMaxWait = 500;
		clone.closerElement = null;
		clone.findNodeK = false;
		clone.resourceKey = 0;
		return clone;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

	public void run() throws RunException {
		GeoKadPeer currNode = (GeoKadPeer) this.getAssociatedNode();

		if (currNode.nlResults.get(resourceKey).isValueFound()) {
			// Resource found!
			currNode.nlResults.get(resourceKey).setValueFound(false);
			for (int j = 0; j < currNode.nlResults.get(resourceKey).size()
					&& j < currNode.getKBucketDim(); j++) {
				currNode.insertPeer((GeoKadPeer) currNode.nlResults.get(
						resourceKey).getFoundNodes().toArray()[j]);
			}

			currNode.nlResults.get(resourceKey).getFoundNodes().clear();
			currNode.nlContactedNodes.clear();
			this.resourceKey = 0;
			return;
		}

		if (resourceKey == 0) {
			throw new RunException("The resourceKey should really be set in "
					+ this);
		}

		currNode.logSearch.put(resourceKey,
				currNode.logSearch.get(resourceKey) + 1);

		GeoKadPeer first = null;
		if (currNode.nlResults.get(resourceKey).size() != 0) {
			first = currNode.nlResults.get(resourceKey).getFoundNodes().first();
		}

		if (closerElement == first) {
			if (this.isFindNodeK()) {
				// no new result even from the first k
				// nodes. In the Find_VALUE case this unfortunately means
				// that they resourceKey wasn't found
				for (int j = 0; j < currNode.nlResults.get(resourceKey).size()
						&& j < currNode.getKBucketDim(); j++) {
					currNode.insertPeer((GeoKadPeer) currNode.nlResults.get(
							resourceKey).getFoundNodes().toArray()[j]);
				}
				currNode.nlResults.get(resourceKey).getFoundNodes().clear();
				currNode.nlContactedNodes.clear();
				this.resourceKey = 0;
				this.setFindNodeK(false);
				return;
			}
			// No closer elements found! find_node-ing k not-already-contacted
			// closer nodes
			this.setFindNodeK(true);
			scheduleFindValueEvent(currNode, currNode.getKBucketDim());
		} else {
			this.setFindNodeK(false);
			scheduleFindValueEvent(currNode, currNode.getAlpha());
		}

		try {
			GeoKadValueLookUpRecursiveEvent vlk = (GeoKadValueLookUpRecursiveEvent) new GeoKadValueLookUpRecursiveEvent(
					"value_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);
			vlk.setCloserElement(first);
			vlk.setDiscoveryMaxWait(discoveryMaxWait);
			vlk.setOneShot(true);
			vlk.setAssociatedNode(currNode);
			vlk.setResourceKey(resourceKey);
			vlk.setFindNodeK(this.findNodeK);
			Engine.getDefault().insertIntoEventsList(vlk);
		} catch (InvalidParamsException e1) {
			e1.printStackTrace();
		}
	}

	private void scheduleFindValueEvent(GeoKadPeer currNode, int numElements) {
		GeoKadFindValueEvent fv = null;
		int i = 0;
		int contactedNodes = i;
		for (i = 0; i < currNode.nlResults.get(resourceKey).size()
				&& contactedNodes <= numElements; i++) {
			if (!currNode.nlContactedNodes.contains(currNode.nlResults.get(
					resourceKey).getFoundNodes().toArray()[i])) {
				try {
					float delay = expRandom((float) 250.0);
					if (delay > discoveryMaxWait)
						continue;
					params = new Properties();
					fv = (GeoKadFindValueEvent) new GeoKadFindValueEvent(
							"find_value", params, null, currNode)
							.createInstance(triggeringTime + delay);

					fv.setRequestingNode(currNode);
					fv.setOneShot(true);
					fv.setAssociatedNode((GeoKadPeer) currNode.nlResults.get(
							resourceKey).getFoundNodes().toArray()[i]);
					fv.setResourceKey(resourceKey);
					Engine.getDefault().insertIntoEventsList(fv);
					currNode.nlContactedNodes
							.add((GeoKadPeer) currNode.nlResults.get(
									resourceKey).getFoundNodes().toArray()[i]);
					contactedNodes++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public boolean isFindNodeK() {
		return findNodeK;
	}

	public void setFindNodeK(boolean findNodeK) {
		this.findNodeK = findNodeK;
	}

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

}
