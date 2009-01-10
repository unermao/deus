package it.unipr.ce.dsg.deus.example.revol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This Event logs a number of statistics related to the current "snapshot" of
 * the RevolPeer network:
 * <ol>
 * <li>mean value and variance of the chromosomes</li>
 * <li>initial and mean CPU value</li>
 * <li>initial and mean RAM value</li>
 * <li>initial and mean DISK value</li>
 * <li>number of searchers (i.e. peers with at least 1 query sent)</li>
 * <li>average QHR (query hit ratio)</li>
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
		
		AutomatorLogger a = new AutomatorLogger("logger");
		ArrayList<LoggerObject> fileValue = new ArrayList<LoggerObject>();
		
		int numNodes = Engine.getDefault().getNodes().size();
		int numSearchers = 0;
		int[] cTot = new int[3];
		for (int i = 0; i < 3; i++)
			cTot[i] = 0;
		double qhrTot = 0;
		double qhrSearchersTot = 0;
		int initialCpuTot = 0;
		int cpuTot = 0;
		int initialRamTot = 0;
		int ramTot = 0;
		int initialDiskTot = 0;
		int diskTot = 0;
		
		RevolPeer currentNode = null;
		int numPeers = Engine.getDefault().getNodes().size();
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				numSearchers++;
				qhrSearchersTot += currentNode.getQhr();
			}
			qhrTot += currentNode.getQhr();
			initialCpuTot += currentNode.getInitialCpu();
			cpuTot += currentNode.getCpu();
			initialRamTot += currentNode.getInitialRam();
			ramTot += currentNode.getRam();
			initialDiskTot += currentNode.getInitialDisk();
			diskTot += currentNode.getDisk();
		}

		double qhrMean = qhrTot / numPeers;
		double qhrSearchersMean = qhrSearchersTot / numSearchers;

		double qhrTotBiased = 0;
		double qhrSearchersBiased = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			qhrTotBiased += (currentNode.getQhr() - qhrMean) * (currentNode.getQhr() - qhrMean);
			if (currentNode.getQ() > 0) {
				qhrSearchersBiased += (currentNode.getQhr() - qhrSearchersMean) * (currentNode.getQhr() - qhrSearchersMean);
			}
		}
		double qhrVariance = (double) qhrTotBiased / (numPeers - 1);
		double qhrSearchersVariance = (double) qhrSearchersBiased / (numSearchers - 1);
		
		//getLogger().info("num peers = " + numPeers + " ** num searchers = " + numSearchers);
		//getLogger().info("QHR (total): mean = " + qhrMean + ", variance = " + qhrVariance);
		//getLogger().info("QHR (searchers): mean = " + qhrSearchersMean + ", variance = " + qhrSearchersVariance);
				
		fileValue.add(new LoggerObject("num peers", numPeers));
		fileValue.add(new LoggerObject("num searchers", numSearchers));
		fileValue.add(new LoggerObject("QHR (total): mean", qhrMean));
		fileValue.add(new LoggerObject("QHR (total): variance", qhrVariance));
		fileValue.add(new LoggerObject("QHR (searchers): mean", qhrSearchersMean));
		fileValue.add(new LoggerObject("QHR (searchers): variance", qhrSearchersVariance));
		
		double[] cMean = new double[3];
		for (int i = 0; i < 3; i++) {
			cMean[i] = (double) cTot[i] / numNodes;
			fileValue.add(new LoggerObject("mean value of c" + i, cMean[i]));
			//getLogger().info("mean value of c" + i + " is " + cMean[i]);
		}

		double[] cTotBiased = new double[3];
		for (int i = 0; i < 3; i++)
			cTotBiased[i] = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTotBiased[i] += (currentNode.getC()[i] - cMean[i])
						* (currentNode.getC()[i] - cMean[i]);
		}
		double[] cVariance = new double[3];
		for (int i = 0; i < 3; i++) {
			cVariance[i] = (double) cTotBiased[i] / (numNodes - 1);
			fileValue.add(new LoggerObject("variance of c" + i, cVariance[i]));
			//getLogger().info("variance of c" + i + " is " + cVariance[i]);
		}

		fileValue.add(new LoggerObject("mean initial CPU value", (double) initialCpuTot / numNodes));
		//getLogger().info("mean initial CPU value: " + (double) initialCpuTot / numNodes);
		fileValue.add(new LoggerObject("mean CPU value", (double) cpuTot / numNodes));
		//getLogger().info("mean CPU value: " + (double) cpuTot / numNodes);
		fileValue.add(new LoggerObject("mean initial RAM value", (double) initialRamTot / numNodes));
		//getLogger().info("mean initial RAM value: " + (double) initialRamTot / numNodes);
		fileValue.add(new LoggerObject("mean RAM value", (double) ramTot / numNodes));
		//getLogger().info("mean RAM value: " + (double) ramTot / numNodes);
		fileValue.add(new LoggerObject("mean initial DISK value", (double) initialDiskTot / numNodes));
		//getLogger().info("mean initial DISK value: " + (double) initialDiskTot / numNodes);
		fileValue.add(new LoggerObject("mean DISK value", (double) diskTot / numNodes));
		//getLogger().info("mean DISK value: " + (double) diskTot / numNodes);
						
		a.write(Engine.getDefault().getVirtualTime(), fileValue);
	}
	
	public void oldrun() throws RunException {

		getLogger().info("##### RevolPeer stats:");
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());

		int numNodes = Engine.getDefault().getNodes().size();
		int numSearchers = 0;
		int[] cTot = new int[3];
		for (int i = 0; i < 3; i++)
			cTot[i] = 0;
		double qhrTot = 0;
		double qhrSearchersTot = 0;
		int initialCpuTot = 0;
		int cpuTot = 0;
		int initialRamTot = 0;
		int ramTot = 0;
		int initialDiskTot = 0;
		int diskTot = 0;
		
		RevolPeer currentNode = null;
		int numPeers = Engine.getDefault().getNodes().size();
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				numSearchers++;
				qhrSearchersTot += currentNode.getQhr();
			}
			qhrTot += currentNode.getQhr();
			initialCpuTot += currentNode.getInitialCpu();
			cpuTot += currentNode.getCpu();
			initialRamTot += currentNode.getInitialRam();
			ramTot += currentNode.getRam();
			initialDiskTot += currentNode.getInitialDisk();
			diskTot += currentNode.getDisk();
		}

		double qhrMean = qhrTot / numPeers;
		double qhrSearchersMean = qhrSearchersTot / numSearchers;

		double qhrTotBiased = 0;
		double qhrSearchersBiased = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			qhrTotBiased += (currentNode.getQhr() - qhrMean) * (currentNode.getQhr() - qhrMean);
			if (currentNode.getQ() > 0) {
				qhrSearchersBiased += (currentNode.getQhr() - qhrSearchersMean) * (currentNode.getQhr() - qhrSearchersMean);
			}
		}
		double qhrVariance = (double) qhrTotBiased / (numPeers - 1);
		double qhrSearchersVariance = (double) qhrSearchersBiased / (numSearchers - 1);

		getLogger().info("num peers = " + numPeers + " ** num searchers = " + numSearchers);
		getLogger().info("QHR (total): mean = " + qhrMean + ", variance = " + qhrVariance);
		getLogger().info("QHR (searchers): mean = " + qhrSearchersMean + ", variance = " + qhrSearchersVariance);

		double[] cMean = new double[3];
		for (int i = 0; i < 3; i++) {
			cMean[i] = (double) cTot[i] / numNodes;
			getLogger().info("mean value of c" + i + " is " + cMean[i]);
		}

		double[] cTotBiased = new double[3];
		for (int i = 0; i < 3; i++)
			cTotBiased[i] = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (RevolPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTotBiased[i] += (currentNode.getC()[i] - cMean[i])
						* (currentNode.getC()[i] - cMean[i]);
		}
		double[] cVariance = new double[3];
		for (int i = 0; i < 3; i++) {
			cVariance[i] = (double) cTotBiased[i] / (numNodes - 1);
			getLogger().info("variance of c" + i + " is " + cVariance[i]);
		}

		getLogger().info(
				"mean initial CPU value: " + (double) initialCpuTot / numNodes);
		getLogger().info("mean CPU value: " + (double) cpuTot / numNodes);
		getLogger().info(
				"mean initial RAM value: " + (double) initialRamTot / numNodes);
		getLogger().info("mean RAM value: " + (double) ramTot / numNodes);
		getLogger().info(
				"mean initial DISK value: " + (double) initialDiskTot
						/ numNodes);
		getLogger().info("mean DISK value: " + (double) diskTot / numNodes);
	}

}
