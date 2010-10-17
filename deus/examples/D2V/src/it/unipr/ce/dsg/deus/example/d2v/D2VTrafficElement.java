package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.core.*;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPath;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPathPoint;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStation;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStationController;

import java.util.ArrayList;
import java.util.Properties;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VTrafficElement extends Node {
	
	private static final String PERIOD = "period";
	private static final String JAM_PERIOD = "jamPeriod";
	private static final String TYPE = "type";
		
	private float startTime = 0;
	
	private double period = 0.0;
	private double jamPeriod = 0.0;
	private String type = null;
	
	private GeoLocation location = null;
	
	private ArrayList<Integer> nodeKeysInTrafficJam = new ArrayList<Integer>();
	private CityPath path;
	private CityPathPoint point;
	
	public D2VTrafficElement(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		
		//Read value of parameter avgSpeed
		if (params.getProperty(PERIOD) == null)
			throw new InvalidParamsException(PERIOD
					+ " param is expected");
		try {
			period = Double.parseDouble(params.getProperty(PERIOD));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(PERIOD
					+ " must be a valid double value.");
		}
		
		//Read value of parameter epsilon
		if (params.getProperty(JAM_PERIOD) == null)
			throw new InvalidParamsException(JAM_PERIOD
					+ " param is expected");
		try {
			jamPeriod = Double.parseDouble(params.getProperty(JAM_PERIOD));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(JAM_PERIOD
					+ " must be a valid double value.");
		}
		
		//Read value of parameter radius
		if (params.getProperty(TYPE) == null)
			throw new InvalidParamsException(TYPE
					+ " param is expected");
		try {
			type = params.getProperty(TYPE);
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(TYPE
					+ " must be a valid double value.");
		}
			
		System.out.println("D2VTrafficElement D2VTrafficElement() !");
		
	}

	public Object clone() {
		//System.out.println("D2VTrafficElement clone() !");
		D2VTrafficElement clone = (D2VTrafficElement) super.clone();	
		clone.nodeKeysInTrafficJam = new ArrayList<Integer>();
		return clone;
	}

	@Override
	public void initialize() throws InvalidParamsException {
	
		int ssIndex = Engine.getDefault().getSimulationRandom().nextInt(D2VPeer.ssc.getSwitchStationList().size());
		SwitchStation ss = D2VPeer.ssc.getSwitchStationList().get(ssIndex);	
		
		//Select a path from its starting switch station
		ArrayList<CityPath> availablePaths = D2VPeer.ssc.getPathListFromSwithStation(ss);
		
		//Pick Up a random path among available
		boolean pathFounded = false;
		
		while(pathFounded == false)
		{
			int pathIndex = Engine.getDefault().getSimulationRandom().nextInt(availablePaths.size());
			
			if(availablePaths.get(pathIndex).isHasTrafficJam()==false)
			{	
				this.path = availablePaths.get(pathIndex);
				this.path.setHasTrafficJam(true);
				
				int locationIndex = Engine.getDefault().getSimulationRandom().nextInt(path.getPathPoints().size());
				
				if(locationIndex == 0)
					locationIndex += 1 + Engine.getDefault().getSimulationRandom().nextInt(path.getPathPoints().size())/2;
				
				//PickUp a random point
				this.point = path.getPathPoints().get(locationIndex);
				//Set TrafficJam true in the CityPathPoint
				this.point.setTe(this);
				//Set the point for the traffic element
				this.location = point;
				
				/*
				for(int i=0; i<D2VPeer.ssc.getPathList().size();i++)
				{
					CityPath path = D2VPeer.ssc.getPathList().get(i);
					int index = path.getPathPoints().indexOf(this.point);
					if(index != -1)
					{
						CityPathPoint p = path.getPathPoints().get(index);
						path.setHasTrafficJam(true);
						p.setTe(this);
					}
				}
				*/
				
				pathFounded = true;
			}
		}
	}

	public void exitTrafficJamStatus(float time) {
		
		this.getPath().setHasTrafficJam(false);
		this.point.setTe(null);
		
		for(int i=0; i<D2VPeer.ssc.getPathList().size();i++)
		{
			CityPath path = D2VPeer.ssc.getPathList().get(i);
			int index = path.getPathPoints().indexOf(this.point);
			if(index != -1)
			{
				CityPathPoint p = path.getPathPoints().get(index);
				path.setHasTrafficJam(false);
				p.setTe(null);
			}
		}

		
		for(int i=0; i<nodeKeysInTrafficJam.size();i++)
		{
			D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(nodeKeysInTrafficJam.get(i));
			peer.exitTrafficJamStatus(time);
		}
		
		this.nodeKeysInTrafficJam.clear();
		Engine.getDefault().removeNode(this);
	}

	public void addCarInTrafficJam(int key_value) {
		if(!this.nodeKeysInTrafficJam.contains(key_value))
			this.nodeKeysInTrafficJam.add(key_value);
	}
	
	
	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public double getJamPeriod() {
		return jamPeriod;
	}

	public void setJamPeriod(double jamPeriod) {
		this.jamPeriod = jamPeriod;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public float getStartTime() {
		return startTime;
	}

	public void setStartTime(float startTime) {
		this.startTime = startTime;
	}

	public ArrayList<Integer> getNodeKeysInTrafficJam() {
		return nodeKeysInTrafficJam;
	}

	public void setNodeKeysInTrafficJam(ArrayList<Integer> nodeKeysInTrafficJam) {
		this.nodeKeysInTrafficJam = nodeKeysInTrafficJam;
	}

	public CityPath getPath() {
		return path;
	}

	public void setPath(CityPath path) {
		this.path = path;
	}

}
