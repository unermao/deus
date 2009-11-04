/**
 * 
 */
package it.unipr.ce.dsg.deus.example.geokad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event writes a log file with the kbuckets' data for each node in the network.
 * This event should be scheduled in the simulation's XML file
 * 
 * @author Vittorio Sozzi
 * 
 */
public class LogGeoKadNodesStatsEvent extends Event {

	public LogGeoKadNodesStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		System.out.println(
				"#KademliaNodesStats info @ "
						+ Engine.getDefault().getVirtualTime()
						+ " Number of nodes: "
						+ Engine.getDefault().getNodes().size());
		
		getLogger().info(
				"#KademliaNodesStats info @ "
						+ Engine.getDefault().getVirtualTime()
						+ " Number of nodes: "
						+ Engine.getDefault().getNodes().size());

		Collections.sort(Engine.getDefault().getNodes());
		verbose();
		//compressed();
		//network_dump();

	}
	
	public void network_dump() {
		// Assuming all nodes have the same Properties
		GeoKadPeer peer =  (GeoKadPeer) Engine.getDefault().getNodes().get(0);
		getLogger().info("Properties = " + peer.getKBucketDim() + " " + peer.getResourcesNode() + " " + peer.getAlpha() + " " + peer.getDiscoveryMaxWait());
		String s;
		for (Node node: Engine.getDefault().getNodes()) {
			peer = (GeoKadPeer) node;
			s = new String();
			s += peer.getKey() + " = ";
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				for (GeoKadPeer entry : bucket) {
					s += entry.getKey() + " ";
				}
			}
			getLogger().info(s);
		}
	}

	public void verbose() {
		for (Node node : Engine.getDefault().getNodes()) {
			GeoKadPeer peer = (GeoKadPeer) node;
			getLogger().info("\nn: " + peer.getKey());
			int size = 0;
			int i = 0;
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				size += bucket.size();
				String s = new String();
				for (GeoKadPeer entry : bucket) {
					s += entry.getKey() + " ";
				}
				getLogger().info(
						"\t[" + i + " size: " + bucket.size() + "]:" + s);
				i++;

			}
			getLogger().info(
					"Size of kbuckets for " + peer.getKey() + ": " + size);
			getLogger().info("Searches total: " + peer.logSearch.size());
			for (Integer k : peer.logSearch.keySet()) {
				getLogger().info(
						"key= " + k + " (" + peer.logSearch.get(k) + ")");
			}
		}
		
	}

	public void compressed() {
		Set<Integer> knownNodes = new TreeSet<Integer>();
		for (Node node : Engine.getDefault().getNodes()) {
			GeoKadPeer peer = (GeoKadPeer) node;
			String s = new String("n: " + peer.getKey() + "K"
					+ peer.getKBucketDim() + "A" + peer.getAlpha()
					+ " Searches#: " + peer.logSearch.size() + " ");
			String s2 = new String();
			String s3 = new String();
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				s2 += " " + bucket.size();
				for (GeoKadPeer knownNode : bucket) {
					knownNodes.add(knownNode.getKey()); // Set = no duplicate
														// elements
				}
			}

			for (Integer v : peer.logSearch.values()) {
				s3 += " " + v;
			}
			getLogger().info(s + s2 + " S " + s3);
		}
		getLogger().info("Total known nodes " + knownNodes.size() + "\n");

		knownNodes = null;
	}
	
	

}
