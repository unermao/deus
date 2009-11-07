package it.unipr.ce.dsg.deus.example.hierarchical.streaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;


/**
 * 
 * @author Picone Marco
 * 
 */
public class Streaming2GTo3GEvent extends NodeEvent {

	private static final String NEW_MAX_ACCEPTED_CONNECTION = "newMaxAcceptedConnection";
	private static final String NEW_UPLOAD_SPEED = "newUploadSpeed";
	private static final String NEW_NUMBER_OF_LAYER = "newNumberOfLayer";
	
	
	private int newMaxAcceptedConnection = 0;
	private double newUploadSpeed;
	private int newNumberOfLayer;
	
	public Streaming2GTo3GEvent(String id, Properties params,
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
		
		if (params.containsKey(NEW_NUMBER_OF_LAYER))
			newNumberOfLayer = Integer.parseInt(params.getProperty(NEW_NUMBER_OF_LAYER));
	}
	
	public Object clone() {
		Streaming2GTo3GEvent clone = (Streaming2GTo3GEvent) super.clone();
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
		
		//Il peer alla fine del ciclo ? il peer che voglio modificare
		//Il peer passa da 3G a 2G
		peer.change3GTo2G(StreamingPeer.G3, this.newUploadSpeed, this.newMaxAcceptedConnection);
		*/
		
		
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		
		if( associatedStreamingNode.getConnectionType().equals("2g") && Engine.getDefault().getSimulationRandom().nextInt(10) > 5 ){
				
		  associatedStreamingNode.change2GTo3G(StreamingPeer.G3, this.newUploadSpeed, this.newMaxAcceptedConnection,this.newMaxAcceptedConnection,this.triggeringTime);
		}
		
		getLogger().fine("end new 2G --> 3G Event ##");
	}
	
	public int getNewMaxAcceptedConnection() {
		return newMaxAcceptedConnection;
	}

}
