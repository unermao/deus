package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the discovery of a random created resource. 
 * During the execution of the event the associated node research 
 * the resource.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTADiscoveryEvent extends NodeEvent {

	public JXTADiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		//Choose between created resource
		ArrayList<Integer> advertisement = new ArrayList<Integer>();
		
		for (int i=0; i < Engine.getDefault().getNodes().size(); i++){
			JXTAEdgePeer ep = (JXTAEdgePeer) Engine.getDefault().getNodes().get(i);
			Set<Integer> key_set = ep.cacheAdv.keySet();
			Iterator<Integer> keys = key_set.iterator();
			for (int j=0; j < ep.cacheAdv.size(); j++ ){
				int key = keys.next(); 
				if(!advertisement.contains(ep.cacheAdv.get(key))){
					advertisement.add(ep.cacheAdv.get(key).id);
				}
			}
		}
		
		if (advertisement.size() > 0){

			int indexResourceKey = Engine.getDefault().getSimulationRandom().nextInt(advertisement.size());
			int resourceKey = advertisement.get(indexResourceKey);

			if( getAssociatedNode() != null && ( (JXTAEdgePeer) getAssociatedNode()).isConnected())
				( (JXTAEdgePeer) getAssociatedNode()).searchAdvertisement(resourceKey);
		}
	}

}
