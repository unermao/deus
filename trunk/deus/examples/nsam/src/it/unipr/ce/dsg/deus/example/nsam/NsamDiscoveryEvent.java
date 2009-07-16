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
		private NsamService serv = null;
		private NsamService compositionStep = null;
		private int ttl = 0;
		private boolean isComposition = false;
		
		
		private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
		private float meanArrivalTriggeredDiscovery = 0;
		private static final String MEAN_ARRIVAL_FREE_RESOURCE = "meanArrivalFreeResource";
		private float meanArrivalFreeResource = 0;
		private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
		private int numInitialConnections = 0;
		
		public NsamDiscoveryEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			
			System.out.println("Discovery !");	
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
		
		public void setMeanArrivalTriggeredDiscovery(
				float meanArrivalTriggeredDiscovery) {
			this.meanArrivalTriggeredDiscovery = meanArrivalTriggeredDiscovery;
		}
		
		public void setMeanArrivalFreeResource(float meanArrivalFreeResource) {
			this.meanArrivalFreeResource = meanArrivalFreeResource;
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
			clone.serv=null;
			return clone;
		}

//**************************** RUN DISCOVERY *******************************/
	
		/**
		 * <ol>
		 * <li>
		 * check if the NsamService is null; if not, check if it has been already
		 * found (in that case do not go on)</li>
		 * <li>
		 * check if the associated NsamPeer is null</li>
		 * <li>
		 * check if the associated NsamPeer is isolated; if it is, put a
		 * MultipleConnectionsEvent in the event queue and do not go on</li>
		 * <li>
		 * check if this event is an intermediate step of a query propagation
		 * process; if not, initialize a new search by generating and filling (with
		 * random service info) a NsamService</li>
		 * <li>
		 * check if the interested node of the search is null (it may have left the
		 * network); if it is, do not go on</li>
		 * <li>
		 * check if the requested service is locally available; if it is, consume
		 * it, put a future NsamFreeServiceEvent in the event queue; otherwise,
		 * check if a local service may be used in a composition for the requested service; if it is 
		 * set the composition and propagate both the original query and a new one with the  local 
		 * service outputs as input;
		 * if the service is not composable,search the local Service cache, 
		 * then propagate the query to neighbors
		 * (i.e. put new NsamDiscoveryEvents in the event queue)</li>
		 * </ol>
		 * 
		 */
		
				
		public void run() throws RunException {
		 
			getLogger().fine("####### disc event: " + this);
			getLogger().fine("####### disc event time: " + triggeringTime);
			getLogger().fine("ttl = " + ttl);
			getLogger().fine("mean arrival triggered discovery " + meanArrivalTriggeredDiscovery);
			getLogger().fine("mean arrival free resource " + meanArrivalFreeResource);
			getLogger().fine("ttl = " + ttl);
			
			//associo il nodo all'evento ricerca
			NsamPeer associatedNsamNode = (NsamPeer) associatedNode;
			
			// the following if statement should avoid to search for resources
			// which have been already found by the intersted node
			if (serv != null) {
				if (isPropagation)
					getLogger().fine("is propagation");
					else
						getLogger().fine(
								"Strange! service != null but it is not propagation");
					if (serv.isFound()) {
						getLogger().fine("node: " + associatedNsamNode.getId());
						getLogger().fine("service already found!");
						return;
					}
			
			}
			if (associatedNsamNode == null) {
				if ((!isPropagation) && (hasSameAssociatedNode == false)
						&& (Engine.getDefault().getNodes().size() > 0)) {
					getLogger().fine("generating associated node ");
					associatedNsamNode = (NsamPeer) Engine.getDefault()
							.getNodes().get(
									Engine.getDefault().getSimulationRandom()
											.nextInt(
													Engine.getDefault().getNodes()
															.size()));
				} else if (isPropagation) { // associate this discovery to a neighbor of the interested node
					getLogger().fine("!!!! associated node is null AND is propagation !!!!");
					do {
						associatedNsamNode = (NsamPeer) serv.getInterestedNode()
								.getNeighbors().get(
										Engine.getDefault().getSimulationRandom()
												.nextInt(
														serv.getInterestedNode()
																.getNeighbors()
																.size()));
					} while (associatedNsamNode == null); 
				} else
					return;
			}	
							
			Random random = Engine.getDefault().getSimulationRandom();

			//se sono il primo inizio una ricerca
			if (!isPropagation) 
				initializeDiscoveryProcess(associatedNsamNode, random);	
			
			//se non è la prima ricerca prendo il nodo che ha avviato la ricerca
			NsamPeer interestedNode = (NsamPeer) serv.getInterestedNode();
			if (interestedNode == null)
				return;
			
			getLogger().fine("interested node = " + interestedNode);



			
	/*		if (associatedRevolNode.getCachedQueries().contains(res))
				return;
			else
				associatedRevolNode.getCachedQueries().add(res);  */
			
			if (isRequestedServiceLocallyAvailable(associatedNsamNode,
					interestedNode))   
				return;
			else {
				getLogger().fine("service not found locally");
				if (isServiceAdvInCache(associatedNsamNode))
					return;

	//TODO if composition crea composizione.....
	
	
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
	}
	getLogger().fine("ttl = " + ttl);
	if (ttl > 0)
	{
		//propago la richiesta ad un numero di vicini
		propagateRequestToNeighbors(associatedNsamNode, random);
		if (isComposition){
			propagateCompositionRequest();
		}
	}
}
	
			}
	



