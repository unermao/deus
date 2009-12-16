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

	private ArrayList<GeoKadPoint> pathCoordinates = new ArrayList<GeoKadPoint>();
	
	private int numOfKBuckets = 0;
	private int resourcesNode = 0;
	private double rayDistance = 0.0;

	//Latitude and Longitude
	private double latitude = 0.0;
	private double longitude = 0.0;
	
	//Path Index
	private int pathIndex = 0;
	
	//Direction Delta Value
	private int deltaValue = 1;
	
	//Path Direction Forward
	private boolean pathDirectionForward = true;
	
	private GeoKadPoint startPoint = null;
	private GeoKadPoint endPoint = null;
	
	public Map<Integer, Integer> logSearch = new HashMap<Integer, Integer>();

	public ArrayList<GeoKadResourceType> kademliaResources = new ArrayList<GeoKadResourceType>();
	public ArrayList<GeoKadResourceType> storedResources = new ArrayList<GeoKadResourceType>();
	public HashMap<Integer, SearchResultType> nlResults = new HashMap<Integer, SearchResultType>();
	public ArrayList<GeoKadPeer> nlContactedNodes = new ArrayList<GeoKadPeer>();
	

	public static ArrayList<String> pathList = new ArrayList<String>();
	
	public static ArrayList<Double> graphKey = new ArrayList<Double>();
	
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
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File("examples/geokad/path_result_2000.txt"))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{
				//System.out.println("Linea: "+ line );
				
				pathList.add(line);
				
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	public Object clone() {
		
		GeoKadPeer clone = (GeoKadPeer) super.clone();
		// clone.kbucket = new Vector<LinkedList<KademliaPeer>>();
		clone.kbucket = new Vector<ArrayList<GeoKadPeer>>();
			
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

		//Get Random Path
		int index = Engine.getDefault().getNodes().size();
		String path = pathList.get(index);
		
		//Remove from available list
		pathList.remove(index);
		
		//Split to find single lat,log point
		String[] pathPoints = path.split("#");
		
		clone.pathCoordinates = new ArrayList<GeoKadPoint>();
		
		//Add GPS Points to node's list
		for (int i = 0; i < pathPoints.length; i++)
		{
			double lat = Double.parseDouble(pathPoints[i].split(",")[0]);
			double lon = Double.parseDouble(pathPoints[i].split(",")[1]);
			
			clone.pathCoordinates.add(new GeoKadPoint(lat, lon));
		}
			
		//Set Start end Final Point for the path
		clone.startPoint = clone.pathCoordinates.get(0);
		clone.endPoint = clone.pathCoordinates.get(clone.pathCoordinates.size()-1);
		
		//Set Current Position
		clone.latitude = clone.pathCoordinates.get(pathIndex).getLat();
		clone.longitude = clone.pathCoordinates.get(pathIndex).getLon();
		
		//Remove position 0
		//clone.pathCoordinates.remove(0);
		
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
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		//Update Info
		newPeer = (GeoKadPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
		
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(newPeer);
			
			if( index != -1)
			{
				//Set new PeerInfo
				this.kbucket.get(i).remove(index);
				break;
			}
		}
		
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
				
				bucketFounded = true;
				
				//System.out.println("Distance: " + distance + " KBucket Index: " + i);
				
				break;
			}
		}
		
		//If the peer's distance is very high it will be added in the last available KBucket
		if(bucketFounded == false)
		{
			//Add new Peer in the last Bucket
			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer)&& this.kbucket.get(numOfKBuckets-1).size() < 20)
				kbucket.get(numOfKBuckets-1).add(newPeer);
		}
		
	}

	public void insertPeerByGossip(GeoKadPeer newPeer) {
		
		//System.out.println("INSERTTTTTTTTT");
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		newPeer = (GeoKadPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
		
		//Check if the peer is in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(newPeer);
			
			if( index != -1)
			{
				this.kbucket.get(i).remove(index);
				break;
			}
		}
		
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

		int maxSize = numOfKBuckets;
		
		boolean flag = false;
		int a = 1;
		while (tempResults.size() < maxSize) {
			flag = false;
			try {
				it = kbucket.get(index + a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				flag = true;
			}

			if (tempResults.size() >= maxSize)
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
		if (tempResults.size() > maxSize) {
			tempResults.subList(maxSize, tempResults.size()).clear();
		}

		if(tempResults.size() == 0)
			System.out.println("FIND NODE ---> RETURN AN EMPTY LIST !");
		
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

		newPeer = (GeoKadPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
		
		//Check if the peer is in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(newPeer);
			
			if( index != -1)
			{
				//Set new PeerInfo
				this.kbucket.get(i).remove(index);
				break;
			}
		}
		
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
			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer) && this.kbucket.get(numOfKBuckets-1).size() < 20)
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

	/**
	 * Move the node to a new position according to his path
	 */
	public void move(float triggeringTime) {
		
		if(this.pathCoordinates.size() > 0 )
		{
			//If it is at the end of the path or at the beginning, it changes the direction
			if( pathCoordinates.size()-1 == pathIndex || ( pathIndex == 0 && deltaValue  == -1) )
				pathDirectionForward = !pathDirectionForward;	

			if(pathDirectionForward == true)
				deltaValue = 1;
			else
				deltaValue = -1;
			
			pathIndex = pathIndex + deltaValue;
			
			//Set Current Position
			this.latitude = this.pathCoordinates.get(pathIndex).getLat();
			this.longitude = this.pathCoordinates.get(pathIndex).getLon();
			
			//Remove position 0
			//this.pathCoordinates.remove(0);
			
			//TODO Edit in order to update only neighbors of the first "alpha" KBuckets
			//Send updated information to its neighbors
			for(int i=0; i<(numOfKBuckets-1); i++)
			{
				for(int k=0; k <  this.kbucket.get(i).size(); k++)
					this.kbucket.get(i).get(k).updateInfoAboutNode(this);
			}
			
			
			//Move the peer
			this.scheduleMove(triggeringTime);
			
		}
			
	}
	
	public void scheduleMove(float triggeringTime) {
		//Create a new move element
		try {
			
			//Random Time for a single peer move (50VT=3 min)
			int randomTime = 50;
			
			if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
				randomTime = 100;
				
			float delay = Engine.getDefault().getSimulationRandom().nextFloat()*(randomTime);
			
			//System.out.println("Delay: " + delay + " New VT: " + (triggeringTime+delay));
			
			GeoKadMoveNodeEvent moveEvent = (GeoKadMoveNodeEvent) new GeoKadMoveNodeEvent("node_lookup", params, null).createInstance(triggeringTime + delay);
			moveEvent.setOneShot(true);
			moveEvent.setAssociatedNode(this);
			Engine.getDefault().insertIntoEventsList(moveEvent);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Update information about a node
	 */
	private void updateInfoAboutNode(GeoKadPeer peer)
	{		
		this.insertPeer(peer);	
	}

	public ArrayList<GeoKadPoint> getPathCoordinates() {
		return pathCoordinates;
	}

	public void setPathCoordinates(ArrayList<GeoKadPoint> pathCoordinates) {
		this.pathCoordinates = pathCoordinates;
	}

	public int getNumOfKBuckets() {
		return numOfKBuckets;
	}

	public void setNumOfKBuckets(int numOfKBuckets) {
		this.numOfKBuckets = numOfKBuckets;
	}

	public Map<Integer, Integer> getLogSearch() {
		return logSearch;
	}

	public void setLogSearch(Map<Integer, Integer> logSearch) {
		this.logSearch = logSearch;
	}

	public ArrayList<GeoKadResourceType> getKademliaResources() {
		return kademliaResources;
	}

	public void setKademliaResources(ArrayList<GeoKadResourceType> kademliaResources) {
		this.kademliaResources = kademliaResources;
	}

	public ArrayList<GeoKadResourceType> getStoredResources() {
		return storedResources;
	}

	public void setStoredResources(ArrayList<GeoKadResourceType> storedResources) {
		this.storedResources = storedResources;
	}

	public HashMap<Integer, SearchResultType> getNlResults() {
		return nlResults;
	}

	public void setNlResults(HashMap<Integer, SearchResultType> nlResults) {
		this.nlResults = nlResults;
	}

	public ArrayList<GeoKadPeer> getNlContactedNodes() {
		return nlContactedNodes;
	}

	public void setNlContactedNodes(ArrayList<GeoKadPeer> nlContactedNodes) {
		this.nlContactedNodes = nlContactedNodes;
	}

	public static ArrayList<String> getPathList() {
		return pathList;
	}

	public static void setPathList(ArrayList<String> pathList) {
		GeoKadPeer.pathList = pathList;
	}

	public static String getNumOfKbuckets() {
		return NUM_OF_KBUCKETS;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setKbucket(Vector<ArrayList<GeoKadPeer>> kbucket) {
		this.kbucket = kbucket;
	}

	public GeoKadPoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(GeoKadPoint startPoint) {
		this.startPoint = startPoint;
	}

	public GeoKadPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(GeoKadPoint endPoint) {
		this.endPoint = endPoint;
	}
}
