package it.unipr.ce.dsg.deus.example.d2v.networkmodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class NetworkStationController {

	private String mobile3gStationFileName = null;
	private String wifiStationFileName = null;
	private String mobile2gStationFileName = null;

	public NetworkStationController(String mobile2gStationFileName, String mobile3gStationFileName, String wifiStationFileName )
	{
		this.mobile2gStationFileName  = mobile2gStationFileName;
		this.mobile3gStationFileName = mobile3gStationFileName;
		this.wifiStationFileName = wifiStationFileName;
	}
	
	public ArrayList<WiFiStation> readWiFiStationFile()
	{
		System.out.println("Reading " + wifiStationFileName + " ...");
		
		ArrayList<WiFiStation> nsList = new ArrayList<WiFiStation>();
		
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File(wifiStationFileName))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{	
				String[] coordinates = line.split(",");
				
				double lat = Double.parseDouble(coordinates[0]);
				double lon = Double.parseDouble(coordinates[1]);
				double radius = Double.parseDouble(coordinates[2]);
				double maxUplink = Double.parseDouble(coordinates[3]);
				double maxDownlink = Double.parseDouble(coordinates[4]);
					
				WiFiStation ms = new WiFiStation(lat, lon,radius,maxUplink,maxDownlink);
				
				nsList.add(ms);
	
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Network Stations from "+ wifiStationFileName + " : " + nsList.size());
		
		return nsList;
	}
	
	public ArrayList<Mobile3GStation> read3GStationFile()
	{
		System.out.println("Reading " + mobile3gStationFileName + " ...");
		
		ArrayList<Mobile3GStation> nsList = new ArrayList<Mobile3GStation>();
		
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File(mobile3gStationFileName))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{	
				String[] coordinates = line.split(",");
				
				double lat = Double.parseDouble(coordinates[0]);
				double lon = Double.parseDouble(coordinates[1]);
				double radius = Double.parseDouble(coordinates[2]);
				double maxUplink = Double.parseDouble(coordinates[3]);
				double maxDownlink = Double.parseDouble(coordinates[4]);
					
				Mobile3GStation ms = new Mobile3GStation(lat, lon,radius,maxUplink,maxDownlink);
				
				nsList.add(ms);
	
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Network Stations from "+ mobile3gStationFileName + " : " + nsList.size());
		
		return nsList;
	}
	
	public ArrayList<Mobile2GStation> read2GStationFile()
	{
		System.out.println("Reading " + mobile2gStationFileName + " ...");
		
		ArrayList<Mobile2GStation> nsList = new ArrayList<Mobile2GStation>();
		
		try
		{
			BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(new File(mobile2gStationFileName))));
			
			String line = null;
			line = br.readLine();
			
			while(line!= null)
			{	
				String[] coordinates = line.split(",");
				
				double lat = Double.parseDouble(coordinates[0]);
				double lon = Double.parseDouble(coordinates[1]);
				double radius = Double.parseDouble(coordinates[2]);
				double maxUplink = Double.parseDouble(coordinates[3]);
				double maxDownlink = Double.parseDouble(coordinates[4]);
					
				Mobile2GStation ms = new Mobile2GStation(lat, lon,radius,maxUplink,maxDownlink);
				
				nsList.add(ms);
	
				line = br.readLine();
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Network Stations from "+ mobile2gStationFileName + " : " + nsList.size());
		
		return nsList;
	}

	public String getMobile3gStationFileName() {
		return mobile3gStationFileName;
	}

	public void setMobile3gStationFileName(String mobile3gStationFileName) {
		this.mobile3gStationFileName = mobile3gStationFileName;
	}

	public String getWifiStationFileName() {
		return wifiStationFileName;
	}

	public void setWifiStationFileName(String wifiStationFileName) {
		this.wifiStationFileName = wifiStationFileName;
	}
	
}
