package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sun.xml.internal.ws.api.pipe.NextAction;

public class SwitchStationController {

	private String pathFileName = null;
	private String switchStationFileName = null;
	private ArrayList<CityPath> pathList = null;
	private ArrayList<SwitchStation> switchStationList = null;
	private ArrayList<CityPathPoint> locationList = null;
	
	/**
	 * 
	 * @param pathFileName
	 * @param switchStationFileName
	 */
	public SwitchStationController(String switchStationFileName,String pathFileName) {
		super();
		this.pathFileName = pathFileName;
		this.switchStationFileName = switchStationFileName;
		this.pathList = new ArrayList<CityPath>();
		this.switchStationList = new ArrayList<SwitchStation>();
		this.locationList = new ArrayList<CityPathPoint>();
	}

	/**
	 * 
	 * @param ss
	 * @return
	 */
	public ArrayList<CityPath> getPathListFromSwithStation(SwitchStation ss)
	{
		 ArrayList<CityPath> pList = new ArrayList<CityPath>();
		 
		 for(int i=0; i<this.pathList.size();i++)
		 {
			 if(this.pathList.get(i).getStartPoint().equals(ss))
				 pList.add(this.pathList.get(i));
		 }
		 
		 //System.out.println("SwitchStationController Returning List of Path with:"+pList.size()+" elements");
		 
		 return pList;
	}
	
	/**
	 * 
	 * @param geoLocation
	 * @param geoLocation2
	 * @return
	 */
	public CityPath getPathBetweenPoints(GeoLocation geoLocation, GeoLocation geoLocation2)
	{
		 
		 for(int i=0; i<this.pathList.size();i++)
		 {
			 if(this.pathList.get(i).getStartPoint().equals(geoLocation) && this.pathList.get(i).getEndPoint().equals(geoLocation2))
				 return this.pathList.get(i);
		 }
		
		return null;
	}
	
	
	/**
	 * 
	 */
	public void readSwitchStationFile()
	{
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.switchStationFileName))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{	
				String[] coordinates = line.split(",");
				
				double lat = Double.parseDouble(coordinates[0]);
				double lon = Double.parseDouble(coordinates[1]);
					
				SwitchStation ss = new SwitchStation(lat, lon);
				
				this.switchStationList.add(ss);
	
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Switch Station: " + this.switchStationList.size());
	}
	
	/**
	 * 
	 */
	public void readPathFile()
	{
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.pathFileName))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{	
				String[] points = line.split("#"); 
				
				CityPath path = new CityPath();
				
				for(int i=0; i<points.length; i++)
				{
					String[] coordinates = points[i].split(",");
					
					double lat = Double.parseDouble(coordinates[0]);
					double lon = Double.parseDouble(coordinates[1]);
					
					//GeoLocation point = new GeoLocation(lat, lon, 0);
					CityPathPoint point = new CityPathPoint(lat, lon);
					
					//If the point already exist in the locationList
					if(!this.locationList.contains(point))
						this.locationList.add(point);
					
					int index = this.locationList.indexOf(point);
					
					//Add new point to path
					path.addCityPathPoint(this.locationList.get(index));
					
					//Increment Path Length
					if(path.getPathPoints().size() >= 2)
					{
						CityPathPoint point1 = path.getPathPoints().get(path.getPathPoints().size()-1);
						CityPathPoint point2 = path.getPathPoints().get(path.getPathPoints().size()-2);
						
						double distance = GeoDistance.distance(point1, point2);
						
						if(!( !(distance > 0.0) && distance !=0.0 && !(distance <0.0) ))
								path.incrementPathLength(distance);
					}
					
					//path.addCityPathPoint(point);
				}
				
				this.pathList.add(path);
				
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("City Paths: " + this.pathList.size());
	}

	/**
	 * 
	 * @param numOfInterestedPath
	 */
	public void addMultipleBadSurfaceCondition(int numOfInterestedPath)
	{
		System.out.println("Adding Multiple Bad Surface Condition to " + numOfInterestedPath + " Paths");
		
		ArrayList<Integer> selectedPath = new ArrayList<Integer>();
		
		while(selectedPath.size() < numOfInterestedPath)
		{
			int newPathIndex = Engine.getDefault().getSimulationRandom().nextInt(this.pathList.size());
			
			if(!selectedPath.contains(newPathIndex))
			{
				selectedPath.add(newPathIndex);
				this.addRandomBadSurfaceConditionToPath(newPathIndex);
			}
		}
	}
	
	/**
	 * 
	 * @param numberOfPath
	 */
	public void addRandomBadSurfaceConditionToPath(int pathIndex)
	{
		
		System.out.println("Adding Bad Surface Condition to Path: " + pathIndex);
		
		CityPath cp = this.pathList.get(pathIndex);
		
		//Take the middle point of the path 
		int pointPosition = (cp.getPathPoints().size()-1)/2;
		
		//CityPoint
		CityPathPoint point = cp.getPathPoints().get(pointPosition);
		
		int surfaceConditionIndex = Engine.getDefault().getSimulationRandom().nextInt(3);
		String surfaceConditionType = "POTHOLE";
		
		switch (surfaceConditionIndex) {
		case 0:
			surfaceConditionType = "ICE";
			break;
		case 1:
			surfaceConditionType = "SNOW";
			break;
		case 2:
			surfaceConditionType = "WATER";
			break;
		case 3:
			surfaceConditionType = "OIL";
			break;
		case 4:
			surfaceConditionType = "POTHOLE";
			break;
		default:
			surfaceConditionType = "POTHOLE";
			break;
		}
		
		cp.setHasBadSurfaceCondition(true);
		cp.setBadSurfaceIndex(pointPosition);
		point.setSurfaceCondition(surfaceConditionType);
		
		System.out.println("Added "+surfaceConditionType+" to Path: " + pathIndex);
	}
	
	public String getPathFileName() {
		return pathFileName;
	}

	public void setPathFileName(String pathFileName) {
		this.pathFileName = pathFileName;
	}

	public String getSwitchStationFileName() {
		return switchStationFileName;
	}

	public void setSwitchStationFileName(String switchStationFileName) {
		this.switchStationFileName = switchStationFileName;
	}

	public ArrayList<CityPath> getPathList() {
		return pathList;
	}

	public void setPathList(ArrayList<CityPath> pathList) {
		this.pathList = pathList;
	}

	public ArrayList<SwitchStation> getSwitchStationList() {
		return switchStationList;
	}

	public void setSwitchStationList(ArrayList<SwitchStation> switchStationList) {
		this.switchStationList = switchStationList;
	}
	
}
