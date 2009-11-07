package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;

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
public class StreamingDiscoveryEvent extends NodeEvent {
	
	public StreamingDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	
		System.out.println("Discovery !");
		
	}

	public Object clone() {
		StreamingDiscoveryEvent clone = (StreamingDiscoveryEvent) super.clone();

		return clone;
	}


	public void run() throws RunException {
		
		getLogger().fine("####### disc event: " + associatedNode.getKey());
		getLogger().fine("####### disc event time: " + triggeringTime);
		
		
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		
		ServerPeer server = (ServerPeer)Engine.getDefault().getNodes().get(0);	
		
		associatedStreamingNode.findFirstProviderNode(this.triggeringTime);
		
		getLogger().fine("Ho trovato come nodo( Nodo - Server ): " + associatedStreamingNode.getSourceStreamingNode() + " - " +(associatedStreamingNode.getServerNode()) );
		getLogger().fine("########################################");
	}

}
