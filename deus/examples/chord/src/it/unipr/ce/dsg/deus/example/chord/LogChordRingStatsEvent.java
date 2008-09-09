package it.unipr.ce.dsg.deus.example.chord;

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

public class LogChordRingStatsEvent extends Event{

		public LogChordRingStatsEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			initialize();
		}

		public void initialize() throws InvalidParamsException {
		}

		public void run() throws RunException {

			getLogger().info("##### ChordPeer stats:");

			int numNodes = Engine.getDefault().getNodes().size();
			int numSearchers = 0;
			int[] cTot = new int[3];
			for (int i = 0; i < 3; i++)
				cTot[i] = 0;
			double qhrTot = 0;
			double qhrSearchersTot = 0;
			
			ChordPeer currentNode = null;
			int numPeers = Engine.getDefault().getNodes().size();
				getLogger().info("num peers = " + numPeers);
				for(int i = 0; i < Engine.getDefault().getNodes().size(); i++)
				getLogger().info("nodo  "+ i + " " + Engine.getDefault().getNodes().get(i).getId());
				
				
				for(int i = 0; i < Engine.getDefault().getNodes().size(); i++)
					{
					ChordPeer peer = (ChordPeer) Engine.getDefault().getNodes().get(i);
					getLogger().info("fingerTable del nodo "+ i + " " + peer.getId());
					for(int c = 0; c < peer.fingerTable.size(); c++)
					getLogger().info(peer.fingerTable.get(c));
					}
			}

		}

	
	
	
