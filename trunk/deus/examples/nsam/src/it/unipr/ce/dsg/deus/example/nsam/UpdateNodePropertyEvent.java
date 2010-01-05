package it.unipr.ce.dsg.deus.example.nsam;


import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.nsam.NsamPeer;




public class UpdateNodePropertyEvent extends NodeEvent{

	
		 public UpdateNodePropertyEvent (String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
	 }
	 
			public Object clone(){
				UpdateNodePropertyEvent clone = (UpdateNodePropertyEvent) super.clone();
				return clone;
			}
		 
		 
	 public void run() throws RunException{
		 
		getLogger().fine("## updating properties for nodes...");
		if (!Engine.getDefault().getNodes().isEmpty())
			for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
			{
				NsamPeer updatingNode  = (NsamPeer)Engine.getDefault().getNodes().get(i);
				if (updatingNode.getId().equals("pcNode")){
				updatingNode.setBattery(updatingNode.getBattery()-5);
				if (updatingNode.getBattery()<5){
					updatingNode.setConnected(false);	
					updatingNode.resetNeighbors();
					System.out.println("Batteria esaurita, il nodo si disconnette!!!");
					getLogger().fine("## low battery!!! Node disconnection...");
				}
				}
			}
		//TODO qua puo' starci anche un periodico aggiornamento della cache
	 }
}
	 


	
	
	
	
	

