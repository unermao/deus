package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;


public class RevolNode extends Node {
	/*
	private static final String CPU_FACTOR = "cpuFactor";
	private static final String RAM_FACTOR = "ramFactor";
	private static final String DISK_FACTOR = "diskFactor";
	private int cpuFactor = 0;
	private int ramFactor = 0;
	private int diskFactor = 0;
	private int initialCpu = 0;
	private int initialRam = 0;
	private int initialDisk = 0;
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;	
	*/
	
	private int g = 0;
	// chromosome
	private int[] c = new int[3]; 
	// query log
	private int q = 0;
	private int qh = 0;
	
	private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>(); 
	
	public RevolNode(String id, Properties params)
			throws InvalidParamsException {
		super(id, params);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		/*
		if (params.containsKey(CPU_FACTOR))
			cpuFactor = Integer.parseInt(params.getProperty(CPU_FACTOR));
		if (params.containsKey(RAM_FACTOR))
			ramFactor = Integer.parseInt(params.getProperty(RAM_FACTOR));
		if (params.containsKey(DISK_FACTOR))
			diskFactor = Integer.parseInt(params.getProperty(DISK_FACTOR));
			*/
	}
	
	public Object clone() {
		RevolNode clone = (RevolNode) super.clone();
		clone.g = 0;
		clone.c = new int[3];
		Random random = Engine.getDefault().getSimulationRandom(); 
		for (int i = 0; i < 3; i++)
			clone.c[i] = random.nextInt(10) + 1; // each gene is a random integer in [1,10]
		clone.setInitialCpu((random.nextInt(cpuFactor)+1)*256);
		clone.setInitialRam((random.nextInt(ramFactor)+1)*256);
		clone.setInitialDisk((random.nextInt(diskFactor)+1)*256);
		clone.q = 0;
		clone.qh = 0;
		clone.cache = new ArrayList<ResourceAdv>();
		return clone;
	}

	public double getFk() {
		return (double) c[0]/10;
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

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public int getQh() {
		return qh;
	}

	public void setQh(int qh) {
		this.qh = qh;
	}

	public double getQhr() {
		if (this.q == 0)
			return 0;
		else
			return this.qh / this.q;
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
			Node currentNode = currentResourceAdv.getOwner();
			if ((currentNode == null) || (!currentNode.isReachable()))
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
