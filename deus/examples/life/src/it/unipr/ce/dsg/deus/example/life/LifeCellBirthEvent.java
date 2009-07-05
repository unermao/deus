package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCellBirthEvent extends NodeEvent {

	int x = 0;
	int y = 0;
	int regionSide = 0;
	
	public LifeCellBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		System.out.println("birth");
		// check if this birth still makes sense
		int count = ((LifeRegion) associatedNode).getNeighboursCellCount(x,y);
		if (count < 2 || count > 3) 
			return;
		// if this birth makes sense, do it
		((LifeRegion) associatedNode).grid[y*regionSide + x] = 1;
		// if this birth creates the conditions for new births or deaths, schedule them 
		// TODO (control neighbor cells and schedule B&D accordingly)
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setRegionSide(int regionSide) {
		this.regionSide = regionSide;
	}

}
