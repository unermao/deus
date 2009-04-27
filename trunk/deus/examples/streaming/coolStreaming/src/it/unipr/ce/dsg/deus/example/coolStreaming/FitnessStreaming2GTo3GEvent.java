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
public class FitnessStreaming2GTo3GEvent extends NodeEvent {

	private static final String NEW_MAX_ACCEPTED_CONNECTION = "newMaxAcceptedConnection";
	private static final String NEW_UPLOAD_SPEED = "newUploadSpeed";
	
	private int newMaxAcceptedConnection = 0;
	private double newUploadSpeed = 0.0;
	
	public FitnessStreaming2GTo3GEvent(String id, Properties params,
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
		FitnessStreaming2GTo3GEvent clone = (FitnessStreaming2GTo3GEvent) super.clone();
		clone.newMaxAcceptedConnection = this.newMaxAcceptedConnection;
		clone.newUploadSpeed = this.newUploadSpeed;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new 2G --> 3G Event");
		
		FitnessCoolStreamingPeer associatedStreamingNode = (FitnessCoolStreamingPeer)associatedNode;
		
		if( associatedStreamingNode.getConnectionType().equals("2g") && Engine.getDefault().getSimulationRandom().nextInt(10) > 5 ){
			
		associatedStreamingNode.change2GTo3G(FitnessCoolStreamingPeer.G3, this.newUploadSpeed, this.newMaxAcceptedConnection);
		}
		getLogger().fine("end new 2G --> 3G Event ##");
	}
	
	public int getNewMaxAcceptedConnection() {
		return newMaxAcceptedConnection;
	}

}
