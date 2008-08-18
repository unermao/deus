package it.unipr.ce.dsg.deus.example.energy;

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
 * This Event logs a number of statistics related to the current "snapshot" of
 * the EnergyPeer network:
 * <ol>
 * <li>mean value and variance of the chromosomes</li>
 * <li>initial and mean ENERGY value</li>
 * <li>number of searchers (i.e. peers with at least 1 query sent)</li>
 * <li>average QHR (query hit ratio)</li>
 * </ol>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class LogEnergyPeerStatsEvent extends Event {

	public LogEnergyPeerStatsEvent(String id, Properties params,
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
		double qhrSearchersTot = 0;
		int initialEnergyTot = 0;
		int energyTot = 0;
		
		EnergyPeer currentNode = null;
		int numPeers = Engine.getDefault().getNodes().size();
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (EnergyPeer) it.next();
			for (int i = 0; i < 3; i++)
				cTot[i] += currentNode.getC()[i];
			if (currentNode.getQ() > 0) {
				numSearchers++;
				qhrSearchersTot += currentNode.getQhr();
			}
			qhrTot += currentNode.getQhr();
			initialEnergyTot += currentNode.getMaxPower();
			energyTot += currentNode.getPower();
		}

		double qhrMean = qhrTot / numPeers;
		double qhrSearchersMean = qhrSearchersTot / numSearchers;

		double qhrTotBiased = 0;
		double qhrSearchersBiased = 0;
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			currentNode = (EnergyPeer) it.next();
			qhrTotBiased += (currentNode.getQhr() - qhrMean) * (currentNode.getQhr() - qhrMean);
			if (currentNode.getQ() > 0) {
				qhrSearchersBiased += (currentNode.getQhr() - qhrSearchersMean) * (currentNode.getQhr() - qhrSearchersMean);
			}
		}
		double qhrVariance = (double) qhrTotBiased / (numPeers - 1);
		double qhrSearchersVariance = (double) qhrSearchersBiased / (numSearchers - 1);

		getLogger().info("num peers = " + numPeers + " ** num searchers = " + numSearchers);
		getLogger().info("QHR (total): mean " + qhrMean + ", variance = " + qhrVariance);
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
			currentNode = (EnergyPeer) it.next();
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
				"mean max POWER value: " + (double) initialEnergyTot / numNodes);
		getLogger().info("mean POWER value: " + (double) energyTot / numNodes);
	}

}
