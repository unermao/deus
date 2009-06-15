package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.revol.RevolDiscoveryEvent;
import it.unipr.ce.dsg.deus.example.revol.RevolFreeResourceEvent;
import it.unipr.ce.dsg.deus.example.revol.RevolPeer;
import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;



import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

public class NsamDiscoveryEvent extends NodeEvent {
	

		private boolean isPropagation = false;
		private NsamPeer senderNode = null;
		private Service serv = null;
		private int ttl = 0;

		public NsamDiscoveryEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			
			System.out.println("Discovery !");	
	
		}
	
		public void setSenderNode(NsamPeer senderNode) {
			this.senderNode = senderNode;
		}
	
		public void setTtl(int ttl) {
			this.ttl = ttl;
		}
		
		public boolean isPropagation() {
			return isPropagation;
		}
		
		public void setPropagation(boolean isPropagation) {
			this.isPropagation = isPropagation;
		}
	
		public Object clone() {
			NsamDiscoveryEvent clone = (NsamDiscoveryEvent) super.clone();
			clone.isPropagation = false;
			clone.ttl = 0;
			return clone;
		}
	
		public void run() throws RunException {
	
			getLogger().fine("####### disc event: " + this);
			getLogger().fine("####### disc event time: " + triggeringTime);
			getLogger().fine("ttl = " + ttl);
	
			//associo il nodo all'evento ricerca
			NsamPeer associatedNsamNode = (NsamPeer) associatedNode;
		
			Random random = Engine.getDefault().getSimulationRandom();

			//se sono il primo inizio una ricerca
			//TODO implementa l'inizializzazione della ricerca
			if (!isPropagation) 
				initializeDiscoveryProcess(associatedNsamNode, random);	

//se non è la prima ricerca prendo il nodo che ha avviato la ricerca
NsamPeer interestedNode = (NsamPeer) serv.getInterestedNode();
if (interestedNode == null)
	return;

getLogger().fine("interested node = " + interestedNode);

//controllo se il servizio è presente nel nodo che ha lanciato la ricerca
//TODO implementa il controllo

// if the node associated to this discovery event has no neighbors
// or if they are not reachable, reconnect it and stop this discovery
// event sequence
boolean atLeastOneNeighborIsAlive = false;
if (associatedNsamNode.getNeighbors().size() > 0) {
	for (Iterator<Peer> it = associatedNsamNode.getNeighbors()
			.iterator(); it.hasNext();) {
		NsamPeer currentNeighbor = (NsamPeer) it.next();
		if (currentNeighbor != null)
			atLeastOneNeighborIsAlive = true;
	}
}
if ((associatedNsamNode.getNeighbors().size() == 0)
		|| (!atLeastOneNeighborIsAlive)) {
	getLogger().fine("no neighbors...");
	try {
		Properties connEvParams = new Properties();
		MultipleRandomConnectionsEvent connEv = (MultipleRandomConnectionsEvent) new MultipleRandomConnectionsEvent(
				"connection", connEvParams, null)
				.createInstance(triggeringTime
						+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
		connEv.setOneShot(true);
		connEv.setAssociatedNode(associatedNsamNode);
		//connEv.setNumInitialConnections(numInitialConnections); 
		Engine.getDefault().insertIntoEventsList(connEv);
	} catch (InvalidParamsException e) {
		e.printStackTrace();
	}
	return;
	
	getLogger().fine("ttl = " + ttl);
	if (ttl > 0)
		propagateRequestToNeighbors(associatedNsamNode, random);
	
} 

//propago la richiesta ad un numero di vicini
//TODO implementa la funzione






/**
 * Checks if this discovery event is the first of a discovery event
 * sequence. If it is, creates a random resource request and sets the
 * associatedNsamNode as the interestedNode for the requested resource
 * 
 * @param associatedRevolNode
 * @param random
 */
public void initializeDiscoveryProcess(NsamPeer associatedNsamNode,
		Random random) {
		getLogger().fine("First discovery from node " + associatedNsamNode);
		ttl = associatedNsamNode.getTtlMax();
		associatedNsamNode.setQ(associatedNsamNode.getQ() + 1);

	serv = new Service();
	serv.setInterestedNode(associatedNsamNode);
	senderNode = associatedNsamNode;
	
//TODO implementa la ricerca sulla base di input e output
}


/*********** IS REQUESTED SERVICE LOCALLY AVAILABLE **************/
/**
 * Searches locally for the service associated to this discovery event. If
 * the requested service is locally available, updates the resource
 * advertisement adding the resource owner (i.e. the node associated to this
 * event), notifies the interested node (QH++), stores the resource adv. in the cache of the
 * interested node, occupies the resource and puts a free resource event in
 * the event queue.
 * 
 * @param associatedRevolNode
 * @param interestedNode
 * @return
 */
public boolean isRequestedServiceLocallyAvailable(
		NsamPeer associatedNsamNode, NsamPeer interestedNode) {
	getLogger().fine(
			"local search for service ");
	
	if (serv.getServiceInput().containsAll(associatedNsamNode.)
	
	
	if (((res.getName().equals("cpu")) && (res.getAmount() <= associatedRevolNode
			.getCpu()))
			|| ((res.getName().equals("ram")) && (res.getAmount() <= associatedRevolNode
					.getRam()))
			|| ((res.getName().equals("disk")) && (res.getAmount() <= associatedRevolNode
					.getDisk()))) {
		res.setOwner(associatedNsamNode);
		res.setFound(true);
		getLogger().fine(
				"Res " + res + " found in node " + associatedRevolNode);
		getLogger().fine("qh = " + interestedNode.getQh());
		interestedNode.setQh(interestedNode.getQh() + 1);
		getLogger().fine("qh = " + interestedNode.getQh());
		getLogger().fine("q = " + interestedNode.getQ());
		getLogger().fine("qhr = " + interestedNode.getQhr());
		interestedNode.addToCache(res);

		if (res.getName().equals("cpu"))
			associatedRevolNode.setCpu(associatedRevolNode.getCpu()
					- res.getAmount());
		else if (res.getName().equals("ram"))
			associatedRevolNode.setRam(associatedRevolNode.getRam()
					- res.getAmount());
		else if (res.getName().equals("disk"))
			associatedRevolNode.setDisk(associatedRevolNode.getDisk()
					- res.getAmount());

		// this connection changes the topology!
		/*
		if (isPropagation) {
			interestedNode.addNeighbor(associatedRevolNode);
			associatedRevolNode.addNeighbor(interestedNode);
		}
		*/

		// create and enqueue an event that will free the resource
		getLogger().fine(
				"set freeRes for " + res.getName() + " = "
						+ res.getAmount());
		RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) Engine
				.getDefault()
				.createEvent(RevolFreeResourceEvent.class,
						triggeringTime + expRandom(this.getEventRandom(), meanArrivalFreeResource));
		freeResEv.setOneShot(true);
		freeResEv.setResOwner(associatedRevolNode);
		freeResEv.setResName(res.getName());
		freeResEv.setResAmount(res.getAmount());
		Engine.getDefault().insertIntoEventsList(freeResEv);

		return true;
	} else
		return false;
}


/**
 * Propagates the resource request to fk*k neighbors.
 * 
 * @param associatedRevolNode
 * @param random
 */

public void propagateRequestToNeighbors(NsamPeer associatedNsamNode,
		Random random) {
	// check if neighbors are alive and remove those which are null from the
	// neighbor list
	getLogger().fine(
			"num neighbors: " + associatedNsamNode.getNeighbors().size());
	for (Iterator<Peer> it2 = associatedNsamNode.getNeighbors().iterator(); it2
			.hasNext();) {
		Peer currentNode = it2.next();
		if (currentNode == null)
			associatedNsamNode.removeNeighbor(currentNode);
	}
	getLogger().fine(
			"num neighbors after cleaning: " + associatedNsamNode.getNeighbors().size());
	
	if (associatedNsamNode.getNeighbors().size() == 0)
		return;

	if (associatedNsamNode.getNeighbors().size() == 1)
		if (associatedNsamNode.getNeighbors().get(0) == senderNode)
			return;

	getLogger().fine("fk = " + associatedNsamNode.getFk());
	int numDestinations = (int) (associatedNsamNode.getFk()
			* (double) associatedNsamNode.getNeighbors().size());
	getLogger().fine("num destinations = " + numDestinations);
	if (numDestinations == associatedNsamNode.getNeighbors().size())
		numDestinations--; // to exclude senderNode
	if (numDestinations == 0)
		numDestinations = 1;
	getLogger().fine("node = " + associatedNsamNode.getId());
	getLogger().fine("ttl = " + ttl);
	getLogger().fine(
			"Discovery: service not found, send to "
					+ numDestinations + " neighbors");
	/**
	 * take numDestinations neighbors randomly (excluding the sender of this
	 * query) and put in the event queue a NsamDiscoveryEvent associated to
	 * each destination
	 */
	int[] destinations = new int[numDestinations];
	for (int i = 0; i < numDestinations; i++) {		
		boolean controlPassed;
		do {
			controlPassed = true;
			do {
				destinations[i] = random.nextInt(associatedNsamNode
						.getNeighbors().size());
			} while (associatedNsamNode.getNeighbors()
					.get(destinations[i]) == senderNode);
			for (int j = 0; j < i; j++)
				if (destinations[i] == destinations[j])
					controlPassed = false;
			//System.out.println(this + " dentro alla propagazione..");
		} while (!controlPassed);

		NsamDiscoveryEvent discEv = (NsamDiscoveryEvent) Engine
				.getDefault().createEvent(
						RevolDiscoveryEvent.class,
						triggeringTime
								+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
		getLogger().fine("disc event: " + discEv);
		getLogger().fine("disc event time: " + discEv.triggeringTime);

		discEv.setHasSameAssociatedNode(false);
		discEv.setOneShot(true);
	//	discEv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
		//discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
		discEv.setPropagation(true);
		getLogger().fine(
				"Dest node: "
						+ ((NsamPeer) associatedNsamNode.getNeighbors()
								.get(destinations[i])));
		discEv.setAssociatedNode((NsamPeer) associatedNsamNode
				.getNeighbors().get(destinations[i]));
		discEv.setSenderNode(associatedNsamNode);
		discEv.setTtl(ttl - 1);
		Engine.getDefault().insertIntoEventsList(discEv);
		getLogger().fine(
				"sim. virtual time: "
						+ Engine.getDefault().getVirtualTime());
	}
}

/**
 * returns exponentially distributed random variable
 */
private float expRandom(Random random, float meanValue) {
	float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
	return myRandom;
}





}
