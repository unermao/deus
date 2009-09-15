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
 * 
 * JXTA Edge Peers is identified by a JXTAID, have a list of 
 * well-known Rendezvous and a Rendezvous to connect.
 * There are fields for count the number of query and query hits.
 * All received created or received Advertisement are saved on cache. 
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

		return clone;
	}

	/**
	 * Add "rdv" to list of well-known rendezvous if isn't already
	 * on cache
	 * 
	 * @param rdv
	 */
	public void listOfRdv(JXTARendezvousSuperPeer rdv){
		if(!this.rendezvousSP.contains(rdv))
			this.rendezvousSP.add(rdv);
	}
	
	/**
	 * Connect to "rdv" and if "rdv" is a Persistent Rendezvous ask him to get 
	 * another Rendezvous
	 *  
	 * @param rdv
	 */
	public void connectToRdv(JXTARendezvousSuperPeer rdv){
		if(rdv != null){
			if(rdv.isPersistentRdV()){

				JXTARendezvousSuperPeer to_conn = rdv.requestRdVToConnect();
				this.connectedRdV = to_conn;

			}
			else{
				this.connectedRdV = rdv;
				
			}
			
			this.connectedRdV.addEP(this);
		}
	}
	/**
	 * Inform connected Rendezvous of his disconnection. Set
	 * all Advertisement published flag to false for execute
	 * a new publish at next connection. 
	 */
	public void disconnectJXTANode(){
		
		this.setConnected(false);
		
		Set<Integer> key_set = this.cacheAdv.keySet();
		Iterator<Integer> key = key_set.iterator();
		for(int i=0; i < this.cacheAdv.size(); i++){
			this.cacheAdv.get(key.next()).adv.published = false;
			
		}

		if(this.connectedRdV != null)
			this.connectedRdV.infoDisconnection(this);
		
	}
	
	/**
	 * Death node with a delete by Engine
	 */
	public void deathJXTANode(){
		
		this.setConnected(false);
		int my_pos = Engine.getDefault().getNodes().indexOf(this);
		if(my_pos > -1) //i'm in Engine
			Engine.getDefault().getNodes().remove(my_pos);
	}
	
	/**
	 * Create a new random Advertisement and save on cache
	 */
	public void createAdvertisement(){
 
		int random = Engine.getDefault().generateResourceKey();		
		
		JXTAAdvertisement adv = new JXTAAdvertisement(random);
		this.saveAdvertisement(random, adv, this);

	}
	
	/**
	 * Research for "advID" on cache, on connected Rendezvous and if 
	 * it isn't online to another well-known Rendezvous. For obtain the 
	 * requested advertisement from owner EdgePeer 
	 *  
	 * @param advID
	 */
	public void searchAdvertisement(int advID) {
		this.totalQuery++;
		
		JXTAAdvertisement adv = null;
		boolean find = false;	
		
		//First research on cache
		if(this.cacheAdv.containsKey(advID)){
			adv = this.cacheAdv.get(advID).adv;
			find = true;
		}
		
		else{
			
			boolean to_search = false;
			
			if(this.connectedRdV != null && this.connectedRdV.isConnected()){
				find = this.connectedRdV.requestAdv(this, advID);
				to_search = true;
			} else{
				//Research on other well known Rendezvous
				for(int i=0; i<this.rendezvousSP.size() && to_search != true; i++){
				
					if(this.rendezvousSP.get(i) != null && this.rendezvousSP.get(i).isConnected()){
						to_search = true;
						find = this.rendezvousSP.get(i).requestAdv(this, advID);
						
						//Change connected Rendezvous and delete from previous if it's null
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
		}
			
	}
	
	/**
	 * Send the Advertisement identified by "id" to "dest" 
	 * Peer if is on cache
	 * 
	 * @param id
	 * @param dest
	 */
	public void sendAdvertisement (int id, JXTAEdgePeer dest) {
		
		JXTAAdvertisement adv = null;
		if(this.cacheAdv.containsKey(id)){
			adv = this.cacheAdv.get(id).adv;
			dest.saveAdvertisement(id, adv, this);
		}
			
	}
	
	/**
	 * Save Advertisement "adv" on cache with id "id" and owner "epProp"
	 * 
	 * @param id
	 * @param adv
	 * @param epProp
	 */
	public void saveAdvertisement (int id, JXTAAdvertisement adv, JXTAEdgePeer epProp){
		IdAdv newAdv = new IdAdv(id, adv, epProp); 
		this.cacheAdv.put(id, newAdv);
		
	}

	/**
	 * Check for publish all Advertisement on cache that aren't already
	 * published: the SRDI (Shared Resource Distributed Index) service.
	 * 
	 */
	public void publishAdvertisement () {
		Set<Integer> s_it= this.cacheAdv.keySet();
		Iterator<Integer> it = s_it.iterator();

		for(int i=0; i<this.cacheAdv.size(); i++){
			int res_key = it.next();
			
			if(!this.cacheAdv.get(res_key).adv.published){
				//try to find a Rendezvous for publish
				boolean find_toPub = false;
				
				if(this.connectedRdV != null && this.connectedRdV.isConnected()){
					//first try on connected Rendezvous
					this.cacheAdv.get(res_key).adv.published = true;
					this.connectedRdV.receiveAdvIndex(this, this.cacheAdv.get(res_key).adv.JXTAID);
					find_toPub = true;
				}
				else {
					//Research on other Rendezvous
					for(int j=0; j<this.rendezvousSP.size() && find_toPub != true; j++){
						if(this.rendezvousSP.get(j) != null && this.rendezvousSP.get(j).isConnected()){
							this.cacheAdv.get(res_key).adv.published = true;

							this.rendezvousSP.get(j).receiveAdvIndex(this, this.cacheAdv.get(res_key).adv.JXTAID);
							
							find_toPub = true;
						
							//Change connected Rendezvous and delete from previous if it's null
							JXTARendezvousSuperPeer newConRdV = this.rendezvousSP.get(j);
							if(this.connectedRdV == null){
								this.rendezvousSP.remove(this.connectedRdV);
							}

							this.connectToRdv(newConRdV);
						}
					}
				}

			}
		}		
		
	}
}
