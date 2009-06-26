package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * This is the Value LookUp first class: it is usually scheduled by the Engine
 * and not by another event. Moreover, it should run once for each search
 * 
 * @author vittorio sozzi
 * 
 */

public class KademliaValueLookUpEvent extends NodeEvent {

	private int resourceKey = 0;

	public KademliaValueLookUpEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		initialize();
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	public Object clone() {
		KademliaValueLookUpEvent clone = (KademliaValueLookUpEvent) super
				.clone();
		clone.resourceKey = 0;
		return clone;
	}

	private void initialize() {
	}

	@SuppressWarnings("unchecked")
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

		while (resourceKey == 0 || currNode.logSearch.containsKey(resourceKey)) {
			Random random = new Random();
			resourceKey = random.nextInt(Engine.getDefault().getKeySpaceSize());
		}


		currNode.nlResults.put(resourceKey, new SearchResultType(resourceKey));
		currNode.logSearch.put(resourceKey, 1);

		Object findv = currNode.find_value(resourceKey);

		if (findv instanceof ArrayList) {

			currNode.nlResults.get(resourceKey).addAll((ArrayList<KademliaPeer>)findv);
		} else if (findv instanceof KademliaResourceType) {
			// Resource found!
			return;
		}

		KademliaPeer first = null;
		if (currNode.nlResults.get(resourceKey).size() != 0) {
			first = currNode.nlResults.get(resourceKey).getFoundNodes().first();
		}

		try {
			KademliaValueLookUpRecursiveEvent vlk = (KademliaValueLookUpRecursiveEvent) new KademliaValueLookUpRecursiveEvent(
					"value_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);
			vlk.setCloserElement(first);
			vlk.setDiscoveryMaxWait(discoveryMaxWait);
			vlk.setOneShot(true);
			vlk.setAssociatedNode(currNode);
			vlk.setResourceKey(resourceKey);
			Engine.getDefault().insertIntoEventsList(vlk);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		KademliaFindValueEvent fv = null;
		for (int i = 0; currNode.nlResults.get(resourceKey).size() > i
				&& i < currNode.getAlpha(); i++) {
			try {

				float delay = expRandom((float) 300.0);
				if (delay > discoveryMaxWait)
					continue;
				params = new Properties();
				fv = (KademliaFindValueEvent) new KademliaFindValueEvent(
						"find_value", params, null, 
						currNode).createInstance(triggeringTime
						+ expRandom((float) 300.0));

				fv.setRequestingNode(currNode);
				fv.setOneShot(true);
				fv.setAssociatedNode((KademliaPeer) currNode.nlResults.get(resourceKey).getFoundNodes()
						.toArray()[i]);
				fv.setResourceKey(resourceKey);
				Engine.getDefault().insertIntoEventsList(fv);
				currNode.nlContactedNodes.add((KademliaPeer) currNode.nlResults.get(resourceKey).getFoundNodes()
						.toArray()[i]);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
		}

	}

	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

}
