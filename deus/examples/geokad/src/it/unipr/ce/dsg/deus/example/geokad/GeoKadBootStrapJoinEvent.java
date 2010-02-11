package it.unipr.ce.dsg.deus.example.geokad;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class GeoKadBootStrapJoinEvent extends NodeEvent {

	public GeoKadBootStrapJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		System.out.println("Node List: " + Engine.getDefault().getNodes().size());
		System.out.println("BootStrap Peer Join Event .... " + this.getAssociatedNode().getId() + "-" + this.getAssociatedNode().getKey());
		GeoKadBootStrapPeer bootStrapPeer = (GeoKadBootStrapPeer) this.getAssociatedNode();
		bootStrapPeer.setConnected(true);
		GeoKadBootStrapPeer.BOOTSTRAP_KEY = this.getAssociatedNode().getKey();
	}
}
