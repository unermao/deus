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
	
	public D2VRandomDisconnection(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
	}

	public Object clone() {
		D2VRandomDisconnection clone = (D2VRandomDisconnection) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		//Pick up a random number between 1 and 10
		int random = Engine.getDefault().getSimulationRandom().nextInt(10) +1;
		
		System.out.println("Disconnecting up to" + random + " peers ...");
		
		ArrayList<Integer> d2vPeerList = Engine.getDefault().getNodeKeysById("D2VPeer");
		ArrayList<D2VPeer> activePeerList = new ArrayList<D2VPeer>();
		
		for(int index=0; index<d2vPeerList.size(); index++)
		{
			D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(d2vPeerList.get(index));
			
			if(peer.isConnected() == true)
				activePeerList.add(peer);
		}
		
		if(activePeerList.size() <= random)
		{
			for(int index=0; index<activePeerList.size(); index++)
				activePeerList.get(index).disconnectNode();
		}
		else
		{
			for(int index=0; index<random; index++)
			{
				int randomIndex = Engine.getDefault().getSimulationRandom().nextInt(activePeerList.size());
				activePeerList.get(randomIndex).disconnectNode();			
			}
		}
	}

}
