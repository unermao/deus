package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LogChordForPajekEvent extends Event {

	public LogChordForPajekEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("##### Network links:");
		Collections.sort(Engine.getDefault().getNodes());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			for (int i = 0; i < n.getFingerTable().length; i++)
			{
				if(n.getFingerTable()[i] != null)
				getLogger().info((n.getKey()+1) + " " + (n.getFingerTable()[i].getKey()+1));
			}
		}
	}

}
