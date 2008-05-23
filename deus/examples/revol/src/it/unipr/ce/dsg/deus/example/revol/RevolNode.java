package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


public class RevolNode extends Node {
	private static final String CPU_FACTOR = "cpuFactor";
	private static final String RAM_FACTOR = "ramFactor";
	private static final String DISK_FACTOR = "diskFactor";
	private int cpuFactor = 0;
	private int ramFactor = 0;
	private int diskFactor = 0;	
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;	
	
	private int g = 0;
	// chromosome
	private int[] c = new int[4]; 
	// query log
	private int q = 0;
	private int qh = 0;
	private double qhr = 0;
	
	private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>(); 
	
	public RevolNode(String id, Properties params)
			throws InvalidParamsException {
		super(id, params);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(CPU_FACTOR))
			cpuFactor = Integer.parseInt(params.getProperty(CPU_FACTOR));
		if (params.containsKey(RAM_FACTOR))
			ramFactor = Integer.parseInt(params.getProperty(RAM_FACTOR));
		if (params.containsKey(DISK_FACTOR))
			diskFactor = Integer.parseInt(params.getProperty(DISK_FACTOR));
	}
	
	public Object clone() {
		RevolNode clone = (RevolNode) super.clone();
		clone.g = 0;
		clone.c = new int[4];
		Random random = Engine.getDefault().getSimulationRandom(); 
		for (int i = 0; i < 4; i++)
			clone.c[i] = random.nextInt(10) + 1; // each gene is a random integer in [1,10]
		clone.setCpu((random.nextInt(cpuFactor)+1)*256);
		clone.setRam((random.nextInt(ramFactor)+1)*256);
		clone.setDisk((random.nextInt(diskFactor)+1)*256);
		clone.q = 0;
		clone.qh = 0;
		clone.qhr = 0;
		clone.cache = new ArrayList<ResourceAdv>();
		return clone;
	}
	
	public int getKMax() {
		return ((int) c[0]/2 + 1);
	}

	public double getFk() {
		return (double) c[1]/10;
	}

	public int getTtlMax() {
		return c[2];
	}

	public int getDMax() {
		return c[3]*2;
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
		return qhr;
	}

	public void setQhr(double qhr) {
		this.qhr = qhr;
	}

	public void updateQhr() {
		this.qhr = this.qh / this.q;
	}
	
	public ArrayList<ResourceAdv> getCache() {
		return cache;
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
	
	
	// FIXME deve buttare via quelli con fitness più bassa!
	public void dropExceedingNeighbors() {
		//System.out.println("number of nodes: " + Engine.getDefault().getNodes().size());
		//System.out.println("pre drop: k = " + neighbors.size());
		int kMax = this.getKMax();
		//System.out.println("kMax = " + kMax);
		int numNeighbors = neighbors.size();
		if (numNeighbors <= kMax)
			return;
		ArrayList<Node> newNeighborsList = new ArrayList<Node>();
		for (int i = (numNeighbors - kMax); i < numNeighbors; i++)
			newNeighborsList.add((RevolNode) neighbors.get(i));
		neighbors = newNeighborsList;
		//System.out.println("post drop: k = " + neighbors.size());
	}
	
	// FIXME buttare via anzitutto quelli i cui associated nodes sono null!
	public void dropExceedingResourceAdvs() {
		int dMax = this.getKMax();
		int numResourceAdvs = this.cache.size();
		if (numResourceAdvs <= dMax)
			return;
		ArrayList<ResourceAdv> newResourceAdvsList = new ArrayList<ResourceAdv>();
		for (int i = (numResourceAdvs - dMax); i < numResourceAdvs; i++)
			newResourceAdvsList.add((ResourceAdv) this.cache.get(i));
		this.cache = newResourceAdvsList;
	}

	public void setCache(ArrayList<ResourceAdv> cache) {
		this.cache = cache;
	}

}
