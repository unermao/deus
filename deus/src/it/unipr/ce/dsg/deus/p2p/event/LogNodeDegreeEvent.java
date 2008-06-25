package it.unipr.ce.dsg.deus.p2p.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Iterator;
import java.util.Properties;

public class LogNodeDegreeEvent extends Event {

	public LogNodeDegreeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() throws RunException {
		int nodeDegree[] = new int[Engine.getDefault().getNodes().size()];
		int nodeIndex = 0;
		int kMax = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			int k = it.next().getNeighbors().size();
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
