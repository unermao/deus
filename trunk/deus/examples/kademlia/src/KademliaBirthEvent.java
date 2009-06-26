package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.kademlia.KademliaResourceType;

public class KademliaBirthEvent extends NodeEvent {
	
	public KademliaBirthEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id,params,parentProcess);
		initialize();
	}
	
	public void initialize() { }

	public void run() throws RunException {
		if (getParentProcess() == null)
			throw new RunException(
					"A parent process must be set in order to run "
							+ getClass().getCanonicalName());
		// create a node (the type is randomly chosen among those which are
		// associated to the process)
		Node n = (Node) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateKey());
		
		KademliaPeer app = (KademliaPeer) n;

		
		Engine.getDefault().getNodes().add(app);
		associatedNode = app;
		
		
		for(int i = 0; i <app.getResourcesNode(); i++)
			try {
				app.kademliaResources.add(new KademliaResourceType(Engine.getDefault().generateResourceKey()));
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

}
