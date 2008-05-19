package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


public class RevolNode extends Node {
	private static final String CPU = "cpu";
	private static final String RAM = "ram";
	private static final String DISK = "disk";
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
		if (params.containsKey(CPU))
			cpu = Integer.parseInt(params.getProperty(CPU));
		if (params.containsKey(RAM))
			ram = Integer.parseInt(params.getProperty(RAM));
		if (params.containsKey(DISK))
			disk = Integer.parseInt(params.getProperty(DISK));
	}
	
	public Object clone() {
		RevolNode clone = (RevolNode) super.clone();
		clone.g = 0;
		clone.c = new int[4];
		Random random = Engine.getDefault().getSimulationRandom(); 
		for (int i = 0; i < 4; i++)
			clone.c[i] = random.nextInt(10) + 1; // each gene is a random integer in [1,10]
		clone.setCpu((random.nextInt(cpu)+1)*1000);
		clone.setRam((random.nextInt(ram)+1)*512);
		clone.setDisk((random.nextInt(disk)+1)*512);
		clone.q = 0;
		clone.qh = 0;
		clone.qhr = 0;
		clone.cache = new ArrayList<ResourceAdv>();
		return clone;
	}
	
	public int getKMax() {
		return c[0]*2;
	}

	public double getFk() {
		return c[1]/10;
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
	
	public void dropExceedingNeighbors() {
		int kMax = this.getKMax();
		int numNeighbors = this.neighbors.size();
		if (numNeighbors <= kMax)
			return;
		ArrayList<Node> newNeighborsList = new ArrayList<Node>();
		for (int i = (numNeighbors - kMax); i < numNeighbors; i++)
			newNeighborsList.add((RevolNode) this.neighbors.get(i));
		this.neighbors = newNeighborsList;
	}
	
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
