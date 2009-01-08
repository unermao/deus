package it.unipr.ce.dsg.deus.example.coolStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
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
		
//System.out.println("entro");
		int index = 0;
		CoolStreamingPeer peer = null;
		
		int size = (Engine.getDefault().getNodes().size() - 1 );
		
		index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
		peer = (CoolStreamingPeer)Engine.getDefault().getNodes().get(index);
		
		if(peer.isConnected())
			//Disconnetto il nodo
			peer.disconnectionCoolStreaming(this.triggeringTime);
		
		
		getLogger().fine("end disconnection event ##");
		
	}

}
