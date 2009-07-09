package it.unipr.ce.dsg.deus.example.life;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;


public class LifeRegion extends Node {

	private static final String SQRT_NUM_REGIONS = "sqrtNumRegions";
	private static final String REGION_SIDE = "regionSide";
	private static final String INITIAL_ZONES = "initialZones";
	private static final String MEAN_ARRIVAL_BIRTH_DEATH = "meanArrivalBirthDeath";
	private int sqrtNumRegions = 0;
	private int regionSide = 0;
	private int initialZones = 0;
	private float meanArrivalBirthDeath = 0;
	
	public Integer[] neighbourRegions = new Integer[8];
	
	public int[] grid = null;
	public LifeRegionPanel regionPanel = null;
	public static LifeGUI l = null;
	
	public LifeRegion(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
		initialize();		
		grid = new int[regionSide * regionSide];
		regionPanel = new LifeRegionPanel(regionSide);	
	}

	public void initialize() throws InvalidParamsException {
		if (params.getProperty(SQRT_NUM_REGIONS) == null)
			throw new InvalidParamsException(SQRT_NUM_REGIONS + " param is expected.");
		try {
			sqrtNumRegions = (int)Double.parseDouble(params.getProperty(SQRT_NUM_REGIONS));		
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(SQRT_NUM_REGIONS + " must be a valid int value.");
		}
		if (params.getProperty(REGION_SIDE) == null)
			throw new InvalidParamsException(REGION_SIDE + " param is expected.");
		try {
			regionSide = (int)Double.parseDouble(params.getProperty(REGION_SIDE));		
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(REGION_SIDE + " must be a valid int value.");
		}
		if (params.getProperty(INITIAL_ZONES) == null)
			throw new InvalidParamsException(INITIAL_ZONES + " param is expected.");
		try {
			initialZones = (int)Double.parseDouble(params.getProperty(INITIAL_ZONES));		
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(INITIAL_ZONES + " must be a valid int value.");
		}
		if (params.containsKey(MEAN_ARRIVAL_BIRTH_DEATH)) {
			try {
				meanArrivalBirthDeath = Float.parseFloat(params
						.getProperty(MEAN_ARRIVAL_BIRTH_DEATH));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(
						MEAN_ARRIVAL_BIRTH_DEATH
								+ " must be a valid float value.");
			}
		}
	}

	public Object clone() {
		LifeRegion clone = (LifeRegion) super.clone();
		neighbourRegions = new Integer[8]; 
		grid = new int[regionSide * regionSide]; 
		regionPanel = new LifeRegionPanel(regionSide); 
		return clone;
	}
	
	public int getSqrtNumRegions() {
		return sqrtNumRegions;
	}
	
