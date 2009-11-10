package it.unipr.ce.dsg.deus.example.geokad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.LinkedList;

import it.unipr.ce.dsg.deus.p2p.node.Peer;
import it.unipr.ce.dsg.deus.core.*;
import it.unipr.ce.dsg.deus.example.geokad.GeoKadResourceType;

import java.util.Properties;

public class GeoKadPeer extends Peer {
	
	private static final String NUM_OF_KBUCKETS = "numOfKBuckets";
	private static final String RESOURCES_NODE = "resourcesNode";
	private static final String ALPHA = "alpha";
	private static final String RAY_DISTANCE = "rayDistance";
	private int alpha = 3;
	private static final String DISCOVERY_MAX_WAIT = "discoveryMaxWait";
	private float discoveryMaxWait = 500;

	private Vector<ArrayList<GeoKadPeer>> kbucket = null;

	private int numOfKBuckets = 0;
	private int resourcesNode = 0;
	private double rayDistance = 0.0;

	//Latitude and Longitude
	private double latitude = 0.0;
	private double longitude = 0.0;
	
	public Map<Integer, Integer> logSearch = new HashMap<Integer, Integer>();

	public ArrayList<GeoKadResourceType> kademliaResources = new ArrayList<GeoKadResourceType>();
	public ArrayList<GeoKadResourceType> storedResources = new ArrayList<GeoKadResourceType>();
	public HashMap<Integer, SearchResultType> nlResults = new HashMap<Integer, SearchResultType>();
	public ArrayList<GeoKadPeer> nlContactedNodes = new ArrayList<GeoKadPeer>();

	public static ArrayList<GeoKadPoint> pointsList = new ArrayList<GeoKadPoint>();
	
