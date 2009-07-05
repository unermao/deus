package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCellBirthEvent extends NodeEvent {

	int targetCell = 0;
	
	public LifeCellBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		System.out.println("birth");
		// check if this birth still makes sense
		// if this birth makes sense, do it
		((LifeRegion) associatedNode).grid[targetCell] = 1;
		// if this birth creates the conditions for new births or deaths, schedule them 
	}
	
	public void setTargetCell(int targetCell) {
		this.targetCell = targetCell;
	}

}
