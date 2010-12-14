/**
 * 
 */
package it.unipr.ce.dsg.example.mobilityexample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Marco Picone (picone@ce.unipr.it)
 *
 */
public class LogNodeMapEvent extends Event {
	
	public LogNodeMapEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
		//Clear old marker file in node_markers directory
		File directory = new File("examples/MobilityExample/map/node_markers/");

		// Get all files in directory

		File[] files = directory.listFiles();
		for (File file : files)
		{
		   // Delete each file

		   if (!file.delete())
		   {
		       // Failed to delete file

		       System.out.println("Failed to delete "+file);
		   }
		}

	}

	public void run() throws RunException {
		
		if(triggeringTime % 100 == 0)
			System.out.println(triggeringTime+": LOGGING MAP ...");
		
		FileOutputStream file = null;
		PrintStream out = null;

		try {

			file = new FileOutputStream("examples/MobilityExample/map/marker_ALL.xml");
			out = new PrintStream(file);

			out.println("<markers>");
			
			for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
			{
				CarPeer peer = (CarPeer)Engine.getDefault().getNodes().get(i);
					
					out.println("<marker lat=\""+peer.getGeoLocation().getLatitude()
							+"\" long=\""+peer.getGeoLocation().getLongitude()
							+"\" descriz=\""+peer.getKey()
							+"\" startlat=\""+peer.getMobilityPath().getStartPoint().getLatitude()
							+"\" startlon=\""+peer.getMobilityPath().getStartPoint().getLongitude()
							+"\" endlat=\""+peer.getMobilityPath().getEndPoint().getLatitude()
							+"\" endlon=\""+peer.getMobilityPath().getEndPoint().getLongitude()
							+"\" trafficJam=\"false"
							//+"\" backward=\""+peer.getCi().isBackward()
							+"\" backward=\""+"false"
							+"\" isPathChanged=\""+"false"
							+"\" isConnected=\""+peer.isConnected()
							+"\"/>");	
			}
			
			out.println("</markers>");

			out.close();
			file.close();
			
			printAllNodeInfo();
			
			Thread.sleep(1000);
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
			if(Engine.getDefault().getNodes().get(index).getId().equals("CarPeer"))
			{
				CarPeer peer = (CarPeer)Engine.getDefault().getNodes().get(index);
				
				if(peer != null)
				{
					
					file = new FileOutputStream("examples/MobilityExample/map/node_markers/markers_"+peer.getKey()+".xml");
					out = new PrintStream(file);

					out.println("<markers>");

					out.println("<marker lat=\""+peer.getGeoLocation().getLatitude()+"\" long=\""+peer.getGeoLocation().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getMobilityPath().getStartPoint().getLatitude()+"\" long=\""+peer.getMobilityPath().getStartPoint().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");
					out.println("<marker lat=\""+peer.getMobilityPath().getEndPoint().getLatitude()+"\" long=\""+peer.getMobilityPath().getEndPoint().getLongitude()+"\" descriz=\""+peer.getKey()+"\"/>");

					out.println("</markers>");

					out.close();
					file.close();


				}
			}
		}

	}
	
}
