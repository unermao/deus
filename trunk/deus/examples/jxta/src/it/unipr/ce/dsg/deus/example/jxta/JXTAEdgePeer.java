package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

public class JXTAEdgePeer extends Peer {

	//private static final String JXTA_Id = "jxtaId";
	
	
	//private ArrayList<Integer> peerGroup = new ArrayList<Integer>();
	public ArrayList<JXTARendezvousSuperPeer> rendezvousSP = new ArrayList<JXTARendezvousSuperPeer>();
	private ArrayList<JXTAEdgePeer> peer = new ArrayList<JXTAEdgePeer>();
	public ArrayList<IdAdv> cacheAdvertisement = new ArrayList<IdAdv>();
	public int JXTAID;
	public int keyspace;
	public HashMap<Integer, IdAdv> cacheAdv = new HashMap<Integer, IdAdv>();
	
	private class IdAdv{
		public int id;
		public JXTAAdvertisement adv;
		public JXTAEdgePeer ep;
		
		public IdAdv(int i, JXTAAdvertisement a, JXTAEdgePeer e){
			id = i;
			adv = a;
			e = ep;
		}
	}
	
	public JXTAEdgePeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		//keyspace = Engine.getDefault().getKeySpaceSize();
		//keyspace = Integer.parseInt(params.getProperty("keyspacesize"));
		
		this.JXTAID = (int) (Math.round(Math.random()*(keyspace+1)))%(keyspace+1);
		System.out.println("SPAZIO CHIAVI: " + keyspace + " JXTA_Id " + JXTAID);
		// TODO Auto-generated constructor stub
	}

	public Object clone() {
		JXTAEdgePeer clone = (JXTAEdgePeer) super.clone();
		clone.rendezvousSP = new ArrayList<JXTARendezvousSuperPeer>();
		//clone.cacheAdvertisement = new ArrayList<JXTAAdvertisement>();
		clone.cacheAdvertisement = new ArrayList<IdAdv>();
		clone.cacheAdv = new HashMap<Integer, IdAdv>();
		clone.peer = new ArrayList<JXTAEdgePeer>();
		clone.keyspace = this.keyspace;
		//TODO: copy of fields
		//Per l'ID generarne uno nuovo casualmente?
		System.out.println("FINE CLONE ( " +clone.JXTAID);
		return clone;
	}
	
	public void ciao(){
		System.out.println("CIAOCIAO da " + this.JXTAID );
	}
	
	//creazione nuovo adv (casuale) ed inserimento nella lista
	public void createAdvertisement(){
		System.out.println("ENTRA CREATE");
		int random = (int) (Math.round(Math.random()*(keyspace+1)))%keyspace; 
				
		JXTAAdvertisement adv = new JXTAAdvertisement(random);
		this.saveAdvertisement(random, adv, this);
		
		System.out.println("CREATA RISORSA " + adv.JXTAID + "di "+ this.JXTAID + "(k )" + this.keyspace);
		//this.cacheAdvertisement.add(adv);
		//return adv;
	}
	
	//ricerca di un adv. Si deve ottenere l'adv cercato(dall'EP che lo possiede )
	public JXTAAdvertisement searchAdvertisement(int advID) {
		//JXTAEdgePeer possessorPeer = null;
		JXTAAdvertisement adv = null;
		//TODO:non si dovrebbe conoscere l'ADV ma solo l'id
		//JXTAAdvertisement advertisementToFind = new JXTAAdvertisement(advID);
		//IdAdv idToFind = new IdAdv(advID, advertisementToFind);
		IdAdv idToFind = new IdAdv(advID, null, null);
		
		//Prima ricerca nella cache locale
		if(this.cacheAdv.containsKey(advID)){
			adv = this.cacheAdv.get(advID).adv;
		}
			
		
		//if(this.cacheAdvertisement.contains(advertisementToFind))
		if(this.cacheAdvertisement.contains(idToFind)){
			int position = this.cacheAdvertisement.indexOf(idToFind);
			adv = this.cacheAdvertisement.get(position).adv;
			//possessorPeer = this;
		}
		//Far ricercare in tutti i rdv noti
		else{
			boolean find = false;
			//ricerca per ogni rdv
			for(int i=0; i<this.rendezvousSP.size() && find != true; i++){
				adv = this.rendezvousSP.get(i).searchAdvResp(advID,this);
				if(adv != null)
					find = true;	
			}
		}
		
		
		return adv;
	}
	
	//invio dell'adv se lo si possiede
	public void sendAdvertisement (int id, JXTAEdgePeer dest) {
		IdAdv idToFind = new IdAdv(id, null, null);
		JXTAAdvertisement adv = null;
		//TODO: DO
		if(this.cacheAdv.containsKey(id)){
			adv = this.cacheAdv.get(id).adv;
			dest.saveAdvertisement(id, adv, this);
		}
			
		
		if(this.cacheAdvertisement.contains(idToFind)){
			int position = this.cacheAdvertisement.indexOf(idToFind);
			adv = this.cacheAdvertisement.get(position).adv;
			dest.saveAdvertisement(id, adv, this);
		}
	}
	
	public void saveAdvertisement (int id, JXTAAdvertisement adv, JXTAEdgePeer ep){
		IdAdv newAdv = new IdAdv(id, adv, null); 
		this.cacheAdvertisement.add(newAdv);
		
		//TODO: da aggiustare----ultimo parametro
		this.cacheAdv.put(id, newAdv);
	}

	//pubblicazione di un nuovo adv su un rdv(SRDI service)(asincorno o periodico vedendo quelli non pubblicati)
	public void publishAdvertisement () {
		//TODO:VERIficare la consistenza di  Iterator
		Iterator<Integer> it = (Iterator<Integer>) this.cacheAdv.keySet();
		
		for(int i=0; i<this.cacheAdv.size(); i++){
			if(!this.cacheAdv.get(it.next()).adv.published){
				boolean find = false;
				//ricerca per ogni rdv
				for(int j=0; j<this.rendezvousSP.size() && find != true; j++){
					if(this.rendezvousSP.get(j) != null )
						this.cacheAdv.get(it).adv.published = true;
						//this.cacheAdvertisement.add(i, pub);
						
						//this.rendezvousSP.get(j).saveAdvertisement(pub.id, pub.adv, pub.ep);
						find = true;
					
					
				}
			}
		}
		
		
		for(int i=0; i<this.cacheAdvertisement.size(); i++){
			if(!this.cacheAdvertisement.get(i).adv.published){
				IdAdv pub = this.cacheAdvertisement.remove(i);
				
				//invio dell'informazione al RdvSP
				boolean find = false;
				//ricerca per ogni rdv
				for(int j=0; j<this.rendezvousSP.size() && find != true; j++){
					if(this.rendezvousSP.get(j) != null )
						pub.adv.published = true;
						this.cacheAdvertisement.add(i, pub);
						
						this.rendezvousSP.get(j).saveAdvertisement(pub.id, pub.adv, pub.ep);
						find = true;
					
					
				}
				
//				pub.adv.published = true;
//				this.cacheAdvertisement.add(i, pub);
				
			}
		}
		
		
	}
}
