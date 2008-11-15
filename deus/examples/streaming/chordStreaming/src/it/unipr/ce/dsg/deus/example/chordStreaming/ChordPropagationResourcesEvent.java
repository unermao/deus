package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Engine;

public class ChordPropagationResourcesEvent extends NodeEvent{
		
		public ChordPropagationResourcesEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);

		}

		@Override
		public void run() throws RunException {
			
			for(int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer propagationVideoNode = (ChordPeer)Engine.getDefault().getNodes().get(i);
				
				if(propagationVideoNode.isPublished() && propagationVideoNode.isStarted()){
					int max_connections = 0;
					if(propagationVideoNode.getTypePeer() == 1)
						max_connections = propagationVideoNode.getMaxConnectionsFast();
					else if (propagationVideoNode.getTypePeer() == 2)
						max_connections = propagationVideoNode.getMaxConnectionsMedium();
					else 
						max_connections = propagationVideoNode.getMaxConnectionsSlow();
					propagationVideoNode.propagationVideoBuffer(max_connections);
					}	
			}
		}
		
}

