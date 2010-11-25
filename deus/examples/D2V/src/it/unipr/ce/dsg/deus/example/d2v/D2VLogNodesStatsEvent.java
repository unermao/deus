/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v;

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
import it.unipr.ce.dsg.deus.example.d2v.message.TrafficInformationMessage;
import it.unipr.ce.dsg.deus.example.d2v.message.TrafficJamMessage;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPath;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.CityPathPoint;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * This event writes a log file with the kbuckets' data for each node in the network.
 * This event should be scheduled in the simulation's XML file
 * 
 * @author Vittorio Sozzi
 * 
 */
public class D2VLogNodesStatsEvent extends Event {

	private AutomatorLogger a;
	private ArrayList<LoggerObject> fileValue;

	public D2VLogNodesStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		if(Engine.getDefault().getNodeKeysById("D2VPeer") != null)
			System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT NODES: " + Engine.getDefault().getNodeKeysById("D2VPeer").size());
		else
			System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT NODES: 0");
			
		//Collections.sort(Engine.getDefault().getNodes());
		
		//a = new AutomatorLogger("./temp/logger");
		//fileValue = new ArrayList<LoggerObject>();
		//fileValue.add(new LoggerObject("Peers",Engine.getDefault().getNodes().size()));	

		//checkNodesStatistics();
		
		//checkNodeDistance();
		
		//logKBucketsDimAndSentMessages();
		
		//writeTotalKBucketsDimGraph();
		//writeKBucketsDimGraph();
		
		//verbose();
		//compressed();
		//network_dump();
		
		//a.write(Engine.getDefault().getVirtualTime(), fileValue);

		/*
		if(Engine.getDefault().getVirtualTime() == 100.0)
		{
			TopologyMainGUI gui = new TopologyMainGUI();
			//printNetworkTopology();
		}
		*/
		
