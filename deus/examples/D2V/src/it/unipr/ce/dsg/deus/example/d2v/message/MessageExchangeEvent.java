package it.unipr.ce.dsg.deus.example.d2v.message;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
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

	private TrafficInformationMessage msg = null;
	
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
			
			D2VPeer sender = (D2VPeer)Engine.getDefault().getNodeByKey(this.msg.getSenderNodeId());
			sender.addSentKbAmountForDissemination((double)(this.msg.getMessageHash().getBytes().length + this.msg.getPayload().length)/1000.0);
			
			//Check Traffic Information Knowledge of current peer
			currNode.checkTrafficInformationKnowledge(triggeringTime);
			
			//Store if necessary the incoming message
			if(!currNode.getTrafficInformationKnowledge().contains(this.msg))
			{	
				currNode.getTrafficInformationKnowledge().add(this.msg);
				currNode.distributeTrafficInformationMessageToAllPeers(this.msg,this.triggeringTime);
			}
			else
				currNode.incrementDuplicateReceivedMessages();
			
			/*
			//TrafficJamMessage
			if(msg.getType().equals(TrafficJamMessage.typeName))
			{
				TrafficJamMessage trafficMessage = (TrafficJamMessage)this.msg;
				
				//Store if necessary the incoming message
				if(!currNode.getTrafficInformationKnowledge().contains(trafficMessage))
				{	
					currNode.getTrafficInformationKnowledge().add(trafficMessage);
					currNode.distributeTrafficInformationMessage(trafficMessage,this.triggeringTime);
				}
				else
					currNode.incrementDuplicateReceivedMessages();
			}
			
			//RoadSurfaceConditionMessage
			if(msg.getType().equals(RoadSurfaceConditionMessage.typeName))
			{
				RoadSurfaceConditionMessage rcm = (RoadSurfaceConditionMessage)this.msg;
				
				if(!currNode.getTrafficInformationKnowledge().contains(rcm))
					currNode.getTrafficInformationKnowledge().add(rcm);
				else
					currNode.incrementDuplicateReceivedMessages();
			}
			*/
		}
		else
			System.err.println("VT:"+triggeringTime+" Message Exchange Event ---> NULL Message !!!");
	}

	public TrafficInformationMessage getMsg() {
		return msg;
	}

	public void setMsg(TrafficInformationMessage msg) {
		this.msg = msg;
	}

}
