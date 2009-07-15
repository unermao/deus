package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * JXTA Edge Peers have a list of well-known Rendezvous and a Rendezvous to connect.
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class JXTAEdgePeer extends Peer {
	
	
	public ArrayList<JXTARendezvousSuperPeer> rendezvousSP = new ArrayList<JXTARendezvousSuperPeer>();
	public int JXTAID;
	public int keyspace;
	public HashMap<Integer, IdAdv> cacheAdv = new HashMap<Integer, IdAdv>();
	public JXTARendezvousSuperPeer connectedRdV = null;
	public int queryHit = 0;
	public int totalQuery = 0;
	
	public class IdAdv{
		public int id;
		public JXTAAdvertisement adv;
		public JXTAEdgePeer ep;
		
		public IdAdv(int i, JXTAAdvertisement a, JXTAEdgePeer e){
			id = i;
			adv = a;
			ep = e;
		}
	}
	
	public JXTAEdgePeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);

	}

	public Object clone() {
		JXTAEdgePeer clone = (JXTAEdgePeer) super.clone();
		clone.rendezvousSP = new ArrayList<JXTARendezvousSuperPeer>();
		clone.cacheAdv = new HashMap<Integer, IdAdv>();

		clone.connectedRdV = null;
		
		clone.keyspace = Engine.getDefault().getKeySpaceSize();
		clone.JXTAID = Engine.getDefault().generateKey();
		
		clone.queryHit = 0;
		clone.totalQuery = 0;
		
		//System.out.println("END CLONE ( " + clone.JXTAID + " spaz " + clone.keyspace);
		return clone;
	}

	/**
	 * Add "rdv" to list of well-known rendezvous
	 * @param rdv
	 */
	public void listOfRdv(JXTARendezvousSuperPeer rdv){
		if(!this.rendezvousSP.contains(rdv))
			this.rendezvousSP.add(rdv);
		//System.out.println("NUMBER OF well-known RdV : " + this.rendezvousSP.size());
	}
	
	/**
	 * Connect to "rdv" and if "rdv" is a Persistant Rendezvous ask him to an another Rendezvous 
	 * @param rdv
	 */
	public void connectToRdv(JXTARendezvousSuperPeer rdv){
		if(rdv != null){
			if(rdv.isPersistantRdV()){
				//System.out.print("REQUEST to Perst_RdV: " + rdv.JXTAID);
				JXTARendezvousSuperPeer to_conn = rdv.requestRdVToConnect();
				this.connectedRdV = to_conn;
				
				//System.out.println("RESPONSE at request of RdV to connect " + this.connectedRdV.JXTAID);
			}
			else{
				this.connectedRdV = rdv;
				
			}
			
			this.connectedRdV.addEP(this);
			//System.out.println("EP " + this.JXTAID + " connected to " + this.connectedRdV.JXTAID);
		}
	}
	/**
	 * Inform connected Rendezvous of his disconnection 
	 */
	public void disconnectJXTANode(){
		
		this.setConnected(false);
		
		//Occorre portare lo stato di tutti gli Adv in cache a non pubblicato così da ripubblicarli quando ci si riconnette
		Set<Integer> key_set = this.cacheAdv.keySet();
		Iterator<Integer> key = key_set.iterator();
		for(int i=0; i < this.cacheAdv.size(); i++){
			this.cacheAdv.get(key.next()).adv.published = false;
			
		}
		//System.out.println("SPUBBLICATE tutte le risorse");
		//System.out.println("DISCONNECTION of: " + this.JXTAID);
		if(this.connectedRdV != null)
			this.connectedRdV.infoDisconnection(this);
		
	}
	
	/**
	 * Death from Engine
	 */
	public void deathJXTANode(){
		
		this.setConnected(false);
		int my_pos = Engine.getDefault().getNodes().indexOf(this);
		if(my_pos > -1) //quindi sono ancora nell'Engine
			Engine.getDefault().getNodes().remove(my_pos);
		//System.out.println("DEATH of: " + this.JXTAID);
		
	}
	
	/**
	 * Create a new Advertisement and save on cache
	 */
	//creazione nuovo adv (casuale) ed inserimento nella lista
	public void createAdvertisement(){
 
		int random = Engine.getDefault().generateResourceKey();		
		
		JXTAAdvertisement adv = new JXTAAdvertisement(random);
		this.saveAdvertisement(random, adv, this);
		
		//System.out.println("RESOURCE CREATED " + adv.JXTAID + " di "+ this.JXTAID + " (owned " + this.cacheAdv.size());

	}
	
	/**
	 * Research for "advID" on cache, on connected Rendezvous and if it is online to another 
	 * well-known Rendezvous 
	 * @param advID
	 */
	//ricerca di un adv. Si deve ottenere l'adv cercato (dall'EP che lo possiede )
	public void searchAdvertisement(int advID) {
		this.totalQuery++;
		//System.out.println("DISCOVERY by peer "  + this.JXTAID + " for RESOURCE " + advID + " num query " + this.totalQuery);
		
		
		JXTAAdvertisement adv = null;
		boolean find = false;	
		
		//Prima ricerca nella cache locale
		if(this.cacheAdv.containsKey(advID)){
			//System.out.println("Resource " + advID + " on cache ");
			adv = this.cacheAdv.get(advID).adv;
			find = true;
		}
		
		else{
			
			boolean to_search = false;
			
			if(this.connectedRdV != null && this.connectedRdV.isConnected()){
				//System.out.println("RESEARCH on RdV " + this.connectedRdV.JXTAID + " connected");
				find = this.connectedRdV.requestAdv(this, advID);
				to_search = true;
			}
			//ricerca per ogni rdv
			else{
				//System.out.println("RICERCA all'ESTERO");
				for(int i=0; i<this.rendezvousSP.size() && to_search != true; i++){
				
					if(this.rendezvousSP.get(i) != null && this.rendezvousSP.get(i).isConnected()){
						//System.out.println("RESEARCH on RdV " + this.rendezvousSP.get(i).JXTAID + " foreing");
						to_search = true;
						find = this.rendezvousSP.get(i).requestAdv(this, advID);
						
						//cambio del RdV cui si è connessi ed eliminazione di quello precedente se è null
						JXTARendezvousSuperPeer newConRdV = this.rendezvousSP.get(i);
						if(this.connectedRdV == null){
							this.rendezvousSP.remove(this.connectedRdV);
						}

						this.connectToRdv(newConRdV);
	
					}
				}
			}
		}
		
		if(find){
			this.queryHit++;
			//System.out.println("Research for " + advID + " hit (tot " + this.queryHit);
			
		}
		//else
		//	System.out.println("Resource " + advID + " not finded");
			
	}
	
	/**
	 * Send the Advertisement identified by "id" to "dest" if is on cache
	 * @param id
	 * @param dest
	 */
	//invio dell'adv se lo si possiede
	public void sendAdvertisement (int id, JXTAEdgePeer dest) {
		
		JXTAAdvertisement adv = null;
		//System.out.println("Request by " + this.JXTAID + " for adv " + id + " to " + dest.JXTAID);
		if(this.cacheAdv.containsKey(id)){
			adv = this.cacheAdv.get(id).adv;
			//System.out.println("Sending Adv " + id + " to node " + dest.JXTAID);
			dest.saveAdvertisement(id, adv, this);
		}
			
	}
	
	/**
	 * Save "adv" on cache with id "id" and owned "epProp"
	 * @param id
	 * @param adv
	 * @param epProp
	 */
	public void saveAdvertisement (int id, JXTAAdvertisement adv, JXTAEdgePeer epProp){
		IdAdv newAdv = new IdAdv(id, adv, epProp); 
		//System.out.println("SAVING on cache " + this.JXTAID + " for Adv " + id + " owned by " + epProp.JXTAID);
		this.cacheAdv.put(id, newAdv);
		
	}

	/**
	 * Publish all Advertisemnt on cache who aren't already published
	 */
	//pubblicazione di un nuovo adv su un rdv(SRDI service)(asincorno o periodico vedendo quelli non pubblicati)
	public void publishAdvertisement () {
		Set<Integer> s_it= this.cacheAdv.keySet();
		Iterator<Integer> it = s_it.iterator();

		//System.out.println("STARTING of publishing phase for " + this.JXTAID);
		for(int i=0; i<this.cacheAdv.size(); i++){
			int res_key = it.next();
			//System.out.println("Try to PUBBLISH of resource number: " + res_key);
			
			if(!this.cacheAdv.get(res_key).adv.published){
				//tentativo di trovare un RdV su cui pubblicare
				boolean find_toPub = false;
				
				if(this.connectedRdV != null && this.connectedRdV.isConnected()){
					//si prova prima sul RdV su cui si è connessi
					this.cacheAdv.get(res_key).adv.published = true;
					this.connectedRdV.receiveAdvIndex(this, this.cacheAdv.get(res_key).adv.JXTAID);
					find_toPub = true;
				}
				else {
					//ricerca per ogni rdv
					for(int j=0; j<this.rendezvousSP.size() && find_toPub != true; j++){
						if(this.rendezvousSP.get(j) != null && this.rendezvousSP.get(j).isConnected()){
							this.cacheAdv.get(res_key).adv.published = true;

							this.rendezvousSP.get(j).receiveAdvIndex(this, this.cacheAdv.get(res_key).adv.JXTAID);
							
							find_toPub = true;
						
							//cambio del RdV cui si è connessi ed eliminazione di quello precedente se è null
							JXTARendezvousSuperPeer newConRdV = this.rendezvousSP.get(j);
							if(this.connectedRdV == null){
								this.rendezvousSP.remove(this.connectedRdV);
							}

							this.connectToRdv(newConRdV);
						}
					}
				}
				//if(find_toPub){
				//	System.out.println("published resource " + this.cacheAdv.get(res_key).adv.JXTAID);
				//} else {
				//	System.out.println("nothing to publish (no RdV)");
				//}
			}
		}		
		
	}
}
