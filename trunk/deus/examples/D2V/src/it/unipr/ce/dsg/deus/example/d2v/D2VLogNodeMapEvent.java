/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.example.googleearth.kml.Folder;
import it.unipr.ce.dsg.example.googleearth.kml.GeographicPoint;
import it.unipr.ce.dsg.example.googleearth.kml.KmlManager;
import it.unipr.ce.dsg.example.googleearth.kml.LookAt;
import it.unipr.ce.dsg.example.googleearth.kml.PlaceMark;
import it.unipr.ce.dsg.example.googleearth.kml.Style;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class D2VLogNodeMapEvent extends Event {
	
	public D2VLogNodeMapEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {

		System.out.println("VT:" + triggeringTime + " Node: " + Engine.getDefault().getNodes().size() +" ... Logging MARKERS");

		FileOutputStream file = null;
		PrintStream out = null;

		ArrayList<D2VTrafficElement> trafficElements = new ArrayList<D2VTrafficElement>();
		
		try {

			file = new FileOutputStream("examples/D2V/map/marker_ALL.xml");
			out = new PrintStream(file);

			out.println("<markers>");
			
			for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
			{
				if(Engine.getDefault().getNodes().get(i).getId().equals("TrafficElement"))
				{
					trafficElements.add((D2VTrafficElement)Engine.getDefault().getNodes().get(i));
				}
				else {
					D2VPeer peer = (D2VPeer)Engine.getDefault().getNodes().get(i);
					
					out.println("<marker lat=\""+peer.getPeerDescriptor().getGeoLocation().getLatitude()
							+"\" long=\""+peer.getPeerDescriptor().getGeoLocation().getLongitude()
							+"\" descriz=\""+peer.getKey()
							+"\" startlat=\""+peer.getCp().getStartPoint().getLatitude()
							+"\" startlon=\""+peer.getCp().getStartPoint().getLongitude()
							+"\" endlat=\""+peer.getCp().getEndPoint().getLatitude()
							+"\" endlon=\""+peer.getCp().getEndPoint().getLongitude()
							+"\"/>");
				}
				
				
			}
			
			out.println("</markers>");

			out.close();
			file.close();
			
			printTrafficElements(trafficElements);
			
			Thread.sleep(500);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printTrafficElements(ArrayList<D2VTrafficElement> trafficElements)
	{
		System.out.println("VT:" + triggeringTime + " Node: " + Engine.getDefault().getNodes().size() +" ... Logging Traffic Elements:"+trafficElements.size());

		FileOutputStream file = null;
		PrintStream out = null;
		
		try {

			file = new FileOutputStream("examples/D2V/map/trafficElements.xml");
			out = new PrintStream(file);

			out.println("<markers>");
			
			for(int i=0; i<trafficElements.size(); i++)
			{
				
				D2VTrafficElement peer = (D2VTrafficElement)trafficElements.get(i);
				
				
				out.println("<marker lat=\""+peer.getLocation().getLatitude()
							+"\" long=\""+peer.getLocation().getLongitude()
							+"\" id=\""+peer.getKey()
							+"\" type=\""+peer.getType()
							+"\"/>");
				
			}
			
			out.println("</markers>");

			out.close();
			file.close();
			
			Thread.sleep(500);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
