package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * JXTA Rendezvous Super Peers have a list of well-known Rendezvous, 
 * a list of Rendezvous together with divide key-space (RPV: Rendezvous
 * Peer View) and a list of connected Edge Peer. There are fields for
 * identifier of position take up on RPV, distance made by walker. 
 * There are a list to keep all id of responsibility Advertisement.
 * A flag indicate if this Rendezvous is persistent. 
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class JXTARendezvousSuperPeer extends JXTAEdgePeer {
 
	public ArrayList<JXTARendezvousSuperPeer> RPV = new ArrayList<JXTARendezvousSuperPeer>();
	public ArrayList<JXTAEdgePeer> peer = new ArrayList<JXTAEdgePeer>();
	
	private int nDivSpace;
	private int myRespSpace;
	private int walker;
	public HashMap<Integer, IdEP> respAdv = new HashMap<Integer, IdEP>();
	private static final String WALKER = "walker";
	private static final String PERSISTENT = "isPersistent";
	public boolean persistent_RdV = false;

	
	public class IdEP{
		public int id;
		public JXTAEdgePeer ep;
		
		public IdEP(int i, JXTAEdgePeer e){
			id = i;
			ep = e;
		}
	}
	
	public JXTARendezvousSuperPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		
		if(params.getProperty(WALKER) == null)
			throw new InvalidParamsException(WALKER + " param is expected.");
			
		try{
			this.walker = (int) Double.parseDouble(params.getProperty(WALKER));
		} catch (NumberFormatException ne) {
			throw new InvalidParamsException(WALKER + " must be a valid int value.");
		}

		if(params.getProperty(PERSISTENT) != null){
			try{
				this.persistent_RdV = Boolean.parseBoolean(params.getProperty(PERSISTENT));
			} catch (NumberFormatException ne) {
				throw new InvalidParamsException(PERSISTENT + " must be a boolean.");
			}	
		}
			
		
	}

	public Object clone() {
		JXTARendezvousSuperPeer clone = (JXTARendezvousSuperPeer) super.clone();

		clone.respAdv = new HashMap<Integer, IdEP>();
		clone.RPV = new ArrayList<JXTARendezvousSuperPeer>();
		clone.walker = this.walker;
		
		clone.peer = new ArrayList<JXTAEdgePeer>();
		clone.persistent_RdV = this.persistent_RdV;

		return clone;
	}

	/**
	 * 
	 * Return if this Rendezvous is Persistent or not
	 * 
	 * @return
	 */
	public boolean isPersistentRdV(){
		return this.persistent_RdV;
	}
	
	/**
	 * 
	 * Respond to Edge Peer with a random Rendezvous that isn't Persistent 
	 * if it is know
	 * 
	 * @return
	 */
	public JXTARendezvousSuperPeer requestRdVToConnect(){
		
		JXTARendezvousSuperPeer to_connect = this;
		
		//sign RdV that aren't persistent
		ArrayList<JXTARendezvousSuperPeer> rdv = new ArrayList<JXTARendezvousSuperPeer>();
		for (int i = 0; i < this.rendezvousSP.size(); i++) {
			if(this.rendezvousSP.get(i) != null && this.rendezvousSP.get(i).isConnected() &&
					!this.rendezvousSP.get(i).isPersistentRdV())
				rdv.add(this.rendezvousSP.get(i));
			
			//If is null remove from well-known RdV
			if (this.rendezvousSP.get(i) == null){
				this.rendezvousSP.remove(i); //
			}
		}
		
		if(rdv.size() > 0){
			int num_RdV = Engine.getDefault().getSimulationRandom().nextInt(rdv.size());
			to_connect = rdv.get(num_RdV);
			
		}

		return to_connect;
	}
	
	/**
	 * 
	 * Connect this Rendezvous to all well-known Rendezvous that constitute
	 * the Rendezvous Peer View
	 *  
	 */
	public void connectToWNRdV(){
		
		for(int i=0; i<this.rendezvousSP.size(); i++){
			//if there is already connected 
			if(!this.RPV.contains(this.rendezvousSP.get(i)))
				this.RPV.add(this.rendezvousSP.get(i));		
		}
		
		this.nDivSpace = this.RPV.size();
		this.myRespSpace = this.RPV.indexOf(this);

	}
	
	/**
	 * 
	 * Disconnection of Rendezvous from network with corrected procedure 
	 * that notify to all other well-known Rendezvous
	 * 
	 */
	public void disconnectRdV(){
		
		super.disconnectJXTANode();
		this.infoDisconnect();
		
	}
	
	/**
	 * 
	 * Inform for disconnection for purge of him on every Rendezvous
	 * on his Rendezvous Peer View
	 * 
	 */
	public void infoDisconnect(){
		this.RPV.remove(this);
		this.sendDisconnect();
	}
	
	/**
	 * 
	 * Inform all Rendezvous in his RPV of his disconnection
	 * 
	 */
	public void sendDisconnect(){
		for(int i=0; i < this.RPV.size(); i++){
			this.RPV.get(i).receiveDisconnect(this);
		}
	}
	
	/**
	 * 
	 * Remove "rdv" from his RPV if it's inside
	 * 
	 * @param rdv
	 */
	public void receiveDisconnect(JXTARendezvousSuperPeer rdv){
		if(this.RPV.contains(rdv)){
			this.RPV.remove(rdv);
		}
	}
	
	/**
	 * 
	 * Research Advertisement (if necessary with walker) with 
	 * "id" for Edge Peer "sourceReq". The flag "resp" ask 
	 * owner to respond with Advertisement
	 *  
	 * @param id
	 * @param sourceReq
	 * @param resp
	 * @return
	 */
	public JXTARendezvousSuperPeer searchAdvResp(int id, JXTAEdgePeer sourceReq, boolean resp){
		JXTARendezvousSuperPeer respPeer = null;
		JXTAEdgePeer propPeer = null;
		boolean finded = false;
		
		//Update info on space before use
		this.nDivSpace = this.RPV.size();
		if (this.nDivSpace == 0)
			return null;
		int dimDivSpace = Math.round(this.keyspace/this.nDivSpace);//dimensions of fraction space
		int respSpace = (int) Math.floor(id/dimDivSpace);//find responsible id

		//see if responsible is current RdV
		
		if(respSpace == myRespSpace){
			//responsible is current RdV
			respPeer = this;
			
			if(this.respAdv.containsKey(id)){
				
				respPeer = this;
				propPeer = this.respAdv.get(id).ep;
				finded = true;
				//request of response from owner Peer
				if(propPeer != null && propPeer.isConnected()){
					if(resp){
						propPeer.sendAdvertisement(id, sourceReq);
					} 
				}
			}
			else if(resp){
				return null;
			}
		} else {
			//Verify that RdV is again online
			
			//For precision lost on division 
			if(respSpace > (this.RPV.size() - 1))
				respSpace = (this.RPV.size() - 1);
			
			respPeer = this.RPV.get(respSpace);
			
			//Research on Rendezvous
			if(respPeer != null && respPeer.isConnected()){

				if(resp) {
					finded = respPeer.forwardToEP(id, sourceReq);
					
					if(!finded)
						return null;
				} else {
					
					finded = respPeer.hasRespAdv(id);
				}
			}
		}
		//Research with walker
		if (!finded) {
				JXTARendezvousSuperPeer walkerUp = null;
				JXTARendezvousSuperPeer walkerDown = null;
				for(int i=0; i < this.walker && !finded; i++){
					//walker start
					if( ( respSpace + (i+1) ) < this.RPV.size())
						walkerUp = this.RPV.get(respSpace + (i+1));
					if( (respSpace + (i+1)*(-1) ) >= 0 )
						walkerDown = this.RPV.get(respSpace + (i+1)*(-1));
					
					if(walkerUp != null ){
						if (walkerUp.isConnected()){ 
							if(resp){
								finded = walkerUp.forwardToEP(id, sourceReq);
								if(!finded)
									return null;
							} else {
								finded = walkerUp.hasRespAdv(id);
							}
							
							if(finded){
								respPeer = walkerUp;
							}
						}
					}
					 
					if(!finded && walkerDown != null) { 
						if (walkerDown.isConnected() ){
							if(resp){
								finded = walkerDown.forwardToEP(id, sourceReq);
								if(!finded)
									return null;
							} else {
								finded = walkerDown.hasRespAdv(id);
							}
							
							if(finded){
								respPeer = walkerDown;
							}
						}
					}
				}
				
				//if is find by walker is needed of refresh RPV
				if (finded){
					this.testRPV();
					
				}
			
		}
		
		return respPeer;
	}
	
	/**
	 * 
	 * Test of Rendezvous Peer View for give consistency to distributed
	 * hash table. Control if other Rendezvous are online and 
	 * select a random portion of RPV for send it to a given random 
	 * number of Rendezvous.
	 * 
	 */
	public void testRPV(){
		int my_pos = this.RPV.indexOf(this);
		int my_old_pos = my_pos;

		//if i'm not online remove by RPV
		if(!this.isConnected()){
			this.RPV.remove(this);
			my_pos = this.RPV.indexOf(this);
		}
		
		if(this.RPV.size() == 0){
			return;
		}
		if(my_old_pos > 0){
			
			if(this.RPV.get(my_old_pos - 1) == null || !this.RPV.get(my_old_pos - 1).isConnected){
				purge(my_old_pos - 1);
				my_pos = this.RPV.indexOf(this);
				
			}
			
		} else if (this.RPV.get(this.RPV.size() - 1) == null || !this.RPV.get(this.RPV.size() - 1).isConnected){
				purge(this.RPV.size() - 1);
				my_pos = this.RPV.indexOf(this);
				
			}
		
		my_old_pos = my_pos;
		
		if(my_old_pos < (this.RPV.size() - 1) && my_old_pos != -1){
			if(this.RPV.get(my_old_pos +1) == null ||  !this.RPV.get(my_old_pos +1).isConnected){
				purge(my_old_pos + 1);
				my_pos = this.RPV.indexOf(this);
			}
		} else if (this.RPV.get(0) == null || !this.RPV.get(0).isConnected){
				purge(0);
				my_pos = this.RPV.indexOf(this);
		}
		
		this.nDivSpace = this.RPV.size();
		this.myRespSpace = this.RPV.indexOf(this);
		
		//select a portion of RPV and a send it to a given number of Rendezvous  
		int dim_RPV_update = Engine.getDefault().getSimulationRandom().nextInt(this.RPV.size());
		int num_RdV_toUpdate = Engine.getDefault().getSimulationRandom().nextInt(this.RPV.size());
		
		ArrayList<JXTARendezvousSuperPeer> RPV_toSend = new ArrayList<JXTARendezvousSuperPeer>();
		ArrayList<JXTARendezvousSuperPeer> update_RdV = new ArrayList<JXTARendezvousSuperPeer>();
		
		
		//element to transmit
		int toSend;
		for (int i=0; i < dim_RPV_update && i< this.RPV.size(); i++) {
			toSend = Engine.getDefault().getSimulationRandom().nextInt(this.RPV.size());
			RPV_toSend.add(this.RPV.get(toSend));
		}
		
		//selected RdV to inform
		int toUpdate;

		for (int i=0; i < num_RdV_toUpdate; i++) {

			toUpdate = Engine.getDefault().getSimulationRandom().nextInt(this.RPV.size());

			update_RdV.add(this.RPV.get(toUpdate));
		}
		
		this.sendRPV(RPV_toSend, update_RdV);
		
	}
	
	/**
	 * 
	 * Delete element of index "i" from RPV
	 * 
	 * @param i
	 */
	public void purge(int i){
		this.RPV.remove(i);
	}
	
	/**
	 * 
	 * Update RPV with list "new_list" of received RPV portion 
	 * 
	 * @param new_list
	 */
	public void receiveRPVPortion(ArrayList<JXTARendezvousSuperPeer> new_list){
		
		JXTARendezvousSuperPeer peer = null;
		for (int i=0; i < new_list.size(); i++){
			
			peer = new_list.get(i);

			if(!this.RPV.contains(peer)){

				this.RPV.add(peer);
			}
			
			if(!this.rendezvousSP.contains(peer)) { //add to list of well-known rendezvous
				this.rendezvousSP.add(peer);
			}
			
		}
	}
	
	/**
	 * 
	 * Send RPV portion "to_send" to all Rendezvous on "to_update"
	 *  
	 * @param to_send
	 * @param to_update
	 */
	public void sendRPV(ArrayList<JXTARendezvousSuperPeer> to_send, ArrayList<JXTARendezvousSuperPeer> to_update){
		
		JXTARendezvousSuperPeer dest = null;
		for (int i=0; i < to_update.size(); i++){
			
			dest = to_update.get(i);
			dest.receiveRPVPortion(to_send);
		}
		
	}
	
	/**
	 * 
	 * Receive information of disconnection of Edge Peer "del" and delete 
	 * all Advertisement owned by "del". Is the function called by corrected
	 * procedure of Edge Peer disconnection.
	 * 
	 * @param del
	 * 
	 */
	public void infoDisconnection(JXTAEdgePeer del){

		Set<Integer> keys =  respAdv.keySet();

		Iterator<Integer> iter_key =  keys.iterator();

		IdEP element = null;
		for(int i=0; i < respAdv.size(); i++){
			int key = iter_key.next();
			element = respAdv.get(key);
			if(element.ep == this){
				respAdv.remove(key);
			}
		}
		
		if(this.peer.contains(del)){
			this.peer.remove(del);
		}
	}
	
	/**
	 * 
	 * Receive a responsibility Advertisement with "idAdv" and owner 
	 * "prop" and relay to correct Rendezvous based on RPV
	 * 
	 * @param prop
	 * @param idAdv
	 */
	public void receiveAdvIndex(JXTAEdgePeer prop, int idAdv){
		
		JXTARendezvousSuperPeer resp = this.searchAdvResp(idAdv, null, false);
		if(resp != null && resp.isConnected())
			resp.saveIndex(prop, idAdv);
	}
	
	/**
	 * 
	 * Permit at Edge Peer "ric" connected to him to know if there is a
	 * Rendezvous responsible for resource 
	 * 
	 * @param ric
	 * @param idAdv
	 * @return
	 */
	public boolean requestAdv(JXTAEdgePeer ric, int idAdv) {

		JXTARendezvousSuperPeer rdv = this.searchAdvResp(idAdv, ric, true);
		if (rdv != null) {
			return true;
		} else
			return false;
	}
	
	/**
	 * 
	 * Print all responsibility Advertisement
	 * 
	 */
	public void printAllAdv(){
		Set<Integer> key_set = this.respAdv.keySet();
		Iterator<Integer> iter =  key_set.iterator();
		for(int i = 0; i < this.respAdv.size(); i++){
			System.out.println(iter.next());
		}
			
		
	}
	
	/**
	 * 
	 * Save on his respAdv "id" with owner "prop"
	 * 
	 * @param prop
	 * @param id
	 */
	public void saveIndex(JXTAEdgePeer prop, int id){
		IdEP newId = new IdEP(id, prop);
		this.respAdv.put(id, newId);
	}
	

	/**
	 * 
	 * Research of "id" in his responsibility Adv and return
	 * result 
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasRespAdv(int id){
		if(this.respAdv.containsKey(id)){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Request to owner Advertisement of "idAdv" to send Advertisement at 
	 * "des" Edge Peer
	 *  
	 * @param id
	 * @param des
	 * @return
	 */
	public boolean forwardToEP(int id, JXTAEdgePeer des){
		if(this.respAdv.containsKey(id)){
			JXTAEdgePeer propEP = this.respAdv.get(id).ep;
			
			if(propEP != null && propEP.isConnected()){
				propEP.sendAdvertisement(id, des);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * Return the dimension of RPV
	 * 
	 * @return
	 */
	public int dimRPV(){
		return this.RPV.size();
	}
	
	/**
	 * 
	 * Add Edge Peer "e" to list of connected Edge Peers 
	 * if it isn't already inside
	 * 
	 * @param e
	 */
	public void addEP( JXTAEdgePeer e ){
		if(!this.peer.contains(e)){
			this.peer.add(e);
		}
	}
	
	/**
	 * 
	 * Return the number of Edge Peer connected
	 * 
	 * @return
	 */
	public int dimEP(){
		return this.peer.size();
	}
	
	/**
	 * 
	 * Return the number of responsibility Advertisement
	 * 
	 * @return
	 */
	public int dimRespAdv(){
		return this.respAdv.size();
	}
	
}
