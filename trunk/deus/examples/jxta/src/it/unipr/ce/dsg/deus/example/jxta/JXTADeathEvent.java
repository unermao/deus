package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the death of a simulation node. 
 * During the execution of the event the specified node will 
 * be killed or, in case nothing is specified, a random node 
 * will be killed.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */


public class JXTADeathEvent extends NodeEvent {

	private Node nodeToKill = null;
	
	private String EP = "JXTAPeer";
	private String RdV = "JXTARdVPeer";
	private String TYPE = "typeOfPeer";
	
	
	private boolean typeRdV;
	
	
	//Number of candidates to kill
	private int numOfCandRdV;
	private int numOfCandEP;
	
	public JXTADeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		
		if(params.getProperty(TYPE) == null)
			throw new InvalidParamsException(TYPE + " param is expected.");
		
			this.typeRdV = params.getProperty(TYPE).contentEquals(RdV);

	}

	public Object clone() {
		JXTADeathEvent clone = (JXTADeathEvent) super.clone();
		clone.nodeToKill = null;
		clone.numOfCandEP = 0;
		clone.numOfCandRdV = 0;
		return clone;
	}
	
	/**
	 * 
	 * Set the node to kill at "nodeToKill"
	 * 
	 * @param nodeToKill
	 */
	public void setNodeToKill(Node nodeToKill) {
		this.nodeToKill = nodeToKill;
	}
	
	@Override
	public void run() throws RunException {
		
		JXTAEdgePeer disconnectedNode = (JXTAEdgePeer) nodeToKill;
		
		if(disconnectedNode == null)
		{

			this.countNumOfCandidates();
			
			//if there aren't peer of the type request
			if ( (this.typeRdV && this.numOfCandRdV < 1) || (!this.typeRdV && this.numOfCandEP < 1) ){
				return;
			}
			
			boolean ok_toKill = false;
			while(!ok_toKill){
				
				int initialized_nodes = Engine.getDefault().getNodes().size();
				int random_node_to_kill = Engine.getDefault().getSimulationRandom().nextInt(initialized_nodes);
				disconnectedNode = (JXTAEdgePeer) Engine.getDefault().getNodes().get(random_node_to_kill);
				
				if (this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_kill) instanceof JXTARendezvousSuperPeer) {
				
					if(!((JXTARendezvousSuperPeer)Engine.getDefault().getNodes().get(random_node_to_kill)).persistent_RdV){
					
						if(disconnectedNode != null && disconnectedNode.isConnected())
							ok_toKill = true;
				
					}
				}
				else if(!this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_kill) instanceof JXTAEdgePeer){

					if(disconnectedNode != null && disconnectedNode.isConnected()){
						disconnectedNode.disconnectJXTANode();
						ok_toKill = true;
					}
					
				} 
		
			}
		}
		
		disconnectedNode.deathJXTANode();
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
