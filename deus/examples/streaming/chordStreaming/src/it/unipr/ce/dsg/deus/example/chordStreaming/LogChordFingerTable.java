package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This class is used to print all the predecessor, the successor and the
 * fingerTables of all ChordPeers of the network
 * </p>
 * 
 * @author Matteo Agosti (matteo.agosti@unipr.it)
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 */
public class LogChordFingerTable extends Event {

	public LogChordFingerTable(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info(
				"######################### ChordPeer fingerTable:"
						+ Engine.getDefault().getVirtualTime());

		Collections.sort(Engine.getDefault().getNodes());
		getLogger().info("nodes: " + Engine.getDefault().getNodes().size());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor() + "\t server?: "
							+ n.getServerId() + "\tarriva: " + n.getArrival());
			for (int i = 0; i < n.getFingerTable().length; i++)
				getLogger().info("\ti: " + i + "\tn: " + n.getFingerTable()[i]);
		}
		getLogger().info("################################");
	}
}
