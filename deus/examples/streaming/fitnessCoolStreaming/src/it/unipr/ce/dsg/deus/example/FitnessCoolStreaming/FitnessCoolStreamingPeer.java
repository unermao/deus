package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;


import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;

/**
 * <p>
 * EnergyPeers are characterized by one kind of consumable resource: energy.
 * Moreover, each EnergyPeer has a chromosome, i.e.
 * a set of parameters whose values are randomly initialized when the
 * RevolPeer is instantiated, and may change during its lifetime, depending
 * on external events. The EnergyPeer keeps track of the number of sent queries (Q)
 * and of the number of query hits (QH). The query hit ratio (QHR = QH/Q) is 
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */


public class FitnessCoolStreamingPeer extends Peer {

	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String VIDEO_RESOURCE_BUFFER_LIMIT = "videoResourceBufferLimit";
	private static final String K_VALUE = "k_value"; 
	private static final String MAX_PARTNERS_NUMBER = "maxPartnersNumber";
	private static final String INCENTIVE_BASED = "incentiveBased";
	private static final String FITNESS_BASED = "fitnessBased";
	
	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";
	public static final String G2 = "2g"; 
	public static final String GPRS = "gprs";

	private int overhead = 0;
	private double battery = 0;
	private String connectionType = "";
	private boolean incentiveBased = false;
	private boolean fitnessBased = false;
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int videoResourceBufferLimit = 10;
	private int k_value = 0;
	private int maxPartnersNumber = 0;
	private int downloadActiveConnection = 0;
	private int nodeDepth = 0;
	private FitnessCoolStreamingPeer gossipNode = null;
	private int missingChunkNumber = 0;
	private int totalChunkReceived = 0;
	private int exchangeInternalISP = 0;
	private int exchangeExternalISP = 0;
	private int indexOfLastPlayedChunk;
	private int duplicateChunkNumber = 0;
	private int deadlineNumber = 0;
	private float startUpTime = 0;
	private float connectionTime = 0;
	private int isp = 0;
	private int city = 0;
	
	private double fitnessValue = 0.0;

	private long time1; 
	private long time2;
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private double onlineTime = 0.0;
	
	private FitnessCoolStreamingPeer sourceStreamingNode = null;
	private FitnessCoolStreamingServerPeer serverNode = null;
	private int continuityTrialCount = 0;
	
	//Lista contenente i peer che mi forniscono i chunk	
	private ArrayList<FitnessCoolStreamingPeer> serverByPeer = new ArrayList<FitnessCoolStreamingPeer>();
	private ArrayList<FitnessCoolStreamingServerPeer> serverByServer = new ArrayList<FitnessCoolStreamingServerPeer>();
	
	//Lista contenente i peer a cui fornisco chunk
	//private ArrayList<FitnessCoolStreamingPeer> servedPeers = new ArrayList<FitnessCoolStreamingPeer>();
	private ArrayList<ArrayList<FitnessCoolStreamingPeer>> servedPeers = new ArrayList<ArrayList<FitnessCoolStreamingPeer>>();
		
	//Lista contenente le richieste di chunk
	private ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> sendChunkBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
	
	//LIsta contenente i chunk da richiedere
	private ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> requestChunkBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
	
	//K-Buffer
	private ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> k_buffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
	private ArrayList<Integer> lastIndexOfChunk = new ArrayList<Integer>();
	
	//Buffer di riproduzione
	private ArrayList<FitnessCoolStreamingVideoChunk> player = new ArrayList<FitnessCoolStreamingVideoChunk>();
	
	private ArrayList<FitnessCoolStreamingVideoChunk> videoResource = new ArrayList<FitnessCoolStreamingVideoChunk>();
	private ArrayList<FitnessCoolStreamingVideoChunk> videoPlayBuffer = new ArrayList<FitnessCoolStreamingVideoChunk>();
	private ArrayList<Float> arrivalTimes;
	private ArrayList<FitnessCoolStreamingVideoChunk> requestedChunk;
	private boolean init_bool = false;
	private int initChunk;
	private boolean first = true;
	private int stop = 0;
	private ArrayList<Boolean> cambio = new ArrayList<Boolean>();
	private int firstChunk;
	
	private static ArrayList<FitnessCoolStreamingPeer> updated = new ArrayList<FitnessCoolStreamingPeer>();
	
	public FitnessCoolStreamingPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();		
		
		
	}

