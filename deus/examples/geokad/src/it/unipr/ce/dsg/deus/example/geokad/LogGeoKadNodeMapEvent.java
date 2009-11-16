/**
 * 
 */
package it.unipr.ce.dsg.deus.example.geokad;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event writes a log file with the kbuckets' data for each node in the network.
 * This event should be scheduled in the simulation's XML file
 * 
 * @author Vittorio Sozzi
 * 
 */
public class LogGeoKadNodeMapEvent extends Event {

	public LogGeoKadNodeMapEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		System.out.println("VT:" + triggeringTime + " Node: " + Engine.getDefault().getNodes().size() + " ... Logging MARKERS");
		
		writeAllPeersFile();
		
		for(int index=0; index<Engine.getDefault().getNodes().size();index++)
		{
			GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(index);
			
			if(peer != null)
			{
				FileOutputStream file = null;
				
				try {
					file = new FileOutputStream("/home/Gudhrun/picone/public_html/node_markers/markers_"+peer.getKey()+".xml");
					PrintStream out = new PrintStream(file);
					
					out.println("<markers>");
					
					
					out.println("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
					
					out.println("<marker lat=\""+peer.getStartPoint().getLat()+"\" long=\""+peer.getStartPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getEndPoint().getLat()+"\" long=\""+peer.getEndPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");
					
					for(int i=0; i<peer.getKbucket().size(); i++)
					{
						for(int k=0; k<peer.getKbucket().get(i).size();k++)
						{
							out.println("<marker lat=\""+peer.getKbucket().get(i).get(k).getLatitude()+"\" long=\""+peer.getKbucket().get(i).get(k).getLongitude()+"\" descriz=\""+peer.getKbucket().get(i).get(k).getKey()+"\"/>");
						}
					}
					
					out.println("</markers>");

					out.close();
					file.close();
					
					//Thread.sleep(1000);
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		}
	

			/*
			getLogger().info("<markers>");
			
			
			getLogger().info("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
			
			getLogger().info("<marker lat=\""+peer.getStartPoint().getLat()+"\" long=\""+peer.getStartPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");
			getLogger().info("<marker lat=\""+peer.getEndPoint().getLat()+"\" long=\""+peer.getEndPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");
			
			for(int i=0; i<peer.getKbucket().size(); i++)
			{
				for(int k=0; k<peer.getKbucket().get(i).size();k++)
				{
					getLogger().info("<marker lat=\""+peer.getKbucket().get(i).get(k).getLatitude()+"\" long=\""+peer.getKbucket().get(i).get(k).getLongitude()+"\" descriz=\""+peer.getKbucket().get(i).get(k).getKey()+"\"/>");
				}
			}
			
			getLogger().info("</markers>");
	*/

		}
	}
	
	private void writeAllPeersFile()
	{
			

			FileOutputStream file = null;
				
				try {
					file = new FileOutputStream("/home/Gudhrun/picone/public_html/marker_ALL.xml");
					PrintStream out = new PrintStream(file);
					
					out.println("<markers>");
					
					for(int i=0; i<Engine.getDefault().getNodes().size();i++)
					{
						GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
					
					
					out.println("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
										
					
					}
					out.println("</markers>");
					out.close();
					file.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

		}
	
}
