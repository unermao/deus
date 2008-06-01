package it.unipr.ce.dsg.deus.impl.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

import java.util.Properties;

public class BirthEvent extends Event {

	public BirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {
		//System.out.println("birth -- N = " + Engine.getDefault().getNodes().size());
		if (getParentProcess() == null)
			throw new RunException(
					"A parent process must be set in order to run "
							+ getClass().getCanonicalName());
		// create a node (the type is randomly chosen among those which are
		// associated to the process)
		final Node n = (Node) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateUUID());
		Engine.getDefault().getNodes().add(n);
		
		addSchedulerListener(new SchedulerListener() {

			public void newEventScheduled(Event e) {
				if (e instanceof SingleConnectionEvent) {
					((SingleConnectionEvent) e).setNodesToConnect(n, null);
				} else if (e instanceof DisconnectionEvent) {
					((DisconnectionEvent) e).setNodesToDisconnect(n, null);
				} else if (e instanceof DeathEvent) {
					((DeathEvent) e).setNodeToKill(n);
				}
			}

		});
	}

}
