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
public class D2VTrafficElementBirthEvent extends NodeEvent {

	public D2VTrafficElementBirthEvent(String id, Properties params,
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

		D2VTrafficElement app = (D2VTrafficElement) n;
		//Engine.getDefault().getNodes().add(app);
		Engine.getDefault().addNode(app);
		try {
			app.initialize();
		} catch (InvalidParamsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		associatedNode = app;
		
	}

}
