package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
					
				SwitchStation ss = new SwitchStation(lat, lon, 0);
				
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
					CityPathPoint point = new CityPathPoint(lat, lon, 0);
					
					//If the point already exist in the locationList
					if(!this.locationList.contains(point))
						this.locationList.add(point);
					
					int index = this.locationList.indexOf(point);
					path.addCityPathPoint(this.locationList.get(index));
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