	public void connect() {
		int k = this.getKey();
		if (k < (sqrtNumRegions*sqrtNumRegions - 1))
			return;
		for (int x = 0; x < sqrtNumRegions*sqrtNumRegions; x++) {
			
			if (x - sqrtNumRegions - 1 < 0)
				if (x-1 >= 0)
					if (x%sqrtNumRegions != 0)
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[0] = x - 1 + sqrtNumRegions*(sqrtNumRegions-1);
					else
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[0] = x - 1;
				else
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[0] = x + (sqrtNumRegions-1) + sqrtNumRegions*(sqrtNumRegions-1);
			else
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[0] = x - sqrtNumRegions - 1;
			System.out.println("x = " + x + ", n0 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[0]);
			
			if (x - sqrtNumRegions < 0)
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[1] = x + sqrtNumRegions*(sqrtNumRegions-1);
			else
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[1] = x - sqrtNumRegions;
			System.out.println("x = " + x + ", n1 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[1]);
			
			if (x - sqrtNumRegions + 1 < 0)
				if (x+1+sqrtNumRegions*(sqrtNumRegions-1) <= sqrtNumRegions*sqrtNumRegions - 1)
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2] = x + 1 + sqrtNumRegions*(sqrtNumRegions-1);
				else 
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2] = x + sqrtNumRegions*(sqrtNumRegions-1) - sqrtNumRegions;
			else
				if ((x+1)%sqrtNumRegions != 0)
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2] = x - sqrtNumRegions + 1;
				else
					if (x - (sqrtNumRegions-1) == 0)
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2] = sqrtNumRegions*(sqrtNumRegions-1);
					else
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2] = x - (sqrtNumRegions-1) - sqrtNumRegions;
			System.out.println("x = " + x + ", n2 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[2]);
			
			if ((x+1)%sqrtNumRegions == 0)
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[3] = x - sqrtNumRegions + 1;
			else
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[3] = x + 1;
			System.out.println("x = " + x + ", n3 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[3]);
			
			if ((x + sqrtNumRegions + 1)%sqrtNumRegions == 0)
				if (x+1 < sqrtNumRegions*sqrtNumRegions - 1)
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[4] = x + 1;
				else
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[4] = 0;
			else
				if (x + sqrtNumRegions + 1 > sqrtNumRegions*sqrtNumRegions - 1)
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[4] = x +1 - sqrtNumRegions*(sqrtNumRegions-1);
				else
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[4] = x + sqrtNumRegions + 1;
			System.out.println("x = " + x + ", n4 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[4]);
			
			if (x + sqrtNumRegions > sqrtNumRegions*sqrtNumRegions - 1)
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[5] = x - sqrtNumRegions*(sqrtNumRegions-1);
			else
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[5] = x + sqrtNumRegions;
			System.out.println("x = " + x + ", n5 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[5]);
			
			if (x + sqrtNumRegions - 1 > sqrtNumRegions*sqrtNumRegions - 1)
				if (x%sqrtNumRegions != 0)
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6] = x - 1 - sqrtNumRegions*(sqrtNumRegions-1);
				else
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6] = sqrtNumRegions - 1;
			else
				if ((x + sqrtNumRegions)%sqrtNumRegions == 0)
					if (x == sqrtNumRegions*(sqrtNumRegions-1))
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6] = sqrtNumRegions - 1;
					else
						((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6] = x + sqrtNumRegions + sqrtNumRegions - 1;
				else
					((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6] = x + sqrtNumRegions - 1;
			System.out.println("x = " + x + ", n6 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[6]);
			
			if (x%sqrtNumRegions == 0)
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[7] = x + sqrtNumRegions - 1;
			else	
				((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[7] = x - 1;
			System.out.println("x = " + x + ", n7 = " + ((LifeRegion)Engine.getDefault().getNodeByKey(x)).neighbourRegions[7]);
		}
		
		/*
		switch (this.getKey()) {
			case 0:
				break;
			case 1:
				this.neighbourRegions[6] = 0;
				((LifeRegion)Engine.getDefault().getNodeByKey(0)).neighbourRegions[1] = this.getKey();
				break;
			case 2:
				this.neighbourRegions[0] = 0;
				this.neighbourRegions[1] = 1;
				((LifeRegion)Engine.getDefault().getNodeByKey(0)).neighbourRegions[4] = this.getKey();
				((LifeRegion)Engine.getDefault().getNodeByKey(1)).neighbourRegions[5] = this.getKey();
				break;
			case 3:
				this.neighbourRegions[6] = 2;
				this.neighbourRegions[7] = 0;
				this.neighbourRegions[0] = 1;				
				((LifeRegion)Engine.getDefault().getNodeByKey(0)).neighbourRegions[3] = this.getKey();
				((LifeRegion)Engine.getDefault().getNodeByKey(1)).neighbourRegions[4] = this.getKey();
				((LifeRegion)Engine.getDefault().getNodeByKey(2)).neighbourRegions[2] = this.getKey();
				break;
			
			default:
				break;
		}
		*/
	}
	
	public void scheduleBirthsDeaths(float triggeringTime) {
		System.out.println("schedule for node: " + this);
		System.out.println("meanArrival " + meanArrivalBirthDeath);
		int count = 0;
		for (int x=0; x<regionSide-1; x++) {
			for (int y=0; y<regionSide-1; y++) {
				count = this.getNeighboursCellCount(x,y);
				
				if (grid[y*regionSide + x] == 1) {
					if (count < 2 || count > 3) {
						//System.out.println("x = " + x + ", y = " + y);
						//System.out.println("(1) count = " + count);
						// schedule death in grid[y*regionSide + x]
						LifeCellDeathEvent cellDeathEv = (LifeCellDeathEvent) Engine.getDefault().createEvent(
								LifeCellDeathEvent.class,
								triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), meanArrivalBirthDeath));
						cellDeathEv.setAssociatedNode(this);
						cellDeathEv.setMeanArrival(meanArrivalBirthDeath);
						//System.out.println("mean arr death: " + cellDeathEv.getMeanArrival());
						cellDeathEv.setX(x);
						cellDeathEv.setY(y);
						cellDeathEv.setRegionSide(regionSide);
						Engine.getDefault().insertIntoEventsList(cellDeathEv);
					}
					// else survive
				}
				else {
					if (count == 2 || count == 3) {
						//System.out.println("x = " + x + ", y = " + y);
						//System.out.println("(0) count = " + count);
						// schedule birth in grid[y*regionSide + x]
						LifeCellBirthEvent cellBirthEv = (LifeCellBirthEvent) Engine.getDefault().createEvent(
								LifeCellBirthEvent.class,
								triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), meanArrivalBirthDeath));
						cellBirthEv.setAssociatedNode(this);
						cellBirthEv.setMeanArrival(meanArrivalBirthDeath);
						//System.out.println("mean arr birth: " + cellBirthEv.getMeanArrival());
						cellBirthEv.setX(x);
						cellBirthEv.setY(y);
						cellBirthEv.setRegionSide(regionSide);
						Engine.getDefault().insertIntoEventsList(cellBirthEv);				
					}
				}
			}
		}
	}
	
	/**
	 * returns exponentially distributed random variable
	 */
	private float expRandom(Random random, float meanValue) {
		float myRandom = (float) (-Math.log(1-random.nextFloat()) * meanValue);
		return myRandom;
	}
	
	public void updateRegionPanel() {
		regionPanel.updateGrid(this.grid);
	}
	
	public int getNeighboursCellCount(int x, int y) {
		int count = 0;
		count += this.getCellValue(x-1, y-1); // NO
		count += this.getCellValue(x, y-1); // N
		count += this.getCellValue(x+1, y-1); // NE
		count += this.getCellValue(x+1, y); // E
		count += this.getCellValue(x+1, y+1); // SE
		count += this.getCellValue(x, y+1); // S
		count += this.getCellValue(x-1, y+1); // SO
		count += this.getCellValue(x-1, y); // O
		
		return count;
		
		/*
		if(x==0 && y==0) {
			count += this.getCellValue(x+1, y);
			count += this.getCellValue(x+1, y+1);
			count += this.getCellValue(x, y+1);
			if(this.neighbourRegions[6] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y+1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y);
			}
			if(this.neighbourRegions[7] != null)
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[7])).getCellValue(regionSide-1, regionSide-1);		
			if(this.neighbourRegions[0] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x, regionSide-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x+1, regionSide-1);
			}
		} else if(x!=0 && x!=(regionSide-1) && y==0) {
			count += this.getCellValue(x+1, y);
			count += this.getCellValue(x+1, y+1);
			count += this.getCellValue(x, y+1);
			count += this.getCellValue(x-1, y+1);
			count += this.getCellValue(x-1, y);
			if(this.neighbourRegions[0] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x-1, regionSide-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x, regionSide-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x+1, regionSide-1);
			}
		} else if(x==(regionSide-1) && y==0) {
			count += this.getCellValue(x, y+1);
			count += this.getCellValue(x-1, y+1);
			count += this.getCellValue(x-1, y);
			if(this.neighbourRegions[0] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x-1, regionSide-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(x, regionSide-1);
			}
			if(this.neighbourRegions[1] != null)
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[1])).getCellValue(0,regionSide-1);		
			if(this.neighbourRegions[2] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y+1);
			}					
		} else if(x==0 && y!=0 && y!=(regionSide-1)) {
			count += this.getCellValue(x, y-1);
			count += this.getCellValue(x+1, y-1);
			count += this.getCellValue(x+1, y);
			count += this.getCellValue(x+1, y+1);
			count += this.getCellValue(x, y+1);
			if(this.neighbourRegions[6] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y+1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y-1);
			}
		} else if(x==(regionSide-1) && y!=0 && y!=(regionSide-1)) {
			count += this.getCellValue(x, y+1);
			count += this.getCellValue(x-1, y+1);
			count += this.getCellValue(x-1, y);
			count += this.getCellValue(x-1, y-1);
			count += this.getCellValue(x, y-1);
			if(this.neighbourRegions[2] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y+1);
			}
		} else if(x==0 && y==(regionSide-1)) {
			count += this.getCellValue(x, y-1);
			count += this.getCellValue(x+1, y-1);
			count += this.getCellValue(x+1, y);
			if(this.neighbourRegions[4] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x+1,0);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x,0);
			}
			if(this.neighbourRegions[5] != null)
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[5])).getCellValue(regionSide-1,0);

			if(this.neighbourRegions[6] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1,y-1);
			}
		} else if(x!=0 && x!=(regionSide-1) && y==(regionSide-1)) {
			count += this.getCellValue(x-1, y);
			count += this.getCellValue(x-1, y-1);
			count += this.getCellValue(x, y-1);
			count += this.getCellValue(x+1, y-1);
			count += this.getCellValue(x+1, y);
			if(this.neighbourRegions[4] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x+1,0);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x,0);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x-1,0);
			}
		} else if(x==(regionSide-1) && y==(regionSide-1)) {
			count += this.getCellValue(x-1, y);
			count += this.getCellValue(x-1, y-1);
			count += this.getCellValue(x, y-1);
			if(this.neighbourRegions[2] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y-1);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(0, y);
			}
			if(this.neighbourRegions[3] != null)
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[3])).getCellValue(0, 0);
			if(this.neighbourRegions[4] != null) {
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x,0);
				count += ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x-1,0);
			}
		} else {
			count += this.getCellValue(x, y-1);
			count += this.getCellValue(x+1, y-1);
			count += this.getCellValue(x+1, y);
			count += this.getCellValue(x+1, y+1);
			count += this.getCellValue(x, y+1);
			count += this.getCellValue(x-1, y+1);
			count += this.getCellValue(x-1, y);
			count += this.getCellValue(x-1, y-1);
		}
		*/
	}
	
	public int getCellValue(int x, int y) {
		if ( ((x >= 0) && (x <= regionSide-1)) && ((y >= 0) && (y <= regionSide-1)) )
			return this.grid[y*regionSide + x];
		else if ( (x < 0) && (y < 0) ) // NO
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[0])).getCellValue(regionSide-1+x, regionSide-1+y);
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y < 0) )  // N
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[1])).getCellValue(x, regionSide-1+y);
		else if ( (x > regionSide-1) && (y < 0) ) // NE
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[2])).getCellValue(x - (regionSide-1), regionSide-1+y);
		else if ( (x > regionSide-1) && ((y >= 0) && (y <= regionSide-1)) ) // E
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[3])).getCellValue(x - (regionSide-1), y);
		else if ( (x > regionSide-1) && (y > regionSide-1) ) // SE
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[4])).getCellValue(x - (regionSide-1), y - (regionSide-1));
		else if ( ((x >= 0) && (x <= regionSide-1)) && (y > regionSide-1) ) // S
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[5])).getCellValue(x, y - (regionSide-1));
		else if ( (x < 0) && (y > regionSide-1) ) // SO
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[6])).getCellValue(regionSide-1+x, y - (regionSide-1));
		else if ( (x < 0) && ((y >= 0) && (y <= regionSide-1)) ) // O
			return ((LifeRegion)Engine.getDefault().getNodeByKey(this.neighbourRegions[7])).getCellValue(regionSide-1+x, y);
		return -1;
	}
	
	public void initRegion() {
		Random r = Engine.getDefault().getSimulationRandom();
		for(int i=0; i<initialZones-1; i++) {
			int row = r.nextInt(regionSide);
			int col = r.nextInt(regionSide);	
			//System.out.println("x = " + col + " y = " + row);
			grid[row*regionSide + col] = 1;
		}
	}

}
