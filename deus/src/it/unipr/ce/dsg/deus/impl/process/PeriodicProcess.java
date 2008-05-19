package it.unipr.ce.dsg.deus.impl.process;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;

public class PeriodicProcess extends Process {
	private static final String PERIOD = "period";
	
	private float period = 0;
	
	public PeriodicProcess(String id, Properties params,
			ArrayList<Node> referencedNodes, ArrayList<Event> referencedEvents)
			throws InvalidParamsException {
		super(id, params, referencedNodes, referencedEvents);
		initialize();
	}

	@Override
	public float getNextTriggeringTime(float virtualTime) {
		return virtualTime+period;
	}

	@Override
	public void initialize() throws InvalidParamsException {
		if(params.getProperty(PERIOD) == null)
			throw new InvalidParamsException(PERIOD + " param is expected.");
		
		try {
			period = Float.parseFloat(params.getProperty(PERIOD)); 
		}
		catch(NumberFormatException ex) {
			throw new InvalidParamsException(PERIOD + " must be a valid float value.");
		}

	}

}
