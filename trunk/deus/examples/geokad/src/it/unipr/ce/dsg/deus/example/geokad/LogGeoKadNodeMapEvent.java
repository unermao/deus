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
public class LogGeoKadNodeMapEvent extends Event {

	static private ArrayList<PlaceMark> placeMarkList = new ArrayList<PlaceMark>();
	static private Date startDate = new Date();
	
	public LogGeoKadNodeMapEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {

		System.out.println("VT:" + triggeringTime + " Node: " + Engine.getDefault().getNodes().size() +" ... Logging MARKERS");

		//writeAllPeersFile();

		FileOutputStream file = null;
		//FileOutputStream file_AllPeer = null;
		PrintStream out = null;
		//PrintStream out_AllPeer = null;

		try {

			//file_AllPeer = new FileOutputStream("/home/Gudhrun/picone/public_html/marker_ALL.xml");
			//out_AllPeer = new PrintStream(file_AllPeer);

			//Write BootFile
			GeoKadBootStrapPeer bootStrapPeer = (GeoKadBootStrapPeer)Engine.getDefault().getNodeByKey(GeoKadBootStrapPeer.BOOTSTRAP_KEY);
			
			file = new FileOutputStream("examples/geokad/map/marker_ALL.xml");
			//file = new FileOutputStream("/home/Gudhrun/picone/public_html/node_markers/markers_"+peer.getKey()+".xml");
			out = new PrintStream(file);

			out.println("<markers>");
			
			for(int i=0; i<bootStrapPeer.getPeerList().size(); i++)
			{
				
				GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
				
				out.println("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
			}
			
			out.println("</markers>");

			out.close();
			file.close();
			
			//Evaluation PlaceMark Time

			int hrs = startDate.getHours();
			int min = 0;
			int sec = 0;
			
			
			if(startDate.getSeconds() + 1 > 60)
			{
				min = startDate.getMinutes() +1;
				sec = startDate.getSeconds() + 1 - 60;
			}
			else
			{
				min = startDate.getMinutes();
				sec = startDate.getSeconds() + 1;
			}
			
				
			
			Date plmDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), hrs, min, sec);
			System.out.println("################################################## Date PlaceMark: " + plmDate.toString());
			startDate = plmDate;
			
			for(int index=0; index<Engine.getDefault().getNodes().size();index++)
			{
				
				GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(index);
				
				
				PlaceMark placeMark = new PlaceMark(plmDate, new GeographicPoint(peer.getLatitude(), peer.getLongitude()), ""+peer.getKey());
				placeMarkList.add(placeMark);
				
				if(peer != null)
				{
					peer.updateGeoBucketInfo();
					
					file = new FileOutputStream("examples/geokad/map/node_markers/markers_"+peer.getKey()+".xml");
					//file = new FileOutputStream("/home/Gudhrun/picone/public_html/node_markers/markers_"+peer.getKey()+".xml");
					out = new PrintStream(file);

					out.println("<markers>");

					//Write All Peers file
					//out_AllPeer.println("<markers>");
					//out_AllPeer.println("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");


					out.println("<marker lat=\""+peer.getLatitude()+"\" long=\""+peer.getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");

					out.println("<marker lat=\""+peer.getStartPoint().getLat()+"\" long=\""+peer.getStartPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getEndPoint().getLat()+"\" long=\""+peer.getEndPoint().getLon()+"\" descriz=\""+peer.getKey()+"\"/>");

					for(int i=0; i<peer.getKbucket().size(); i++)
					{
						for(int k=0; k<peer.getKbucket().get(i).size();k++)
						{
							out.println("<marker lat=\""+peer.getKbucket().get(i).get(k).getLatitude()+"\" long=\""+peer.getKbucket().get(i).get(k).getLongitude()
									+"\" descriz=\""+peer.getKbucket().get(i).get(k).getKey()
									+"\" real_lat=\""+((GeoKadPeer)Engine.getDefault().getNodeByKey(peer.getKbucket().get(i).get(k).getKey())).getLatitude()
									+"\" real_lon=\""+((GeoKadPeer)Engine.getDefault().getNodeByKey(peer.getKbucket().get(i).get(k).getKey())).getLongitude()
									+"\" distance=\""+GeoKadDistance.distance(peer.getKbucket().get(i).get(k), ((GeoKadPeer)Engine.getDefault().getNodeByKey(peer.getKbucket().get(i).get(k).getKey())).createPeerInfo())
									+"\"/>");
						}
					}

					out.println("</markers>");

					out.close();
					file.close();


				}
			}

		//	out_AllPeer.println("</markers>");
			//out_AllPeer.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("PlaceMark LIST: " + placeMarkList.size());
		
		if(Engine.getDefault().getVirtualTime() == Engine.getDefault().getMaxVirtualTime())
		{
			System.out.println("Writing KML File .....");
			
			 try {
					
				 KmlManager kmlManager = new KmlManager();
				 //kmlManager.setLookAt(new LookAt(new GeographicPoint(68.5978610564592,5.286515202934736), "8698607.350624971", "0", "-0.3840786059394472"));
				 kmlManager.setStyle(new Style("geoKadStyle","http://www.ce.unipr.it/~picone/marker26.png"));
				 kmlManager.setFolder(new Folder("folder", "1", "description", null, null, placeMarkList));
				 
				 kmlManager.writeKMLFile("prova.kml");
				 
			 } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void writeAllPeersFile()
	{


		FileOutputStream file = null;

		try {
			file = new FileOutputStream("examples/geokad/map/marker_ALL.xml");
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
