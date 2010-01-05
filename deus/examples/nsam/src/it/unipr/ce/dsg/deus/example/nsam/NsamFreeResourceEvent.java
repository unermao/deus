package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class NsamFreeResourceEvent extends NodeEvent {
	
private NsamService serv = null;


	public NsamFreeResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		NsamFreeResourceEvent clone = (NsamFreeResourceEvent) super.clone();
		clone.serv=null;
		return clone;
	}

	
	public void run() throws RunException {
		getLogger().fine("## free resource");
	NsamPeer provider = (NsamPeer)associatedNode;
	getLogger().fine("service to be set free: " + serv.getServiceId());
	getLogger().fine("Number of active connections for the node before freedom" + provider.getActiveConnections());
	provider.removeActiveConnection();
	getLogger().fine("end free resource ##");
	}


	public NsamService getServ() {
	return serv;
}

public void setServ(NsamService serv) {
	this.serv = serv;
}

}