public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(BATTERY))
			battery = Double.parseDouble(params.getProperty(BATTERY));
		
		if (params.containsKey(CONNECTION_TYPE))
			connectionType = new String(params.getProperty(CONNECTION_TYPE));
		
		if (params.containsKey(UPLOAD_SPEED))
			uploadSpeed = Double.parseDouble(params.getProperty(UPLOAD_SPEED));
		
		if (params.containsKey(DOWNLOAD_SPEED))
			downloadSpeed = Double.parseDouble(params.getProperty(DOWNLOAD_SPEED));	
		
		if (params.containsKey(VIDEO_RESOURCE_BUFFER_LIMIT))
			videoResourceBufferLimit = Integer.parseInt(params.getProperty(VIDEO_RESOURCE_BUFFER_LIMIT));
		
		if (params.containsKey(K_VALUE))
			k_value = Integer.parseInt(params.getProperty(K_VALUE));
		
		if (params.containsKey(MAX_PARTNERS_NUMBER))
			maxPartnersNumber = (int) Double.parseDouble(params.getProperty(MAX_PARTNERS_NUMBER));
		
		if (params.containsKey(INCENTIVE_BASED))
			incentiveBased = Boolean.parseBoolean(params.getProperty(INCENTIVE_BASED));
		
		if (params.containsKey(FITNESS_BASED))
			fitnessBased = Boolean.parseBoolean(params.getProperty(FITNESS_BASED));
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
		
		time1 = System.currentTimeMillis();
		
		//Aggiorno il valore di fitness del nodo
		this.updateFitnessValue();
		
	}

	
	public Object clone() {
	
		FitnessCoolStreamingPeer clone = (FitnessCoolStreamingPeer) super.clone();

		clone.activeConnection = this.activeConnection;
		clone.battery = this.battery;
		clone.isp = 0;
		clone.connectionType = this.connectionType;
		clone.k_value = this.k_value;	
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.maxPartnersNumber = this.maxPartnersNumber;
		clone.onlineTime = 0;
		clone.time1 = 0;
		clone.time2 = 0;
		clone.incentiveBased = this.incentiveBased;
		clone.fitnessBased = this.fitnessBased;
		clone.initChunk = this.initChunk;
		clone.sendChunkBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		clone.requestChunkBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		clone.serverNode = this.serverNode;
		clone.sourceStreamingNode = this.sourceStreamingNode;
		clone.videoResource = new ArrayList<FitnessCoolStreamingVideoChunk>();
		clone.player = new ArrayList<FitnessCoolStreamingVideoChunk>();
		clone.k_buffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		clone.servedPeers = new ArrayList<ArrayList<FitnessCoolStreamingPeer>>();
		clone.servedPeers = new ArrayList<ArrayList<FitnessCoolStreamingPeer>>();
		clone.serverByPeer = new ArrayList<FitnessCoolStreamingPeer>();
		clone.serverByServer = new ArrayList<FitnessCoolStreamingServerPeer>();
		clone.arrivalTimes = new ArrayList<Float>(); 
		clone.videoResourceBufferLimit  = this.videoResourceBufferLimit;
		clone.nodeDepth = 0;
		clone.missingChunkNumber = 0;
		clone.videoPlayBuffer = new ArrayList<FitnessCoolStreamingVideoChunk>();
		clone.totalChunkReceived = 0;
		clone.duplicateChunkNumber = 0;
		clone.lastIndexOfChunk = new ArrayList<Integer>();
		clone.requestedChunk = new ArrayList<FitnessCoolStreamingVideoChunk>();
		clone.deadlineNumber = 0;
		clone.isConnected = true;
		clone.init_bool = this.init_bool;
		clone.first = true;
		clone.cambio = new ArrayList<Boolean>();
		clone.stop = 0;
		clone.startUpTime = 0;	
		clone.overhead = 0;
		clone.fitnessValue = 0.0;
		
		
		return clone;
	}
	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	public void updateFitnessValue(){
		
		//Campiono il nuovo tempo per determinare il tempo di permanenza OnLine	
		time2 = System.currentTimeMillis();
		
		this.onlineTime = this.time2 - this.time1;
		
		double newFitness = 0.0;
		
		//Batteria[0,100]% , UploadSpeed [0,1000]Kbit/sec , Online Time[0,24]h, Connessioni Attive[0,100]% 
		
		//Determino la velocitˆ di upload in base alle connessioni attive 
		double speedValue = 0.0;
		
		if(this.activeConnection > 0)
			speedValue = this.uploadSpeed / (double)this.activeConnection;
		else
			speedValue = this.uploadSpeed;
		
		//double depthValue = 1.0/((double)this.nodeDepth +1.0);
		
		newFitness = ( 1.5 * (double)this.battery + 0.4*(double)(speedValue) + 2.083*this.onlineTime) / (10.0 + (double)(this.activeConnection/this.maxAcceptedConnection)); 
		
		//System.out.println("New Fitness("+ this.getId() + ") : "  + newFitness);
		
		this.fitnessValue = newFitness;
	}
	
	/**
	 * Inizializza gli ArrayList
	 */
	public void init()
	{
		for(int i=0;i<this.getK_value();i++)
		{
			this.getSendBuffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
			this.getRequestChunkBuffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
			this.getK_buffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());	
			this.getServedPeers2().add(i,new ArrayList<FitnessCoolStreamingPeer>());
			this.getServerByPeer().add(i,null);
			this.getServerByServer().add(i,null);
			this.getLastIndexOfChunk().add(i,0);			
			this.cambio.add(false);
		}
		
		this.init_bool = true;
	}
	
	
	public void removeActiveConnection(){
		
		if( this.activeConnection >= 1 )
			this.activeConnection--;
		else
			System.out.println("ERRORE PEER ! Connessioni Attive = 0 non posso decrementare");
	}
	
	public void addActiveConnection(){
		
		if( this.activeConnection < this.maxAcceptedConnection )
		 this.activeConnection++;
		else
			System.out.println("ERRORE PEER ! Connessioni Attive = "+ this.maxAcceptedConnection  +" non posso incrementare");
	}
	
	
	/**
	 * Invia al nodo client di destinazione, la porzione video newResource partendo dal tempo 
	 * triggerTime
	 * 
	 * @param clientNode
	 * @param newResource
	 * @param triggeringTime
	 */
	public void sendVideoChunk(FitnessCoolStreamingPeer clientNode,FitnessCoolStreamingVideoChunk newResource, float triggeringTime){
		
		//Verifico se devo degradare la velocitË† di download del nodo client in base alle
		//sue connessioni in ingresso attive
		double clientDownloadSpeed = 0.0;
		if( clientNode.getDownloadActiveConnection() > 0 )
			clientDownloadSpeed = clientNode.getDownloadSpeed() / (double)clientNode.getDownloadActiveConnection();
		else
			clientDownloadSpeed = clientNode.getDownloadSpeed();
		
		float appTime = nextChunkArrivalTime(this.getUploadSpeed(),clientDownloadSpeed,newResource,clientNode);		
		int index = this.calculate_buffer_index(newResource);
		int pos = newResource.getChunkIndex()%this.k_value + newResource.getChunkIndex()/this.k_value - index;				
		
		float time = triggeringTime + appTime;
		
		FitnessCoolStreamingPeerNewVideoResourceEvent newPeerResEvent = (FitnessCoolStreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(FitnessCoolStreamingPeerNewVideoResourceEvent.class,time);
		newPeerResEvent.setOneShot(true);
		newPeerResEvent.setAssociatedNode(clientNode);
		newPeerResEvent.setResourceValue(newResource);
		Engine.getDefault().insertIntoEventsList(newPeerResEvent);
		
	}
	
	/**
	 * Determina  il tempo in cui dovra' essere schedulato il nuovo arrivo di un chunk al destinatario
	 * in base alla velocita' di Upload del fornitore e quella di Downalod del cliente.
	 * @param providerUploadSpeed
	 * @param clientDownloadSpeed
	 * @param clientNode 
	 * @return
	 */
	private float nextChunkArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, FitnessCoolStreamingVideoChunk chunk, FitnessCoolStreamingPeer clientNode) {
		
		double time = 0.0;
		double minSpeed = Math.min(  (providerUploadSpeed  / (double) this.getActiveConnection()) , clientDownloadSpeed);
		double chunkMbitSize = (double)( (double) chunk.getChunkSize() / 1024.0 );
		time = (chunkMbitSize / minSpeed);
		
		float floatTime = (float)( time + Engine.getDefault().getSimulationRandom().nextDouble()*time);
		
		float sec=0;
		
		/*
		if(((FitnessCoolStreamingPeer)chunk.getSourceNode()).getIsp() == clientNode.getIsp()
				&& ((FitnessCoolStreamingPeer)chunk.getSourceNode()).getCity() == clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			this.exchangeInternalISP++;
			sec = Engine.getDefault().getSimulationRandom().nextInt(2);
		}
		
		if(((FitnessCoolStreamingPeer)chunk.getSourceNode()).getIsp() == clientNode.getIsp()
				&& ((FitnessCoolStreamingPeer)chunk.getSourceNode()).getCity() != clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			this.exchangeInternalISP++;
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 5;
		}
		
		if(((FitnessCoolStreamingPeer)chunk.getSourceNode()).getIsp() != clientNode.getIsp()
				&& ((FitnessCoolStreamingPeer)chunk.getSourceNode()).getCity() == clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			this.exchangeExternalISP++;
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 1;
		}
		
		if(((FitnessCoolStreamingPeer)chunk.getSourceNode()).getIsp() != clientNode.getIsp()
				&& ((FitnessCoolStreamingPeer)chunk.getSourceNode()).getCity() != clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			this.exchangeExternalISP++;
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 6;
		}
		*/
		
		return (float) (floatTime*(20)+sec);
			
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}
	
	/**
	 * Funzione che controlla se un nodo e' ancora attivo nel sistema
	 * 
	 * @param peer
	 * @return
	 */
	public boolean checkDisconnection( FitnessCoolStreamingPeer peer ){
		
		if(peer == null)
			return true;
		
		if(peer.isConnected() == false)
			return true;
		
		return false;
		
	}
	
	public ArrayList<FitnessCoolStreamingVideoChunk> sort(ArrayList<FitnessCoolStreamingVideoChunk> arrayList) {		 		
		
		 ArrayList<FitnessCoolStreamingVideoChunk> appList = new ArrayList<FitnessCoolStreamingVideoChunk>();
		 
		 //Ordinamento dei vicini in base al loro valore di fitness
		 for(int i = 0 ; i < arrayList.size() ; i++)
		 {
			 FitnessCoolStreamingVideoChunk chunkOriginal = (FitnessCoolStreamingVideoChunk)arrayList.get(i);
		  
			 if(appList.size() == 0)
				 appList.add(chunkOriginal);
			 else
			 {
				 for(int j = 0 ; j < appList.size(); j++)
				 {
		    
					 FitnessCoolStreamingVideoChunk chunkApp = (FitnessCoolStreamingVideoChunk)appList.get(j);
		    
					 
					 if(chunkOriginal.getChunkIndex() <= chunkApp.getChunkIndex())
					 {	 
						 appList.add(j,chunkOriginal);
						 break;
					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
					 else if( j == appList.size() - 1)
					 {
						 appList.add(chunkOriginal);
						 break;
					 }
				 }
			 }
		 }
		 
		 arrayList.clear();
		 return appList;
		 
	}
	
	
	public int calculate_buffer_index(FitnessCoolStreamingVideoChunk chunk)
	{	 	
	 return (chunk.getChunkIndex()%this.getK_value());	
	}
	
	public void updateNodeBattery()
	{
		
		//Aggiornamento Livello di batteria per i nodi mobili con Wifi
		if(this.id.equals("pcNodeHigh") || this.id.equals("pcNode"))
			this.battery = this.battery - 0.20 - ( this.activeConnection * 0.005 );
		
		//Se la batteria e' scesa sotto una certa soglia
		if(this.battery < 1.0)
		{	
			System.out.println("DISCONNETTO !!!");
			this.disconnectionCoolStreaming(Engine.getDefault().getVirtualTime());
		}
	}
	
	public void setUploadSpeed(double uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}

	public void setActiveConnection(int activeConnection) {
		this.activeConnection = activeConnection;
	}

	public static String getBATTERY() {
		return BATTERY;
	}

	public static String getCONNECTION_TYPE() {
		return CONNECTION_TYPE;
	}

	public static String getUPLOAD_SPEED() {
		return UPLOAD_SPEED;
	}

	public static String getMAX_ACCEPTED_CONNECTION() {
		return MAX_ACCEPTED_CONNECTION;
	}

	public static String getADSL() {
		return ADSL;
	}

	public double getBattery() {
		return battery;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public double getUploadSpeed() {
		return uploadSpeed;
	}

	public long getTime1() {
		return time1;
	}

	public long getTime2() {
		return time2;
	}

	public int getMaxAcceptedConnection() {
		return maxAcceptedConnection;
	}

	public int getActiveConnection() {
		return activeConnection;
	}

	public ArrayList<FitnessCoolStreamingVideoChunk> getVideoResource() {
		return videoResource;
	}

	public FitnessCoolStreamingPeer getSourceStreamingNode() {
		return sourceStreamingNode;
	}

	public void setSourceStreamingNode(FitnessCoolStreamingPeer sourceStreamingNode) {
		this.sourceStreamingNode = sourceStreamingNode;
	}

	public FitnessCoolStreamingServerPeer getServerNode() {
		return serverNode;
	}

	public void setServerNode(FitnessCoolStreamingServerPeer serverNode) {
		this.serverNode = serverNode;
	}
	
	public int getMaxPartnersNumber() {
		return maxPartnersNumber;
	}

	public double getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(double downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public ArrayList<Float> getArrivalTimes() {
		return arrivalTimes;
	}

	public void setArrivalTimes(ArrayList<Float> arrivalTimes) {
		this.arrivalTimes = arrivalTimes;
	}

	public int getNodeDepth() {
		return nodeDepth;
	}

	public void setNodeDepth(int nodeDepth) {
		this.nodeDepth = nodeDepth;
	}

	public int getDownloadActiveConnection() {
		return downloadActiveConnection;
	}

	public void setDownloadActiveConnection(int downloadActiveConnection) {
		this.downloadActiveConnection = downloadActiveConnection;
	}

	public ArrayList<FitnessCoolStreamingVideoChunk> getVideoPlayBuffer() {
		return videoPlayBuffer;
	}

	public void setVideoPlayBuffer(ArrayList<FitnessCoolStreamingVideoChunk> videoPlayBuffer) {
		this.videoPlayBuffer = videoPlayBuffer;
	}

	public int getMissingChunkNumber() {
		return missingChunkNumber;
	}

	public void setMissingChunkNumber(int missingChunkNumber) {
		this.missingChunkNumber = missingChunkNumber;
	}

	public int getTotalChunkReceived() {
		return totalChunkReceived;
	}

	public void setTotalChunkReceived(int totalChunkReceived) {
		this.totalChunkReceived = totalChunkReceived;
	}

	public int getIndexOfLastReceivedChunk() {
		return indexOfLastPlayedChunk;
	}

	public void setIndexOfLastReceivedChunk(int indexOfLastReceivedChunk) {
		this.indexOfLastPlayedChunk = indexOfLastReceivedChunk;
	}

	public int getDuplicateChunkNumber() {
		return duplicateChunkNumber;
	}

	public void setDuplicateChunkNumber(int duplicateChunkNumber) {
		this.duplicateChunkNumber = duplicateChunkNumber;
	}

	public ArrayList<FitnessCoolStreamingVideoChunk> getNeededChunk() {
		return requestedChunk;
	}

	public void setNeededChunk(ArrayList<FitnessCoolStreamingVideoChunk> neededChunk) {
		this.requestedChunk = neededChunk;
	}

	public int getDeadlineNumber() {
		return deadlineNumber;
	}

	public void setDeadlineNumber(int deadlineNumber) {
		this.deadlineNumber = deadlineNumber;
	}

	public int getK_value() {
		return k_value;
	}

	public void setK_value(int k_value) {
		this.k_value = k_value;
	}

	public ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> getK_buffer() {
		return k_buffer;
	}

	public void setK_buffer(ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> k_buffer) {
		this.k_buffer = k_buffer;
	}

	public void printK_Buffer()
	{
		System.out.println("NODO : " +this.getKey());
		String el = "";
		for(int i=0; i<this.k_value; i++)
			//if(this.k_buffer.get(i).size()>0)
		 for(int j=0; j<this.k_buffer.get(i).size(); j++)
		 { el = el + " " + this.k_buffer.get(i).get(j).getChunkIndex(); 
			 if(j==this.k_buffer.get(i).size()-1)
				 { System.out.println("buffer " + i + " elementi " + el); el = ""; }
			 
		 }
	}
	
	public void printFornitori()
	{
		System.out.println("NODO : " +this.getKey());
		String el = " Fornitori ";
		for(int i=0; i<this.k_value; i++)
		el = el + " " + this.getServerByPeer().get(i);
		
	for(int i=0; i<this.k_value; i++)
		el = el + " " +  this.getServerByServer().get(i);
		 		 System.out.println( el); 
		 		 el = ""; 
			 
		 
	}
	
	public void printVideoBuffer(ArrayList<FitnessCoolStreamingVideoChunk> arrayList)
	{
		String my ="";
		for(int j = 0 ; j < arrayList.size(); j++)
			my = my + " " + arrayList.get(j).getChunkIndex();
				
		System.out.println("Id: "+ this.getKey() + " " + my);
	}

	

	
	public ArrayList<FitnessCoolStreamingVideoChunk> getPlayer() {
		return player;
	}

	public void setPlayer(ArrayList<FitnessCoolStreamingVideoChunk> player) {
		this.player = player;
	}

		
	public void findFirstProvidersNode(float triggeringTime){			
		
		if(this.connectionTime == 0)
			this.connectionTime = Engine.getDefault().getVirtualTime();
		
		boolean find_provider = false;
		
		ArrayList<FitnessCoolStreamingPeer> testedPeer = new ArrayList<FitnessCoolStreamingPeer>();
			
		if(init_bool == false)
		this.init();		
		
		int chunkMiddle = this.calculateMiddleChunk();
				
		//Scorro i k fornitori
		for(int i = 0 ; i < this.k_value ; i++ )							
			if( this.getServerByPeer().get(i) == null && this.getServerByServer().get(i) == null )
			{		
				int index = 0;
				int index2 = 0;		
				find_provider = false;						
		
				int chunkToFind = chunkMiddle - (chunkMiddle%this.getK_value() - i);
				
				if(this.isIncentiveBased())
					this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(chunkToFind));
				else
					this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(chunkToFind));

				
				if(this.getNeighbors().size() > 0)
				{			
				 do{
									
					index = Engine.getDefault().getSimulationRandom().nextInt(this.getNeighbors().size());														
					
					FitnessCoolStreamingPeer SourceStreamingNode = (FitnessCoolStreamingPeer)this.getNeighbors().get(index);
					
					//TODO ISP Scelta dei primi fornitori					
					if(this.isIncentiveBased())
					{
						SourceStreamingNode = (FitnessCoolStreamingPeer)this.getNeighbors().get(index2);
						index2 ++;
					}															
					
					if(!testedPeer.contains(SourceStreamingNode) && SourceStreamingNode.isConnected()==true)
						{
						//System.out.println("Ciao2");
  						  testedPeer.add(SourceStreamingNode);
						 
  						  //System.out.println(SourceStreamingNode.init_bool);
  							if(SourceStreamingNode.init_bool == false)
  	  								SourceStreamingNode.init();
  							  						 
  						  if( 	SourceStreamingNode.getK_buffer().get(i).contains(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))&&
								 SourceStreamingNode.getK_buffer().get(i).size() > 0 
								&& (SourceStreamingNode.getMaxAcceptedConnection() - SourceStreamingNode.getActiveConnection())>0 
								//|| ((FitnessCoolStreamingServerPeer) Engine.getDefault().getNodes().get(0)).getActiveConnection() == ((FitnessCoolStreamingServerPeer) Engine.getDefault().getNodes().get(0)).getMaxAcceptedConnection() 
								)
						   {						
  							
  							//  System.out.println("DASDA");
						    //Imposto il mio fornitore
							this.serverByPeer.set(i, SourceStreamingNode);//.get(i).setProviderPeer(SourceStreamingNode, i);
							
							//Incremento il mio ordine di nodo
							//this.updateNodeDepthCoolStreaming();
							updated.clear();
							
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//System.out.println("FIND_FIRST: " + (SourceStreamingNode.getMaxAcceptedConnection() - SourceStreamingNode.getActiveConnection()));
							//Imposto la connessione attiva con il nodo fornitore trovato
							SourceStreamingNode.addActiveConnection();												
							
							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
							SourceStreamingNode.getServedPeers2().get(i).add(this);
																				
							//Chiamiamo la funzione per avere segmenti mancanti			
							this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(SourceStreamingNode,triggeringTime,chunkToFind,true);
							
							find_provider = true;
						    
							break;
						   }						
						}					
					
				} while(testedPeer.size()<this.getNeighbors().size());
				}	
				
				if(find_provider == false)
				{
					FitnessCoolStreamingServerPeer Server_peer = (FitnessCoolStreamingServerPeer) Engine.getDefault().getNodes().get(0);
										
					if(Server_peer.isInit_bool() == false)
						Server_peer.init();
					
					//Imposto il mio fornitore
					this.getServerByServer().set(i, Server_peer);
					
					//Incremento il mio ordine di nodo
					this.updateNodeDepthCoolStreaming();
					updated.clear();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il server centrale
					Server_peer.addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					Server_peer.getServedPeers2().get(i).add(this);
									
					//this.getBufferNeighborFitnessCoolStreaming(Server_peer,triggeringTime,i);
					this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(Server_peer,triggeringTime,chunkToFind,true);
				}
				
			
			testedPeer.clear();	
		}
	
		this.requestedChunk.addAll(this.sort(this.requestedChunk));
		
		if(this.requestedChunk.size()>0)
		this.initChunk = this.requestedChunk.get(0).getChunkIndex();
		
		ArrayList<FitnessCoolStreamingVideoChunk> app = new ArrayList<FitnessCoolStreamingVideoChunk>();
		app.addAll(this.requestedChunk);
		
			
		this.requestChunk();
		
	}

	
	
