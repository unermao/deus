package it.unipr.ce.dsg.deus.example.geokad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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
	private static final String GOSSIP = "gossip";
	private static final double MAX_SPEED = 80.0;
	private static final int LAST_GEO_BUCKET_SIZE = 20;
	private float discoveryMaxWait = 500;

	private Vector<ArrayList<GeoKadPeerInfo>> kbucket = null;

	private ArrayList<GeoKadPeerInfo> periodicPeerList = new ArrayList<GeoKadPeerInfo>();
	
	private ArrayList<GeoKadPoint> pathCoordinates = new ArrayList<GeoKadPoint>();
	
	private int numOfKBuckets = 0;
	private int resourcesNode = 0;
	private double rayDistance = 0.0;

	//Latitude and Longitude
	private double base_latitude = 0.0;
	private double base_longitude = 0.0;
	private double bootstrap_base_latitude = 0.0;
	private double bootstrap_base_longitude = 0.0;
	private double old_latitude = 0.0;
	private double old_longitude = 0.0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	
	//Speed
	private double original_speed = 0.0;
	private double speed = 0.0;
	
	//Path Index
	private int pathIndex = 0;
	
	//Direction Delta Value
	private int deltaValue = 1;
	
	//Path Direction Forward
	private boolean pathDirectionForward = true;
	
	//Number of sent messages
	private int sentMessages = 0;

	//Timestamp
	private float timeStamp = 0;
	
	//Gossip Flag
	private boolean isGossipActive = false;
	
	//Flag for active discovery
	private boolean isDiscoveryActive = false;
	
	//Counter of performed step for each discovery procedure
	private int avDiscoveryStepCounter = 0;
	private int discoveryCounter = 0;
	
	private GeoKadPoint startPoint = null;
	private GeoKadPoint endPoint = null;
	
	public Map<Integer, Integer> logSearch = new HashMap<Integer, Integer>();

	public ArrayList<GeoKadResourceType> kademliaResources = new ArrayList<GeoKadResourceType>();
	public ArrayList<GeoKadResourceType> storedResources = new ArrayList<GeoKadResourceType>();
	public HashMap<Integer, SearchResultType> nlResults = new HashMap<Integer, SearchResultType>();
	public ArrayList<GeoKadPeerInfo> nlContactedNodes = new ArrayList<GeoKadPeerInfo>();
	

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
		
		if (params.getProperty(GOSSIP) != null) {
			
			try {
				this.isGossipActive = Boolean.parseBoolean(params
						.getProperty(GOSSIP));
				
				//System.out.println("GOSSIP STATUS: " +  this.isGossipActive);
				
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(GOSSIP
						+ " must be a valid Boolean value.");
			}
		}
		
		// this.kbucket = new Vector<LinkedList<KademliaPeer>>();
		this.kbucket = new Vector<ArrayList<GeoKadPeerInfo>>();
		for (int i = 0; i < this.numOfKBuckets; i++) {
			// kbucket.add(i, new LinkedList<KademliaPeer>());
			kbucket.add(i, new ArrayList<GeoKadPeerInfo>());
		}
		
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File("examples/geokad/path_result_3000.txt"))));
			
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
		clone.kbucket = new Vector<ArrayList<GeoKadPeerInfo>>();
			
		//Create the list of KBuckets
		for (int i = 0; i < this.numOfKBuckets; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			clone.kbucket.add(i, new ArrayList<GeoKadPeerInfo>());
		}
		
		clone.kademliaResources = new ArrayList<GeoKadResourceType>();
		clone.storedResources = new ArrayList<GeoKadResourceType>();
		clone.nlResults = new HashMap<Integer, SearchResultType>();
		clone.nlContactedNodes = new ArrayList<GeoKadPeerInfo>();
		clone.logSearch = new HashMap<Integer, Integer>();
		clone.periodicPeerList = new ArrayList<GeoKadPeerInfo>();

		//Get Random Path
		//int index = Engine.getDefault().getNodes().size();
		//String path = pathList.get(index);
		
		if(pathList.size() == 0)
		{
			try
			{
				BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File("examples/geokad/path_result_3000.txt"))));
				
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
		
		int randomPathIndex = Engine.getDefault().getSimulationRandom().nextInt()%pathList.size();
		
		if(randomPathIndex < 0)
			randomPathIndex = -randomPathIndex;
		
		String path = pathList.get(randomPathIndex);
		pathList.remove(randomPathIndex);
		
		//Remove from available list
		//pathList.remove(index);
		
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
		
		clone.old_latitude = clone.pathCoordinates.get(pathIndex).getLat();
		clone.old_longitude = clone.pathCoordinates.get(pathIndex).getLon();
		
		clone.base_latitude = clone.pathCoordinates.get(pathIndex).getLat();
		clone.base_longitude = clone.pathCoordinates.get(pathIndex).getLon();
		
		clone.bootstrap_base_latitude = clone.pathCoordinates.get(pathIndex).getLat();
		clone.bootstrap_base_longitude = clone.pathCoordinates.get(pathIndex).getLon();
		
		//Set Speed Average
		clone.speed = Engine.getDefault().getSimulationRandom().nextDouble()*MAX_SPEED + 5.0;
		clone.original_speed = clone.speed;
		clone.isGossipActive = this.isGossipActive;
		
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
	public void insertPeer(GeoKadPeerInfo newPeer) {
		
		if (this.getKey() == newPeer.getKey())
			return;
		
		//Update Info
		//newPeer = (GeoKadPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
		
		//Save Periodic PeerList
		if(!this.periodicPeerList.contains(newPeer))
			this.periodicPeerList.add(newPeer);
		
		boolean alreadyStored = false;
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(newPeer);
			
			if( index != -1)
			{
				//Set new PeerInfo
				this.kbucket.get(i).remove(index);
				alreadyStored = true;
				break;
			}
		}
		
		//int distance = this.getKey() ^ newPeer.getKey();
		double distance = GeoKadDistance.distance(this.createPeerInfo(), newPeer);
		
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//System.out.println(this.getKey() + "@Distance: " + distance + " IF: " + (double)(i)*rayDistance);
			
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
					
				//Add the peer in the right bucket
				if(!this.kbucket.get(i).contains(newPeer))
				{
					this.kbucket.get(i).add(newPeer);
					
					
					if(alreadyStored == false)
					{
						try
						{
							GeoKadAddPeerInfoEvent nlk = (GeoKadAddPeerInfoEvent) new GeoKadAddPeerInfoEvent("node_lookup", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);

							nlk.setOneShot(true);
							nlk.setAssociatedNode((GeoKadPeer)Engine.getDefault().getNodeByKey(newPeer.getKey()));
							nlk.setPeerInfo(this.createPeerInfo());
							Engine.getDefault().insertIntoEventsList(nlk);
						}
						catch(Exception e)
						{e.printStackTrace();}
					}
					
				}
					
				
				bucketFounded = true;
				
				//System.out.println("Distance: " + distance + " KBucket Index: " + i);
				
				break;
			}
		}
		
		
