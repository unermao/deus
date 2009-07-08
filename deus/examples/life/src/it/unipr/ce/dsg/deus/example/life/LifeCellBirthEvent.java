package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCellBirthEvent extends NodeEvent {

	private float meanArrival = 0;
	int x = 0;
	int y = 0;
	int regionSide = 0;
	
	public LifeCellBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		//System.out.println("birth in node" + associatedNode);
		
		// check if this birth still makes sense
		int count = ((LifeRegion) associatedNode).getNeighboursCellCount(x,y);
		if (count < 2 || count > 3) 
			return;
		
		// if this birth makes sense, do it
		((LifeRegion) associatedNode).grid[y*regionSide + x] = 1;
		
		// if this birth creates the conditions for new births or deaths, schedule them 
			System.out.println("y = " + y);			
			if (y == 0) {
				// TODO .. lo stato di (x,y-1) va chiesto al nodo..
				
			} else {
				count = ((LifeRegion) associatedNode).getNeighboursCellCount(x, y-1);
				if (((LifeRegion) associatedNode).getCellValue(x, y-1) == 1) {			
					if (count < 2 || count > 3) 
						generateCellDeathEvent((LifeRegion) associatedNode, x, y-1);
				}
				else {
					if (count == 2 || count == 3) 
						generateCellBirthEvent((LifeRegion) associatedNode, x, y-1);
				}
			}
						
			if ((x == regionSide - 1) || (y == 0)) {
				// TODO ..
				System.out.println("x == regionSide - 1 OR y == 0");
			}
			else {
				count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y-1);
				if (((LifeRegion) associatedNode).getCellValue(x+1, y-1) == 1) {			
					if (count < 2 || count > 3)
						generateCellDeathEvent((LifeRegion) associatedNode, x+1, y-1);
				}
				else {
					if (count == 2 || count == 3) 
						generateCellBirthEvent((LifeRegion) associatedNode, x+1, y-1);
				}
			}
		
		if (x < regionSide - 1) {
			count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y);
			if (((LifeRegion) associatedNode).getCellValue(x+1, y) == 1) {			
				if (count < 2 || count > 3)
					generateCellDeathEvent((LifeRegion) associatedNode, x+1, y);
			}
			else {
				if (count == 2 || count == 3) 
					generateCellBirthEvent((LifeRegion) associatedNode, x+1, y);
			}
			if (y < regionSide - 1) {
				count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y+1);
				if (((LifeRegion) associatedNode).getCellValue(x+1, y+1) == 1) {			
					if (count < 2 || count > 3)
						generateCellDeathEvent((LifeRegion) associatedNode, x+1, y+1);
				}
				else {
					if (count == 2 || count == 3) 
						generateCellBirthEvent((LifeRegion) associatedNode, x+1, y+1);
				}	
			}
		}
		if (y < regionSide - 1) {
			count = ((LifeRegion) associatedNode).getNeighboursCellCount(x, y+1);
			if (((LifeRegion) associatedNode).getCellValue(x, y+1) == 1) {			
				if (count < 2 || count > 3)
					generateCellDeathEvent((LifeRegion) associatedNode, x, y+1);
			}
			else {
				if (count == 2 || count == 3) 
					generateCellBirthEvent((LifeRegion) associatedNode, x, y+1);
			}	
			if (x >= 1) {
				count = ((LifeRegion) associatedNode).getNeighboursCellCount(x-1, y+1);
				if (((LifeRegion) associatedNode).getCellValue(x-1, y+1) == 1) {			
					if (count < 2 || count > 3)
						generateCellDeathEvent((LifeRegion) associatedNode, x-1, y+1);
				}
				else {
					if (count == 2 || count == 3) 
						generateCellBirthEvent((LifeRegion) associatedNode, x-1, y+1);
				}	
			}
		}
		if (x >= 1) {
			count = ((LifeRegion) associatedNode).getNeighboursCellCount(x-1, y);
			if (((LifeRegion) associatedNode).getCellValue(x-1, y) == 1) {			
				if (count < 2 || count > 3)
					generateCellDeathEvent((LifeRegion) associatedNode, x-1, y);
			}
			else {
				if (count == 2 || count == 3) 
					generateCellBirthEvent((LifeRegion) associatedNode, x-1, y);
			}	
			if (y >= 1) {
				count = ((LifeRegion) associatedNode).getNeighboursCellCount(x-1, y-1);
				if (((LifeRegion) associatedNode).getCellValue(x-1, y-1) == 1) {			
					if (count < 2 || count > 3)
						generateCellDeathEvent((LifeRegion) associatedNode, x-1, y-1);
				}
				else {
					if (count == 2 || count == 3) 
						generateCellBirthEvent((LifeRegion) associatedNode, x-1, y-1);
				}		
			}
		}
	}
	
	private void generateCellBirthEvent(Node associatedNode, int x, int y) {
		LifeCellBirthEvent cellBirthEv = (LifeCellBirthEvent) Engine.getDefault().createEvent(
				LifeCellBirthEvent.class,
				triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), 
				meanArrival));
		cellBirthEv.setAssociatedNode(associatedNode);
		cellBirthEv.setMeanArrival(meanArrival);
		cellBirthEv.setX(x);
		cellBirthEv.setY(y);
		cellBirthEv.setRegionSide(regionSide);
		Engine.getDefault().insertIntoEventsList(cellBirthEv);
	}
	
	private void generateCellDeathEvent(Node associatedNode, int x, int y) {
		LifeCellDeathEvent cellDeathEv = (LifeCellDeathEvent) Engine.getDefault().createEvent(
				LifeCellDeathEvent.class,
				triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), 
				meanArrival));
		cellDeathEv.setAssociatedNode(associatedNode);
		cellDeathEv.setMeanArrival(meanArrival);
		cellDeathEv.setX(x);
		cellDeathEv.setY(y);
		cellDeathEv.setRegionSide(regionSide);
		Engine.getDefault().insertIntoEventsList(cellDeathEv);
	}
	
	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
	}
	
	public void setMeanArrival(float meanArrival) {
		this.meanArrival = meanArrival;
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
