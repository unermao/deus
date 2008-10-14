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

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	private CoolStreamingVideoChunk videoChunk = null;
	
	public CoolStreamingPeerNewVideoResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(MEAN_ARRIVAL_TRIGGERED_DISCOVERY)) {
			try {
				meanArrivalTriggeredDiscovery  = Float.parseFloat(params
						.getProperty(MEAN_ARRIVAL_TRIGGERED_DISCOVERY));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(
						MEAN_ARRIVAL_TRIGGERED_DISCOVERY
								+ " must be a valid float value.");
			}
		}
	}
	
	public Object clone() {
		
		CoolStreamingPeerNewVideoResourceEvent clone = (CoolStreamingPeerNewVideoResourceEvent) super.clone();
		clone.videoChunk = this.videoChunk;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
	
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;		
		
		//Verifico se il nodo che mi ha inviato il pacchetto è ancora connesso
		Peer sourcePeer = (Peer) videoChunk.getSourceNode();
		
		/**
		 * Se il nodo che mi ha inviato il pacchetto e' ancora conneso
		 * effettuo le operazioni di invio ai miei forniti.
		 * Altrimenti il pacchetto non viene inoltrato dato che si suppone che a causa
		 * della disconnessione della sorgente non sia arrivato.
		 * 
		 */
	
	    if(Engine.getDefault().getNodes().contains(sourcePeer) )
	     {
	    	CoolStreamingVideoChunk newVideoChunk = new CoolStreamingVideoChunk(videoChunk.getChunkIndex(),videoChunk.getChunkSize());
			
			//Aggiungo la nuova porzione video al nodo
			associatedStreamingNode.addNewVideoResource(newVideoChunk);
			
			//Imposto il nuovo nodo sorgente sulla porzione video
			newVideoChunk.setSourceNode(associatedNode);
				
			//Innesca per i nodi forniti l'evento di aggiornamento risorsa
			for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
			{
				   //getLogger().fine("Sono : " + associatedStreamingNode.getKey() + " Invio a: " + associatedStreamingNode.getServedPeers().get(index).getKey() + " Chunk: " + videoChunk.getChunkIndex());
				   associatedStreamingNode.sendVideoChunk(associatedStreamingNode.getServedPeers().get(index), newVideoChunk, this.triggeringTime);
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
