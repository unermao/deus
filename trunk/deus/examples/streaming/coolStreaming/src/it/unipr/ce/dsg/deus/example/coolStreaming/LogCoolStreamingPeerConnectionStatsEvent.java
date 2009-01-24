package it.unipr.ce.dsg.deus.example.coolStreaming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This Event logs a number of statistics related to the current "snapshot" of
 * the StreamingPeer network:
 * <ol>
 * <li>mean value and variance of the chromosomes</li>
 * <li>initial and mean Streaming value</li>
 * <li>number of searchers (i.e. peers with at least 1 query sent)</li>
 * <li>average QHR (query hit ratio)</li>
 * </ol>
 *
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class LogCoolStreamingPeerConnectionStatsEvent extends Event {

	private boolean fileCreated = false;

	public LogCoolStreamingPeerConnectionStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {

		getLogger().info("\nNode Connection Stats:");
		
		double activeConnectionTotal = 0.0;
		double activeConnectionPercentTotalPcNode = 0.0;
		double activeConnectionPercentTotalPcNodeHigh = 0.0;
		double activeConnectionPercentTotalSuperNode = 0.0;
		double activeConnectionServer = 0.0;
		
		int PcISP0 = 0;
		int HighISP0 = 0;
		int SuperISP0 = 0;
		int PcISP1 = 0;
		int HighISP1 = 0;
		int SuperISP1 = 0;
		int PcISP2 = 0;
		int HighISP2 = 0;
		int SuperISP2 = 0;
		int totalstop = 0;
		
		double totalPcNode = 0.0;
		double totalPcNodeHigh = 0.0;
		double totalSuperNode = 0.0;
		
		float totalReceivedChunk = 0;
		float totalPcReceivedChunk = 0;
		float totalPcHighReceivedChunk = 0;
		float totalSuperNodeReceivedChunk = 0;

		int unstablesNode90pcNode = 0;
		int unstablesNode90pcNodeHigh = 0;
		int unstablesNode90superNode = 0;
		//int unstablesNode80 = 0;
		
		float totalArrivalTime = 0;
		float totalPcArrivalTime = 0;
		float totalPcHighArrivalTime = 0;
		float totalSuperNodeArrivalTime = 0;
		
		float averageArrivalTime = 0;
		float averagePcArrivalTime = 0;
		float averagePcHighArrivalTime = 0;
		double totalOverheadMessage = 0;
		
		float totalStartUpTimePc = 0;
		int numberPlayerPcNode = 0;
		float totalStartUpTimeHigh = 0;
		int numberPlayerHighNode = 0;
		
		int out_degree_pcNode = 0;//new ArrayList<Integer>();
		int out_degree_pcNodeHigh = 0;
		int out_degree_superNode = 0;
		
		int superConnection = 0;
		int pcConnection = 0;
		int highConnection = 0 ;
		
		double uploadPc = 0.0;
		double uploadHigh = 0.0;
		double uploadSuper = 0.0;
		
		float PctotalReceivedChunkReal = 0;
		double PctotalDeadlineNumberReal = 0;
		float HightotalReceivedChunkReal = 0;
		double HightotalDeadlineNumberReal = 0;
		//for(int i=0; i<4; i++)
		//out_degree.add(0);
		
		//Varibiali per calcolare la media dei chunk mancanti
		double totalMissingChunk = 0;
		double totalArrivedChunk = 0;
	
		//Varibiali per calcolare la media dei chunk mancanti
		double totalDuplicateChunk = 0;
		
		//Variabili per calcolare le deadLine
		double totalDeadlineNumber = 0;
		
		double totalInternalISP = 0;
		double totalExternalISP = 0;
		
		CoolStreamingServerPeer serverPeer = (CoolStreamingServerPeer) Engine.getDefault().getNodes().get(0);
		
				

		//Aggiungo alle statistiche relative ai chunk mancanti quelle memorizzate nel server dai nodi disconnessi
		totalMissingChunk = totalMissingChunk + (serverPeer.getMissingChunkNumber());
		totalArrivedChunk = totalArrivedChunk + serverPeer.getTotalChunkReceived();
		
		totalDeadlineNumber = totalDeadlineNumber + serverPeer.getTotalDeadline();
		
		//totalStartUpTime += serverPeer.getStartUpTime();
		
		//Aggiungo le statistiche relative ai doppioni dei nodi che si sono disconnessi e che hanno salvato le statistiche nel server
		totalDuplicateChunk = totalDuplicateChunk + serverPeer.getDuplicateChunkNumber();
		
		activeConnectionServer = (double)serverPeer.getActiveConnection();
		
		//Trovo il massimo grado di nodo
		int maxNodeDepth = 0;
		for(int k = 1; k < Engine.getDefault().getNodes().size(); k++ ){
		CoolStreamingPeer peer = (CoolStreamingPeer) Engine.getDefault().getNodes().get(k);
			
			maxNodeDepth = Math.max(maxNodeDepth, peer.getNodeDepth());
		}
			
		//Aggiungo i tempi di ricezione dei nodi PC disconnessi
		totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesPcNode().size();
		totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesPcNodeHigh().size();
		//totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesMobile3GNode().size();
		
		totalPcReceivedChunk = totalPcReceivedChunk + serverPeer.getArrivalTimesPcNode().size();
		
		for(int i = 0 ; i < serverPeer.getArrivalTimesPcNode().size(); i++)
		{
			
			float localValue = serverPeer.getArrivalTimesPcNode().get(i);			
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalPcArrivalTime = totalPcArrivalTime + localValue;
		}
		
		
		totalPcHighReceivedChunk = totalPcHighReceivedChunk + serverPeer.getArrivalTimesPcNodeHigh().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesPcNodeHigh().size(); i++)
		{
			
			float localValue = serverPeer.getArrivalTimesPcNodeHigh().get(i);			
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalPcHighArrivalTime = totalPcHighArrivalTime + localValue;
		}
		
	
		//Creo la matrice per le statistiche sulla profondita'
		ArrayList<ArrayList<Integer>> depthMatrix = new ArrayList<ArrayList<Integer>>();
		
		for(int j = 0 ; j < maxNodeDepth; j++ )
			depthMatrix.add(new ArrayList<Integer>());
			
		//Controllo le connessioni attie dei diversi nodi
		for(int index = 1; index < Engine.getDefault().getNodes().size(); index++ ){												
			
			CoolStreamingPeer peer = (CoolStreamingPeer) Engine.getDefault().getNodes().get(index);						
			
			totalOverheadMessage += peer.getOverhead();
			
			totalInternalISP += peer.getExchangeInternalISP();
			totalExternalISP += peer.getExchangeExternalISP();
			
			if(peer.getStartUpTime() != 0)
			{
				if(peer.getId().equals("pcNode"))
				{
					numberPlayerPcNode++;
					totalStartUpTimePc += peer.getStartUpTime();
				}
				else 
				{
					numberPlayerHighNode++;
					totalStartUpTimeHigh += peer.getStartUpTime();
				}	
			//	System.out.println(peer.getStartUpTime() + " num " + numberPlayerNode);				
				
			}
			//CALCOLO IL CONTINUITY INDEX DEI NODI CONNESSI 
			if(peer.getTotalChunkReceived() > 0 )
			{
				double nodeContinuityIndex = 0;
				if(!peer.isFirst()){
					nodeContinuityIndex = (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0;
				if(nodeContinuityIndex < 0)
					nodeContinuityIndex=0;
						
				}
					
				totalstop+= peer.getStop();			
				
//				System.out.println(nodeContinuityIndex);
				//Incremento il numero di nodi instabili
				if(peer.getId().equals("pcNode"))
				{	
				if( (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 <= 90.0 && (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 !=0)
					unstablesNode90pcNode ++;
		
				uploadPc = peer.getUploadSpeed();
				
					if(peer.getIsp() == 0)
						PcISP0++;						
					if(peer.getIsp() == 1)
						PcISP1++;	
					if(peer.getIsp() == 2)
						PcISP2++;
				
				pcConnection += peer.getExchangeExternalISP();	
					
				//for(int ind=0; ind<peer.getK_value();ind++)										{
					out_degree_pcNode += peer.getActiveConnection();
				//System.out.println("Pc "+ peer.getServedPeers2().get(ind).size());
				//	}
				}
				
				if(peer.getId().equals("pcNodeHigh")) 
				{									
				//	for(int ind=0; ind<peer.getK_value();ind++)	{
					//	System.out.println("High "+ peer.getServedPeers2().get(ind).size());
						out_degree_pcNodeHigh += peer.getActiveConnection();
				//	}
						uploadHigh = peer.getUploadSpeed();
						
						if(peer.getIsp() == 0)
							HighISP0++;						
						if(peer.getIsp() == 1)
							HighISP1++;	
						if(peer.getIsp() == 2)
							HighISP2++;	
				
						highConnection += peer.getExchangeExternalISP();	
						
					if( (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 <= 90.0 && (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 !=0)
						unstablesNode90pcNodeHigh ++;
				}	
				
				if(peer.getId().equals("superNode")) 
				{									
				//	for(int ind=0; ind<peer.getK_value();ind++)	{
					//	System.out.println("High "+ peer.getServedPeers2().get(ind).size());
						out_degree_superNode += peer.getActiveConnection();
				//	}
						uploadSuper = peer.getUploadSpeed();
						
						if(peer.getIsp() == 0)
							SuperISP0++;						
						if(peer.getIsp() == 1)
							SuperISP1++;	
						if(peer.getIsp() == 2)
							SuperISP2++;
						
						superConnection += peer.getExchangeExternalISP();
						
					if( (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 <= 90.0 && (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0 !=0)
						unstablesNode90superNode++;
				}	
					//System.out.println(out_degree);
//				if( nodeContinuityIndex <= 80.0 )
//					unstablesNode80 ++;
			}
			
			//CALCOLO LA MEDIA DEI CHUNK MANCANTI
			totalMissingChunk = totalMissingChunk + (double)(peer.getMissingChunkNumber());
			totalArrivedChunk = totalArrivedChunk + (double)peer.getTotalChunkReceived();
		
			//CALCOLO IL NUMERO DEI DUPLICATI
			totalDuplicateChunk = totalDuplicateChunk + peer.getDuplicateChunkNumber();
			
			//CALCOLO LE DEADLINE TOTALI
			totalDeadlineNumber = totalDeadlineNumber + peer.getDeadlineNumber();
			
			//CALCOLO MEDIA SEGMENTI RICEVUTI IN BASE ALLA PROFONDITA' DEL NODO
			
			
			//Se il nodo e' connesso a qualcuno e ha almeno una risorsa 
			if(peer.getNodeDepth() > 0 && !peer.isFirst())									
				depthMatrix.get(peer.getNodeDepth()-1).add(peer.getIndexOfLastPlayedChunk());//peer.getNodeDepth()-1).add(peer.getPlayer().get(peer.getPlayer().size()-1).getChunkIndex());
				
				
			//CALCOLO TEMPO MEDIO DI ARRIVO CHUNK
			
			//Aggiungo il numero di chunk ricevuti dal singolo nodo
			totalReceivedChunk = totalReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("pcNode"))
				totalPcReceivedChunk = totalPcReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("pcNodeHigh"))
				totalPcHighReceivedChunk = totalPcHighReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("superNode"))
				totalSuperNodeReceivedChunk = totalSuperNodeReceivedChunk + peer.getArrivalTimes().size();
			
			for( int k = 0 ; k < peer.getArrivalTimes().size(); k++ ){
				
				float localValue =  peer.getArrivalTimes().get(k);
				
				totalArrivalTime = totalArrivalTime + localValue;
				
				if(peer.getId().equals("pcNode"))
					totalPcArrivalTime = totalPcArrivalTime + localValue;
						
				if(peer.getId().equals("pcNodeHigh"))
					totalPcHighArrivalTime = totalPcHighArrivalTime + localValue;
				
				if(peer.getId().equals("superNode"))
					totalSuperNodeArrivalTime = totalSuperNodeArrivalTime + localValue;
			}
			
			//CALCOLO PERCENTUALI CONNESSIONI ATTIVE
			activeConnectionTotal = activeConnectionTotal + ( (double)peer.getActiveConnection());
			
			
			if(peer.getId().equals("pcNode"))
			{
				activeConnectionPercentTotalPcNode = activeConnectionPercentTotalPcNode + ( (double)peer.getActiveConnection());
				totalPcNode++;
			}

			if(peer.getId().equals("pcNodeHigh"))
			{
				activeConnectionPercentTotalPcNodeHigh = activeConnectionPercentTotalPcNodeHigh + ( (double)peer.getActiveConnection());
				totalPcNodeHigh++;
			}
			
			if(peer.getId().equals("superNode"))
			{
				activeConnectionPercentTotalSuperNode = activeConnectionPercentTotalSuperNode + ( (double)peer.getActiveConnection());
				totalSuperNode++;
			}
			
		}
		
		    getLogger().info("Totale Connessioni attive Totale               : " + activeConnectionTotal + "\n");
		
		    getLogger().info("Media Connessioni attive SERVER             (%): " + 100.0*(activeConnectionServer)/(activeConnectionTotal + activeConnectionServer));  
		    
		if(totalPcNode!=0.0)
			getLogger().info("Media Connessioni attive PC-NODE            (%): " + 100.0*(activeConnectionPercentTotalPcNode/(activeConnectionTotal + activeConnectionServer)));
	
		if(totalPcNodeHigh!=0.0)
			getLogger().info("Media Connessioni attive PC-NODE-HIGH            (%): " + 100.0*(activeConnectionPercentTotalPcNodeHigh/(activeConnectionTotal + activeConnectionServer)));
		
		getLogger().info("\n");
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime() + "\n");
		
		averageArrivalTime = totalArrivalTime / totalReceivedChunk;
		averagePcArrivalTime = totalPcArrivalTime / totalPcReceivedChunk;
		averagePcHighArrivalTime = totalPcHighArrivalTime / totalPcHighReceivedChunk;	

		getLogger().info("-------------------- NODES INFO ----------------------------------------");
		getLogger().info("Total Nodes             : " + (double)( totalPcNode+totalPcNodeHigh +serverPeer.getDisconnectedNodes()));
		getLogger().info("Disconnected Nodes      : " + serverPeer.getDisconnectedNodes());
		getLogger().info("Total Active Node       : " + (double)( totalPcNode + totalPcNodeHigh));
		getLogger().info("Total PcNode            : " + totalPcNode + " [ " + (totalPcNode/(double)( totalPcNode+totalPcNodeHigh))*100 + "% ]");
		getLogger().info("Total PcNodeHigh        : " + totalPcNodeHigh + " [ " + (totalPcNodeHigh/(double)( totalPcNode+totalPcNodeHigh))*100 + "% ]");	
		getLogger().info("------------------------------------------------------------------------");
		
		
		getLogger().info("\n");
		getLogger().info("-------------------- CHUNKS ARRIVAL TIME INFO --------------------------");
		getLogger().info("Average Arrival Times                   : " + averageArrivalTime);	
		getLogger().info("");
		getLogger().info("PC-Node Average Arrival Times           : " + averagePcArrivalTime);
		getLogger().info("");
		getLogger().info("PC-Node-High Average Arrival Times           : " + averagePcHighArrivalTime);
		getLogger().info("------------------------------------------------------------------------");
		
		getLogger().info("\n");		
		getLogger().info("-------------------- NODES DEPTH INFO ----------------------------------");
		
		
		AutomatorLogger a = new AutomatorLogger("./temp/logger");
		
		ArrayList<LoggerObject> fileValue = new ArrayList<LoggerObject>();
		
		for(int j = 0 ; j < maxNodeDepth; j++ )
		{
			
			long totalSum = 0;
			int chunkIndexAverage = 0;
			
			if( depthMatrix.get(j).size() > 0 )
			{
				
				for( int k = 0 ; k < depthMatrix.get(j).size(); k++)
				{
					
					totalSum = totalSum + depthMatrix.get(j).get(k);
				}
				
			//	System.out.println("sum " +totalSum);
				chunkIndexAverage = (int) (totalSum / depthMatrix.get(j).size());
			//	System.out.println(chunkIndexAverage);
				//System.out.println(chunkIndexAverage);
				getLogger().info("Nodes Depth: " + (j+1) + " Chunk Average: " + chunkIndexAverage);		
//				fileValue.add(new LoggerObject("Nodes Depth: " + (j+1) + " Chunk Average:", chunkIndexAverage));
			}
		}	
		getLogger().info("------------------------------------------------------------------------");

		getLogger().info("\n");
		getLogger().info("-------------------- CHUNKS NUMBER / DEADLINE INFO ---------------------");
		getLogger().info("\n");
		getLogger().info("Total Chunk           : " + (totalDuplicateChunk+totalArrivedChunk));
		getLogger().info("Total Arrived Chunk   : " + totalArrivedChunk);
		getLogger().info("Average Missed Chunk  : " + (totalMissingChunk/totalArrivedChunk)*100 + " %");	
		
		getLogger().info("\n");
		getLogger().info("Total Duplicated Chunk                                     : " + totalDuplicateChunk);
		getLogger().info("Average Duplicated Chunk (Total Duplicate / Total Arrived) : " + (totalDuplicateChunk/(totalDuplicateChunk+totalArrivedChunk))*100.0 + " %");	
		getLogger().info("\n");
		
		getLogger().info("Chunk Missing Number: " + totalMissingChunk);
		getLogger().info("\n");
		
		getLogger().info("Chunk Deadline Number: " + totalDeadlineNumber);
		getLogger().info("\n");
		
		getLogger().info("Perceived Continuity Index Pc: " + ((PctotalReceivedChunkReal -PctotalDeadlineNumberReal)/PctotalReceivedChunkReal)*100.0 + "%");
		getLogger().info("Perceived Continuity Index High: " + ((HightotalReceivedChunkReal -HightotalDeadlineNumberReal)/HightotalReceivedChunkReal)*100.0 + "%");
		getLogger().info("\n");	
		
		getLogger().info("Unstables Node pcNode(Continuity Index < 90 %): " + unstablesNode90pcNode);
		getLogger().info("Unstables Node pcNodeHigh(Continuity Index < 90 %): " + unstablesNode90pcNodeHigh);
		//getLogger().info("Unstables Node (Continuity Index < 80 %): " + unstablesNode80);
		getLogger().info("\n");
		
		getLogger().info("Average Out-Degree pcNode: " + out_degree_pcNode/(totalPcNode));
		getLogger().info("Average Out-Degree pcNodeHigh: " + out_degree_pcNodeHigh/(totalPcNodeHigh));
		getLogger().info("\n");
		getLogger().info("Average Start Up Time Pc: " + ((totalStartUpTimePc/((double)( numberPlayerPcNode))))/20);
		getLogger().info("Average Start Up Time High: " + ((totalStartUpTimeHigh/((double)( numberPlayerHighNode))))/20);
		getLogger().info("------------------------------------------------------------------------");
			
		fileValue.add(new LoggerObject("Tot Pc in Isp 0", PcISP0));
		fileValue.add(new LoggerObject("Tot High Pc in Isp 0", HighISP0));
		fileValue.add(new LoggerObject("Tot Super in Isp 0", SuperISP0));
		fileValue.add(new LoggerObject("Tot Pc in Isp 1", PcISP1));
		fileValue.add(new LoggerObject("Tot High Pc in Isp 1", HighISP1));
		fileValue.add(new LoggerObject("Tot Super in Isp 1", SuperISP1));
		fileValue.add(new LoggerObject("Tot Pc in Isp 2", PcISP2));
		fileValue.add(new LoggerObject("Tot High Pc in Isp 2", HighISP2));
		fileValue.add(new LoggerObject("Tot Super in Isp 2", SuperISP2));
		fileValue.add(new LoggerObject("Buffering", totalstop/(totalPcNode + totalPcNodeHigh + totalSuperNode)));
		//Paccheti di overhead di 30byte
		fileValue.add(new LoggerObject("pcConnection", (double)((pcConnection+highConnection)/totalExternalISP)));
	//	fileValue.add(new LoggerObject("highConenction", (double)(highConnection/totalPcNodeHigh)));
		fileValue.add(new LoggerObject("superConnection", (double)(superConnection/totalExternalISP)));
//		fileValue.add(new LoggerObject("pcConnectionsuPcOver", (double)((pcConnection/totalPcNode))/2.0));
//		fileValue.add(new LoggerObject("highConenctionOver", (double)((highConnection/totalPcNodeHigh)/2.0)));
//		fileValue.add(new LoggerObject("superConnectionOver", (double)((superConnection/totalSuperNode)/2.0)));
		fileValue.add(new LoggerObject("Overhead [Byte]%", (double)((totalOverheadMessage)/((totalInternalISP + totalExternalISP)*682+ totalOverheadMessage) )*100));
		fileValue.add(new LoggerObject("Average bandwidth", (((double)totalPcNode*uploadPc + (double)totalPcNodeHigh*uploadHigh + (double)totalSuperNode*uploadSuper)) / (totalSuperNode+totalPcNodeHigh+totalPcNode)));
		fileValue.add(new LoggerObject("Overhead message%", (double)(totalOverheadMessage/(totalInternalISP + totalExternalISP+totalOverheadMessage))*100));
		fileValue.add(new LoggerObject("Exchange Internal ISP %", (double)totalInternalISP/(totalInternalISP + totalExternalISP)*100));
		fileValue.add(new LoggerObject("Exchange External ISP %", (double)totalExternalISP/(totalInternalISP + totalExternalISP)*100));
		fileValue.add(new LoggerObject("Total PC Node", (double) totalPcNode ));
		fileValue.add(new LoggerObject("Total PC Node High", (double) totalPcNodeHigh ));
		fileValue.add(new LoggerObject("Total SuperNode", (double) totalSuperNode ));
		fileValue.add(new LoggerObject("Average Start Up Time Pc", ((totalStartUpTimePc/((double)( numberPlayerPcNode))))/20));
		fileValue.add(new LoggerObject("Average Start Up Time High" , ((totalStartUpTimeHigh/((double)( numberPlayerHighNode))))/20));
		fileValue.add(new LoggerObject("Continuity Index", ((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0));
		fileValue.add(new LoggerObject("Duplicate %", (totalDuplicateChunk/(totalDuplicateChunk+totalArrivedChunk))*100.0));
		fileValue.add(new LoggerObject("Total Node", (double)( totalPcNode + totalPcNodeHigh + totalSuperNode)));
		fileValue.add(new LoggerObject("Total Disconnected Node", serverPeer.getDisconnectedNodes()));
		fileValue.add(new LoggerObject("Average Out-Degree superNode", out_degree_superNode/(totalSuperNode)));
		fileValue.add(new LoggerObject("Average Out-Degree pcNode", out_degree_pcNode/(totalPcNode)));
		fileValue.add(new LoggerObject("Average Out-Degree pcNodeHigh",out_degree_pcNodeHigh/(totalPcNodeHigh)));
		fileValue.add(new LoggerObject("Unstable Node", unstablesNode90pcNode + unstablesNode90pcNodeHigh + unstablesNode90superNode));
		
		
				//for(int vt = 10 ; vt < 100 ; vt = vt +10 )
		a.write(Engine.getDefault().getVirtualTime(), fileValue);		

		
		
		if(Engine.getDefault().getVirtualTime() > 20){
		this.writeGnuPlotFile(totalPcNode+totalPcNodeHigh,((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0,"continuityIndex-node");
		this.writeGnuPlotFile(Engine.getDefault().getVirtualTime()/20,((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0,"continuityIndex-time");
		//this.writeGnuPlotFile(Engine.getDefault().getVirtualTime(),unstablesNode90pcNode, "UnstablepcNode123456789");
		//this.writeGnuPlotFile(Engine.getDefault().getVirtualTime(),unstablesNode90pcNodeHigh, "UnstablepcNodeHigh123456789");
		this.writeGnuPlotFile(Engine.getDefault().getVirtualTime(),unstablesNode90pcNodeHigh+unstablesNode90pcNode, "UnstablepcNodeTotal");
		
		this.writeGnuPlotFile(Engine.getDefault().getVirtualTime(),out_degree_pcNode/(totalPcNode), "AverageOutDegreepcNode");
		
		this.writeGnuPlotFile(Engine.getDefault().getVirtualTime(),out_degree_pcNodeHigh/(totalPcNodeHigh), "AverageOutDegreepcNodeHigh");
		
		 fileCreated = true;
		}
		getLogger().info("\n");
		getLogger().info("########################################################################");
		
	}
	
//	public void write(double activePcNode, double continuityIndex)
//	{
//		try {
//			
//			 String filename = params.getProperty("filename");						 
//		     
//			 FileOutputStream fis = new FileOutputStream(new File("C:/Users/Marco/Desktop/"+filename),true);
//			  		      		     
//		     String string = "";
//		    
//		     string = activePcNode + " " + continuityIndex + "\n";
//			  
//			 fis.write(string.getBytes());
//
//		     fis.flush();
//		     fis.close();
//		      
//		    } catch (IOException e) {
//		      System.out.println("Errore: " + e);
//		      System.exit(1);
//		    }
//	}

	
	
	public void writeGnuPlotFile(double x, double y, String filenameSuffix)
	{
		
	//Verifico che la y sia un numero	

	 try {
	  
	   String filename = filenameSuffix + "_" + params.getProperty("filename");       
	  
	   File file = new File("log/"+filename);
	   
	   if(fileCreated  == false)
	   { 
		   if(file.exists())
		   {   
			   System.out.println("ELIMINO: " + file.getName() );
			   if(file.delete())
				   System.out.println("ELIMINO");
			   else System.out.println("NON ELIMINO");
		   }
		   		  
	   }
	   
	   FileOutputStream fis = new FileOutputStream(file,true);
	                   
	      String string = "";
	     
	      string = x + " " + y + "\n";
	    
	   fis.write(string.getBytes());

	      fis.flush();
	      fis.close();
	       
	     } catch (IOException e) {
	       System.out.println("Errore: " + e);
	       System.exit(1);
	     }
	
	}
	
		
}
