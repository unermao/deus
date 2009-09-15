package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the disconnection of a simulation node. 
 * During the execution of the event the specified node will be 
 * disconnected or, in case nothing is specified, a random node 
 * will be disconnected.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class JXTADisconnectionEvent extends NodeEvent {

	private String EP = "JXTAPeer";
	private String RdV = "JXTARdVPeer";
	private String TYPE = "typeOfPeer";
	
	private boolean typeRdV; 
	//Number of candidates to Disconnect
	private int numOfCandRdV;
	private int numOfCandEP;
	
	public JXTADisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
			
		if(params.getProperty(TYPE) == null)
			throw new InvalidParamsException(TYPE + " param is expected.");

			this.typeRdV = params.getProperty(TYPE).contentEquals(RdV);
		
	}

	public Object clone(){
		JXTADisconnectionEvent clone = (JXTADisconnectionEvent) super.clone();
		clone.numOfCandEP = 0;
		clone.numOfCandRdV = 0;
		return clone;
	}
	
	@Override
	public void run() throws RunException {
		
		this.countNumOfCandidates();
		
		//if there aren't peer of the type request
		if ( (this.typeRdV && this.numOfCandRdV < 1) || (!this.typeRdV && this.numOfCandEP < 1) ){
			return;
		}
		
		boolean ok_toDisconnect = false;
		while(!ok_toDisconnect){
			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node_to_disc = Engine.getDefault().getSimulationRandom().nextInt(initialized_nodes);
			
			if (this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_disc) instanceof JXTARendezvousSuperPeer) {
				
				if (!((JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(random_node_to_disc)).persistent_RdV){
					
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
	
	//Count number of candidate for both type of peer
	private void countNumOfCandidates(){
		for (int i = 0; i < Engine.getDefault().getNodes().size(); i++){
			if (Engine.getDefault().getNodes().get(i) instanceof JXTARendezvousSuperPeer && !((JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(i)).persistent_RdV
					&& Engine.getDefault().getNodes().get(i) != null && ((JXTARendezvousSuperPeer) Engine.getDefault().getNodes().get(i)).isConnected()){
				this.numOfCandRdV++;
			}
			
			else if (Engine.getDefault().getNodes().get(i) instanceof JXTAEdgePeer && 
				 Engine.getDefault().getNodes().get(i) != null && ((JXTAEdgePeer) Engine.getDefault().getNodes().get(i)).isConnected()) {
				this.numOfCandEP++;
			}
		}
	}

}
