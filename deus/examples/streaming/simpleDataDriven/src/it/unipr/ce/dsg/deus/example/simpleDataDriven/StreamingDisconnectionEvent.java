package it.unipr.ce.dsg.deus.example.simpleDataDriven;
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
public class StreamingDisconnectionEvent extends NodeEvent {

	public StreamingDisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("Disconnection !");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		StreamingDisconnectionEvent clone = (StreamingDisconnectionEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new disconnection event");
		
		
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
	
		
		if(associatedStreamingNode.isConnected())
			//Disconnetto il nodo
			associatedStreamingNode.disconnection();
		
		
		getLogger().fine("end disconnection event ##");
		
	}

}
