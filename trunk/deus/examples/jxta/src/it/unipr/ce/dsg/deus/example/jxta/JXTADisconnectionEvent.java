package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event represents the disconnection of a simulation node. During the execution of
 * the event the specified node will be disconnected or, in case nothing is specified,
 * a random node will be disconnected.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class JXTADisconnectionEvent extends NodeEvent {

	private String EP = "JXTAPeer";
	private String RdV = "JXTARdVPeer";
	private String TYPE = "typeOfPeer";
	
	
	private boolean typeRdV; 
	
	public JXTADisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
			
		if(params.getProperty(TYPE) == null)
			throw new InvalidParamsException(TYPE + " param is expected.");
		
			System.out.println("AVUTO : " + params.getProperty(TYPE));
			this.typeRdV = params.getProperty(TYPE).contentEquals(RdV);
			if(this.typeRdV)
				System.out.println("RdV");
			else if (!this.typeRdV)
				System.out.println("EP");
		
	}

	public Object clone(){
		JXTADisconnectionEvent clone = (JXTADisconnectionEvent) super.clone();
		return clone;
	}
	
	@Override
	public void run() throws RunException {
		
		System.out.println("DISCONNECTION EVENT");
		
		Random random = new Random();
		
		boolean ok_toDisconnect = false;
		while(!ok_toDisconnect){
			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node_to_disc = random.nextInt(initialized_nodes);
			
			if (this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_disc) instanceof JXTARendezvousSuperPeer) {
				
				if (!((JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(random_node_to_disc)).persistant_RdV){
					
					JXTARendezvousSuperPeer disconnectedNode = (JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(random_node_to_disc);
					if(disconnectedNode != null && disconnectedNode.isConnected()){
						disconnectedNode.disconnectRdV();	
						ok_toDisconnect = true;
					}
				
				}
			}
			else if(!this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_disc) instanceof JXTAEdgePeer){
				JXTAEdgePeer disconnectedNode = (JXTAEdgePeer) Engine.getDefault().getNodes().get(random_node_to_disc);
				if(disconnectedNode != null && disconnectedNode.isConnected()){
					disconnectedNode.disconnectJXTANode();
					ok_toDisconnect = true;
				}
				
			} 
		}
		
	}

}
