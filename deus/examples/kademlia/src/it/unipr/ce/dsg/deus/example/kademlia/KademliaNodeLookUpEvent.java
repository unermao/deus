package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;
import java.util.Random;

public class KademliaNodeLookUpEvent extends NodeEvent {

	private int resourceKey = 0;

	public KademliaNodeLookUpEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		initialize();
	}

	public Object clone() {
		KademliaNodeLookUpEvent clone = (KademliaNodeLookUpEvent) super.clone();
		clone.resourceKey = 0;
		return clone;
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	private void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unipr.ce.dsg.deus.core.Event#run()
	 */
	public void run() throws RunException {
		KademliaPeer currNode = (KademliaPeer) this.getAssociatedNode();

		if (currNode == null) {
			Random random = new Random();

			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node = random.nextInt(initialized_nodes);
			currNode = (KademliaPeer) Engine.getDefault().getNodes().get(
					random_node);
		}

		float discoveryMaxWait = currNode.getDiscoveryMaxWait();

		if (resourceKey == 0) {
			Random random = new Random();
			resourceKey = random.nextInt(Engine.getDefault().getKeySpaceSize());
		}

		currNode.nlResults.put(resourceKey, new SearchResultType(resourceKey));
		currNode.nlResults.get(resourceKey).addAll(
				currNode.find_node(resourceKey));

		KademliaPeer first = null;
		if (currNode.nlResults.get(resourceKey).size() != 0) {
			first = currNode.nlResults.get(resourceKey).getFoundNodes().first();
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
			nlk.setFindNodeK(false);
			Engine.getDefault().insertIntoEventsList(nlk);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		KademliaFindNodeEvent fn = null;
		for (int i = 0; currNode.nlResults.get(resourceKey).size() > i
				&& i < currNode.getAlpha(); i++) {
			try {
				float delay = expRandom((float) 300.0);
				if (delay > discoveryMaxWait)
					continue;
				KademliaPeer p = (KademliaPeer) currNode.nlResults.get(
						resourceKey).getFoundNodes().toArray()[i];
				fn = (KademliaFindNodeEvent) new KademliaFindNodeEvent(
						"find_node", new Properties(), null, currNode)

				.createInstance(triggeringTime + delay);

				fn.setRequestingNode(currNode);
				fn.setOneShot(true);
				fn.setAssociatedNode(p);
				fn.setResourceKey(resourceKey);
				Engine.getDefault().insertIntoEventsList(fn);
				currNode.nlContactedNodes.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

}