public void addNewVideoResourceCoolStreaming(FitnessCoolStreamingVideoChunk newVideoRes, float triggeringTime){		
	
		//Salvo il tempo in cui e' arrivato il chunk
		float arrivalValue = triggeringTime - newVideoRes.getOriginalTime(); 
		this.arrivalTimes.add(arrivalValue);					
		
		if(this.firstChunk != 0)
			firstChunk = newVideoRes.getChunkIndex();
		
		int index = this.calculate_buffer_index(newVideoRes);		
		
		//Controllo se si e' verificata un eventuale deadline
		if( newVideoRes.getChunkIndex() <= this.indexOfLastPlayedChunk )
			this.missingChunkNumber--;			
		
		else
		if(!this.k_buffer.get(index).contains(newVideoRes))
		{
			if(this.getLastIndexOfChunk().get(index) < newVideoRes.getChunkIndex())
				this.getLastIndexOfChunk().set(index, newVideoRes.getChunkIndex());
			//Incremento il numero totale di chunk ricevuti
			this.totalChunkReceived++;
		
			//Inserisco il chunk nel k_buffer appropriato e lo ordino
			this.k_buffer.get(index).add(newVideoRes);							
			this.k_buffer.get(index).addAll(this.sort(this.getK_buffer().get(index)));
		
		}
		else{
			this.duplicateChunkNumber ++; //Incremento il numero di duplicati		
		}
	}


/**
 * Aggiorna la lista dei vicini
 * @param triggeringTime
 */
public void updateParentsListCoolStreaming(float triggeringTime){
		
	if(!this.isInit_bool())
		this.init();
	
	boolean served = false;	
	
	//Attraverso la lista dei nodi
	for(int i = 0; i < this.getNeighbors().size(); i++)
		if(checkDisconnection((FitnessCoolStreamingPeer) this.getNeighbors().get(i)))
			this.getNeighbors().remove(i);
	

	//Se qualche nodo e' stato rimosso dalla lista dei vicini
	if( this.getNeighbors().size() < this.getMaxPartnersNumber() ){
		
		//Cerco tra i miei vicini qualche nuovo contatto
		for(int index = 0; index < this.neighbors.size() ; index++)
		{

			FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)this.neighbors.get(index);
			
			for(int j = 0; j < peer.getNeighbors().size(); j++ ){
			
				FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)peer.neighbors.get(j);
					
				if(
						peerApp.isConnected() //Se il peer e' connesso
						&& !this.neighbors.contains(peerApp) //Se non e' gia' tra i miei vicni
						&& !peerApp.equals(this) //Se non sono io	
				  )
				{
				  this.addNeighbor(peerApp);
									
				} 
				
				//Se ho raggiunto il limite massimo esco
				if(this.neighbors.size() == this.maxPartnersNumber)
					break;
			
			}
			
			//Se ho raggiunto il limite massimo esco
			if(this.neighbors.size() == this.maxPartnersNumber)
				break;
			
		}
		
	}				
	
	int endListNumber = this.neighbors.size();
	
	if( endListNumber > this.maxPartnersNumber )
		System.out.println("ERRORE LISTA TROPPO GRANDE !!!! ("+ endListNumber + "/" + this.maxPartnersNumber + ")");
		
}



/**
 * Aggiorna la lista dei vicini
 * @param triggeringTime
 */
