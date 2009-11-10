package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;
import java.util.Random;

public class GeoKadNodeLookUpEvent extends NodeEvent {

	//private int resourceKey = 0;
	private GeoKadResourceType res = null;

	public GeoKadNodeLookUpEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public Object clone() {
		GeoKadNodeLookUpEvent clone = (GeoKadNodeLookUpEvent) super.clone();
	//	clone.resourceKey = 0;
		return clone;
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

//	public int getResourceKey() {
//		return resourceKey;
//	}

	private void initialize() {
	}

	public void run() throws RunException {
		
		//System.out.println("NODE LOOK-UP EVENT");
		
		GeoKadPeer currNode = (GeoKadPeer) this.getAssociatedNode();

		if (currNode == null) {
			Random random = new Random();

			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node = random.nextInt(initialized_nodes);
			currNode = (GeoKadPeer) Engine.getDefault().getNodes().get(
					random_node);
		}

		float discoveryMaxWait = currNode.getDiscoveryMaxWait();

//		if (resourceKey == 0) {
//			Random random = new Random();
//			resourceKey = random.nextInt(Engine.getDefault().getKeySpaceSize());
//		}

		currNode.nlResults.put(currNode.getKey(), new SearchResultType(currNode));
		currNode.nlResults.get(currNode.getKey()).addAll(currNode.find_node(currNode));

		GeoKadPeer first = null;
		if (currNode.nlResults.get(currNode.getKey()).size() != 0) {
			first = currNode.nlResults.get(currNode.getKey()).getFoundNodes().first();
		}

		try {
			GeoKadNodeLookUpRecursiveEvent nlk = (GeoKadNodeLookUpRecursiveEvent) new GeoKadNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first, discoveryMaxWait)
					.createInstance(triggeringTime + discoveryMaxWait);

			nlk.setCloserElement(first);
			nlk.setDiscoveryMaxWait(discoveryMaxWait);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			nlk.setFindNodeK(false);
			nlk.setRes(res);
			Engine.getDefault().insertIntoEventsList(nlk);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		GeoKadFindNodeEvent fn = null;
		for (int i = 0; currNode.nlResults.get(currNode.getKey()).size() > i
				&& i < currNode.getAlpha(); i++) {
			try {
				float delay = expRandom((float) 300.0);
				if (delay > discoveryMaxWait)
					continue;
				
				GeoKadPeer p = (GeoKadPeer) currNode.nlResults.get(currNode.getKey()).getFoundNodes().toArray()[i];
				fn = (GeoKadFindNodeEvent) new GeoKadFindNodeEvent(
						"find_node", new Properties(), null, currNode)
				.createInstance(triggeringTime + delay);

				fn.setRequestingNode(currNode);
				fn.setOneShot(true);
				fn.setAssociatedNode(p);
				Engine.getDefault().insertIntoEventsList(fn);
				currNode.nlContactedNodes.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//
//	public void setResourceKey(int resourceKey) {
//		this.resourceKey = resourceKey;
//	}

	public GeoKadResourceType getRes() {
		return res;
	}

	public void setRes(GeoKadResourceType res) {
		this.res = res;
	}

}
