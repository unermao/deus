package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Random;

public class GeoKadNodeLookUpEvent extends NodeEvent {

	private static final float DISCOVERY_PERIOD = 100;
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
		
		currNode.checkNodeAvailability();
		currNode.updateGeoBucketInfo();
		
		//Set Discovery Active
		if(currNode.isDiscoveryActive() == true)
		{
			//Clean Periodic list
			currNode.getPeriodicPeerList().clear();
			
			try
			{
					GeoKadNodeLookUpEvent discovery = (GeoKadNodeLookUpEvent) new GeoKadNodeLookUpEvent("find_node", new Properties(), null).createInstance(triggeringTime + DISCOVERY_PERIOD);
					discovery.setOneShot(true);
					discovery.setAssociatedNode(currNode);
					Engine.getDefault().insertIntoEventsList(discovery);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return;
		}
		else
			currNode.setDiscoveryActive(true);
			
		float discoveryMaxWait = currNode.getDiscoveryMaxWait();

		GeoKadPeerInfo currNodeInfo = new GeoKadPeerInfo(currNode.getKey(), currNode.getLatitude(), currNode.getLongitude(),currNode.getPeerCounter(),currNode.getTimeStamp());
		
		//Trova tutti i nodi conosciuti dal peer corrente vicino alla sua posizione
		currNode.nlResults.put(currNode.getKey(), new SearchResultType(currNodeInfo));
		currNode.nlResults.get(currNode.getKey()).addAll(currNode.find_node(currNodeInfo));

		GeoKadPeerInfo first = null;
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
			nlk.setStepCounter(0);
			Engine.getDefault().insertIntoEventsList(nlk);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
			
		//Prendo i primi alfa nodi tra quelli che conosco vicini alla mia posizione e invio una messaggio di FIND_NODE
		GeoKadFindNodeEvent fn = null;
		for (int i = 0; currNode.nlResults.get(currNode.getKey()).size() > i
				&& i < currNode.getAlpha(); i++) {
			try {
				//TODO Modifica al tempo da 300.0 a 25
				float delay = expRandom((float) 25.0);
				if (delay > discoveryMaxWait)
					continue;
				
				GeoKadPeerInfo p = (GeoKadPeerInfo) currNode.nlResults.get(currNode.getKey()).getFoundNodes().toArray()[i];
				fn = (GeoKadFindNodeEvent) new GeoKadFindNodeEvent(
						"find_node", new Properties(), null, currNodeInfo)
				.createInstance(triggeringTime + delay);

				fn.setRequestingNode(currNodeInfo);
				fn.setOneShot(true);
				fn.setAssociatedNode((GeoKadPeer)Engine.getDefault().getNodeByKey(p.getKey()));
				
				//System.out.println("Node Key: " + currNode.getKey() +  " Setting Periodic Node List Size: " + currNode.getPeriodicPeerList().size() );
				fn.getPeriodicPeerList().addAll(currNode.getPeriodicPeerList());
				
				Engine.getDefault().insertIntoEventsList(fn);
				currNode.nlContactedNodes.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Clean Periodic list
		currNode.getPeriodicPeerList().clear();
		
		try
		{
			GeoKadNodeLookUpEvent discovery = (GeoKadNodeLookUpEvent) new GeoKadNodeLookUpEvent("find_node", new Properties(), null).createInstance(triggeringTime + DISCOVERY_PERIOD);
			discovery.setOneShot(true);
			discovery.setAssociatedNode(currNode);
			Engine.getDefault().insertIntoEventsList(discovery);
		}
		catch (Exception e) {
			e.printStackTrace();
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
