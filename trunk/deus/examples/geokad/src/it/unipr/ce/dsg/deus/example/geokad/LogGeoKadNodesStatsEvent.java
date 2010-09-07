/**
 * 
 */
package it.unipr.ce.dsg.deus.example.geokad;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

import org.w3c.dom.NodeList;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;
import it.unipr.ce.dsg.deus.topology.DeusLink;
import it.unipr.ce.dsg.deus.topology.DeusNode;
import it.unipr.ce.dsg.deus.topology.GraphGUI;
import it.unipr.ce.dsg.deus.topology.TopologyMainGUI;

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
		
		/*
		getLogger().info(
				"#KademliaNodesStats info @ "
						+ Engine.getDefault().getVirtualTime()
						+ " Number of nodes: "
						+ Engine.getDefault().getNodes().size());
	*/

		Collections.sort(Engine.getDefault().getNodes());
		
		a = new AutomatorLogger("./temp/logger");
		fileValue = new ArrayList<LoggerObject>();
		fileValue.add(new LoggerObject("Peers",Engine.getDefault().getNodes().size()));	

		checkNodesStatistics();
		
		//checkNodeDistance();
		
		logKBucketsDimAndSentMessages();
		
		//writeTotalKBucketsDimGraph();
		//writeKBucketsDimGraph();
		
		//verbose();
		//compressed();
		//network_dump();
		
		a.write(Engine.getDefault().getVirtualTime(), fileValue);

		if(Engine.getDefault().getVirtualTime() == 100.0)
		{
			TopologyMainGUI gui = new TopologyMainGUI();
			//printNetworkTopology();
		}
		
		
	}
	
	
	private void printNetworkTopology()
	{
		System.out.println("Printing Network Topology ...");
		
		  try{
			    // Create file 
			    FileWriter fstream = new FileWriter("net_"+Engine.getDefault().getVirtualTime()+"_VT.nwb");
			    BufferedWriter out = new BufferedWriter(fstream);
			    
			    ArrayList<Node> nodeList = Engine.getDefault().getNodes();
			    
			    out.write("#NWB Data for Streaming P2P\n");
			    out.write("*Nodes "+ nodeList.size()+"\n");
			    out.write("id*int label*string color*string\n");
			    
			    for(int i=0; i<nodeList.size();i++)
				{
					Peer peer = (Peer)nodeList.get(i);	
					out.write(peer.getKey()+" \""+peer.getId()+"\" \"blue\"\n");
				}
		                
			    out.write("*DirectedEdges\n");
			    out.write("source*int	target*int color*string\n");
			    
		        for(int k=0; k<nodeList.size();k++)
				{
					Peer peer = (Peer)nodeList.get(k);
					
					for(int j=0; j<peer.getNeighbors().size(); j++)
					{
						if(peer.getKey() != peer.getNeighbors().get(j).getKey())
							out.write(peer.getKey()+" "+peer.getNeighbors().get(j).getKey()+" \"blue\"\n");
					}
				}
				
			    out.close();
			    
		  }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
	      }
		
	}
	
	private void checkNodesStatistics() {
		
		//System.out.println("Checking Nodes Statistics ...");
		
		double missingNode = 0.0;
		double gbErrorIndex = 0.0;
		double distanceError = 0;
		double peerWithMissing = 0.0;
		int distanceErrorCounter = 0;
		
		//int missGeoBucketIndex = 0;
		//int missNumber = 0;
		
		//double missNodesIndex[];
		double missNodesNumForGB[] = null;
		
		
		int numOfKBucket = ((GeoKadPeer)Engine.getDefault().getNodes().get(0)).getNumOfKBuckets();
		
		double totalMissNodesNumForGB[] = new double[numOfKBucket];
		
		for(int a=0; a < numOfKBucket; a++)
			totalMissNodesNumForGB[a] = 0.0;
		
		double sumOfAverageOfDiscoveryStep = 0.0;
		int nodeWithDiscoveryCounter = 0;
		
		for(int i=0; i<Engine.getDefault().getNodes().size(); i++)
		{
			
			GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
		
			peer.printEdgeList();
			
			//missNodesIndex = new double [peer.getNumOfKBuckets()];
			missNodesNumForGB = new double [peer.getNumOfKBuckets()];
			
			for(int a=0; a < peer.getNumOfKBuckets(); a++)
				missNodesNumForGB[a] = 0.0;
				
			
			int optimalNumber = 0;
			int currentNumber = 0;
			int geoBucketDistanceError = 0;
	
			
			if(peer.getKey()!=GeoKadBootStrapPeer.BOOTSTRAP_KEY)
			{
				if(peer.getDiscoveryCounter() != 0)
				{	
					nodeWithDiscoveryCounter++;
					sumOfAverageOfDiscoveryStep += (double)peer.getAvDiscoveryStepCounter()/(double)peer.getDiscoveryCounter();
				}
				
				//Read all available nodes in the system
				for(int k=0; k < Engine.getDefault().getNodes().size(); k++)
				{
					if(Engine.getDefault().getNodes().get(k).getKey() != GeoKadBootStrapPeer.BOOTSTRAP_KEY)
					{	
						GeoKadPeer testPeer = (GeoKadPeer)Engine.getDefault().getNodes().get(k);
						
						if(!peer.equals(testPeer))
						{
							double distance = GeoKadDistance.distance(peer,testPeer);
							
							boolean bucketFounded = false;
							
							for(int j=0; j<(peer.getNumOfKBuckets()); j++)
							{
								if((distance <= (double)(j)*peer.getRayDistance()) && bucketFounded == false)
								{		
									bucketFounded = true;
									
									//Increment the optimal number of peer
									optimalNumber++;
									
									//Check if is in the original peer list
									if(peer.containsPeerInGeoBuckets(testPeer.createPeerInfo()))
									{
										//Increment the number of current available peer
										currentNumber++;
									
										//Check if is in the right GeoBucket
										int geoBucketPosition = peer.indexOfGeoBucketFor(testPeer.createPeerInfo());
										
										if(geoBucketPosition != -1)
										{
											GeoKadPeerInfo localPeerInfo = peer.getKbucket().get(geoBucketPosition).get(peer.getKbucket().get(geoBucketPosition).indexOf(testPeer.createPeerInfo()));
											
											double localDistance = GeoKadDistance.distance(localPeerInfo, testPeer.createPeerInfo());
											
											if(!(!(localDistance >0) && !(localDistance<0) && localDistance != 0))
											{	
												distanceError += localDistance;
												distanceErrorCounter ++;
											}
											
											geoBucketDistanceError += Math.abs(j - geoBucketPosition);
										}
										
										//System.out.println("ERROR :" + Math.abs(j - geoBucketPosition));
									}
									else
										missNodesNumForGB[j]++;
									
									
									break;
								}
							}
						}							
					}
				}
				
				//System.out.println("Current: " +  currentNumber + " - Optimal Number: " + optimalNumber );
				
				if(currentNumber != 0)
					//Calculate the average for the gbDistanceError
					gbErrorIndex += (double)((double)geoBucketDistanceError)/(double)(currentNumber);
				
				if(optimalNumber != 0)
				{
					//Calculate the % of missing node
					missingNode += (double)(optimalNumber-currentNumber)/(double)optimalNumber;
					
					//System.out.println("Missing Node: " + (double)(optimalNumber-currentNumber));
					
					if( (double)(optimalNumber-currentNumber) > 0.0)
						peerWithMissing ++;
					
					for(int index=0; index < numOfKBucket; index++ )
					{
						//System.out.println( (double)(optimalNumber-currentNumber) + "-" + missNodesNumForGB[index]);
						if( (double)(optimalNumber-currentNumber) > 0.0)
						{
							//System.out.println(100.0*(double)missNodesNumForGB[index]/(double)(optimalNumber-currentNumber));
							totalMissNodesNumForGB[index] += 100.0*((double)(missNodesNumForGB[index]/(double)(optimalNumber-currentNumber))); 
						}
					}
				}
			}
		}
		
		if(Engine.getDefault().getNodes().size()-1 > 0)
		{
			fileValue.add(new LoggerObject("MISSING_NODE", 100.0*(double)((double)missingNode/(double)(Engine.getDefault().getNodes().size()-1))));	
			fileValue.add(new LoggerObject("GB_DISTANCE_ERROR", 100.0*(double)((double)gbErrorIndex/(double)(Engine.getDefault().getNodes().size()-1))));	
			System.out.println("########################## % MISSING NODE: " + 	100.0*(double)((double)missingNode/(double)(Engine.getDefault().getNodes().size()-1)));
			System.out.println("########################## % GB DISTANCE ERROR: " + (double)((double)gbErrorIndex/(double)(Engine.getDefault().getNodes().size()-1)));
			System.out.println("########################## AV Of Discovery Step Counter: " + (double)(sumOfAverageOfDiscoveryStep/(double)(nodeWithDiscoveryCounter)));
			
			if(distanceErrorCounter != 0)
			{
				System.out.println("########################## AV Local Distance Error (Km): " + (double)(distanceError/(double)distanceErrorCounter));
				fileValue.add(new LoggerObject("AV_Local_Distance_Error", (double)(distanceError/(double)distanceErrorCounter)));
			}
			else
				fileValue.add(new LoggerObject("AV_Local_Distance_Error", 0.0));
			
			double tot = 0.0;
			
			for(int index=0; index < numOfKBucket; index++ )
			{
				if(peerWithMissing > 0.0)
				{
					System.out.println("########################## Per_Miss_Node_For_GB_"+ index + " --->" + (double)totalMissNodesNumForGB[index]/peerWithMissing);
					fileValue.add(new LoggerObject("Per_Miss_Node_For_GB_"+index, (double)totalMissNodesNumForGB[index]/peerWithMissing));	
					tot +=(double)totalMissNodesNumForGB[index]/peerWithMissing;
				}
				else
				{
					System.out.println("########################## Per_Miss_Node_For_GB_"+ index + " --->" + 0.0);
					fileValue.add(new LoggerObject("Per_Miss_Node_For_GB_"+index, 0.0));
				}
			}
			System.out.println("TOT:" + tot);
		}
	}

	private void logKBucketsDimAndSentMessages()
	{
		//System.out.println("Logging KBuckets Dim & Sent Messages ...");
		
		int numOfKBucket = ((GeoKadPeer)Engine.getDefault().getNodes().get(0)).getNumOfKBuckets();
		int totalNumOfSentMessages = 0;
		
		ArrayList<Integer> kBucketAppList = new ArrayList<Integer>(numOfKBucket);
		for(int j = 0; j<numOfKBucket; j++)
			kBucketAppList.add(0);
		
		for(int i=0; i<Engine.getDefault().getNodes().size();i++)
		{
			GeoKadPeer peer = (GeoKadPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.getKey() != GeoKadBootStrapPeer.BOOTSTRAP_KEY)
			{

				totalNumOfSentMessages += peer.getSentMessages();
				
				for(int j = 0; j<numOfKBucket; j++)
				{	
					kBucketAppList.set(j, kBucketAppList.get(j) + peer.getKbucket().get(j).size() ); 
				}
			}
		}
		
		double sentMessagesPerPeer = 0.0;
		if(Engine.getDefault().getNodes().size() > 0)
			sentMessagesPerPeer = (double)((double)totalNumOfSentMessages/(double)(Engine.getDefault().getNodes().size()-1));

		double sentMessagesPerVT = (double)sentMessagesPerPeer/(double)Engine.getDefault().getVirtualTime();
		
		
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
			fileValue.add(new LoggerObject("K"+index+"_Dim", kBucketAppList.get(index)/(Engine.getDefault().getNodes().size()-1)));	
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
			peer.updateGeoBucketInfo();
			
			if(peer.getKey()!=GeoKadBootStrapPeer.BOOTSTRAP_KEY)
			{
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
					if(Engine.getDefault().getNodes().get(k).getKey() != GeoKadBootStrapPeer.BOOTSTRAP_KEY)
					{	
						GeoKadPeer testPeer = (GeoKadPeer)Engine.getDefault().getNodes().get(k);
						
						if(!peer.equals(testPeer))
						{
							double distance = GeoKadDistance.distance(peer,testPeer);
							
							boolean bucketFounded = false;
							
							for(int j=0; j<(peer.getNumOfKBuckets()); j++)
							{
								if((distance <= (double)(j)*peer.getRayDistance()) && bucketFounded == false)
								{		
									bucketFounded = true;
									appArray.set(j, appArray.get(j)+1);
									break;
								}
							}
							
							/*
							if(bucketFounded == false)
								appArray.set(peer.getNumOfKBuckets()-1, appArray.get(peer.getNumOfKBuckets()-1)+1);
							*/
						}
							
					}
				}
				
				for(int k=0; k<peer.getNumOfKBuckets(); k++)		
				{	
					if(appArray.get(k) != 0)
					{
						if(peer.getKbucket().get(k).size() > appArray.get(k))
							System.out.println("Peer Key: " + peer.getKey() + " GB Index: " + k + " Available Number: " + peer.getKbucket().get(k).size() + " Optimal Number: " + appArray.get(k));
					
						perc.set(k,(perc.get(k) + (double)((double)Math.abs(peer.getKbucket().get(k).size()-appArray.get(k))/(double)appArray.get(k))));
							//perc.set(k,(perc.get(k) + (double)Math.abs(peer.getKbucket().get(k).size()-appArray.get(k))));
					}
				}
			}
		}
		
		for(int k=0; k<perc.size()-1; k++)
		{
			System.out.println(k+" "+100.0*(double)perc.get(k)/((double)Engine.getDefault().getNodes().size()-1));
			fileValue.add(new LoggerObject("Perc_Miss_KB_"+k, 100.0*(double)perc.get(k)/(double)((double)Engine.getDefault().getNodes().size()-1.0)));	
			//fileValue.add(new LoggerObject("Miss_KB_"+k, (double)perc.get(k)/(double)Engine.getDefault().getNodes().size()));
		}
	}
	
	public void network_dump() {
		// Assuming all nodes have the same Properties
		GeoKadPeer peer =  (GeoKadPeer) Engine.getDefault().getNodes().get(0);
		//getLogger().info("Properties = " + peer.getKBucketDim() + " " + peer.getResourcesNode() + " " + peer.getAlpha() + " " + peer.getDiscoveryMaxWait());
		String s;
		for (Node node: Engine.getDefault().getNodes()) {
			peer = (GeoKadPeer) node;
			s = new String();
			s += peer.getKey() + " = ";
			for (ArrayList<GeoKadPeerInfo> bucket : peer.getKbucket()) {
				for (GeoKadPeerInfo entry : bucket) {
					s += entry.getKey() + " ";
				}
			}
			//getLogger().info(s);
		}
	}

	public void verbose() {
		
		//getLogger().info("VT: "+Engine.getDefault().getVirtualTime()+"############# VERBOSE ################################");
		
		for (Node node : Engine.getDefault().getNodes()) {
			GeoKadPeer peer = (GeoKadPeer) node;
			//getLogger().info("\nn: " + peer.getKey());
			int size = 0;
			int i = 0;
			for (ArrayList<GeoKadPeerInfo> bucket : peer.getKbucket()) {
				size += bucket.size();
				String s = new String();
				for (GeoKadPeerInfo entry : bucket) {
					s += entry.getKey() + " ";
				}
				//getLogger().info(
					//	"\t[" + i + " size: " + bucket.size() + "]:" + s);
				i++;

			}
			//getLogger().info(
				//	"Size of kbuckets for " + peer.getKey() + ": " + size);
			//getLogger().info("Searches total: " + peer.logSearch.size());
			/*
			for (Integer k : peer.logSearch.keySet()) {
				getLogger().info(
						"key= " + k + " (" + peer.logSearch.get(k) + ")");
			}
			*/
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
			for (ArrayList<GeoKadPeerInfo> bucket : peer.getKbucket()) {
				s2 += " " + bucket.size();
				for (GeoKadPeerInfo knownNode : bucket) {
					knownNodes.add(knownNode.getKey()); // Set = no duplicate
														// elements
				}
			}

			for (Integer v : peer.logSearch.values()) {
				s3 += " " + v;
			}
			//getLogger().info(s + s2 + " S " + s3);
		}
		//getLogger().info("Total known nodes " + knownNodes.size() + "\n");

		knownNodes = null;
	}
	
	

}
