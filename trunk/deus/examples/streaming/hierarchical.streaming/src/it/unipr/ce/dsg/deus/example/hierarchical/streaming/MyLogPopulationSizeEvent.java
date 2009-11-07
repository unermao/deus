package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Node;

/**
 * This class represents a logging event. In particular, the population size
 * (number of living simulation nodes) will be printed.
 * 
 * 
 */
public class MyLogPopulationSizeEvent extends Event {

	public MyLogPopulationSizeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		getLogger().info("## Network size: "+ Integer.toString(Engine.getDefault().getNodes().size()));	
	    getLogger().info("## Peers: ");
	    ArrayList<Node> nodes = Engine.getDefault().getNodes();
	    
	    for (int i = 0; i< nodes.size(); i++){
	    	getLogger().info(nodes.get(i).getId() + nodes.get(i).getClass());
	    }
		        
	}

}
