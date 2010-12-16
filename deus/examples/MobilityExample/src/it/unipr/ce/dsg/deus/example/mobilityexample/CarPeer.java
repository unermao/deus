package it.unipr.ce.dsg.deus.example.mobilityexample;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.mobility.model.FTMSpeedModel;
import it.unipr.ce.dsg.deus.mobility.node.MobilePeer;

/**
 * 
 * @author Marco Picone (picone@ce.unipr.it)
 *
 */
public class CarPeer extends MobilePeer{

	private static final String AVG_DOWNLINK = "avgDownlink";
	private static final String AVG_UPLINK = "avgUplink";
	private FTMSpeedModel ftmModel = null;
	private double avgDownlink = 0.0;
	private double avgUplink = 0.0;
	
	private double sentByte = 0.0;

	public CarPeer(String id, Properties params, ArrayList<Resource> resources)
	throws InvalidParamsException {
		super(id, params, resources);	
		
		if (params.getProperty(AVG_DOWNLINK) == null)
			throw new InvalidParamsException(AVG_DOWNLINK
					+ " param is expected");
		try {
			
			this.avgDownlink  = Double.parseDouble(params.getProperty(AVG_DOWNLINK));
			
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(AVG_DOWNLINK
					+ " must be a valid double value.");
		}
		System.out.println("CarPeer avgDownlink ?: " +  this.avgDownlink);
		
		if (params.getProperty(AVG_UPLINK) == null)
			throw new InvalidParamsException(AVG_UPLINK
					+ " param is expected");
		try {
			
			this.avgUplink  = Double.parseDouble(params.getProperty(AVG_UPLINK));
			
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(AVG_UPLINK
					+ " must be a valid double value.");
		}
		System.out.println("CarPeer avgUplink ?: " +  this.avgUplink);

		
		System.out.println("CarPeer Created !");
	}
	
	public Object clone() {
		CarPeer clone = (CarPeer) super.clone();
		clone.ftmModel = new FTMSpeedModel();
		return clone;
	}

	
	public FTMSpeedModel getFtmModel() {
		return ftmModel;
	}

	public void setFtmModel(FTMSpeedModel ftmModel) {
		this.ftmModel = ftmModel;
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
            		//Evaluate transmission speed in Kbit/sec
            		double senderUplink = this.expRandom(this.avgUplink);
            		double receiverDownlink = this.expRandom(destPeer.getAvgDownlink());
            		double speed = Math.min(senderUplink, receiverDownlink);
           
            		//Increase sent Byte
            		this.sentByte += message.getMessageSize();
            		
            		//Convert msg size from Byte to Kbit
            		double msgKbitSize = (message.getMessageSize()*8.0)/100.0;
            		
            		//Evaluate delay in VT of transmission time
            		double delay =  this.sec2VT(msgKbitSize / speed);
                    
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

	@Override
	public void moved(float triggeringTime) {
		
		if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
		{	
			String messageText = "PING_MESSAGE#"+this.key;
			MyMessage msg = new MyMessage(this.key,messageText.getBytes(),0,Engine.getDefault().getVirtualTime());
		
			ArrayList<Integer> carPeerList = Engine.getDefault().getNodeKeysById("CarPeer");
			int destKeyIndex = Engine.getDefault().getSimulationRandom().nextInt(carPeerList.size());
			CarPeer destPeer = (CarPeer)Engine.getDefault().getNodeByKey(carPeerList.get(destKeyIndex));
			this.sendMessage(destPeer, msg, Engine.getDefault().getVirtualTime());
		}
		
	}

	/**
	 * 
	 * @param meanValue
	 * @return
	 */
	private double expRandom(double meanValue) {
		double myRandom = (double) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextDouble()) * meanValue);
		return myRandom;
	}
	
	public double getAvgDownlink() {
		return avgDownlink;
	}

	public void setAvgDownlink(double avgDownlink) {
		this.avgDownlink = avgDownlink;
	}

	public double getAvgUplink() {
		return avgUplink;
	}

	public void setAvgUplink(double avgUplink) {
		this.avgUplink = avgUplink;
	}

	public double getSentByte() {
		return sentByte;
	}

	public void setSentByte(double sentByte) {
		this.sentByte = sentByte;
	}


}
