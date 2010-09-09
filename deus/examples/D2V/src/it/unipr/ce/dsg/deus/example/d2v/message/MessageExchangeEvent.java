package it.unipr.ce.dsg.deus.example.d2v.message;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.D2VPeer;

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
		
		D2VPeer connectingNode = (D2VPeer) this.getAssociatedNode();
		
		if(this.msg != null)
		{
			System.out.println("VT:"+triggeringTime+" Message Exchange Event ---> From: " + msg.getSenderNodeId() + " To: " + msg.getDestinationNodeId() +"("+connectingNode.getKey()+")");
			System.out.println("VT:"+triggeringTime+" Message Exchange Event ---> Type: " + msg.getType());
		}
		else
			System.out.println("VT:"+triggeringTime+" Message Exchange Event ---> NULL Message !!!");
	}

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}

}
