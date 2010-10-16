package it.unipr.ce.dsg.deus.example.d2v.message;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.D2VPeer;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class MessageExchangeEvent extends NodeEvent {

	private Message msg = null;
	
	public MessageExchangeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		D2VPeer currNode = (D2VPeer) this.getAssociatedNode();
		
		if(this.msg != null)
		{
			//System.out.println("VT:"+triggeringTime+" Message Exchange Event ---> From: " + msg.getSenderNodeId() + " To: " + currNode.getKey());
			//System.out.println("VT:"+triggeringTime+" Message Exchange Event ---> Type: " + msg.getType());
		
			if(msg.getType().equals(TrafficJamMessage.typeName))
			{
				//System.out.println("VT:"+triggeringTime+" EVALUATING MESSAGE: "+TrafficJamMessage.typeName);
				TrafficJamMessage trafficMessage = (TrafficJamMessage)this.msg;
				currNode.distributeTrafficaJamMessage(trafficMessage,this.triggeringTime);
				
				if(!currNode.getIncomingMessageHistory().contains(trafficMessage))
					currNode.getIncomingMessageHistory().add(trafficMessage);
				
				//Check validity of traffic message
				double distance = GeoDistance.distance(trafficMessage.getLocation(), currNode.getPeerDescriptor().getGeoLocation());
				
				if(distance<=trafficMessage.getRange() && currNode.getCp().getPathPoints().contains(trafficMessage.getLocation()))
				{
					currNode.changeMovingDirection(triggeringTime);
				}
			}
		}
		else
			System.err.println("VT:"+triggeringTime+" Message Exchange Event ---> NULL Message !!!");
	}

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}

}
