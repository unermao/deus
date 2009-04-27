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
public class FitnessStreaming3GTo2GEvent extends NodeEvent {

	private static final String NEW_MAX_ACCEPTED_CONNECTION = "newMaxAcceptedConnection";
	private static final String NEW_UPLOAD_SPEED = "newUploadSpeed";
	
	private int newMaxAcceptedConnection = 0;
	private double newUploadSpeed;

	public FitnessStreaming3GTo2GEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
		initialize();
		
		System.out.println("Streaming3GTo2GEvent");
	}

	public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(NEW_MAX_ACCEPTED_CONNECTION))
			newMaxAcceptedConnection  = Integer.parseInt(params.getProperty(NEW_MAX_ACCEPTED_CONNECTION));
		
		if (params.containsKey(NEW_UPLOAD_SPEED))
			newUploadSpeed  = Double.parseDouble(params.getProperty(NEW_UPLOAD_SPEED));
	}	
	
	public Object clone() {
		
		FitnessStreaming3GTo2GEvent clone = (FitnessStreaming3GTo2GEvent) super.clone();
		clone.newMaxAcceptedConnection = 0;
		clone.newUploadSpeed = 0.0;
		return clone;
	}

	public void run() throws RunException {
		
		System.out.println("3G");
		
		FitnessCoolStreamingPeer associatedStreamingNode = (FitnessCoolStreamingPeer)associatedNode;
		
		if( associatedStreamingNode.getConnectionType().equals("3g") && Engine.getDefault().getSimulationRandom().nextInt(10) > 5 ){
		
			associatedStreamingNode.change3GTo2G(FitnessCoolStreamingPeer.G2, this.newUploadSpeed, this.newMaxAcceptedConnection,this.triggeringTime);
		}
		getLogger().fine("end new 3G --> 2G Event ##");
	}

	public int getNewMaxAcceptedConnection() {
		return newMaxAcceptedConnection;
	}
	
}
