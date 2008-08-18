package it.unipr.ce.dsg.deus.example.energy;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

/**
 * <p>
 * EnergyPeers are characterized by one kind of consumable resource: energy.
 * Moreover, each EnergyPeer has a chromosome, i.e.
 * a set of parameters whose values are randomly initialized when the
 * RevolPeer is instantiated, and may change during its lifetime, depending
 * on external events. The EnergyPeer keeps track of the number of sent queries (Q)
 * and of the number of query hits (QH). The query hit ratio (QHR = QH/Q) is 
 * initialized to 0.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class EnergyPeer extends Peer {

	private static final String AVG_INIT_CHROMOSOME = "avgInitChromosome";
	private static final String IS_RANDOM_INIT = "isRandomInit";
	private static final String POWER_FACTOR = "powerFactor";
	private int avgInitChromosome = 0;
	private boolean isRandomInit = false;
	private int powerFactor = 0;
	private int maxPower = 0;
	private int power = 0;	
	
	private int g = 0;
	// chromosome
	private int[] c = new int[3]; 
	// query log
	private double q = 0;
	private double qh = 0;
	
	private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>();
	private ArrayList<ResourceAdv> cachedQueries = new ArrayList<ResourceAdv>();
	
	public EnergyPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(AVG_INIT_CHROMOSOME))
			avgInitChromosome = Integer.parseInt(params.getProperty(AVG_INIT_CHROMOSOME));
		if (params.containsKey(IS_RANDOM_INIT))
			isRandomInit = Boolean.parseBoolean(params.getProperty(IS_RANDOM_INIT));
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(POWER_FACTOR) )
				powerFactor = (int) ((AllocableResource) r).getAmount();
		}	
	}
	
	public Object clone() {
		EnergyPeer clone = (EnergyPeer) super.clone();
		clone.g = 0;
		clone.c = new int[3];
		Random random = Engine.getDefault().getSimulationRandom(); 
		for (int i = 0; i < 3; i++)
			if (isRandomInit)
				clone.c[i] = random.nextInt(avgInitChromosome*2 - 1) + 1;
			else
				clone.c[i] = avgInitChromosome; 

		clone.maxPower = (random.nextInt(powerFactor)+1)*10; // kW
		clone.power = clone.maxPower;
		clone.q = 0;
		clone.qh = 0;
		clone.cache = new ArrayList<ResourceAdv>();
		clone.cachedQueries = new ArrayList<ResourceAdv>();
		return clone;
	}

	public double getFk() {
		return ((double) c[0])/6;
	}

	public int getTtlMax() {
		return c[1];
	}

	public int getDMax() {
		return c[2]*2;
	}

	public int getMaxPower() {
		return maxPower;
	}

	public int getPower() {
		return power;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
	
	public int[] getC() {
		return c;
	}

	public void setC(int[] c) {
		this.c = c;
	}

	public double getQ() {
		return q;
	}

	public void setQ(double q) {
		this.q = q;
	}

	public double getQh() {
		return qh;
	}

	public void setQh(double qh) {
		this.qh = qh;
	}

	public double getQhr() {
		if (this.q == 0)
			return 0.5;
		else
			return this.qh / this.q;
	}

	public double getAvgNeighborsQhr() {
		/*
		double sumQhr = 0;
		if (this.getQ() > 0)
			sumQhr += this.getQhr();
		int numNeighborsWithPositiveQ = 0;
		RevolPeer currentNeighbor = null;
		for (Iterator<Peer> it = this.getNeighbors().iterator(); it.hasNext(); ) {
			currentNeighbor = (RevolPeer) it.next();
			if (currentNeighbor.getQ() > 0) {
				sumQhr += currentNeighbor.getQhr();
				numNeighborsWithPositiveQ++;
			}
		}
			
		if ((this.getQ() == 0) && (numNeighborsWithPositiveQ == 0))
			return -1;
		else if ((this.getQ() == 0) && (numNeighborsWithPositiveQ > 0))
			return sumQhr / numNeighborsWithPositiveQ;
		return sumQhr / (numNeighborsWithPositiveQ + 1);
		*/
		double sumQhr = this.getQhr();
		EnergyPeer currentNeighbor = null;
		for (Iterator<Peer> it = this.getNeighbors().iterator(); it.hasNext(); ) {
			currentNeighbor = (EnergyPeer) it.next();
			sumQhr += currentNeighbor.getQhr();
		}
		return sumQhr / (this.getNeighbors().size() + 1);
	}
	
	public ArrayList<ResourceAdv> getCache() {
		return cache;
	}

	public void setCache(ArrayList<ResourceAdv> cache) {
		this.cache = cache;
	}
	
	public void addToCache(ResourceAdv res) {
		if (cache.size() < getDMax()) 
			cache.add(res);
		else if (cache.size() == getDMax()) {
			cache.remove(0);
			cache.add(res);
		}
	}
	
	public ArrayList<ResourceAdv> getCachedQueries() {
		return cachedQueries;
	}

	public void setCachedQueries(ArrayList<ResourceAdv> cachedQueries) {
		this.cachedQueries = cachedQueries;
	}

	public void addToCachedQueries(ResourceAdv res) {
		cachedQueries.add(res);
	}
	
	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}
	
	public void dropExceedingResourceAdvs() {
		// pulizia della cache: via gli adv. associati a nodi morti
		for (Iterator<ResourceAdv> it = cache.iterator(); it.hasNext();) {
			ResourceAdv currentResourceAdv = it.next();
			EnergyPeer currentNode = (EnergyPeer) currentResourceAdv.getOwner();
			if (currentNode == null)
				this.removeResourceAdvFromCache(currentResourceAdv);
		}
		
		int dMax = this.getDMax();
		int numResourceAdvs = cache.size();
		if (numResourceAdvs <= dMax)
			return;
		ArrayList<ResourceAdv> newResourceAdvsList = new ArrayList<ResourceAdv>();
		// mantengo solo le più recenti
		for (int i = (numResourceAdvs - dMax); i < numResourceAdvs; i++)
			newResourceAdvsList.add((ResourceAdv) cache.get(i));
		cache = newResourceAdvsList;
		
		// cleaning the query cache
		for (Iterator<ResourceAdv> it = cachedQueries.iterator(); it.hasNext();) {
			ResourceAdv currentCachedQuery = it.next();	
			EnergyPeer currentNode = (EnergyPeer) currentCachedQuery.getOwner();
			if ((currentNode == null) || (currentCachedQuery.isFound()))
				this.removeResourceAdvFromCache(currentCachedQuery);
		}
	
	}
	
	public void removeResourceAdvFromCache(ResourceAdv currentResourceAdv) {
		ArrayList<ResourceAdv> newCache = new ArrayList<ResourceAdv>();
		for (Iterator<ResourceAdv> it = cache.iterator(); it.hasNext();) {
			ResourceAdv r = it.next();
			if (!r.equals(currentResourceAdv))
				newCache.add(r);
		}
		cache = newCache;
	}
	
}