		this.evaluateNodeStatistics();
	}
	
	/**
	 * 
	 */
	public void evaluateNodeStatistics()
	{
		
		//DebugLog log = new DebugLog();
		//log.printStart(0, this.getClass().getCanonicalName(), triggeringTime);
		
		System.out.println("################################################################################################");
		System.out.println("VT:" + triggeringTime + " EVALUATING NODE STATISTICS");
		
		ArrayList<Integer> d2vPeerIndexList = Engine.getDefault().getNodeKeysById("D2VPeer");
		
		double totalPercentageMissing = 0.0;
		int nodeWithDiscoveryCounter = 0;
		double sumOfAverageOfDiscoveryStep = 0.0;
		double discoveryPeriodSum = 0.0;
		double sentMessagesSum = 0.0;
		double sentKbSumDGT = 0.0;
		double sentKbSumDissemination = 0.0;
		double duplicateMessageSum = 0.0;
		int missingPerGBCounter = 0;
		int peerNumber = 0;
		int numOfTrafficElements = 0;
		int d2vActivePeerCount = 0;
		
		a = new AutomatorLogger("./temp/logger");
		fileValue = new ArrayList<LoggerObject>();
			
		if(d2vPeerIndexList != null)
		{
			
			ArrayList<Double> missingNodesPerGB = new ArrayList<Double>();
			
			peerNumber = d2vPeerIndexList.size();
			
			for(int index=0; index<d2vPeerIndexList.size();index++)
			{
				D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(d2vPeerIndexList.get(index));
				
				if(peer.isConnected() == true)
				{
					d2vActivePeerCount ++;
					
					//Initialize array only the first time
					if(missingNodesPerGB.size() == 0)	
						for(int k=0;k<peer.getK();k++)
							missingNodesPerGB.add(0.0);
					
					//Evaluate global missing node and missing per GB 
					ArrayList<Double> results = peer.getGb().evaluateCompletePerMissingNodes(peer.createPeerInfo());
					
					//sum percentage of globla missing node
					totalPercentageMissing += results.get(results.size()-1);
					
					//Store and sum percentage of missing node per GB
					if(results.get(results.size()-1) > 0.0)
					{
						missingPerGBCounter ++;
						for(int k=0;k<peer.getK();k++)
							missingNodesPerGB.set(k,missingNodesPerGB.get(k)+results.get(k));
					}
						
						
					//sum discovery period of the peer
					discoveryPeriodSum += peer.getDiscoveryPeriod();
					
					//sum sent messages
					sentMessagesSum += peer.getSentMessages();
					
					//sum sent Kb for DGT maintainance
					sentKbSumDGT += peer.getTotalKbSentForDGT();
					
					//sum sent Kb for dissemination
					sentKbSumDissemination += peer.getTotalKbSentForDissemination();
					
					//sum of received duplicated messages
					duplicateMessageSum += peer.getDuplicateReceivedMessageCount();
					
					//sum discovery's step 
					if(peer.getDiscoveryCounter() != 0)
					{	
						nodeWithDiscoveryCounter++;
						sumOfAverageOfDiscoveryStep += (double)peer.getAvDiscoveryStepCounter()/(double)peer.getDiscoveryCounter();
					}
				}
			}
			
			
			System.out.println("VT:" + triggeringTime + "#Nodes: " + peerNumber + "  Active Nodes: " +  d2vActivePeerCount);
			fileValue.add(new LoggerObject("Peers",peerNumber));
			
			System.out.println("VT:" + triggeringTime + "  % TOTAL Missing Nodes: " +  totalPercentageMissing/(double)d2vActivePeerCount);
			fileValue.add(new LoggerObject("TotalMissingPercentage",totalPercentageMissing/(double)d2vActivePeerCount));
			
			if(missingPerGBCounter > 0)
			{	
				for(int k=0;k<missingNodesPerGB.size();k++)
				{
					System.out.println("VT:" + triggeringTime + "  % Missing Nodes GB("+ k +"/"+missingNodesPerGB.size()+"):" +  (double)missingNodesPerGB.get(k)/(double)missingPerGBCounter);
					fileValue.add(new LoggerObject("Miss_GB_"+k,(double)missingNodesPerGB.get(k)/(double)missingPerGBCounter));
				}
			}
			else
			{
				for(int k=0;k<missingNodesPerGB.size();k++)
				{
					System.out.println("VT:" + triggeringTime + "  % Missing Nodes GB("+ k +"/"+missingNodesPerGB.size()+"):" +  0.0);
					fileValue.add(new LoggerObject("Miss_GB_"+k,0.0));
				}
			}
			
			
			double avDiscoveryStep = 0.0;
			
			if(nodeWithDiscoveryCounter > 0)
				avDiscoveryStep = sumOfAverageOfDiscoveryStep/(double)nodeWithDiscoveryCounter;
			
			System.out.println("VT:" + triggeringTime + "  Average Of Discovery Step: " +  avDiscoveryStep);
			fileValue.add(new LoggerObject("Av_DiscStep",avDiscoveryStep));
			
			System.out.println("VT:" + triggeringTime + "  Average Discovery Period: " +  (double)discoveryPeriodSum/(double)d2vActivePeerCount);
			fileValue.add(new LoggerObject("Av_DiscPeriod",(double)discoveryPeriodSum/(double)d2vActivePeerCount));
			
			double avSentMessageInVT = (sentMessagesSum/(double)d2vActivePeerCount)/((double)triggeringTime);
			System.out.println("VT:" + triggeringTime + "  Average Sent Messages (min): " +  avSentMessageInVT/16.6666666666);
			fileValue.add(new LoggerObject("Av_SentMess",avSentMessageInVT/16.6666666666));
			
			double avSentKbForDGTInVT = (sentKbSumDGT/(double)d2vActivePeerCount)/((double)triggeringTime);
			System.out.println("VT:" + triggeringTime + "  Average Sent DGT Messages Kb/min: " +  avSentKbForDGTInVT/16.6666666666);
			fileValue.add(new LoggerObject("Av_Sent_Kb_DGT_min",avSentKbForDGTInVT/16.6666666666));
			
			double avSentKbForDisseminationInVT = (sentKbSumDissemination/(double)d2vActivePeerCount)/((double)triggeringTime);
			System.out.println("VT:" + triggeringTime + "  Average Sent Dissemination Messages Kb/min: " +  avSentKbForDisseminationInVT/16.6666666666);
			fileValue.add(new LoggerObject("Av_Sent_Kb_Dissemination_min",avSentKbForDisseminationInVT/16.6666666666));
			
			
			double avDuplicateMessageInVT = (duplicateMessageSum/(double)d2vActivePeerCount)/((double)triggeringTime);
			System.out.println("VT:" + triggeringTime + "  Duplicate Received Messages (min): " +  avDuplicateMessageInVT/16.6666666666);
			fileValue.add(new LoggerObject("Av_DuplicateMess",avDuplicateMessageInVT/16.6666666666));
			
			ArrayList<Integer> trafficElementIndexList = Engine.getDefault().getNodeKeysById("TrafficElement");
			
			double carInTrafficJam = 0.0;
			
			if(trafficElementIndexList != null)
			{
				numOfTrafficElements = trafficElementIndexList.size();
				
				for(int index=0; index<trafficElementIndexList.size(); index++)
				{
					D2VTrafficElement te = (D2VTrafficElement)Engine.getDefault().getNodeByKey(trafficElementIndexList.get(index));
					carInTrafficJam += te.getNodeKeysInTrafficJam().size();
				}
			}
			
			double avgCarInTrafficJam = 0.0;
			
			if(numOfTrafficElements > 0)
				avgCarInTrafficJam = carInTrafficJam / (double)numOfTrafficElements;
			
			System.out.println("VT:" + triggeringTime + " Num Of Traffic Element: " +  numOfTrafficElements);
			System.out.println("VT:" + triggeringTime + " Avg Cars in Traffic Element: " +  avgCarInTrafficJam);
			fileValue.add(new LoggerObject("TrafficElements",numOfTrafficElements));
			fileValue.add(new LoggerObject("AvgCarInTraffic",avgCarInTrafficJam));
			
			//Evaluate average of node in each path.
			/*
			D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(d2vPeerIndexList.get(0));
			double carSumInPaths = 0.0;
			for(int ci=0; ci < peer.ssc.getPathList().size(); ci++)
			{
				carSumInPaths += peer.ssc.getPathList().get(ci).getNumOfCars();
			}
			System.out.println("VT:" + triggeringTime + " Avg Cars in Paths: " +  carSumInPaths/(double)peer.ssc.getPathList().size());
			fileValue.add(new LoggerObject("AvgCarsInPaths",carSumInPaths/(double)peer.ssc.getPathList().size()));
			*/
			
			
		}
		
		System.out.println("################################################################################################");
		
		double distributionCoveragePercentageSUM = 0.0;
		double nodeCount = 0.0;
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		for(int i=0; i<D2VPeer.globalMessageKnowledge.size(); i++)
		{
			TrafficInformationMessage msg = D2VPeer.globalMessageKnowledge.get(i);
			
			if(triggeringTime-msg.getTime() < msg.getTtl())
			{
				
				double contactedNode = 0.0;
				double optimalNode = 0.0;
				
				if(d2vPeerIndexList != null)
				{
					for(int index=0; index<d2vPeerIndexList.size();index++)
					{
						D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(d2vPeerIndexList.get(index));
						
						double distance = GeoDistance.distance(peer.getPeerDescriptor().getGeoLocation(), msg.getLocation());
						
						if(distance <= msg.getRange())
						{
							optimalNode ++;
							
							if(peer.getTrafficInformationKnowledge().contains(msg))
								contactedNode ++;
						}
					}
				}
				
				double messageCoveragePercentage = 100.0;
				
				if(optimalNode > 0)
					messageCoveragePercentage = 100.0 * (contactedNode / optimalNode );
				
				distributionCoveragePercentageSUM += messageCoveragePercentage;
				
				nodeCount ++;
				
				System.out.println("Birth Time:" + msg.getTime() + " Message: " + msg.getType() + " in " + msg.getLocation().getLatitude()+";"+msg.getLocation().getLongitude() + " PayLoad: " + new String(msg.getPayload()) + " Distribution Coverage: " + messageCoveragePercentage);
			}
			else
				D2VPeer.globalMessageKnowledge.remove(msg);
		}
		
		double globalAvgCoveragePercentage = 0.0;
		
		if(nodeCount > 0.0)
			globalAvgCoveragePercentage = distributionCoveragePercentageSUM/(double)nodeCount;
		else
			globalAvgCoveragePercentage = 100.0;
		
		System.out.println("GLOBAL COVERAGE PERCENTAGE: " + globalAvgCoveragePercentage);
		fileValue.add(new LoggerObject("CoveragePercentage",globalAvgCoveragePercentage));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		//PATH AVERAGE SPEED EVALUATION
		/*
		if(triggeringTime == Engine.getDefault().getMaxVirtualTime())
		{

			for(int index=0; index<D2VPeer.ssc.getPathList().size(); index++)
			{
				CityPath cp = D2VPeer.ssc.getPathList().get(index);
				
				if(cp.isBadSurfaceCondition() == true)
				{
					System.out.println("Path: " + index);
					
					for(int pointIndex=0; pointIndex < cp.getPathPoints().size(); pointIndex++)
					{
						CityPathPoint point = cp.getPathPoints().get(pointIndex);
						CityPathPoint badSurfacePoint = cp.getPathPoints().get(cp.getBadSurfaceIndex());
						double distance = GeoDistance.distance(point,badSurfacePoint);
						
						if(distance <= 0.3)
						{
							double sum = 0.0;
							
							for(int speedValueIndex=0; speedValueIndex<point.getLogMonitoredSpeed().size(); speedValueIndex++)
								sum += point.getLogMonitoredSpeed().get(speedValueIndex);
							
							double speedAvg = sum / (double)point.getLogMonitoredSpeed().size();
							double sign = 1.0;
							
							if(pointIndex < cp.getBadSurfaceIndex())
								sign = -1.0;
							
							//System.out.println((distance*sign)+" "+speedAvg/cp.getSpeedLimit());
							System.out.println((distance*sign)+" "+speedAvg);
						}
					}
					
				}
				
			}
			
		}
		*/
			/*
			System.out.println("################################################################################################");
			System.out.println("Avg Speed near Bad Surface Condition");
			//ArrayList<ArrayList<Double>> speedAvgSum = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>> speedAvgSum = new ArrayList<ArrayList<Double>>();
			
			int numOfValue = 60;
			
			for(int i=0; i< numOfValue; i++)
			{
				speedAvgSum.add(new ArrayList<Double>());
			}
			
			for(int index=0; index<D2VPeer.ssc.getPathList().size(); index++)
			{
				CityPath cp = D2VPeer.ssc.getPathList().get(index);
				
				if(cp.isBadSurfaceCondition() == true)
				{
					int surfaceIndex = cp.getBadSurfaceIndex();
					
					int basePosition = surfaceIndex-numOfValue/2;
					
					for(int i=0; i< numOfValue; i++)
					{
						CityPathPoint point = cp.getPathPoints().get(basePosition+i);
						
						speedAvgSum.get(i).addAll(point.getLogMonitoredSpeed());

						if(point.getLogMonitoredSpeed().size() > 0)
							System.out.println("Index:" + i + "Speed Value: " + point.getLogMonitoredSpeed().get(0));
						
						//Get monitored speed in the point
						//for(int speedValueIndex=0; speedValueIndex< point.getLogMonitoredSpeed().size(); speedValueIndex++)
						//{
						//	System.out.println("Index:" + i + "Speed Value: " + point.getLogMonitoredSpeed().get(speedValueIndex));
						//	speedAvgSum.get(i).add(point.getLogMonitoredSpeed().get(speedValueIndex));
						//}
					}
				}
			}
			
			for(int index=0; index< speedAvgSum.size(); index++)
			{
				System.out.println("Stored Values: "+speedAvgSum.get(index).size());		
			}
			
			for(int index=0; index< speedAvgSum.size(); index++)
			{
				double sum = 0.0;
				for(int k=0; k<speedAvgSum.get(index).size(); k++)
					sum += speedAvgSum.get(index).get(k);
					
				System.out.println(index+" "+((double)sum/(double)speedAvgSum.get(index).size()));		
			}
			
			System.out.println("################################################################################################");
		}
		*/
		a.write(Engine.getDefault().getVirtualTime(), fileValue);
		

		//log.printEnd(0, this.getClass().getCanonicalName(), triggeringTime);
	}
	
	/*
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
		
		if(Engine.getDefault().getNodeKeysById("D2VPeer") == null || Engine.getDefault().getNodeKeysById("D2VPeer").size() == 0)
			return;
		
		D2VPeer app = (D2VPeer)Engine.getDefault().getNodeByKey(Engine.getDefault().getNodeKeysById("D2VPeer").get(0));
		
		int numOfKBucket = app.getK();
		
		double totalMissNodesNumForGB[] = new double[numOfKBucket];
		
		for(int a=0; a < numOfKBucket; a++)
			totalMissNodesNumForGB[a] = 0.0;
		
		double sumOfAverageOfDiscoveryStep = 0.0;
		int nodeWithDiscoveryCounter = 0;
		
		ArrayList<Integer> peerList = Engine.getDefault().getNodeKeysById("D2VPeer");
		
		for(int i=0; i<peerList.size(); i++)
		{
			
			D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(peerList.get(i));
		
			//peer.printEdgeList();
			
			//missNodesIndex = new double [peer.getNumOfKBuckets()];
			missNodesNumForGB = new double [peer.getGb().getK_VALUE()];
			
			for(int a=0; a < peer.getGb().getK_VALUE(); a++)
				missNodesNumForGB[a] = 0.0;
				
			
			int optimalNumber = 0;
			int currentNumber = 0;
			int geoBucketDistanceError = 0;
	
			
			if(peer.getId().equals("D2VPeer"))
			{
				if(peer.getDiscoveryCounter() != 0)
				{	
					nodeWithDiscoveryCounter++;
					sumOfAverageOfDiscoveryStep += (double)peer.getAvDiscoveryStepCounter()/(double)peer.getDiscoveryCounter();
				}
				
				//Read all available nodes in the system
				for(int k=0; k < Engine.getDefault().getNodes().size(); k++)
				{
					if(Engine.getDefault().getNodes().get(k).getId().equals("D2VPeer"))
					{	
						D2VPeer testPeer = (D2VPeer)Engine.getDefault().getNodes().get(k);
						
						if(!peer.equals(testPeer))
						{
							double distance = GeoDistance.distance(peer.getPeerDescriptor(),testPeer.getPeerDescriptor());
							
							boolean bucketFounded = false;
							
							for(int j=0; j<(peer.getGb().getK_VALUE()); j++)
							{
								if((distance <= (double)(j)*peer.getRadiusKm()) && bucketFounded == false)
								{		
									bucketFounded = true;
									
									//Increment the optimal number of peer
									optimalNumber++;
									
									//Check if is in the original peer list
									if(peer.getGb().containsPeerInGeoBuckets(testPeer.getPeerDescriptor()))
									{
										//Increment the number of current available peer
										currentNumber++;
									
										//Check if is in the right GeoBucket
										int geoBucketPosition = peer.getGb().indexOfGeoBucketFor(testPeer.getPeerDescriptor());
										
										if(geoBucketPosition != -1)
										{
											D2VPeerDescriptor localPeerInfo = peer.getGb().getBucket().get(geoBucketPosition).get(peer.getGb().getBucket().get(geoBucketPosition).indexOf(testPeer.getPeerDescriptor()));
											
											double localDistance = GeoDistance.distance(localPeerInfo, testPeer.getPeerDescriptor());
											
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
		
		if(Engine.getDefault().getNodeKeysById("D2VPeer") != null && Engine.getDefault().getNodeKeysById("D2VPeer").size() > 0)
		{
			fileValue.add(new LoggerObject("MISSING_NODE", 100.0*(double)((double)missingNode/(double)(Engine.getDefault().getNodeKeysById("D2VPeer").size()))));	
			fileValue.add(new LoggerObject("GB_DISTANCE_ERROR", 100.0*(double)((double)gbErrorIndex/(double)(Engine.getDefault().getNodeKeysById("D2VPeer").size()))));	
			System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT ---> % MISSING NODE: " + 	100.0*(double)((double)missingNode/(double)(Engine.getDefault().getNodeKeysById("D2VPeer").size())));
			System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT --->  % GB DISTANCE ERROR: " + (double)((double)gbErrorIndex/(double)(Engine.getDefault().getNodeKeysById("D2VPeer").size())));
			System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT --->  AV Of Discovery Step Counter: " + (double)(sumOfAverageOfDiscoveryStep/(double)(nodeWithDiscoveryCounter)));
			
			if(distanceErrorCounter != 0)
			{
				System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT --->  AV Local Distance Error (Km): " + (double)(distanceError/(double)distanceErrorCounter));
				fileValue.add(new LoggerObject("AV_Local_Distance_Error", (double)(distanceError/(double)distanceErrorCounter)));
			}
			else
				fileValue.add(new LoggerObject("AV_Local_Distance_Error", 0.0));
			
			double tot = 0.0;
			
			for(int index=0; index < numOfKBucket; index++ )
			{
				if(peerWithMissing > 0.0)
				{
					System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT --->  Per_Miss_Node_For_GB_"+ index + " --->" + (double)totalMissNodesNumForGB[index]/peerWithMissing);
					fileValue.add(new LoggerObject("Per_Miss_Node_For_GB_"+index, (double)totalMissNodesNumForGB[index]/peerWithMissing));	
					tot +=(double)totalMissNodesNumForGB[index]/peerWithMissing;
				}
				else
				{
					System.out.println("VT:" + triggeringTime + " LOG_STAT_EVENT --->  Per_Miss_Node_For_GB_"+ index + " --->" + 0.0);
					fileValue.add(new LoggerObject("Per_Miss_Node_For_GB_"+index, 0.0));
				}
			}
			System.out.println("TOT:" + tot);
		}
	}
	*/
}