//		//If the peer's distance is very high it will be added in the last available KBucket
//		if(bucketFounded == false)
//		{
//			//Add new Peer in the last Bucket
//			if(!this.kbucket.get(numOfKBuckets-1).contains(newPeer))
//			{
//				//Add as first element
//				kbucket.get(numOfKBuckets-1).add(0,newPeer);
//				
//				if(this.kbucket.get(numOfKBuckets-1).size() > LAST_GEO_BUCKET_SIZE)
//				{
//					//Send Remove Message to node
//					/*
//					try
//					{
//						GeoKadRemoveEvent nlk = (GeoKadRemoveEvent) new GeoKadRemoveEvent("node_lookup", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);
//
//						nlk.setOneShot(true);
//						nlk.setAssociatedNode((GeoKadPeer)Engine.getDefault().getNodeByKey(this.kbucket.get(numOfKBuckets-1).get(LAST_GEO_BUCKET_SIZE).getKey()));
//						nlk.setPeerInfo(this.createPeerInfo());
//						Engine.getDefault().insertIntoEventsList(nlk);
//					}
//					catch(Exception e)
//					{e.printStackTrace();}
//					*/
//					
//					this.kbucket.get(numOfKBuckets-1).remove(LAST_GEO_BUCKET_SIZE);
//				}
//			}
//		}
		
	}
	
	private int findIndexForDistance(double distance)
	{
		int index = 0;
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
				bucketFounded = true;
				index=i;
				break;
			}
		}
		/*
		if(bucketFounded == false)
		{
			index = numOfKBuckets-1;
		}
		*/
		
		return index;
	}
	
	public boolean ping(GeoKadPeer peer) {
		if (Engine.getDefault().getNodes().contains(peer)) {
			return true;
		}
		return false;
	}

	
	
	public ArrayList<GeoKadPeerInfo> find_node(GeoKadPeerInfo peer) {
		
		double distance = GeoKadDistance.distance(this.createPeerInfo(), peer);
		
		int index = findIndexForDistance(distance);
		
		ArrayList<GeoKadPeerInfo> tempResults = new ArrayList<GeoKadPeerInfo>();
		Iterator<GeoKadPeerInfo> it;

		it = kbucket.get(index).iterator();
		
		while (it.hasNext())
			tempResults.add(it.next());

		int maxSize = numOfKBuckets;
		
		boolean flag = false;
		int a = 0;
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

		
		//if(tempResults.size() == 0)
			//System.out.println("FIND NODE ---> RETURN AN EMPTY LIST !");
		
		
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

	public Vector<ArrayList<GeoKadPeerInfo>> getKbucket() {
		return kbucket;
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
		
		boolean stopWalking = false;
		
		if(this.pathCoordinates.size() > 0 )
		{
			//If it is at the end of the path or at the beginning, it changes the direction
			if( pathCoordinates.size()-1 == pathIndex || ( pathIndex == 0 && deltaValue  == -1) )
			{
				stopWalking = true;
				pathDirectionForward = !pathDirectionForward;	
			}

			if(pathDirectionForward == true)
				deltaValue = 1;
			else
				deltaValue = -1;
			
			pathIndex = pathIndex + deltaValue;
			
			//Save Old position
			this.old_latitude = this.latitude;
			this.old_longitude = this.longitude;
			
			//Set Current Position
			this.latitude = this.pathCoordinates.get(pathIndex).getLat();
			this.longitude = this.pathCoordinates.get(pathIndex).getLon();
			
			if(stopWalking == false)
			{
				//Check nodes availability in my GeoBuckets
				this.checkNodeAvailability();
				
				//Update known peers according to updated position
				this.updateGeoBucketInfo();
				
				this.timeStamp = triggeringTime + (float)(((this.rayDistance/2.0)/this.speed)*60.0*16.6);
				
				if(GeoKadDistance.distance(base_longitude, base_latitude, longitude, latitude) >= this.rayDistance/3.0)
				{
					this.base_latitude = this.latitude;
					this.base_longitude = this.longitude;
					
					//Sending Update position messages
					for(int i=0; i < (this.kbucket.size()) ; i++)
					{
						for(int k=0; k <  this.kbucket.get(i).size(); k++)
						{
							try
							{
								GeoKadUpdatePositionEvent nlk = (GeoKadUpdatePositionEvent) new GeoKadUpdatePositionEvent("node_lookup", params, null).createInstance(triggeringTime+1);

								nlk.setOneShot(true);
								nlk.setAssociatedNode((GeoKadPeer)Engine.getDefault().getNodeByKey(this.kbucket.get(i).get(k).getKey()));
								nlk.setPeerInfo(this.createPeerInfo());
								Engine.getDefault().insertIntoEventsList(nlk);
							}
							catch(Exception e)
							{e.printStackTrace();}
						}
					}
				 }
				
				//Check if is necessary to update the information to the BootstrapNode
				if(GeoKadDistance.distance(bootstrap_base_longitude, bootstrap_base_latitude, longitude, latitude) >= (double)(this.rayDistance*(double)this.numOfKBuckets))
				{	
					this.bootstrap_base_latitude = this.latitude;
					this.bootstrap_base_longitude = this.longitude;
					
					//Update info in the bootstrap
					GeoKadBootStrapPeer bootStrap = null;
					bootStrap = (GeoKadBootStrapPeer)Engine.getDefault().getNodeByKey(GeoKadBootStrapPeer.BOOTSTRAP_KEY);
					bootStrap.addIncomingNode(this);
				}
				
				this.scheduleMove(triggeringTime);
			 }
			else
			{
				this.timeStamp = -1;
			}
			
			
			//Remove position 0
			//this.pathCoordinates.remove(0);
			
			//TODO Edit in order to update only neighbors of the first "alpha" KBuckets
			//Send updated information to its neighbors
			
			
			//Update position of all known nodes
			
		}
			
	}
	
	public void updateGeoBucketInfo() {
	
		Vector<ArrayList<GeoKadPeerInfo>> localGeoBucketVector = new Vector<ArrayList<GeoKadPeerInfo>>();
		
		//Create the list of KBuckets
		for (int i = 0; i < this.numOfKBuckets; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			localGeoBucketVector.add(i, new ArrayList<GeoKadPeerInfo>());
		}
		
		for(int i=0; i<(numOfKBuckets); i++)
		{
			for(int j=0; j<this.kbucket.get(i).size();j++)
			{
				GeoKadPeerInfo peerInfo = this.kbucket.get(i).get(j);
		
				double distance = GeoKadDistance.distance(this.createPeerInfo(), peerInfo);
					
				boolean bucketFounded = false;
					
					//For each KBucket without the last one that is for all peers out of previous circumferences 
					for(int index=0;index<(numOfKBuckets); index++)
					{
						//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
						if((distance <= (double)(index)*rayDistance) && bucketFounded == false)
						{
							
							//Add the peer in the right bucket
							if(!localGeoBucketVector.get(index).contains(peerInfo))
								localGeoBucketVector.get(index).add(peerInfo);
							
							bucketFounded = true;
						
							break;
						}
					}
					
					//If the peer's distance is very high it will be added in the last available KBucket
					if(bucketFounded == false)
					{
						//Add new Peer in the last Bucket
						//if(!localGeoBucketVector.get(numOfKBuckets-1).contains(peerInfo)&& localGeoBucketVector.get(numOfKBuckets-1).size() < 20)
							//localGeoBucketVector.get(numOfKBuckets-1).add(peerInfo);
						//else
						//{
							try
							{
								GeoKadRemoveEvent nlk = (GeoKadRemoveEvent) new GeoKadRemoveEvent("node_lookup", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);

								nlk.setOneShot(true);
								nlk.setAssociatedNode((GeoKadPeer)Engine.getDefault().getNodeByKey(peerInfo.getKey()));
								nlk.setPeerInfo(this.createPeerInfo());
								Engine.getDefault().insertIntoEventsList(nlk);
							}
							catch(Exception e)
							{e.printStackTrace();}
						//}
					}

				}
		}
		
		//Create the list of KBuckets
		for (int i = 0; i < this.numOfKBuckets; ++i) {
			this.kbucket.set(i, localGeoBucketVector.get(i));
		}
		
	}

	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
	}
	
	public void scheduleMove(float triggeringTime) {
		
		//System.out.println("MOVING .... ");
		
		//Create a new move element
		try {
			
			/*
			//Random Time for a single peer move (50VT=3 min)
			int randomTime = 50;
			
			if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
				randomTime = 100;
				
			float delay = 25 + (float)Engine.getDefault().getSimulationRandom().nextInt(randomTime);
			*/
			float delay = 0;
			double distance = 0.0;
			
			if(old_longitude != longitude && old_latitude != latitude)
			{
				distance = GeoKadDistance.distance(old_longitude,old_latitude,longitude,latitude);
				
				//System.out.println("DISTANCE: " + distance);
				
				this.speed = (double)expRandom(Engine.getDefault().getSimulationRandom(), (float)this.original_speed);
				
				if(this.speed > MAX_SPEED)
					this.speed = MAX_SPEED;
				
				delay = (float)( ( (double)distance / (double)this.speed ) *60.0*16.6);
				
				//System.out.println(this.speed + " - " + delay);
			}
			else
			{
				int randomTime = 50;
				
				if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
					randomTime = 100;
					
				delay = 25 + (float)Engine.getDefault().getSimulationRandom().nextInt(randomTime);
			}
			
			
			//System.out.println(old_longitude+" "+old_latitude+" "+longitude+" "+latitude);
			//System.out.println(this.getKey() + " Delay: " + delay + " New VT: " + (triggeringTime+delay) + " Distance: " + distance + " Speed: " + this.speed);
			
			GeoKadMoveNodeEvent moveEvent = (GeoKadMoveNodeEvent) new GeoKadMoveNodeEvent("node_lookup", params, null).createInstance(triggeringTime + delay);
			moveEvent.setOneShot(true);
			moveEvent.setAssociatedNode(this);
			Engine.getDefault().insertIntoEventsList(moveEvent);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void checkNodeAvailability() {
		//		//Check if Peer List has a low number of peers
		int count = 0;
		for(int i=0; i<this.getKbucket().size()-1; i++)
			count += this.getKbucket().get(i).size();
		
		if(count < 20 )
		{
			GeoKadBootStrapPeer bootStrap = null;
			bootStrap = (GeoKadBootStrapPeer)Engine.getDefault().getNodeByKey(GeoKadBootStrapPeer.BOOTSTRAP_KEY);
			
			ArrayList<GeoKadPeerInfo> peerInfoList = bootStrap.getInitialPeerList(this.createPeerInfo());
			
			//System.out.println("Boot List: " + peerInfoList.size());
			
			/*
			if(peerInfoList.size() > 0)
				for(int index=0; index<peerInfoList.size();index++)
					this.insertPeer(peerInfoList.get(index));
					*/
			if(peerInfoList.size() > 0)	
				for(int index=0; index<peerInfoList.size();index++)
				{
					GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodeByKey(peerInfoList.get(index).getKey());
					this.insertPeer(peer.createPeerInfo());
				}
		}
		
		
	}
	
	public boolean containsPeerInGeoBuckets(GeoKadPeerInfo peer) {
		
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(peer);
			
			if( index != -1)
			{
				//Set new PeerInfo
				return true;
			}
		}
		
		return false;
	}
	
	public int indexOfGeoBucketFor(GeoKadPeerInfo testPeer) {
		
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(numOfKBuckets); i++)
		{
			//Find peer index
			int index = this.kbucket.get(i).indexOf(testPeer);
			
			if( index != -1)
			{
				//Set new PeerInfo
				return i;
			}
		}
		
		return -1;
	}
	
	public GeoKadPeerInfo createPeerInfo()
	{
		return new GeoKadPeerInfo(this.getKey(),this.getLatitude(),this.getLongitude(),this.getPeerCounter(),this.timeStamp);
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

	public ArrayList<GeoKadPeerInfo> getNlContactedNodes() {
		return nlContactedNodes;
	}

	public void setNlContactedNodes(ArrayList<GeoKadPeerInfo> nlContactedNodes) {
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

	public void setKbucket(Vector<ArrayList<GeoKadPeerInfo>> kbucket) {
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
	
	public int getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(int setMessages) {
		this.sentMessages = setMessages;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isGossipActive() {
		return isGossipActive;
	}

	public void setGossipActive(boolean isGossipActive) {
		this.isGossipActive = isGossipActive;
	}

	public ArrayList<GeoKadPeerInfo> getPeriodicPeerList() {
		return periodicPeerList;
	}

	public void setPeriodicPeerList(ArrayList<GeoKadPeerInfo> periodicPeerList) {
		this.periodicPeerList = periodicPeerList;
	}

	public double getOld_latitude() {
		return old_latitude;
	}

	public void setOld_latitude(double oldLatitude) {
		old_latitude = oldLatitude;
	}

	public double getOld_longitude() {
		return old_longitude;
	}

	public void setOld_longitude(double oldLongitude) {
		old_longitude = oldLongitude;
	}

	public int getPeerCounter() {
		int peerCounter = 0;
		
		for(int i=0; i<(numOfKBuckets); i++)
				peerCounter += this.kbucket.get(i).size();
		
		return peerCounter;
	}

	public int getPathIndex() {
		return pathIndex;
	}

	public void setPathIndex(int pathIndex) {
		this.pathIndex = pathIndex;
	}

	public int getDeltaValue() {
		return deltaValue;
	}

	public void setDeltaValue(int deltaValue) {
		this.deltaValue = deltaValue;
	}

	public boolean isPathDirectionForward() {
		return pathDirectionForward;
	}

	public void setPathDirectionForward(boolean pathDirectionForward) {
		this.pathDirectionForward = pathDirectionForward;
	}

	public static ArrayList<Double> getGraphKey() {
		return graphKey;
	}

	public static void setGraphKey(ArrayList<Double> graphKey) {
		GeoKadPeer.graphKey = graphKey;
	}

	public static String getGossip() {
		return GOSSIP;
	}

	public int getAvDiscoveryStepCounter() {
		return avDiscoveryStepCounter;
	}

	public void setAvDiscoveryStepCounter(int avDiscoveryStepCounter) {
		this.avDiscoveryStepCounter = avDiscoveryStepCounter;
	}

	public int getDiscoveryCounter() {
		return discoveryCounter;
	}

	public void setDiscoveryCounter(int discoveryCounter) {
		this.discoveryCounter = discoveryCounter;
	}

	public boolean isDiscoveryActive() {
		return isDiscoveryActive;
	}

	public void setDiscoveryActive(boolean isDiscoveryActive) {
		this.isDiscoveryActive = isDiscoveryActive;
	}

	public double getBase_latitude() {
		return base_latitude;
	}

	public void setBase_latitude(double baseLatitude) {
		base_latitude = baseLatitude;
	}

	public double getBase_longitude() {
		return base_longitude;
	}

	public void setBase_longitude(double baseLongitude) {
		base_longitude = baseLongitude;
	}

	public double getOriginal_speed() {
		return original_speed;
	}

	public void setOriginal_speed(double originalSpeed) {
		original_speed = originalSpeed;
	}

	public float getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void removePeer(GeoKadPeerInfo peerInfo) {
		
		//System.out.println("################################################## REMOVE !");
		
		for(int i=0; i<(numOfKBuckets); i++)
		{
			for(int j=0; j<this.kbucket.get(i).size();j++)
			{
				this.kbucket.get(i).remove(peerInfo);
			}
		}
		
	}

	public double getBootstrap_base_latitude() {
		return bootstrap_base_latitude;
	}

	public void setBootstrap_base_latitude(double bootstrapBaseLatitude) {
		bootstrap_base_latitude = bootstrapBaseLatitude;
	}

	public double getBootstrap_base_longitude() {
		return bootstrap_base_longitude;
	}

	public void setBootstrap_base_longitude(double bootstrapBaseLongitude) {
		bootstrap_base_longitude = bootstrapBaseLongitude;
	}

	public static double getMaxSpeed() {
		return MAX_SPEED;
	}

	public static int getLastGeoBucketSize() {
		return LAST_GEO_BUCKET_SIZE;
	}

}
