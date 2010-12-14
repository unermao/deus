package it.unipr.ce.dsg.example.mobilityexample;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.mobility.FTMSpeedModel;
import it.unipr.ce.dsg.mobility.MobilePeer;

/**
 * 
 * @author Marco Picone (picone@ce.unipr.it)
 *
 */
public class CarPeer extends MobilePeer{

	private FTMSpeedModel ftmModel = null;
	
	public FTMSpeedModel getFtmModel() {
		return ftmModel;
	}

	public void setFtmModel(FTMSpeedModel ftmModel) {
		this.ftmModel = ftmModel;
	}

	public CarPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		
		System.out.println("CarPeer Created !");
	}
	
	public Object clone() {
		CarPeer clone = (CarPeer) super.clone();
		clone.ftmModel = new FTMSpeedModel();
		return clone;
	}

	@Override
	public void configureMobilityModelParameter() {
		this.ftmModel.setCarMaxSpeed(this.getMobilityPath().getSpeedLimit());
		this.ftmModel.setCarMinSpeed(10.0);
		this.ftmModel.setNumCarsInPath(this.getMobilityPath().getNumOfCars());
		this.ftmModel.setPathLenght(this.getMobilityPath().getLenght());
	}

	@Override
	public double sec2VT(double secValue) {
		return secValue*0.27776;
	}

	@Override
	public double vt2Sec(double vtValue) {
		return vtValue*(1.0/0.27776);
	}

}
