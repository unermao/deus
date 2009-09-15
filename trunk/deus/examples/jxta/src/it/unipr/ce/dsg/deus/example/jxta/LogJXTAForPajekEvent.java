package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event print the network topology in <p>Pajek</p> format
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class LogJXTAForPajekEvent extends Event {

	public LogJXTAForPajekEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	} 
	
	@Override
	public void run() throws RunException {

		getLogger().info("##### Network links:");
		Collections.sort(Engine.getDefault().getNodes());
		getLogger().info("*Vertices " + Engine.getDefault().getNodes().size());
		ArrayList<Integer> peer = new ArrayList<Integer>();
		int k=1;
		for(Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); k++){
			
			JXTAEdgePeer n = (JXTAEdgePeer) it.next();
			if (n instanceof JXTARendezvousSuperPeer ){
				if ( ((JXTARendezvousSuperPeer) n).isPersistentRdV() )
					getLogger().info(k + " \"" + n.JXTAID + "\" ic Green bc Black");
				else
					getLogger().info(k + " \"" + n.JXTAID + "\" ic Yellow bc Black");
			} else if (n instanceof JXTAEdgePeer){
				getLogger().info(k + " \"" + n.JXTAID + "\" ic Red bc Black");
			}
			
			peer.add(n.JXTAID);
		}
		
		
		
		getLogger().info("*Edges");
		
		int q=1;
		for(Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ){
			
			JXTAEdgePeer n = (JXTAEdgePeer) it.next();
			
			if (n instanceof JXTARendezvousSuperPeer ){
				for(int i =0; i < ((JXTARendezvousSuperPeer) n).RPV.size(); i++ ) {
					if (n.JXTAID != ((JXTARendezvousSuperPeer) n).RPV.get(i).JXTAID && peer.indexOf(((JXTARendezvousSuperPeer) n).RPV.get(i).JXTAID) != -1
							&&  peer.indexOf(n.JXTAID) != -1){
						getLogger().info(q + " " + (peer.indexOf(((JXTARendezvousSuperPeer) n).RPV.get(i).JXTAID) + 1) + " " + (peer.indexOf(n.JXTAID) + 1) + " c Black" );
						q++;
					}
				}
					
				for(int i = 0; i < ((JXTARendezvousSuperPeer) n).peer.size(); i++){
					if (n.JXTAID != ((JXTARendezvousSuperPeer) n).peer.get(i).JXTAID && peer.indexOf(((JXTARendezvousSuperPeer) n).peer.get(i).JXTAID) != -1
							&& peer.indexOf(n.JXTAID) != -1){
						getLogger().info(q + " " + (peer.indexOf(((JXTARendezvousSuperPeer) n).peer.get(i).JXTAID) + 1) + " " + (peer.indexOf(n.JXTAID) + 1) + " c Black" );
						q++;
					}
				}
			} else if (n instanceof JXTAEdgePeer){
				if (n.connectedRdV != null && peer.indexOf(n.JXTAID) != -1 && peer.indexOf(n.connectedRdV.JXTAID) != -1){
					getLogger().info(q + " " + (peer.indexOf(n.JXTAID) + 1) + " " + (peer.indexOf(n.connectedRdV.JXTAID) + 1) + " c Black"  );
					q++;
				}
			}
			
		}	

	}

}
