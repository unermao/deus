package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the join of a simulation node. 
 * During the execution of the event the join to network.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTAJoinEvent extends NodeEvent {

	public JXTAJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	
	}
	
	@Override
	public void run() throws RunException {
		
		if(getAssociatedNode() == null){
			System.out.println("Join NULL");
			return;
		}

		
		JXTAEdgePeer connectingNode = null;
		
		if(getAssociatedNode() instanceof JXTARendezvousSuperPeer){
			connectingNode = (JXTARendezvousSuperPeer) getAssociatedNode();
			connectingNode.listOfRdv( (JXTARendezvousSuperPeer) connectingNode);
		}
		else if(getAssociatedNode() instanceof JXTAEdgePeer){
			connectingNode = (JXTAEdgePeer) getAssociatedNode();
		}
		
		if(!connectingNode.isConnected() || connectingNode.rendezvousSP.size() <=1 ){
		
			//if there aren't other node exit
			if(Engine.getDefault().getNodes().size() <= 1){
				connectingNode.setConnected(true);
				return;
			} else {
				
				if(connectingNode.rendezvousSP.size() <= 1){ 
					//there is only current node on list of well-known Rendezvous
					
					//Research new node at startup
					for(int i=0; i<Engine.getDefault().getNodes().size(); i++){
						
						//Add RdV Persistent to list of well known Rendezvous
						
						if( (Engine.getDefault().getNodes().get(i) instanceof JXTARendezvousSuperPeer ) && 
						 ( (JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(i) ).persistent_RdV ){
							connectingNode.listOfRdv( (JXTARendezvousSuperPeer)Engine.getDefault().getNodes().get(i));
							connectingNode.setConnected(true);
						}	
					}
					
				} else { //There is already Rendezvous on cache  
					connectingNode.setConnected(true);
					if(connectingNode instanceof JXTARendezvousSuperPeer)
						( (JXTARendezvousSuperPeer) connectingNode).listOfRdv( (JXTARendezvousSuperPeer) connectingNode);
				}
				
				if(connectingNode.isConnected()){
					
					if(getAssociatedNode() instanceof JXTARendezvousSuperPeer){
						
						((JXTARendezvousSuperPeer) connectingNode).connectToWNRdV();
						
					} else if(getAssociatedNode() instanceof JXTAEdgePeer){
						 
							
						//connect to a random node between Persistent Rendezvous
						boolean ok_toConnect = false;
						int to_connect = 0;
						
						while(!ok_toConnect){
							to_connect = Engine.getDefault().getSimulationRandom().nextInt(connectingNode.rendezvousSP.size());
							if ( connectingNode.rendezvousSP.get(to_connect).isConnected() ) {
								connectingNode.connectToRdv(connectingNode.rendezvousSP.get(to_connect));
								ok_toConnect = true;
								
							}
							
							to_connect++;
						}
						
					}		
				}
				
			}
		} 
		
	}
}
