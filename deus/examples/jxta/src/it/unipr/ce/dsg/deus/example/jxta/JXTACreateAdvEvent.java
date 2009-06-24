package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class JXTACreateAdvEvent extends NodeEvent {

	public JXTACreateAdvEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
		System.out.println("In create-AdvEvent");
		//initialize();
	}
	
//	public void initialize() throws InvalidParamsException {
//		
//	}

	
	public Object clone() {
		JXTACreateAdvEvent clone = (JXTACreateAdvEvent) super.clone();
		return clone;
	}
	
	public void run() throws RunException {
		System.out.println("RUN Adv_EVENT " + this);
		// TODO Auto-generated method stub
		JXTAEdgePeer aa = (JXTAEdgePeer) Engine.getDefault().getNodes().get(0);
		aa.ciao();
		System.out.println("A:: "+ associatedNode);
		JXTAEdgePeer creatingAdvNode = (JXTAEdgePeer) getAssociatedNode();
		if(creatingAdvNode == null)
			System.out.println("NULLO");

	//	creatingAdvNode.createAdvertisement();
		creatingAdvNode.ciao();
		System.out.println("In create-POST");
		
	}

}
