package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class JXTADeathEvent extends Event {

	private Node nodeToKill = null;
	
	public JXTADeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		JXTAEdgePeer disconnectedNode = (JXTAEdgePeer) nodeToKill;
		if(disconnectedNode == null)
		{
			Random random = new Random();
			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node = random.nextInt(initialized_nodes);
			disconnectedNode = (JXTAEdgePeer) Engine.getDefault().getNodes().get(random_node);
		}
		
		//disconnectedNode.deathJXTANode();
	}

}
