package it.unipr.ce.dsg.deus.example.streaming;

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
 * Each StreamingDiscoveryEvent must be associated to a StreamingPeer and to a
 * ResourceAdv. If the latter has not been set previously it is set by the
 * StreamingDiscoveryEvent itself, with randomly generated resource name and amount,
 * meaning that it is the first discovery attempt for that resource.
 * </p>
 * <p>
 * The discovery algorithm is flooding-based (like Gnutella), but the
 * propagation range and the TTL are not the same for each node: they depend on
 * the current value of the chromosome of the associated StreamingPeer. The
 * discovery process also takes into account the ResourceAdv cache of the
 * associated StreamingPeer.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class StreamingDiscoveryEvent extends NodeEvent {
	private static final String POWER = "power";
	private int power = 0;
	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	private static final String MEAN_ARRIVAL_FREE_RESOURCE = "meanArrivalFreeResource";
	private float meanArrivalFreeResource = 0;
	private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
	private int numInitialConnections = 0;

	private boolean isPropagation = false;
	private StreamingPeer senderNode = null;

	private ResourceAdv res = null;
	private int ttl = 0;

	public StreamingDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		if (params.containsKey(POWER))
			power = Integer.parseInt(params.getProperty(POWER));
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

	public void setSenderNode(StreamingPeer senderNode) {
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
		StreamingDiscoveryEvent clone = (StreamingDiscoveryEvent) super.clone();
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
		//System.out.println("####### disc event time: " + triggeringTime);
		getLogger().fine("mean arrival triggered discovery " + meanArrivalTriggeredDiscovery);
		getLogger().fine("mean arrival free resource " + meanArrivalFreeResource);
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;

		Random random = Engine.getDefault().getSimulationRandom();
		
		if (res != null) {
			// if query timeout elapsed, do not execute this event
			//System.out.println("triggeringTime = " + triggeringTime);
			//System.out.println("res.getFirstQueryTime() = " + res.getFirstQueryTime());
			//System.out.println("((StreamingPeer) res.getInterestedNode()).getQueryTimeout() = " + ((StreamingPeer) res.getInterestedNode()).getQueryTimeout());
			if (triggeringTime > res.getFirstQueryTime() + ((StreamingPeer) res.getInterestedNode()).getQueryTimeout()) {
				if (!res.isFound())
					selectProvider((StreamingPeer) res.getInterestedNode(), random);
				return;	
			}
			if (isPropagation)
				getLogger().fine("is propagation");
			else
				getLogger().fine("Strange! res != null but it is not propagation");
		}

		if (associatedStreamingNode == null) {
			if ((!isPropagation) && (hasSameAssociatedNode == false)
					&& (Engine.getDefault().getNodes().size() > 0)) {
				getLogger().fine("generating associated node ");
				associatedStreamingNode = (StreamingPeer) Engine.getDefault()
						.getNodes().get(
								Engine.getDefault().getSimulationRandom()
										.nextInt(
												Engine.getDefault().getNodes()
														.size()));
			} else if (isPropagation) { // associate this discovery to a neighbor of the interested node
				getLogger().fine("!!!! associated node is null AND is propagation !!!!");
				do {
					associatedStreamingNode = (StreamingPeer) res.getInterestedNode()
							.getNeighbors().get(
									Engine.getDefault().getSimulationRandom()
											.nextInt(
													res.getInterestedNode()
															.getNeighbors()
															.size()));
				} while (associatedStreamingNode == null); 
				// FIXME we should choose
				// a neighbor which has
				// not already received
				// this query
			} else
				return;
		}

		if (!isPropagation)
			initializeDiscoveryProcess(associatedStreamingNode, random);
		
		StreamingPeer interestedNode = (StreamingPeer) res.getInterestedNode();
		if (interestedNode == null)
			return;

		getLogger().fine("interested node = " + interestedNode);
		
		if (associatedStreamingNode.getCachedQueries().contains(res))
			return;
		else
			associatedStreamingNode.getCachedQueries().add(res);
		
		if (isRequestedResourceLocallyAvailable(associatedStreamingNode, interestedNode)) 
			return;
		
		/*
		if (isResourceAdvInCache(associatedRevolNode))
			getLogger().info("probable resource owner found in cache");
		*/
		
		// if the node associated to this discovery event has no neighbors
		// or if they are not reachable, reconnect it and stop this discovery
		// event sequence
		boolean atLeastOneNeighborIsAlive = false;
		if (associatedStreamingNode.getNeighbors().size() > 0) {
			for (Iterator<Peer> it = associatedStreamingNode.getNeighbors()
					.iterator(); it.hasNext();) {
				StreamingPeer currentNeighbor = (StreamingPeer) it.next();
				if (currentNeighbor != null)
					atLeastOneNeighborIsAlive = true;
			}
		}
		if ((associatedStreamingNode.getNeighbors().size() == 0)
				|| (!atLeastOneNeighborIsAlive)) {
			getLogger().fine("no neighbors...");
			try {
				Properties connEvParams = new Properties();
				MultipleRandomConnectionsEvent connEv = (MultipleRandomConnectionsEvent) new MultipleRandomConnectionsEvent(
						"connection", connEvParams, null)
						.createInstance(triggeringTime
								+ expRandom(meanArrivalTriggeredDiscovery));
				connEv.setOneShot(true);
				connEv.setAssociatedNode(associatedStreamingNode);
				connEv.setNumInitialConnections(numInitialConnections); 
				Engine.getDefault().insertIntoEventsList(connEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
			return;
		}
			
		getLogger().fine("ttl = " + ttl);
		if (ttl > 0)
			propagateRequestToNeighbors(associatedStreamingNode, random);
	}

	/**
	 * Checks if this discovery event is the first of a discovery event
	 * sequence. If it is, creates a random resource request and sets the
	 * associatedRevolNode as the interestedNode for the requested resource
	 * 
	 * @param associatedRevolNode
	 * @param random
	 */
	public void initializeDiscoveryProcess(StreamingPeer associatedRevolNode, Random random) {
		getLogger().fine("First discovery from node " + associatedRevolNode);
		ttl = associatedRevolNode.getTtlMax();
		getLogger().fine("ttl = " + ttl);
		//getLogger().fine("q = " + associatedRevolNode.getQ());
		associatedRevolNode.setQ(associatedRevolNode.getQ() + 1);
		//getLogger().fine("q = " + associatedRevolNode.getQ());
		res = new ResourceAdv();
		res.setInterestedNode(associatedRevolNode);
		senderNode = associatedRevolNode;
		res.setName("power");
		res.setAmount(random.nextInt(power) + 1);
		res.setDuration(expRandom(meanArrivalFreeResource));
		res.setFirstQueryTime(triggeringTime);
		getLogger().fine("res " + res);
		getLogger().fine("res name = " + res.getName());
		getLogger().fine("res amount = " + res.getAmount());
		getLogger().fine("res occupancy duration = " + res.getDuration());
	}

	/**
	 * Searches locally for the resource associated to this discovery event. If
	 * the requested resource is locally available, updates the resource
	 * advertisement adding the resource owner (i.e. the node associated to this
	 * event), notifies the interested node (QH++), connects the interested node
	 * and the resource owner, stores the resource adv. in the cache of the
	 * interested node, occupies the resource and puts a free resource event in
	 * the event queue.
	 * 
	 * @param associatedRevolNode
	 * @param interestedNode
	 * @return
	 */
	public boolean isRequestedResourceLocallyAvailable(
			StreamingPeer associatedStreamingNode, StreamingPeer interestedNode) {
		getLogger().fine(
				"local search for res = " + res.getName() + ", amount: "
						+ res.getAmount() + ", duration: " + res.getDuration());
		getLogger().fine("local amount: " + associatedStreamingNode.getPower());
		if (!isPropagation) {
			if (res.getAmount() <= associatedStreamingNode.getPower()) {
				res.setFound(true);
				res.setOwner(associatedStreamingNode);
				getLogger().fine("resource found locally");				
				getLogger().fine("qh = " + interestedNode.getQh());
				interestedNode.setQh(interestedNode.getQh() + 1);
				getLogger().fine("qh = " + interestedNode.getQh());
				getLogger().fine("q = " + interestedNode.getQ());
				getLogger().fine("qhr = " + interestedNode.getQhr());
				associatedStreamingNode.setPower(associatedStreamingNode.getPower() - res.getAmount());
				// create and enqueue an event that will free the resource
				getLogger().fine(
						"set freeRes for " + res.getName() + " = "
								+ res.getAmount());
				StreamingFreeResourceEvent freeResEv = (StreamingFreeResourceEvent) Engine
						.getDefault()
						.createEvent(StreamingFreeResourceEvent.class,
								triggeringTime + res.getDuration());
				freeResEv.setOneShot(true);
				freeResEv.setResOwner(associatedStreamingNode);
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(res.getAmount());
				Engine.getDefault().insertIntoEventsList(freeResEv);
				return true;
			}
			else {
				int power = associatedStreamingNode.getPower();
				associatedStreamingNode.setPower(0);
				int previousAmount = res.getAmount();
				res.setAmount(previousAmount - power); // requested power X-X'
				getLogger().fine(
						"set freeRes for " + res.getName() + " = "
								+ power);
				StreamingFreeResourceEvent freeResEv = (StreamingFreeResourceEvent) Engine
						.getDefault()
						.createEvent(StreamingFreeResourceEvent.class,
								triggeringTime + res.getDuration());
				freeResEv.setOneShot(true);
				freeResEv.setResOwner(associatedStreamingNode);
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(power);
				Engine.getDefault().insertIntoEventsList(freeResEv);
				return false;
			}
		}
		else { // if (isPropagation)
			if (res.getAmount() <= associatedStreamingNode.getPower()) {
				getLogger().fine("resource found locally.. now adding this node to list of possible providers");
				//res.setFound(true);
				res.getPossibleProviders().add(associatedStreamingNode);
				return true;
			}
			else
				return false;
		}
	}
	
	private void selectProvider(StreamingPeer interestedNode, Random random) {
		//System.out.println("now select");
		int numPossibleProviders = res.getPossibleProviders().size();
		getLogger().fine("numPossibleProviders = " + numPossibleProviders);
		if (numPossibleProviders > 0) {
			/*
			 * - in realta' la selezione del fornitore si basa su 
			 * 1) il prezzo che fa 
			 * 2) la sua distanza dal nodo richiedente (devo trovare un modo furbo per calcolarla)
			 */
			StreamingPeer selectedProvider = (StreamingPeer) res.getPossibleProviders().get(random.nextInt(numPossibleProviders));
			res.setOwner(selectedProvider);
			res.setFound(true);
			getLogger().fine("select: interested node: " + interestedNode);
			getLogger().fine("qh = " + interestedNode.getQh());
			interestedNode.setQh(interestedNode.getQh() + 1);
			getLogger().fine("qh = " + interestedNode.getQh());
			getLogger().fine("q = " + interestedNode.getQ());
			getLogger().fine("qhr = " + interestedNode.getQhr());
			interestedNode.addToCache(res);
			interestedNode.addNeighbor(selectedProvider);
			selectedProvider.addNeighbor(interestedNode);
			selectedProvider.setPower(selectedProvider.getPower() - res.getAmount());
			// create and enqueue an event that will free the resource
			getLogger().fine(
					"set freeRes for " + res.getName() + " = "
							+ res.getAmount());
			StreamingFreeResourceEvent freeResEv = (StreamingFreeResourceEvent) Engine
					.getDefault()
					.createEvent(StreamingFreeResourceEvent.class,
							res.getFirstQueryTime() + res.getDuration());
			freeResEv.setOneShot(true);
			freeResEv.setResOwner(selectedProvider);
			freeResEv.setResName(res.getName());
			freeResEv.setResAmount(res.getAmount());
			Engine.getDefault().insertIntoEventsList(freeResEv);
		}
		res.setFound(true); // even if the resource is not found, we must set this to true
	}

	/**
	 * If the cache of the peer contains the advertisement of a resource which
	 * match the needed one, try to contact the owner.
	 * 
	 * @param associatedStreamingNode
	 * @return
	 */
	public boolean isResourceAdvInCache(StreamingPeer associatedStreamingNode) {
		boolean resFound = false;
		if (senderNode != associatedStreamingNode)
			getLogger().fine("sender node = " + senderNode);
		getLogger().fine("associatedStreamingNode = " + associatedStreamingNode);
		associatedStreamingNode.dropExceedingResourceAdvs(); // clean the cache
		Iterator<ResourceAdv> it = associatedStreamingNode.getCache().iterator();
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

			StreamingDiscoveryEvent discEv = (StreamingDiscoveryEvent) Engine
					.getDefault().createEvent(
							StreamingDiscoveryEvent.class,
							triggeringTime
									+ expRandom(meanArrivalTriggeredDiscovery));
			getLogger().fine("disc event: " + discEv);
			discEv.setHasSameAssociatedNode(false);
			discEv.setOneShot(true);
			discEv
					.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
			discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
			discEv.setPropagation(true);
			discEv.setAssociatedNode((StreamingPeer) resInCache.getOwner());
			discEv.setSenderNode(associatedStreamingNode);
			discEv.setResourceToSearchFor(res);
			discEv.setTtl(ttl - 1);
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
	 * @param associatedStreamingNode
	 * @param random
	 */
	public void propagateRequestToNeighbors(StreamingPeer associatedStreamingNode,
			Random random) {
		// check if neighbors are alive and remove those which are null from the
		// neighbor list
		getLogger().fine(
				"num neighbors: " + associatedStreamingNode.getNeighbors().size());
		for (Iterator<Peer> it2 = associatedStreamingNode.getNeighbors().iterator(); it2
				.hasNext();) {
			Peer currentNode = it2.next();
			// if ((currentNode == null) || (!currentNode.isReachable()))
			if (currentNode == null)
				associatedStreamingNode.removeNeighbor(currentNode);
		}
		getLogger().fine(
				"num neighbors after cleaning: " + associatedStreamingNode.getNeighbors().size());
		
		if (associatedStreamingNode.getNeighbors().size() == 0)
			return;

		if (associatedStreamingNode.getNeighbors().size() == 1)
			if (associatedStreamingNode.getNeighbors().get(0) == senderNode)
				return;

		getLogger().fine("fk = " + associatedStreamingNode.getFk());
		int numDestinations = (int) (associatedStreamingNode.getFk()
				* (double) associatedStreamingNode.getNeighbors().size());
		getLogger().fine("num destinations = " + numDestinations);
		if (numDestinations == associatedStreamingNode.getNeighbors().size())
			numDestinations--; // to exclude senderNode
		if (numDestinations == 0)
			numDestinations = 1;
		getLogger().fine("node = " + associatedStreamingNode.getId());
		getLogger().fine("ttl = " + ttl);
		getLogger().fine(
				"Discovery: res " + res + " not found, send to "
						+ numDestinations + " neighbors");
		/**
		 * take numDestinations neighbors randomly (excluding the sender of this
		 * query) and put in the event queue a StreamingDiscoveryEvent associated to
		 * each destination
		 */
		int[] destinations = new int[numDestinations];
		for (int i = 0; i < numDestinations; i++) {		
			boolean controlPassed;
			do {
				controlPassed = true;
				do {
					destinations[i] = random.nextInt(associatedStreamingNode
							.getNeighbors().size());
				} while (associatedStreamingNode.getNeighbors()
						.get(destinations[i]) == senderNode);
				for (int j = 0; j < i; j++)
					if (destinations[i] == destinations[j])
						controlPassed = false;
			} while (!controlPassed);

			StreamingDiscoveryEvent discEv = (StreamingDiscoveryEvent) Engine
					.getDefault().createEvent(
							StreamingDiscoveryEvent.class,
							triggeringTime
									+ expRandom(meanArrivalTriggeredDiscovery));
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
							+ ((StreamingPeer) associatedStreamingNode.getNeighbors()
									.get(destinations[i])));
			discEv.setAssociatedNode((StreamingPeer) associatedStreamingNode
					.getNeighbors().get(destinations[i]));
			discEv.setSenderNode(associatedStreamingNode);
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
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

}
