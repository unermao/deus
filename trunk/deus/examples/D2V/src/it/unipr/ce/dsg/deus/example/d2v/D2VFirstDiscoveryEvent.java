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
		
		DebugLog log = new DebugLog();
		log.printStart(connectingNode.getKey(),this.getClass().getName(),triggeringTime);
		
		//Retrieve Initial List from BootStrap
		ArrayList<D2VPeerDescriptor> initList = this.getInitialPeerList(connectingNode.getPeerDescriptor());
		
		for(int index=0;index<initList.size();index++)
			connectingNode.insertPeer("D2VFirstDiscoveryEvent",initList.get(index));
		
		System.out.println("VT:"+triggeringTime+" FIRST_DISCOVERY_EVENT ---> Peer Key: " + connectingNode.getKey() + " InitList: " + initList.size() + " Total Peers: " + Engine.getDefault().getNodeKeysById("D2VPeer").size());
		
		//Schedule a new DISCOVERY EVENT
		connectingNode.scheduleDiscovery(triggeringTime);
		
		/*
		try {
				
			int random = Engine.getDefault().getSimulationRandom().nextInt(Engine.getDefault().getNodes().size());
			
			D2VPeer randomPeer = (D2VPeer)Engine.getDefault().getNodes().get(random);
			
			Message msg = new Message("TEST_MSG", connectingNode.getKey(), randomPeer.getKey(), new String("AHAHAH").getBytes());
			
			MessageExchangeEvent event = (MessageExchangeEvent) new MessageExchangeEvent("message_exchange", params, null).createInstance(triggeringTime+25);
			event.setOneShot(true);
			event.setAssociatedNode(randomPeer);
			event.setMsg(msg);
			Engine.getDefault().insertIntoEventsList(event);
		} catch (InvalidParamsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		log.printEnd(connectingNode.getKey(),this.getClass().getName(),triggeringTime);
	}
	
	public ArrayList<D2VPeerDescriptor> getInitialPeerList(D2VPeerDescriptor peer)
	{
		final double peerLat = peer.getGeoLocation().getLatitude();
		final double peerLon = peer.getGeoLocation().getLongitude();
		
		
		ArrayList<D2VPeerDescriptor> peerList = new ArrayList<D2VPeerDescriptor>();
		
		ArrayList<Integer> keyList = Engine.getDefault().getNodeKeysById("D2VPeer");
		
		for(int i=0; i<keyList.size(); i++)
		{
			D2VPeer p = (D2VPeer)Engine.getDefault().getNodeByKey(keyList.get(i));
			peerList.add(p.getPeerDescriptor());
		}
		
		if(peerList.size() > NODE_LIST_LIMIT)
		{
			ArrayList<D2VPeerDescriptor> tempList = new ArrayList<D2VPeerDescriptor>();
			
			// Sort PeerInfo according to distance
			Collections.sort(peerList, new Comparator<D2VPeerDescriptor>() {

				public int compare(D2VPeerDescriptor o1, D2VPeerDescriptor o2) {
			    
					double dist1 = GeoDistance.distance(peerLon,peerLat, o1.getGeoLocation().getLongitude(), o1.getGeoLocation().getLatitude());
					double dist2 = GeoDistance.distance(peerLon,peerLat, o2.getGeoLocation().getLongitude(), o2.getGeoLocation().getLatitude());
						
					if(dist1 == dist2)
						return 0;
					
					if(dist1 < dist2)
						return -1;
				
					if(dist1 > dist2)
						return 1;
					
					return 0;
			    }});
			
				for(int index=0; index<NODE_LIST_LIMIT; index++)
				{
					D2VPeerDescriptor peerInfo = peerList.get(index);
					tempList.add(peerInfo);
				}	
				//System.out.println("#########################################################");
				
				return new ArrayList<D2VPeerDescriptor>(tempList);
		}
		else
			return new ArrayList<D2VPeerDescriptor>(peerList);
		
		
	}

}
