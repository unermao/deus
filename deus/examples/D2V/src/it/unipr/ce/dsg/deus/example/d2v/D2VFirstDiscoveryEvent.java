package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VFirstDiscoveryEvent extends NodeEvent {

	private int NODE_LIST_LIMIT = 20;
	
	public D2VFirstDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		
		D2VPeer connectingNode = (D2VPeer) this.getAssociatedNode();
		
		//DebugLog log = new DebugLog();
		//log.printStart(connectingNode.getKey(),this.getClass().getName(),triggeringTime);
		
		//Retrieve Initial List from BootStrap
		ArrayList<D2VPeerDescriptor> initList = connectingNode.getInitialPeerList(connectingNode.getPeerDescriptor(),NODE_LIST_LIMIT);
		
		for(int index=0;index<initList.size();index++)
			connectingNode.insertPeer("D2VFirstDiscoveryEvent",initList.get(index),triggeringTime);
		
		//System.out.println("VT:"+triggeringTime+" FIRST_DISCOVERY_EVENT ---> Peer Key: " + connectingNode.getKey() + " InitList: " + initList.size() + " Total Peers: " + Engine.getDefault().getNodeKeysById("D2VPeer").size());
		
		//Schedule a new DISCOVERY EVENT
		connectingNode.scheduleDiscovery(triggeringTime);
		
		//log.printEnd(connectingNode.getKey(),this.getClass().getName(),triggeringTime);
	}

}
