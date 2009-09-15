package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the creation of an Advertisement with
 * a random ID.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTACreateAdvEvent extends NodeEvent {

	public JXTACreateAdvEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	
	public Object clone() {
		JXTACreateAdvEvent clone = (JXTACreateAdvEvent) super.clone();
		return clone;
	}
	
	public void run() throws RunException {
		JXTAEdgePeer creatingAdvNode = (JXTAEdgePeer) getAssociatedNode();

		if(creatingAdvNode == null){
			System.out.println("NO PEER ASSOCIATED TO EVENT");
			return;
		}
		if(!Engine.getDefault().getNodes().contains(creatingAdvNode)){
			return;
		}
		creatingAdvNode.createAdvertisement();
		
	}

}
