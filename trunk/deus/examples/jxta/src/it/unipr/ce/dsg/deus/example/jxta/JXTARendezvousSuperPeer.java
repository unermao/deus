package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

/**
 * JXTA Edge Peers have a list of well-known Rendezvous and a Rendezvous to connect.
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 *
 */

public class JXTARendezvousSuperPeer extends JXTAEdgePeer {

	//RdV cui si è connesso dividendo lo spazio delle chiavi
	public ArrayList<JXTARendezvousSuperPeer> RPV = new ArrayList<JXTARendezvousSuperPeer>();
	public ArrayList<JXTAEdgePeer> peer = new ArrayList<JXTAEdgePeer>();
	
	private int nDivSpace;
	private int myRespSpace;
	private int walker;
	public HashMap<Integer, IdEP> respAdv = new HashMap<Integer, IdEP>();
	private static final String WALKER = "walker";
	private static final String PERSISTANT = "num_perst";
	public boolean persistant_RdV = false;
	public int num_perst;
	
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
			this.walker = Integer.parseInt(params.getProperty(WALKER));
		} catch (NumberFormatException ne) {
			throw new InvalidParamsException(WALKER + " must be a valid int value.");
		}
		
		if(params.getProperty(PERSISTANT) != null){
			try{
				this.num_perst = Integer.parseInt(params.getProperty(PERSISTANT));
				this.persistant_RdV = true;
			} catch (NumberFormatException ne) {
				throw new InvalidParamsException(PERSISTANT + " must be a valid int value.");
			}	
		}
			
		
	}

	public Object clone() {
		JXTARendezvousSuperPeer clone = (JXTARendezvousSuperPeer) super.clone();

		clone.respAdv = new HashMap<Integer, IdEP>();
		clone.RPV = new ArrayList<JXTARendezvousSuperPeer>();
		clone.walker = this.walker;
		
		clone.peer = new ArrayList<JXTAEdgePeer>();
		
		clone.num_perst = this.num_perst;
		clone.persistant_RdV = this.persistant_RdV;
		//System.out.println("Created a RdVSP (walker " + this.walker + " )" + this.persistant_RdV);
		//if(this.persistant_RdV)
			//System.out.println("RdV PERSISTANT");

		return clone;
	}

	/**
	 * Return if this Rendezvous is Persistant or not
	 * @return
	 */
	public boolean isPersistantRdV(){
		return this.persistant_RdV;
	}
	
	/**
	 * Respond to EdgePeer with a Rendevouz who isn't Persistant if exist
	 * @return
	 */
	//fa in modo che se conosce qualche RdV normale lo faccia collegare
	public JXTARendezvousSuperPeer requestRdVToConnect(){
		
		JXTARendezvousSuperPeer to_connect = this;
		
		//segna i RdV che non sono Persistant
		ArrayList<JXTARendezvousSuperPeer> rdv = new ArrayList<JXTARendezvousSuperPeer>();
		for (int i = 0; i < this.rendezvousSP.size(); i++) {
			if(this.rendezvousSP.get(i) != null && this.rendezvousSP.get(i).isConnected() &&
					!this.rendezvousSP.get(i).isPersistantRdV())
				rdv.add(this.rendezvousSP.get(i));
			
			//Se è null lo rimuove da quelli noti
			if (this.rendezvousSP.get(i) == null){
				this.rendezvousSP.remove(i); //
			}
		}
		
		if(rdv.size() > 0){
			//System.out.println("FIND a Persistant RdV to give at EP");
			Random rand = new Random();
			int num_RdV = rand.nextInt(rdv.size());
			to_connect = rdv.get(num_RdV);
			
		}

		return to_connect;
	}
	
	/**
	 * Connect this Rendezvous to all well-known Rendezvous 
	 */
	//Connessione a tutti i RdV noti (Well-Known) (RendezvousPeerView)
	public void connectToWNRdV(){
		
		for(int i=0; i<this.rendezvousSP.size(); i++){
			//se non ci si era già connesso si connette
			if(!this.RPV.contains(this.rendezvousSP.get(i)))
				this.RPV.add(this.rendezvousSP.get(i));
			
		}
		System.out.println("RdV " + this.JXTAID + " have dimension of RPV " + this.RPV.size());
		System.out.println("Formed by:");
		for(int i=0; i< this.RPV.size(); i++){
			//System.out.println(this.RPV.get(i).JXTAID);
		}
		
		this.nDivSpace = this.RPV.size();
		this.myRespSpace = this.RPV.indexOf(this);
		//System.out.println("Dimension of RPV " + this.nDivSpace + " , with position " + this.myRespSpace);
	}
	
	/**
	 * Disconnection
	 */
	public void disconnectRdV(){
		
		super.disconnectJXTANode();
		this.infoDisconnect();
		
	}
	
	/**
	 * Inform for disconnection
	 */
	//informa della propria disconnessione così che gli altri lo elimino dalla RPV
	public void infoDisconnect(){
		//System.out.println("Disconnection " + this.JXTAID);
		this.RPV.remove(this);
		this.sendDisconnect();
	}
	
	/**
	 * Inform all Rendezvous in his RPV of his disconnection
	 */
	public void sendDisconnect(){
		for(int i=0; i < this.RPV.size(); i++){
			this.RPV.get(i).receiveDisconnect(this);
		}
	}
	
	/**
	 * Remove "rdv" from his RPV
	 * @param rdv
	 */
	public void receiveDisconnect(JXTARendezvousSuperPeer rdv){
		if(this.RPV.contains(rdv)){
			this.RPV.remove(rdv);
		}
	}
	
	/**
	 * Research Advertisement with "id" for Edge Peer "sourceReq". 
	 * The flag "resp" ask owner to respond with Advertisement 
	 * @param id
	 * @param sourceReq
	 * @param resp
	 * @return
	 */
	//ricerca del rdvSP responsabile dell'adv(anche con walker)
	//resp indica se si vuole far avere la risposta da parte del propietario
	public JXTARendezvousSuperPeer searchAdvResp(int id, JXTAEdgePeer sourceReq, boolean resp){
		JXTARendezvousSuperPeer respPeer = null;
		JXTAEdgePeer propPeer = null;
		boolean finded = false;

		//System.out.println("RESEARCH of responsibility RdV for resource by " + this.JXTAID);
		
		//aggironamento delle info sullo spazio prima di usarle
		this.nDivSpace = this.RPV.size();
		if (this.nDivSpace == 0)
			return null;
		int dimDivSpace = Math.round(this.keyspace/this.nDivSpace);//dimensioni spazio diviso
		//System.out.println("Dimension of divided space " + dimDivSpace);
		int respSpace = (int) Math.floor(id/dimDivSpace);//individuazione dello spazio responsabile dell'id
		//System.out.println("Resp space " + respSpace);
		//vede se il proprietario è il Rdv corrente
		
		if(respSpace == myRespSpace){
			//il responsabile è il Rdv corrente
			respPeer = this;
			//System.out.println("RESPONSIBLE is current RdV");
			
			if(this.respAdv.containsKey(id)){
				
				respPeer = this;
				propPeer = this.respAdv.get(id).ep;
				finded = true;
				// richiesta di risposta al EP propietario
				if(propPeer != null && propPeer.isConnected()){
					if(resp){
						propPeer.sendAdvertisement(id, sourceReq);
					} //altrimenti si deve solo informare di possederlo
				}
			}
			else if(resp){
				return null;
			}
		} else {
			//Verifica che il Rdv esista ancora 
			//(e consistenza RPV(chiamando la relativa funzione)
			//System.out.println("Responsible RdV based on RPV is number " + respSpace);
			//respPeer = this.rendezvousSP.get(respSpace);090712 dovendo cercare sulla RPV
			respPeer = this.RPV.get(respSpace);
			
			//Ricerca sul Rdv responsabile
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
			//Ricerca con walker
		if (!finded) {
				//System.out.println("RESEARCH with WALKER");
				JXTARendezvousSuperPeer walkerUp = null;
				JXTARendezvousSuperPeer walkerDown = null;
				//System.out.println("N Div Space " + nDivSpace);
				//System.out.println("DIMENSION of RPV " + this.RPV.size());
				
				//int dirWalker = +1;
				for(int i=0; i < this.walker && !finded; i++){
					//lancio dei walker
					if( ( respSpace + (i+1) ) < this.RPV.size())//090712 nDivSpace)
						walkerUp = this.RPV.get(respSpace + (i+1));
					if( (respSpace + (i+1)*(-1) ) >= 0 )
						walkerDown = this.RPV.get(respSpace + (i+1)*(-1));
					
					if(walkerUp != null ){
						if (walkerUp.isConnected()){ 
							if(resp){
								finded = walkerUp.forwardToEP(id, sourceReq);
								if(!finded)
									return null; //così da capire di non aver trovato la risorsa
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
				
				//se lo hanno trovato i walker occorre ristabilizzare la tabella
				if (finded){
					//System.out.println("FINDED by walker");
					this.testRPV();
					
				}
			
		}
		
		return respPeer;
	}
	
	/**
	 * Test of Rendezvous Peer View
	 */
	//controllo consistenza RPV vedendo i nodi se sono isConnected
	public void testRPV(){
		//System.out.println("  IN TEST RPV  " + this.JXTAID);
		//heartbeat to +1-1 in RPV
		int my_pos = this.RPV.indexOf(this);
		int my_old_pos = my_pos;
		//System.out.println("My Position :" + my_pos);
		//se non sono connesso mi elimino dalla RPV
		if(!this.isConnected()){
			//System.out.println("SONO disconnesso e mi elimino dalla RPV");
			this.RPV.remove(this);
			my_pos = this.RPV.indexOf(this);
			//System.out.println("My new position " + my_pos);
		}
		
		if(this.RPV.size() == 0){
			//System.out.println("\tRPV VUOTA");
			return;
		}
		if(my_old_pos > 0){
			
			if(this.RPV.get(my_old_pos - 1) == null || !this.RPV.get(my_old_pos - 1).isConnected){
				//System.out.println("Previous peer isn't connected");
				//System.out.println("delete from RPV of element for position " + (my_old_pos-1) );
				purge(my_old_pos - 1);
				my_pos = this.RPV.indexOf(this);
				
			}
			
		} else if (this.RPV.get(this.RPV.size() - 1) == null || !this.RPV.get(this.RPV.size() - 1).isConnected){
				//System.out.println("Previous peer isn't connected TURN");
				//System.out.println("TURN DONE: delete from RPV of element for position " + (this.RPV.size() - 1) );
				purge(this.RPV.size() - 1);
				my_pos = this.RPV.indexOf(this);
				
			}

		//System.out.println("My current position after next control  " + my_pos);
		
		my_old_pos = my_pos;
		
		if(my_old_pos < (this.RPV.size() - 1) && my_old_pos != -1){
			if(this.RPV.get(my_old_pos +1) == null ||  !this.RPV.get(my_old_pos +1).isConnected){
				//System.out.println("Next peer isn't connected");
				//System.out.println("delete from RPV of element for position " + (my_old_pos+1) );
				purge(my_old_pos + 1);
				my_pos = this.RPV.indexOf(this);
			}
		} else if (this.RPV.get(0) == null || !this.RPV.get(0).isConnected){
				//System.out.println("Next peer isn't connected TURN");
				//System.out.println("delete from RPV of element for position " + 0 );
				purge(0);
				my_pos = this.RPV.indexOf(this);
		}
		
		//System.out.println("My current position after second control ");
		
		this.nDivSpace = this.RPV.size();
		this.myRespSpace = this.RPV.indexOf(this);
		
		//System.out.println("New divSpace " + this.nDivSpace + " and my space " + this.myRespSpace);
		
		Random random = new Random();
		//dimensione porzione random del RPV da inviare  
		int dim_RPV_update = random.nextInt(this.RPV.size());
		int num_RdV_toUpdate = random.nextInt(this.RPV.size());
			
		//System.out.println("Dimensione che si vuole distribuire " + dim_RPV_update + " numero di RdV da informare " + num_RdV_toUpdate);
		
		ArrayList<JXTARendezvousSuperPeer> RPV_toSend = new ArrayList<JXTARendezvousSuperPeer>();
		ArrayList<JXTARendezvousSuperPeer> update_RdV = new ArrayList<JXTARendezvousSuperPeer>();
		
		
		//elementi da trasmettere
		random = new Random();
		int toSend;
		//System.out.println("INIZIO scelta elementi della tabella da inviare...");
		for (int i=0; i < dim_RPV_update && i< this.RPV.size(); i++) {
			//System.out.print(i + "  (");
			toSend = random.nextInt(this.RPV.size());
			//System.out.print(this.RPV.get(toSend).JXTAID +  " )");
			RPV_toSend.add(this.RPV.get(toSend));
			//this.updateRPV(this)
		}
		
		//RdV scelti da informare
		random = new Random();
		int toUpdate;
		//System.out.println("\nINIZIO scelta RdV da informare...");
		for (int i=0; i < num_RdV_toUpdate; i++) {
			//System.out.print(i + "  (");
			toUpdate = random.nextInt(this.RPV.size());
			//System.out.println(this.RPV.get(toUpdate).JXTAID + " )");
			update_RdV.add(this.RPV.get(toUpdate));
		}
		
		//System.out.println("\nINVIO IN CORSO...");
		this.sendRPV(RPV_toSend, update_RdV);
		
	}
	
	/**
	 * Delete element of index "i" from RPV
	 * @param i
	 */
	public void purge(int i){
		//System.out.println("Cancellazione di " + i);
		this.RPV.remove(i);
	}
	
	/**
	 * Update RPV with list "new_list"
	 * @param new_list
	 */
	public void receiveRPVPortion(ArrayList<JXTARendezvousSuperPeer> new_list){
		
		JXTARendezvousSuperPeer peer = null;
		//System.out.println("DIMENSIONE delle informazioni ricevute " + new_list.size());
		for (int i=0; i < new_list.size(); i++){
			
			peer = new_list.get(i);
			//System.out.println("Il RdV " + this.JXTAID + " controlla che abbia informazioni di " + peer.JXTAID);
			if(!this.RPV.contains(peer)){
				//System.out.println("info aggiunte");
				this.RPV.add(peer);
			}
			
			if(!this.rendezvousSP.contains(peer)) { //per aggiungerlo alla lista dei RdV noti
				//System.out.println("INFORMAZIONI di " + peer.JXTAID + " non presenti neanche nella lista dei RdV noti");
				this.rendezvousSP.add(peer);
			}
			
		}
	}
	
	/**
	 * Send RPV portion "to_send" to all Rendezvous on "to_update" 
	 * @param to_send
	 * @param to_update
	 */
	public void sendRPV(ArrayList<JXTARendezvousSuperPeer> to_send, ArrayList<JXTARendezvousSuperPeer> to_update){
		
		JXTARendezvousSuperPeer dest = null;
		//System.out.println("DIMENSIONE delle destinazioni " + to_update.size());
		for (int i=0; i < to_update.size(); i++){
			
			dest = to_update.get(i);
			//System.out.println("INVIO a " + dest.JXTAID + " della lista");
			dest.receiveRPVPortion(to_send);
		}
		
	}
	
	/**
	 * Receive informaiton of disconnection of Edge Peer "del" and delete all Advertisement owned dy "del"
	 * @param del
	 */
	//Permette al EP di informare il RdV cui è connesso che stà abbandonando la rete
	public void infoDisconnection(JXTAEdgePeer del){
		//System.out.println("Entrato in INFO-DISCONNECTION");
		Set<Integer> keys =  respAdv.keySet();
		//System.out.println("dopo set keys");
		Iterator<Integer> iter_key =  keys.iterator();
		//System.out.println("dopo integer keys");
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
	 * Receive a responsibility Advertisement with "idAdv" and owner "prop" and relay to correct 
	 * Rendevous based on RPV
	 * @param prop
	 * @param idAdv
	 */
	//permette agli EP di inviare la pubblicazione di una risorsa che viene distribuita al RdV responsabile
	public void receiveAdvIndex(JXTAEdgePeer prop, int idAdv){
		//System.out.println("Ricevuta l'Adv " + idAdv + " da " + prop.JXTAID);
		
		JXTARendezvousSuperPeer resp = this.searchAdvResp(idAdv, null, false);
		//System.out.println("Individuazione del responsabile " + resp.JXTAID);
		if(resp != null && resp.isConnected())
			resp.saveIndex(prop, idAdv);
		
		resp.printAllAdv();
	}
	
	/**
	 * Permit at Edge Peer "ric" connected to him to obtain Advertisement "idAdv"
	 * @param ric
	 * @param idAdv
	 * @return
	 */
	//permette agli EP di inviare una richiesta
	public boolean requestAdv(JXTAEdgePeer ric, int idAdv) {
		//System.out.println("INIZIO ricerca per " + idAdv);
		JXTARendezvousSuperPeer rdv = this.searchAdvResp(idAdv, ric, true);
		if (rdv != null) {
			//System.out.println("Risorsa " + idAdv + " TROVATA");
			return true;
		} else
			return false;
	}
	
	/**
	 * Print all responsibility Advertisement
	 */
	public void printAllAdv(){
		Set<Integer> key_set = this.respAdv.keySet();
		Iterator<Integer> iter =  key_set.iterator();
		//System.out.println("RISORSE mantenute da " +  this.JXTAID);
		for(int i = 0; i < this.respAdv.size(); i++){
			System.out.println(iter.next());
		}
			
		
	}
	
	/**
	 * Save on his respAdv "id" with owner "prop"
	 * @param prop
	 * @param id
	 */
	public void saveIndex(JXTAEdgePeer prop, int id){
		IdEP newId = new IdEP(id, prop);
		this.respAdv.put(id, newId);
		//System.out.println("SALVATAGGIO completato su " + this.JXTAID + " della risorsa " + id);
	}
	

	/**
	 * Research of "id" in his resposibility Adv
	 * @param id
	 * @return
	 */
	public boolean hasRespAdv(int id){
		//System.out.println("Richiesta di disponibilità della responsabilità dell'Adv");
		if(this.respAdv.containsKey(id)){
			return true;
		}
		return false;
	}
	
	/**
	 * Request to owner Advertisement of "idAdv" to send Advertisement at "des" 
	 * @param id
	 * @param des
	 * @return
	 */
	//richiesta di inoltro di Adv al EP
	public boolean forwardToEP(int id, JXTAEdgePeer des){
		//System.out.println("Richiesta di inoltro dell'AdV a " + des.JXTAID + " se lo possiede");
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
	 * Return the dimension of RPV
	 * @return
	 */
	public int dimRPV(){
		return this.RPV.size();
	}
	
	/**
	 * Add Edge Peer "e" to list of connected Edge Peers
	 * @param e
	 */
	public void addEP( JXTAEdgePeer e ){
		if(!this.peer.contains(e)){
			//System.out.println("Edge Peer " + e.JXTAID + " aggiunto alla lista di quelli connessi");
			this.peer.add(e);
		}
	}
	
	/**
	 * Return the number of Edge Peer connected
	 * @return
	 */
	public int dimEP(){
		return this.peer.size();
	}
	
	/**
	 * Return the number of responsibility Advertisement
	 * @return
	 */
	public int dimRespAdv(){
		return this.respAdv.size();
	}
	
}
