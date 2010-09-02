package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;


/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VBirthEvent extends NodeEvent {

	public D2VBirthEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		Node n = (Node) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateKey());

		D2VPeer app = (D2VPeer) n;
		//Engine.getDefault().getNodes().add(app);
		Engine.getDefault().addNode(app);
		app.setConnected(true);
		app.init(triggeringTime);
		associatedNode = app;
		
	}

}