/*************INITIALIZE DISCOVERY PROCESS*********************/

/**
 * Checks if this discovery event is the first of a discovery event
 * sequence. If it is, creates a random service request and sets the
 * associatedNsamNode as the interestedNode for the requested service
 * 
 * @param associatedNsamNode
 * @param random
 */
public void initializeDiscoveryProcess(NsamPeer associatedNsamNode,
		Random random) {
		getLogger().fine("First discovery from node " + associatedNsamNode);
		ttl = associatedNsamNode.getTtlMax();
		associatedNsamNode.setQ(associatedNsamNode.getQ() + 1);  //incremento il numero di query del nodo
		
	serv = new NsamService(random.nextInt(5), random.nextInt(5), random.nextInt(20), random.nextInt(20)); 

	serv.setInterestedNode(associatedNsamNode);
	senderNode = associatedNsamNode;
		
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
	getLogger().fine("local search for service ");
	NsamService localService = null;
	Iterator<NsamService> it = associatedNsamNode.getServiceList().iterator();
	ServiceMatch matchingServices = new ServiceMatch();
	
//per tutti i servizi presenti localmente controllo input e output e memorizzo nella classe di match
	while (it.hasNext()) {
		localService = (NsamService) it.next();		
		matchingServices.setQuality(associatedNsamNode.getQoS());
		if(localService.getServiceInput().containsAll(serv.getServiceInput())) {
			matchingServices.updateMatchingInput(true);
			if (localService.getServiceOuput().containsAll(serv.getServiceOuput()))
				matchingServices.updateMatchingOutput(true);
			else matchingServices.updateMatchingOutput(false);
			}
		else matchingServices.updateMatchingInput(false);
	}		
	
	for (int i=0; i<associatedNsamNode.getServiceList().size(); i++)
		if ((matchingServices.getMatchingInput().get(i)==true) && (matchingServices.getMatchingOutput().get(i)==true)){
			//servizio trovato localmente!!
			serv.setOwner(associatedNsamNode);
			serv.setFound(true);
			getLogger().fine("Service found in node " + associatedNsamNode);
			interestedNode.addToCache(serv);	
			getLogger().fine("qh = " + interestedNode.getQh());
			interestedNode.setQh(interestedNode.getQh() + 1);
			getLogger().fine("qh = " + interestedNode.getQh());
			getLogger().fine("q = " + interestedNode.getQ());
			getLogger().fine("qhr = " + interestedNode.getQhr());
			// create and enqueue an event that will free the resource
			getLogger().fine("set freeRes for service");
			RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) Engine
					.getDefault()
					.createEvent(RevolFreeResourceEvent.class,
							triggeringTime + expRandom(this.getEventRandom(), meanArrivalFreeResource)); //sostituisci questo...
			
			freeResEv.setOneShot(true);
			freeResEv.setResOwner(associatedNsamNode);
			Engine.getDefault().insertIntoEventsList(freeResEv);
	 
		return true;
		}
	
	
	
	}
	

		
	

		
			
		/*	else
			{  //TODO implementa la gestione degli step per la composizione
				isComposition=true;
				
				//creo un nuovo servizio che ha come input l'output del servizio locale, 
				//e come output quello del servizio da cercare
				 compositionStep = new NsamService(localService.getServiceOuput(), serv.getServiceOuput());
				 compositionStep.setInterestedNode(serv.getInterestedNode());
				 
				//propago la ricerca per quegli output e come input quelli del servizio attuale
				//propago anche la ricerca completa
				//impegno il nodo per la sua porzione di servizio
				//
				 
			}   
		
		if (associatedNsamNode.getMaxAcceptedConnection()>0)		
			associatedNsamNode.setMaxAcceptedConnection(associatedNsamNode.getMaxAcceptedConnection()-1);
		else 
			getLogger().fine("no more connections available for this node");
		
				
		
	}
	
	
	
	//TODO se trova il servizio che corrisponde negli input e negli output il servizio è trovato
	// se trova corrispondenza negli input e non negli output propaga e lancia una nuova ricerca
	//però impegna il servizio --> compositionStep=true??
	
	

//TODO decrementa il numero di connessioni possibili per il nodo corrente, e imposta il tempo per l'esecuzione
   
		// create and enqueue an event that will free the resource
		getLogger().fine("set freeRes for service");
		RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) Engine
				.getDefault()
				.createEvent(RevolFreeResourceEvent.class,
						triggeringTime + expRandom(this.getEventRandom(), meanArrivalFreeResource)); //sostituisci questo...
		
		freeResEv.setOneShot(true);
		freeResEv.setResOwner(associatedNsamNode);
		Engine.getDefault().insertIntoEventsList(freeResEv);
 
	return true;
	}else  
		return false;  
}   */

