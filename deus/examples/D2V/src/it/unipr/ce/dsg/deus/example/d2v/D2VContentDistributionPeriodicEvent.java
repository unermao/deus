package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.message.TrafficJamMessage;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VContentDistributionPeriodicEvent extends NodeEvent {
	
	private TrafficJamMessage msg = null;
	
	public D2VContentDistributionPeriodicEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		
		D2VPeer connectingNode = (D2VPeer) this.getAssociatedNode();
		
		connectingNode.distributeAllKnownTrafficInformation(triggeringTime);
		
		connectingNode.schedulePeriodicContentDistributionEvent(triggeringTime);
		
		/*
		if(this.msg != null && connectingNode.isTrafficJam() == true)
		{
			if(triggeringTime-this.msg.getTime() < this.msg.getTtl())
			{
				//DebugLog log = new DebugLog();
				//log.print(connectingNode.getKey() + "-" +this.getClass().getName(),triggeringTime);
				
				connectingNode.distributeTrafficInformationMessage(msg, triggeringTime);
				connectingNode.scheduleTrafficJamPeriodicEvent(msg, triggeringTime);
			}
			else
				connectingNode.getIncomingMessageHistory().remove(this.msg);
		}
		*/
	}

	public TrafficJamMessage getMsg() {
		return msg;
	}

	public void setMsg(TrafficJamMessage msg) {
		this.msg = msg;
	}

}
