package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

public class D2VRandomDisconnection extends Event {
	
	private static String DISCONNECTION_PERCENTAGE = "discPercentage";
	private double diconnectionPercentage = 0.0;
	
	public D2VRandomDisconnection(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
		
		if (params.getProperty(DISCONNECTION_PERCENTAGE) == null)
			throw new InvalidParamsException(DISCONNECTION_PERCENTAGE
					+ " param is expected");
		try {
			
			diconnectionPercentage = Double.parseDouble(params.getProperty(DISCONNECTION_PERCENTAGE));
			
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(DISCONNECTION_PERCENTAGE
					+ " must be a valid double value.");
		}
		
		System.out.println("D2VRandomDisconnection -> Disconnection Percentage: " +  diconnectionPercentage);
	
	}

	public Object clone() {
		D2VRandomDisconnection clone = (D2VRandomDisconnection) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		ArrayList<Integer> d2vPeerList = Engine.getDefault().getNodeKeysById("D2VPeer");
		ArrayList<D2VPeer> activePeerList = new ArrayList<D2VPeer>();
		
		for(int index=0; index<d2vPeerList.size(); index++)
		{
			D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(d2vPeerList.get(index));
			
			if(peer.isConnected() == true)
				activePeerList.add(peer);
		}
		
		int discPeers = (int)(this.diconnectionPercentage*(double)activePeerList.size()/100.0);
		System.out.println("D2VRandomDisconnection -> Disconnecting " + discPeers + " peers ...");
		
		if(activePeerList.size() <= discPeers)
		{
			for(int index=0; index<activePeerList.size(); index++)
				activePeerList.get(index).disconnectNode();
		}
		else
		{
			for(int index=0; index<discPeers; index++)
			{
				int randomIndex = Engine.getDefault().getSimulationRandom().nextInt(activePeerList.size());
				activePeerList.get(randomIndex).disconnectNode();			
			}
		}
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}

}
