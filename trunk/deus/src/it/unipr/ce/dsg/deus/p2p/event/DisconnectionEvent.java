package it.unipr.ce.dsg.deus.p2p.event;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;

public class DisconnectionEvent extends Event {

	private Peer initiator = null;
	private Peer target = null;

	public DisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {

	}

	public void setNodesToDisconnect(Peer initiator, Peer target) {
		this.initiator = initiator;
		this.target = target;
	}
	
	public Object clone() {
		DisconnectionEvent clone = (DisconnectionEvent) super.clone();
		clone.target = null;
		return clone;
	}

	@Override
	public void run() throws RunException {
		if (target != null)
			initiator.removeNeighbor(this.target);
		else { // disconnect from all neighbors
			initiator.resetNeighbors();
			initiator.setReachable(false);
		}
	}

}
