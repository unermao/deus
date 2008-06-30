package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

public class RevolDiscoveryEvent extends NodeEvent {
	private static final String CPU = "cpu";
	private static final String RAM = "ram";
	private static final String DISK = "disk";
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;
	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	private static final String MEAN_ARRIVAL_FREE_RESOURCE = "meanArrivalFreeResource";
	private float meanArrivalFreeResource = 0;

	private boolean isPropagation = false;
	private RevolNode senderNode = null;
	
	private ResourceAdv res = null;
	private int ttl = 0;

	public RevolDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		super.initialize();
		if (params.containsKey(CPU))
			cpu = Integer.parseInt(params.getProperty(CPU));
		if (params.containsKey(RAM))
			ram = Integer.parseInt(params.getProperty(RAM));
		if (params.containsKey(DISK))
			disk = Integer.parseInt(params.getProperty(DISK));
		if (params.containsKey(MEAN_ARRIVAL_TRIGGERED_DISCOVERY)) {
			try {
				meanArrivalTriggeredDiscovery = Float.parseFloat(params
						.getProperty(MEAN_ARRIVAL_TRIGGERED_DISCOVERY));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(
						MEAN_ARRIVAL_TRIGGERED_DISCOVERY
								+ " must be a valid float value.");
			}
		}
		if (params.containsKey(MEAN_ARRIVAL_FREE_RESOURCE)) {
			try {
				meanArrivalFreeResource = Float.parseFloat(params
						.getProperty(MEAN_ARRIVAL_FREE_RESOURCE));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(MEAN_ARRIVAL_FREE_RESOURCE
						+ " must be a valid float value.");
			}
		}
	}

	public float getMeanArrivalTriggeredDiscovery() {
		return meanArrivalTriggeredDiscovery;
	}

	public void setMeanArrivalTriggeredDiscovery(
			float meanArrivalTriggeredDiscovery) {
		this.meanArrivalTriggeredDiscovery = meanArrivalTriggeredDiscovery;
	}

	public float getMeanArrivalFreeResource() {
		return meanArrivalFreeResource;
	}

	public void setMeanArrivalFreeResource(float meanArrivalFreeResource) {
		this.meanArrivalFreeResource = meanArrivalFreeResource;
	}
	
	public void setSenderNode(RevolNode senderNode) {
		this.senderNode = senderNode;
	}

	public void setResourceToSearchFor(ResourceAdv res) {
		this.res = res;
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
		RevolDiscoveryEvent clone = (RevolDiscoveryEvent) super.clone();
		clone.isPropagation = false;
		clone.res = null;
		clone.ttl = 0;
		return clone;
	}

	
	public void run() throws RunException {
		
		getLogger().fine("####### disc event: " + this);
		getLogger().fine("####### disc event time: " + this.triggeringTime);
		
		RevolNode associatedRevolNode = (RevolNode) associatedNode;
		
		// the following if statement should avoid to search for resources
		// which have been already found by the intersted node
		if (res != null)
			if (res.isFound()) { 
				getLogger().fine("node: " + associatedRevolNode.getId());
				getLogger().fine("res already found: " + res);
				return;
			}
		
		if (associatedRevolNode == null) {
			if ( (!isPropagation) && (hasSameAssociatedNode == false) && (Engine.getDefault().getNodes().size() > 0)) {
				getLogger().fine("generating associated node ");
				associatedRevolNode = (RevolNode) Engine.getDefault().getNodes().get(
						Engine.getDefault().getSimulationRandom().nextInt(
								Engine.getDefault().getNodes().size()));
			}
			else if (isPropagation) { // associate this discovery to a neighbor of the interested node
				do {
					associatedRevolNode = (RevolNode) res.getInterestedNode().getNeighbors().get(
							Engine.getDefault().getSimulationRandom().nextInt(res.getInterestedNode().getNeighbors().size()));
				} while (associatedRevolNode == null); // FIXME we should choose a neighbor which has not already received this query
			}
			else	
				return;
		}

		/*
		if (!associatedRevolNode.isReachable()) {
			getLogger().fine("associated node not reachable ");
			return;
		}
		*/

		// if the node associated to this discovery event has no neighbors
		// or if they are not reachable, reconnect it and stop this discovery event sequence
		boolean atLeastOneNeighborIsAlive = false;
		if (associatedRevolNode.getNeighbors().size() > 0) {
			for (Iterator<Peer> it = associatedRevolNode.getNeighbors().iterator(); it.hasNext();) {
				RevolNode currentNeighbor = (RevolNode) it.next();
				//if ((currentNeighbor != null) && (currentNeighbor.isReachable()))
				if (currentNeighbor != null)
					atLeastOneNeighborIsAlive = true;
			}
		}
		if ((associatedRevolNode.getNeighbors().size() == 0) || (!atLeastOneNeighborIsAlive)) {
			try {
				Properties connEvParams = new Properties();
				// FIXME the re-connection event should be set according to the value of a param
				MultipleRandomConnectionsEvent connEv = (MultipleRandomConnectionsEvent) new MultipleRandomConnectionsEvent(
						"connection", connEvParams, null)
						.createInstance(triggeringTime
								+ expRandom(meanArrivalTriggeredDiscovery));
				connEv.setOneShot(true);
				connEv.setNodeToConnect(associatedRevolNode);
				Engine.getDefault().insertIntoEventsList(connEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
			return;
		}

		Random random = Engine.getDefault().getSimulationRandom();
		
		if (!isPropagation)
			initializeDiscoveryProcess(associatedRevolNode, random);

		RevolNode interestedNode = (RevolNode) res.getInterestedNode();
		//if ((interestedNode == null) || (!interestedNode.isReachable()))
		if (interestedNode == null)
			return;
			
		if (isRequestedResourceLocallyAvailable(associatedRevolNode, interestedNode))
			return;
		else {
			if (isResourceAdvInCache(associatedRevolNode))
				return;
			if (ttl > 0) 
				propagateRequestToNeighbors(associatedRevolNode, random);
		}
	}
	
	
	/**
	 * Checks if this discovery event is the first of a discovery event sequence.
	 * If it is, creates a random resource request and sets the associatedRevolNode as
	 * the interestedNode for the requested resource 
	 * @param associatedRevolNode
	 * @param random
	 */
	public void initializeDiscoveryProcess(RevolNode associatedRevolNode, Random random) {
			getLogger().fine("First discovery from node " + associatedRevolNode.getId());
			ttl = associatedRevolNode.getTtlMax();
			getLogger().fine("q = " + associatedRevolNode.getQ());
			associatedRevolNode.setQ(associatedRevolNode.getQ() + 1);
			getLogger().fine("q = " + associatedRevolNode.getQ());
			res = new ResourceAdv();
			res.setInterestedNode(associatedRevolNode);
			senderNode = associatedRevolNode;
			int resourceType = random.nextInt(3);
			switch (resourceType) {
			case 0:
				res.setName("cpu");
				res.setAmount(random.nextInt(cpu) + 1);
				break;
			case 1:
				res.setName("ram");
				res.setAmount(random.nextInt(ram) + 1);
				break;
			case 2:
				res.setName("disk");
				res.setAmount(random.nextInt(disk) + 1);
				break;
			}
			getLogger().fine("res " + res);
			getLogger().fine("res name = " + res.getName());
			getLogger().fine("res amount = " + res.getAmount());
	}
	
	
	/**
	 * Searches locally for the resource associated to this discovery event. 
	 * If the requested resource is locally available, updates the resource 
	 * advertisement adding the resource owner (i.e. the node associated to 
	 * this event), notifies the interested node (QH++), 
	 * connects the interested node and the resource owner,
	 * stores the resource adv. in the cache of the interested node,
	 * occupies the resource and puts a free resource event in the event queue.
	 * @param associatedRevolNode
	 * @param interestedNode
	 * @return
	 */
	public boolean isRequestedResourceLocallyAvailable(RevolNode associatedRevolNode, 
													   RevolNode interestedNode) {
		if (((res.getName().equals("cpu")) && (res.getAmount() <= associatedRevolNode
				.getCpu()))
				|| ((res.getName().equals("ram")) && (res.getAmount() <= associatedRevolNode
						.getRam()))
				|| ((res.getName().equals("disk")) && (res.getAmount() <= associatedRevolNode
						.getDisk()))) {
			res.setOwner(associatedRevolNode);
			res.setFound(true);
			getLogger().fine("Res " + res + " found in node " + associatedRevolNode.getId());
			getLogger().fine("qh = " + associatedRevolNode.getQh());
			interestedNode.setQh(interestedNode.getQh() + 1);
			getLogger().fine("qh = " + associatedRevolNode.getQh());
			interestedNode.addToCache(res);
			if (res.getName().equals("cpu"))
				associatedRevolNode
						.setCpu(associatedRevolNode.getCpu() - res.getAmount());
			else if (res.getName().equals("ram"))
				associatedRevolNode
						.setRam(associatedRevolNode.getRam() - res.getAmount());
			else if (res.getName().equals("disk"))
				associatedRevolNode.setDisk(associatedRevolNode.getDisk()
						- res.getAmount());

			if (!associatedRevolNode.getId().equals(interestedNode.getId())) {
				interestedNode.addNeighbor(associatedRevolNode);
				associatedRevolNode.addNeighbor(interestedNode);
			}

			// creo e metto in coda un evento che libererà la risorsa impegnata
				getLogger().fine("set freeRes for " + res.getName() + " = " + res.getAmount());
				RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) Engine.getDefault().createEvent(RevolFreeResourceEvent.class, triggeringTime
								+ expRandom(meanArrivalFreeResource));
				freeResEv.setResOwner(associatedRevolNode);
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(res.getAmount());
				Engine.getDefault().insertIntoEventsList(freeResEv);
		
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * If the cache of the peer contains the advertisement 
	 * of a resource which match the needed one,
	 * try to contact the owner.
	 * @param associatedRevolNode
	 * @return
	 */
	public boolean isResourceAdvInCache(RevolNode associatedRevolNode) {
		boolean resFound = false;
		if (senderNode != associatedRevolNode)
			getLogger().fine("sender node = " + senderNode);
		associatedRevolNode.dropExceedingResourceAdvs(); // clean the cache
		Iterator<ResourceAdv> it = associatedRevolNode.getCache().iterator();
		ResourceAdv resInCache = null;
		while (it.hasNext() && (resFound == false)) {
			getLogger().fine("search in cache");
			resInCache = (ResourceAdv) it.next();
			if ((resInCache.getName().equals(res.getName()))
					&& (res.getAmount() <= resInCache.getAmount())
					&& (resInCache.getOwner() != null)
					&& (resInCache.getOwner() != senderNode)
					//&& (resInCache.getOwner().isReachable())) {
					) {
				resFound = true;
				getLogger().fine("found in cache, owner is " + resInCache.getOwner().getId());
			}
		}
		if (resFound == true) {
			getLogger().fine(
					"res " + res + " found in cache, owner = "
							+ resInCache.getOwner().getId());
		
				//Properties discEvParams = new Properties();
				RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) Engine.getDefault().createEvent(RevolDiscoveryEvent.class, 
						triggeringTime + expRandom(meanArrivalTriggeredDiscovery));
				getLogger().fine("disc event: " + discEv);
				discEv.setHasSameAssociatedNode(false);
				discEv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
				discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
				discEv.setPropagation(true);
				discEv.setAssociatedNode((RevolNode) resInCache.getOwner());
				discEv.setSenderNode(associatedRevolNode);
				discEv.setResourceToSearchFor(res);
				discEv.setTtl(ttl-1);
				Engine.getDefault().insertIntoEventsList(discEv);

			return true; 
		}
		else
			return false;
	}
	
	
	/**
	 * Propagates the resource request to fk*k neighbors.
	 * @param associatedRevolNode
	 * @param random
	 */
	public void propagateRequestToNeighbors(RevolNode associatedRevolNode, Random random) {
		// check if neighbors are alive and remove those which are null from the neighbor list
		getLogger().fine("num neighbors: " + associatedRevolNode.getNeighbors().size());
		for (Iterator<Peer> it2 = associatedRevolNode.getNeighbors()
				.iterator(); it2.hasNext();) {
			Peer currentNode = it2.next();
			//if ((currentNode == null) || (!currentNode.isReachable()))
			if (currentNode == null)
				associatedRevolNode.removeNeighbor(currentNode);
		}

		if (associatedRevolNode.getNeighbors().size() == 0)
			return;

		if (associatedRevolNode.getNeighbors().size() == 1)
			if (associatedRevolNode.getNeighbors().get(0) == senderNode)
				return;

		int numDestinations = (int) associatedRevolNode.getFk()
				* associatedRevolNode.getNeighbors().size();
		if (numDestinations == associatedRevolNode.getNeighbors().size())
			numDestinations--; // to exclude senderNode
		if (numDestinations == 0)
			numDestinations++;
		getLogger().fine("node = " + associatedRevolNode.getId());
		getLogger().fine("ttl = " + ttl);
		getLogger().fine(
				"Discovery: res " + res + " not found, send to "
						+ numDestinations + " neighbors");
		/**
		 * take numDestinations neighbors randomly (excluding the sender of this query)
		 * and put in the event queue a RevolDiscoveryEvent associated to each destination
		 */ 
		int[] destinations = new int[numDestinations];
		for (int i = 0; i < numDestinations; i++) {
			boolean controlPassed;
			do {
				controlPassed = true;
				do {
					destinations[i] = random.nextInt(associatedRevolNode
							.getNeighbors().size());
				} while (associatedRevolNode.getNeighbors().get(
						destinations[i]) == senderNode);
				for (int j = 0; j < i; j++)
					if (destinations[i] == destinations[j])
						controlPassed = false;
			} while (!controlPassed);
			
				//Properties discEvParams = new Properties();
				RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) Engine.getDefault().createEvent(RevolDiscoveryEvent.class, triggeringTime
								+ expRandom(meanArrivalTriggeredDiscovery));
				getLogger().fine("disc event: " + discEv);
				getLogger().fine("disc event time: " + discEv.triggeringTime);
				
				discEv.setHasSameAssociatedNode(false);
				discEv
						.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
				discEv
						.setMeanArrivalFreeResource(meanArrivalFreeResource);
				discEv.setPropagation(true);
				getLogger().fine(
						"Dest node: "
								+ ((RevolNode) associatedRevolNode
										.getNeighbors().get(
												destinations[i]))
										.getId());
				discEv.setAssociatedNode((RevolNode) associatedRevolNode
						.getNeighbors().get(destinations[i]));
				discEv.setSenderNode(associatedRevolNode);
				discEv.setResourceToSearchFor(res);
				discEv.setTtl(ttl - 1);
				Engine.getDefault().insertIntoEventsList(discEv);
				getLogger().fine("sim. virtual time: " + Engine.getDefault().getVirtualTime());
		}
	}
	
	/** 
	 * returns exponentially distributed random variable
	 */
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

}