/************************* IS SERVICE IN PEER CACHE ********************************/
 		
		/**
		 * If the cache of the peer contains the advertisement of the service which
		 * match the needed one, try to contact the owner.
		 * 
		 * @param associatedNsamNode
		 * @return
		 */
		public boolean isServiceAdvInCache(NsamPeer associatedNsamNode) {
			boolean serviceFound = false;
			if (senderNode != associatedNsamNode)
				getLogger().fine("sender node = " + senderNode);
			getLogger().fine("associatednsamNode = " + associatedNsamNode);
			associatedNsamNode.dropExceedingResourceAdvs(); //  TODO  clean the cache
			Iterator<NsamService> it = associatedNsamNode.getCache().iterator();
			NsamService servInCache = null;
			while (it.hasNext() && (serviceFound == false)) {
				getLogger().fine("search in cache");
				servInCache = (NsamService) it.next();
				if (// TODO ho trovato il servizio nella cache, implementa 
						 (servInCache.getOwner() != null)
						&& (servInCache.getOwner() != senderNode)
				) 
					serviceFound = true;
			}
			if (serviceFound == true) {
				getLogger().fine(
						"service found in cache, owner = "
								+ servInCache.getOwner().getId());

				NsamDiscoveryEvent discEv = (NsamDiscoveryEvent) Engine
						.getDefault().createEvent(
								RevolDiscoveryEvent.class,
								triggeringTime
										+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery));
				getLogger().fine("disc event: " + discEv);
				discEv.setHasSameAssociatedNode(false);
				discEv.setOneShot(true);
			//	discEv
			//			.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
			//	discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
				discEv.setPropagation(true);
				discEv.setAssociatedNode((RevolPeer) servInCache.getOwner());
				discEv.setSenderNode(associatedNsamNode);
			//	discEv.setResourceToSearchFor(res);
				if (ttl > 0)
					discEv.setTtl(ttl - 1);
				else 
					discEv.setTtl(0);
				Engine.getDefault().insertIntoEventsList(discEv);

				return true;
			} else {
				getLogger().fine("service not found in cache");
				return false;
			}
		}

		
/************************** PROPAGATE REQUEST TO NEIGHBORS ***************************/
		
/**
 * Propagates the resource request to fk*k neighbors.
 * 
 * @param associatedRevolNode
 * @param random
 */

		
		//TODO implementa la funzione 
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

	getLogger().fine("fk = " + associatedNsamNode.getFk());  //TODO implementa la funzione getFk() in modo adeguato
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
		discEv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
		discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
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
