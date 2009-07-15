package it.unipr.ce.dsg.deus.example.jxta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event represents the discovery of a random created resource. During the execution of
 * the event the associated node search the resource.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTADiscoveryEvent extends NodeEvent {

	public JXTADiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws RunException {
		//Per scegliere tra le risorse che sono state create
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
			Random random = new Random();
			
			int indexResourceKey = random.nextInt(advertisement.size());
			int resourceKey = advertisement.get(indexResourceKey);

			if( getAssociatedNode() != null && ( (JXTAEdgePeer) getAssociatedNode()).isConnected())
				( (JXTAEdgePeer) getAssociatedNode()).searchAdvertisement(resourceKey);
		}
	}

}
