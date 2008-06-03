package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.impl.event.MultipleRandomConnectionsEvent;

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

	private boolean firstDiscoveryEvent = true;
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

	public void setFirstDiscoveryEvent(boolean firstDiscoveryEvent) {
		this.firstDiscoveryEvent = firstDiscoveryEvent;
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

	public Object clone() {
		RevolDiscoveryEvent clone = (RevolDiscoveryEvent) super.clone();
		clone.firstDiscoveryEvent = true;
		clone.res = null;
		clone.ttl = 0;
		return clone;
	}

	// returns exponentially distributed random variable
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public void run() throws RunException {
		
		RevolNode associatedRevolNode = (RevolNode) associatedNode;

		getLogger().fine("######## Discovery ");
		// System.out.println(cpuFactor + " " + ramFactor + " " + diskFactor);

		if (res != null)
			if (res.isFound()) { // dovrebbe evitare l'occupazione di risorse
									// che non servono piu' perche'
									// l'interesetdNode ha gia' trovato
				getLogger().fine("node: " + associatedRevolNode.getId());
				getLogger().fine("res already found: " + res);
				return;
			}

		if (associatedRevolNode == null) {
			if (hasSameAssociatedNode == false) {
				getLogger().fine("generating associated node ");
				associatedRevolNode = (RevolNode) Engine.getDefault().getNodes().get(
						Engine.getDefault().getSimulationRandom().nextInt(
								Engine.getDefault().getNodes().size()));
			}
			else
				return;
		}

		if (!associatedRevolNode.isReachable()) {
			getLogger().fine("associated node not reachable ");
			return;
		}

		boolean isNeighborAlive = false;
		if (associatedRevolNode.getNeighbors().size() > 0) {
			for (Iterator<Node> it = associatedRevolNode.getNeighbors().iterator(); it
					.hasNext();)
				if (it.next() != null)
					isNeighborAlive = true;
		}

		if ((associatedRevolNode.getNeighbors().size() == 0) || (!isNeighborAlive)) {
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

		if (firstDiscoveryEvent) {
			getLogger().fine("First discovery from node " + associatedRevolNode.getId());
			ttl = associatedRevolNode.getTtlMax();
			associatedRevolNode.setQ(associatedRevolNode.getQ() + 1);
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

		RevolNode interestedNode = (RevolNode) res.getInterestedNode();
		if ((interestedNode == null) || (!interestedNode.isReachable()))
			return;

		// NOTA: anche il nodo che genera la query cerca nelle sue risorse e
		// nella propria cache!
		// non faccio distinzioni tra nodo generatore della query e nodi
		// intermedi
		boolean resFound = false;
		// cerca risorsa tra quelle dell'associatedRevolNode
		if (((res.getName().equals("cpu")) && (res.getAmount() <= associatedRevolNode
				.getCpu()))
				|| ((res.getName().equals("ram")) && (res.getAmount() <= associatedRevolNode
						.getRam()))
				|| ((res.getName().equals("disk")) && (res.getAmount() <= associatedRevolNode
						.getDisk()))) {
			resFound = true;
			res.setOwner(associatedRevolNode);

			// se la risorsa viene trovata, notifica il nodo iniziatore
			// (res.getInterestedNode())
			// (gli metto la local resource nella cache (eventualmente
			// eliminando
			// l'item + vecchio),
			// e gli aggiorno qh e qhr)

			res.setFound(true);
			getLogger().fine("Res " + res + " found in node " + associatedRevolNode.getId());
			interestedNode.setQh(interestedNode.getQh() + 1);
			interestedNode.updateQhr();
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

			// aggiungo owner alla lista dei vicini del nodo associato a questo
			// evento
			// n.b. se owner è già nella lista, non viene aggiunto
			if (!associatedRevolNode.getId().equals(interestedNode.getId())) {
				interestedNode.addNeighbor(associatedRevolNode);
				associatedRevolNode.addNeighbor(interestedNode);
			}

			// creo e metto in coda un evento che libererà la risorsa impegnata
			try {
				getLogger().fine("set freeRes for " + res.getName() + " = " + res.getAmount());
				Properties freeResEvParams = new Properties();
				RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) 
				new RevolFreeResourceEvent("freeResource", freeResEvParams, null)
						.createInstance(triggeringTime
								+ expRandom(meanArrivalFreeResource));
				freeResEv.setResOwner(associatedRevolNode);
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(res.getAmount());
				Engine.getDefault().insertIntoEventsList(freeResEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
		}
		// altrimenti cerco in cache e se TTL > 0 propago la query a fk*k vicini
		// escluso il mittente
		else {
			if (senderNode != associatedRevolNode)
				getLogger().fine("sender node = " + senderNode);
			associatedRevolNode.dropExceedingResourceAdvs(); // pulizia che non guasta
			Iterator<ResourceAdv> it = associatedRevolNode.getCache().iterator();
			ResourceAdv resInCache = null;
			while (it.hasNext() && (resFound == false)) {
				getLogger().fine("search in cache");
				resInCache = (ResourceAdv) it.next();
				if ((resInCache.getName().equals(res.getName()))
						&& (res.getAmount() <= resInCache.getAmount())
						&& (resInCache.getOwner() != null)
						&& (resInCache.getOwner() != senderNode)
						&& (resInCache.getOwner().isReachable())) {
					resFound = true;
					getLogger().fine("found in cache, owner is " + resInCache.getOwner().getId());
				}
			}
			// manda discovery a resInCache.getOwner()
			if (resFound == true) {
				getLogger().fine(
						"res " + res + " found in cache, owner = "
								+ resInCache.getOwner().getId());
				try {
					Properties discEvParams = new Properties();
					RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) new RevolDiscoveryEvent(
							"discovery", discEvParams, null)
							.createInstance(triggeringTime
									+ expRandom(meanArrivalTriggeredDiscovery));
					discEv.setHasSameAssociatedNode(false);
					discEv.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
					discEv.setMeanArrivalFreeResource(meanArrivalFreeResource);
					discEv.setFirstDiscoveryEvent(false);
					discEv.setAssociatedNode((RevolNode) resInCache.getOwner());
					discEv.setSenderNode(associatedRevolNode);
					discEv.setResourceToSearchFor(res);
					discEv.setTtl(0);
					Engine.getDefault().insertIntoEventsList(discEv);
				} catch (InvalidParamsException e) {
					e.printStackTrace();
				}
				return; 
			}
			if (this.ttl > 0) {
				// controlla che tutti i neighbor siano vivi e rimuovi quelli
				// null
				getLogger().fine("num neighbors: " + associatedRevolNode.getNeighbors().size());
				for (Iterator<Node> it2 = associatedRevolNode.getNeighbors()
						.iterator(); it2.hasNext();) {
					Node currentNode = it2.next();
					if ((currentNode == null) || (!currentNode.isReachable()))
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
				// prendi numDestinations neighbors a caso (escludendo il
				// mittente di questa query) e
				// metti in coda un evento RevolDiscovery x ciascuno
				// settando res, associatedRevolNode, e ttl aggiornato
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
					try {
						Properties discEvParams = new Properties();
						RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) new RevolDiscoveryEvent(
								"discovery", discEvParams, null)
								.createInstance(triggeringTime
										+ expRandom(meanArrivalTriggeredDiscovery));
						discEv.setHasSameAssociatedNode(false);
						discEv
								.setMeanArrivalTriggeredDiscovery(meanArrivalTriggeredDiscovery);
						discEv
								.setMeanArrivalFreeResource(meanArrivalFreeResource);
						discEv.setFirstDiscoveryEvent(false);
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
					} catch (InvalidParamsException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
