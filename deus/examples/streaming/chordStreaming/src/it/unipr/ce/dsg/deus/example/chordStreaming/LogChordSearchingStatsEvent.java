package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Comparator;
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
public class LogChordSearchingStatsEvent extends Event {

	private int numGeneratedResource = 0;

	public LogChordSearchingStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info(
				"######################### ChordPeer Searching Stats:"
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
			numGeneratedResource += n.chordResources.size();
			getLogger().info(
					"\tnumber of resources: " + n.chordResources.size()
							+ "\tnum connections: " + n.getNumConnections()
							+ "\tvideo search: " + n.getVideoName());
			getLogger().info(" ");
			for (int d = 0; d < n.consumableResources.size(); d++)
				getLogger().info(
						"\t searchResults: "
								+ n.consumableResources.get(d)
										.getResource_key()
								+ "\tsequence number: "
								+ n.consumableResources.get(d)
										.getSequenceNumber() + "\tfilm: "
								+ n.consumableResources.get(d).getVideoName());
			for (int r = 0; r < n.bufferVideo.size(); r++)
				getLogger().info(
						"\t\t bufferVideo: "
								+ n.bufferVideo.get(r).getResource_key()
								+ "\tsequence number: "
								+ n.bufferVideo.get(r).getSequenceNumber()
								+ "\tfilm: "
								+ n.bufferVideo.get(r).getVideoName()
								+ "\tUltima risorsa vista: "
								+ n.getLastPlayingResource());

		}

		getLogger().info("\t generatedResources: = " + numGeneratedResource);
		getLogger().info("################################");
	}

	class MyComp implements Comparator<ChordResourceType> {
		public MyComp(Object object) {

		}

		public int compare(ChordResourceType o1, ChordResourceType o2) {
			return o1.compareTo(o2);
		}
	}

}
