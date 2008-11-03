package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import java.util.Properties;

/**
 * <p>
 * This class is used to run the Stabilize methods that provide to stabilize
 * fingerTables of the ChordPeer
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */

public class ChordStabilizeEvent extends NodeEvent {

	public ChordStabilizeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}
	@Override
	public void run() throws RunException {	
		if(((ChordPeer) getAssociatedNode()).isConnected())
		((ChordPeer) getAssociatedNode()).stabilize();
	}
}