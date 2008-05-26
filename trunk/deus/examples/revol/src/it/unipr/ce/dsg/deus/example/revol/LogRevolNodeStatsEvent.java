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
		int[] cTot = new int[3]; 
		for (int i = 0; i < 3; i++)
			cTot[i] = 0;
		double qhrTot = 0;
		int initialCpuTot = 0;
		int cpuTot = 0;
		int initialRamTot = 0;
		int ramTot = 0;
		int initialDiskTot = 0;
		int diskTot = 0;
		RevolNode currentNode = null;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolNode) it.next();
			for (int i = 0; i < 3; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				qhrTot += currentNode.getQhr();
				numSearchers++;
			}
			initialCpuTot += currentNode.getInitialCpu();
			cpuTot += currentNode.getCpu();
			initialRamTot += currentNode.getInitialRam();
			ramTot += currentNode.getRam();
			initialDiskTot += currentNode.getInitialDisk();
			diskTot += currentNode.getDisk();
		}
		double[] cMean = new double[3];
		for (int i = 0; i < 3; i++) {
			cMean[i] = (double) cTot[i]/numNodes;
			getLogger().info("mean value of c" + i + " is " + cMean[i]);
		}
		
		int[] cTotBiased = new int[3]; 
		for (int i = 0; i < 3; i++)
			cTotBiased[i] = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolNode) it.next();
			for (int i = 0; i < 3; i++)
				cTotBiased[i] += (currentNode.getC()[i] - cMean[i])*(currentNode.getC()[i] - cMean[i]);
		}
		double[] cVariance = new double[3];
		for (int i = 0; i < 3; i++) {
			cVariance[i] = (double) cTotBiased[i]/(numNodes - 1);
			getLogger().info("variance of c" + i + " is " + cVariance[i]);
		}
			
		getLogger().info("mean QHR value: " + qhrTot / numSearchers);
		
		getLogger().info("mean initial CPU value: " + (double) initialCpuTot / numNodes);
		getLogger().info("mean CPU value: " + (double) cpuTot / numNodes);
		getLogger().info("mean initial RAM value: " + (double) initialRamTot / numNodes);
		getLogger().info("mean RAM value: " + (double) ramTot / numNodes);
		getLogger().info("mean initial DISK value: " + (double) initialDiskTot / numNodes);
		getLogger().info("mean DISK value: " + (double) diskTot / numNodes);
	}

}
