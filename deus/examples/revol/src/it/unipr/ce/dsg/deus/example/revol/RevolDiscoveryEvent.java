package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;


public class RevolDiscoveryEvent extends Event {
	private static final String CPU = "cpu";
	private static final String RAM = "ram";
	private static final String DISK = "disk";
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;
	
	private boolean firstDiscoveryEvent = true;
	private RevolNode associatedNode = null;
	private ResourceAdv res = null;
	private int ttl = 0;

	public RevolDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(CPU))
			cpu = Integer.parseInt(params.getProperty(CPU));
		if (params.containsKey(RAM))
			ram = Integer.parseInt(params.getProperty(RAM));
		if (params.containsKey(DISK))
			disk = Integer.parseInt(params.getProperty(DISK));
	}

	public void setFirstDiscoveryEvent(boolean firstDiscoveryEvent) {
		this.firstDiscoveryEvent = firstDiscoveryEvent;
	}

	public void setAssociatedNode(RevolNode associatedNode) {
		this.associatedNode = associatedNode;
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
		clone.associatedNode = null;
		clone.res = null;
		clone.ttl = 0;
		return clone;
	}

	// returns exponentially distributed random variable
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault().getSimulationRandom()
				.nextFloat()) * meanValue);
		return myRandom;
	}
	
	public void run() throws RunException {
		
		getLogger().info("######## Discovery ");
		//System.out.println(cpuFactor + " " + ramFactor + " " + diskFactor);
		
		if (res != null)
			if (res.isFound()) { // dovrebbe evitare l'occupazione di risorse che non servono piu' perche' l'interesetdNode ha gia' trovato
				getLogger().info("node: " + associatedNode.getId());
				getLogger().info("res already found: " + res);	
				return;
			}

		if (associatedNode == null) {
			associatedNode = (RevolNode) Engine.getDefault().getNodes().get(Engine.getDefault().getSimulationRandom().nextInt(Engine.getDefault().getNodes().size()));
		}
		
		if (!associatedNode.isReachable())
			return;
		
		boolean isNeighborAlive = false;
		if (associatedNode.getNeighbors().size() > 0) {			
			for (Iterator<Node> it = associatedNode.getNeighbors().iterator(); it.hasNext(); )
				if (it.next() != null)
					isNeighborAlive = true;
		}
		
		if ((associatedNode.getNeighbors().size() == 0 ) || (!isNeighborAlive)) {
			try {
				Properties connEvParams = new Properties();
				RevolConnectionEvent connEv = (RevolConnectionEvent) new RevolConnectionEvent("connection", connEvParams, null).
					createInstance(triggeringTime + expRandom(5)); // FIXME 5 dovrebbe essere un param di RevolDiscoveryEvent
				connEv.setOneShot(true);
				connEv.setNodesToConnect(associatedNode, null);
				Engine.getDefault().insertIntoEventsList(connEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
			return;
		}

		Random random = Engine.getDefault().getSimulationRandom();

		if (firstDiscoveryEvent) {
			//System.out.println("First discovery from node " + associatedNode.getId());
			ttl = this.associatedNode.getTtlMax();
			associatedNode.setQ(associatedNode.getQ() + 1);
			res = new ResourceAdv();
			res.setInterestedNode(associatedNode);
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
			//System.out.println("res " + res);
			//System.out.println("res name = " + res.getName());
			//System.out.println("res amount = " + res.getAmount());
		}

		RevolNode interestedNode = (RevolNode) res.getInterestedNode();
		if ((interestedNode == null) || (!interestedNode.isReachable()))
			return;

		// NOTA: anche il nodo che genera la query cerca nelle sue risorse e
		// nella propria cache!
		// non faccio distinzioni tra nodo generatore della query e nodi
		// intermedi
		boolean resFound = false;
		// cerca risorsa tra quelle dell'associatedNode
		if (((res.getName().equals("cpu")) && (res.getAmount() <= associatedNode.getCpu()))
			|| ((res.getName().equals("ram")) && (res.getAmount() <= associatedNode.getRam()))
			|| ((res.getName().equals("disk")) && (res.getAmount() <= associatedNode.getDisk()))) {
			resFound = true;
			res.setOwner(this.associatedNode);

			// se la risorsa viene trovata, notifica il nodo iniziatore
			// (res.getInterestedNode())
			// (gli metto la local resource nella cache (eventualmente eliminando
			// l'item + vecchio),
			// e gli aggiorno qh e qhr)
			
			res.setFound(true);
			getLogger().info("Res " + res + " found in node " + associatedNode.getId());
			interestedNode.setQh(interestedNode.getQh() + 1);
			interestedNode.updateQhr();
			interestedNode.addToCache(res); 
			if (res.getName().equals("cpu"))
				associatedNode.setCpu(associatedNode.getCpu() - res.getAmount());
			else if (res.getName().equals("ram"))
				associatedNode.setRam(associatedNode.getRam() - res.getAmount());
			else if (res.getName().equals("disk"))
				associatedNode.setRam(associatedNode.getDisk() - res.getAmount());
			
			// aggiungo owner alla lista dei vicini del nodo associato a questo evento
			// n.b. se owner è già nella lista, non viene aggiunto 
			if ((!associatedNode.getId().equals(interestedNode.getId())) && (interestedNode.getNeighbors().size() < interestedNode.getKMax()))
				interestedNode.addNeighbor(associatedNode);
			
			// creo e metto in coda un evento che libererà la risorsa impegnata
			try {
				Properties freeResEvParams = new Properties();
				RevolFreeResourceEvent freeResEv = (RevolFreeResourceEvent) new RevolFreeResourceEvent("freeResource", freeResEvParams, null).
					createInstance(triggeringTime + expRandom(120000)); //FIXME 120000 dovrebbe essere un param di RevolDiscoveryEvent
				freeResEv.setResOwner(associatedNode);
				freeResEv.setResName(res.getName());
				freeResEv.setResAmount(res.getAmount());
				Engine.getDefault().insertIntoEventsList(freeResEv);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
		}
		// altrimenti cerco in cache e se TTL >0 propaga la query a fk*k vicini
		else {
			Iterator<ResourceAdv> it = associatedNode.getCache().iterator();
			ResourceAdv resInCache = null;
			while (it.hasNext() && (resFound == false)) {
				resInCache = (ResourceAdv) it.next();
				if ((resInCache.getName().equals(res.getName()))
						&& (res.getAmount() <= resInCache.getAmount())
						&& (resInCache.getOwner() != null)
						&& (resInCache.getOwner().isReachable())) {
					resFound = true;
				}
			}
			// manda discovery a resInCache.getOwner()
			if (resFound == true) {
				getLogger().info("res " + res + " found in cache, owner = " + resInCache.getOwner());
				try {
					Properties discEvParams = new Properties();
					RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) new RevolDiscoveryEvent("discovery", discEvParams, null)
						.createInstance(triggeringTime + expRandom(5)); //FIXME 5 dovrebbe essere un param di RevolDiscoveryEvent
					discEv.setFirstDiscoveryEvent(false);
					discEv.setAssociatedNode((RevolNode) resInCache.getOwner());
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
				for (Iterator<Node> it2 = associatedNode.getNeighbors().iterator(); it2.hasNext();) {
					Node currentNode = it2.next();
					if ((currentNode == null) || (!currentNode.isReachable()))
						associatedNode.removeNeighbor(currentNode);
				}

				if (associatedNode.getNeighbors().size() == 0)
					return;
				
				int numDestinations = (int) associatedNode.getFk()
						* associatedNode.getNeighbors().size();
				if (numDestinations == 0)
					numDestinations++;
				getLogger().info("node = " + associatedNode.getId());
				getLogger().info("ttl = " + ttl);
				getLogger().info("Discovery: res " + res + " not found, send to " + numDestinations + " neighbors");
				// prendi numDestinations neighbors a caso (escludendo il mittente di questa query) e
				// metti in coda un evento RevolDiscovery x ciascuno
				// settando res, associatedNode, e ttl aggiornato
				int[] destinations = new int[numDestinations];
				for (int i = 0; i < numDestinations; i++) {
					boolean controlPassed;
					do {
						controlPassed = true;
						destinations[i] = random.nextInt(associatedNode
								.getNeighbors().size());
						for (int j = 0; j < i; j++)
							if (destinations[i] == destinations[j])
								controlPassed = false;
					} while (!controlPassed);
					try {
						Properties discEvParams = new Properties();
						RevolDiscoveryEvent discEv = (RevolDiscoveryEvent) new RevolDiscoveryEvent("discovery", discEvParams, null)
							.createInstance(triggeringTime + random.nextInt(5)); //FIXME 5 dovrebbe essere un param di RevolDiscoveryEvent
						discEv.setFirstDiscoveryEvent(false);
						getLogger().info("Dest node: " + ((RevolNode) associatedNode
								.getNeighbors().get(destinations[i])).getId());
						discEv.setAssociatedNode((RevolNode) associatedNode
							.getNeighbors().get(destinations[i]));
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
