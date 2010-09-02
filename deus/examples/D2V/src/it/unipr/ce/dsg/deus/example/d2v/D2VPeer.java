package it.unipr.ce.dsg.deus.example.d2v;


import it.unipr.ce.dsg.deus.p2p.node.Peer;
import it.unipr.ce.dsg.deus.core.*;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPath;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStationController;
import it.unipr.ce.dsg.example.d2v.util.GeoDistance;

import java.util.ArrayList;
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
	
	private int alpha = 3;
	private int k = 10;
	private int bucketNodeLimit = 20;
	private double radiusKm = 1.5;
	private double epsilon = 1.5;
	private double avgSpeedMax = 30.0;
	
	private SwitchStation ss = null;
	private CityPath cp = null;
	private D2VPeerDescriptor peerDescriptor = null;

	
	public D2VPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		
		// Init the Switch Station Controller for Peer Mobility Model
		if(ssc == null)
		{
			ssc = new SwitchStationController("examples/D2V/SwitchStation_Parma.csv","examples/D2V/paths_result_Parma.txt");
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
		
		System.out.println("D2VPeer Created !");
			
	}

	public Object clone() {
		
		D2VPeer clone = (D2VPeer) super.clone();	
		return clone;
	}

	public void init(float triggeringTime)
	{
		//System.out.println("Init Peer:"+this.key);
		
		//Select Randomly a starting Switch Station
		int ssIndex = Engine.getDefault().getSimulationRandom().nextInt(ssc.getSwitchStationList().size());
		this.ss = ssc.getSwitchStationList().get(ssIndex);	
		
		//Create Peer Descriptor
		this.peerDescriptor = new D2VPeerDescriptor(this.ss,this.key);
		
		//Select a path from its starting switch station
		ArrayList<CityPath> availablePaths = ssc.getPathListFromSwithStation(this.ss);
		
		//Pick Up a random path among available
		int pathIndex = Engine.getDefault().getSimulationRandom().nextInt(availablePaths.size());
		this.cp = new CityPath(availablePaths.get(pathIndex));
		this.peerDescriptor.setGeoLocation(this.cp.getStartPoint());
		
		//System.out.println("Peer:"+this.key+" Starting Position:"+this.peerDescriptor.getGeoLocation().getLatitude()+","+this.peerDescriptor.getGeoLocation().getLongitude());
		
		//Schedule the first movement
		this.scheduleMove(triggeringTime);
	}
	
	/**
	 * Move the node to a new position according to his path
	 */
	public void move(float triggeringTime) {			
		
		//If there isn't other point on the path pick up a new one
		//System.out.println("Peer:"+this.key+" City Path Index:"+this.cp.getIndex()+" Max:"+this.cp.getPathPoints().size());
		if(this.cp.nextStep() == 0)
		{	
			//System.out.println("Peer:"+this.key+" changing switch station !");
			
			//Actual Switch Station is the last point of the path
			SwitchStation actualSS = new SwitchStation(this.cp.getEndPoint().getLatitude(), this.cp.getEndPoint().getLongitude(), this.cp.getEndPoint().getTimeStamp());
			
			//Select a path from its starting switch station
			ArrayList<CityPath> availablePaths = ssc.getPathListFromSwithStation(actualSS);
			
			//Pick Up a random path among available
			int pathIndex = Engine.getDefault().getSimulationRandom().nextInt(availablePaths.size());
			this.cp = new CityPath(availablePaths.get(pathIndex));
			this.peerDescriptor.setGeoLocation(this.cp.getStartPoint());
			
		}
		else{
			//System.out.println("Peer:"+this.key+" changing position !");
			this.peerDescriptor.setGeoLocation(this.cp.getCurrentLocation());
		}
		
		this.scheduleMove(triggeringTime);
	
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
		
    		GeoLocation nextStep = this.cp.getPathPoints().get(this.cp.getIndex()+1);
			
			distance = GeoDistance.distance(this.peerDescriptor.getGeoLocation().getLongitude(),this.peerDescriptor.getGeoLocation().getLatitude(),nextStep.getLongitude(),nextStep.getLatitude());
			
			double speed = (double)expRandom(Engine.getDefault().getSimulationRandom(), (float)this.avgSpeedMax);
			
			delay = (float)( ( (double)distance / (double)speed ) *60.0*16.6);
			
			if(!(delay>0) && !(delay==0) && !(delay<0))
				delay = 0;
				
			System.out.println("Distance:"+distance+" Delay:"+delay+" Speed:"+speed);
			
			D2VMoveNodeEvent moveEvent = (D2VMoveNodeEvent) new D2VMoveNodeEvent("node_move_event", params, null).createInstance(triggeringTime + delay);
			moveEvent.setOneShot(true);
			moveEvent.setAssociatedNode(this);
			Engine.getDefault().insertIntoEventsList(moveEvent);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	
	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
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
		return peerDescriptor;
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


}
