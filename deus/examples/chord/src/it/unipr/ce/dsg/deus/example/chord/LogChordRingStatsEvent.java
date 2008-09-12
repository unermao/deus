package it.unipr.ce.dsg.deus.example.chord;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LogChordRingStatsEvent extends Event {

	public LogChordRingStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("##### ChordPeer stats:");
		Collections.sort(Engine.getDefault().getNodes());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor());
			for (int i = 0; i < n.getFingerTable().length; i++)
				getLogger().info("\ti: " + i + "\tn: " + n.getFingerTable()[i]);
		}
	}

}
