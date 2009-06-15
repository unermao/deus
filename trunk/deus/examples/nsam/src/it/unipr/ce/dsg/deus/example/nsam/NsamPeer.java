package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.example.simpleDataDriven.ServerPeer;
import it.unipr.ce.dsg.deus.example.simpleDataDriven.StreamingPeer;
import it.unipr.ce.dsg.deus.example.simpleDataDriven.VideoChunk;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;


public class NsamPeer extends Peer {

	
	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String CPU ="cpuFactor";
	private static final String RAM = "ramFactor";
	private static final String DISK = "diskFactor";
	private static final String BANDWIDTH = "bandwidth";
	private static final String IS_RANDOM_INIT = "isRandomInit";
	private static final String QUALITY_FACTOR = "qos";
	private static final String MAX_SERVICE_NUMBER ="maxServiceNum";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String MAX_SERVICE_INPUT_NUM = "maxServiceInputNum";
	private static final String MAX_SERVICE_OUTPUT_NUM = "maxServiceOutputNum";
	private static final String SERVICE_INPUT_RANGE = "serviceInputRange";
	private static final String SERVICE_OUTPUT_RANGE="serviceOutputRange";
	
	private boolean isRandomInit = false;

	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";

	private int qos = 0;
	private double battery = 0.0;
	private String connectionType = "";
	private int bandwidth = 0;
	private int cpuFactor = 0;
	private int maxServiceNum =0;
	private int maxServiceInputNum = 0;
	private int maxServiceOutputNum = 0;
	private int serviceInputRange = 0;
	private int serviceOutputRange = 0;
	private int ramFactor = 0;
	private int diskFactor = 0;
	private int initialCpu = 0;
	private int initialRam = 0;
	private int initialDisk = 0;
	private int cpu = 0;
	private int ram = 0;
	private int disk = 0;	
	
	private int maxAcceptedConnection = 0;


//	private NsamServerPeer serverNode = null;
	private  ArrayList<Service> serviceList = new ArrayList<Service>();
	private ArrayList<ResourceAdv> cache = new ArrayList<ResourceAdv>();
	private ArrayList<ResourceAdv> cachedQueries = new ArrayList<ResourceAdv>();
	
	public NsamPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	
		
	
public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(BATTERY))
			battery = Double.parseDouble(params.getProperty(BATTERY));
		
		if (params.containsKey(CONNECTION_TYPE))
			connectionType = new String(params.getProperty(CONNECTION_TYPE));
		
		if (params.containsKey(QUALITY_FACTOR))
			qos = Integer.parseInt(params.getProperty(QUALITY_FACTOR));
		
		if (params.containsKey(BANDWIDTH))
			bandwidth = Integer.parseInt(params.getProperty(BANDWIDTH));
		
		if (params.containsKey(MAX_SERVICE_NUMBER))
		maxServiceNum = Integer.parseInt(params.getProperty(MAX_SERVICE_NUMBER));
		
		if (params.containsKey(MAX_SERVICE_INPUT_NUM))
			maxServiceInputNum = Integer.parseInt(params.getProperty(MAX_SERVICE_INPUT_NUM));
		
		if (params.containsKey(MAX_SERVICE_OUTPUT_NUM))
			maxServiceOutputNum = Integer.parseInt(params.getProperty(MAX_SERVICE_OUTPUT_NUM));
		
		if (params.containsKey(SERVICE_INPUT_RANGE))
			serviceInputRange = Integer.parseInt(params.getProperty(SERVICE_INPUT_RANGE));
		
		if (params.containsKey(SERVICE_OUTPUT_RANGE))
			serviceOutputRange = Integer.parseInt(params.getProperty(SERVICE_OUTPUT_RANGE));
		
		if (params.containsKey(IS_RANDOM_INIT))
			isRandomInit = Boolean.parseBoolean(params.getProperty(IS_RANDOM_INIT));
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
	}
	
	public Object clone() {
		NsamPeer clone = (NsamPeer) super.clone();
		clone.battery = this.battery;
		clone.connectionType = this.connectionType;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		
		clone.isConnected = true;
		clone.serviceList = this.serviceList;
	//	clone.cache = new ArrayList<ResourceAdv>();
	//	clone.cachedQueries = new ArrayList<ResourceAdv>();
		return clone;
	}

	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}
	
	public int getTtlMax() {
		return 7;
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
	
	
	public void removeResourceAdvFromCache(ResourceAdv currentResourceAdv) {
		ArrayList<ResourceAdv> newCache = new ArrayList<ResourceAdv>();
		for (Iterator<ResourceAdv> it = cache.iterator(); it.hasNext();) {
			ResourceAdv r = it.next();
			if (!r.equals(currentResourceAdv))
				newCache.add(r);
		}
		cache = newCache;
	}
	
	public ArrayList<Service> createServiceList (){
		
		 /*creo una array list che al max ha maxServiceNum elementi */
		 int numServices = Engine.getDefault().getSimulationRandom().nextInt(maxServiceNum);
		 for (int i=0; i<numServices; i++)
		 {
			 Service service = new Service(maxServiceInputNum, maxServiceOutputNum,serviceInputRange, serviceOutputRange);
			 serviceList.add(service); 
		 } 
		 return serviceList;
	}	
}
