package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LogRevolNodeStatsEvent extends Event {

	public LogRevolNodeStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}
	
	@Override
	public void initialize() throws InvalidParamsException {
	}

	@Override
	public void run() throws RunException {
		
		getLogger().info("##### RevolNode stats:");
		
		int numNodes = Engine.getDefault().getNodes().size();
		int numSearchers = 0;
		int[] cTot = new int[4]; 
		for (int i = 0; i < 4; i++)
			cTot[i] = 0;
		double qhrTot = 0;
		RevolNode currentNode = null;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolNode) it.next();
			for (int i = 0; i < 4; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				qhrTot += currentNode.getQhr();
				numSearchers++;
			}
		}
		double[] cMean = new double[4];
		for (int i = 0; i < 4; i++) {
			cMean[i] = (double) cTot[i]/numNodes;
			getLogger().info("mean value of c" + i + " is " + cMean[i]);
		}
		
		int[] cTotBiased = new int[4]; 
		for (int i = 0; i < 4; i++)
			cTotBiased[i] = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolNode) it.next();
			for (int i = 0; i < 4; i++)
				cTotBiased[i] += (currentNode.getC()[i] - cMean[i])*(currentNode.getC()[i] - cMean[i]);
		}
		double[] cVariance = new double[4];
		for (int i = 0; i < 4; i++) {
			cVariance[i] = (double) cTotBiased[i]/(numNodes - 1);
			getLogger().info("variance of c" + i + " is " + cVariance[i]);
		}
			
		getLogger().info("mean QHR value: " + qhrTot/numSearchers);
	}

}
