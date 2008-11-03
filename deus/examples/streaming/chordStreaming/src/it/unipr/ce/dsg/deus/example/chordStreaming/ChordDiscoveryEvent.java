package it.unipr.ce.dsg.deus.example.chordStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * <p>
 * This Class is used to run a search of a determinated reosurce in the Network
 * if he founds it, returns the resource's identificator and the node's identificator 
 * that have her; else return null.  
 * is disconnected from all its neighbors). 
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 *
 */

public class ChordDiscoveryEvent extends NodeEvent{
	
		public ChordDiscoveryEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
		}

		@Override
		public void run() throws RunException {

			ChordPeer app = (ChordPeer) getAssociatedNode();
			if(app.isPublished())
				((ChordPeer) getAssociatedNode()).searchForAResource(app.getVideoName(),searchNextSequenceNumber(app));
		}
		
		private int searchNextSequenceNumber(ChordPeer app) {
			int maxSeq = 0;
			for(int i = 0; i < app.consumableResources.size(); i++)
			{
				if(app.consumableResources.get(i).getSequenceNumber() > maxSeq)
					maxSeq = app.consumableResources.get(i).getSequenceNumber();
			}
		
			if (maxSeq ==(Engine.getDefault().getKeySpaceSize()-1)/4/app.videoList.size())
			{
				maxSeq = maxSeq-1;
				return maxSeq;
			}
			else
			return maxSeq;
		}
}
