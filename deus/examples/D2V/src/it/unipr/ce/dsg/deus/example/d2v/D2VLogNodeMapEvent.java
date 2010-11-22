/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

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

		//System.out.println("VT:" + triggeringTime + " LOG_MAP_EVENT ---> Node: " + Engine.getDefault().getNodes().size() +" ... Logging MARKERS");

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
							+"\" trafficJam=\""+peer.isTrafficJam()
							//+"\" backward=\""+peer.getCi().isBackward()
							+"\" backward=\""+"false"
							+"\" isPathChanged=\""+"false"
							+"\"/>");
				}
				
				
			}
			
			out.println("</markers>");

			out.close();
			file.close();
			
			printAllNodeInfo();
			
			printTrafficElements(trafficElements);
			
			//Thread.sleep(100);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printTrafficElements(ArrayList<D2VTrafficElement> trafficElements)
	{
		//System.out.println("VT:" + triggeringTime + " LOG_MAP_EVENT --->  Traffic Elements:"+trafficElements.size());

		FileOutputStream file = null;
		PrintStream out = null;
		
		try {

			file = new FileOutputStream("examples/D2V/map/trafficElements.xml");
			out = new PrintStream(file);

			out.println("<markers>");
			
			for(int i=0; i<trafficElements.size(); i++)
			{
				D2VTrafficElement peer = (D2VTrafficElement)trafficElements.get(i);
				
				//System.out.println("Traffic Element: " + peer.getKey() + " Cars in Jam: " + peer.getNodeKeysInTrafficJam().size());
				
				out.println("<marker lat=\""+peer.getLocation().getLatitude()
							+"\" long=\""+peer.getLocation().getLongitude()
							+"\" id=\""+peer.getKey()
							+"\" type=\""+peer.getType()
							+"\"/>");
				
			}
			
			out.println("</markers>");

			out.close();
			file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void printAllNodeInfo() throws IOException
	{
		FileOutputStream file = null;
		PrintStream out = null;

		
		for(int index=0; index<Engine.getDefault().getNodes().size();index++)
		{
			if(Engine.getDefault().getNodes().get(index).getId().equals("D2VPeer"))
			{
				D2VPeer peer = (D2VPeer)Engine.getDefault().getNodes().get(index);
				
				
				if(peer != null)
				{
					
					file = new FileOutputStream("examples/D2V/map/node_markers/markers_"+peer.getPeerDescriptor().getKey()+".xml");
					out = new PrintStream(file);

					out.println("<markers>");

					out.println("<marker lat=\""+peer.getPeerDescriptor().getGeoLocation().getLatitude()+"\" long=\""+peer.getPeerDescriptor().getGeoLocation().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getCp().getStartPoint().getLatitude()+"\" long=\""+peer.getCp().getStartPoint().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getCp().getEndPoint().getLatitude()+"\" long=\""+peer.getCp().getEndPoint().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");

					for(int i=0; i<peer.getGb().getBucket().size(); i++)
					{
						for(int k=0; k<peer.getGb().getBucket().get(i).size();k++)
						{
							D2VPeerDescriptor peerDescr = peer.getGb().getBucket().get(i).get(k);
							D2VPeer realPeer = (D2VPeer) Engine.getDefault().getNodeByKey(peerDescr.getKey());
							
							out.println("<marker lat=\""+peerDescr.getGeoLocation().getLatitude()+"\" long=\""+peerDescr.getGeoLocation().getLongitude()
									+"\" descriz=\""+peerDescr.getKey()
									+"\" real_lat=\""+realPeer.getPeerDescriptor().getGeoLocation().getLatitude()
									+"\" real_lon=\""+realPeer.getPeerDescriptor().getGeoLocation().getLongitude()
									+"\" distance=\""+GeoDistance.distance(peerDescr, realPeer.getPeerDescriptor())
									+"\"/>");
						}
					}

					out.println("</markers>");

					out.close();
					file.close();


				}
			}
		}

	}
	
}
