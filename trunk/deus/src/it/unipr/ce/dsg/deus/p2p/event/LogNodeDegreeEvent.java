package it.unipr.ce.dsg.deus.p2p.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Iterator;
import java.util.Properties;

/**
 * This class represents a logger that works out on Peer nodes. It calculates
 * the node degree distribution for each peer of the network. The results is a
 * list of degree starting from 1 up to the maximum degree inside the netowkr.
 * For each degree is computed the number of nodes that has it.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class LogNodeDegreeEvent extends Event {

	public LogNodeDegreeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {
		int nodeDegree[] = new int[Engine.getDefault().getNodes().size()];
		int nodeIndex = 0;
		int kMax = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			Node n = it.next();
			if (!(n instanceof Peer))
				continue;
			int k = ((Peer) n).getNeighbors().size();
			if (k > kMax)
				kMax = k;
			nodeDegree[nodeIndex++] = k;
		}

		int kValues[] = new int[kMax + 1];

		for (int i = 0; i < nodeDegree.length; i++)
			kValues[nodeDegree[i]]++;

		getLogger().info("## Node degree distribution:");
		for (int i = 0; i < kValues.length; i++)
			getLogger().info(i + " " + kValues[i]);
	}

}
