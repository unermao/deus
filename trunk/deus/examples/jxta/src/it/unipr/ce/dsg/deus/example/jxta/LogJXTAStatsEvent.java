package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This class is used to print for Rendezvous Super Peer: the number of Well-Known Rendezvous,
 * the dimension of RPV, the connected Edge Peer and the number of resposibility Advertisement;
 * for Edge Peer: the number of Well-Known Rendezvous, the Advertisement on cache, total number of
 * research and hits. Finally print statistics.
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 */

public class LogJXTAStatsEvent extends Event {
	
	public int totalHits = 0;
	public int totalSearch = 0;
	public int numPersistantRdV = 0;
	public int numRdV = 0;
	public int numEP = 0;
	
	public LogJXTAStatsEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException{
		
	}
	
	@Override
	public void run() throws RunException {
		
		AutomatorLogger a = new AutomatorLogger("./temp/JXTAlogger");
		ArrayList<LoggerObject> fileValue = new ArrayList<LoggerObject>();
		int numNodes = Engine.getDefault().getNodes().size();
		
		JXTAEdgePeer n = null;
		
		for(Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ){
			n = (JXTAEdgePeer) it.next();
			//System.out.println("nel ciclo ");
			if (n instanceof JXTARendezvousSuperPeer ){
				//fileValue.add(new LoggerObject("name", value));
				if ( ( (JXTARendezvousSuperPeer) n).isPersistantRdV()) {
					//System.out.println("PERS");
					numPersistantRdV++;
					fileValue.add(new LoggerObject("peer type of Persistant Rendezvous Super Peer ", n.JXTAID));
				} else {
					//System.out.println("NON perst");
					numRdV++;
					fileValue.add(new LoggerObject("peer type of Rendezvous Super Peer ", n.JXTAID));
				}
				
				fileValue.add(new LoggerObject("\tWell-know Rendezvous : ", n.rendezvousSP.size()));
				//printWNRdV(n);
				fileValue.add(new LoggerObject("\tDimensions of Rendezvous Peer View : ", ( (JXTARendezvousSuperPeer) n).dimRPV()));
				//rintRPV( (JXTARendezvousSuperPeer) n );
				fileValue.add(new LoggerObject("\tEP connected : ", ( (JXTARendezvousSuperPeer) n).dimEP()));
				//printConnectedEP( (JXTARendezvousSuperPeer) n );
				fileValue.add(new LoggerObject("\tNumber of responsibility Advertisement : ", ( (JXTARendezvousSuperPeer) n).dimRespAdv()));
				//printResponsibilityAdv((JXTARendezvousSuperPeer) n);
				
			}
			else if (n instanceof JXTAEdgePeer){
				numEP++;
				//System.out.println("Edge");
				fileValue.add(new LoggerObject("peer type of Edge Peer ", n.JXTAID));
				fileValue.add(new LoggerObject("\tWell-know Rendezvous : ", n.rendezvousSP.size()));
				//printWNRdV(n);
				fileValue.add(new LoggerObject("\tAdvertisement on cache : ", n.cacheAdv.size()));
				//printAdvOnCache(n);
				
				totalSearch = totalSearch + n.totalQuery;
				totalHits = totalHits + n.queryHit;
				fileValue.add(new LoggerObject("\tNumber of search : ", n.totalQuery));
				fileValue.add(new LoggerObject("\tNumber of hits : ", n.queryHit));
				
			}
			
			
			System.out.println(n.JXTAID);
		}
		
		
		fileValue.add(new LoggerObject("\n\nTotal number of Peer: ", numNodes));
		fileValue.add(new LoggerObject("\tPersistant Rendezvous Super Peer: ", numPersistantRdV));
		fileValue.add(new LoggerObject("\tRendezvous Super Peer: ", numRdV));
		fileValue.add(new LoggerObject("\tEdge Peer: ", numEP));
		fileValue.add(new LoggerObject("\tTotal number of search: ", totalSearch));
		fileValue.add(new LoggerObject("\tTotal number of hits: ", totalHits));
		fileValue.add(new LoggerObject("\tTotal number of miss: ", totalSearch - totalHits));
		if (totalSearch != 0)
			fileValue.add(new LoggerObject("\tPercent of hit: ", (totalHits*100)/totalSearch));
		
		
		a.write(Engine.getDefault().getVirtualTime(), fileValue);
		


	}

