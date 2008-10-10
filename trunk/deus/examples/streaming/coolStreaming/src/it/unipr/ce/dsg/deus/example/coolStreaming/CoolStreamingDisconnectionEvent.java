package it.unipr.ce.dsg.deus.example.coolStreaming;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;


/**
 * <p>
 * This event is related to the release of a previously 
 * consumed resource, by updating the corresponding value 
 * on the resource owner.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class CoolStreamingDisconnectionEvent extends NodeEvent {

	public CoolStreamingDisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("Disconnection !");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		CoolStreamingDisconnectionEvent clone = (CoolStreamingDisconnectionEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new disconnection event");
		
		
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
	
		
		if(associatedStreamingNode.isConnected())
			//Disconnetto il nodo
			associatedStreamingNode.disconnection(triggeringTime);
		
		
		getLogger().fine("end disconnection event ##");
		
	}

}
