package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.DeathEvent;
import it.unipr.ce.dsg.deus.impl.event.DisconnectionEvent;
import it.unipr.ce.dsg.deus.impl.event.MultipleRandomConnectionsEvent;

import java.util.Properties;


public class RevolBirthEvent extends Event {

	public RevolBirthEvent(String id, Properties params, Process parentProcess)
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
		final RevolNode n = (RevolNode) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateUUID());
		Engine.getDefault().getNodes().add(n);
		
		//System.out.println(n.getC()[0] + " " + n.getC()[1] + " " + n.getC()[2] + " " + n.getC()[3]);
		//System.out.println(n.getFk());
		
		addSchedulerListener(new SchedulerListener() {

			public void newEventScheduled(Event e) {
				if (e instanceof MultipleRandomConnectionsEvent) {
					((MultipleRandomConnectionsEvent) e).setNodeToConnect(n);
				} else if (e instanceof DisconnectionEvent) {
					((DisconnectionEvent) e).setNodesToDisconnect(n, null);
				} else if (e instanceof DeathEvent) {
					((DeathEvent) e).setNodeToKill(n);
				} else if (e instanceof RevolAdaptationEvent) {
					((RevolAdaptationEvent) e).setAssociatedNode(n);
				} else if (e instanceof RevolDiscoveryEvent) {
					((RevolDiscoveryEvent) e).setAssociatedNode(n);
				}
			}

		});
	}

}