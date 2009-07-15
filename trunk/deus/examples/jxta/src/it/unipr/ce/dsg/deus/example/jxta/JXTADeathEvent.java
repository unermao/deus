package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event represents the death of a simulation node. During the execution of
 * the event the specified node will be killed or, in case nothing is specified,
 * a random node will be killed.
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
	
	
	public JXTADeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
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

	public Object clone() {
		JXTADeathEvent clone = (JXTADeathEvent) super.clone();
		clone.nodeToKill = null;
		return clone;
	}
	
	public void setNodeToKill(Node nodeToKill) {
		this.nodeToKill = nodeToKill;
	}
	
	@Override
	public void run() throws RunException {
		System.out.println("DEATH EVENT");
		
		JXTAEdgePeer disconnectedNode = (JXTAEdgePeer) nodeToKill;
		
		if(disconnectedNode == null)
		{
			System.out.println("Discovery node to kill");
			boolean ok_toKill = false;
			Random random = new Random();
			while(!ok_toKill){
				
				int initialized_nodes = Engine.getDefault().getNodes().size();
				int random_node_to_kill = random.nextInt(initialized_nodes);
				
				disconnectedNode = (JXTAEdgePeer) Engine.getDefault().getNodes().get(random_node_to_kill);
				System.out.println("Try to kill: " + disconnectedNode.JXTAID);
				
				if (this.typeRdV && Engine.getDefault().getNodes().get(random_node_to_kill) instanceof JXTARendezvousSuperPeer) {
				
					if(!((JXTARendezvousSuperPeer)Engine.getDefault().getNodes().get(random_node_to_kill)).persistant_RdV){
					
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

}
