/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.NodeList;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * This event writes a log file with the kbuckets' data for each node in the network.
 * This event should be scheduled in the simulation's XML file
 * 
 * @author Vittorio Sozzi
 * 
 */
public class D2VLogDiscoveryStatEvent extends Event {

	private AutomatorLogger a;
	private ArrayList<LoggerObject> fileValue;

	public D2VLogDiscoveryStatEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		System.out.println("VT:" + triggeringTime + " LOG_DISCOVERY_STAT_EVENT");

		for(int index=0; index<Engine.getDefault().getNodes().size(); index++)
		{
			Node node = Engine.getDefault().getNodes().get(index);
			
			if(node.getId().equals("D2VPeer"))
			{
				D2VPeer peer = (D2VPeer)node;
				
				System.out.println("VT:" + triggeringTime + " LOG_DISCOVERY_STAT_EVENT Key:" + peer.getKey() + " Discovery Samples: " + peer.getDiscoveryStatistics().size());
			}
		}
		
		
	}
	

}
