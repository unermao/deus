package it.unipr.ce.dsg.deus.example.d2v;


import it.unipr.ce.dsg.deus.p2p.node.Peer;
import it.unipr.ce.dsg.deus.core.*;
import it.unipr.ce.dsg.deus.example.d2v.buckets.D2VGeoBuckets;
import it.unipr.ce.dsg.deus.example.d2v.discovery.SearchResultType;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPath;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPathIndex;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPathPoint;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStationController;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VPeer extends Peer {
	
	static public SwitchStationController ssc = null; 
	
	private static final String ALPHA = "alpha";
	private static final String K_VALUE = "k";
	private static final String BUCKET_NODE_LIMIT = "bucketNodeLimit";
	private static final String RADIUS_KM = "radiusKm";
	private static final String EPSILON = "epsilon";
	private static final String AVG_SPEED_MAX = "avgSpeedMax";
	private static final String DISCOVERY_MAX_PERIOD = "discoveryMaxPeriod";
	private static final String DISCOVERY_MIN_PERIOD = "discoveryMinPeriod";
	private static final String DISCOVER_PERIOD_PEER_LIMIT = "discoveryPeriodPeerLimit";
	private static final String DISCOVER_MAX_PEER_NUMBER = "discoveryMaxPeerNumber";
	
	private float discoveryMaxWait = 25;
	
	private ArrayList<Double> discoveryStatistics = new ArrayList<Double>();;
	
	private boolean isTrafficJam = false;
	
	private int alpha = 3;
	private int k = 10;
	private int bucketNodeLimit = 20;
	private double radiusKm = 1.5;
	private double epsilon = 1.5;
	private double avgSpeedMax = 30.0;
	private float discoveryMinPeriod = 25;
	private float discoveryMaxPeriod = 100;
	private int discoveryMaxPeerNumber = 100;
	
	private float discoveryPeriod = 25;
	
	private int sentFindNode = 0;
	private int findNodeLimit = 0;
	
	private SwitchStation ss = null;
	private CityPath cp = null;
	private CityPathIndex ci = null;
	private D2VPeerDescriptor peerDescriptor = null;
	private D2VTrafficElement trafficElement = null;

	public HashMap<Integer, SearchResultType> nlResults = new HashMap<Integer, SearchResultType>();
	public ArrayList<D2VPeerDescriptor> nlContactedNodes = new ArrayList<D2VPeerDescriptor>();
	
	//Number of sent messages
	private int sentMessages = 0;
	
	//Flag for active discovery
	private boolean isDiscoveryActive = false;
	
	//Counter of performed step for each discovery procedure
	private int avDiscoveryStepCounter = 0;
	private int discoveryCounter = 0;
	
	private boolean findNodeK = false;
	
	private GeoLocation oldSentPosition = null;
	
	private D2VGeoBuckets gb = null;

	private int discoveryPeriodPeerLimit = 20;
	
	public D2VPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		
		// Init the Switch Station Controller for Peer Mobility Model
		if(ssc == null)
		{
			ssc = new SwitchStationController("examples/D2V/SwitchStation_Parma.csv","examples/D2V/paths_result_mid_Parma.txt");
			ssc.readSwitchStationFile();
			ssc.readPathFile();
		}

		//Read value of parameter avgSpeed
		if (params.getProperty(AVG_SPEED_MAX) == null)
			throw new InvalidParamsException(AVG_SPEED_MAX
					+ " param is expected");
		try {
			avgSpeedMax = Double.parseDouble(params.getProperty(AVG_SPEED_MAX));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(AVG_SPEED_MAX
					+ " must be a valid double value.");
		}
		
		//Read value of parameter epsilon
		if (params.getProperty(EPSILON) == null)
			throw new InvalidParamsException(EPSILON
					+ " param is expected");
		try {
			epsilon = Double.parseDouble(params.getProperty(EPSILON));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(EPSILON
					+ " must be a valid double value.");
		}
		
		//Read value of parameter radius
		if (params.getProperty(RADIUS_KM) == null)
			throw new InvalidParamsException(RADIUS_KM
					+ " param is expected");
		try {
			radiusKm = Double.parseDouble(params.getProperty(RADIUS_KM));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(RADIUS_KM
					+ " must be a valid double value.");
		}
		
		//Read value of parameter bucketNodeLimit
		if (params.getProperty(BUCKET_NODE_LIMIT) == null)
			throw new InvalidParamsException(BUCKET_NODE_LIMIT
					+ " param is expected");
		try {
			bucketNodeLimit = Integer.parseInt(params.getProperty(BUCKET_NODE_LIMIT));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(BUCKET_NODE_LIMIT
					+ " must be a valid int value.");
		}
		
		//Read value of parameter Alpha
		if (params.getProperty(ALPHA) == null)
			throw new InvalidParamsException(ALPHA
					+ " param is expected");
		try {
			alpha = Integer.parseInt(params.getProperty(ALPHA));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(ALPHA
					+ " must be a valid int value.");
		}
		
		//Read value of parameter k
		if (params.getProperty(K_VALUE) == null)
			throw new InvalidParamsException(K_VALUE
					+ " param is expected");
		try {
			k = Integer.parseInt(params.getProperty(K_VALUE));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(K_VALUE
					+ " must be a valid int value.");
		}
		
		//Read value of parameter discoveryMinPeriod
		if (params.getProperty(DISCOVERY_MIN_PERIOD) == null)
			throw new InvalidParamsException(DISCOVERY_MIN_PERIOD
					+ " param is expected");
		try {
			discoveryMinPeriod = Float.parseFloat(params.getProperty(DISCOVERY_MIN_PERIOD));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(DISCOVERY_MIN_PERIOD
					+ " must be a valid float value.");
		}
		
		//Read value of parameter discoveryMaxPeriod
		if (params.getProperty(DISCOVERY_MAX_PERIOD) == null)
			throw new InvalidParamsException(DISCOVERY_MAX_PERIOD
					+ " param is expected");
		try {
			discoveryMaxPeriod = Float.parseFloat(params.getProperty(DISCOVERY_MAX_PERIOD));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(DISCOVERY_MAX_PERIOD
					+ " must be a valid float value.");
		}
		
		
		
		if (params.getProperty(DISCOVER_PERIOD_PEER_LIMIT) == null)
			throw new InvalidParamsException(DISCOVER_PERIOD_PEER_LIMIT
					+ " param is expected");
		try {
			discoveryPeriodPeerLimit  = Integer.parseInt(params.getProperty(DISCOVER_PERIOD_PEER_LIMIT));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(DISCOVER_PERIOD_PEER_LIMIT
					+ " must be a valid int value.");
		}
		
		if (params.getProperty(DISCOVER_MAX_PEER_NUMBER) == null)
			throw new InvalidParamsException(DISCOVER_MAX_PEER_NUMBER
					+ " param is expected");
		try {
			discoveryMaxPeerNumber = Integer.parseInt(params.getProperty(DISCOVER_MAX_PEER_NUMBER));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(DISCOVER_MAX_PEER_NUMBER
					+ " must be a valid int value.");
		}
		
		System.out.println("D2VPeer Created !");
			
	}

	public Object clone() {
		
		D2VPeer clone = (D2VPeer) super.clone();	
		
		clone.isTrafficJam = false;
		
		clone.gb = new D2VGeoBuckets(this.getK(), this.getRadiusKm());
		clone.nlResults = new HashMap<Integer, SearchResultType>();
		clone.nlContactedNodes = new ArrayList<D2VPeerDescriptor>();
		clone.discoveryStatistics = new ArrayList<Double>();
		clone.sentFindNode = 0;
		clone.avDiscoveryStepCounter = 0;
		clone.discoveryCounter = 0;
		clone.findNodeK = false;
		clone.findNodeLimit = 0;
		
		clone.discoveryPeriod = clone.discoveryMinPeriod;
		
		return clone;
	}

	public void init(float triggeringTime)
	{
		//Select Randomly a starting Switch Station
		int ssIndex = Engine.getDefault().getSimulationRandom().nextInt(ssc.getSwitchStationList().size());
		this.ss = ssc.getSwitchStationList().get(ssIndex);	
		
		//Create Peer Descriptor
		this.peerDescriptor = new D2VPeerDescriptor(this.ss,this.key);
		this.peerDescriptor.setTimeStamp(Engine.getDefault().getVirtualTime());
		
		//Select a path from its starting switch station
		ArrayList<CityPath> availablePaths = ssc.getPathListFromSwithStation(this.ss);
		
		//Pick Up a random path among available
		int pathIndex = Engine.getDefault().getSimulationRandom().nextInt(availablePaths.size());
		this.cp = availablePaths.get(pathIndex);
		this.ci = new CityPathIndex(0, this.cp.getPathPoints().size());
		this.peerDescriptor.setGeoLocation(this.cp.getStartPoint());
			
		//Schedule the first movement
		this.scheduleMove(triggeringTime);
	}
	
	/**
	 * 
	 */
	public void broadcastUpdatePositionMessage(float triggeringTime)
	{
		if(oldSentPosition == null || GeoDistance.distance(this.cp.getPathPoints().get(this.ci.getIndex()), oldSentPosition) > this.epsilon)
		{

			oldSentPosition = this.cp.getPathPoints().get(this.ci.getIndex());
			
			//Sending Update position messages
			for(int i=0; i < (this.gb.getBucket().size()) ; i++)
			{
				for(int k=0; k <  this.gb.getBucket().get(i).size(); k++)
				{
					try
					{
						D2VUpdatePositionEvent nlk = (D2VUpdatePositionEvent) new D2VUpdatePositionEvent("node_lookup", params, null).createInstance(triggeringTime+1);

						D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(this.gb.getBucket().get(i).get(k).getKey());
						
						nlk.setOneShot(true);
						nlk.setAssociatedNode(peer);
						nlk.setPeerInfo(this.createPeerInfo());
						Engine.getDefault().insertIntoEventsList(nlk);
					}
					catch(Exception e)
					{e.printStackTrace();}
				}
			}
		 }
	}
	
	/**
	 * Move the node to a new position according to his path
	 */
	public void move(float triggeringTime) {			
		
		//Move to next position among CityPath
		this.ci.next();
		
		//If there isn't other point on the path pick up a new one
		//System.out.println("Peer:"+this.key+" City Path Index:"+this.ci.getIndex()+" Max:"+this.cp.getPathPoints().size());
		if(!this.ci.hasNextStep())
		{	
			//System.out.println("Peer:"+this.key+" changing switch station !");
	
			//Actual Switch Station is the last point of the path
			SwitchStation actualSS = new SwitchStation(this.cp.getEndPoint().getLatitude(), this.cp.getEndPoint().getLongitude(), this.cp.getEndPoint().getTimeStamp());
			
			//Select a path from its starting switch station
			ArrayList<CityPath> availablePaths = ssc.getPathListFromSwithStation(actualSS);
			
			//Pick Up a random path among available
			int pathIndex = Engine.getDefault().getSimulationRandom().nextInt(availablePaths.size());
			this.cp = availablePaths.get(pathIndex);
			this.ci = new CityPathIndex(0, this.cp.getPathPoints().size());
			this.peerDescriptor.setGeoLocation(this.cp.getStartPoint());
			
		}
		else{
			//System.out.println("Peer:"+this.key+" changing position !");
			this.peerDescriptor.setGeoLocation(this.cp.getPathPoints().get(this.ci.getIndex()));
			this.checkTrafficJam(triggeringTime);
		}
		
		this.updateBucketInfo(peerDescriptor);
		this.broadcastUpdatePositionMessage(triggeringTime);
		
		if(this.isTrafficJam == false)
			this.scheduleMove(triggeringTime);
	
	}

	/**
	 * 
	 * @return
	 */
	public D2VPeerDescriptor createPeerInfo()
	{
		GeoLocation gl = new GeoLocation(this.peerDescriptor.getGeoLocation().getLatitude(), this.peerDescriptor.getGeoLocation().getLongitude(), this.peerDescriptor.getGeoLocation().getTimeStamp());
		return new D2VPeerDescriptor(gl,this.key,Engine.getDefault().getVirtualTime());
	}
	
	/**
	 * 
	 * @param triggeringTime
	 */
	public void scheduleMove(float triggeringTime) {
		
		try 
		{
			
			float delay = 0;
			double distance = 0.0;
		
    		GeoLocation nextStep = this.cp.getPathPoints().get(this.ci.getIndex()+1);
			
			distance = GeoDistance.distance(this.peerDescriptor.getGeoLocation().getLongitude(),this.peerDescriptor.getGeoLocation().getLatitude(),nextStep.getLongitude(),nextStep.getLatitude());
			
			double speed = (double)expRandom(Engine.getDefault().getSimulationRandom(), (float)this.avgSpeedMax);
			
			delay = (float)( ( (double)distance / (double)speed ) *60.0*16.6);
			
			if(!(delay>0) && !(delay==0) && !(delay<0))
				delay = 0;
				
			//System.out.println("Distance:"+distance+" Delay:"+delay+" Speed:"+speed);
			
			D2VMoveNodeEvent moveEvent = (D2VMoveNodeEvent) new D2VMoveNodeEvent("node_move_event", params, null).createInstance(triggeringTime + delay);
			moveEvent.setOneShot(true);
			moveEvent.setAssociatedNode(this);
			Engine.getDefault().insertIntoEventsList(moveEvent);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * @param triggeringTime
	 */
	public void checkTrafficJam(float triggeringTime)
	{
		CityPathPoint nextCpPoint = this.cp.getPathPoints().get(this.ci.getIndex()+1);
			
		//System.out.println("Peer:"+this.key+" Check Traffic Jam ...");
		
		if(nextCpPoint.getTe() != null)
		{
			this.isTrafficJam = true;
			nextCpPoint.getTe().getNodeKeysInTrafficJam().add(this.key);
			this.cp.getPathPoints().get(this.ci.getIndex()).setTe(nextCpPoint.getTe());
			
			for(int i=0; i<D2VPeer.ssc.getPathList().size();i++)
			{
				CityPath path = D2VPeer.ssc.getPathList().get(i);
				int index = path.getPathPoints().indexOf(this.cp.getPathPoints().get(this.ci.getIndex()));
				if(index != -1)
				{
					CityPathPoint p = path.getPathPoints().get(index);
					p.setTe(nextCpPoint.getTe());
				}
			}
		}		
	}
	
	public void exitTrafficJamStatus(float triggeringTime)
	{
		this.isTrafficJam = false;
		this.cp.getPathPoints().get(this.ci.getIndex()).setTe(null);
		
		/*
		for(int i=0; i<D2VPeer.ssc.getPathList().size();i++)
		{
			CityPath path = D2VPeer.ssc.getPathList().get(i);
			int index = path.getPathPoints().indexOf(this.cp.getPathPoints().get(this.ci.getIndex()));
			if(index != -1)
			{
				CityPathPoint p = path.getPathPoints().get(index);
				p.setTe(null);
			}
		}
		*/
		
		this.scheduleMove(triggeringTime);
	}
	
	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
	}
	
	/**
	 * 
	 * @param d2vPeerDescriptor
	 */
	public boolean insertPeer(String from,D2VPeerDescriptor newPeer) {
		
		if(this.getKey() != newPeer.getKey())
		{
			if(!this.neighbors.contains((Peer) Engine.getDefault().getNodeByKey(newPeer.getKey())))
				this.addNeighbor( (Peer) Engine.getDefault().getNodeByKey(newPeer.getKey()));
			
			//System.out.println("############################################ INSERT FROM :  " + from);
			return this.gb.insertPeer(params,this.createPeerInfo(), newPeer);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param triggeringTime
	 */
	public void scheduleDiscovery(float triggeringTime) {
		try {
		
			//At the beginning of a new discovery evaluate and save information about percentage of missing nodes
			/*
			if(this.discoveryStatistics.size() < 100)
			{	
				double perMissing = this.getGb().evaluatePerMissingNodes(this.createPeerInfo());
				this.discoveryStatistics.add(perMissing);
			}
			*/
			
			D2VDiscoveryEvent event = (D2VDiscoveryEvent) new D2VDiscoveryEvent("discovery", params, null).createInstance(triggeringTime+this.discoveryPeriod);
			event.setOneShot(true);
			event.setAssociatedNode(this);
			Engine.getDefault().insertIntoEventsList(event);
		
		} catch (InvalidParamsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateBucketInfo(D2VPeerDescriptor peerDescriptor2) {
		this.gb.updateBucketInfo(params,peerDescriptor2);	
	}

	
	public static SwitchStationController getSsc() {
		return ssc;
	}

	public static void setSsc(SwitchStationController ssc) {
		D2VPeer.ssc = ssc;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getBucketNodeLimit() {
		return bucketNodeLimit;
	}

	public void setBucketNodeLimit(int bucketNodeLimit) {
		this.bucketNodeLimit = bucketNodeLimit;
	}

	public double getRadiusKm() {
		return radiusKm;
	}

	public void setRadiusKm(double radiusKm) {
		this.radiusKm = radiusKm;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double getAvgSpeedMax() {
		return avgSpeedMax;
	}

	public void setAvgSpeed(double avgSpeedMax) {
		this.avgSpeedMax = avgSpeedMax;
	}

	public SwitchStation getSs() {
		return ss;
	}

	public void setSs(SwitchStation ss) {
		this.ss = ss;
	}

	public D2VPeerDescriptor getPeerDescriptor() {
		//return peerDescriptor;
		return this.createPeerInfo();
	}

	public void setPeerDescriptor(D2VPeerDescriptor peerDescriptor) {
		this.peerDescriptor = peerDescriptor;
	}

	public void setAvgSpeedMax(double avgSpeedMax) {
		this.avgSpeedMax = avgSpeedMax;
	}

	public CityPath getCp() {
		return cp;
	}

	public void setCp(CityPath cp) {
		this.cp = cp;
	}

	public boolean isTrafficJam() {
		return isTrafficJam;
	}

	public void setTrafficJam(boolean isTrafficJam) {
		this.isTrafficJam = isTrafficJam;
	}

	public CityPathIndex getCi() {
		return ci;
	}

	public void setCi(CityPathIndex ci) {
		this.ci = ci;
	}

	public D2VTrafficElement getTrafficElement() {
		return trafficElement;
	}

	public void setTrafficElement(D2VTrafficElement trafficElement) {
		this.trafficElement = trafficElement;
	}

	public int getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(int sentMessages) {
		this.sentMessages = sentMessages;
	}

	public HashMap<Integer, SearchResultType> getNlResults() {
		return nlResults;
	}

	public void setNlResults(HashMap<Integer, SearchResultType> nlResults) {
		this.nlResults = nlResults;
	}

	public ArrayList<D2VPeerDescriptor> getNlContactedNodes() {
		return nlContactedNodes;
	}

	public void setNlContactedNodes(ArrayList<D2VPeerDescriptor> nlContactedNodes) {
		this.nlContactedNodes = nlContactedNodes;
	}

	public D2VGeoBuckets getGb() {
		return gb;
	}

	public void setGb(D2VGeoBuckets gb) {
		this.gb = gb;
	}

	public boolean isDiscoveryActive() {
		return isDiscoveryActive;
	}

	public void setDiscoveryActive(boolean isDiscoveryActive) {
		this.isDiscoveryActive = isDiscoveryActive;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
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

	public GeoLocation getOldSentPosition() {
		return oldSentPosition;
	}

	public void setOldSentPosition(GeoLocation oldSentPosition) {
		this.oldSentPosition = oldSentPosition;
	}

	public ArrayList<Double> getDiscoveryStatistics() {
		return discoveryStatistics;
	}

	public void setDiscoveryStatistics(ArrayList<Double> discoveryStatistics) {
		this.discoveryStatistics = discoveryStatistics;
	}

	public int getSentFindNode() {
		return sentFindNode;
	}

	public void setSentFindNode(int sentFindNode) {
		this.sentFindNode = sentFindNode;
	}

	public boolean isFindNodeK() {
		return findNodeK;
	}

	public void setFindNodeK(boolean findNodeK) {
		this.findNodeK = findNodeK;
	}

	public int getFindNodeLimit() {
		return findNodeLimit;
	}

	public void setFindNodeLimit(int findNodeLimit) {
		this.findNodeLimit = findNodeLimit;
	}

	public float getDiscoveryMinPeriod() {
		return discoveryMinPeriod;
	}

	public void setDiscoveryMinPeriod(float discoveryMinPeriod) {
		this.discoveryMinPeriod = discoveryMinPeriod;
	}

	public float getDiscoveryMaxPeriod() {
		return discoveryMaxPeriod;
	}

	public void setDiscoveryMaxPeriod(float discoveryMaxPeriod) {
		this.discoveryMaxPeriod = discoveryMaxPeriod;
	}

	public float getDiscoveryPeriod() {
		return discoveryPeriod;
	}

	public void setDiscoveryPeriod(float discoveryPeriod) {
		this.discoveryPeriod = discoveryPeriod;
	}

	public int getDiscoveryMaxPeerNumber() {
		return discoveryMaxPeerNumber;
	}

	public void setDiscoveryMaxPeerNumber(int discoveryMaxPeerNumber) {
		this.discoveryMaxPeerNumber = discoveryMaxPeerNumber;
	}

	public int getDiscoveryPeriodPeerLimit() {
		return discoveryPeriodPeerLimit;
	}

	public void setDiscoveryPeriodPeerLimit(int discoveryPeriodPeerLimit) {
		this.discoveryPeriodPeerLimit = discoveryPeriodPeerLimit;
	}
}
