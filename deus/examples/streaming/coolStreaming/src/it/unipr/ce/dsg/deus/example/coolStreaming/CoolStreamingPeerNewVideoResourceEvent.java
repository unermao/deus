package it.unipr.ce.dsg.deus.example.coolStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;


/**
 * <p>
 * This event is related to the release of a previously 
 * consumed resource, by updating the corresponding value 
 * on the resource owner.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class CoolStreamingPeerNewVideoResourceEvent extends NodeEvent {

	private CoolStreamingVideoChunk videoChunk = null;
	
	public CoolStreamingPeerNewVideoResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {		
	}
	
	public Object clone() {
		
		CoolStreamingPeerNewVideoResourceEvent clone = (CoolStreamingPeerNewVideoResourceEvent) super.clone();
		clone.videoChunk = this.videoChunk;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
		
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
		
		Peer sourcePeer = (Peer) videoChunk.getSourceNode();
		
		/**
		 * Se il nodo che mi ha inviato il pacchetto e' ancora conneso
		 * effettuo le operazioni di invio ai miei forniti.
		 * Altrimenti il pacchetto non viene inoltrato dato che si suppone che a causa
		 * della disconnessione della sorgente non sia arrivato.
		 * 
		 */		
		if( Engine.getDefault().getNodes().contains(sourcePeer) && Engine.getDefault().getNodes().contains(associatedStreamingNode))
		{
			
			//Aggiungo la nuova porzione video al nodo
			//associatedStreamingNode.addNewVideoResource(videoChunk,this.triggeringTime);
			//if(	associatedStreamingNode.getKey() == 2125316136)
		//	System.out.println("INVIO");
			associatedStreamingNode.addNewVideoResourceCoolStreaming(videoChunk,this.triggeringTime);
			
			CoolStreamingVideoChunk newVideoChunk = new CoolStreamingVideoChunk(videoChunk.getChunkIndex(),videoChunk.getChunkSize());
			
			//Imposto il nuovo nodo sorgente sulla porzione video
			newVideoChunk.setSourceNode(associatedNode);
			newVideoChunk.setOriginalTime(triggeringTime);
			
			
//			if(associatedStreamingNode.getKey() == 1247386817 )
//				System.out.println("Arrivato pezzo " + videoChunk.getChunkIndex() + " in " + (Engine.getDefault().getVirtualTime()-videoChunk.getOriginalTime()));
			
			//Innesca per i nodi forniti l'evento di aggiornamento risorsa
//			for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
//			{
//				   //getLogger().fine("Sono : " + associatedStreamingNode.getKey() + " Invio a: " + associatedStreamingNode.getServedPeers().get(index).getKey() + " Chunk: " + videoChunk.getChunkIndex());
//				   //if( !associatedStreamingNode.getServedPeers().get(index).getNeededChunk().contains(newVideoChunk.getChunkIndex()) )
//					associatedStreamingNode.sendVideoChunk(associatedStreamingNode.getServedPeers().get(index), newVideoChunk, this.triggeringTime);
//			}
			int i = associatedStreamingNode.calculate_buffer_index(newVideoChunk);
			
			//System.out.println("Sono : " + associatedStreamingNode.getKey());
			
			for(int index = 0 ; index < associatedStreamingNode.getServedPeers2().get(i).size(); index++)
			{
				   //getLogger().fine("Sono : " + associatedStreamingNode.getKey() + " Invio a: " + associatedStreamingNode.getServedPeers().get(index).getKey() + " Chunk: " + videoChunk.getChunkIndex());
				  // if( !associatedStreamingNode.getServedPeers2().get(i).get(index).getNeededChunk().contains(newVideoChunk.getChunkIndex()) )
				//if(associatedStreamingNode.getServedPeers2().get(i).get(index).getKey() == 1602282472 )
//				if(associatedStreamingNode.getKey() == 1376590870 )
//				System.out.println("Sono " + associatedStreamingNode.getKey() + " Invio " + newVideoChunk.getChunkIndex() + " a " + associatedStreamingNode.getServedPeers2().get(i).get(index).getKey());	
				
				CoolStreamingVideoChunk newResource2 = new CoolStreamingVideoChunk(newVideoChunk.getChunkIndex(),newVideoChunk.getChunkSize());
				
				//Imposto nel chunk le informazioni sul sorgente
			    newResource2.setSourceNode(associatedStreamingNode);
			    newResource2.setOriginalTime(this.triggeringTime);
				
				newResource2.setDestNode(associatedStreamingNode.getServedPeers2().get(i).get(index));
				
			//	System.out.println("ASA "+ associatedStreamingNode.getServedPeers2().get(i).get(index));
				
				//associatedStreamingNode.getServedPeers2().get(i).get(index).getRequestChunkBuffer().get(i).add(newResource2);
				
				//TODO AGGIUNGERE CHE è STATO RICHIESTO
				
//				if( newResource2.getChunkIndex() == 189)
//				System.out.println("INVIO " + newResource2.getChunkIndex() + " a " + associatedStreamingNode.getServedPeers2().get(i).get(index).getKey() + "   " + associatedStreamingNode.getServedPeers2().get(i).get(index).getIndexOfLastReceivedChunk());
				
				//TODO TOGLIERE
				//if( newResource2.getChunkIndex() > associatedStreamingNode.getServedPeers2().get(i).get(index).getInitChunk() )
				//	associatedStreamingNode.getSendBuffer().get(i).add(newResource2);
				
				if( newResource2.getChunkIndex() > associatedStreamingNode.getServedPeers2().get(i).get(index).getInitChunk() && !associatedStreamingNode.getServedPeers2().get(i).get(index).getK_buffer().get(i).contains(newResource2))
					associatedStreamingNode.sendVideoChunk(associatedStreamingNode.getServedPeers2().get(i).get(index), newVideoChunk, this.triggeringTime);
			}
			
		}
		
		
			
		getLogger().fine("end new node video resource ##");
	}

	public CoolStreamingVideoChunk getResourceValue() {
		return videoChunk;
	}

	public void setResourceValue(CoolStreamingVideoChunk newResource) {
		this.videoChunk = newResource;
	}

}
