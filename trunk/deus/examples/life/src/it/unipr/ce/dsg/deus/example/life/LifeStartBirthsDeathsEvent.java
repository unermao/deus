package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeStartBirthsDeathsEvent extends NodeEvent {

	public LifeStartBirthsDeathsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		int k = ((LifeRegion)this.associatedNode).getKey();
		System.out.println("startBDevent: k = " + k);
		int sqrtNumeRegions = ((LifeRegion)this.associatedNode).getSqrtNumRegions();
		if (k < (sqrtNumeRegions*sqrtNumeRegions - 1))
			return;
		for (int x = 0; x < sqrtNumeRegions*sqrtNumeRegions; x++) {
			((LifeRegion)Engine.getDefault().getNodeByKey(x)).scheduleBirthsDeaths(triggeringTime);
		}
	}
	
}
