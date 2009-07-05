package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * <p>
 * Each RevolDiscoveryEvent must be associated to a RevolPeer and to a
 * ResourceAdv. If the latter has not been set previously it is set by the
 * RevolDiscoveryEvent itself, with randomly generated resource name and amount,
 * meaning that it is the first discovery attempt for that resource.
 * </p>
 * <p>
 * The discovery algorithm is flooding-based (like Gnutella), but the
 * propagation range and the TTL are not the same for each node: they depend on
 * the current value of the chromosome of the associated RevolPeer. The
 * discovery process also takes into account the ResourceAdv cache of the
 * associated RevolPeer.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
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
	private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
	private int numInitialConnections = 0;

	private boolean isPropagation = false;
	private RevolPeer senderNode = null;

	private ResourceAdv res = null;
	private int ttl = 0;

	public RevolDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
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
		if (params.containsKey(NUM_INITIAL_CONNECTIONS)) {
			try {
				numInitialConnections = Integer.parseInt(params
						.getProperty(NUM_INITIAL_CONNECTIONS));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(NUM_INITIAL_CONNECTIONS
						+ " must be a valid int value.");
			}
		}
	}

	/*
	public float getMeanArrivalTriggeredDiscovery() {
		return meanArrivalTriggeredDiscovery;
	}*/

	public void setMeanArrivalTriggeredDiscovery(
			float meanArrivalTriggeredDiscovery) {
		this.meanArrivalTriggeredDiscovery = meanArrivalTriggeredDiscovery;
	}
	
	/*
	public float getMeanArrivalFreeResource() {
		return meanArrivalFreeResource;
	}
	 */
	
	public void setMeanArrivalFreeResource(float meanArrivalFreeResource) {
		this.meanArrivalFreeResource = meanArrivalFreeResource;
	}

	public void setSenderNode(RevolPeer senderNode) {
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

	/**
	 * <ol>
	 * <li>
	 * check if the ResourceAdv is null; if not, check if it has been already
	 * found (in that case do not go on)</li>
	 * <li>
	 * check if the associated RevolPeer is null</li>
	 * <li>
	 * check if the associated RevolPeer is isolated; if it is, put a
	 * MultipleConnectionsEvent in the event queue and do not go on</li>
	 * <li>
	 * check if this event is an intermediate step of a query propagation
	 * process; if not, initialize a new search by generating and filling (with
	 * random resource info) a ResourceAdv</li>
	 * <li>
	 * check if the interested node of the search is null (it may have left the
	 * network); if it is, do not go on</li>
	 * <li>
	 * check if the requested resource is locally available; if it is, consume
	 * it, put a future RevolFreeResourceEvent in the event queue; otherwise,
	 * search the local ResourceAdv cache, then propagate the query to neighbors
	 * (i.e. put new RevolDiscoveryEvents in the event queue)</li>
	 * </ol>
	 * 
	 */
	public void run() throws RunException {
			
		getLogger().fine("####### disc event: " + this);
		getLogger().fine("####### disc event time: " + triggeringTime);

		getLogger().fine("mean arrival triggered discovery " + meanArrivalTriggeredDiscovery);
		getLogger().fine("mean arrival free resource " + meanArrivalFreeResource);
		getLogger().fine("ttl = " + ttl);
		RevolPeer associatedRevolNode = (RevolPeer) associatedNode;
		
		// the following if statement should avoid to search for resources
		// which have been already found by the intersted node
		if (res != null) {
			//System.out.println("************** TIME = " + triggeringTime + " ***************");
			//System.out.println("************** RESOURCE = " + res + " ***************");
			//System.out.println("************** ASSOCIATED NODE = " + associatedNode + " ***************");
			if (isPropagation)
				getLogger().fine("is propagation");
			else
				getLogger().fine(
						"Strange! res != null but it is not propagation");
			if (res.isFound()) {
				getLogger().fine("node: " + associatedRevolNode.getId());
				getLogger().fine("res already found: " + res);
				return;
			}
		}

		if (associatedRevolNode == null) {
			if ((!isPropagation) && (hasSameAssociatedNode == false)
					&& (Engine.getDefault().getNodes().size() > 0)) {
				getLogger().fine("generating associated node ");
				associatedRevolNode = (RevolPeer) Engine.getDefault()
						.getNodes().get(
								Engine.getDefault().getSimulationRandom()
										.nextInt(
												Engine.getDefault().getNodes()
														.size()));
			} else if (isPropagation) { // associate this discovery to a neighbor of the interested node
				getLogger().fine("!!!! associated node is null AND is propagation !!!!");
				do {
					associatedRevolNode = (RevolPeer) res.getInterestedNode()
							.getNeighbors().get(
									Engine.getDefault().getSimulationRandom()
											.nextInt(
													res.getInterestedNode()
															.getNeighbors()
															.size()));
				} while (associatedRevolNode == null); 
				// FIXME we should choose
				// a neighbor which has
				// not already received
				// this query
			} else
				return;
		}	
		
		Random random = Engine.getDefault().getSimulationRandom();

		if (!isPropagation) 
			initializeDiscoveryProcess(associatedRevolNode, random);
			
		RevolPeer interestedNode = (RevolPeer) res.getInterestedNode();
		if (interestedNode == null)
			return;

		getLogger().fine("interested node = " + interestedNode);
		
		if (associatedRevolNode.getCachedQueries().contains(res))
			return;
		else
			associatedRevolNode.getCachedQueries().add(res);
		
		if (isRequestedResourceLocallyAvailable(associatedRevolNode,
				interestedNode)) 
			return;
		else {
			getLogger().fine("resource not found locally");
			if (isResourceAdvInCache(associatedRevolNode))
				return;
			
			// if the node associated to this discovery event has no neighbors
			// or if they are not reachable, reconnect it and stop this discovery
			// event sequence
			boolean atLeastOneNeighborIsAlive = false;
			if (associatedRevolNode.getNeighbors().size() > 0) {
				for (Iterator<Peer> it = associatedRevolNode.getNeighbors()
						.iterator(); it.hasNext();) {
					RevolPeer currentNeighbor = (RevolPeer) it.next();
					if (currentNeighbor != null)
						atLeastOneNeighborIsAlive = true;
				}
			}
			if ((associatedRevolNode.getNeighbors().size() == 0)
					|| (!atLeastOneNeighborIsAlive)) {
				getLogger().fine("no neighbors...");
				try {
					Properties connEvParams = new Properties();
					MultipleRandomConnectionsEvent connEv = (MultipleRandomConnectionsEvent) new MultipleRandomConnectionsEvent(
							"connection", connEvParams, null)
							.createInstance(triggeringTime
									+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
					connEv.setOneShot(true);
					connEv.setAssociatedNode(associatedRevolNode);
					connEv.setNumInitialConnections(numInitialConnections); 
					Engine.getDefault().insertIntoEventsList(connEv);
				} catch (InvalidParamsException e) {
					e.printStackTrace();
				}
				return;
			}
			
			getLogger().fine("ttl = " + ttl);
			if (ttl > 0)
				propagateRequestToNeighbors(associatedRevolNode, random);
		}
	}

	/**
	 * Checks if this discovery event is the first of a discovery event
	 * sequence. If it is, creates a random resource request and sets the
	 * associatedRevolNode as the interestedNode for the requested resource
	 * 
	 * @param associatedRevolNode
	 * @param random
	 */
	public void initializeDiscoveryProcess(RevolPeer associatedRevolNode,
			Random random) {
		getLogger().fine("First discovery from node " + associatedRevolNode);
		ttl = associatedRevolNode.getTtlMax();
		//getLogger().fine("q = " + associatedRevolNode.getQ());
		associatedRevolNode.setQ(associatedRevolNode.getQ() + 1);
		//getLogger().fine("q = " + associatedRevolNode.getQ());
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
	 * Searches locally for the resource associated to this discovery event. If
	 * the requested resource is locally available, updates the resource
	 * advertisement adding the resource owner (i.e. the node associated to this
	 * event), notifies the interested node (QH++), stores the resource adv. in the cache of the
	 * interested node, occupies the resource and puts a free resource event in
	 * the event queue.
	 * 
	 * @param associatedRevolNode
	 * @param interestedNode
	 * @return
	 */
	public boolean isRequestedResourceLocallyAvailable(
			RevolPeer associatedRevolNode, RevolPeer interestedNode) {
		getLogger().fine(
				"local search for res = " + res.getName() + " amount: "
						+ res.getAmount());
		if (((res.getName().equals("cpu")) && (res.getAmount() <= associatedRevolNode
				.getCpu()))
				|| ((res.getName().equals("ram")) && (res.getAmount() <= associatedRevolNode
						.getRam()))
				|| ((res.getName().equals("disk")) && (res.getAmount() <= associatedRevolNode
						.getDisk()))) {
			res.setOwner(associatedRevolNode);
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
	 * If the cache of the peer contains the advertisement of a resource which
	 * match the needed one, try to contact the owner.
	 * 
	 * @param associatedRevolNode
	 * @return
	 */
	public boolean isResourceAdvInCache(RevolPeer associatedRevolNode) {
		boolean resFound = false;
		if (senderNode != associatedRevolNode)
			getLogger().fine("sender node = " + senderNode);
		getLogger().fine("associatedRevolNode = " + associatedRevolNode);
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
			) 
				resFound = true;
		}
		if (resFound == true) {
			getLogger().fine(
					"res " + res + " found in cache, owner = "
							+ resInCache.getOwner().getId());

			RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) Engine
					.getDefault().createEvent(
							RevolDiscoveryEvent.class,
							triggeringTime
									+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
			getLogger().fine("disc event: " + discEv);
			discEv.setHasSameAssociatedNode(false);
			discEv.setOneShot(true);
			discEv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
			discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
			discEv.setPropagation(true);
			discEv.setAssociatedNode((RevolPeer) resInCache.getOwner());
			discEv.setSenderNode(associatedRevolNode);
			discEv.setResourceToSearchFor(res);
			if (ttl > 0)
				discEv.setTtl(ttl - 1);
			else 
				discEv.setTtl(0);
			Engine.getDefault().insertIntoEventsList(discEv);

			return true;
		} else {
			getLogger().fine("res not found in cache");
			return false;
		}
	}

	/**
	 * Propagates the resource request to fk*k neighbors.
	 * 
	 * @param associatedRevolNode
	 * @param random
	 */
	public void propagateRequestToNeighbors(RevolPeer associatedRevolNode,
			Random random) {
		// check if neighbors are alive and remove those which are null from the
		// neighbor list
		getLogger().fine(
				"num neighbors: " + associatedRevolNode.getNeighbors().size());
		for (Iterator<Peer> it2 = associatedRevolNode.getNeighbors().iterator(); it2
				.hasNext();) {
			Peer currentNode = it2.next();
			// if ((currentNode == null) || (!currentNode.isReachable()))
			if (currentNode == null)
				associatedRevolNode.removeNeighbor(currentNode);
		}
		getLogger().fine(
				"num neighbors after cleaning: " + associatedRevolNode.getNeighbors().size());
		
		if (associatedRevolNode.getNeighbors().size() == 0)
			return;

		if (associatedRevolNode.getNeighbors().size() == 1)
			if (associatedRevolNode.getNeighbors().get(0) == senderNode)
				return;

		getLogger().fine("fk = " + associatedRevolNode.getFk());
		int numDestinations = (int) (associatedRevolNode.getFk()
				* (double) associatedRevolNode.getNeighbors().size());
		getLogger().fine("num destinations = " + numDestinations);
		if (numDestinations == associatedRevolNode.getNeighbors().size())
			numDestinations--; // to exclude senderNode
		if (numDestinations == 0)
			numDestinations = 1;
		getLogger().fine("node = " + associatedRevolNode.getId());
		getLogger().fine("ttl = " + ttl);
		getLogger().fine(
				"Discovery: res " + res + " not found, send to "
						+ numDestinations + " neighbors");
		/**
		 * take numDestinations neighbors randomly (excluding the sender of this
		 * query) and put in the event queue a RevolDiscoveryEvent associated to
		 * each destination
		 */
		int[] destinations = new int[numDestinations];
		for (int i = 0; i < numDestinations; i++) {		
			boolean controlPassed;
			do {
				controlPassed = true;
				do {
					destinations[i] = random.nextInt(associatedRevolNode
							.getNeighbors().size());
				} while (associatedRevolNode.getNeighbors()
						.get(destinations[i]) == senderNode);
				for (int j = 0; j < i; j++)
					if (destinations[i] == destinations[j])
						controlPassed = false;
				//System.out.println(this + " dentro alla propagazione..");
			} while (!controlPassed);

			RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) Engine
					.getDefault().createEvent(
							RevolDiscoveryEvent.class,
							triggeringTime
									+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
			getLogger().fine("disc event: " + discEv);
			getLogger().fine("disc event time: " + discEv.triggeringTime);

			discEv.setHasSameAssociatedNode(false);
			discEv.setOneShot(true);
			discEv
					.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
			discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
			discEv.setPropagation(true);
			getLogger().fine(
					"Dest node: "
							+ ((RevolPeer) associatedRevolNode.getNeighbors()
									.get(destinations[i])));
			discEv.setAssociatedNode((RevolPeer) associatedRevolNode
					.getNeighbors().get(destinations[i]));
			discEv.setSenderNode(associatedRevolNode);
			discEv.setResourceToSearchFor(res);
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
