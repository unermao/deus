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
 * This class is used to print all the predecessor, the successor
 * and the fingerTables of all ChordPeers of the network
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */
public class LogChordNodesResources extends Event {
	
	public LogChordNodesResources(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("######################### ChordPeer Nodes Resources:" + Engine.getDefault().getVirtualTime());

		Collections.sort(Engine.getDefault().getNodes());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			Collections.sort(n.chordResources, new MyComp(null));
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor() + "\t server?: "
							+ n.getServerId() + "\tarriva: " + n.getArrival()
							+ "\tchordResource size: "
							+ n.chordResources.size());
			for (int c = 0; c < n.chordResources.size(); c++) {
//				if(n.chordResources.get(c).getResource_key() >= n.getSuccessor().getKey() || n.chordResources.get(c).getResource_key() <=  n.getPredecessor().getKey())
//					n.setCountFailedResources();
				getLogger().info(
						"\ti: " + c + "\tresourceKey: "
								+ n.chordResources.get(c).getResource_key()
								+ "\tsequence number: "
								+ n.chordResources.get(c).getSequenceNumber()
								+ "\tfilm: "
								+ n.chordResources.get(c).getVideoName());
				
			
				
			}
			getLogger().info("################################");
	}
}

	class MyComp implements Comparator<ChordResourceType>{
		public MyComp(Object object) {
			
		}

		public int compare(ChordResourceType o1, ChordResourceType o2) {
			return o1.compareTo(o2);
		}
		}
}
