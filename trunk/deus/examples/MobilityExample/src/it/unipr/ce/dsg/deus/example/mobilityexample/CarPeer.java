package it.unipr.ce.dsg.deus.example.mobilityexample;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.mobility.FTMSpeedModel;
import it.unipr.ce.dsg.deus.mobility.MobilePeer;

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
		
		String messageText = "PING_MESSAGE#"+this.key;
		MyMessage msg = new MyMessage(this.key,messageText.getBytes(),0,Engine.getDefault().getVirtualTime());
	
		ArrayList<Integer> carPeerList = Engine.getDefault().getNodeKeysById("CarPeer");
		int destKeyIndex = Engine.getDefault().getSimulationRandom().nextInt(carPeerList.size());
		CarPeer destPeer = (CarPeer)Engine.getDefault().getNodeByKey(carPeerList.get(destKeyIndex));
		this.sendMessage(destPeer, msg, Engine.getDefault().getVirtualTime());
		
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
	
	/**
     * Send a Message to a destination peer 
     * 
     * @param destPeer
     * @param message
     * @param triggeringTime
     */
    public void sendMessage(CarPeer destPeer, MyMessage message, float triggeringTime)
    {
            try {
            		double delay = Engine.getDefault().getSimulationRandom().nextDouble()*5.0;
                    MessageExchangeEvent event = (MessageExchangeEvent) new MessageExchangeEvent("message_exchange", params, null).createInstance((float) (triggeringTime+delay));
                    event.setOneShot(true);
                    event.setAssociatedNode(destPeer);
                    event.setMsg(message);
                    Engine.getDefault().insertIntoEventsList(event);
            } catch (InvalidParamsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
    }


}
