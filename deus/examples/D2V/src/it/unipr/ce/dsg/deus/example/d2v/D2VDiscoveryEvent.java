package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.discovery.SearchResultType;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

import java.util.Properties;

import com.sun.net.ssl.internal.ssl.Debug;

public class D2VDiscoveryEvent extends NodeEvent {

	private static final float DISCOVERY_PERIOD = 25;

	public D2VDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	private void initialize() {
	}

	public void run() throws RunException {
		D2VPeer currNode = (D2VPeer) this.getAssociatedNode();
	
		
		//DebugLog log = new DebugLog();
		//log.printStart(currNode.getKey(),this.getClass().getName(),triggeringTime);
		
	
		currNode.updateBucketInfo(currNode.createPeerInfo());
		
		//System.out.println("VT:"+Engine.getDefault().getVirtualTime()+" PERIODIC_DISCOVERY ---> Peer:" +currNode.getPeerDescriptor().getKey() + " Neighbours: " + currNode.getGb().getPeerCount());
		
		float discoveryMaxWait = currNode.getDiscoveryMaxWait();
		
		D2VPeerDescriptor currNodeInfo = currNode.createPeerInfo();
		
		//Trova tutti i nodi conosciuti dal peer corrente vicino alla sua posizione
		currNode.nlResults.put(currNode.getKey(), new SearchResultType(currNodeInfo));
		currNode.nlResults.get(currNode.getKey()).addAll(currNode.getGb().find_node(currNode.createPeerInfo(),currNodeInfo));

		D2VPeerDescriptor first = null;
		if (currNode.nlResults.get(currNode.getKey()).size() != 0) {
			first = currNode.nlResults.get(currNode.getKey()).getFoundNodes().first();
		}

		try {
			
			//System.out.println("VT:"+triggeringTime+" SENDING LOOK UP RECURSIVE MESSAGE !");
	
			currNode.setFindNodeK(false);
			
			D2VNodeLookUpRecursiveEvent nlk = (D2VNodeLookUpRecursiveEvent) new D2VNodeLookUpRecursiveEvent(
					"node_lookup", params, null, first)
					.createInstance(triggeringTime);

			nlk.setCloserElement(first);
			nlk.setOneShot(true);
			nlk.setAssociatedNode(currNode);
			Engine.getDefault().insertIntoEventsList(nlk);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
			
		//Prendo i primi alfa nodi tra quelli che conosco vicini alla mia posizione e invio una messaggio di FIND_NODE
		D2VFindNodeEvent fn = null;
		for (int i = 0; currNode.nlResults.get(currNode.getKey()).size() > i
				&& i < currNode.getAlpha(); i++) {
			try {
				//TODO Modifica al tempo da 300.0 a 25
				float delay = expRandom((float) 25.0);
				if (delay > discoveryMaxWait)
					continue;
				
				D2VPeerDescriptor p = (D2VPeerDescriptor) currNode.nlResults.get(currNode.getKey()).getFoundNodes().toArray()[i];
				fn = (D2VFindNodeEvent) new D2VFindNodeEvent(
						"find_node", new Properties(), null, currNodeInfo)
				.createInstance(triggeringTime + delay);

				fn.setRequestingNode(currNodeInfo);
				fn.setOneShot(true);
				fn.setAssociatedNode((D2VPeer)Engine.getDefault().getNodeByKey(p.getKey()));
				
				//System.out.println("Node Key: " + currNode.getKey() +  " Setting Periodic Node List Size: " + currNode.getPeriodicPeerList().size() );
				
				Engine.getDefault().insertIntoEventsList(fn);
				currNode.nlContactedNodes.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//log.printEnd(currNode.getKey(),this.getClass().getName(),triggeringTime);
	}

}
