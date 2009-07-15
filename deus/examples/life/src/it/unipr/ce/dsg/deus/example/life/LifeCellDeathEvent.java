package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCellDeathEvent extends NodeEvent {

	private float meanArrival = 0;
	private int x = 0;
	private int y = 0;
	private int regionSide = 0;
	
	public LifeCellDeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		// check if this death still makes sense
		int count = ((LifeRegion) associatedNode).getNeighboursCellCount(x,y);
		if (count == 2 || count == 3) 
			return;
		
		// if this death makes sense, do it
		((LifeRegion) associatedNode).grid[y*regionSide + x] = 0;
		
		// if this death creates the conditions for new births or deaths, schedule them 
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x, y-1);
		if (((LifeRegion) associatedNode).getCellValue(x, y-1) == 1) {			
			if (count < 2 || count > 3) 
				generateCellDeathEvent((LifeRegion) associatedNode, x, y-1);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x, y-1);
		}
			
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y-1);
		if (((LifeRegion) associatedNode).getCellValue(x+1, y-1) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x+1, y-1);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x+1, y-1);
		}
	
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y);
		if (((LifeRegion) associatedNode).getCellValue(x+1, y) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x+1, y);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x+1, y);
		}
			
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x+1, y+1);
		if (((LifeRegion) associatedNode).getCellValue(x+1, y+1) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x+1, y+1);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x+1, y+1);
		}	
			
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x, y+1);
		if (((LifeRegion) associatedNode).getCellValue(x, y+1) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x, y+1);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x, y+1);
		}	
			
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x-1, y+1);
		if (((LifeRegion) associatedNode).getCellValue(x-1, y+1) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x-1, y+1);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x-1, y+1);
		}	
		
		count = ((LifeRegion) associatedNode).getNeighboursCellCount(x-1, y);
		if (((LifeRegion) associatedNode).getCellValue(x-1, y) == 1) {			
			if (count < 2 || count > 3)
				generateCellDeathEvent((LifeRegion) associatedNode, x-1, y);
		}
		else {
			if (count == 2 || count == 3) 
				generateCellBirthEvent((LifeRegion) associatedNode, x-1, y);
		}	
			
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
	
	private void generateCellBirthEvent(Node associatedNode, int x, int y) {
		LifeCellBirthEvent cellBirthEv = (LifeCellBirthEvent) Engine.getDefault().createEvent(
				LifeCellBirthEvent.class,
				triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), meanArrival));
		cellBirthEv.setAssociatedNode(associatedNode);
		if ( ((x >= 0) && (x <= regionSide-1)) && ((y >= 0) && (y <= regionSide-1)) ) {
			cellBirthEv.setAssociatedNode(associatedNode);
			cellBirthEv.setX(x);
			cellBirthEv.setY(y);
		}
		else if ( (x < 0) && (y < 0) ) { // NO
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[0]));
			cellBirthEv.setX(regionSide+x);
			cellBirthEv.setY(regionSide+y);
		}
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y < 0) ) { // N
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[1]));
			cellBirthEv.setX(x);
			cellBirthEv.setY(regionSide+y);
		}
		else if ( (x > regionSide-1) && (y < 0) ) { // NE
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[2]));
			cellBirthEv.setX(x - regionSide);
			cellBirthEv.setY(regionSide+y);
		}
		else if ( (x > regionSide-1) && ((y >= 0) && (y <= regionSide-1)) ) { // E
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[3]));
			cellBirthEv.setX(x - regionSide);
			cellBirthEv.setY(y);
		}
		else if ( (x > regionSide-1) && (y > regionSide-1) ) { // SE
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[4]));
			cellBirthEv.setX(x - regionSide);
			cellBirthEv.setY(y - regionSide);
		}
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y > regionSide-1) ) { // S
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[5]));
			cellBirthEv.setX(x);
			cellBirthEv.setY(y - regionSide);
		}
		else if ( (x < 0) && (y > regionSide-1) ) { // SO
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[6]));
			cellBirthEv.setX(regionSide+x);
			cellBirthEv.setY(y - regionSide);
		}
		else if ( (x < 0) && ((y >= 0) && (y <= regionSide-1)) ) { // O
			cellBirthEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[7]));
			cellBirthEv.setX(regionSide+x);
			cellBirthEv.setY(y);
		}
		cellBirthEv.setMeanArrival(meanArrival);
		cellBirthEv.setRegionSide(regionSide);
		Engine.getDefault().insertIntoEventsList(cellBirthEv);
	}
	
	private void generateCellDeathEvent(Node associatedNode, int x, int y) {
		LifeCellDeathEvent cellDeathEv = (LifeCellDeathEvent) Engine.getDefault().createEvent(
				LifeCellDeathEvent.class,
				triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), 
				meanArrival));
		if ( ((x >= 0) && (x <= regionSide-1)) && ((y >= 0) && (y <= regionSide-1)) ) {
			cellDeathEv.setAssociatedNode(associatedNode);
			cellDeathEv.setX(x);
			cellDeathEv.setY(y);
		}
		else if ( (x < 0) && (y < 0) ) { // NO
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[0]));
			cellDeathEv.setX(regionSide+x);
			cellDeathEv.setY(regionSide+y);
		}
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y < 0) ) { // N
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[1]));
			cellDeathEv.setX(x);
			cellDeathEv.setY(regionSide+y);
		}
		else if ( (x > regionSide-1) && (y < 0) ) { // NE
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[2]));
			cellDeathEv.setX(x - regionSide);
			cellDeathEv.setY(regionSide+y);
		}
		else if ( (x > regionSide-1) && ((y >= 0) && (y <= regionSide-1)) ) { // E
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[3]));
			cellDeathEv.setX(x - regionSide);
			cellDeathEv.setY(y);
		}
		else if ( (x > regionSide-1) && (y > regionSide-1) ) { // SE
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[4]));
			cellDeathEv.setX(x - regionSide);
			cellDeathEv.setY(y - regionSide);
		}
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y > regionSide-1) ) { // S
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[5]));
			cellDeathEv.setX(x);
			cellDeathEv.setY(y - regionSide);
		}
		else if ( (x < 0) && (y > regionSide-1) ) { // SO
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[6]));
			cellDeathEv.setX(regionSide+x);
			cellDeathEv.setY(y - regionSide);
		}
		else if ( (x < 0) && ((y >= 0) && (y <= regionSide-1)) ) { // O
			cellDeathEv.setAssociatedNode((LifeRegion)Engine.getDefault().getNodeByKey(((LifeRegion) associatedNode).neighbourRegions[7]));
			cellDeathEv.setX(regionSide+x);
			cellDeathEv.setY(y);
		}
		cellDeathEv.setMeanArrival(meanArrival);
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
	
	public float getMeanArrival() {
		return this.meanArrival;
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
