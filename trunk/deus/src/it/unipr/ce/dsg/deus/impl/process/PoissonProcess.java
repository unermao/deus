package it.unipr.ce.dsg.deus.impl.process;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class represents a generic Poisson process. It accept one parameter
 * called "meanArrival" (float) that is used to generate the triggering time.
 * Each time the process receives a request for generating a new triggering
 * time, it will compute it by adding the current simulation virtual the value
 * of an Homogeneous Poisson Process with the rate parameter calculated as
 * 1/meanArrival time.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class PoissonProcess extends Process {
	private static final String MEAN_ARRIVAL = "meanArrival";

	private float meanArrival = 0;

	public PoissonProcess(String id, Properties params,
			ArrayList<Node> referencedNodes, ArrayList<Event> referencedEvents)
			throws InvalidParamsException {
		super(id, params, referencedNodes, referencedEvents);
		initialize();
	}

	public float getMeanArrival() {
		return meanArrival;
	}

	@Override
	public float getNextTriggeringTime(float virtualTime) {
		return virtualTime + expRandom((float) 1 / meanArrival);
	}

	@Override
	public void initialize() throws InvalidParamsException {
		if (params.getProperty(MEAN_ARRIVAL) == null)
			throw new InvalidParamsException(MEAN_ARRIVAL
					+ " param is expected.");

		try {
			meanArrival = Float.parseFloat(params.getProperty(MEAN_ARRIVAL));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(MEAN_ARRIVAL
					+ " must be a valid float value.");
		}
	}

	// returns exponentially distributed random variable
	private float expRandom(float lambda) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) / lambda);
		return myRandom;
	}
}
