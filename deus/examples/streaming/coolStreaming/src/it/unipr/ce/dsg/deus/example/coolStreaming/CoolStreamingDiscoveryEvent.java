package it.unipr.ce.dsg.deus.example.coolStreaming;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * <p>
 * Each StreamingDiscoveryEvent must be associated to a StreamingPeer and to a
 * ResourceAdv. If the latter has not been set previously it is set by the
 * StreamingDiscoveryEvent itself, with randomly generated resource name and amount,
 * meaning that it is the first discovery attempt for that resource.
 * </p>
 * <p>
 * The discovery algorithm is flooding-based (like Gnutella), but the
 * propagation range and the TTL are not the same for each node: they depend on
 * the current value of the chromosome of the associated StreamingPeer. The
 * discovery process also takes into account the ResourceAdv cache of the
 * associated StreamingPeer.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class CoolStreamingDiscoveryEvent extends NodeEvent {
	
	public CoolStreamingDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	
		System.out.println("Discovery !");
		
	}

	public Object clone() {
		CoolStreamingDiscoveryEvent clone = (CoolStreamingDiscoveryEvent) super.clone();

		return clone;
	}


	public void run() throws RunException {
		
		getLogger().fine("####### disc event: " + associatedNode.getKey());
		getLogger().fine("####### disc event time: " + triggeringTime);
		
		
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
		//associatedStreamingNode.findFirstProviderNode(this.triggeringTime);
		
		if(associatedStreamingNode.isConnected())
		associatedStreamingNode.findFirstProvidersNode(triggeringTime);
		
		getLogger().fine("Ho trovato come nodo( Nodo - Server ): " + associatedStreamingNode.getSourceStreamingNode() + " - " +(associatedStreamingNode.getServerNode()) );
		getLogger().fine("########################################");
	}

}
