package it.unipr.ce.dsg.deus.example.revol;

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
 * This Event logs a number of statistics related to the 
 * current "snapshot" of the RevolPeer network:
 *  <ol>
 * 	<li> mean value and variance of the chromosomes </li>
 * 	<li> initial and mean CPU value </li>
 * 	<li> initial and mean RAM value </li>
 *  <li> initial and mean DISK value </li>
 *  <li> number of searchers (i.e. peers with at least 1 query sent) </li>
 *  <li> average QHR (query hit ratio) </li>
 * </ol>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class LogRevolPeerStatsEvent extends Event {

	public LogRevolPeerStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}
	
	public void initialize() throws InvalidParamsException {
	}
	
	public void run() throws RunException {
		
		getLogger().info("##### RevolPeer stats:");
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());
		
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
		RevolPeer currentNode = null;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				//getLogger().info("qhr = " + currentNode.getQhr());
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
		
		double qhrMean = qhrTot / numSearchers;
		
		double qhrTotBiased = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolPeer) it.next();
			if (currentNode.getQ() > 0) {
				qhrTotBiased += (currentNode.getQhr() - qhrMean) * (currentNode.getQhr() - qhrMean);
			}
		}
		double qhrVariance = (double) qhrTotBiased /(numSearchers - 1);
		
		getLogger().info("num searchers = " + numSearchers);
		getLogger().info("mean QHR value: " + qhrMean);
		getLogger().info("QHR variance: " + qhrVariance);
		
		double[] cMean = new double[3];
		for (int i = 0; i < 3; i++) {
			cMean[i] = (double) cTot[i]/numNodes;
			getLogger().info("mean value of c" + i + " is " + cMean[i]);
		}
		
		double[] cTotBiased = new double[3]; 
		for (int i = 0; i < 3; i++)
			cTotBiased[i] = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTotBiased[i] += (currentNode.getC()[i] - cMean[i])*(currentNode.getC()[i] - cMean[i]);
		}
		double[] cVariance = new double[3];
		for (int i = 0; i < 3; i++) {
			cVariance[i] = (double) cTotBiased[i]/(numNodes - 1);
			getLogger().info("variance of c" + i + " is " + cVariance[i]);
		}
		
		getLogger().info("mean initial CPU value: " + (double) initialCpuTot / numNodes);
		getLogger().info("mean CPU value: " + (double) cpuTot / numNodes);
		getLogger().info("mean initial RAM value: " + (double) initialRamTot / numNodes);
		getLogger().info("mean RAM value: " + (double) ramTot / numNodes);
		getLogger().info("mean initial DISK value: " + (double) initialDiskTot / numNodes);
		getLogger().info("mean DISK value: " + (double) diskTot / numNodes);
	}

}
