package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Engine;

public class ChordUpdateVideoBufferEvent extends NodeEvent{
			
			public ChordUpdateVideoBufferEvent(String id, Properties params,
					Process parentProcess) throws InvalidParamsException {
				super(id, params, parentProcess);

			}

			@Override
			public void run() throws RunException {
				
				for(int i = 0; i < Engine.getDefault().getNodes().size(); i++)
				{
					ChordPeer updateVideoNode = (ChordPeer)Engine.getDefault().getNodes().get(i);
					if(updateVideoNode.isPublished() && updateVideoNode.isStarted())
					updateVideoNode.updateVideoBuffer();
				}	
			}

}