public void Protocol2(float triggeringTime,ArrayList<Peer> nodes){
			
	if(!this.isInit_bool())
		this.init();	
		
	//Attraverso la lista dei nodi
	for(int i = 0; i < this.getNeighbors().size(); i++)
		if(checkDisconnection((FitnessCoolStreamingPeer) this.getNeighbors().get(i)))
			this.getNeighbors().remove(i);
	
	
	for( int i = 0; i < nodes.size(); i++ )
	{
		FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)nodes.get(i);
		
		//Scorro i vicini 
		for(int k = 0; k < peer.getNeighbors().size(); k++ )
		{		 		
			for(int j = 0; j < this.getNeighbors().size(); j++ )
			{
				if(this.getNeighbors().size() < this.getMaxPartnersNumber())
					this.getNeighbors().add(peer.getNeighbors().get(k));
				else
				{
				//Se non è un mio fornitore o non è già presente nella mia lista
			 	if(!this.getServerByPeer().contains((FitnessCoolStreamingPeer)this.getNeighbors().get(j)) && !this.getServerByPeer().contains((FitnessCoolStreamingPeer)peer.getNeighbors().get(k)))
			 	{
			 		//Se è migliore
			 		if(((FitnessCoolStreamingPeer)peer.getNeighbors().get(k)).getPlayer().contains(((FitnessCoolStreamingPeer)this.getNeighbors().get(j)).getIndexOfLastReceivedChunk()) 
			 			&& ((FitnessCoolStreamingPeer)peer.getNeighbors().get(k)).getUploadSpeed()/(((FitnessCoolStreamingPeer)peer.getNeighbors().get(k)).getActiveConnection()+1) >	((FitnessCoolStreamingPeer)this.getNeighbors().get(j)).getUploadSpeed()/((FitnessCoolStreamingPeer)this.getNeighbors().get(j)).getActiveConnection())
			 		{
			 		//Rimuovo il vecchio e aggiungo il nuovo			 		
			 		this.getNeighbors().remove(this.getNeighbors().get(j));
			 		this.getNeighbors().add(peer.getNeighbors().get(k));
			 		}
			 	}
				}
			}
		}
		
		for(int k = 0; k < this.getNeighbors().size(); k++ )
		{		 		
			for(int j = 0; j < peer.getNeighbors().size(); j++ )
			{
				if(peer.getNeighbors().size() < peer.getMaxPartnersNumber())
					peer.getNeighbors().add(this.getNeighbors().get(k));
				else
				{
				//Se non è un mio fornitore
			 	if(!peer.getServerByPeer().contains((FitnessCoolStreamingPeer)peer.getNeighbors().get(j)) && !peer.getServerByPeer().contains((FitnessCoolStreamingPeer)this.getNeighbors().get(k)))
			 	{
			 		//Se è migliore
			 		if(((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getPlayer().contains(((FitnessCoolStreamingPeer)peer.getNeighbors().get(j)).getIndexOfLastReceivedChunk()) 
			 			&& ((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getUploadSpeed()/(((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getActiveConnection()+1) >	((FitnessCoolStreamingPeer)peer.getNeighbors().get(j)).getUploadSpeed()/((FitnessCoolStreamingPeer)peer.getNeighbors().get(j)).getActiveConnection())
			 		{
			 		//Rimuovo il vecchio e aggiungo il nuovo
			 		peer.getNeighbors().remove(peer.getNeighbors().get(j));
			 		peer.getNeighbors().add(this.getNeighbors().get(k));
			 		}
			 	}
			   }
		   }
		}
		
	}
			
}


/**
 * Aggiorna la lista dei vicini
 * @param triggeringTime
 */
public void gossipProtocol(FitnessCoolStreamingPeer node, int value){				
	
	int good = 0;
	
//	System.out.println("this" + this);
//	System.out.println("node" + node);
	
	if(!this.equals(node))
	{
		
		if(value == -1)
			this.getNeighbors().remove(node);					
		
		else if (node!=null)
		{					
		if(this.getNeighbors().size() < this.getMaxPartnersNumber())
			{
				this.getNeighbors().add(node);
			}

		else
		{
		if(this.getNeighbors().size()>0)	
		{
		if(this.isIncentiveBased())
			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(this.getIndexOfLastReceivedChunk()));
//		if(!this.isIncentiveBased())
//			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(this.getIndexOfLastReceivedChunk()));
		
		for(int k = this.getNeighbors().size() - 1; k >= 0; k-- )
		{			

			if(this.isIncentiveBased()){
			//if(node.getPlayer().size()>0)
				if(this.getNeighbors().contains(node))
					good = 1;
				
				if( //this.calculateGeographicDistance(this, node) <= this.calculateGeographicDistance(this,(FitnessCoolStreamingPeer)this.getNeighbors().get(k)) && 
						node.getUploadSpeed()/(node.getActiveConnection()+1) > ((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getUploadSpeed()/((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getActiveConnection()
						//&& node.getPlayer().contains(this.getIndexOfLastPlayedChunk())
						&& !this.getNeighbors().contains(node))
				{										
					this.getNeighbors().remove(this.getNeighbors().get(k));
					this.getNeighbors().add(node);
					good = 1;
					break;
					
				}			
			}
			
			if(!this.isIncentiveBased())
//				if(node.getPlayer().size()>0)	
					if(node.getUploadSpeed()/(node.getActiveConnection()+1) > ((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getUploadSpeed()/((FitnessCoolStreamingPeer)this.getNeighbors().get(k)).getActiveConnection()
//						&& node.getPlayer().contains(this.getIndexOfLastPlayedChunk())
						&& !this.getNeighbors().contains(node))
					{		
						//System.out.println("CACA");
						this.getNeighbors().remove(this.getNeighbors().get(k));
						this.getNeighbors().add(node);
						good = 1;
						break;
					}
			
			}
		}
		}
		}
	}
	
		
	
	if(this.gossipNode != node)
	{
	this.gossipNode = node;
	
	int param = 4;//Engine.getDefault().getSimulationRandom().nextInt(6); 	
	
	if(this.getNeighbors().size() < param)
		param = this.getNeighbors().size();	
		
	//System.out.println(param);
	
	for(int i = 0 ; i < param ; i++)
	{
		FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)this.getNeighbors().get(Engine.getDefault().getSimulationRandom().nextInt(this.getNeighbors().size()));	

		//MODIFICARE GOSSIP LO RIINVIO SOLO SE MI è SERVITO
		
		//AGGINGURE NUOVO GOSSIP
		if(this.isIncentiveBased())
		{
			if(good==1 || this.equals(node))
			{				
			  this.overhead++;
			  peer.gossipProtocol(node,value);				
			}
		}
		// Vecchio gossip
		else
		{
			this.overhead++;
			peer.gossipProtocol(node,value);
		}
	}
	}
}



private Peer choiseBest(FitnessCoolStreamingPeer node) {
	
	Peer best = node;
	
	for(int i = 0 ; i < node.getServerByPeer().size(); i++ )
		if(node.getServerByPeer().get(i)!=null)
		if(node.getServerByPeer().get(i).getPlayer().contains(this.getIndexOfLastReceivedChunk()) 
				&& node.getServerByPeer().get(i).getUploadSpeed()/(node.getServerByPeer().get(i).getActiveConnection()+1) >(((FitnessCoolStreamingPeer) best).getUploadSpeed()/(((FitnessCoolStreamingPeer) best).getActiveConnection()+1)) )
		{
		 best = node.getServerByPeer().get(i);
		}
	
	return  best;
}

private boolean findProviderNodeFromLastSegment(float triggeringTime, int k) {
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho gia' un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getServerByPeer().get(k) == null && this.getServerByServer().get(k) == null )
		{								
					
				FitnessCoolStreamingPeer appSourceStreamingNode = this.choiseNeighbor(k);
									
						//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilita' di accettare le connessioni e se non ha tra la lista di quelli che sto fornendo
						if( appSourceStreamingNode != null && (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0)
						{
											
							 //Imposto il mio fornitore
							this.serverByPeer.set(k, appSourceStreamingNode);//.add(k, appSourceStreamingNode);//.get(i).setProviderPeer(SourceStreamingNode, i);
							//Incremento il mio ordine di nodo
							//this.updateNodeDepthCoolStreaming();
							updated.clear();
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//System.out.println("LAST SEGMENT");
							//Imposto la connessione attiva con il nodo fornitore trovato
							appSourceStreamingNode.addActiveConnection();
							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
							appSourceStreamingNode.getServedPeers2().get(k).add(this);																				  	
																				
							if(this.k_buffer.get(k).size() > 0)
								this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(appSourceStreamingNode,triggeringTime,this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex(),false);
							
							else
								this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(appSourceStreamingNode,triggeringTime,this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k),false);
							
							return true;							
			
			}
			
			//Se non trovo nessun nodo da cui fornirmi per una certa porzione rilancio la funzione base di ricerca fornitore
			if( this.getServerByPeer().get(k)== null && this.getServerByServer().get(k) == null)
			{		
				
				FitnessCoolStreamingServerPeer Server_peer = (FitnessCoolStreamingServerPeer) Engine.getDefault().getNodes().get(0);
				
					//Imposto il mio fornitore
					this.getServerByServer().set(k, Server_peer);
					
					//Incremento il mio ordine di nodo
					//this.updateNodeDepthCoolStreaming();	
					updated.clear();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il server centrale
					Server_peer.addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					Server_peer.getServedPeers2().get(k).add(this);									
														
					if(this.getK_buffer().get(k).size() > 0)
						this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(Server_peer,triggeringTime,this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex(),false);
					
					else
						this.getBufferNeighborFitnessCoolStreamingFromInitialChunk(Server_peer,triggeringTime,this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k),false);
			}	
		}
		
		return true;
	
}



	/**
	 * Ricavo dal fornitore i segmenti video che mancano.
	 * Il triggerTime e' utilizzato per determinare l'istante di invio di tali porzioni video.
	 * 
	 * L'indice j specifica nell'array del fornitore da quale pacchetto iniziare a ricevere i segmenti.
	 * 
	 * @param appSourceStreamingNode
	 * @param j
	 * @param triggeringTime
	 */
	public void getBufferNeighborFitnessCoolStreaming(Peer providerNode, float triggeringTime, int k)
	{
	
		if(providerNode.getId().equals("serverNode"))
		{
			
			FitnessCoolStreamingServerPeer source = (FitnessCoolStreamingServerPeer)providerNode;
			
			int startIndex = source.getK_buffer().get(k).size() - 3;			 				
			
			 if( startIndex < 0 )
			   startIndex = 0;			  					 	
			 
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk) )
			  {
				  FitnessCoolStreamingVideoChunk chunk = new FitnessCoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);
				  
				  chunk.setDestNode(this);
				  
				  if(!this.requestedChunk.contains(chunk))
					  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				  				  
			  }
		
		}
		else
		{
			
			FitnessCoolStreamingPeer source = (FitnessCoolStreamingPeer)providerNode;
						
			int startIndex = source.getK_buffer().get(k).size() - 3;;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex();			
			
			 if( startIndex < 0 )
			  startIndex = 0;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex() - 5;
			
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {		
				  FitnessCoolStreamingVideoChunk chunk = new FitnessCoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);

				  chunk.setDestNode(this);
				  
				  if(!this.requestedChunk.contains(chunk))
				  this.getRequestChunkBuffer().get(k).add(chunk);

				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				
		
			  }
		
		}
		
		//Richiedo i chunk in ordine
		this.requestChunk2(k);
		
	}
	
	
	
	/**
	 * Ricavo dal fornitore i segmenti video che mancano.
	 * Il triggerTime e' utilizzato per determinare l'istante di invio di tali porzioni video.
	 * 
	 * L'indice j specifica nell'array del fornitore da quale pacchetto iniziare a ricevere i segmenti.
	 * 
	 * @param appSourceStreamingNode
	 * @param j
	 * @param triggeringTime
	 * @param b 
	 */
	public void getBufferNeighborFitnessCoolStreamingFromInitialChunk(Peer providerNode, float triggeringTime, int initialChunk, boolean b)
	{

		if(providerNode.getId().equals("serverNode"))
		{
			
			FitnessCoolStreamingServerPeer source = (FitnessCoolStreamingServerPeer)providerNode;
			
			if(!b)
				initialChunk = this.getIndexOfLastReceivedChunk();
			
			int k = initialChunk%source.getK_value();
			
			int startIndex = source.getK_buffer().get(k).indexOf(new FitnessCoolStreamingVideoChunk(initialChunk,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()));
			
			
		
			 if( startIndex < 0 )
			   startIndex = 0;			  					 	
			 
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) 
				  && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {
			
				  FitnessCoolStreamingVideoChunk chunk = new FitnessCoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);
				  
				  chunk.setDestNode(this);
				  
				  if(!this.requestedChunk.contains(chunk))
					  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				  				  
				  
			  }
		
		}
		else
		{
			
			FitnessCoolStreamingPeer source = (FitnessCoolStreamingPeer)providerNode;
			
			if(!b)
				initialChunk = this.getIndexOfLastReceivedChunk();
			
			int k = initialChunk%source.getK_value();
			
			int startIndex = source.getK_buffer().get(k).indexOf(new FitnessCoolStreamingVideoChunk(initialChunk,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()));

			if( startIndex < 0 )
			  startIndex = 0;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex() - 5;
			
			 if(source.getK_buffer().get(k).size()>0 && !b)		
			 startIndex = source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex();
		
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) 
					&& (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {
				  
				  FitnessCoolStreamingVideoChunk chunk = new FitnessCoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);

				  chunk.setDestNode(this);
				  				  
				  if(!this.requestedChunk.contains(chunk))
				  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				
		
			  }
		}
		
		//Richiedo i chunk in ordine
		this.requestChunk2(initialChunk%this.getK_value());
		
	}	
	
	
	
	
	//TODO ULTIME COSE LO COLLEGO AL MIO FORNITORE ?????
public void disconnectionCoolStreaming(float triggeringTime){
								
		//Imposto il nodo come disconnesso
		this.setConnected(false);
		
		//Comunico le mie statistiche al Server
		FitnessCoolStreamingServerPeer server = (FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0);

		//Incremento il numero di disconnessioni
		server.addDisconnectedNode();				
		
		if(this.getServerByPeer().size() + this.getServerByServer().size() > 0)
		{	
		
		//Mi tolgo dai mie fornitori
		for(int i=0; i<this.k_value; i++)
			if(this.getServerByPeer().get(i) != null)
				{
				 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
				 this.getServerByPeer().get(i).removeActiveConnection();
				}
				
		
		for(int i=0; i<this.k_value; i++)
			if(this.getServerByServer().get(i) != null)
				{
				this.getServerByServer().get(i).getServedPeers2().get(i).remove(this);
				this.getServerByServer().get(i).removeActiveConnection();
				}
		
		
		
		//Azzero il mio grado di nodo
		this.nodeDepth = 0;
		
		
		//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
		for(int j=0; j<this.k_value; j++)
		for( int i = 0 ; i < this.getServedPeers2().get(j).size(); i++){				

	
			this.getServedPeers2().get(j).get(i).setProviderPeer(null, j);
			
			//Decremento il numero di download attivi del nodo che stavo fornendo
			this.getServedPeers2().get(j).get(i).setDownloadActiveConnection(this.getServedPeers2().get(j).get(i).getDownloadActiveConnection()-1);
			
			//Azzero la profondita' del nodo che si stava fornendo da me
			//this.getServedPeers2().get(j).get(i).resetNodeDepthFitnessCoolStreaming();
					
		}
		
		//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
		for(int j=0; j<this.k_value; j++)
		for( int i = 0 ; i < this.getServedPeers2().get(j).size(); i++){
			
			//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
			this.getServedPeers2().get(j).get(i).getNeededChunk().clear();
			
			/*
			//Se il mio fornitore e' attivo assegno il mio fornitore al nodo che prima stavo servendo io
			if( this.getServerByPeer().get(j) != null && this.getServerByPeer().get(j).isConnected() 
					&& ( this.getServerByPeer().get(j).getMaxAcceptedConnection() - this.getServerByPeer().get(j).getActiveConnection() ) > 0
					&& this.isIncentiveBased() && this.getServedPeers2().get(j).get(i).getIndexOfLastReceivedChunk() < this.getServerByPeer().get(j).getIndexOfLastReceivedChunk()
					&& (this.getServerByPeer().get(j).getMaxAcceptedConnection() - this.getServerByPeer().get(j).getActiveConnection())>0
			){
				
		
				//Imposto il mio nuovo fornitore
				this.getServedPeers2().get(j).get(i).getServerByPeer().set(j, this.getServerByPeer().get(j));//setProviderPeer(this.getServerByPeer().get(j), j);//setSourceStreamingNode(this.getServerByPeer().get(j));
				
				//System.out.println("DISCONNECTION");
				this.getServerByPeer().get(j).addActiveConnection();
			
				//Mi aggiungo nella lista dei serviti del mio fornitore
				this.getServerByPeer().get(j).getServedPeers2().get(j).add(this.getServedPeers2().get(j).get(i));
								
				//Incremento il mio ordine di nodo
				//this.getServedPeers2().get(j).get(i).updateNodeDepthCoolStreaming();
				updated.clear();
				
				//Incremento il numero di download attivi
				this.getServedPeers2().get(j).get(i).downloadActiveConnection ++;
				
				
				if(this.getK_buffer().get(j).size() > 0)
					this.getServedPeers2().get(j).get(i).getBufferNeighborFitnessCoolStreamingFromInitialChunk(this.getServerByPeer().get(j),triggeringTime,this.getK_buffer().get(j).get(this.getK_buffer().get(j).size()-1).getChunkIndex(),false);
				
			}			
			else{
			*/		
					if(this.getServedPeers2().get(j).get(i).getServerByPeer().get(j) == null && this.getServedPeers2().get(j).get(i).getServerByServer().get(j) == null)
						//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
						{							
						
						 this.getServedPeers2().get(j).get(i).findProviderNodeFromLastSegment(triggeringTime,j);
						 
						}
			//	}
			
			
		}
		
		}
		//Pulisco la lista dei nodi che stavo fornendo
		this.getServedPeers2().clear();
		
		//Rimuovo tutti i miei vicini
		this.resetNeighbors();
			
		this.gossipProtocol(this,-1);
		
		Engine.getDefault().getNodes().remove(this);
		
	}


/**
 * Funzione che controlla periodicamente il Buffer delle porzioni video
 * per verificare se ci sono dei pezzi mancanti.
 * 
 */
public void updateVideoBufferListCoolStreaming(float triggeringTime) {

	
	ArrayList<FitnessCoolStreamingVideoChunk> missingChunk = new ArrayList<FitnessCoolStreamingVideoChunk>(); 
	
	if(this.init_bool==false)
		this.init();
	
	for(int i=0; i < this.k_value ; i++ )
	if(this.k_buffer.get(i).size() >= 10)
	{
		//Guardo la prima meta' del mio buffer e vedo se ci sono porzioni mancanti
		for(int index = 0 ; index < this.k_buffer.get(i).size()-1 ; index++){
			
			int diff = this.k_buffer.get(i).get(index+1).getChunkIndex() - this.k_buffer.get(i).get(index).getChunkIndex();
					
			diff = diff/this.k_value;
			
			if( diff > 1 ){
			
				
			
				FitnessCoolStreamingVideoChunk chunk = this.k_buffer.get(i).get(index);

				int baseIndex = chunk.getChunkIndex();
				
				for(int k = 0 ; k < diff - 1 ; k++)
				{
					baseIndex = baseIndex + k_value;
					
					FitnessCoolStreamingVideoChunk newChunk = new FitnessCoolStreamingVideoChunk(baseIndex,chunk.getChunkSize());
					
					if( !this.requestedChunk.contains(newChunk.getChunkIndex()))
					{											
						this.requestedChunk.add(newChunk);
						missingChunk.add(newChunk);
					}
				}
			}
			
		}
	}					
	
	for(int i = 0 ; i < missingChunk.size(); i++)
		this.findChunkFromProviderFitnessCoolStreaming(missingChunk.get(i), triggeringTime);
	
	//Richiedo i chunk in ordine
	 this.requestChunk();

}
	





public boolean findChunkFromProviderFitnessCoolStreaming(FitnessCoolStreamingVideoChunk chunk,float triggeringTime){
	
	
	int index = this.calculate_buffer_index(chunk);	
		
	FitnessCoolStreamingVideoChunk newChunk = new FitnessCoolStreamingVideoChunk(chunk.getChunkIndex(),chunk.getChunkSize());
	
	//Controllo tra i miei fornitori se hanno la porzione che sto cercando
	if( this.getServerByPeer().get(index) != null ){						
		
		if(this.getServerByPeer().get(index).k_buffer.get(index).contains(chunk))
		{				
			newChunk.setSourceNode(this.getServerByPeer().get(index));
			newChunk.setOriginalTime(triggeringTime);
			newChunk.setDestNode(this);		
			
			if(!this.requestedChunk.contains(newChunk))
			this.getRequestChunkBuffer().get(index).add(newChunk);
			
			return true;
		}
		
	}	
	
	//Controllo tra i miei fornitori se hanno la porzione che sto cercando
	if( this.getServerByServer().get(index) != null ){							
		if(this.getServerByServer().get(index).getK_buffer().get(index).contains(chunk))
		{		
			newChunk.setSourceNode(this.getServerByServer().get(index));
			newChunk.setOriginalTime(triggeringTime);
			newChunk.setDestNode(this);
			
			if(!this.requestedChunk.contains(newChunk))
			this.getRequestChunkBuffer().get(index).add(newChunk);
					
			return true;
		}
	}	
		
	return false;

}



/**
 * Funzione utilizzata per azzerare iterativamente il mio grado di nodo 
 * e tutti i gradi di nodo dei nodi a me collegati
 */
public void resetNodeDepthFitnessCoolStreaming() {
		
	this.nodeDepth = 0;
	for(int j=0;j<this.k_value;j++)
	for(int i = 0 ; i < this.getServedPeers2().get(j).size(); i++)
		if(this.getServedPeers2().get(j).get(i).nodeDepth != 0)	
			this.getServedPeers2().get(j).get(i).resetNodeDepthFitnessCoolStreaming();
	
}

/**
 * Nel caso di un cambio di fornitore aggiorno il grado di nodo di tutti i 
 * miei serviti
 */
public void updateNodeDepthCoolStreaming() {

	int value = 0;
	
	updated.add(this);
	
	for(int i=0; i<this.k_value;i++)
	{
		if(this.getServerByPeer().get(i) != null)
		value = value + this.getServerByPeer().get(i).getNodeDepth();
	
	   if(this.getServerByServer().get(i) != null)
		value = value + this.getServerByServer().get(i).getNodeDepth();
	}
	
	this.nodeDepth = (value + this.k_value)/this.k_value;
	
	//if(this.getServedPeers2().size()>0)
	for(int j=0;j<this.k_value;j++)
		for(int i = 0 ; i < this.getServedPeers2().get(j).size(); i++)
			if(!updated.contains(this.getServedPeers2().get(j).get(i)))	
				this.getServedPeers2().get(j).get(i).updateNodeDepthCoolStreaming();
		
}





/**
 * Funzione utilizzata per simulare la riproduzione del filmato
 * utilizzando i chunk all'interno del videoPlayBuffer.
 * 
 * Effettua dei controlli su eventuali pacchetti mancanti al momento della
 * riproduzione e memorizza l'ultimo indice riprodotto per controllare
 * successive deadline.
 * 
 */
public boolean playVideoBufferCoolStreaming(){
	
	
	if(this.getK_buffer().size()>0)
	
	for(int i = 0 ; i < this.k_value ; i++ )
		for( int j = 0; j < this.getK_buffer().get(i).size(); j++)
			{
			if(!this.getPlayer().contains(this.getK_buffer().get(i).get(j)) && this.getK_buffer().get(i).get(j).getChunkIndex() > this.indexOfLastPlayedChunk)
			 this.getPlayer().add(this.getK_buffer().get(i).get(j));
			}
	
	this.getPlayer().addAll(this.sort(this.getPlayer()));	
		
	if(first)
	{
	if( this.player.size() >= 6*this.getK_value() ){			
		
		if(this.startUpTime == 0)
			this.startUpTime = Engine.getDefault().getVirtualTime() - this.connectionTime;
		
		first = false;			
		
		
		//Se sono arrivato qua ho la continuita' del primo blocco di elementi e posso riprodurli		
		this.indexOfLastPlayedChunk = this.player.get(0).getChunkIndex();			
		
		int initChunk = this.indexOfLastPlayedChunk; 
		
		int count = 0;
		
		for(int j=0 ; j < 5; j++)
			if(!this.player.contains(new FitnessCoolStreamingVideoChunk(initChunk+j,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
				{
				 this.deadlineNumber += 1;
				 this.missingChunkNumber += 1;
				}
			else 
				count ++;
		
		for(int j=0; j<count;j++)
		{
			int a = this.calculate_buffer_index(this.player.get(0));
			this.getK_buffer().get(a).remove(this.player.get(0));
			this.player.remove(0);			
		}
		
		this.indexOfLastPlayedChunk = initChunk+5;	
			
	}
	
	}
	else{				
        for(int i=0;i<this.getK_value();i++)		 
			 {
        	
        	if(this.getK_buffer().get(i).size()==0)
        		{
        		
        		
        		this.stop++;
        		}
        
        		this.choiseNewProvider(i);
        	
        	
			 }
		
		if(this.player.size()>5){
			
								
			int initChunk = this.indexOfLastPlayedChunk; 
			
			
			int count = 0;
			
			for(int j=0 ; j < 5; j++)
				if(!this.player.contains(new FitnessCoolStreamingVideoChunk(initChunk+j,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
					{
					 this.deadlineNumber += 1;
					 this.missingChunkNumber += 1;
					}
				else 
					count ++;
					
			for(int j=0; j<count;j++)
			{
				int a = this.calculate_buffer_index(this.player.get(0));
				this.getK_buffer().get(a).remove(this.player.get(0));
				this.player.remove(0);			
			}	
			
			this.indexOfLastPlayedChunk = initChunk + 5;

		}
		else {
									
			int initChunk = this.indexOfLastPlayedChunk;
			
			int count = 0;
			
			for(int j=0 ; j < 5; j++)
				if(!this.player.contains(new FitnessCoolStreamingVideoChunk(initChunk+j,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
					{					
					 this.deadlineNumber += 1;			
					 this.missingChunkNumber += 1;
					}
				else 
					count ++;		
			
			for(int j=0; j<count;j++)
			{
				int a = this.calculate_buffer_index(this.player.get(0));
				this.getK_buffer().get(a).remove(this.player.get(0));
				this.player.remove(0);			
			}					

			this.indexOfLastPlayedChunk = initChunk + 5;
		
		}
}
	//Non posso riprodurre
	return false;
}



	private void choiseNewProvider(int i) {
		
		FitnessCoolStreamingPeer app = this.choiseNeighbor(i);		
		
		if(this.getServerByPeer().get(i) != null && app != null && !app.equals(this.getServerByPeer().get(i)))
		if(0.05/this.k_value > this.getServerByPeer().get(i).getUploadSpeed()/this.getServerByPeer().get(i).getActiveConnection() && 0.05/this.k_value <= app.getUploadSpeed()/(app.getActiveConnection()+1) 
//				//&& 	0.648/this.k_value <= app.getUploadSpeed()/(app.getActiveConnection()+1)
			|| (this.getK_buffer().get(i).size()==0) 
				&& 0.05/this.k_value <= app.getUploadSpeed()/(app.getActiveConnection()+1))
		{
		if(this.getK_buffer().get(i).size()==0 && this.getServerByPeer().get(i) != null && app!=null)
		{
		 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
		 this.getServerByPeer().get(i).setActiveConnection(this.getServerByPeer().get(i).getActiveConnection()-1);
		 this.getServerByPeer().set(i, null);
		 this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
		 this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);		 
		}
		
		else if(this.getServerByPeer().get(i) != null && app!=null)	
			 if(this.getServerByPeer().get(i).getK_buffer().get(i).size()>0 && app.getK_buffer().get(i).size()>0){
			 if(!app.equals(this.getServerByPeer().get(i)))			 
			if((double)app.getUploadSpeed()/((double)app.getActiveConnection()+1) > (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection()
				&&	 app.getLastIndexOfChunk().get(i) > this.getServerByPeer().get(i).getLastIndexOfChunk().get(i))		 
			  {
				 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
				 this.getServerByPeer().get(i).setActiveConnection(this.getServerByPeer().get(i).getActiveConnection()-1);
				 this.getServerByPeer().set(i, null);
				 this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
				 this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);
			  }
			 }
		else
		{		
		 if(!app.equals(this.getServerByPeer().get(i)))			 
				if((double)app.getUploadSpeed()/((double)app.getActiveConnection()+1) > (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection()
					&&	 app.getLastIndexOfChunk().get(i) > this.getServerByPeer().get(i).getLastIndexOfChunk().get(i))
				  
				  {
					 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
					 this.getServerByPeer().get(i).setActiveConnection(this.getServerByPeer().get(i).getActiveConnection()-1);
					 this.getServerByPeer().set(i, null);
					 this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
					 this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);
				  }
		}
		}
}

	public ArrayList<FitnessCoolStreamingPeer> getServerByPeer() {
		return serverByPeer;
	}

	public void setServerPeers2(ArrayList<FitnessCoolStreamingPeer> serverPeers) {
		this.serverByPeer = serverPeers;
	}

	public ArrayList<ArrayList<FitnessCoolStreamingPeer>> getServedPeers2() {
		return servedPeers;
	}

	public void setServedPeers2(ArrayList<ArrayList<FitnessCoolStreamingPeer>> servedPeers2) {
		this.servedPeers = servedPeers2;
	}
		
	public void setProviderPeer(FitnessCoolStreamingPeer peer, int i)
	{
		this.getServerByPeer().set(i, peer);//.add(i, peer);
	}

	public ArrayList<FitnessCoolStreamingServerPeer> getServerByServer() {
		return serverByServer;
	}

	public void setServerByServer(ArrayList<FitnessCoolStreamingServerPeer> serverByServer) {
		this.serverByServer = serverByServer;
	}
	
	public void setProviderServer(FitnessCoolStreamingServerPeer peer, int i)
	{
		this.getServerByServer().set(i, peer);//.add(i, peer);
	}

	public boolean isInit_bool() {
		return init_bool;
	}

	public void setInit_bool(boolean init_bool) {
		this.init_bool = init_bool;
	}

	public ArrayList<Integer> getLastIndexOfChunk() {
		return lastIndexOfChunk;
	}

	public void setLastIndexOfChunk(ArrayList<Integer> lastIndexOfChunk) {
		this.lastIndexOfChunk = lastIndexOfChunk;
	}


	
	//TODO ULTIME COSE ARGOMENTI IF
	private FitnessCoolStreamingPeer choiseNeighbor(int k) {
		

		//TODO ISP ordidare i vicini in base a isp e upload e chunk middle  --- if(this.incentiveBased)
		if(this.isIncentiveBased())
		{		
			if( this.getK_buffer().get(k).size() > 0 )
				this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex()));
			
			else 
				this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(this.calculateMiddleChunk() - (this.getIndexOfLastReceivedChunk()%this.getK_value() - k)));		
		}
		
		else
		{
		if( this.getK_buffer().get(k).size() > 0 )
			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex()));
		
		else 
			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(this.calculateMiddleChunk() - (this.getIndexOfLastReceivedChunk()%this.getK_value() - k)));
		}
		
		for(int i=0; i<this.getNeighbors().size();i++)
		{
		FitnessCoolStreamingPeer appSourceStreamingNode = (FitnessCoolStreamingPeer) this.getNeighbors().get(i);;	
			
		if(     (appSourceStreamingNode.getServerByServer().get(k) != null || appSourceStreamingNode.getServerByPeer().get(k) != null)	
				&& appSourceStreamingNode.isConnected()
				//&&  appSourceStreamingNode.getK_buffer().get(k).size() > 8
			//	&& appSourceStreamingNode.getNodeDepth() < this.getNodeDepth() 
				&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0			   
		   )
	       return appSourceStreamingNode;
		}
		return null;
}

	
	
	private Collection<? extends Peer> orderNeighborsByUploadAndChunkMiddle(int chunkToFind) {		
			
			ArrayList<FitnessCoolStreamingPeer> appList = new ArrayList<FitnessCoolStreamingPeer>();			
			ArrayList<FitnessCoolStreamingPeer> appList2 = new ArrayList<FitnessCoolStreamingPeer>();
			ArrayList<FitnessCoolStreamingPeer> appList3 = new ArrayList<FitnessCoolStreamingPeer>();
			
			 for(int i = 0 ; i < this.getNeighbors().size() ; i++)
			 {
				 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)this.getNeighbors().get(i);
			  				 
				if(peer.getK_buffer().size()>0) 
				if(peer.getK_buffer().get(chunkToFind%peer.getK_value()).contains(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))
						&& peer.getIndexOfLastPlayedChunk() > this.getIndexOfLastPlayedChunk())
				 {
				 if(appList.size() == 0 )
					 appList.add(peer);						 
					 
				 else
				 {
					 for(int j = 0 ; j < appList.size(); j++)
					 {		    
					   FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList.get(j);		    					 
					
					   
					   if(this.fitnessBased)
					   {
						   
						   if( peer.fitnessValue >= peerApp.fitnessValue )	
						   {	 
								 appList.add(j,peer);
								 break;
						   }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
						   else if( j == appList.size() - 1)
						   {
								 appList.add(peer);
								 break;
						   }   
					   }
					   else
					   {
					    
						   if( (peer.getUploadSpeed()/(peer.getActiveConnection()+1) >= peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1)) ) 					      						
						   {	 
								 appList.add(j,peer);
								 break;
							}// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
							else if( j == appList.size() - 1)
							{
							 appList.add(peer);
							 break;
							}      
					   }
					 }
				 }			 			 
				}
				 else
				 {
					 appList2.add(peer);
				 }
			 }
			 
			 
			 
		for(int i = 0 ; i < appList.size() ; i++)
		 {
			 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)appList.get(i);
		  
			 if(appList3.size() == 0)
				 appList3.add(peer);						 
				 
			 else
			 {
				 for(int j = 0 ; j < appList3.size(); j++)
				 {		    
					 FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList3.get(j);		    					 
					 
					 if(this.fitnessBased)
					 {
						 if((peer.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peer.getK_buffer().get(chunkToFind%peer.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (peerApp.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peerApp.getK_buffer().get(chunkToFind%peerApp.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
							        && peer.fitnessValue == peerApp.fitnessValue)	 
						 {	 
									 appList3.add(j,peer);
									 break;
								 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
								 else if( j == appList3.size() - 1)
								 {
									 appList3.add(peer);
									 break;
								 }
					 }
					 else
					 {
						 if((peer.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peer.getK_buffer().get(chunkToFind%peer.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (peerApp.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peerApp.getK_buffer().get(chunkToFind%peerApp.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
							        && peer.getUploadSpeed()/(peer.getActiveConnection()+1) == peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1))	 
						 {	 
									 appList3.add(j,peer);
									 break;
						 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
						 else if( j == appList3.size() - 1)
						{
							 appList3.add(peer);
							 break;
					    }
					 }
				 }
			 }
			 
			 
			 
		 }
			 
			this.getNeighbors().clear();
			
			if(appList3.size()>0)
			{	
			  appList3.addAll(appList3.size()-1, appList2);
			  return appList3;
			}
			 else return appList2;
		}
	
	
	private Collection<? extends Peer> orderNeighborsByUploadAndChunkMiddleAndIsp(int chunkToFind) {
			
			ArrayList<FitnessCoolStreamingPeer> appList = new ArrayList<FitnessCoolStreamingPeer>();			
			ArrayList<FitnessCoolStreamingPeer> appList2 = new ArrayList<FitnessCoolStreamingPeer>();
			ArrayList<FitnessCoolStreamingPeer> appList3 = new ArrayList<FitnessCoolStreamingPeer>();
			
			 for(int i = 0 ; i < this.getNeighbors().size() ; i++)
			 {
				 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)this.getNeighbors().get(i);
			  		
				if(peer.getK_buffer().size()>0) 					
				if(peer.getK_buffer().get(chunkToFind%peer.getK_value()).contains(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))
						&& peer.getIndexOfLastPlayedChunk() > this.getIndexOfLastPlayedChunk())
				 {
				 if(appList.size() == 0 )
					 appList.add(peer);						 
					 
				 else
				 {
					 for(int j = 0 ; j < appList.size(); j++)
					 {		    
					   FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList.get(j);		    					 
						 					
					   if( (peer.getUploadSpeed()/(peer.getActiveConnection()+1) >= peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1)) ) 					      						
						 {	 
							 appList.add(j,peer);
							 break;
						 }
					   // Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
						 else if( j == appList.size() - 1)
						 {
							 appList.add(peer);
							 break;
						 }
					 }
				 }			 			 
				}
				 else
				 {
					 appList2.add(peer);					
				 }
			 }			 			
			 
		for(int i = 0 ; i < appList.size() ; i++)
		 {
			 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)appList.get(i);
		  
			 if(appList3.size() == 0)
				 appList3.add(peer);						 
				 
			 else
			 {
				 for(int j = 0 ; j < appList3.size(); j++)
				 {		    
					 FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList3.get(j);		    					 
					 
					 if(this.calculateGeographicDistance(this, peer) < this.calculateGeographicDistance(this, peerApp)
							 && (peer.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peer.getK_buffer().get(chunkToFind%peer.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (peerApp.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peerApp.getK_buffer().get(chunkToFind%peerApp.getK_value()).indexOf(new FitnessCoolStreamingVideoChunk(chunkToFind,((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
							 && peer.getUploadSpeed()/(peer.getActiveConnection()+1) == peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1))	 
					 {	 
						 appList3.add(j,peer);
						 break;
					 }
					 // Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
					 else if( j == appList3.size() - 1)
					 {
						 appList3.add(peer);
						 break;
					 }
				 }
			 }
			 
			 
			 
		 }
			 
			this.getNeighbors().clear();
			
			if(appList3.size()>0)
			{	
			  appList3.addAll(appList3.size()-1, appList2);	
			  return appList3;
			}
			
			 else {
				 return appList2;
			 }
		}
	
	
	
	
	public ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> sortRequestBuffer() {
		 
		 ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> appList = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		 for(int i=0;i<this.k_value;i++)
			 appList.add(i, new ArrayList<FitnessCoolStreamingVideoChunk>());
		 
		 
		for(int k = 0 ; k < this.getK_value(); k++)
		{ 		 			 	
		 for(int i = 0 ; i < this.getRequestChunkBuffer().get(k).size() ; i++)
		 {
			 FitnessCoolStreamingVideoChunk chunkOriginal = (FitnessCoolStreamingVideoChunk)this.getRequestChunkBuffer().get(k).get(i);
		  
			 if(appList.get(k).size() == 0){				 
				 appList.get(k).add(chunkOriginal);
			 }
				
			 else
			 {
				 for(int j = 0 ; j < appList.get(k).size(); j++)
				 {		    
					 FitnessCoolStreamingVideoChunk chunkApp = (FitnessCoolStreamingVideoChunk)appList.get(k).get(j);		    					 
					 
					 // Ordino in base alla presenza del chunk tra i miei vicini
					 if(this.numNeighborsWithChunk(chunkOriginal) <= this.numNeighborsWithChunk(chunkApp))
					 {	 					
						 appList.get(k).add(j, chunkOriginal);//add(j,chunkOriginal);
						 break;
					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
					 else if( j == appList.get(k).size() - 1)
					 {					
						 appList.get(k).add(chunkOriginal);
						 break;
					 }
				 }
			 }
		  }

		 }
		 
		 return appList;
		 
	}
	
	
	private int numNeighborsWithChunk(FitnessCoolStreamingVideoChunk chunk){
		
		int count = 0;
		int index = this.calculate_buffer_index(chunk);	
			
		for(int i=0; i<this.getNeighbors().size(); i++)
			if(((FitnessCoolStreamingPeer)this.getNeighbors().get(i)).getK_buffer().size()>0)
			if(((FitnessCoolStreamingPeer)this.getNeighbors().get(i)).getK_buffer().get(index).contains(chunk))
				count++;
				
		return count;
	}

	public ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> getSendBuffer() {
		return sendChunkBuffer;
	}

	public void setSendBuffer(ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> sendBuffer) {
		this.sendChunkBuffer = sendBuffer;
	}

	public ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> getRequestChunkBuffer() {
		return requestChunkBuffer;
	}

	public void setRequestChunkBuffer(
			ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> requestChunkBuffer) {
		this.requestChunkBuffer = requestChunkBuffer;
	}
	
	/**
	 * Funzione per richiedere in ordine i chunk ai fornitori 
	 * @param k 
	 */
	public void requestChunk(){
		
		ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> app = null;
		app = this.sortRequestBuffer();
		
		for(int i=0; i<this.k_value; i++)
			for(int j=0; j<app.get(i).size(); j++)
			{
				if(this.getServerByPeer().get(i) != null)
					this.getServerByPeer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
				else 
					if(this.getServerByServer().get(i) != null)
						this.getServerByServer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
			}
		
		this.getRequestChunkBuffer().clear();
		
		for(int i=0;i<this.getK_value();i++)
			this.requestChunkBuffer.add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
		
	}

	
	public void requestChunk2(int i){
				
		ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> app = null;
		app = this.sortRequestBuffer();
				
			for(int j=0; j<app.get(i).size(); j++)
			{
				if(this.getServerByPeer().get(i) != null)
					this.getServerByPeer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
				else 
					if(this.getServerByServer().get(i) != null)
						this.getServerByServer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
			}
		
		this.getRequestChunkBuffer().get(i).clear();
		
			this.requestChunkBuffer.add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
		
	}

	
	public int calculateMiddleChunk()
	{
		int sumMiddle = 0;
		int count = 0;
		int max = 0, min = 1000000000;
		if(this.getNeighbors().size()>0)
		{
		for(int i = 0; i < this.getNeighbors().size(); i++ )
			{
			 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)this.getNeighbors().get(i);
				for(int k = 0 ; k < peer.k_value ; k++ )
					if(peer.getK_buffer().size()>0)
					for( int j = 0; j < peer.getK_buffer().get(k).size(); j++)												
					{
						if(max < peer.getK_buffer().get(k).get(j).getChunkIndex())
							max = peer.getK_buffer().get(k).get(j).getChunkIndex();
						
						if(min > peer.getK_buffer().get(k).get(j).getChunkIndex())
							min = peer.getK_buffer().get(k).get(j).getChunkIndex();
					}
				
			if(max !=0 && min!=1000000000)	
				{				
				sumMiddle += (( max - min )/2 + min);
				count ++;
				}
			
			max = 0;			
			min = 1000000000;
			}
		if(count > 0)
			return sumMiddle/count;
		else 
			return 0;
		}
		else return 0;
	}
	
	public void sortParentsNodes(){

		//System.out.println("ORDINO !");
		
		ArrayList<Peer> appList = new ArrayList<Peer>();
		
		//Ordinamento dei vicini in base al loro valore di fitness
		for(int i = 0 ; i < this.neighbors.size() ; i++)
		{
			FitnessCoolStreamingPeer peerOriginal = (FitnessCoolStreamingPeer)this.neighbors.get(i);
			
			//La lista e' vuota aggiungo direttamente l'elemento
			if(appList.size() == 0)
				appList.add(peerOriginal);
			else
			{
				//Cerco se c'e' un Peer con fitness minore
				for(int j = 0 ; j < appList.size(); j++)
				{
					
					FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList.get(j);
					
					//Se il peer che analizzo ha la fitness Maggiore o uguale di quello nel vettore di appoggio
					if(peerOriginal.getFitnessValue() >= peerApp.getFitnessValue())
					{	
						appList.add(j,peerOriginal);
						break;
					}// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
					else if( j == appList.size() - 1)
					{
						appList.add(peerOriginal);
						break;
					}
				}
			}
		}
		
		//Inserisco gli elementi di AppList come vicini del nodo
		this.neighbors.clear();
		this.neighbors.addAll(appList);
		getLogger().fine("Num Vicini: " + this.neighbors.size());	
		
		getLogger().fine("#####################################\n");
	}
	
	
	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change3GTo2G( String connType, double uploadSpeed, int maxAcceptedConnection,float triggeringTime ){
		
		System.out.println("change3GTo2G");
		
		//Verifico il parametro connType
		if( connType.equals(G2) ){
		
			System.out.println("CAMBIO !");
			
			this.uploadSpeed = uploadSpeed;
			this.maxAcceptedConnection = maxAcceptedConnection;
	
			this.updateFitnessValue();
			
			//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
			for(int j=0; j<this.k_value; j++)
			for( int i = 0 ; i < this.getServedPeers2().get(j).size(); i++){				

		
				this.getServedPeers2().get(j).get(i).setProviderPeer(null, j);
				
				//Decremento il numero di download attivi del nodo che stavo fornendo
				this.getServedPeers2().get(j).get(i).setDownloadActiveConnection(this.getServedPeers2().get(j).get(i).getDownloadActiveConnection()-1);
				
				//Azzero la profondita' del nodo che si stava fornendo da me
				//this.getServedPeers2().get(j).get(i).resetNodeDepthFitnessCoolStreaming();
						
			}
			
			//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
			for(int j=0; j<this.k_value; j++)
			for( int i = 0 ; i < this.getServedPeers2().get(j).size(); i++){
				
				//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
				this.getServedPeers2().get(j).get(i).getNeededChunk().clear();
		
				/*
				//Se il mio fornitore e' attivo assegno il mio fornitore al nodo che prima stavo servendo io
				if( this.getServerByPeer().get(j) != null && this.getServerByPeer().get(j).isConnected() 
						&& ( this.getServerByPeer().get(j).getMaxAcceptedConnection() - this.getServerByPeer().get(j).getActiveConnection() ) > 0
						&& this.isIncentiveBased() && this.getServedPeers2().get(j).get(i).getIndexOfLastReceivedChunk() < this.getServerByPeer().get(j).getIndexOfLastReceivedChunk()){
					
			
					//Imposto il mio nuovo fornitore
					this.getServedPeers2().get(j).get(i).getServerByPeer().set(j, this.getServerByPeer().get(j));//setProviderPeer(this.getServerByPeer().get(j), j);//setSourceStreamingNode(this.getServerByPeer().get(j));
					
					this.getServerByPeer().get(j).addActiveConnection();
				
					//Mi aggiungo nella lista dei serviti del mio fornitore
					this.getServerByPeer().get(j).getServedPeers2().get(j).add(this.getServedPeers2().get(j).get(i));
									
					//Incremento il mio ordine di nodo
					//this.getServedPeers2().get(j).get(i).updateNodeDepthCoolStreaming();
					updated.clear();
					
					//Incremento il numero di download attivi
					this.getServedPeers2().get(j).get(i).downloadActiveConnection ++;
					
					
					if(this.getK_buffer().get(j).size() > 0)
						this.getServedPeers2().get(j).get(i).getBufferNeighborFitnessCoolStreamingFromInitialChunk(this.getServerByPeer().get(j),triggeringTime,this.getK_buffer().get(j).get(this.getK_buffer().get(j).size()-1).getChunkIndex(),false);
					
				}			
				else{
				*/		
				if(this.getServedPeers2().get(j).get(i).getServerByPeer().get(j) == null && this.getServedPeers2().get(j).get(i).getServerByServer().get(j) == null)
				//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
				{							
							
				 this.getServedPeers2().get(j).get(i).findProviderNodeFromLastSegment(triggeringTime,j);
							 
				}
				//	}
				
				
			}
		}
			
	}
	
	public void change2GTo3G(String connType, double uploadSpeed, int maxAcceptedConnection) {
		
		//Verifico il parametro connType
		if( connType.equals(G3)){
			
			this.connectionType = connType;
			this.maxAcceptedConnection = maxAcceptedConnection;
			this.uploadSpeed = uploadSpeed;
			
			//Calcolo la nuova fitness
			this.updateFitnessValue();
			
		}
		else
			System.out.println("Method change2GTo3G : Error in Connection Type !");
		
	}
	
	public boolean isIncentiveBased() {
		return incentiveBased;
	}

	public void setIncentiveBased(boolean incentiveBased) {
		this.incentiveBased = incentiveBased;
	}

	public int getInitChunk() {
		return initChunk;
	}

	public void setInitChunk(int initChunk) {
		this.initChunk = initChunk;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public float getStartUpTime() {
		return startUpTime;
	}

	public void setStartUpTime(float startUpTime) {
		this.startUpTime = startUpTime;
	}

	public int getIndexOfLastPlayedChunk() {
		return indexOfLastPlayedChunk;
	}

	public void setIndexOfLastPlayedChunk(int indexOfLastPlayedChunk) {
		this.indexOfLastPlayedChunk = indexOfLastPlayedChunk;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public int getIsp() {
		return isp;
	}

	public void setIsp(int isp) {
		this.isp = isp;
	}
	
	
	private int calculateGeographicDistance(FitnessCoolStreamingPeer myPeer, FitnessCoolStreamingPeer otherPeer) {
		
		if(myPeer.getIsp() == otherPeer.getIsp() 
				&& myPeer.getCity() == otherPeer.getCity())
			return 0;
		
		if(myPeer.getIsp() == otherPeer.getIsp() 
				&& myPeer.getCity() != otherPeer.getCity())
			return 2; //0
		
		if(myPeer.getIsp() != otherPeer.getIsp() 
				&& myPeer.getCity() == otherPeer.getCity())
			return 1;
		
		if(myPeer.getIsp() != otherPeer.getIsp() 
				&& myPeer.getCity() != otherPeer.getCity())
			return 3; //1
			
		return 4;
	}

	public int getExchangeInternalISP() {
		return exchangeInternalISP;
	}

	public void setExchangeInternalISP(int exchangeInternalISP) {
		this.exchangeInternalISP = exchangeInternalISP;
	}

	public int getExchangeExternalISP() {
		return exchangeExternalISP;
	}

	public void setExchangeExternalISP(int exchangeExternalISP) {
		this.exchangeExternalISP = exchangeExternalISP;
	}

	public int getOverhead() {
		return overhead;
	}

	public void setOverhead(int overhead) {
		this.overhead = overhead;
	}

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public int getFirstChunk() {
		return firstChunk;
	}

	public void setFirstChunk(int firstChunk) {
		this.firstChunk = firstChunk;
	}

	public void setBattery(double battery) {
		this.battery = battery;
	}

	public double getFitnessValue() {
		return fitnessValue;
	}

	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}



	
}
