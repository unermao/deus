package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event print the network topology in <p>nwb</p> format.
 *  
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class LogJXTAForNwbEvent extends Event {

	
	public LogJXTAForNwbEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {


		HashMap<String, Integer> app = new HashMap<String, Integer>();
		
		getLogger().info("##### JXTA Script for nwb:" + Engine.getDefault().getVirtualTime());
		
		Collections.sort(Engine.getDefault().getNodes());
		
		getLogger().info("#NWB Data for Streaming P2P\n *Nodes " + Engine.getDefault().getNodes().size() + "\n id*int label*string color*string ");
		
		for (int i = 0; i < Engine.getDefault().getNodes().size(); i++){
			
			JXTAEdgePeer n = (JXTAEdgePeer) Engine.getDefault().getNodes().get(i);
			
			if (n instanceof JXTARendezvousSuperPeer){
				if (((JXTARendezvousSuperPeer) n).persistent_RdV){
					getLogger().info((i+1) + " \"" + n.JXTAID + "\" \"green\" ");
					
				} else {
					getLogger().info((i+1) + " \"" + n.JXTAID + "\" \"yellow\" ");
				}
			} else if (n instanceof JXTAEdgePeer){
				
				getLogger().info((i+1) + " \"" + n.JXTAID + "\" \"red\" ");
				
			}
			
			app.put(Integer.toString(n.JXTAID), i+1);
			
		}
		
		getLogger().info("*DirectedEdges\n source*int target*int directed*string color*string");
		
		for(int i = 0; i < Engine.getDefault().getNodes().size(); i++){
			int control = -1;
			JXTAEdgePeer n = (JXTAEdgePeer) Engine.getDefault().getNodes().get(i);
			
			if (n instanceof JXTARendezvousSuperPeer){
				for(int j = 0; j < ((JXTARendezvousSuperPeer) n).RPV.size(); j++){
					if ( app.get(Integer.toString( ( (JXTARendezvousSuperPeer) n).RPV.get(j).JXTAID )) != null) {
						
						if ( ( (JXTARendezvousSuperPeer) n).RPV.get(j) != null && control != app.get(Integer.toString( ((JXTARendezvousSuperPeer) n).RPV.get(j).JXTAID )) && 
								( (JXTARendezvousSuperPeer) n).RPV.get(j).JXTAID != n.JXTAID){
							getLogger().info( app.get(Integer.toString( ( (JXTARendezvousSuperPeer) n).RPV.get(j).JXTAID ) )  + " " + 
									app.get(Integer.toString(n.JXTAID)) + " \"true\" \"blue\" " );

						}
						
						control = app.get( Integer.toString( ((JXTARendezvousSuperPeer) n).RPV.get(j).JXTAID ) );
												
					}
							
				}

				//Connection with EP
				for(int j = 0; j < ((JXTARendezvousSuperPeer) n).peer.size(); j++){
					if ( app.get(Integer.toString( ( (JXTARendezvousSuperPeer) n).peer.get(j).JXTAID )) != null) {
						
						if ( ( (JXTARendezvousSuperPeer) n).peer.get(j) != null && control != app.get(Integer.toString( ((JXTARendezvousSuperPeer) n).peer.get(j).JXTAID )) && 
								( (JXTARendezvousSuperPeer) n).peer.get(j).JXTAID  != n.JXTAID){
							getLogger().info(app.get(Integer.toString( ( (JXTARendezvousSuperPeer) n).peer.get(j).JXTAID ))  + " " + 
									app.get(Integer.toString(n.JXTAID)) + " \"true\" \"violet\" " );

						}
						
						control = app.get( Integer.toString( ((JXTARendezvousSuperPeer) n).peer.get(j).JXTAID ) );
												
					}
							
				}
				
			} else if (n instanceof JXTAEdgePeer){
				
				if (n.connectedRdV != null){
					if (app.get(Integer.toString( n.connectedRdV.JXTAID ) ) != null ){
						if (control != app.get( Integer.toString( n.connectedRdV.JXTAID ) )){
							getLogger().info(app.get (Integer.toString(n.connectedRdV.JXTAID)) + " " + app.get(Integer.toString(n.JXTAID)) + 
									" \"true\" \"blue\" ");
							
						}
						
						control = app.get((Integer.toString( n.connectedRdV.JXTAID )));
					}
				}
			}
		}
		
	}
	
}