	public GeoKadPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);

		//int size = 100;
		// int size = (int) Math.floor(Math.log
		// (Engine.getDefault().getKeySpaceSize())
		// /Math.log(2) +0.5); Invocation Target Exception!

		if (params.getProperty(NUM_OF_KBUCKETS) == null)
			throw new InvalidParamsException(NUM_OF_KBUCKETS
					+ " param is expected");
		try {
			numOfKBuckets = Integer.parseInt(params.getProperty(NUM_OF_KBUCKETS));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(NUM_OF_KBUCKETS
					+ " must be a valid int value.");
		}
		if (params.containsKey(RESOURCES_NODE))
			this.setResourcesNode(Integer.parseInt(params
					.getProperty(RESOURCES_NODE)));

		if (params.getProperty(ALPHA) != null) {
			alpha = Integer.parseInt(params.getProperty(ALPHA));
		}

		if (params.getProperty(DISCOVERY_MAX_WAIT) != null) {
			try {
				setDiscoveryMaxWait(Float.parseFloat(params
						.getProperty(DISCOVERY_MAX_WAIT)));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(DISCOVERY_MAX_WAIT
						+ " must be a valid float value.");
			}
		}
		
		if (params.getProperty(RAY_DISTANCE) != null) {
			try {
				setRayDistance(Double.parseDouble(params
						.getProperty(RAY_DISTANCE)));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(RAY_DISTANCE
						+ " must be a valid double value.");
			}
		}
		
		// this.kbucket = new Vector<LinkedList<KademliaPeer>>();
		this.kbucket = new Vector<ArrayList<GeoKadPeer>>();
		for (int i = 0; i < this.numOfKBuckets; i++) {
			// kbucket.add(i, new LinkedList<KademliaPeer>());
			kbucket.add(i, new ArrayList<GeoKadPeer>());
		}
		
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File("examples/geokad/1000_Lat_Lon_Points.csv"))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{
				//System.out.println("Linea: "+ line + " Lat: " + line.split(",")[0] + " Lon: " + line.split(",")[1] );
				
				pointsList.add(new GeoKadPoint(Double.parseDouble(line.split(",")[0]), Double.parseDouble(line.split(",")[1])));
				
				line = br.readLine();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	public Object clone() {
		
		GeoKadPeer clone = (GeoKadPeer) super.clone();
		// clone.kbucket = new Vector<LinkedList<KademliaPeer>>();
		clone.kbucket = new Vector<ArrayList<GeoKadPeer>>();
		
//		int size = (int) Math.floor(Math.log(Engine.getDefault()
//				.getKeySpaceSize())
//				/ Math.log(2) + 1.5);

		
		//Create the list of KBuckets
		for (int i = 0; i < this.numOfKBuckets; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			clone.kbucket.add(i, new ArrayList<GeoKadPeer>());
		}
		
		clone.kademliaResources = new ArrayList<GeoKadResourceType>();
		clone.storedResources = new ArrayList<GeoKadResourceType>();
		clone.nlResults = new HashMap<Integer, SearchResultType>();
		clone.nlContactedNodes = new ArrayList<GeoKadPeer>();
		clone.logSearch = new HashMap<Integer, Integer>();

		//Set Random Lat e Lon values
		int index = Engine.getDefault().getSimulationRandom().nextInt(pointsList.size());
		GeoKadPoint point = pointsList.get(index);
		pointsList.remove(index);
		
		clone.latitude = point.getLat();
		clone.longitude = point.getLon();
		
		//System.out.println(clone.latitude + "-" + clone.longitude);
		
		//TODO Caricare la lista delle posizioni per il singolo nodo
		
		return clone;
	}

	public int getResourcesNode() {
		return resourcesNode;
	}

	public void setResourcesNode(int i) {
		this.resourcesNode = i;
	}

	public void deathKademliaNode() {
		this.setConnected(false);
	}

	/**
	 * Add newPeer in the right K-Bucket
	 * 
	 * @param newPeer
	 */
	public void insertPeer(GeoKadPeer newPeer) {
		
		//System.out.println("INSERTTTTTTTTT");
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		//int distance = this.getKey() ^ newPeer.getKey();
		double distance = GeoKadDistance.distance(this, newPeer);
		
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets-1); i++)
		{
			//System.out.println(this.getKey() + "@Distance: " + distance + " IF: " + (double)(i)*rayDistance);
			
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
				
				//GOSSIP
				//for(int j=0; j<this.kbucket.get(i).size(); j++)
					//this.kbucket.get(i).get(j).insertPeerByGossip(newPeer);
					
				//Add the peer in the right bucket
				if(!this.kbucket.get(i).contains(newPeer))
					this.kbucket.get(i).add(newPeer);
				
				//System.out.println("ADD");
				
				bucketFounded = true;
				
				//System.out.println("Distance: " + distance + " KBucket Index: " + i);
				
				break;
			}
		}
		
		//If the peer's distance is very high it will be added in the last available KBucket
		if(bucketFounded == false)
		{
			//Add new Peer in the last Bucket
			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer))
				kbucket.get(numOfKBuckets-1).add(newPeer);
		}
		
	}

	public void insertPeerByGossip(GeoKadPeer newPeer) {
		
		//System.out.println("INSERTTTTTTTTT");
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		//int distance = this.getKey() ^ newPeer.getKey();
		double distance = GeoKadDistance.distance(this, newPeer);
		
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets-1); i++)
		{
			//System.out.println(this.getKey() + "@Distance: " + distance + " IF: " + (double)(i)*rayDistance);
			
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{		
				//Add the peer in the right bucket
				if(!this.kbucket.get(i).contains(newPeer))
					this.kbucket.get(i).add(newPeer);
				
				//System.out.println("ADD");
				
				bucketFounded = true;
				
				//System.out.println("Distance: " + distance + " KBucket Index: " + i);
				
				break;
			}
		}
		
		//If the peer's distance is very high it will be added in the last available KBucket
		if(bucketFounded == false)
		{
			//Add new Peer in the last Bucket
			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer))
				kbucket.get(numOfKBuckets-1).add(newPeer);
		}
		
	}
	
	private int findIndexForDistance(double distance)
	{
		int index = 0;
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets-1); i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
				bucketFounded = true;
				index=i;
				break;
			}
		}
		
		if(bucketFounded == false)
		{
			index = numOfKBuckets-1;
		}
		
		return index;
	}
	
	public boolean ping(GeoKadPeer peer) {
		if (Engine.getDefault().getNodes().contains(peer)) {
			return true;
		}
		return false;
	}

	public ArrayList<GeoKadPeer> find_node(GeoKadPeer peer) {
		
		double distance = GeoKadDistance.distance(this, peer);
		
		int index = findIndexForDistance(distance);
		
		ArrayList<GeoKadPeer> tempResults = new ArrayList<GeoKadPeer>();
		Iterator<GeoKadPeer> it;

		it = kbucket.get(index).iterator();
		
		while (it.hasNext())
			tempResults.add(it.next());

		boolean flag = false;
		int a = 1;
		while (tempResults.size() < numOfKBuckets) {
			flag = false;
			try {
				it = kbucket.get(index + a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				flag = true;
			}

			if (tempResults.size() >= numOfKBuckets)
				break;

			try {
				it = kbucket.get(index - a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				if (flag)
					break;
			}
			a++;
		}
		if (tempResults.size() > numOfKBuckets) {
			tempResults.subList(numOfKBuckets, tempResults.size()).clear();
		}

		return tempResults;
	}

	public void store(GeoKadResourceType res) {
		if (!this.storedResources.contains(res)) {
			this.storedResources.add(res);
		}
	}

	/*
	public Object find_value(int key) {
		int idx = this.storedResources.indexOf(new GeoKadResourceType(key));

		if (idx != -1) {
			return this.storedResources.get(idx);
		}

		return this.find_node(key);
	}
	*/

	public int getAlpha() {
		return alpha;
	}

	public int getKBucketDim() {
		return numOfKBuckets;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public Vector<ArrayList<GeoKadPeer>> getKbucket() {
		return kbucket;
	}

	public void rawInsertPeer(GeoKadPeer newPeer) {
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		//int distance = this.getKey() ^ newPeer.getKey();
		double distance = GeoKadDistance.distance(this, newPeer);
		
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets-1); i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
				//GOSSIP
				//for(int j=0; j<this.kbucket.get(i).size(); j++)
					//this.kbucket.get(i).get(j).insertPeerByGossip(newPeer);
				
				//Add the peer in the right bucket
				if(!this.kbucket.get(i).contains(newPeer))
					this.kbucket.get(i).add(newPeer);
				
				bucketFounded = true;
				
				break;
			}
		}
		
		//If the peer's distance is very high it will be added in the last available KBucket
		if(bucketFounded == false)
		{
			//Add new Peer in the last Bucket
			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer))
				kbucket.get(numOfKBuckets-1).add(newPeer);
		}
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getRayDistance() {
		return rayDistance;
	}

	public void setRayDistance(double rayDistance) {
		this.rayDistance = rayDistance;
	}
}
