package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Engine;

public class ChordPlayVideoEvent extends NodeEvent{
	
	public ChordPlayVideoEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	@Override
	public void run() throws RunException {

		for(int i = 0; i < Engine.getDefault().getNodes().size(); i++)
		{
			ChordPeer playingVideoNode = (ChordPeer)Engine.getDefault().getNodes().get(i);
			if(playingVideoNode.isPublished())
			playingVideoNode.playVideoBuffer();
		}	
	}
	
}