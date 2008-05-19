package it.unipr.ce.dsg.deus.impl.process;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.InvalidReferencesException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;

public class PoissonProcess extends Process {
	private static final String MEAN_ARRIVAL = "meanArrival";

	private float meanArrival = 120;

	public PoissonProcess(String id, Properties params,
			ArrayList<Node> referencedNodes, ArrayList<Event> referencedEvents)
			throws InvalidParamsException, InvalidReferencesException {
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
		float myRandom = (float) (-Math.log(Engine.getDefault().getSimulationRandom()
				.nextFloat()) / lambda);
		return myRandom;
	}
}
