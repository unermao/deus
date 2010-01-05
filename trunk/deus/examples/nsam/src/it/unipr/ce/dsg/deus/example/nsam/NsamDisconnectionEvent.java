package it.unipr.ce.dsg.deus.example.nsam;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class NsamDisconnectionEvent extends NodeEvent{

	
	public NsamDisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("Disconnection !");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		NsamDisconnectionEvent clone = (NsamDisconnectionEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new disconnection event");
		
		int index = 0;
		NsamPeer peer = null;
		
		int size = (Engine.getDefault().getNodes().size() - 1 );
		
		index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
		peer = (NsamPeer)Engine.getDefault().getNodes().get(index);
		
		if(peer.isConnected())
			peer.setConnected(false);
			//Rimuovo tutti i miei vicini
			peer.resetNeighbors();
		
		//con questo elimino il nodo dalla lista...per ora non lo faccio
	//	Engine.getDefault().getNodes().remove(this);

			//Disconnetto il nodo
			//peer.disconnection(this.triggeringTime);
		
		
		getLogger().fine("end disconnection event ##");
		
	}

	
}
