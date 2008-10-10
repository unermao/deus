package it.unipr.ce.dsg.deus.example.coolStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

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
public class CoolStreaming2GTo3GEvent extends NodeEvent {

	private static final String NEW_MAX_ACCEPTED_CONNECTION = "newMaxAcceptedConnection";
	private static final String NEW_UPLOAD_SPEED = "newUploadSpeed";
	
	private int newMaxAcceptedConnection = 0;
	private double newUploadSpeed;
	
	public CoolStreaming2GTo3GEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("Streaming2GTo3GEvent");
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(NEW_MAX_ACCEPTED_CONNECTION))
			newMaxAcceptedConnection  = Integer.parseInt(params.getProperty(NEW_MAX_ACCEPTED_CONNECTION));
		
		if (params.containsKey(NEW_UPLOAD_SPEED))
			newUploadSpeed  = Double.parseDouble(params.getProperty(NEW_UPLOAD_SPEED));
	}
	
	public Object clone() {
		CoolStreaming2GTo3GEvent clone = (CoolStreaming2GTo3GEvent) super.clone();
		clone.newMaxAcceptedConnection = this.newMaxAcceptedConnection;
		clone.newUploadSpeed = this.newUploadSpeed;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new 2G --> 3G Event");
		
		/*
		//Scelgo un nodo casuale che sia un dispositio mobile con connessione 2G e imposto i nuovi parametri 
		int index = 0;
		
		StreamingPeer peer = null;
		int size = (Engine.getDefault().getNodes().size() - 1 );
	
		do{
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (StreamingPeer) Engine.getDefault().getNodes().get(index);
			System.out.println("Ho trovato: " + peer.getConnectionType() + " - " + peer.getId());	
		}
		while( !(peer.getId().equals("mobileNode") && peer.getConnectionType() == StreamingPeer.G2));
		
		//Il peer alla fine del ciclo � il peer che voglio modificare
		//Il peer passa da 3G a 2G
		peer.change3GTo2G(StreamingPeer.G3, this.newUploadSpeed, this.newMaxAcceptedConnection);
		*/
		
		
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
		
		getLogger().fine("Sono : "+ associatedStreamingNode.getKey()+ " - " + associatedStreamingNode.getConnectionType() + " - " + associatedStreamingNode.getId());	
		
		associatedStreamingNode.change3GTo2G(CoolStreamingPeer.G3, this.newUploadSpeed, this.newMaxAcceptedConnection,this.triggeringTime);
		
		getLogger().fine("end new 2G --> 3G Event ##");
	}
	
	public int getNewMaxAcceptedConnection() {
		return newMaxAcceptedConnection;
	}

}
