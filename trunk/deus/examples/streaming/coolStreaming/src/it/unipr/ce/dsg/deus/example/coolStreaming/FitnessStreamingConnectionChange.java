package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

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
public class FitnessStreamingConnectionChange extends NodeEvent {

	private static final String G2_NEW_MAX_ACCEPTED_CONNECTION = "2gMaxAcceptedConnection";
	private static final String G2_NEW_UPLOAD_SPEED = "2gUploadSpeed";
	private static final String G3_NEW_MAX_ACCEPTED_CONNECTION = "3gMaxAcceptedConnection";
	private static final String G3_NEW_UPLOAD_SPEED = "3gUploadSpeed";
	
	private int g2NewMaxAcceptedConnection = 0;
	private double g2NewUploadSpeed = 0.0;
	private int g3NewMaxAcceptedConnection = 0;
	private double g3NewUploadSpeed = 0.0;

	public FitnessStreamingConnectionChange(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
		initialize();
		
		System.out.println("Streaming3GTo2GEvent");
	}

	public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(G2_NEW_MAX_ACCEPTED_CONNECTION))
			g2NewMaxAcceptedConnection  = Integer.parseInt(params.getProperty(G2_NEW_MAX_ACCEPTED_CONNECTION));
		
		if (params.containsKey(G2_NEW_UPLOAD_SPEED))
			g2NewUploadSpeed  = Double.parseDouble(params.getProperty(G2_NEW_UPLOAD_SPEED));
		
		if (params.containsKey(G3_NEW_MAX_ACCEPTED_CONNECTION))
			g3NewMaxAcceptedConnection  = Integer.parseInt(params.getProperty(G3_NEW_MAX_ACCEPTED_CONNECTION));
		
		if (params.containsKey(G3_NEW_UPLOAD_SPEED))
			g3NewUploadSpeed  = Double.parseDouble(params.getProperty(G3_NEW_UPLOAD_SPEED));
	}	
	
	public Object clone() {
		
		FitnessStreamingConnectionChange clone = (FitnessStreamingConnectionChange) super.clone();
		clone.g2NewMaxAcceptedConnection = this.g2NewMaxAcceptedConnection;
		clone.g2NewUploadSpeed = this.g3NewUploadSpeed;
		clone.g3NewMaxAcceptedConnection = this.g3NewMaxAcceptedConnection;
		clone.g3NewUploadSpeed = this.g3NewUploadSpeed;
		return clone;
	}

	public void run() throws RunException {
		
		System.out.println("Connection Change: " + this.g2NewUploadSpeed+"-"+this.g2NewMaxAcceptedConnection);
		
		
		int index = 0;
		
		FitnessCoolStreamingPeer peer = null;
		int size = (Engine.getDefault().getNodes().size() - 1 );
	
		do{
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (FitnessCoolStreamingPeer) Engine.getDefault().getNodes().get(index);
			System.out.println("Ho trovato: " + peer.getConnectionType() + " - " + peer.getId() + "-" + peer.isConnected());
		}
		while( !(peer.isConnected() && peer.getId().equals("pcNode") && peer.getConnectionType().equals(FitnessCoolStreamingPeer.G3)));
		
		//Il peer alla fine del ciclo è il peer che voglio modificare
		//Il peer passa da 3G a 2G
		System.out.println(this.g2NewUploadSpeed);
		System.out.println(this.g2NewMaxAcceptedConnection);
		peer.change3GTo2G(FitnessCoolStreamingPeer.G2, this.g2NewUploadSpeed, this.g2NewMaxAcceptedConnection,this.triggeringTime);
		
		
	}
	
}
