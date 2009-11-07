package it.unipr.ce.dsg.deus.example.hierarchical.streaming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Picone Marco
 * 
 */
public class LogStreamingPeerConnectionStatsEvent extends Event {

	private boolean fileCreated = false;
	
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
		
		float totalReceivedLayer = 0;
		float totalPcReceivedLayer = 0;
		float totalMobileWifiReceivedLayer = 0;
		float totalMobile3GReceivedLayer = 0;
		
		float totalArrivalTime = 0;
		float totalPcArrivalTime = 0;
		float totalMobileWifiArrivalTime = 0;
		float totalMobile3GArrivalTime = 0;
		
		float averageArrivalTime = 0;
		float averagePcArrivalTime = 0;
		float averageMobileWifiArrivalTime = 0;
		float averageMobile3GArrivalTime = 0;
		
		//Variabili per calcolare il numero medio di layer riprodotti dai vari tipi di nodo
		double TotalLayerPlayedPc = 0;
		double TotalChunkPlayedPc = 0;
		double TotalLayerPlayedMobile = 0;
		double TotalChunkPlayedMobile = 0;
		double TotalLayerPlayed3G = 0;
		double TotalChunkPlayed3G = 0;
		
		float AverageLayerPlayedPc = 0;
		float AverageLayerPlayedMobile = 0;
		float AverageLayerPlayed3G = 0;
		
		//Variabiali per calcolare la media dei chunk mancanti
		double totalMissingChunk = 0.0;
		double totalArrivedChunk = 0.0;
	
		//todo: aggiuti questi per statstiche
		double totalArrivedLayer = 0.0;
		
		//Varibiali per calcolare la media dei layer ricevuti doppi
		double totalDuplicateLayer = 0.0;
		
		//Variabili per calcolare le deadLine
		double totalDeadlineNumber = 0.0;
		
		long unstablesNode90 = 0;
		long unstablesNode80 = 0;
		
		double uploadSpeedAverage = 0.0;
		double uploadSpeedCapacity = 0.0;
		
		ServerPeer serverPeer = (ServerPeer) Engine.getDefault().getNodes().get(0);

		
		//Aggiungo alle statistiche relative ai chunk mancanti quelle memorizzate nel server dai nodi disconnessi
		
		//TODO: andare a vedere questo che nn mi ricordo se � stato messo a posto o no
		totalMissingChunk = totalMissingChunk + serverPeer.getMissingChunkNumber();
		totalArrivedChunk = totalArrivedChunk + serverPeer.getTotalChunkReceived();
		
		//TODO: Aggiunte per i layer
		totalArrivedLayer = totalArrivedLayer + serverPeer.getTotalLayerReceived();
		
		//Ricavo dal server i dati dei peer disconnessi relativi al numero medio di layer riprodotti
		TotalLayerPlayedPc = TotalLayerPlayedPc + serverPeer.getTotalLayerPlayedPc();
		TotalChunkPlayedPc = TotalChunkPlayedPc + serverPeer.getTotalChunkPlayedPc();
		TotalLayerPlayedMobile = TotalLayerPlayedMobile + serverPeer.getTotalLayerPlayedMobile();
		TotalChunkPlayedMobile = TotalChunkPlayedMobile + serverPeer.getTotalChunkPlayedMobile();
		TotalLayerPlayed3G = TotalLayerPlayed3G + serverPeer.getTotalLayerPlayed3G();
		TotalChunkPlayed3G = TotalChunkPlayed3G + serverPeer.getTotalChunkPlayed3G();
		
		totalDeadlineNumber = totalDeadlineNumber + serverPeer.getTotalDeadine();
		
		//Aggiungo le statistiche relative ai doppioni dei nodi che si sono disconnessi e che hanno salvato le statistiche nel server
		totalDuplicateLayer = totalDuplicateLayer + serverPeer.getDuplicateLayerNumber();
				
		activeConnectionServer = (double)serverPeer.getActiveConnection();
		
		//Trovo il massimo grado di nodo ELIMINATO GRADO DI NODO
	/*	int maxNodeDepth = 0;
		for(int k = 1; k < Engine.getDefault().getNodes().size(); k++ ){
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(k);
			
			maxNodeDepth = Math.max(maxNodeDepth, peer.getNodeDepth());
		}*/
			
		//Aggiungo i tempi di ricezione dei nodi PC disconnessi
		totalReceivedLayer = totalReceivedLayer + serverPeer.getArrivalTimesPcNode().size();
		totalReceivedLayer = totalReceivedLayer + serverPeer.getArrivalTimesMobileWifiNode().size();
		totalReceivedLayer = totalReceivedLayer + serverPeer.getArrivalTimesMobile3GNode().size();
		
		totalPcReceivedLayer = totalPcReceivedLayer + serverPeer.getArrivalTimesPcNode().size();
		
