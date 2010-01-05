package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

public class NsamDiscoveryEvent extends NodeEvent {
	
	//float time = triggeringTime;
	//Engine.getDefault().getVirtualTime()
	
		private boolean isPropagation = false;
		private boolean isComposition = false;
		private NsamPeer senderNode = null;
		private NsamService serv = null;
		
		private int ttl = 0;
		private boolean isFoundLocally = false;
		
		private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
		private float meanArrivalTriggeredDiscovery = 0;
		private static final String MEAN_ARRIVAL_FREE_RESOURCE = "meanArrivalFreeResource";
		private float meanArrivalFreeResource = 0;
		private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
		private int numInitialConnections = 0;
		private static final String NEIGHBORS_PERCENTAGE = "neighborsPercentage";
		private int neighborsPercentage = 0;
		
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
			
			if (params.containsKey(NEIGHBORS_PERCENTAGE)) {
				try {
					neighborsPercentage = Integer.parseInt(params
							.getProperty(NEIGHBORS_PERCENTAGE));
				} catch (NumberFormatException ex) {
					throw new InvalidParamsException(NEIGHBORS_PERCENTAGE
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

		public NsamService getServ() {
			return serv;
		}

		public void setServ(NsamService serv) {
			this.serv = serv;
		}
		
		public boolean isComposition() {
			return isComposition;
		}

		public void setComposition(boolean isComposition) {
			this.isComposition = isComposition;
		}

		public Object clone() {
			NsamDiscoveryEvent clone = (NsamDiscoveryEvent) super.clone();
			clone.isPropagation = false;
			clone.ttl = 0;
			clone.serv=null;
			return clone;
		}

//**************************** RUN DISCOVERY *******************************/
	

				
		public void run() throws RunException {
		 
			getLogger().fine("####### disc event: " + this);
			getLogger().fine("####### disc event time: " + triggeringTime);
			getLogger().fine("ttl = " + ttl);
			getLogger().fine("mean arrival triggered discovery " + meanArrivalTriggeredDiscovery);
			getLogger().fine("mean arrival free resource " + meanArrivalFreeResource);
			getLogger().fine("ttl = " + ttl);
			
			//associate a node to the discovery event
			NsamPeer associatedNsamNode = (NsamPeer) associatedNode;
			

			// the following if statement should avoid to search for resources
			// which have been already found by the interested node
			if (serv != null) {
				if (isPropagation){
					if (!isComposition)
						getLogger().fine("is propagation");
					else getLogger().fine("is composition");
				}
				else
					getLogger().fine(
								"Strange! service != null but it is not propagation nor composition");
					
						
				
			/*	if (serv.isFound()) {
						getLogger().fine("node: " + associatedNsamNode.getId());
						getLogger().fine("service already found!");
						return;
					} */
			}
			
		//TODO riguarda questa parte su nodo associato e propagazione!!!
/*			if (associatedNsamNode == null) {
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
					// FIXME we should choose a neighbor which has not already received this query
				} else
					return;
			}	*/
							
							
			Random random = Engine.getDefault().getSimulationRandom();

			//check if it is a propagation or a new discovery
			if (!isPropagation) 
				initializeDiscoveryProcess(associatedNsamNode, random);	
			
			
			//se non e' la prima ricerca prendo il nodo che ha avviato la ricerca
			NsamPeer interestedNode = (NsamPeer) serv.getInterestedNode();
			if (interestedNode == null)
				return;
			
			getLogger().fine("interested node = " + interestedNode);
			
			//aggiungo alla lista delle richieste questo servizio ed il nodo interessato
			CompositionElement requested = new CompositionElement(serv, interestedNode);
			if (associatedNsamNode.getRequestedServiceList().contains(requested))
				return;
			else
				associatedNsamNode.getRequestedServiceList().add(requested);
			
			localSearch(associatedNsamNode,interestedNode); 
			//se la risorsa è presente localmente ok, altrimenti devo propagare ai vicini
			if (isFoundLocally)
				return;
			else
			{
				getLogger().fine("resource not found locally");
				
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
					connEv.setNumInitialConnections(numInitialConnections); 
					Engine.getDefault().insertIntoEventsList(connEv);
				} catch (InvalidParamsException e) {
					e.printStackTrace();
				}
				return;
			}
			
			getLogger().fine("ttl = " + ttl);
			if (ttl > 0)
				propagateRequestToNeighbors(associatedNsamNode, random, computeNeighbors(associatedNsamNode, random));	
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
	
	ServiceDiscoveryStructure search = new ServiceDiscoveryStructure(serv, triggeringTime);
	associatedNsamNode.getServiceSearchList().add(search); 
	
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

public void localSearch(NsamPeer associatedNsamNode,
										NsamPeer interestedNode) {
	getLogger().fine("local search for service ");
	NsamService localService = null;
	Random rand = Engine.getDefault().getSimulationRandom();
	ArrayList<CompositionElement> compo = new ArrayList<CompositionElement>();
	Iterator<NsamService> it = associatedNsamNode.getServiceList().iterator();
	ServiceMatch matchingServices = new ServiceMatch();
	
//per tutti i servizi presenti localmente controllo input e output e memorizzo nella classe di match
	while (it.hasNext()) {
		localService = (NsamService) it.next();		
		matchingServices.setQuality(associatedNsamNode.getQoS());
		if(localService.getServiceInput().containsAll(serv.getServiceInput())) {
			matchingServices.updateMatchingInput(true);
			if (localService.getServiceOutput().containsAll(serv.getServiceOutput()))
				matchingServices.updateMatchingOutput(true);
			else matchingServices.updateMatchingOutput(false);
			}
		else matchingServices.updateMatchingInput(false);
	}	
	
	//localService = null; 
	for (int i=0; i<associatedNsamNode.getServiceList().size(); i++)
	{
		if ((matchingServices.getMatchingInput().get(i)==true) && (matchingServices.getMatchingOutput().get(i)==true)){
			//servizio trovato localmente!!
		//	serv.setOwner(associatedNsamNode);
		//	serv.setFound(true);	
			getLogger().fine("Service found in node " + associatedNsamNode);
			getLogger().fine("qh = " + interestedNode.getQh());
			interestedNode.setQh(interestedNode.getQh() + 1);
			getLogger().fine("qh = " + interestedNode.getQh());
			getLogger().fine("q = " + interestedNode.getQ());
			getLogger().fine("qhr = " + interestedNode.getQhr());	
			
			isFoundLocally= true;
			compo.add(new CompositionElement(serv, associatedNsamNode));
			NotifyMessage notif = new NotifyMessage(serv.getServiceId(), compo);
			try {
				NsamNotifyMessageEvent nme =(NsamNotifyMessageEvent)Engine.getDefault().createEvent(NsamNotifyMessageEvent.class,triggeringTime);			
			//	NsamNotifyMessageEvent nme =(NsamNotifyMessageEvent)new NsamNotifyMessageEvent("notify", params, null, notif).createInstance(triggeringTime);
				nme.setNotifyMsg(notif);
				nme.setOneShot(true);
				nme.setAssociatedNode(interestedNode);
				Engine.getDefault().insertIntoEventsList(nme);
			}catch(Exception e1){
				e1.printStackTrace();
			}
			return;	
		}	
		
		else if ((matchingServices.getMatchingInput().get(i)==true) && (matchingServices.getMatchingOutput().get(i)==false))
			{	
				NsamService compositionLocalElement = associatedNsamNode.getServiceList().get(i);
				NsamService compositionRequestedElement = new NsamService(associatedNsamNode.getServiceList().get(i).getServiceOutput(), serv.getServiceOutput());
				compositionRequestedElement.setInterestedNode(associatedNsamNode);
				ServiceDiscoveryStructure compositeSearch = new ServiceDiscoveryStructure(compositionRequestedElement, triggeringTime,
																						compositionLocalElement, serv);
				associatedNsamNode.getServiceSearchList().add(compositeSearch);
				isFoundLocally= true; 
				//calcolo il numero di vicini e propago la composizione creando un nuovo evento discovery
				propagateCompositionToNeighbors(associatedNsamNode, compositionRequestedElement, rand, computeNeighbors(associatedNsamNode,rand));
				return;
			}	
			else if ((matchingServices.getMatchingInput().get(i)==false) && (matchingServices.getMatchingOutput().get(i)==true))
			{
				NsamService compositionLocalElement = associatedNsamNode.getServiceList().get(i);
				NsamService compositionRequestedElement = new NsamService(serv.getServiceInput(), associatedNsamNode.getServiceList().get(i).getServiceInput());				
				compositionRequestedElement.setInterestedNode(associatedNsamNode);
				ServiceDiscoveryStructure compositeSearch = new ServiceDiscoveryStructure(compositionRequestedElement, triggeringTime,
							compositionLocalElement, serv);
				associatedNsamNode.getServiceSearchList().add(compositeSearch);
				associatedNsamNode.getServiceSearchList().add(compositeSearch);
				associatedNsamNode.getRequestedServiceList().add(new CompositionElement(serv, interestedNode));
				isFoundLocally= true;
				//calcolo il numero divicini e propago la composizione creando un nuovo evento discovery
				propagateCompositionToNeighbors(associatedNsamNode, compositionRequestedElement, rand, computeNeighbors(associatedNsamNode,rand));
				return;
			}	
		
	}				
	}
	

				
public void propagateCompositionToNeighbors(NsamPeer associatedNsamNode, NsamService compRequestedElem,  Random random, int numNeighbors){
	getLogger().fine(
			"Discovery: service not found, propagate composite search to "
					+numNeighbors + " neighbors");

	if (numNeighbors!=-1){
	int[] destinations = new int[numNeighbors];
	for (int i = 0; i < numNeighbors; i++) {		
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
			System.out.println(this + " dentro alla propagazione..");
		} while (!controlPassed);
		
		
	/*	NsamDiscoveryEvent discEv = (NsamDiscoveryEvent) Engine
				.getDefault().createEvent(
						NsamDiscoveryEvent.class,
						triggeringTime
								+ expRandom(this.getEventRandom(), meanArrivalTriggeredDiscovery)); */
		try {
			NsamDiscoveryEvent compositionDiscv = (NsamDiscoveryEvent) new NsamDiscoveryEvent("comp_discv",params, null);
			getLogger().fine("composite disc event: " + compositionDiscv); 
			getLogger().fine("disc event time: " + compositionDiscv.triggeringTime);
			//compositionDiscv.setNeighborsPercentage(this.neighborsPercentage);
			compositionDiscv.setSenderNode(associatedNsamNode);
			compositionDiscv.setOneShot(true);
			compositionDiscv.setServ(compRequestedElem);
			compositionDiscv.setPropagation(true);
			getLogger().fine(
					"Dest node: "
							+ ((NsamPeer) associatedNsamNode.getNeighbors()
									.get(destinations[i])));
			compositionDiscv.setAssociatedNode((NsamPeer) associatedNsamNode
					.getNeighbors().get(destinations[i]));
			compositionDiscv.setTtl(ttl - 1);
			//compositionDiscv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
			//	compositionDiscv.setMeanArrivalFreeResource(meanArrivalFreeResource);
			Engine.getDefault().insertIntoEventsList(compositionDiscv);
			getLogger().fine(
					"sim. virtual time: "
							+ Engine.getDefault().getVirtualTime());
			}catch (Exception e) {
			e.printStackTrace();
		}
			}
	}
	}	

		
/************************** PROPAGATE REQUEST TO NEIGHBORS ***************************/
		
/**
 * Propagates the service request to a percentage of neighbors.
 * 
 * @param associatedRevolNode
 * @param random
 */


public int computeNeighbors(NsamPeer associatedNsamNode, Random random){
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
		return -1;

	if (associatedNsamNode.getNeighbors().size() == 1)
		if (associatedNsamNode.getNeighbors().get(0) == senderNode)
			return -1;

	getLogger().fine("percentage of considered neighbors " + neighborsPercentage + "%");  //TODO implementa la funzione getFk() in modo adeguato
	int numDestinations = (int) ((neighborsPercentage/100)
			* (double) associatedNsamNode.getNeighbors().size());
	getLogger().fine("num destinations = " + numDestinations);
	if (numDestinations == associatedNsamNode.getNeighbors().size())
		numDestinations--; // to exclude senderNode
	if (numDestinations == 0)
		numDestinations = 1;
return numDestinations;
}


public void propagateRequestToNeighbors(NsamPeer associatedNsamNode, Random random, int numDestinations) {
	
	getLogger().fine("node = " + associatedNsamNode.getId());
	getLogger().fine("ttl = " + ttl);
	getLogger().fine(
			"Discovery: service not found, send to "
					+numDestinations + " neighbors");
	/**
	 * take numDestinations neighbors randomly (excluding the sender of this
	 * query) and put in the event queue a NsamDiscoveryEvent associated to
	 * each destination
	 */
	if (numDestinations!=-1){
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
			System.out.println(this + " dentro alla propagazione..");
		} while (!controlPassed);
		NsamDiscoveryEvent discEv = (NsamDiscoveryEvent) Engine
				.getDefault().createEvent(
						NsamDiscoveryEvent.class,
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
}




/**
 * returns exponentially distributed random variable
 */
private float expRandom(Random random, float meanValue) {
	float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
	return myRandom;
}





}
