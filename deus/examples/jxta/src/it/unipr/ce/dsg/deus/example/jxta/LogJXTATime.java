package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This class is used to print the current virtual time in the simulation
 * </p>
 * 
 * @author  Stefano Sebastio
 */

public class LogJXTATime extends Event {

	public LogJXTATime(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws RunException {
		System.out.println("Virtual Time: " + Engine.getDefault().getVirtualTime());

	}

}