		for(int i = 0 ; i < serverPeer.getArrivalTimesPcNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesPcNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalPcArrivalTime = totalPcArrivalTime + localValue;
		}
		
		//Aggiungo i tempi di ricezione dei nodi Mobili Wifi disconnessi
		totalMobileWifiReceivedLayer = totalMobileWifiReceivedLayer + serverPeer.getArrivalTimesMobileWifiNode().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesMobileWifiNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesMobileWifiNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalMobileWifiArrivalTime = totalMobileWifiArrivalTime + localValue;
		}	

		//Aggiungo i tempi di ricezione dei nodi Mobili 3G disconnessi
		totalMobile3GReceivedLayer = totalMobile3GReceivedLayer + serverPeer.getArrivalTimesMobile3GNode().size();
		for(int i = 0 ; i < serverPeer.getArrivalTimesMobile3GNode().size(); i++)
		{
			float localValue = serverPeer.getArrivalTimesMobile3GNode().get(i);
			
			totalArrivalTime = totalArrivalTime + localValue;
			
			totalMobile3GArrivalTime = totalMobile3GArrivalTime + localValue;
		}	
		
		//Creo la matrice per le statistiche sulla profondita'
	/*	ArrayList<ArrayList<Integer>> depthMatrix = new ArrayList<ArrayList<Integer>>();
		
		for(int j = 0 ; j < maxNodeDepth; j++ )
			depthMatrix.add(new ArrayList<Integer>());*/
			
		//Controllo le connessioni attive dei diversi nodi
		for(int index = 1; index < Engine.getDefault().getNodes().size(); index++ ){
			
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(index);
			
			//Memorizzo la sua capacit� in Upload
			uploadSpeedCapacity += peer.getUploadSpeed();
			
			//Se il nodo � un fornitore
			if(peer.getServedPeers().size() > 0)
			{	
				//Disponibilit� in Upload media totale
				if(peer.getActiveConnection() > 0 )
					uploadSpeedAverage += (double)peer.getUploadSpeed() / (double)peer.getActiveConnection();
				else
					uploadSpeedAverage += (double)peer.getUploadSpeed();
			}
			
			//CALCOLO IL CONTINUITY INDEX DEI NODI CONNESSI 
			if(peer.getTotalChunkReceived() > 0 )
			{
				double nodeContinuityIndex = (((double)peer.getTotalChunkReceived()-(double)peer.getDeadlineNumber())/(double)peer.getTotalChunkReceived())*100.0;
				//Incremento il numero di nodi instabili
				if( nodeContinuityIndex <= 90.0 )
					unstablesNode90 ++;
				
				if( nodeContinuityIndex <= 80.0 )
					unstablesNode80 ++;
			}
			
			//CALCOLO LA MEDIA DEI CHUNK MANCANTI
			totalMissingChunk = totalMissingChunk + (double)peer.getMissingChunkNumber();
			totalArrivedChunk = totalArrivedChunk + (double)peer.getTotalChunkReceived();
			
			//CALCOLO IL NUMERO DEI DUPLICATI
			totalDuplicateLayer = totalDuplicateLayer + peer.getDuplicateLayerNumber();
			
			//CALCOLO LE DEADLINE TOTALI
			totalDeadlineNumber = totalDeadlineNumber + peer.getDeadlineNumber();
			
			
			//Dati per numero medio layer riprodotti
			
			if(peer.getId().equals("pcNode")){
			  TotalLayerPlayedPc = TotalLayerPlayedPc + peer.getTotalLayerPlayed();
			  TotalChunkPlayedPc = TotalChunkPlayedPc + peer.getTotalChunkPlayed();
			}
			
			if(peer.getId().equals("mobileNode")){
			  TotalLayerPlayedMobile = TotalLayerPlayedMobile + peer.getTotalLayerPlayed();
			  TotalChunkPlayedMobile = TotalChunkPlayedMobile + peer.getTotalChunkPlayed();
			}
			if(peer.getId().equals("mobile3GNode")){
			  TotalLayerPlayed3G = TotalLayerPlayed3G + peer.getTotalLayerPlayed();
			  TotalChunkPlayed3G = TotalChunkPlayed3G + peer.getTotalChunkPlayed();
			}
			
			
			
			//CALCOLO MEDIA SEGMENTI RICEVUTI IN BASE ALLA PROFONDITA' DEL NODO
			
			//Se il nodo e' connesso a qualcuno e ha almeno una risorsa 
		/*	if(peer.getNodeDepth() > 0 && peer.getVideoResource().size() > 0)
				depthMatrix.get(peer.getNodeDepth()-1).add(peer.getVideoResource().get(peer.getVideoResource().size()-1).getChunkIndex());*/
				
			//CALCOLO TEMPO MEDIO DI ARRIVO CHUNK
			
	//TODO: questa sezione � da mettere a posto!!!!		
			
			//Aggiungo il numero di chunk ricevuti dal singolo nodo
			totalReceivedLayer = totalReceivedLayer + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("pcNode"))
				totalPcReceivedLayer = totalPcReceivedLayer + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("mobileNode"))	
				totalMobileWifiReceivedLayer = totalMobileWifiReceivedLayer + peer.getArrivalTimes().size();
			
			if(peer.getId().equals("mobile3GNode"))
				totalMobile3GReceivedLayer = totalMobile3GReceivedLayer + peer.getArrivalTimes().size();
			
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
		
		averageArrivalTime = totalArrivalTime / totalReceivedLayer;
		averagePcArrivalTime = totalPcArrivalTime / totalPcReceivedLayer;
		averageMobileWifiArrivalTime = totalMobileWifiArrivalTime / totalMobileWifiReceivedLayer;
		averageMobile3GArrivalTime = totalMobile3GArrivalTime / totalMobile3GReceivedLayer;
		

		getLogger().info("-------------------- NODES INFO ----------------------------------------");
		getLogger().info("Total Nodes             : " + (double)( totalPcNode + totalmobileNode + totalmobile3GNode+serverPeer.getDisconnectedNodes()));
		getLogger().info("Disconnected Nodes      : " + serverPeer.getDisconnectedNodes());
		getLogger().info("Total Active Node       : " + (double)( totalPcNode + totalmobileNode + totalmobile3GNode));
		getLogger().info("Total PcNode            : " + totalPcNode);
		getLogger().info("Total mobileWifiNode    : " + totalmobileNode);
		getLogger().info("Total mobile3GNode      : " + totalmobile3GNode);	
		getLogger().info("------------------------------------------------------------------------");
		
		
		getLogger().info("\n");
		getLogger().info("-------------------- LAYERS ARRIVAL TIME INFO --------------------------");
		getLogger().info("Average Arrival Times                   : " + averageArrivalTime);	
		getLogger().info("");
		getLogger().info("PC-Node Average Arrival Times           : " + averagePcArrivalTime);
		getLogger().info("MobileWiFi-Node Average Arrival Times   : " + averageMobileWifiArrivalTime);
		getLogger().info("Mobile3G-Node Average Arrival Times     : " + averageMobile3GArrivalTime);
		getLogger().info("------------------------------------------------------------------------");
		
		getLogger().info("\n");		
