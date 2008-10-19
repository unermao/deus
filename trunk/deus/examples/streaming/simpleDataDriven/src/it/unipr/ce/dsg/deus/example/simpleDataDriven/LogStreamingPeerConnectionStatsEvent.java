package it.unipr.ce.dsg.deus.example.simpleDataDriven;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

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
public class LogStreamingPeerConnectionStatsEvent extends Event {

	public LogStreamingPeerConnectionStatsEvent(String id, Properties params,
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
		double activeConnectionPercentTotalMobile = 0.0;
		double activeConnectionPercentTotalMobile3g = 0.0;
		double activeConnectionServer = 0.0;
		
		double totalPcNode = 0.0;
		double totalmobileNode = 0.0;
		double totalmobile3GNode = 0.0;
		
		float totalReceivedChunk = 0;
		float totalPcReceivedChunk = 0;
		float totalMobileWifiReceivedChunk = 0;
		float totalMobile3GReceivedChunk = 0;
		
		float totalArrivalTime = 0;
		float totalPcArrivalTime = 0;
		float totalMobileWifiArrivalTime = 0;
		float totalMobile3GArrivalTime = 0;
		
		float averageArrivalTime = 0;
		float averagePcArrivalTime = 0;
		float averageMobileWifiArrivalTime = 0;
		float averageMobile3GArrivalTime = 0;
		
		//Varibiali per calcolare la media dei chunk mancanti
		double totalMissingChunk = 0;
		double totalArrivedChunk = 0;
	
		//Varibiali per calcolare la media dei chunk mancanti
		double totalDuplicateChunk = 0;
		
		//Variabili per calcolare le deadLine
		double totalDeadlineNumber = 0;
		
		ServerPeer serverPeer = (ServerPeer) Engine.getDefault().getNodes().get(0);

		//Aggiungo alle statistiche relative ai chunk mancanti quelle memorizzate nel server dai nodi disconnessi
		totalMissingChunk = totalMissingChunk + serverPeer.getMissingChunkNumber();
		totalArrivedChunk = totalArrivedChunk + serverPeer.getTotalChunkReceived();
		
		totalDeadlineNumber = totalDeadlineNumber + serverPeer.getTotalDeadine();
		
		
		//Aggiungo le statistiche relative ai doppioni dei nodi che si sono disconnessi e che hanno salvato le statistiche nel server
		totalDuplicateChunk = totalDuplicateChunk + serverPeer.getDuplicateChunkNumber();
		
		activeConnectionServer = (double)serverPeer.getActiveConnection();
		
		//Trovo il massimo grado di nodo
		int maxNodeDepth = 0;
		for(int k = 1; k < Engine.getDefault().getNodes().size(); k++ ){
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(k);
			
			maxNodeDepth = Math.max(maxNodeDepth, peer.getNodeDepth());
		}
			
		//Aggiungo i tempi di ricezione dei nodi PC disconnessi
		totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesPcNode().size();
		totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesMobileWifiNode().size();
		totalReceivedChunk = totalReceivedChunk + serverPeer.getArrivalTimesMobile3GNode().size();
		
		totalPcReceivedChunk = totalPcReceivedChunk + serverPeer.getArrivalTimesPcNode().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesPcNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesPcNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalPcArrivalTime = totalPcArrivalTime + localValue;
		}
		
