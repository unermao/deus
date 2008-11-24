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

/**
 * <p>
 * This class is used to print all the predecessor, the successor
 * and the fingerTables of all ChordPeers of the network
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */
public class LogChordRingStatsEvent extends Event {

	private double numGeneratedResource = 0.0;
	private double numFailedResources = 0.0;
	public LogChordRingStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("##### ChordPeer stats:");
		
		Collections.sort(Engine.getDefault().getNodes());
		getLogger().info("nodes: " + Engine.getDefault().getNodes().size());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor());
			for (int i = 0; i < n.getFingerTable().length; i++)
				getLogger().info("\ti: " + i + "\tn: " + n.getFingerTable()[i]);
			numGeneratedResource +=  n.chordResources.size();
			getLogger().info(
					"\tnumber of resources: " + n.chordResources.size());
	
			for (int c = 0; c < n.chordResources.size(); c++) {
				if(n.chordResources.get(c).getResource_key() >= n.getSuccessor().getKey() || n.chordResources.get(c).getResource_key() <=  n.getPredecessor().getKey())
					setNumFailedResources();
				getLogger().info(
						"\ti: " + c + "\tresourceKey: "
								+ n.chordResources.get(c).getResource_key()
								+ "\towner: "
								+ n.chordResources.get(c).getOwner().getKey());

			}
			for (int d = 0; d < n.searchResults.size(); d++)
				getLogger().info(
						"\t searchResults: "
								+ n.searchResults.get(d).getResource_key());
		}
		getLogger().info("\t generatedResources: = " + numGeneratedResource);
		getLogger().info("\t failedResources: = " + getNumFailedResources());
		getLogger().info("\t % of failedResource: = " + (getNumFailedResources()/numGeneratedResource)*100.0);
	}

	public double getNumFailedResources() {
		return numFailedResources;
	}

	public void setNumFailedResources() {
		this.numFailedResources = numFailedResources+1.0;
	}

}