	/**
	 * This is the old version of DEUS Log
	 * @throws RunException
	 */
	public void oldrun() throws RunException{
		// TODO Auto-generated method stub
		
		getLogger().info("##### JXTAPeer stats:");
		
		Collections.sort(Engine.getDefault().getNodes());
		
		getLogger().info("Total nodes: " + Engine.getDefault().getNodes().size());
		
		for(Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext(); ) {
			
			//Peer n = (JXTAEdgePeer) it.next();
			JXTAEdgePeer n = (JXTAEdgePeer) it.next();
			
			if (n instanceof JXTARendezvousSuperPeer ){
				//n = (JXTARendezvousSuperPeer) n;
				if ( ( (JXTARendezvousSuperPeer) n).isPersistantRdV()) {
					numPersistantRdV++;
					getLogger().info("peer " + n.JXTAID + " type of Persistant Rendezvous Super Peer (on " + ( (JXTARendezvousSuperPeer) n).num_perst + " )");
				} else {
					numRdV++;
					getLogger().info("peer " + n.JXTAID + " type of Rendezvous Super Peer");
				}
				
				getLogger().info("\tWell-know Rendezvous : " + n.rendezvousSP.size());
				printWNRdV(n);
				getLogger().info("\tDimensions of Rendezvous Peer View : " + ( (JXTARendezvousSuperPeer) n).dimRPV());
				printRPV( (JXTARendezvousSuperPeer) n );
				getLogger().info("\tEP connected : " + ( (JXTARendezvousSuperPeer) n).dimEP());
				printConnectedEP( (JXTARendezvousSuperPeer) n );
				getLogger().info("\tNumber of responsibility Advertisement :" + ( (JXTARendezvousSuperPeer) n).dimRespAdv() );
				printResponsibilityAdv((JXTARendezvousSuperPeer) n);
				
			}
			else if (n instanceof JXTAEdgePeer){
				numEP++;
				
				getLogger().info("peer " + n.JXTAID + " type of Edge Peer");
				
				getLogger().info("\tWell-know Rendezvous : " + n.rendezvousSP.size()); 
				printWNRdV(n);
				getLogger().info("\tAdvertisement on cache : " + n.cacheAdv.size());
				printAdvOnCache(n);
			}
			
		}
		
		getLogger().info("\n\nTotal number of: \n\tPersistant Rendezvous Super Peer: " + numPersistantRdV);
		getLogger().info("\tRendezvous Super Peer: " + numRdV);
		getLogger().info("\tEdge Peer: " + numEP);
	}

	
	//Stampa tutti i RdV
	private void printWNRdV(JXTAEdgePeer e){
		
		for (int i = 0; i < e.rendezvousSP.size(); i++){
			getLogger().info("\t\t " + i + " : " + e.rendezvousSP.get(i).JXTAID);
		}
		
	}
	
	private void printRPV(JXTARendezvousSuperPeer r) {
		
		for(int i = 0; i < r.RPV.size(); i++){
			getLogger().info("\t\t " + i + " : " + r.RPV.get(i).JXTAID);
		}
		
	}
	
	private void printConnectedEP(JXTARendezvousSuperPeer r) {
		
		for(int i=0; i < r.peer.size(); i++){
			getLogger().info("\t\t " + i + " : " + r.peer.get(i).JXTAID);
		}
		
	}
	
	private void printResponsibilityAdv ( JXTARendezvousSuperPeer r) {
		
		Set<Integer> key_set = r.respAdv.keySet();
		Iterator<Integer> iter = key_set.iterator();
		for(int i = 0; i < r.respAdv.size(); i++ ) {
			int res_key = iter.next();
			getLogger().info("\t\t " + i + ", advertisement : " + r.respAdv.get(res_key).id + " owned by " + r.respAdv.get(res_key).ep.JXTAID);
		}
		
	}
	
	
	private void printAdvOnCache(JXTAEdgePeer e){
		
		Set<Integer> key_set = e.cacheAdv.keySet();
		Iterator<Integer> iter = key_set.iterator();
		int numOfMyAdv = 0;
		for(int i = 0; i < e.cacheAdv.size(); i++ ) {
			int res_key = iter.next();
			getLogger().info("\t\t " + i + ", advertisement : " + e.cacheAdv.get(res_key).id + " owned by " + e.cacheAdv.get(res_key).ep.JXTAID);
			if (e.cacheAdv.get(res_key).ep == e){
				numOfMyAdv++;
			}
			
			getLogger().info("\n\t\t Total number of peer Advertisement " + numOfMyAdv);
			
		}
		
	}
}
