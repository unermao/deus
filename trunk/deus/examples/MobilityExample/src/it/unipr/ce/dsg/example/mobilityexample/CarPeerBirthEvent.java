package it.unipr.ce.dsg.example.mobilityexample;

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
public class CarPeerBirthEvent extends NodeEvent {

	public CarPeerBirthEvent(String id, Properties params,
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

		CarPeer carPeer = (CarPeer) n;
		
		Engine.getDefault().addNode(carPeer);
		
		carPeer.setConnected(true);
		carPeer.mobilityInit(triggeringTime,carPeer.getFtmModel());
		associatedNode = carPeer;
		
	}

}
