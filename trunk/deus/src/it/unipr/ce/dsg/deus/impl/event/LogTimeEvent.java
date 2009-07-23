package it.unipr.ce.dsg.deus.impl.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * This class is used to print the current virtual time in the simulation
 * 
 * @author  Michele Amoretti
 */

public class LogTimeEvent extends Event {

	public LogTimeEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		System.out.println("Virtual Time: " + Engine.getDefault().getVirtualTime());
	}

}
