package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class JXTARendezvousSuperPeer extends JXTAEdgePeer {

	//private ArrayList<JXTARendezvousSuperPeer> rendezvous = new ArrayList<JXTARendezvousSuperPeer>();
	//private ArrayList<JXTAAdvertisement> respAdv = new ArrayList<JXTAAdvertisement>();
	//private ArrayList<IdEP> respAdv = new ArrayList<IdEP>();
	//private int keyspace;
	private int nDivSpace;
	private int myRespSpace;
	private HashMap<Integer, IdEP> respAdv = new HashMap<Integer, IdEP>();
	
	
	private class IdEP{
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
		// TODO Auto-generated constructor stub
	}

	public Object clone() {
		JXTARendezvousSuperPeer clone = (JXTARendezvousSuperPeer) super.clone();
		//clone.rendezvous = new ArrayList<JXTARendezvousSuperPeer>();
		//clone.advertisementIndex = new ArrayList<JXTAAdvertisement>();
		//clone.respAdv = new ArrayList<IdEP>();
		clone.respAdv = new HashMap<Integer, IdEP>();
		
		
		//TODO: aggiunta di altri campi
		return clone;
	}

	//ricerca del rdvSP responsabile dell'adv(anche con walker)
	public JXTAAdvertisement searchAdvResp(int id, JXTAEdgePeer sourceReq){
		JXTARendezvousSuperPeer respPeer = null;
		JXTAEdgePeer propPeer = null;
		boolean finded = false;
		//JXTAEdgePeer
		int dimDivSpace = Math.round(keyspace/nDivSpace);
		int respSpace = Math.round(dimDivSpace/id);
		//vede se il proprietario è i Rdv corrente
		//if(id >= myRespSpace*dimDivSpace && id < myRespSpace*dimDivSpace + dimDivSpace )
		if(respSpace == myRespSpace){
			//il responsabile è il Rdv corrente
			IdEP epToFind = new IdEP(id,null);
			//respAdv.contains(epToFind);
			
			if(this.respAdv.containsKey(id)){
				propPeer = this.respAdv.get(id).ep;
			
//			int index = this.respAdv.indexOf(epToFind);
//			if(index != -1){
//				propPeer = this.respAdv.get(index).ep;
				
				//TODO: richiesta di risposta al EP propietario
				if(propPeer != null){
					propPeer.sendAdvertisement(id, sourceReq);
				}
				
			}
		}
		else{
			//TODO:Verifica che il Rdv esista ancora e consistenza RPV(chiamando la relativa funzione)
			respPeer = this.rendezvousSP.get(respSpace);
			
			//TODO:Ricerca sul Rdv responsabile
			if(respPeer != null){
				//TODO:Ricerca sul Rdv responsabile
				respPeer.forwardToEP(id, sourceReq);
			}
		
			//TODO:(else)Ricerca con walker
			else{
				
				JXTARendezvousSuperPeer walkerUp = null;
				JXTARendezvousSuperPeer walkerDown = null;
				//int dirWalker = +1;
				for(int i=0; i<3; i++){
					
					walkerUp = this.rendezvousSP.get(respSpace + (i+1));
					walkerDown = this.rendezvousSP.get(respSpace + (i+1)*(-1));
					
					if(walkerUp != null){
						walkerUp.forwardToEP(id, sourceReq);
					}
					
					else if(walkerDown != null){
						walkerDown.forwardToEP(id, sourceReq);
					}
				}
			}
			
		}
		
		
		
			
			
			
		
		
		//return respPeer;
		return null;
	}
	
	//controllo consistenza RPV
	public void testRPV(){
		
	}
	
	//distribuzione responsabilità dei nodi
	public void distrIndex(){
		
	}
	
	//richiesta di inoltro di Adv al EP
	public void forwardToEP(int id, JXTAEdgePeer des){
		IdEP idToFind = new IdEP(id, null);
		if(this.respAdv.containsKey(id)){
			JXTAEdgePeer propEP = this.respAdv.get(id).ep;
			
//		}
//			
//		
//		if(this.respAdv.contains(idToFind)){
//			int index = this.respAdv.indexOf(idToFind);
//			JXTAEdgePeer propEP = this.respAdv.get(index).ep;
			if(propEP != null){
				propEP.sendAdvertisement(id, des);
			}
		}
	}
}
