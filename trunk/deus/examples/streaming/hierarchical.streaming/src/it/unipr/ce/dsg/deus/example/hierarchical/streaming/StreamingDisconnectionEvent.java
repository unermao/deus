package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;


/**
 * 
 * @author Picone Marco
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
		
		int index = 0;
		StreamingPeer peer = null;
		
		int size = (Engine.getDefault().getNodes().size() - 1 );
		
		index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
		peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
		
		if(peer.isConnected()){
			//Disconnetto il nodo
			getLogger().fine("Disconnection event, node: " + peer.getKey());
			peer.disconnection(this.triggeringTime);
		}
	}

}
