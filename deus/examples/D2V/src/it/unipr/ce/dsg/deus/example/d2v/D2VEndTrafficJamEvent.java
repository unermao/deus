package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VEndTrafficJamEvent extends NodeEvent {

	public D2VEndTrafficJamEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		D2VTrafficElement te = (D2VTrafficElement) this.getAssociatedNode();
		te.exitTrafficJamStatus(triggeringTime);
	}

}