/*		getLogger().info("-------------------- NODES DEPTH INFO ----------------------------------");
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
		getLogger().info("------------------------------------------------------------------------");*/
		
		getLogger().info("\n");
		getLogger().info("-------------------- CHUNKS/LAYERS NUMBER / DEADLINE INFO ---------------------");
		getLogger().info("\n");
		getLogger().info("Total Layer           : " + (totalDuplicateLayer+totalArrivedLayer));
		getLogger().info("Total Arrived Chunk   : " + totalArrivedChunk);
		getLogger().info("Total Arrived Layer   : " + totalArrivedLayer);
		getLogger().info("Average Missed Chunk  : " + (totalMissingChunk/(double)( totalArrivedChunk)) + " %");	
		
		getLogger().info("Average PcNode played layers: " + (TotalLayerPlayedPc)/(TotalChunkPlayedPc) );
		getLogger().info("Average mobileNode played layers: " + (TotalLayerPlayedMobile)/(TotalChunkPlayedMobile) );
		getLogger().info("Average 3GNode played layers: " + (TotalLayerPlayed3G)/(TotalChunkPlayed3G) );
		
		getLogger().info("\n");
		getLogger().info("Total Duplicated Layer                                     : " + totalDuplicateLayer);
		//TODO: da sistemare
		getLogger().info("Average Duplicated Layer (Total Duplicate / Total Arrived) : " + (totalDuplicateLayer/(totalDuplicateLayer+totalArrivedLayer))*100.0 + " %");	
		getLogger().info("\n");
		
		getLogger().info("Chunk Skip Number: " + totalMissingChunk);
		getLogger().info("\n");
		
		
		
		getLogger().info("Perceived Continuity Index : " + ((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0 + "%");
		getLogger().info("\n");
		
		getLogger().info("Unstables Node (Continuity Index < 90 %): " + unstablesNode90);
		getLogger().info("Unstables Node (Continuity Index < 80 %): " + unstablesNode80);
		getLogger().info("\n");
		
		getLogger().info("Upload Speed Average : " + (double)uploadSpeedAverage/(double)(Engine.getDefault().getNodes().size()) + " Mbit/sec");
	
		getLogger().info("Upload Speed Capacity : " + (double)uploadSpeedCapacity/(double)(Engine.getDefault().getNodes().size()) + " Mbit/sec");
		getLogger().info("\n");
		getLogger().info("------------------------------------------------------------------------");
	
		
		getLogger().info("\n");
		getLogger().info("########################################################################");
		
		
		this.writeGnuPlotFile((double)(Engine.getDefault().getVirtualTime()), ((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0,"continuityIndex");
		this.writeGnuPlotFile((double)(Engine.getDefault().getVirtualTime()),(double)unstablesNode90 ,"unstableNode");
		this.writeGnuPlotFile((double)(Engine.getDefault().getVirtualTime()),(double)uploadSpeedAverage/(double)(Engine.getDefault().getNodes().size()) ,"uploadSpeed_Average");
		
		//Creo l'array list con tutte le statistiche da stampare
		ArrayList<StatData> statList = new ArrayList<StatData>();
		statList.add(new StatData("VT",Engine.getDefault().getVirtualTime()));
		statList.add(new StatData("TotalActiveConnection",activeConnectionTotal));
		statList.add(new StatData("ServerActiveConnection",100.0*(activeConnectionServer)/(activeConnectionTotal + activeConnectionServer)));
		statList.add(new StatData("PCActiveConnection%",100.0*(activeConnectionPercentTotalPcNode/(activeConnectionTotal + activeConnectionServer))));
		statList.add(new StatData("WiFiActiveConnection%", 100.0*(activeConnectionPercentTotalMobile/(activeConnectionTotal + activeConnectionServer))));
		statList.add(new StatData("3GActiveConnection%",100.0*(activeConnectionPercentTotalMobile3g/(activeConnectionTotal + activeConnectionServer))));
		statList.add(new StatData("TotalNode",(double)( totalPcNode + totalmobileNode + totalmobile3GNode+serverPeer.getDisconnectedNodes())));
		statList.add(new StatData("TotalDisconnection",serverPeer.getDisconnectedNodes()));
		statList.add(new StatData("TotalPC",totalPcNode));
		statList.add(new StatData("TotalWiFi",totalmobileNode));
		statList.add(new StatData("Total3G",totalmobile3GNode));
		statList.add(new StatData("AveArrivalTime",averageArrivalTime));
		statList.add(new StatData("AvePCArrivalTime",averagePcArrivalTime));
		statList.add(new StatData("AveWiFiArrivalTime",averageMobileWifiArrivalTime));
		statList.add(new StatData("Ave3GArrivalTime",averageMobile3GArrivalTime));
		
		//TODO: da mettere a posto!
		statList.add(new StatData("Duplicated%",(totalDuplicateLayer/(totalDuplicateLayer+totalArrivedLayer))*100.0));
		statList.add(new StatData("ContinuityIndex%",((totalArrivedChunk-totalDeadlineNumber)/totalArrivedChunk)*100.0));
		statList.add(new StatData("UnstableNode",unstablesNode90));
		statList.add(new StatData("UploadSpeedAve",(double)uploadSpeedAverage/(double)(Engine.getDefault().getNodes().size())));
		statList.add(new StatData("UploadSpeedCapacity",(double)uploadSpeedCapacity/(double)(Engine.getDefault().getNodes().size())));
		
		this.writeStatFile(statList, "simulationStat");
		
		fileCreated = true;
	}
	
	public void writeStatFile(ArrayList<StatData> statList, String filenameSuffix)
	{
		
	 try {
	  
	   String filename = params.getProperty("filename") + "_" + filenameSuffix + ".dat";       
	  
	   File file = new File("log/"+filename);
	   
	   
	   if(fileCreated == false)
	   { 
		   
		   
		   if(file.exists())
		   {   
			   if(file.delete())
				   System.out.println("ELIMINO: " + file.getName() );
			   else
				   System.out.println("NON ELIMINO");
		   }
	   }
	
	   FileOutputStream fis = new FileOutputStream(file,true);
	   
	   for(int index = 0; index < statList.size(); index++)
	   {
		   String string = statList.get(index).getName() + " " + statList.get(index).getValue() + "\n";
		   fis.write(string.getBytes());
	   }
	      fis.flush();
	      fis.close();
	       
	     } catch (IOException e) {
	       System.out.println("Errore: " + e);
	       System.exit(1);
	     }
	
	}
	
	public void writeGnuPlotFile(double x, double y, String filenameSuffix)
	{	
		

	 try {
	  
		 String filename = params.getProperty("filename") + "_" + filenameSuffix + ".dat";        
	  
	   File file = new File("log/"+filename);
	   
	   if(fileCreated == false)
	   { 
		   
		   
		   if(file.exists())
		   {   
			   if(file.delete())
				   System.out.println("ELIMINO: " + file.getName() );
			   else
				   System.out.println("NON ELIMINO");
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
	

	public class StatData{

		private String name ;
		private double value;
		
		public StatData(String name, double value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
		
		
	}
	
}