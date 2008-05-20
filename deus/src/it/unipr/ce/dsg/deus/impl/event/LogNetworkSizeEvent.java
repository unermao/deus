package it.unipr.ce.dsg.deus.impl.event;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LogNetworkSizeEvent extends Event {

	public LogNetworkSizeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {

	}

	@Override
	public void run() throws RunException {
		getLogger().info("## Network size: " + Integer.toString(Engine.getDefault().getNodes().size()));
	}

}
