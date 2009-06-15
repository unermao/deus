package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;


import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

public class NsamDiscoveryEvent extends NodeEvent {
	
	public NsamDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	
		System.out.println("Discovery !");
		
	}
	
	public Object clone() {
		NsamDiscoveryEvent clone = (NsamDiscoveryEvent) super.clone();

		return clone;
	}
	
public void run() throws RunException {
		
		
	}

}