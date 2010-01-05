
package it.unipr.ce.dsg.deus.example.nsam;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.nsam.NsamPeer;


public class NsamPeriodicDiscoveredListCheckEvent extends NodeEvent {
	
 private NsamPeer nodeToCheck = null;
 
 public NsamPeriodicDiscoveredListCheckEvent (String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
 }
 

 public void run() throws RunException{
	if (!Engine.getDefault().getNodes().isEmpty())
		for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
		{
			nodeToCheck = (NsamPeer)Engine.getDefault().getNodes().get(i);
			nodeToCheck.checkDiscoveredList();
		}	 
 }
 
}
