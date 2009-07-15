package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event represents the join of a simulation node. During the execution of
 * the event the join to network.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTAJoinEvent extends NodeEvent {

	public JXTAJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
	
	}
	
	@Override
	public void run() throws RunException {
		
		System.out.println("JOIN EVENT");
		
		if(getAssociatedNode() == null){
			System.out.println("Join NULL");
			return;
		}

		
		JXTAEdgePeer connectingNode = null;
		
		if(getAssociatedNode() instanceof JXTARendezvousSuperPeer){
			connectingNode = (JXTARendezvousSuperPeer) getAssociatedNode();
			connectingNode.listOfRdv( (JXTARendezvousSuperPeer) connectingNode);
			System.out.println(connectingNode.JXTAID + " is RdV");
		}
		else if(getAssociatedNode() instanceof JXTAEdgePeer){
			connectingNode = (JXTAEdgePeer) getAssociatedNode();
			System.out.println(connectingNode.JXTAID + " is EP");
		}
		
		if(!connectingNode.isConnected() || connectingNode.rendezvousSP.size() <=1 ){
		
			//se non ci sono altri nodi esce
			if(Engine.getDefault().getNodes().size() <= 1){
				connectingNode.setConnected(true);
				return;
			} else {
				
				if(connectingNode.rendezvousSP.size() <= 1){ //c'è solo il nodo attuale nella lista
					System.out.println("NEW SEARCH OF RdV");
					//deve cercare nuovi nodi all'avvio
					for(int i=0; i<Engine.getDefault().getNodes().size(); i++){
						
						//Aggiunge i RdV PERSISTANT alla lista di quelli noti
						
						if( (Engine.getDefault().getNodes().get(i) instanceof JXTARendezvousSuperPeer ) && 
						 ( (JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(i) ).persistant_RdV ){
							connectingNode.listOfRdv( (JXTARendezvousSuperPeer)Engine.getDefault().getNodes().get(i));
							connectingNode.setConnected(true);
						}	
					}
					
				} else { //aveva in cache, prima di disconnetersi, la conoscenza di altri nodi 
					connectingNode.setConnected(true);
					if(connectingNode instanceof JXTARendezvousSuperPeer)
						( (JXTARendezvousSuperPeer) connectingNode).listOfRdv( (JXTARendezvousSuperPeer) connectingNode);
					System.out.println("RICONNECTED... " + connectingNode.JXTAID);
				}
				
				if(connectingNode.isConnected()){
					
					if(getAssociatedNode() instanceof JXTARendezvousSuperPeer){
						System.out.println("TYPE connecting peer: RdV");
						((JXTARendezvousSuperPeer) connectingNode).connectToWNRdV();
					} else if(getAssociatedNode() instanceof JXTAEdgePeer){
						System.out.println("TYPE connecting peer: EP"); 
							
						//così da connettersi ad un nodo casuale tra quelli statici
						boolean ok_toConnect = false;
						int to_connect = 0;
						Random rand = new Random();
						
						while(!ok_toConnect){
							to_connect = rand.nextInt(connectingNode.rendezvousSP.size()); 
							if ( connectingNode.rendezvousSP.get(to_connect).isConnected() ) {
								connectingNode.connectToRdv(connectingNode.rendezvousSP.get(to_connect));
								ok_toConnect = true;
								
							}
							
							to_connect++;
						}
						
							if (ok_toConnect)
								System.out.println("Node connected correctly");
							else
								System.out.println("Nodo fails to connetect");
						}		
				}
				
			}
		} 
		
	}
}
