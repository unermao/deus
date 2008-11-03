package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * This event represents the birth of a simulation ChordPeer. During its execution an
 * instance of the node associated to the event will be created.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordBirthEvent extends NodeEvent {
	
	public ChordBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

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
		
		Engine.getDefault().getNodes().add(n);
		associatedNode = n;
		
		ChordPeer app = (ChordPeer) n;
		app.setConnected(true);
		
		for(int i = 0; i <Engine.getDefault().getKeySpaceSize()/2000; i++)
			try {
				app.chordResources.add(new ChordResourceType(Engine.getDefault().generateResourceKey()));
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	}

}
