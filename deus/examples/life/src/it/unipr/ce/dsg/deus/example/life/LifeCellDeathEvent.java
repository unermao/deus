package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCellDeathEvent extends NodeEvent {

	int targetCell = 0;
	
	public LifeCellDeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		System.out.println("death");
		// check if this death still makes sense
		// if this death makes sense, do it
		((LifeRegion) associatedNode).grid[targetCell] = 0;
		// if this death creates the conditions for new births or deaths, schedule them 
	}
	
	public void setTargetCell(int targetCell) {
		this.targetCell = targetCell;
	}

}
