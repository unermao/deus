package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

/**
 * <p>
 * RevolPeers are characterized by three kinds of consumable resources:
 * CPU, RAM, DISK. Moreover, each RevolPeer has a chromosome, i.e.
 * a set of parameters whose values are randomly initialized when the
 * RevolPeer is instantiated, and may change during its lifetime, depending
 * on external events. The RevolPeer keeps track of the number of sent queries (Q)
 * and of the number of query hits (QH). The query hit ratio (QHR = QH/Q) is 
 * initialized to 0.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class RevolPeer extends Peer {

	private static final String FITNESS_FUNCTION = "fitnessFunction";
	private static final String MAX_INIT_CHROMOSOME = "maxInitChromosome";
	private static final String IS_RANDOM_INIT = "isRandomInit";
	private static final String CPU_FACTOR = "cpuFactor";
	private static final String RAM_FACTOR = "ramFactor";
	private static final String DISK_FACTOR = "diskFactor";
	private String fitnessFunction = null;
	private int maxInitChromosome = 0;
	private boolean isRandomInit = false;
	private int cpuFactor = 0;
	private int ramFactor = 0;
	private int diskFactor = 0;
	private int initialCpu = 0;
	private int initialRam = 0;
	private int initialDisk = 0;
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;	
	
	private int g = 0;
	// chromosome
	private int[] c = new int[3]; 
	// query log
	private double q = 0;
	private double qh = 0;
	
	private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>();
	private ArrayList<ResourceAdv> cachedQueries = new ArrayList<ResourceAdv>();
	
	public RevolPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(FITNESS_FUNCTION))
			fitnessFunction = "F"+params.getProperty(FITNESS_FUNCTION);
		if (params.containsKey(MAX_INIT_CHROMOSOME))
			maxInitChromosome = (int) Double.parseDouble(params.getProperty(MAX_INIT_CHROMOSOME));
		if (params.containsKey(IS_RANDOM_INIT))
			isRandomInit = Boolean.parseBoolean(params.getProperty(IS_RANDOM_INIT));
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(CPU_FACTOR) )
				cpuFactor = (int) ((AllocableResource) r).getAmount();
			else if ( ((AllocableResource) r).getType().equals(RAM_FACTOR) )
				ramFactor = (int) ((AllocableResource) r).getAmount();
			else if ( ((AllocableResource) r).getType().equals(DISK_FACTOR) )
				diskFactor = (int) ((AllocableResource) r).getAmount();
		}	
	}
	
	public Object clone() {
		RevolPeer clone = (RevolPeer) super.clone();
		clone.g = 0;
		clone.c = new int[3];
		Random random = Engine.getDefault().getSimulationRandom(); 
		for (int i = 0; i < 3; i++)
			if (isRandomInit)
				clone.c[i] = random.nextInt(maxInitChromosome) + 1; // c_i is random in in [1,maxInitChromosome]
			else
				clone.c[i] = maxInitChromosome; 
		
		clone.setInitialCpu((random.nextInt(cpuFactor)+1)*512);
		clone.setInitialRam((random.nextInt(ramFactor)+1)*256);
		clone.setInitialDisk((random.nextInt(diskFactor)+1)*10000);
		
		clone.q = 0;
		clone.qh = 0;
		clone.cache = new ArrayList<ResourceAdv>();
		clone.cachedQueries = new ArrayList<ResourceAdv>();
		return clone;
	}

	public double getMaxInitChromosome() {
		return maxInitChromosome;
	}
	
	public String getFitnessFunction() {
		return fitnessFunction;
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

	public int getInitialCpu() {
		return initialCpu;
	}

	public void setInitialCpu(int initialCpu) {
		this.initialCpu = initialCpu;
		this.cpu = initialCpu;
	}

	public int getInitialRam() {
		return initialRam;
	}

	public void setInitialRam(int initialRam) {
		this.initialRam = initialRam;
		this.ram = initialRam;
	}

	public int getInitialDisk() {
		return initialDisk;
	}

	public void setInitialDisk(int initialDisk) {
		this.initialDisk = initialDisk;
		this.disk = initialDisk;
	}

	public int getCpu() {
		return cpu;
	}
	
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
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
		// consider only neighbours with Q > 0
		double sumQhr = this.getQhr();
		RevolPeer currentNeighbor = null;
		int ns = 1;
		for (Iterator<Peer> it = this.getNeighbors().iterator(); it.hasNext(); ) {
			currentNeighbor = (RevolPeer) it.next();
			if (currentNeighbor.getQ() > 0) {
				sumQhr += currentNeighbor.getQhr();
				ns += 1;
			}
		}
		return sumQhr / ns;
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
			RevolPeer currentNode = (RevolPeer) currentResourceAdv.getOwner();
			if (currentNode == null)
				this.removeResourceAdvFromCache(currentResourceAdv);
		}
		
		int dMax = this.getDMax();
		int numResourceAdvs = cache.size();
		if (numResourceAdvs <= dMax)
			return;
		ArrayList<ResourceAdv> newResourceAdvsList = new ArrayList<ResourceAdv>();
		// mantengo solo le pi� recenti
		for (int i = (numResourceAdvs - dMax); i < numResourceAdvs; i++)
			newResourceAdvsList.add((ResourceAdv) cache.get(i));
		cache = newResourceAdvsList;
		
		// cleaning the query cache
		for (Iterator<ResourceAdv> it = cachedQueries.iterator(); it.hasNext();) {
			ResourceAdv currentCachedQuery = it.next();	
			RevolPeer currentNode = (RevolPeer) currentCachedQuery.getOwner();
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