		//Aggiungo i tempi di ricezione dei nodi Mobili Wifi disconnessi
		totalMobileWifiReceivedChunk = totalMobileWifiReceivedChunk + serverPeer.getArrivalTimesMobileWifiNode().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesMobileWifiNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesMobileWifiNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalMobileWifiArrivalTime = totalMobileWifiArrivalTime + localValue;
		}	

		//Aggiungo i tempi di ricezione dei nodi Mobili 3G disconnessi
		totalMobile3GReceivedChunk = totalMobile3GReceivedChunk + serverPeer.getArrivalTimesMobile3GNode().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesMobile3GNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesMobile3GNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalMobile3GArrivalTime = totalMobile3GArrivalTime + localValue;
		}	
		
		//Creo la matrice per le statistiche sulla profondita'
		ArrayList<ArrayList<Integer>> depthMatrix = new ArrayList<ArrayList<Integer>>();
		
		for(int j = 0 ; j < maxNodeDepth; j++ )
			depthMatrix.add(new ArrayList<Integer>());
			
		//Controllo le connessioni attie dei diversi nodi
		for(int index = 1; index < Engine.getDefault().getNodes().size(); index++ ){
			
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(index);
			
			//CALCOLO LA MEDIA DEI CHUNK MANCANTI
			totalMissingChunk = totalMissingChunk + (double)peer.getMissingChunkNumber();
			totalArrivedChunk = totalArrivedChunk + (double)peer.getTotalChunkReceived();
			
			//CALCOLO IL NUMERO DEI DUPLICATI
			totalDuplicateChunk = totalDuplicateChunk + peer.getDuplicateChunkNumber();
			
			//CALCOLO LE DEADLINE TOTALI
			totalDeadlineNumber = totalDeadlineNumber + peer.getDeadlineNumber();
			
			//CALCOLO MEDIA SEGMENTI RICEVUTI IN BASE ALLA PROFONDITA' DEL NODO
			
			//Se il nodo e' connesso a qualcuno e ha almeno una risorsa 
			if(peer.getNodeDepth() > 0 && peer.getVideoResource().size() > 0)
				depthMatrix.get(peer.getNodeDepth()-1).add(peer.getVideoResource().get(peer.getVideoResource().size()-1).getChunkIndex());
				
			//CALCOLO TEMPO MEDIO DI ARRIVO CHUNK
			
			//Aggiungo il numero di chunk ricevuti dal singolo nodo
			totalReceivedChunk = totalReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("pcNode"))
				totalPcReceivedChunk = totalPcReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("mobileNode"))	
				totalMobileWifiReceivedChunk = totalMobileWifiReceivedChunk + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("mobile3GNode"))
				totalMobile3GReceivedChunk = totalMobile3GReceivedChunk + peer.getArrivalTimes().size();
			
			for( int k = 0 ; k < peer.getArrivalTimes().size(); k++ ){
				
				float localValue =  peer.getArrivalTimes().get(k);
				
				totalArrivalTime = totalArrivalTime + localValue;
				
				if(peer.getId().equals("pcNode"))
					totalPcArrivalTime = totalPcArrivalTime + localValue;
				
				if(peer.getId().equals("mobileNode"))	
					totalMobileWifiArrivalTime = totalMobileWifiArrivalTime + localValue;
				
				if(peer.getId().equals("mobile3GNode"))
					totalMobile3GArrivalTime = totalMobile3GArrivalTime + localValue;
			}
			
			//CALCOLO PERCENTUALI CONNESSIONI ATTIVE
			activeConnectionTotal = activeConnectionTotal + ( (double)peer.getActiveConnection());
			
			
			if(peer.getId().equals("pcNode"))
			{
				activeConnectionPercentTotalPcNode = activeConnectionPercentTotalPcNode + ( (double)peer.getActiveConnection());
				totalPcNode++;
			}
		
			if(peer.getId().equals("mobileNode"))
			{
				activeConnectionPercentTotalMobile = activeConnectionPercentTotalMobile + ( (double)peer.getActiveConnection());
				totalmobileNode++;
			}
			
			if(peer.getId().equals("mobile3GNode"))
			{
				activeConnectionPercentTotalMobile3g = activeConnectionPercentTotalMobile3g + ( (double)peer.getActiveConnection());
				totalmobile3GNode++;
			}

		}
		
		    getLogger().info("Totale Connessioni attive Totale               : " + activeConnectionTotal + "\n");
		
		    getLogger().info("Media Connessioni attive SERVER             (%): " + 100.0*(activeConnectionServer)/(activeConnectionTotal + activeConnectionServer));  
		    
		if(totalPcNode!=0.0)
			getLogger().info("Media Connessioni attive PC-NODE            (%): " + 100.0*(activeConnectionPercentTotalPcNode/(activeConnectionTotal + activeConnectionServer)));
	
		if(totalmobileNode!=0.0)
			getLogger().info("Media Connessioni attive MOBILE Wifi NODE   (%): " + 100.0*(activeConnectionPercentTotalMobile/(activeConnectionTotal + activeConnectionServer)));
		
		if(totalmobile3GNode!=0.0)
			getLogger().info("Media Connessioni attive MOBILE 3G NODE     (%): " + 100.0*(activeConnectionPercentTotalMobile3g/(activeConnectionTotal + activeConnectionServer)));
		
		getLogger().info("\n");
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime() + "\n");
		
		averageArrivalTime = totalArrivalTime / totalReceivedChunk;
		averagePcArrivalTime = totalPcArrivalTime / totalPcReceivedChunk;
		averageMobileWifiArrivalTime = totalMobileWifiArrivalTime / totalMobileWifiReceivedChunk;
		averageMobile3GArrivalTime = totalMobile3GArrivalTime / totalMobile3GReceivedChunk;
		

		getLogger().info("-------------------- NODES INFO ----------------------------------------");
		getLogger().info("Total Nodes             : " + (double)( totalPcNode + totalmobileNode + totalmobile3GNode+serverPeer.getDisconnectedNodes()));
		getLogger().info("Disconnected Nodes      : " + serverPeer.getDisconnectedNodes());
		getLogger().info("Total Active Node       : " + (double)( totalPcNode + totalmobileNode + totalmobile3GNode));
		getLogger().info("Total PcNode            : " + totalPcNode);
		getLogger().info("Total mobileWifiNode    : " + totalmobileNode);
		getLogger().info("Total mobile3GNode      : " + totalmobile3GNode);	
		getLogger().info("------------------------------------------------------------------------");
		
		
		getLogger().info("\n");
		getLogger().info("-------------------- CHUNKS ARRIVAL TIME INFO --------------------------");
		getLogger().info("Average Arrival Times                   : " + averageArrivalTime);	
		getLogger().info("");
		getLogger().info("PC-Node Average Arrival Times           : " + averagePcArrivalTime);
		getLogger().info("MobileWiFi-Node Average Arrival Times   : " + averageMobileWifiArrivalTime);
		getLogger().info("Mobile3G-Node Average Arrival Times     : " + averageMobile3GArrivalTime);
		getLogger().info("------------------------------------------------------------------------");
		
		getLogger().info("\n");		
		getLogger().info("-------------------- NODES DEPTH INFO ----------------------------------");
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
				
				chunkIndexAverage = (int) (totalSum / depthMatrix.get(j).size());
				
				getLogger().info("Nodes Depth: " + (j+1) + " Chunk Average: " + chunkIndexAverage);		
			}
		}	
		getLogger().info("------------------------------------------------------------------------");

		getLogger().info("\n");
		getLogger().info("-------------------- CHUNKS NUMBER / DEADLINE INFO ---------------------");
		getLogger().info("\n");
		getLogger().info("Total Chunk           : " + (totalDuplicateChunk+totalArrivedChunk));
		getLogger().info("Total Arrived Chunk   : " + totalArrivedChunk);
		getLogger().info("Average Missed Chunk  : " + (totalMissingChunk/(double)( totalPcNode + totalmobileNode + totalmobile3GNode + serverPeer.getDisconnectedNodes())) + " %");	
		
		getLogger().info("\n");
		getLogger().info("Total Duplicated Chunk                                     : " + totalDuplicateChunk);
		getLogger().info("Average Duplicated Chunk (Total Duplicate / Total Arrived) : " + (totalDuplicateChunk/(totalDuplicateChunk+totalArrivedChunk))*100.0 + " %");	
		getLogger().info("\n");
		
		getLogger().info("Chunk Skip Number: " + totalMissingChunk);
		getLogger().info("\n");
		
		getLogger().info("Perceived Continuity Index : " + ((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0 + "%");
		getLogger().info("\n");
		getLogger().info("------------------------------------------------------------------------");
		
		getLogger().info("\n");
		getLogger().info("########################################################################");
		
	}

}
