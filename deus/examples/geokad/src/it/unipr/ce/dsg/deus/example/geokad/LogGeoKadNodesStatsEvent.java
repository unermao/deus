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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
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
public class LogGeoKadNodesStatsEvent extends Event {

	private AutomatorLogger a;
	private ArrayList<LoggerObject> fileValue;

	public LogGeoKadNodesStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		System.out.println(
				"#KademliaNodesStats info @ "
						+ Engine.getDefault().getVirtualTime()
						+ " Number of nodes: "
						+ Engine.getDefault().getNodes().size());
		
		getLogger().info(
				"#KademliaNodesStats info @ "
						+ Engine.getDefault().getVirtualTime()
						+ " Number of nodes: "
						+ Engine.getDefault().getNodes().size());

		Collections.sort(Engine.getDefault().getNodes());
		
		a = new AutomatorLogger("./temp/logger");
		fileValue = new ArrayList<LoggerObject>();
		fileValue.add(new LoggerObject("Peers",Engine.getDefault().getNodes().size()));	

		
		checkNodeDistance();
		
		logKBucketsDimAndSentMessages();
		
		//writeTotalKBucketsDimGraph();
		//writeKBucketsDimGraph();
		
		//verbose();
		//compressed();
		//network_dump();
		
		a.write(Engine.getDefault().getVirtualTime(), fileValue);

	}
	
	private void logKBucketsDimAndSentMessages()
	{
		System.out.println("Logging KBuckets Dim & Sent Messages ...");
		
		int numOfKBucket = ((GeoKadPeer)Engine.getDefault().getNodes().get(0)).getNumOfKBuckets();
		int totalNumOfSentMessages = 0;
		
		ArrayList<Integer> kBucketAppList = new ArrayList<Integer>(numOfKBucket);
		for(int j = 0; j<numOfKBucket; j++)
			kBucketAppList.add(0);
		
		for(int i=0; i<Engine.getDefault().getNodes().size();i++)
		{
			GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
			
			totalNumOfSentMessages += peer.getSentMessages();
			
			for(int j = 0; j<numOfKBucket; j++)
			{	
				kBucketAppList.set(j, kBucketAppList.get(j) + peer.getKbucket().get(j).size() ); 
			}
		}
		
		double sentMessagesPerPeer = (double)((double)totalNumOfSentMessages/(double)Engine.getDefault().getNodes().size());
		double sentMessagesPerVT = sentMessagesPerPeer/(double)Engine.getDefault().getVirtualTime();
		
		
		
		fileValue.add(new LoggerObject("Sent_Mess",totalNumOfSentMessages));
		fileValue.add(new LoggerObject("Sent_Mess_Peer",sentMessagesPerPeer));
		fileValue.add(new LoggerObject("Sent_Mess_VT",sentMessagesPerVT));
		
		//1VT=3.6sec
		double messPerSecond =sentMessagesPerVT * 3.6;
		
		System.out.println("Sent_Mess ----------->: "+totalNumOfSentMessages);
		System.out.println("Sent_Mess_Peer ----------->: "+sentMessagesPerPeer);
		System.out.println("Sent_Mess_VT ----------->: "+sentMessagesPerVT);
		System.out.println("Sent_Mess_Sec ----------->: "+messPerSecond);
		
		fileValue.add(new LoggerObject("Sent_Mess_Sec",messPerSecond));
		
		for(int index=0; index<numOfKBucket;index++)
			fileValue.add(new LoggerObject("K"+index+"_Dim", kBucketAppList.get(index)/Engine.getDefault().getNodes().size()));	
	}
	
	private void writeKBucketsDimGraph()
	{
		FileOutputStream file = null;
		
		try {
			file = new FileOutputStream("examples/geokad/map/k_buckets_graph.xml");
			
			PrintStream out = new PrintStream(file);

			out.println("<markers>");
			
			int numOfKBucket = ((GeoKadPeer)Engine.getDefault().getNodes().get(0)).getNumOfKBuckets();
			
			ArrayList<Integer> kBucketAppList = new ArrayList<Integer>(numOfKBucket);
			for(int j = 0; j<numOfKBucket; j++)
				kBucketAppList.add(0);
			
			for(int i=0; i<Engine.getDefault().getNodes().size();i++)
			{
				GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
				
				for(int j = 0; j<numOfKBucket; j++)
				{	
					kBucketAppList.set(j, kBucketAppList.get(j) + peer.getKbucket().get(j).size() ); 
				}
			}
			
			for(int index=0; index<numOfKBucket;index++)
			{
			  	out.println("<marker x=\""+index+"\" y=\""+kBucketAppList.get(index)/Engine.getDefault().getNodes().size()+"\" descriz=\""+Engine.getDefault().getNodes().size()+"\"/>");
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
	
	private void writeTotalKBucketsDimGraph()
	{

		FileOutputStream file = null;
		
		try {
			file = new FileOutputStream("examples/geokad/map/totalNeighbours.xml");
			
			PrintStream out = new PrintStream(file);

			out.println("<markers>");

			int knowedNodes = 0;
			
			for(int i=0; i<Engine.getDefault().getNodes().size();i++)
			{
				GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
				
				for(int j = 0; j<peer.getKbucket().size(); j++)
						knowedNodes += peer.getKbucket().get(j).size(); 
			}
			
			GeoKadPeer.graphKey.add((double)(knowedNodes/Engine.getDefault().getNodes().size()));

			for(int index=0; index<GeoKadPeer.graphKey.size();index++)
			{
			  	out.println("<marker x=\""+index+"\" y=\""+GeoKadPeer.graphKey.get(index)+"\" descriz=\""+Engine.getDefault().getNodes().size()+"\"/>");
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
	
	public void checkNodeDistance()
	{
		
		ArrayList<Double> perc = null;
		ArrayList<Integer> appArray = null;
		
		//Read each node available in the system
		for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
		{
			GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
			
			//Init percentages ArrayList
			if(perc == null)
			{
				perc = new ArrayList<Double>(peer.getNumOfKBuckets());
				
				for(int k=0; k<peer.getNumOfKBuckets(); k++)
					perc.add(0.0);
			}
			
			//Support Array for the number of peer of each kbucket
			appArray = new ArrayList<Integer>();
			for(int k=0; k<peer.getNumOfKBuckets(); k++)
					appArray.add(0);
			
			//Read all available nodes in the system
			for(int k=0; k < Engine.getDefault().getNodes().size(); k++)
			{
				
				if(!peer.equals((GeoKadPeer) Engine.getDefault().getNodes().get(k)))
				{
					double distance = GeoKadDistance.distance(peer, (GeoKadPeer) Engine.getDefault().getNodes().get(k));
					
					boolean bucketFounded = false;
					
					for(int j=0; j<(peer.getNumOfKBuckets()-1); j++)
					{
						if((distance <= (double)(j)*peer.getRayDistance()) && bucketFounded == false)
						{		
							bucketFounded = true;
							appArray.set(j, appArray.get(j)+1);
							break;
						}
					}
					
					if(bucketFounded == false)
						appArray.set(peer.getNumOfKBuckets()-1, appArray.get(peer.getNumOfKBuckets()-1)+1);
			
				}
					
			}
			
			for(int k=0; k<peer.getNumOfKBuckets(); k++)		
				if(appArray.get(k) != 0)
					 perc.set(k,(perc.get(k) + (double)((double)Math.abs(peer.getKbucket().get(k).size()-appArray.get(k))/(double)appArray.get(k))));
					//perc.set(k,(perc.get(k) + (double)Math.abs(peer.getKbucket().get(k).size()-appArray.get(k))));
		}
		
		for(int k=0; k<perc.size(); k++)
		{
			System.out.println(k+" "+100.0*(double)perc.get(k)/(double)Engine.getDefault().getNodes().size());
			fileValue.add(new LoggerObject("Perc_Miss_KB_"+k, 100.0*(double)perc.get(k)/(double)Engine.getDefault().getNodes().size()));	
			//fileValue.add(new LoggerObject("Miss_KB_"+k, (double)perc.get(k)/(double)Engine.getDefault().getNodes().size()));
		}
	}
	
	public void network_dump() {
		// Assuming all nodes have the same Properties
		GeoKadPeer peer =  (GeoKadPeer) Engine.getDefault().getNodes().get(0);
		getLogger().info("Properties = " + peer.getKBucketDim() + " " + peer.getResourcesNode() + " " + peer.getAlpha() + " " + peer.getDiscoveryMaxWait());
		String s;
		for (Node node: Engine.getDefault().getNodes()) {
			peer = (GeoKadPeer) node;
			s = new String();
			s += peer.getKey() + " = ";
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				for (GeoKadPeer entry : bucket) {
					s += entry.getKey() + " ";
				}
			}
			getLogger().info(s);
		}
	}

	public void verbose() {
		
		getLogger().info("VT: "+Engine.getDefault().getVirtualTime()+"############# VERBOSE ################################");
		
		for (Node node : Engine.getDefault().getNodes()) {
			GeoKadPeer peer = (GeoKadPeer) node;
			getLogger().info("\nn: " + peer.getKey());
			int size = 0;
			int i = 0;
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				size += bucket.size();
				String s = new String();
				for (GeoKadPeer entry : bucket) {
					s += entry.getKey() + " ";
				}
				getLogger().info(
						"\t[" + i + " size: " + bucket.size() + "]:" + s);
				i++;

			}
			getLogger().info(
					"Size of kbuckets for " + peer.getKey() + ": " + size);
			getLogger().info("Searches total: " + peer.logSearch.size());
			for (Integer k : peer.logSearch.keySet()) {
				getLogger().info(
						"key= " + k + " (" + peer.logSearch.get(k) + ")");
			}
		}
		
	}

	public void compressed() {
		Set<Integer> knownNodes = new TreeSet<Integer>();
		for (Node node : Engine.getDefault().getNodes()) {
			GeoKadPeer peer = (GeoKadPeer) node;
			String s = new String("n: " + peer.getKey() + "K"
					+ peer.getKBucketDim() + "A" + peer.getAlpha()
					+ " Searches#: " + peer.logSearch.size() + " ");
			String s2 = new String();
			String s3 = new String();
			for (ArrayList<GeoKadPeer> bucket : peer.getKbucket()) {
				s2 += " " + bucket.size();
				for (GeoKadPeer knownNode : bucket) {
					knownNodes.add(knownNode.getKey()); // Set = no duplicate
														// elements
				}
			}

			for (Integer v : peer.logSearch.values()) {
				s3 += " " + v;
			}
			getLogger().info(s + s2 + " S " + s3);
		}
		getLogger().info("Total known nodes " + knownNodes.size() + "\n");

		knownNodes = null;
	}
	
	

}
