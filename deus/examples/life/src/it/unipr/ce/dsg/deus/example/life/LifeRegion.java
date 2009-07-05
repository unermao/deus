package it.unipr.ce.dsg.deus.example.life;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;


public class LifeRegion extends Node {

	private static final String REGION_SIDE = "regionSide";
	private static final String INITIAL_ZONES = "initialZones";
	private static final String MEAN_ARRIVAL_BIRTH_DEATH = "meanArrivalBirthDeath";
	private float meanArrivalBirthDeath = 0;
	private int regionSide = 0;
	private int initialZones = 0;
	
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
		this.initRegion();	
	}

	public void initialize() throws InvalidParamsException {
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
		this.initRegion(); 
		return clone;
	}
	
	public void connect() {
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
	}
	
	public void scheduleBirthsDeaths(float triggeringTime) {
		int count = 0;
		for(int k = 0; k<2; ++k){
			for(int y=k; y<regionSide; y=y+2) {
				for(int x=0; x<regionSide; ++x) {
					count = this.getNeighboursCellCount(x,y);
					if (grid[y*regionSide + x] == 1) {
						if (count < 2 || count > 3) {
							// schedule death in grid[y*regionSide + x]
							LifeCellDeathEvent cellDeathEv = (LifeCellDeathEvent) Engine.getDefault().createEvent(
									LifeCellDeathEvent.class,
									triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), 
									meanArrivalBirthDeath));
							cellDeathEv.setAssociatedNode(this);
							cellDeathEv.setTargetCell(y*regionSide + x);
							Engine.getDefault().insertIntoEventsList(cellDeathEv);
						}
						// else survive
					}
					else {
						if (count == 2 || count == 3) {
							// schedule birth in grid[y*regionSide + x]
							LifeCellBirthEvent cellBirthEv = (LifeCellBirthEvent) Engine.getDefault().createEvent(
									LifeCellBirthEvent.class,
									triggeringTime + expRandom(Engine.getDefault().getSimulationRandom(), 
									meanArrivalBirthDeath));
							cellBirthEv.setAssociatedNode(this);
							cellBirthEv.setTargetCell(y*regionSide + x);
							Engine.getDefault().insertIntoEventsList(cellBirthEv);
						}
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
	
	public void updateRegion(boolean random) {		
		int count;
		if(random) {
			Random r = new Random();		
			int x = r.nextInt(regionSide -1);
			int y = r.nextInt(regionSide -1);
			count = this.getNeighboursCellCount(x,y);
			if (count == 2 || count == 3)
				this.grid[y*regionSide + x] = 1;
			else
				this.grid[y*regionSide + x] = 0;
		} else {
			for(int k = 0; k<2; ++k){
				for(int y=k; y<regionSide; y=y+2) {
					for(int x=0; x<regionSide; ++x) {
						count = this.getNeighboursCellCount(x,y);
						if (count == 2 || count == 3)
							this.grid[y*regionSide + x] = 1;
						else
							this.grid[y*regionSide + x] = 0;
					}
				}
			}
		}
	}
	
	public int getNeighboursCellCount(int x, int y) {
		int count = 0;
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
		
		return count;
	}
	
	public int getCellValue(int x, int y) {
		return this.grid[y*regionSide + x];
	}
	
	private void initRegion() {
		Random r = new Random();
		for(int i=0; i<initialZones; ++i) {
			int row = r.nextInt(regionSide -2) + 1;
			int col = r.nextInt(regionSide -2) + 1;		
			grid[row*regionSide + col] = 1;
			grid[row*regionSide + col + 1] = 1;
			grid[(row+1)*regionSide + col + 1] = 1;
			grid[(row+1)*regionSide + col] = 1;
		}
	}

}
