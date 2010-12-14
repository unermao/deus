package it.unipr.ce.dsg.deus.example.mobilityexample;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class MessageExchangeEvent extends NodeEvent{

	private MyMessage msg = null;
	
	public MessageExchangeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		System.out.println("Peer: " + this.associatedNode.getKey() + " Received Payload:" + new String(this.msg.getPayload()));
	}

	public MyMessage getMsg() {
		return msg;
	}

	public void setMsg(MyMessage msg) {
		this.msg = msg;
	}

}
