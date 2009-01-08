package it.unipr.ce.dsg.deus.example.coolStreaming;


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


public class CoolStreamingPeer extends Peer {

	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String VIDEO_RESOURCE_BUFFER_LIMIT = "videoResourceBufferLimit";
	private static final String K_VALUE = "k_value"; 
	private static final String MAX_PARTNERS_NUMBER = "maxPartnersNumber";
	private static final String INCENTIVE_BASED = "incentiveBased";
	
	public static final String ADSL = "adsl";

	private int battery = 0;
	private String connectionType = "";
	private boolean incentiveBased = false;
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int videoResourceBufferLimit = 10;
	private int k_value = 0;
	private int maxPartnersNumber = 0;
	private int downloadActiveConnection = 0;
	private int nodeDepth = 0;
	private CoolStreamingPeer gossipNode = null;
	private int missingChunkNumber = 0;
	private int totalChunkReceived = 0;
	private int indexOfLastPlayedChunk;
	private int duplicateChunkNumber = 0;
	private int deadlineNumber = 0;
	private float startUpTime = 0;
	private float connectionTime = 0;
	private int isp = 0;
	private int city = 0;

	private long time1; 
	private long time2;
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private double onlineTime = 0.0;
	
	private CoolStreamingPeer sourceStreamingNode = null;
	private CoolStreamingServerPeer serverNode = null;
	private int continuityTrialCount = 0;
	
	//Lista contenente i peer che mi forniscono i chunk
	//private ArrayList<CoolStreamingPeer> serverPeers = new ArrayList<CoolStreamingPeer>();
	private ArrayList<CoolStreamingPeer> serverByPeer = new ArrayList<CoolStreamingPeer>();
	private ArrayList<CoolStreamingServerPeer> serverByServer = new ArrayList<CoolStreamingServerPeer>();
	
	//Lista contenente i peer a cui fornisco chunk
	//private ArrayList<CoolStreamingPeer> servedPeers = new ArrayList<CoolStreamingPeer>();
	private ArrayList<ArrayList<CoolStreamingPeer>> servedPeers = new ArrayList<ArrayList<CoolStreamingPeer>>();
	
	
	//Lista contenente i vari valori di fiducia dei nodi con cui ho interagito
	//private ArrayList<NeighborTrust> neighborTrust = new ArrayList<NeighborTrust>();
	
	//Lista contenente le richieste di chunk
	private ArrayList<ArrayList<CoolStreamingVideoChunk>> sendChunkBuffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
	//Lista contenente il numero di volte che invio un determinato chunk
	//private ArrayList<ArrayList<ChunkHash>> numOfChunkSended = new ArrayList<ArrayList<ChunkHash>>();  
	
	//LIsta contenente i chunk da richiedere
	private ArrayList<ArrayList<CoolStreamingVideoChunk>> requestChunkBuffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
	
	//K-Buffer
	private ArrayList<ArrayList<CoolStreamingVideoChunk>> k_buffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
	private ArrayList<Integer> lastIndexOfChunk = new ArrayList<Integer>();
	
	//Buffer di riproduzione
	private ArrayList<CoolStreamingVideoChunk> player = new ArrayList<CoolStreamingVideoChunk>();
	
	private ArrayList<CoolStreamingVideoChunk> videoResource = new ArrayList<CoolStreamingVideoChunk>();
	private ArrayList<CoolStreamingVideoChunk> videoPlayBuffer = new ArrayList<CoolStreamingVideoChunk>();
	private ArrayList<Float> arrivalTimes;
	private ArrayList<CoolStreamingVideoChunk> requestedChunk;
	private boolean init_bool = false;
	private int initChunk;
	private boolean first = true;
	private int stop = 0;
	private ArrayList<Boolean> cambio = new ArrayList<Boolean>();
	private static ArrayList<CoolStreamingPeer> updated = new ArrayList<CoolStreamingPeer>();
	
	public CoolStreamingPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();		
		
		
	}

public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(BATTERY))
			battery = Integer.parseInt(params.getProperty(BATTERY));
		
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
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
		
		time1 = System.currentTimeMillis();
		
	}

	
	public Object clone() {
	
		CoolStreamingPeer clone = (CoolStreamingPeer) super.clone();

		clone.activeConnection = this.activeConnection;
		clone.battery = this.battery;
		clone.connectionType = this.connectionType;
		clone.k_value = this.k_value;	
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.maxPartnersNumber = this.maxPartnersNumber;
		clone.onlineTime = 0;
		clone.time1 = 0;
		clone.time2 = 0;
//		clone.numOfChunkSended = new ArrayList<ArrayList<ChunkHash>>();
		clone.incentiveBased = this.incentiveBased;
		clone.initChunk = this.initChunk;
	//	clone.serverPeers = new ArrayList<CoolStreamingPeer>();;
		//clone.servedPeers = new ArrayList<CoolStreamingPeer>();;
		clone.sendChunkBuffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
		clone.requestChunkBuffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
		clone.serverNode = this.serverNode;
		clone.sourceStreamingNode = this.sourceStreamingNode;
		clone.videoResource = new ArrayList<CoolStreamingVideoChunk>();
		clone.player = new ArrayList<CoolStreamingVideoChunk>();
		clone.k_buffer = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
		clone.servedPeers = new ArrayList<ArrayList<CoolStreamingPeer>>();
		clone.serverByPeer = new ArrayList<CoolStreamingPeer>();
		clone.serverByServer = new ArrayList<CoolStreamingServerPeer>();
		clone.arrivalTimes = new ArrayList<Float>(); 
		clone.videoResourceBufferLimit  = this.videoResourceBufferLimit;
		clone.nodeDepth = 0;
		clone.missingChunkNumber = 0;
		clone.videoPlayBuffer = new ArrayList<CoolStreamingVideoChunk>();
		clone.totalChunkReceived = 0;
		clone.duplicateChunkNumber = 0;
		clone.lastIndexOfChunk = new ArrayList<Integer>();
		clone.requestedChunk = new ArrayList<CoolStreamingVideoChunk>();
		clone.deadlineNumber = 0;
		clone.isConnected = true;
		clone.init_bool = this.init_bool;
		clone.first = true;
		clone.cambio = new ArrayList<Boolean>();
		clone.stop = 0;
		clone.startUpTime = 0;	
		
		return clone;
	}
	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	/**
	 * Inizializza gli ArrayList
	 */
	public void init()
	{
		for(int i=0;i<this.getK_value();i++)
		{
			this.getSendBuffer().add(i,new ArrayList<CoolStreamingVideoChunk>());
			this.getRequestChunkBuffer().add(i,new ArrayList<CoolStreamingVideoChunk>());
			this.getK_buffer().add(i,new ArrayList<CoolStreamingVideoChunk>());	
			this.getServedPeers2().add(i,new ArrayList<CoolStreamingPeer>());
			this.getServerByPeer().add(i,null);
			this.getServerByServer().add(i,null);
			this.getLastIndexOfChunk().add(i,0);			
		//	this.getNumOfChunkSended().add(i,new ArrayList<ChunkHash>());
			this.cambio.add(false);
		}
		
//		for(int i=0;i<this.getK_value();i++)
//			for(int j=0; j<10000; j++)
//				this.getNumOfChunkSended().get(i).add(j,new ChunkHash(j*this.k_value + i,0));
		
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
	public void sendVideoChunk(CoolStreamingPeer clientNode,CoolStreamingVideoChunk newResource, float triggeringTime){
		
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
		
		//System.out.println( this + " Chunk : " +newResource.getChunkIndex() + " Index : " + index + " pos : " + pos);
		
	//	try
	//	{
		//this.getNumOfChunkSended().get(index).set(pos,new ChunkHash(newResource.getChunkIndex(),this.getNumOfChunkSended().get(index).get(pos).getNumberOfSend()+ 1));
	//	}
	//	catch(IndexOutOfBoundsException e)
	//	{
		
	//		this.getNumOfChunkSended().get(index).add(pos,new Integer(1));
	//	}
		
//		if(this.getKey() == 2125316136)
//		{
//			for(int i=0;i<this.k_value;i++)
//		{
//			System.out.println(" Bucket " + i);
//			for(int j=0;j<20;j++)
//				System.out.println(" Index " + this.getNumOfChunkSended().get(i).get(j).getChunkIndex() +" inviato " +  this.getNumOfChunkSended().get(i).get(j).getNumberOfSend());
//		}
//	}
		
//		ArrayList<ArrayList<CoolStreamingVideoChunk>> app = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
//		app.addAll(this.sortSendBuffer());
		
//		if(this.getKey() == 2125316136)
//		for(int i=0;i<this.k_value;i++)
//		{
//			System.out.println("Bucket " + i);
//			for(int j=0;j<a.get(i).size();j++)
//				System.out.println(" Ele " + a.get(i).get(j).getChunkIndex() );
//		
//		}
		
		
	//	System.out.println("Peer : " + this + " Invia: " + newResource.getChunkIndex() + " To:" + clientNode.getKey());
		
		float time = triggeringTime + appTime;
		
		
//		if(this.getKey() == 600835441)
//			System.out.println("Invio " + newResource.getChunkIndex() + " a " + newResource.getDestNode());
		
		CoolStreamingPeerNewVideoResourceEvent newPeerResEvent = (CoolStreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(CoolStreamingPeerNewVideoResourceEvent.class,time);
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
	private float nextChunkArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, CoolStreamingVideoChunk chunk, CoolStreamingPeer clientNode) {
		
		double time = 0.0;
		double minSpeed = Math.min(  (providerUploadSpeed  / (double) this.getActiveConnection()) , clientDownloadSpeed);
		double chunkMbitSize = (double)( (double) chunk.getChunkSize() / 1024.0 );
		time = (chunkMbitSize / minSpeed);
		
		//float floatTime = expRandom((float)time);
//		float floatTime = (float) Engine.getDefault().getSimulationRandom().nextInt(10);
		
		float sec=0;
		
		//System.out.println(((CoolStreamingPeer)chunk.getSourceNode()));
		//System.out.println(((CoolStreamingPeer)chunk.getDestNode()));
		
		//TODO ISP aggiungere latenza in base all'ISP e città 
		if(((CoolStreamingPeer)chunk.getSourceNode()).getIsp() == clientNode.getIsp()
				&& ((CoolStreamingPeer)chunk.getSourceNode()).getCity() == clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2);
		}
		
		if(((CoolStreamingPeer)chunk.getSourceNode()).getIsp() == clientNode.getIsp()
				&& ((CoolStreamingPeer)chunk.getSourceNode()).getCity() != clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 1;
		}
		
		if(((CoolStreamingPeer)chunk.getSourceNode()).getIsp() != clientNode.getIsp()
				&& ((CoolStreamingPeer)chunk.getSourceNode()).getCity() == clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 5;
		}
		
		if(((CoolStreamingPeer)chunk.getSourceNode()).getIsp() != clientNode.getIsp()
				&& ((CoolStreamingPeer)chunk.getSourceNode()).getCity() != clientNode.getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 6;
		}
		
		//if(time > floatTime)		
		//int a = Engine.getDefault().getSimulationRandom().nextInt(2);
		//if(a == 1)
	//	if((int)time*20/2 > 1)
		// sec = Engine.getDefault().getSimulationRandom().nextInt((int)time*20/2);
		//else sec = 0;
		
		//System.out.println(sec);
		
		return (float) (time*(20)+sec);
		
		//else return floatTime*3;
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
	public boolean checkDisconnection( CoolStreamingPeer peer ){
		
		if(peer == null)
			return true;
		
		if(peer.isConnected() == false)
			return true;
		
		return false;
		
	}
	
	public ArrayList<CoolStreamingVideoChunk> sort(ArrayList<CoolStreamingVideoChunk> arrayList) {
		 
		
	//	System.out.println("Ordino");
		
		 ArrayList<CoolStreamingVideoChunk> appList = new ArrayList<CoolStreamingVideoChunk>();
		 
		 //Ordinamento dei vicini in base al loro valore di fitness
		 for(int i = 0 ; i < arrayList.size() ; i++)
		 {
			 CoolStreamingVideoChunk chunkOriginal = (CoolStreamingVideoChunk)arrayList.get(i);
		  
			 if(appList.size() == 0)
				 appList.add(chunkOriginal);
			 else
			 {
				 for(int j = 0 ; j < appList.size(); j++)
				 {
		    
					 CoolStreamingVideoChunk chunkApp = (CoolStreamingVideoChunk)appList.get(j);
		    
					 
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
	
	
	public int calculate_buffer_index(CoolStreamingVideoChunk chunk)
	{	 	
	 return (chunk.getChunkIndex()%this.getK_value());	
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

	public int getBattery() {
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

	public ArrayList<CoolStreamingVideoChunk> getVideoResource() {
		return videoResource;
	}

	public CoolStreamingPeer getSourceStreamingNode() {
		return sourceStreamingNode;
	}

	public void setSourceStreamingNode(CoolStreamingPeer sourceStreamingNode) {
		this.sourceStreamingNode = sourceStreamingNode;
	}

	public CoolStreamingServerPeer getServerNode() {
		return serverNode;
	}

	public void setServerNode(CoolStreamingServerPeer serverNode) {
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

	public ArrayList<CoolStreamingVideoChunk> getVideoPlayBuffer() {
		return videoPlayBuffer;
	}

	public void setVideoPlayBuffer(ArrayList<CoolStreamingVideoChunk> videoPlayBuffer) {
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

	public ArrayList<CoolStreamingVideoChunk> getNeededChunk() {
		return requestedChunk;
	}

	public void setNeededChunk(ArrayList<CoolStreamingVideoChunk> neededChunk) {
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

	public ArrayList<ArrayList<CoolStreamingVideoChunk>> getK_buffer() {
		return k_buffer;
	}

	public void setK_buffer(ArrayList<ArrayList<CoolStreamingVideoChunk>> k_buffer) {
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
	
	public void printVideoBuffer(ArrayList<CoolStreamingVideoChunk> arrayList)
	{
		String my ="";
		for(int j = 0 ; j < arrayList.size(); j++)
			my = my + " " + arrayList.get(j).getChunkIndex();
				
		System.out.println("Id: "+ this.getKey() + " " + my);
	}

	

	
	public ArrayList<CoolStreamingVideoChunk> getPlayer() {
		return player;
	}

	public void setPlayer(ArrayList<CoolStreamingVideoChunk> player) {
		this.player = player;
	}

		
	public void findFirstProvidersNode(float triggeringTime){
		
	//	System.out.println("Cerco primi fornitori");
		
	/*	if(this.getKey() == 1545861358 ){
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa" );
		}*/
		
		if(this.connectionTime == 0)
			this.connectionTime = Engine.getDefault().getVirtualTime();
		
		boolean find_provider = false;
		
		ArrayList<CoolStreamingPeer> testedPeer = new ArrayList<CoolStreamingPeer>();

		
		//if(this.getKey() == 1145794561 )
		//	System.out.println(this + " Cerco i primi fornitori");
		
		if(init_bool == false)
		this.init();		
		
		int chunkMiddle = this.calculateMiddleChunk();
		
		//System.out.println("medio " +chunkMiddle);
		
		//Scorro i k fornitori
		for(int i = 0 ; i < this.k_value ; i++ )							
			if( this.getServerByPeer().get(i) == null && this.getServerByServer().get(i) == null )
			{		
				int index = 0;
				int index2 = 0;
				//System.out.println("Ciao");
				find_provider = false;						
		
				int chunkToFind = chunkMiddle - (chunkMiddle%this.getK_value() - i);
		
//				if(this.getKey() == 860184998)
//				System.out.println("Chunk " +chunkToFind);
				
//				this.getNeighbors().addAll(this.orderNeighbors(i));
				
				if(this.isIncentiveBased())
					this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(chunkToFind));
				
				else
					this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(chunkToFind));
				
			//	System.out.println("Dim 2 " +this.getNeighbors().size());
				
//				for(int j=0;j<this.getNeighbors().size();j++)
//					//System.out.println((double));
//					System.out.print(" upload" +((CoolStreamingPeer)this.getNeighbors().get(j)).getUploadSpeed()/(((CoolStreamingPeer)this.getNeighbors().get(j)).getActiveConnection()+1));
//				System.out.print("\n");
				
				if(this.getNeighbors().size() > 0)
				{			
				 do{
				//	System.out.println("Ciao1");
					//
									
					index = Engine.getDefault().getSimulationRandom().nextInt(this.getNeighbors().size());														
					
					CoolStreamingPeer SourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(index);
					
					//SourceStreamingNode.printK_Buffer();
					
					//TODO ISP Scelta dei primi fornitori
					
					if(this.isIncentiveBased())
					{
						SourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(index2);
						index2 ++;
					}
					
					
					//index++;
					
					if(!testedPeer.contains(SourceStreamingNode) && SourceStreamingNode.isConnected()==true )
						{
						//System.out.println("Ciao2");
  						  testedPeer.add(SourceStreamingNode);
						 
  						  //System.out.println(SourceStreamingNode.init_bool);
  							if(SourceStreamingNode.init_bool == false)
  	  								SourceStreamingNode.init();
  							
  							//SourceStreamingNode.printK_Buffer();
  							//System.out.println(SourceStreamingNode.getK_buffer().get(i).size());
  						  
  						  if( 	SourceStreamingNode.getK_buffer().get(i).contains(new CoolStreamingVideoChunk(chunkToFind,160))&&
								 SourceStreamingNode.getK_buffer().get(i).size() > 0 
								&& (SourceStreamingNode.getMaxAcceptedConnection() - SourceStreamingNode.getActiveConnection())>0)
						   {						
  							
  							//  System.out.println("DASDA");
						    //Imposto il mio fornitore
							this.serverByPeer.set(i, SourceStreamingNode);//.get(i).setProviderPeer(SourceStreamingNode, i);
							
							//Incremento il mio ordine di nodo
							this.updateNodeDepthCoolStreaming();
							updated.clear();
							
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//Imposto la connessione attiva con il nodo fornitore trovato
//TODO ????					//this.addActiveConnection();
							SourceStreamingNode.addActiveConnection();
							
						//	SourceStreamingNode.printFornitori();
							
							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
							SourceStreamingNode.getServedPeers2().get(i).add(this);
							
						/*	if(this.getKey() == 1227444402 ){
								System.out.println("Fornitore " + i);
								SourceStreamingNode.printK_Buffer();
							}*/
													
							//Chiamiamo la funzione per avere segmenti mancanti
							//this.getBufferNeighborCoolStreaming(SourceStreamingNode,triggeringTime,i);
							this.getBufferNeighborCoolStreamingFromInitialChunk(SourceStreamingNode,triggeringTime,chunkToFind);
							
							find_provider = true;
						    
							break;
						   }						
						}					
					
				} while(testedPeer.size()<this.getNeighbors().size());
				}	
				
				if(find_provider == false)
				{
					CoolStreamingServerPeer Server_peer = (CoolStreamingServerPeer) Engine.getDefault().getNodes().get(0);
										
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
									
					//this.getBufferNeighborCoolStreaming(Server_peer,triggeringTime,i);
					this.getBufferNeighborCoolStreamingFromInitialChunk(Server_peer,triggeringTime,chunkToFind);
				}
				
			
			testedPeer.clear();	
		}
	
		this.requestedChunk.addAll(this.sort(this.requestedChunk));
		
		if(this.requestedChunk.size()>0)
		this.initChunk = this.requestedChunk.get(0).getChunkIndex();
		
		ArrayList<CoolStreamingVideoChunk> app = new ArrayList<CoolStreamingVideoChunk>();
		app.addAll(this.requestedChunk);
		
	//	System.out.println(app);
		
		
		
		//Chiedo eventuali chunk mancanti
//		for(int i=0; i < app.size()-1; i++)
//		{
//			int diff = app.get(i+1).getChunkIndex() - app.get(i).getChunkIndex();
//			
//			for(int k = 0 ; k < diff -1 ; k++ )
//			{
//				CoolStreamingVideoChunk chunk = new CoolStreamingVideoChunk(app.get(i).getChunkIndex()+(k+1),app.get(i).getChunkSize());
//				int a = this.calculate_buffer_index(chunk);
//				 
//				 if(this.getServerByPeer().get(a) != null)
//					 chunk.setSourceNode(this.getServerByPeer().get(a));
//				 else chunk.setSourceNode(this.getServerByServer().get(a));
//				  
//				chunk.setOriginalTime(triggeringTime);								
//				
//				chunk.setDestNode(this);
//				
//				
//				if(this.getKey() == 639036909)
//					System.out.println("Chiedo " + chunk.getChunkIndex());
//				
//				if(this.getServerByPeer().get(a) != null)// && this.getServerByPeer().get(a).getK_buffer().get(a).contains(chunk))
//					{
////					if(this.getKey() == 531129312)
////					System.out.println("Aggiungo 1 " + chunk.getChunkIndex() + " da " + chunk.getSourceNode());
//					if(!this.requestedChunk.contains(chunk) && this.getServerByPeer().get(a).getK_buffer().get(a).contains(chunk))
//					{this.getRequestChunkBuffer().get(a).add(chunk);
//					//TODO TOGLIERE
//					//this.getServerByPeer().get(a).getSendBuffer().get(a).add(chunk);
//					
//					this.requestedChunk.add(chunk);}
//					}
//				
//					else if(this.getServerByServer().get(a) != null)// && this.getServerByServer().get(a).getK_buffer().get(a).contains(chunk))
//					{
////					if(this.getKey() == 531129312)
////					System.out.println("Aggiungo 2 " + chunk.getChunkIndex() + " da " + chunk.getSourceNode());
//					if(!this.requestedChunk.contains(chunk) && this.getServerByServer().get(a).getK_buffer().get(a).contains(chunk))
//					{this.getRequestChunkBuffer().get(a).add(chunk);
//					//TODO TOGLIERE
//					//this.getServerByServer().get(a).getSendBuffer().get(a).add(chunk);
//					
//					this.requestedChunk.add(chunk);}
//					}
//			}
//			
//		}
//		
		//Richiedo i chunk in ordine
//		if(this.getKey() == 1247386817)
//			this.printFornitori();
			
		this.requestChunk();
		
	/*	String a = "";
		for(int i=0;i<this.k_value;i++)			
			if(this.getServerByPeer().get(i) != null)		
				a = a + "fornitore " + i + " : " +this.getServerByPeer().get(i).getKey() + "\n";
			else 
				a = a + "fornitore " + i + " : " +this.getServerByServer().get(i).getKey() + "\n";
		  System.out.println("Nodo Id: "+ this.getKey() + " " + a );
		*/
	}

	
	
public void addNewVideoResourceCoolStreaming(CoolStreamingVideoChunk newVideoRes, float triggeringTime){
		
	//System.out.println("Aggiungo risorsa");
//       if(this.getKey() == 755464161 )
//		{
//		System.out.println("Arrivato pezzo " + newVideoRes.getChunkIndex() + " da " + newVideoRes.getSourceNode());
//		}
	
	
		//Salvo il tempo in cui e' arrivato il chunk
		float arrivalValue = triggeringTime - newVideoRes.getOriginalTime(); 
		this.arrivalTimes.add(arrivalValue);					
		
		
		int index = this.calculate_buffer_index(newVideoRes);		
		
		//Controllo se si e' verificata un eventuale deadline
		if( newVideoRes.getChunkIndex() <= this.indexOfLastPlayedChunk )
			this.missingChunkNumber--;
		
		else
		if(!this.k_buffer.get(index).contains(newVideoRes))
		{
			//if(this.getKey() == 1425199765 )
				//System.out.println("Aggiungo pezzo " + newVideoRes.getChunkIndex());
			if(this.getLastIndexOfChunk().get(index) < newVideoRes.getChunkIndex())
				this.getLastIndexOfChunk().set(index, newVideoRes.getChunkIndex());
			//Incremento il numero totale di chunk ricevuti
			this.totalChunkReceived ++;
		
			//Inserisco il chunk nel k_buffer appropriato e lo ordino
			this.k_buffer.get(index).add(newVideoRes);							
			this.k_buffer.get(index).addAll(this.sort(this.getK_buffer().get(index)));
			//if(this.getKey() == 863778027)
			//this.printK_Buffer();
		}
		else{
//			if(this.getKey() == 1439607443)
//		System.out.println(this + " Doppio : " + newVideoRes.getChunkIndex() +" da :" + newVideoRes.getSourceNode());
			this.duplicateChunkNumber ++; //Incremento il numero di duplicati		
		}
		
		//if(this.getKey() == 1437437965)
			//this.printK_Buffer();
		
	}



/**
 * Aggiorna la lista dei vicini
 * @param triggeringTime
 */
public void updateParentsListCoolStreaming(float triggeringTime){
		
	//System.out.println("Aggiorno parents");
	if(!this.isInit_bool())
		this.init();
	
	boolean served = false;	
		
	//Attraverso la lista dei nodi
	for(int i = 0; i < this.getNeighbors().size(); i++)
		if(checkDisconnection((CoolStreamingPeer) this.getNeighbors().get(i)))
			this.getNeighbors().remove(i);
	

	//Se qualche nodo e' stato rimosso dalla lista dei vicini
	if( this.getNeighbors().size() < this.getMaxPartnersNumber() ){
		
		//Cerco tra i miei vicini qualche nuovo contatto
		for(int index = 0; index < this.neighbors.size() ; index++)
		{

			CoolStreamingPeer peer = (CoolStreamingPeer)this.neighbors.get(index);
			
			for(int j = 0; j < peer.getNeighbors().size(); j++ ){
			
				CoolStreamingPeer peerApp = (CoolStreamingPeer)peer.neighbors.get(j);
	
//				for(int i =0 ; i<this.k_value; i++)
//					if(this.getServedPeers2().get(i).contains(peerApp)) 
//						{
//						served = true;
//						break;
//						}
				
				if(
						peerApp.isConnected() //Se il peer e' connesso
						&& !this.neighbors.contains(peerApp) //Se non e' gia' tra i miei vicni
						&& !peerApp.equals(this) //Se non sono io
					//	&& !served //Se non lo sto servendo
				  )
				{
				  this.addNeighbor(peerApp);
									
				//  this.getNeighborTrust().add(new NeighborTrust(peerApp.getKey(),0));
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
	
//	for(int i = 0 ; i < this.k_value ; i++ )
//	{
//	//	System.out.println("  dsdfsd " + this.getServerByPeer().get(i));
//		if( this.getServerByPeer().get(i) != null && !this.getServerByPeer().get(i).isConnected() )
//		{
//		// System.out.println("NON HO UN FORNITORE:" + this.getKey() + " - " + this.getNeighbors().size());
//		//	System.out.println("dwedeeresrwe" + this.getServerByPeer().get(i) + " " + this); 
//		 //Mi trovo un nuovo fornitore 			
//			this.findProviderNodeFromLastSegment(triggeringTime,i);
//	    }
//	}
	
}



/**
 * Aggiorna la lista dei vicini
 * @param triggeringTime
 */
public void gossipProtocol2(float triggeringTime,ArrayList<Peer> nodes){
			
	if(!this.isInit_bool())
		this.init();	
		
	//Attraverso la lista dei nodi
	for(int i = 0; i < this.getNeighbors().size(); i++)
		if(checkDisconnection((CoolStreamingPeer) this.getNeighbors().get(i)))
			this.getNeighbors().remove(i);
	
	
	for( int i = 0; i < nodes.size(); i++ )
	{
		CoolStreamingPeer peer = (CoolStreamingPeer)nodes.get(i);
		
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
			 	if(!this.getServerByPeer().contains((CoolStreamingPeer)this.getNeighbors().get(j)) && !this.getServerByPeer().contains((CoolStreamingPeer)peer.getNeighbors().get(k)))
			 	{
			 		//Se è migliore
			 		if(((CoolStreamingPeer)peer.getNeighbors().get(k)).getPlayer().contains(((CoolStreamingPeer)this.getNeighbors().get(j)).getIndexOfLastReceivedChunk()) 
			 			&& ((CoolStreamingPeer)peer.getNeighbors().get(k)).getUploadSpeed()/(((CoolStreamingPeer)peer.getNeighbors().get(k)).getActiveConnection()+1) >	((CoolStreamingPeer)this.getNeighbors().get(j)).getUploadSpeed()/((CoolStreamingPeer)this.getNeighbors().get(j)).getActiveConnection())
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
			 	if(!peer.getServerByPeer().contains((CoolStreamingPeer)peer.getNeighbors().get(j)) && !peer.getServerByPeer().contains((CoolStreamingPeer)this.getNeighbors().get(k)))
			 	{
			 		//Se è migliore
			 		if(((CoolStreamingPeer)this.getNeighbors().get(k)).getPlayer().contains(((CoolStreamingPeer)peer.getNeighbors().get(j)).getIndexOfLastReceivedChunk()) 
			 			&& ((CoolStreamingPeer)this.getNeighbors().get(k)).getUploadSpeed()/(((CoolStreamingPeer)this.getNeighbors().get(k)).getActiveConnection()+1) >	((CoolStreamingPeer)peer.getNeighbors().get(j)).getUploadSpeed()/((CoolStreamingPeer)peer.getNeighbors().get(j)).getActiveConnection())
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
public void gossipProtocol(CoolStreamingPeer node, int value){				
	
	if(!this.equals(node))
	{
		if(value == -1)
			this.getNeighbors().remove(node);
		
		else
		{		
		if(this.getNeighbors().size() < this.getMaxPartnersNumber())
			this.getNeighbors().add(node);

		else
		{
		for(int k = 0; k < this.getNeighbors().size(); k++ )
		{
		//if(!this.getNeighbors().contains(node) && !this.getServerByPeer().contains((CoolStreamingPeer)this.getNeighbors().get(k)))
		if(node.getPlayer().size()>0)	
		if(node.getPlayer().contains(this.getIndexOfLastReceivedChunk()) 
	 			&& node.getUploadSpeed()/(node.getActiveConnection()+1) >((CoolStreamingPeer)this.getNeighbors().get(k)).getUploadSpeed()/((CoolStreamingPeer)this.getNeighbors().get(k)).getActiveConnection())
		{
			this.getNeighbors().remove(this.getNeighbors().get(k));
			this.getNeighbors().add(node);
		}
		}
		}
		}
	}
	
	if(this.gossipNode != node)
	{
	this.gossipNode = node;
	
	int param = 5; 
	
	if(this.getNeighbors().size() < param)
		param = this.getNeighbors().size();	
		
	for(int i = 0 ; i < param ; i++)
	{
		CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(Engine.getDefault().getSimulationRandom().nextInt(this.getNeighbors().size()));
	
		peer.gossipProtocol(node,value);		
	}
	}
}


///**
// * Aggiorna la lista dei vicini
// * @param triggeringTime
// */
//public void updateParentsListCoolStreaming(float triggeringTime){
//		
//	//System.out.println("Aggiorno parents");
//	if(!this.isInit_bool())
//		this.init();				
//	
//	
//	//Attraverso la lista dei nodi
//	for(int i = 0; i < this.getNeighbors().size(); i++)
//		if(checkDisconnection((CoolStreamingPeer) this.getNeighbors().get(i)))
//			this.getNeighbors().remove(i);
//	
//
//	//Se qualche nodo e' stato rimosso dalla lista dei vicini
//	if( this.getNeighbors().size() < this.getMaxPartnersNumber() ){
//	
//		ArrayList<Peer> nearPeer ;
//		//TODO ISP Cercare nuovi vicini
//		if(false)
//			nearPeer = this.orderNeighbors(this);
//		
//		else nearPeer = this.getNeighbors();
//			
//		//Cerco tra i miei vicini qualche nuovo contatto
//		for(int index = 0; index < nearPeer.size() ; index++)
//		{
//
//			
//			CoolStreamingPeer peer = (CoolStreamingPeer)nearPeer.get(index);
//			
//			for(int j = 0; j <nearPeer.size(); j++ ){
//			
//				CoolStreamingPeer peerApp = (CoolStreamingPeer)nearPeer.get(j);
//	
////				for(int i =0 ; i<this.k_value; i++)
////					if(this.getServedPeers2().get(i).contains(peerApp)) 
////						{
////						served = true;
////						break;
////						}
//				
//				if(
//						peerApp.isConnected() //Se il peer e' connesso
//						&& !nearPeer.contains(peerApp) //Se non e' gia' tra i miei vicni
//						&& !peerApp.equals(this) //Se non sono io
//					//	&& !served //Se non lo sto servendo
//				  )
//				{
//				  this.addNeighbor(peerApp);
//									
//				  this.getNeighborTrust().add(new NeighborTrust(peerApp.getKey(),0));
//				} 
//				
//				//Se ho raggiunto il limite massimo esco
//				if(this.neighbors.size() == this.maxPartnersNumber)
//					break;
//			
//			}
//			
//			//Se ho raggiunto il limite massimo esco
//			if(this.neighbors.size() == this.maxPartnersNumber)
//				break;
//			
//		}
//		
//	}				
//	
//	int endListNumber = this.neighbors.size();
//	
//	if( endListNumber > this.maxPartnersNumber )
//		System.out.println("ERRORE LISTA TROPPO GRANDE !!!! ("+ endListNumber + "/" + this.maxPartnersNumber + ")");
//	
////	for(int i = 0 ; i < this.k_value ; i++ )
////	{
////	//	System.out.println("  dsdfsd " + this.getServerByPeer().get(i));
////		if( this.getServerByPeer().get(i) != null && !this.getServerByPeer().get(i).isConnected() )
////		{
////		// System.out.println("NON HO UN FORNITORE:" + this.getKey() + " - " + this.getNeighbors().size());
////		//	System.out.println("dwedeeresrwe" + this.getServerByPeer().get(i) + " " + this); 
////		 //Mi trovo un nuovo fornitore 			
////			this.findProviderNodeFromLastSegment(triggeringTime,i);
////	    }
////	}
//	
//}



private boolean findProviderNodeFromLastSegment(float triggeringTime, int k) {
		
	//System.out.println("Cerco last segment");
		//System.out.println("MERADA");
	//ArrayList<CoolStreamingPeer> testedPeer = new ArrayList<CoolStreamingPeer>();
	
		//Devo cercare un fornitore per il filmato soltanto se nn ho giË† un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getServerByPeer().get(k) == null && this.getServerByServer().get(k) == null )
		{								
			//if(this.k_buffer.get(k).size() > 0)
			//{
//				for( int neededVideoIndex = this.k_buffer.get(k).size()-1; neededVideoIndex >= 0; neededVideoIndex-- )
//				{
//					
//					CoolStreamingVideoChunk neededChunk = this.k_buffer.get(k).get(neededVideoIndex);
//					
//					//Cerco all'interno della mia lista di vicini se trovo un fornitore partendo dal segmento che gia' posseggo
//					for(int i = 0 ; i < this.getNeighbors().size(); i++){
//						
//						CoolStreamingPeer appSourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(i);
//						
//						//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilita' di accettare le connessioni e se non ha tra la lista di quelli che sto fornendo
//						if(     (appSourceStreamingNode.getServerByServer().get(k) != null || appSourceStreamingNode.getServerByPeer().get(k) != null)	
//								&& appSourceStreamingNode.isConnected()
//								&& !this.servedPeers2.get(k).contains(appSourceStreamingNode) 
//								&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0
//							    && appSourceStreamingNode.k_buffer.get(k).contains(neededChunk)
//						   )
//						{
//							
//							
//							 //Imposto il mio fornitore
//							this.serverByPeer.set(k, appSourceStreamingNode);//.add(k, appSourceStreamingNode);//.get(i).setProviderPeer(SourceStreamingNode, i);
//							//Incremento il mio ordine di nodo
//							this.updateNodeDepthCoolStreaming();
//							//Incremento il numero di download attivi
//							this.downloadActiveConnection ++;
//							//Imposto la connessione attiva con il nodo fornitore trovato
////TODO ????					//this.addActiveConnection();
//							appSourceStreamingNode.addActiveConnection();
//							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
//							appSourceStreamingNode.getServedPeers2().get(k).add(this);							
//													  	
//							//Chiamiamo la funzione per avere segmenti mancanti
//							this.getBufferNeighborCoolStreaming(appSourceStreamingNode,triggeringTime,k);
//							
//							return true;
//						}
//				}
//				}
		//	do{
			//	int index = Engine.getDefault().getSimulationRandom().nextInt(this.getNeighbors().size());
//				if(this.key == 1039036029)
//					System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");				
				
				
				CoolStreamingPeer appSourceStreamingNode = this.choiseNeighbor(k);
				
				//	CoolStreamingVideoChunk neededChunk = this.k_buffer.get(k).get(this.k_buffer.get(k).size()-1);

				//System.out.println(appSourceStreamingNode);
				
				//	System.out.println("asdsadas " + neededChunk.getChunkIndex());
					//Cerco all'interno della mia lista di vicini se trovo un fornitore partendo dal segmento che gia' posseggo					
						
					//CoolStreamingPeer appSourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(index);
					
						//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilita' di accettare le connessioni e se non ha tra la lista di quelli che sto fornendo
						if( appSourceStreamingNode != null )
						{
							
							//testedPeer.add(appSourceStreamingNode);
							 //Imposto il mio fornitore
							this.serverByPeer.set(k, appSourceStreamingNode);//.add(k, appSourceStreamingNode);//.get(i).setProviderPeer(SourceStreamingNode, i);
							//Incremento il mio ordine di nodo
							this.updateNodeDepthCoolStreaming();
							updated.clear();
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							//Imposto la connessione attiva con il nodo fornitore trovato
//TODO ????					//this.addActiveConnection();
							appSourceStreamingNode.addActiveConnection();
							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
							appSourceStreamingNode.getServedPeers2().get(k).add(this);							
													  	
							//Chiamiamo la funzione per avere segmenti mancanti
							//this.getBufferNeighborCoolStreaming(appSourceStreamingNode,triggeringTime,k);
//							if(this.key == 1039036029)
//								System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
																				
							
							if(this.k_buffer.get(k).size() > 0)
								this.getBufferNeighborCoolStreamingFromInitialChunk(appSourceStreamingNode,triggeringTime,this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex());
							
							else
								this.getBufferNeighborCoolStreamingFromInitialChunk(appSourceStreamingNode,triggeringTime,this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k));
							
							return true;
				//		}
			//	} while(testedPeer.size()<this.getNeighbors().size());
				
			
			
			}
			
			//Se non trovo nessun nodo da cui fornirmi per una certa porzione rilancio la funzione base di ricerca fornitore
			if( this.getServerByPeer().get(k)== null && this.getServerByServer().get(k) == null)
			{		
				
				CoolStreamingServerPeer Server_peer = (CoolStreamingServerPeer) Engine.getDefault().getNodes().get(0);
				
				//if(this.getKey() == 1227444402)
					//System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");								
					
					//Imposto il mio fornitore
					this.getServerByServer().set(k, Server_peer);
					
					//Incremento il mio ordine di nodo
					this.updateNodeDepthCoolStreaming();	
					updated.clear();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il server centrale
					Server_peer.addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					Server_peer.getServedPeers2().get(k).add(this);
					
					//this.getBufferNeighborCoolStreaming(Server_peer,triggeringTime,k);
									
					
					if(this.getK_buffer().get(k).size() > 0)
						this.getBufferNeighborCoolStreamingFromInitialChunk(Server_peer,triggeringTime,this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex());
					
					else
						this.getBufferNeighborCoolStreamingFromInitialChunk(Server_peer,triggeringTime,this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k));
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
	public void getBufferNeighborCoolStreaming(Peer providerNode, float triggeringTime, int k)
	{

		//if(triggeringTime>11000)
		//System.out.println("get buffer nerirg");
		//if(this.getKey() == 1145794561)
			//System.out.println("Cerco vicini per pezzi mancanti ");
		
	//	System.out.println("K = " + k);				
		
		if(providerNode.getId().equals("serverNode"))
		{
			
			CoolStreamingServerPeer source = (CoolStreamingServerPeer)providerNode;
			
			int startIndex = source.getK_buffer().get(k).size() - 3;
			 
			//System.out.println("StartIndex : " + startIndex);					
			
			 if( startIndex < 0 )
			   startIndex = 0;			  					 	
			 
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk) )
			  {
//				  if(this.getKey() == 639036909) 		
//				  System.out.println("Chiedo " + source.getK_buffer().get(k).get(index).getChunkIndex() + " a " + source.getKey());
				  
			//	  System.out.println("Chunk 3 " + source.getK_buffer().get(k).get(index).getChunkSize());
				  CoolStreamingVideoChunk chunk = new CoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);
				  
				  chunk.setDestNode(this);
				  
//				  if(this.getKey() == 531129312)
//						System.out.println("Aggiungo 3 " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
				  
				  if(!this.requestedChunk.contains(chunk))
					  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //TODO TOGLIERE
				  //source.getSendBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				  				  
				  
				//  System.out.println("Richiedo " + source.getK_buffer().get(k).get(index).getChunkIndex());
				
			  }
		
		}
		else
		{
			
			CoolStreamingPeer source = (CoolStreamingPeer)providerNode;
						
			int startIndex = source.getK_buffer().get(k).size() - 3;;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex();
			 
			//System.out.println("StartIndex : " + startIndex);
			
			 if( startIndex < 0 )
			  startIndex = 0;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex() - 5;
			 
			 //startIndex = startIndex/this.k_value; 
			 			
			// System.out.println("index "+ startIndex);
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {
//				  if(this.getKey() == 639036909)
//				 System.out.println("Chiedo " + source.getK_buffer().get(k).get(index).getChunkIndex() + " a " + source.getKey());
				  
			//	  System.out.println("Chunk 4 " + source.getK_buffer().get(k).get(index).getChunkSize());
				  CoolStreamingVideoChunk chunk = new CoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);

				  chunk.setDestNode(this);
				  
//				  if(this.getKey() == 531129312)
//						System.out.println("Aggiungo 4 " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
				  
				  if(!this.requestedChunk.contains(chunk))
				  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //TODO TOGLIERE
				 // source.getSendBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));
				//  System.out.print("Richiedo " + source.getK_buffer().get(k).get(index).getChunkIndex());
		
			  }
			// System.out.println("");
		}
		
		//Richiedo i chunk in ordine
		//if(this.getKey()==531129312)
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
	 */
	public void getBufferNeighborCoolStreamingFromInitialChunk(Peer providerNode, float triggeringTime, int initialChunk)
	{

		//if(triggeringTime>11000)
		//System.out.println("get buffer nerirg");
		//if(this.getKey() == 1145794561)
			//System.out.println("Cerco vicini per pezzi mancanti ");
		
	//	System.out.println("K = " + k);				
				
		
		if(providerNode.getId().equals("serverNode"))
		{
			
			CoolStreamingServerPeer source = (CoolStreamingServerPeer)providerNode;
			
			int k = initialChunk%source.getK_value();
			
			int startIndex = source.getK_buffer().get(k).indexOf(new CoolStreamingVideoChunk(initialChunk,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()));
			 
			//System.out.println("StartIndex : " + startIndex);					
			
			 if( startIndex < 0 )
			   startIndex = 0;			  					 	
			 
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) 
				  && (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {
//				  if(this.getKey() == 639036909) 		
//				  System.out.println("Chiedo " + source.getK_buffer().get(k).get(index).getChunkIndex() + " a " + source.getKey());
				  
			//	  System.out.println("Chunk 3 " + source.getK_buffer().get(k).get(index).getChunkSize());
				  CoolStreamingVideoChunk chunk = new CoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);
				  
				  chunk.setDestNode(this);
				  
//				  if(this.getKey() == 531129312)
//						System.out.println("Aggiungo 3 " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
				  
				  if(!this.requestedChunk.contains(chunk))
					  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //TODO TOGLIERE
				  //source.getSendBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));				  				  
				  
				//  System.out.println("Richiedo " + source.getK_buffer().get(k).get(index).getChunkIndex());
				
			  }
		
		}
		else
		{
			
			CoolStreamingPeer source = (CoolStreamingPeer)providerNode;
			
			int k = initialChunk%source.getK_value();
			
			int startIndex = source.getK_buffer().get(k).indexOf(new CoolStreamingVideoChunk(initialChunk,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()));
			 
			//System.out.println("StartIndex : " + startIndex);
			
			 if( startIndex < 0 )
			  startIndex = 0;//source.getK_buffer().get(k).get(source.getK_buffer().get(k).size()-1).getChunkIndex() - 5;
			 
			 //startIndex = startIndex/this.k_value; 
			 
			// System.out.println("index "+ startIndex);
			 for(int index = startIndex ; index < source.getK_buffer().get(k).size(); index++)
			  if(!this.getK_buffer().get(k).contains(source.getK_buffer().get(k).get(index)) 
					&& (source.getK_buffer().get(k).get(index).getChunkIndex() > this.indexOfLastPlayedChunk))
			  {
//				  if(this.getKey() == 1039036029){
//				this.printFornitori();
//					  System.out.println("Chiedo " + source.getK_buffer().get(k).get(index).getChunkIndex() + " a " + source.getKey());
//				  }
				 
				  
			//	  System.out.println("Chunk 4 " + source.getK_buffer().get(k).get(index).getChunkSize());
				  CoolStreamingVideoChunk chunk = new CoolStreamingVideoChunk(source.getK_buffer().get(k).get(index).getChunkIndex(),source.getK_buffer().get(k).get(index).getChunkSize());
				  chunk.setSourceNode(source);
				  chunk.setOriginalTime(triggeringTime);

				  chunk.setDestNode(this);
				  
//				  if(this.getKey() == 531129312)
//						System.out.println("Aggiungo 4 " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
				  
				  if(!this.requestedChunk.contains(chunk))
				  this.getRequestChunkBuffer().get(k).add(chunk);
				  
				  //TODO TOGLIERE
				 // source.getSendBuffer().get(k).add(chunk);
				  
				  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
				  this.requestedChunk.add(source.getK_buffer().get(k).get(index));
				//  System.out.print("Richiedo " + source.getK_buffer().get(k).get(index).getChunkIndex());
		
			  }
			// System.out.println("");
		}
		
		//Richiedo i chunk in ordine
		//if(this.getKey()==531129312)
		this.requestChunk2(initialChunk%this.getK_value());
		
	}	
	
	
	
	
	//TODO ULTIME COSE LO COLLEGO AL MIO FORNITORE ?????
public void disconnectionCoolStreaming(float triggeringTime){
		
	//System.out.println("Disconetto");
		//System.out.println("Mi Disconnetto : " + this);
	
//		if(this.getKey() == 557215546)
//			this.printFornitori();
						
		//Imposto il nodo come disconnesso
		this.setConnected(false);
		
		//Comunico le mie statistiche al Server
		CoolStreamingServerPeer server = (CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0);
//		server.setMissingChunkNumber(server.getMissingChunkNumber() + this.missingChunkNumber);
//		server.setTotalChunkReceived(server.getTotalChunkReceived() + this.totalChunkReceived);
//		server.setDuplicateChunkNumber(server.getDuplicateChunkNumber() + this.duplicateChunkNumber );
//		server.setTotalDeadline(server.getTotalDeadline() + this.deadlineNumber);
//		server.setStartUpTime(server.getStartUpTime() + this.startUpTime);
//		
//		//Aggiungo i tempi di arrivo di questo nodo al server per avere delle statistiche complete
//		if(this.getId().equals("pcNode"))
//			server.getArrivalTimesPcNode().addAll(this.arrivalTimes);
//		
//		if(this.getId().equals("pcNodeHigh"))
//			server.getArrivalTimesPcNodeHigh().addAll(this.arrivalTimes);
//		
		//Incremento il numero di disconnessioni
		server.addDisconnectedNode();				
		
		if(this.getServerByPeer().size() + this.getServerByServer().size() > 0)
		{	
		//this.printFornitori();
		
		
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

	//		System.out.println("Sono 0: " + this.getServedPeers2().get(j).get(i) );
			
			//System.out.println("Servivo : " + j + " " +this.getServedPeers2().get(j).get(i));
			this.getServedPeers2().get(j).get(i).setProviderPeer(null, j);
			
			//Decremento il numero di download attivi del nodo che stavo fornendo
			this.getServedPeers2().get(j).get(i).setDownloadActiveConnection(this.getServedPeers2().get(j).get(i).getDownloadActiveConnection()-1);
			
			//Azzero la profondita' del nodo che si stava fornendo da me
			this.getServedPeers2().get(j).get(i).resetNodeDepthCoolStreaming();
			
			//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
			//this.getServedPeers2().get(j).get(i).updateParentsListCoolStreaming(triggeringTime);						
			
		}
		
		//
		//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
		for(int j=0; j<this.k_value; j++)
		for( int i = 0 ; i < this.getServedPeers2().get(j).size(); i++){
		
			
		//	System.out.println("Iterazione " + this.getServedPeers2().get(j).get(i));
			
			//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
			this.getServedPeers2().get(j).get(i).getNeededChunk().clear();
			
			//Se il mio fornitore Ã¨ attivo assegno il mio fornitore al nodo che prima stavo servendo io
			if( this.getServerByPeer().get(j) != null && this.getServerByPeer().get(j).isConnected() 
					&& ( this.getServerByPeer().get(j).getMaxAcceptedConnection() - this.getServerByPeer().get(j).getActiveConnection() ) > 0
					&& this.isIncentiveBased()){
				
			//	System.out.println("Servivo : " + j + " " +this.getServedPeers2().get(j).get(i));
		//		System.out.println("Sono 1: " + this.getServedPeers2().get(j).get(i) );						
				
			//	System.out.println("Prima");
			//	this.getServedPeers2().get(j).get(i).printFornitori();
				
				//Imposto il mio nuovo fornitore
				this.getServedPeers2().get(j).get(i).getServerByPeer().set(j, this.getServerByPeer().get(j));//setProviderPeer(this.getServerByPeer().get(j), j);//setSourceStreamingNode(this.getServerByPeer().get(j));
				
	//			System.out.println("Mi imposto : " + j + " " +this.getServerByPeer().get(j));
				
				this.getServerByPeer().get(j).addActiveConnection();
				
		//		System.out.println("Dopo");
			//	this.getServedPeers2().get(j).get(i).printFornitori();
												
				//Mi aggiungo nella lista dei serviti del mio fornitore
				this.getServerByPeer().get(j).getServedPeers2().get(j).add(this.getServedPeers2().get(j).get(i));
								
				//Incremento il mio ordine di nodo
				this.getServedPeers2().get(j).get(i).updateNodeDepthCoolStreaming();
				updated.clear();
				
				//Incremento il numero di download attivi
				this.getServedPeers2().get(j).get(i).downloadActiveConnection ++;
				
				
				if(this.getK_buffer().get(j).size() > 0)
					this.getServedPeers2().get(j).get(i).getBufferNeighborCoolStreamingFromInitialChunk(this.getServerByPeer().get(j),triggeringTime,this.getK_buffer().get(j).get(this.getK_buffer().get(j).size()-1).getChunkIndex());
				
				
				//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
			//	this.getServedPeers2().get(j).get(i).getBufferNeighborCoolStreaming(this.getServerByPeer().get(j),triggeringTime,j);
			}			
			else{
					
					if(this.getServedPeers2().get(j).get(i).getServerByPeer().get(j) == null && this.getServedPeers2().get(j).get(i).getServerByServer().get(j) == null)
						//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
						{							
						
						 this.getServedPeers2().get(j).get(i).findProviderNodeFromLastSegment(triggeringTime,j);
						 
						}
				}
			
			
		}
		
		}
		//Pulisco la lista dei nodi che stavo fornendo
		this.getServedPeers2().clear();
		
		//this.printFornitori();
		
		//Rimuovo tutti i miei vicini
		this.resetNeighbors();
			
		this.gossipProtocol(this,-1);
		
		Engine.getDefault().getNodes().remove(this);
		
		//System.out.println("Levo dal engine" + this);
	}


/**
 * Funzione che controlla periodicamente il Buffer delle porzioni video
 * per verificare se ci sono dei pezzi mancanti.
 * 
 */
public void updateVideoBufferListCoolStreaming(float triggeringTime) {

	//System.out.println("Update buffer list");
	/*if(this.getKey() == 1425199765 )
		System.out.println("Aggiorno buffer ");*/
	
	ArrayList<CoolStreamingVideoChunk> missingChunk = new ArrayList<CoolStreamingVideoChunk>(); 
	
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
			
				//System.out.println("Diff : " + diff);
				
				//this.printK_Buffer();
				CoolStreamingVideoChunk chunk = this.k_buffer.get(i).get(index);
				//System.out.println("Chunk : " + this.k_buffer.get(i).get(index).getChunkIndex());
				int baseIndex = chunk.getChunkIndex();
				
				for(int k = 0 ; k < diff - 1 ; k++)
				{
					baseIndex = baseIndex + k_value;
					
		//			System.out.println("Chunk 2 " + chunk.getChunkSize());
					CoolStreamingVideoChunk newChunk = new CoolStreamingVideoChunk(baseIndex,chunk.getChunkSize());
					
//					if(this.getKey() == 639036909 )
//						System.out.println("Chiedo " + newChunk.getChunkIndex());// + " a " + source.getKey());
					
					if( !this.requestedChunk.contains(newChunk.getChunkIndex()))
					{						
						//if(this.getKey() == 1227444402 )
							//System.out.println("Agggiungo " + newChunk.getChunkIndex());
						this.requestedChunk.add(newChunk);
						missingChunk.add(newChunk);
					}
				}
			}
			
		}
	}					
	
	//this.playVideoBufferCoolStreaming();

	for(int i = 0 ; i < missingChunk.size(); i++)
		this.findChunkFromProviderCoolStreaming(missingChunk.get(i), triggeringTime);
	
	//Richiedo i chunk in ordine
	 this.requestChunk();

}
	





public boolean findChunkFromProviderCoolStreaming(CoolStreamingVideoChunk chunk,float triggeringTime){
	
	//System.out.println("find chunk from provider");
	
	int index = this.calculate_buffer_index(chunk);	
	
//	if(this.getKey() == 1425199765 )
	//	System.out.println("Cerco pezzo " + chunk.getChunkIndex()+ "da " + this.getServerByPeer().get(index));
	
//	System.out.println("Chunk 1 " + chunk.getChunkSize());
	CoolStreamingVideoChunk newChunk = new CoolStreamingVideoChunk(chunk.getChunkIndex(),chunk.getChunkSize());
	
	//Controllo tra i miei fornitori se hanno la porzione che sto cercando
	if( this.getServerByPeer().get(index) != null ){						
		
		if(this.getServerByPeer().get(index).k_buffer.get(index).contains(chunk))
		{			
			//System.out.println("Trovato");
			newChunk.setSourceNode(this.getServerByPeer().get(index));
			newChunk.setOriginalTime(triggeringTime);
			newChunk.setDestNode(this);
			
//			if(this.getKey() == 531129312)
//				System.out.println("Aggiungo " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
			
			if(!this.requestedChunk.contains(newChunk))
			this.getRequestChunkBuffer().get(index).add(newChunk);
			
			//TODO AGGIUNGERE CHE Ã© STATO RICHIESTO ??? PENSO NO
			//this.requestedChunk.add(newChunk);
			
			//TODO TOGLIERE
			//this.getServerByPeer().get(index).getSendBuffer().get(index).add(newChunk);
			
			//if(this.getKey() == 1992509272 )
			//System.out.println("TROVATO " + chunk.getChunkIndex() );
			return true;
		}
		
	//	else System.out.println("NON LO TROVO");
		
	}	
	
	//Controllo tra i miei fornitori se hanno la porzione che sto cercando
	if( this.getServerByServer().get(index) != null ){							
		if(this.getServerByServer().get(index).getK_buffer().get(index).contains(chunk))
		{		
			newChunk.setSourceNode(this.getServerByServer().get(index));
			newChunk.setOriginalTime(triggeringTime);
			newChunk.setDestNode(this);
			
//			if(this.getKey() == 531129312)
//				System.out.println("Aggiungo " + chunk.getChunkIndex()+ " da " + chunk.getSourceNode());
			
			if(!this.requestedChunk.contains(newChunk))
			this.getRequestChunkBuffer().get(index).add(newChunk);
			
			//TODO AGGIUNGERE CHE Ã© STATO RICHIESTO ??? PENSO NO
			//this.requestedChunk.add(newChunk);
			
			//TODO TOGLIERE
			//this.getServerByServer().get(index).getSendBuffer().get(index).add(newChunk);
			
			
			return true;
		}
	}	
	//else System.out.println("NON LO TROVO");
	
	
	return false;

}



/**
 * Funzione utilizzata per azzerare iterativamente il mio grado di nodo 
 * e tutti i gradi di nodo dei nodi a me collegati
 */
public void resetNodeDepthCoolStreaming() {
		
	this.nodeDepth = 0;
	//if(this.getServedPeers2().size()>0)
	for(int j=0;j<this.k_value;j++)
	for(int i = 0 ; i < this.getServedPeers2().get(j).size(); i++)
		if(this.getServedPeers2().get(j).get(i).nodeDepth != 0)	
			this.getServedPeers2().get(j).get(i).resetNodeDepthCoolStreaming();
	
}

/**
 * Nel caso di un cambio di fornitore aggiorno il grado di nodo di tutti i 
 * miei serviti
 */
public void updateNodeDepthCoolStreaming() {

	//System.out.println("update");
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
	
//	System.out.println("Paly buffer");
//	if(this.getKey() == 639036909)
//		this.printK_Buffer();
	//this.printFornitori();
	//this.printK_Buffer();
	//if(this.getKey() == 1728696015)
		//{
//	if(this.getKey() == 755464161)
//		{
//		this.printFornitori();	
//		this.printK_Buffer();
//		}
//	if(this.getKey() == 1038481085)
//	{
//	this.printFornitori();	
//	this.printK_Buffer();
//	}
	
	if(this.getK_buffer().size()>0)
	
	for(int i = 0 ; i < this.k_value ; i++ )
		for( int j = 0; j < this.getK_buffer().get(i).size(); j++)
			{
			if(!this.getPlayer().contains(this.getK_buffer().get(i).get(j)) && this.getK_buffer().get(i).get(j).getChunkIndex() > this.indexOfLastPlayedChunk)
			 this.getPlayer().add(this.getK_buffer().get(i).get(j));
			// this.getK_buffer().get(i).clear();
			}
	
	this.getPlayer().addAll(this.sort(this.getPlayer()));
	
	int difftot = 0;
	
	/*if(this.getKey() == 747329007)
	{
		this.printVideoBuffer(this.getPlayer());
		System.out.println(continuityTrialCount);
	}*/
	
	//this.getPlayer().clear();
	
	//Se il numero di tentativi di trovare 5 chunk continui e' fallito
/*	if( continuityTrialCount >= 1 )
	{
		//Azzero il contatore
		continuityTrialCount = 0;						
		
		ArrayList<CoolStreamingVideoChunk> appList = new ArrayList<CoolStreamingVideoChunk>();
		appList.addAll(this.player);
		
		//Rimuovo i segmenti non continui all'interno dei primi 5 e dopo procedo normalmente nella ricerca
		for(int index = 0 ; index < 5 ; index++){
			
			int diff = appList.get(index+1).getChunkIndex() - appList.get(index).getChunkIndex();
		
			//diff = diff/this.k_value;
			
			//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
			if( diff > 1 ){
				
				difftot = difftot + diff - 1 ;
				//System.out.println("TROPPI TENTATIVI! RIMUOVO:" + this.videoResource.get(index).getChunkIndex());
				
				//Rimuovo l'elemento non continuo
			//	int a = this.calculate_buffer_index(appList.get(index));							
			//	this.getK_buffer().get(a).remove(appList.get(index));
				
			//	this.indexOfLastPlayedChunk = appList.get(index).getChunkIndex();
								
			//	this.player.remove(index);
				//Incremento il numero di missing, ovvero di volte che sono costretto a rimuovere dei segmenti per mancanza di continuita'
			//	this.missingChunkNumber += diff - 1;	
			
				//Incremento il numero di deadline in quanto non posso riprodurre questi segmenti per mancanza di continuitË†
			//	this.deadlineNumber += diff - 1 ;
			}				
			
		}
		
		this.indexOfLastPlayedChunk = this.player.get(4).getChunkIndex();
		
	
		//this.printK_Buffer();
			//System.out.println(difftot);
			
	//	}
		
		
		for(int k = 0 ; k < 5 ; k++)
		{	
		//	list = list + "," + this.player.get(0).getChunkIndex();
			//System.out.println("Rimuovo");
			int a = this.calculate_buffer_index(this.player.get(0));							
			this.getK_buffer().get(a).remove(this.player.get(0));
			this.player.remove(0);												
		}
	
		this.deadlineNumber += difftot;
		this.missingChunkNumber += difftot;
		
		//difftot = 0;
	}
	*/
	
	//System.out.println(this.player.size());
	//Se ci sono un numero sufficente di elementi nel buffer
//	if(this.getKey() == 860184998)
//	if( this.key == 820502299)
//	{
//	//	this.printFornitori();
//		this.printK_Buffer();
//	}
	if(first )
	{
	if( this.player.size() >= 5*this.getK_value() ){			
		
//		if(this.getK_buffer().get(0).size()>10 && this.getK_buffer().get(1).size()>10 
//				&& this.getK_buffer().get(2).size()>10 && this.getK_buffer().get(3).size()>10)
//		{
		if(this.startUpTime == 0)
			this.startUpTime = Engine.getDefault().getVirtualTime() - this.connectionTime;
		
		first = false;
		/*
		String origin = "";
		for(int k = 0 ; k < 5 ; k++)
			origin = origin + "," + this.videoResource.get(k).getChunkIndex();
			
		System.out.println("Id: " + this.getKey() + " Primi Elementi:" + origin);
		*/
		
		//Verifichiamo la continuita' dei primi elementi che voglio mandare in esecuzione
//		for(int index = 0 ; index < 4 ; index++){
//			
//			int diff = this.player.get(index+1).getChunkIndex() - this.player.get(index).getChunkIndex();
//		
//			//diff = diff/this.k_value;
//			
//			//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
//			if( diff > 1 ){
//					
//				difftot = difftot + diff - 1 ;
//	//			difftot = difftot + diff - 1 ;		
//				/*	String list = "";
//					
//					for(int k = 0 ; k < 5 ; k++)
//						list = list + "," + this.player.get(k).getChunkIndex();*/
//						
//				//	System.out.println("Attendo ! Id: " + this.getKey() + " Primi Elementi:" + list + "\n");
//					
//					//Incremento il numero di tentativi di trovare delle sequenze continue
//					//continuityTrialCount  ++;
//					
//					//return false;
//			}
//		}
//				
		
		
		//Se sono arrivato qua ho la continuita' del primo blocco di elementi e posso riprodurli		
		this.indexOfLastPlayedChunk = this.player.get(0).getChunkIndex();
		
		//Memorizzo l'ultimo indice riprodotto		
		//this.indexOfLastPlayedChunk = this.player.get((5-difftot)-1).getChunkIndex();				
		
		//this.deadlineNumber += difftot;
		//this.missingChunkNumber += difftot;
		
		int initChunk = this.indexOfLastPlayedChunk; 
		
//		if(this.getKey() == 820502299)
//			System.out.println("init " + initChunk);
		
		//String list = "";		
		
//		for(int k = 0 ; k < 5 ; k++)
//		{	
//			//list = list + "," + this.player.get(0).getChunkIndex();
//			//System.out.println("Rimuovo");			
//			int a = this.calculate_buffer_index(this.player.get(0));
//		
//			int diff = this.player.get(0).getChunkIndex() - this.indexOfLastPlayedChunk;
//			
////			if(this.getKey() == 820502299)
////				System.out.println("diff " + (diff));
//			
//			if( this.player.get(0).getChunkIndex() - initChunk < 5 )
//			{	
//				if((diff ) != 0)
//				{
//				this.deadlineNumber += (diff );
//				this.missingChunkNumber +=(diff);
//				this.indexOfLastPlayedChunk += (diff );
//				}
//				this.indexOfLastPlayedChunk += (1);
//				this.getK_buffer().get(a).remove(this.player.get(0));
//				this.player.remove(0);				
//			}
//			
////			if(this.getKey() == 820502299)
////				System.out.println("last play " + this.indexOfLastPlayedChunk);
//			
//		}				
		
		int count = 0;
		
		for(int j=0 ; j < 5; j++)
			if(!this.player.contains(new CoolStreamingVideoChunk(initChunk+j,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
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
		
//		if(this.getKey() == 820502299)
//		{
//		System.out.println(this.deadlineNumber );
//		this.printK_Buffer();
//		}
		//System.out.println("Riproduco ! Id: " + this.getKey() + " Primi Elementi:" + list + "\n");
			
	}
	
	}
	else{
		//this.printK_Buffer();
//		if(this.getKey() == 600835441){
//			this.printFornitori();
//			this.printK_Buffer();
//			}
//
//		
//		if(this.getKey() == 865088624){
//			this.printFornitori();
//			
//			for(int i=0;i<this.getK_value();i++)
//				{
//				if(this.getServerByPeer().get(i)!=null)
//					System.out.println( this.getServerByPeer().get(i).getUploadSpeed()/this.getServerByPeer().get(i).getActiveConnection());
//				//this.getServerByPeer().get(i).printK_Buffer();}
//				else
//					System.out.println( this.getServerByServer().get(i).getUploadSpeed()/this.getServerByServer().get(i).getActiveConnection());
//				
//				}
//			this.printK_Buffer();
//		System.out.println(this.deadlineNumber);	
//		}
////			}
			
		
		//this.printK_Buffer();
        for(int i=0;i<this.getK_value();i++)
		 //if(this.getK_buffer().get(i).size() < 5 )
			 {
			//if(this.getK_buffer().size()>0)
		//	{
//           app = this.cchoise()()(;        	
//        	 if(this.getServerByPeer().get(i) != null && app!=null)				 
//    			 if(!app.equals(this.getServerByPeer().get(i)))			 
//    				if((double)app.getUploadSpeed()/((double)app.getActiveConnection()+1) > (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection())  
//    			  {
//    	//			if(this.getKey() ==1387824271)	
//    				//System.out.println(this + " CAMBIO");	
//    		//		 System.out.println("Vecchio " + (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection());
//    				 
//    	//			 this.getServerByPeer().get(i).printK_Buffer();
////    				 System.out.println("Nuovo " + (double)app.getUploadSpeed()/((double)app.getActiveConnection()+1));
//
//    				// System.out.println((double) (0.640/(double)this.k_value));				 				 
//    				 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
//    				 this.getServerByPeer().get(i).setActiveConnection(this.getServerByPeer().get(i).getActiveConnection()-1);
//    				 this.getServerByPeer().set(i, null);
//    				 this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
//    				 this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);
//    			  }
        	
        	this.choiseNewProvider(i);
        	
      //  	System.out.println(app);
       // 	System.out.println(this.getServerByPeer().get(i));
			 //this.printFornitori();
			
			   
			 /* else if(this.getServerByServer().get(i) != null) 
				 if(this.getServerByServer().get(i).getUploadSpeed()/this.getServerByServer().get(i).getActiveConnection() < (double) (0.640/(double)this.k_value))
					  if(app!=null)
					//		if((double)app.getUploadSpeed()/((double)app.getActiveConnection()+1) > (double)this.getServerByServer().get(i).getUploadSpeed()/(double)this.getServerByServer().get(i).getActiveConnection())  

				  {				  
				  this.getServerByServer().get(i).getServedPeers2().get(i).remove(this);
				  this.getServerByServer().get(i).setActiveConnection(this.getServerByServer().get(i).getActiveConnection()-1);
				  this.getServerByServer().set(i, null);
				  this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
				  this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);
				  }
				*/		  			  			 
			 // System.out.println("AHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		
			  //this.printFornitori();
			//  this.cambio.set(i, true);
			 }
		
		if(this.player.size()>5){
			
			
//			for(int index = 0 ; index < 5 ; index++){
//			
//			int diff = this.player.get(index+1).getChunkIndex() - this.player.get(index).getChunkIndex();
//		
//			//diff = diff/this.k_value;
//			
//			//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
//			if( diff > 1 ){
//					
//				difftot = difftot + diff - 1 ;
//	//			difftot = difftot + diff - 1 ;		
//				/*	String list = "";
//					
//					for(int k = 0 ; k < 5 ; k++)
//						list = list + "," + this.player.get(k).getChunkIndex();*/
//						
//				//	System.out.println("Attendo ! Id: " + this.getKey() + " Primi Elementi:" + list + "\n");
//					
//					//Incremento il numero di tentativi di trovare delle sequenze continue
//					//continuityTrialCount  ++;
//					
//					//return false;
//			}
//		}
//		
		//Se sono arrivato qua ho la continuita' del primo blocco di elementi e posso riprodurli
			
			//this.indexOfLastPlayedChunk = this.player.get(0).getChunkIndex();
					
			int initChunk = this.indexOfLastPlayedChunk; 
			
			//String list = "";	
//			if(this.getKey() == 820502299)
//			System.out.println("init 2 " + initChunk);
			
//			for(int k = 0 ; k < 5 ; k++)
//			{	
//				//list = list + "," + this.player.get(0).getChunkIndex();
//				//System.out.println("Rimuovo");			
//				int a = this.calculate_buffer_index(this.player.get(0));
//			
////				if(this.getKey() == 820502299)
////					System.out.println("first 2 " + this.player.get(0).getChunkIndex());
//				
//				int diff = this.player.get(0).getChunkIndex() - this.indexOfLastPlayedChunk;
//				
////				if(this.getKey() == 820502299)
////					System.out.println("diff 2 " + (diff ));
//				
//				if( this.player.get(0).getChunkIndex() - initChunk < 5 )
//				{	
//					if((diff) != 0)
//					{
//					this.deadlineNumber += (diff);
//					this.missingChunkNumber +=(diff);
//					this.indexOfLastPlayedChunk += ( diff);
//					}
//					this.indexOfLastPlayedChunk += (1);
//					this.getK_buffer().get(a).remove(this.player.get(0));
//					this.player.remove(0);				
//				}
////				if(this.getKey() == 820502299)
////					System.out.println("last play 2 " + this.indexOfLastPlayedChunk);
//				
//			}
			
			int count = 0;
			
			for(int j=0 ; j < 5; j++)
				if(!this.player.contains(new CoolStreamingVideoChunk(initChunk+j,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
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
//			if(this.getKey() == 1770719569)
//				{
//				System.out.println(this.deadlineNumber );
//				this.printFornitori();
//				this.printK_Buffer();
//				}
		}
		else {
		//	this.indexOfLastPlayedChunk = this.player.get(this.player.size()-1).getChunkIndex();
		//	System.out.println(this);
		//	this.deadlineNumber += (5 - this.player.size());
		//	this.missingChunkNumber += (5 - this.player.size());
			//TODO Cercare nuovi fornitori ???????
			//this.first = true;
									
			int initChunk = this.indexOfLastPlayedChunk;
			
			System.out.println("BUFFERING:.....");
			
		//	this.first = true;
			
			
//			System.out.println("last play 2 " + this.indexOfLastPlayedChunk + " deadline " + this.deadlineNumber);
//			this.printK_Buffer();
			//String list = "";	
//			if(this.getKey() == 820502299)
//			System.out.println("init 2 " + initChunk);
			
//			for(int k = 0 ; k < this.player.size() ; k++)
//			{	
//				//list = list + "," + this.player.get(0).getChunkIndex();
//				//System.out.println("Rimuovo");			
//				int a = this.calculate_buffer_index(this.player.get(0));
//			
////				if(this.getKey() == 820502299)
////					System.out.println("first 2 " + this.player.get(0).getChunkIndex());
//				
//				int diff = this.player.get(0).getChunkIndex() - this.indexOfLastPlayedChunk;
//				
////				if(this.getKey() == 820502299)
//					System.out.println("primo nel buffer " + (this.player.get(0).getChunkIndex() ));
//					//System.out.println("diff " + (this.player.get(0).getChunkIndex() - initChunk ));
//					
//				if( k==0 && diff > 5)
//				{
//					this.deadlineNumber += (5);
//					this.missingChunkNumber +=(5);
//					this.indexOfLastPlayedChunk += (5);
//				}
//				
//				
//				
//				if( this.player.get(0).getChunkIndex() - initChunk <= 5 )
//				{	
//					if((diff) != 0)
//					{
//					this.deadlineNumber += (diff);
//					this.missingChunkNumber +=(diff);
//					this.indexOfLastPlayedChunk += ( diff);
//					}
//					this.indexOfLastPlayedChunk += (1);
//					this.getK_buffer().get(a).remove(this.player.get(0));
//					this.player.remove(0);				
//				}
////				if(this.getKey() == 820502299)
////					System.out.println("last play 2 " + this.indexOfLastPlayedChunk);
//				
//			}

			
			int count = 0;
			
			for(int j=0 ; j < 5; j++)
				if(!this.player.contains(new CoolStreamingVideoChunk(initChunk+j,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
					{					
					 this.deadlineNumber += 1;
					 //System.out.println(this.deadlineNumber);
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
			
//			if(this.getKey() == 820502299)			

			this.indexOfLastPlayedChunk = initChunk + 5;
		
//			
			
//			for(int i=0;i<this.getK_value();i++)
//				{
//				if(this.getServerByPeer().get(i)!=null){
//					System.out.println( this.getServerByPeer().get(i).nodeDepth + this.getServerByPeer().get(i).getId());
//				this.getServerByPeer().get(i).printK_Buffer();}
//				else{
//					System.out.println( this.getServerByServer().get(i).getNodeDepth()+  this.getServerByServer().get(i).getId());
//				this.getServerByServer().get(i).printK_Buffer();}
//				}
		//	System.out.println( this + " " + this.getNodeDepth() + " PREGHIAMO!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
		//	System.out.println("last play 2 " + this.indexOfLastPlayedChunk + " deadline " + this.deadlineNumber);
			this.stop ++;
		}
}
	//Non posso riprodurre
	return false;
}



	private void choiseNewProvider(int i) {
		
		CoolStreamingPeer app = this.choiseNeighbor(i);			
		
		if(this.getK_buffer().get(i).size()==0 && this.getServerByPeer().get(i) != null && app!=null)
		{
		 this.stop++;	
		// this.first = true;	
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
					//|| 0.648/this.k_value>this.getServerByPeer().get(i).getUploadSpeed()/this.getServerByPeer().get(i).getActiveConnection())  
			  {
	//			if(this.getKey() ==1387824271)	
			//	System.out.println(this + " CAMBIO");	
		//		 System.out.println("Vecchio " + (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection());
				 
	//			 this.getServerByPeer().get(i).printK_Buffer();
//				 System.out.println("Nuovo " + (double)app.getUploadSpeed()/((double)app.getActiveConnection()+1));

				// System.out.println((double) (0.640/(double)this.k_value));				 				 
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
						//&& (app.getK_buffer().get(i).size() - app.getK_buffer().get(i).indexOf(new CoolStreamingVideoChunk(this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - i),((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (this.getServerByPeer().get(i).getK_buffer().get(i).size() - this.getServerByPeer().get(i).getK_buffer().get(i).indexOf(new CoolStreamingVideoChunk(this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - i),((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
						//|| 0.648/this.k_value>this.getServerByPeer().get(i).getUploadSpeed()/this.getServerByPeer().get(i).getActiveConnection())  
				  {
		//			if(this.getKey() ==1387824271)	
				//	System.out.println(this + " CAMBIO2");	
			//		 System.out.println("Vecchio " + (double)this.getServerByPeer().get(i).getUploadSpeed()/(double)this.getServerByPeer().get(i).getActiveConnection());
					 
		//			 this.getServerByPeer().get(i).printK_Buffer();
//					 System.out.println("Nuovo " + (double)app.getUploadSpeed()/((double)app.getActiveConnection()+1));

					// System.out.println((double) (0.640/(double)this.k_value));				 				 
					 this.getServerByPeer().get(i).getServedPeers2().get(i).remove(this);
					 this.getServerByPeer().get(i).setActiveConnection(this.getServerByPeer().get(i).getActiveConnection()-1);
					 this.getServerByPeer().set(i, null);
					 this.setDownloadActiveConnection(this.getDownloadActiveConnection()-1);
					 this.findProviderNodeFromLastSegment(Engine.getDefault().getVirtualTime(),i);
				  }
		}
}

	public ArrayList<CoolStreamingPeer> getServerByPeer() {
		return serverByPeer;
	}

	public void setServerPeers2(ArrayList<CoolStreamingPeer> serverPeers) {
		this.serverByPeer = serverPeers;
	}

	public ArrayList<ArrayList<CoolStreamingPeer>> getServedPeers2() {
		return servedPeers;
	}

	public void setServedPeers2(ArrayList<ArrayList<CoolStreamingPeer>> servedPeers2) {
		this.servedPeers = servedPeers2;
	}
		
	public void setProviderPeer(CoolStreamingPeer peer, int i)
	{
		this.getServerByPeer().set(i, peer);//.add(i, peer);
	}

	public ArrayList<CoolStreamingServerPeer> getServerByServer() {
		return serverByServer;
	}

	public void setServerByServer(ArrayList<CoolStreamingServerPeer> serverByServer) {
		this.serverByServer = serverByServer;
	}
	
	public void setProviderServer(CoolStreamingServerPeer peer, int i)
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
	private CoolStreamingPeer choiseNeighbor(int k) {
		

		//TODO ISP ordidare i vicini in base a isp e upload e chunk middle  --- if(this.incentiveBased)
		if(this.isIncentiveBased())
		{		
			if( this.getK_buffer().get(k).size() > 0 )
				this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex()));
			
			else 
				this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddleAndIsp(this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k)));		
		}
		
		else
		{
		if( this.getK_buffer().get(k).size() > 0 )
			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(this.getK_buffer().get(k).get(this.getK_buffer().get(k).size()-1).getChunkIndex()));
		
		else 
			this.getNeighbors().addAll(this.orderNeighborsByUploadAndChunkMiddle(this.calculateMiddleChunk() - (this.calculateMiddleChunk()%this.getK_value() - k)));
		}
		
		for(int i=0; i<this.getNeighbors().size();i++)
		{
		CoolStreamingPeer appSourceStreamingNode = (CoolStreamingPeer) this.getNeighbors().get(i);;	
			
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

	
//TODO RIFARLA CON PULSE PER SCEGLIERE A CHI COLLEGARSI
//	private Collection<? extends Peer> orderNeighbors(int k) {
// 
//	//	System.out.println("Ordino vicini");
//		
//		ArrayList<CoolStreamingPeer> appList = new ArrayList<CoolStreamingPeer>();
//		ArrayList<CoolStreamingPeer> appList2 = new ArrayList<CoolStreamingPeer>(); 
//		
//		//Ordinamento dei vicini in base agli incentivi
//		if(this.incentiveBased)
//		{
//			for(int i = 0 ; i < this.getNeighbors().size() ; i++)
//			 {
//				 CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(i);
//			  
//				 if(appList.size() == 0)
//					 appList.add(peer);						 
//					 
//				 else
//				 {
//					 for(int j = 0 ; j < appList.size(); j++)
//					 {		    
//						 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);		    					 
//						 
//						 if(this.getNeighborTrust().get(this.getNeighborTrust().indexOf(peer)).getTrust_value() >= this.getNeighborTrust().get(this.getNeighborTrust().indexOf(peerApp)).getTrust_value())
//						 {	 
//							 appList.add(j,peer);
//							 break;
//						 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
//						 else if( j == appList.size() - 1)
//						 {
//							 appList.add(peer);
//							 break;
//						 }
//					 }
//				 }
//			 }
//			 
//			 this.getNeighbors().clear();
//		}
//				
//		
//		//Ordinamento dei vicini in base al loro ultimo indice di chunk
//		else
//		{		 
//		 for(int i = 0 ; i < this.getNeighbors().size() ; i++)
//		 {
//			 CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(i);
//		  
//			 if(appList.size() == 0)
//				 appList.add(peer);						 
//				 
//			 else
//			 {
//				 for(int j = 0 ; j < appList.size(); j++)
//				 {		    
//					 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);		    					 
//					 
//					 
//						 if(
//							 (peer.getUploadSpeed()/(peer.getActiveConnection()+1) >= peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1)) )
//						 //if(peer.getUploadSpeed()/peer.getActiveConnection() >= peerApp.getUploadSpeed()/peerApp.getActiveConnection())	 
//					 {	 
//						 appList.add(j,peer);
//						 break;
//					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
//					 else if( j == appList.size() - 1)
//					 {
//						 appList.add(peer);
//						 break;
//					 }
//				 }
//			 }			 			 
//			 
//		 }
//		 
//		 for(int i = 0 ; i < appList.size() ; i++)
//		 {
//			 CoolStreamingPeer peer = (CoolStreamingPeer)appList.get(i);
//		  
//			 if(appList2.size() == 0)
//				 appList2.add(peer);						 
//				 
//			 else
//			 {
//				 for(int j = 0 ; j < appList2.size(); j++)
//				 {		    
//					 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList2.get(j);		    					 
//					 
//					 if(peer.getLastIndexOfChunk().get(k) >= peerApp.getLastIndexOfChunk().get(k)
//				        && peer.getUploadSpeed()/(peer.getActiveConnection()+1) == peerApp.getUploadSpeed()/(peerApp.getActiveConnection()+1))	 
//					 {	 
//						 appList2.add(j,peer);
//						 break;
//					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
//					 else if( j == appList2.size() - 1)
//					 {
//						 appList2.add(peer);
//						 break;
//					 }
//				 }
//			 }
//			 
//			 
//			 
//		 }
//		 this.getNeighbors().clear();
//		}
//		 return appList2;
//	}

	
	
	
	private Collection<? extends Peer> orderNeighborsByUploadAndChunkMiddle(int chunkToFind) {
		 
		//	System.out.println("Ordino vicini");
			
			ArrayList<CoolStreamingPeer> appList = new ArrayList<CoolStreamingPeer>();			
			ArrayList<CoolStreamingPeer> appList2 = new ArrayList<CoolStreamingPeer>();
			ArrayList<CoolStreamingPeer> appList3 = new ArrayList<CoolStreamingPeer>();
			
			 for(int i = 0 ; i < this.getNeighbors().size() ; i++)
			 {
				 CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(i);
			  				 
				if(peer.getK_buffer().get(chunkToFind%peer.getK_value()).contains(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
				 {
//					if(this.getKey() == 860184998)
//					 System.out.println("TROVATO " + chunkToFind);
				 if(appList.size() == 0 )
					 appList.add(peer);						 
					 
				 else
				 {
					 for(int j = 0 ; j < appList.size(); j++)
					 {		    
					   CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);		    					 
						 					
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
				 else
				 {
					 appList2.add(peer);
					// System.out.println("Noooooooooooooooooooooo");
				 }
			 }
			 
			 
			 
		for(int i = 0 ; i < appList.size() ; i++)
		 {
			 CoolStreamingPeer peer = (CoolStreamingPeer)appList.get(i);
		  
			 if(appList3.size() == 0)
				 appList3.add(peer);						 
				 
			 else
			 {
				 for(int j = 0 ; j < appList3.size(); j++)
				 {		    
					 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList3.get(j);		    					 
					 
					 if((peer.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peer.getK_buffer().get(chunkToFind%peer.getK_value()).indexOf(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (peerApp.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peerApp.getK_buffer().get(chunkToFind%peerApp.getK_value()).indexOf(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
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
			 
			this.getNeighbors().clear();
			
			if(appList3.size()>0)
			{	
			  appList3.addAll(appList3.size()-1, appList2);
			  return appList3;
			}
			 else return appList2;
		}
	
	
	private Collection<? extends Peer> orderNeighborsByUploadAndChunkMiddleAndIsp(int chunkToFind) {
		 
		//	System.out.println("Prima " + this.getNeighbors().size());
			
			ArrayList<CoolStreamingPeer> appList = new ArrayList<CoolStreamingPeer>();			
			ArrayList<CoolStreamingPeer> appList2 = new ArrayList<CoolStreamingPeer>();
			ArrayList<CoolStreamingPeer> appList3 = new ArrayList<CoolStreamingPeer>();
			
			 for(int i = 0 ; i < this.getNeighbors().size() ; i++)
			 {
				 CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(i);
			  				 
				if(peer.getK_buffer().get(chunkToFind%peer.getK_value()).contains(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
				 {
				 if(appList.size() == 0 )
					 appList.add(peer);						 
					 
				 else
				 {
					 for(int j = 0 ; j < appList.size(); j++)
					 {		    
					   CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);		    					 
						 					
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
			 CoolStreamingPeer peer = (CoolStreamingPeer)appList.get(i);
		  
			 if(appList3.size() == 0)
				 appList3.add(peer);						 
				 
			 else
			 {
				 for(int j = 0 ; j < appList3.size(); j++)
				 {		    
					 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList3.get(j);		    					 
					 
					 if(this.calculateGeographicDistance(this, peer) < this.calculateGeographicDistance(this, peerApp)
						//	 && (peer.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peer.getK_buffer().get(chunkToFind%peer.getK_value()).indexOf(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize()))) > (peerApp.getK_buffer().get(chunkToFind%peer.getK_value()).size() - peerApp.getK_buffer().get(chunkToFind%peerApp.getK_value()).indexOf(new CoolStreamingVideoChunk(chunkToFind,((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getChunkSize())))
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
			 // System.out.println("Dopo " + appList3.size());
			  return appList3;
			}
			
			 else {
			//	 System.out.println("Dopo " + appList2.size());
				 return appList2;
			 }
		}
	
	
	
//	public ArrayList<ArrayList<CoolStreamingVideoChunk>> sortSendBuffer() {
//		
//	//	System.out.println("Ordino buffer invio");
//		 ArrayList<ArrayList<CoolStreamingVideoChunk>> appList = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
//		 for(int i=0;i<this.k_value;i++)
//			 appList.add(i, new ArrayList<CoolStreamingVideoChunk>());
//		 
//		 
//		 for(int k=0 ; k<this.getK_value();k++)
//		 { 		 			 
//		 for(int i = 0 ; i < this.getSendBuffer().get(k).size() ; i++)
//		 {
//			 CoolStreamingVideoChunk chunkOriginal = (CoolStreamingVideoChunk)this.getSendBuffer().get(k).get(i);
//		  
//			 if(appList.get(k).size() == 0){
//				 appList.get(k).add(chunkOriginal);
//			 }
//				
//			 else
//			 {
//				 for(int j = 0 ; j < appList.get(k).size(); j++)
//				 {		    
//					 CoolStreamingVideoChunk chunkApp = (CoolStreamingVideoChunk)appList.get(k).get(j);		    					 
//					 
//					 // In base al numero di volte che l'ho inviato
//					 if(this.getNumOfSend(chunkOriginal) < this.getNumOfSend(chunkApp))
//					 {	 
//						 appList.get(k).add(j, chunkOriginal);//add(j,chunkOriginal);
//						 break;
//					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
//					 else if( j == appList.get(k).size() - 1)
//					 {
//						 appList.get(k).add(chunkOriginal);
//						 break;
//					 }
//				 }
//			 }
//		 }
//
//		 }
//		 
//		 return appList;
//		 
//	}
	
	//Ritorna il numero di volte che il chunk Ã¨ stato inviato
//	private int getNumOfSend(CoolStreamingVideoChunk chunk) {
//		
//		int index = this.calculate_buffer_index(chunk);
//		int pos = chunk.getChunkIndex()%this.k_value + chunk.getChunkIndex()/this.k_value - index;
//		
//		return this.getNumOfChunkSended().get(index).get(pos).getNumberOfSend();
//	}
	
	
	public ArrayList<ArrayList<CoolStreamingVideoChunk>> sortRequestBuffer() {
		 
	//	System.out.println("Ordino buffer richieste");
		 ArrayList<ArrayList<CoolStreamingVideoChunk>> appList = new ArrayList<ArrayList<CoolStreamingVideoChunk>>();
		 for(int i=0;i<this.k_value;i++)
			 appList.add(i, new ArrayList<CoolStreamingVideoChunk>());
		 
		 
		for(int k = 0 ; k < this.getK_value(); k++)
		{ 		 			 	
		 for(int i = 0 ; i < this.getRequestChunkBuffer().get(k).size() ; i++)
		 {
			 CoolStreamingVideoChunk chunkOriginal = (CoolStreamingVideoChunk)this.getRequestChunkBuffer().get(k).get(i);
		  
			 if(appList.get(k).size() == 0){				 
				 appList.get(k).add(chunkOriginal);
			 }
				
			 else
			 {
				 for(int j = 0 ; j < appList.get(k).size(); j++)
				 {		    
					 CoolStreamingVideoChunk chunkApp = (CoolStreamingVideoChunk)appList.get(k).get(j);		    					 
					 
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
	
	
	private int numNeighborsWithChunk(CoolStreamingVideoChunk chunk){
		
		int count = 0;
		int index = this.calculate_buffer_index(chunk);	
			
		for(int i=0; i<this.getNeighbors().size(); i++)
			if(((CoolStreamingPeer)this.getNeighbors().get(i)).getK_buffer().size()>0)
			if(((CoolStreamingPeer)this.getNeighbors().get(i)).getK_buffer().get(index).contains(chunk))
				count++;
				
		return count;
	}

	public ArrayList<ArrayList<CoolStreamingVideoChunk>> getSendBuffer() {
		return sendChunkBuffer;
	}

	public void setSendBuffer(ArrayList<ArrayList<CoolStreamingVideoChunk>> sendBuffer) {
		this.sendChunkBuffer = sendBuffer;
	}

//	public ArrayList<ArrayList<ChunkHash>> getNumOfChunkSended() {
//		return numOfChunkSended;
//	}
//
//	public void setNumOfChunkSended(ArrayList<ArrayList<ChunkHash>> numOfChunkSended) {
//		this.numOfChunkSended = numOfChunkSended;
//	}

	public ArrayList<ArrayList<CoolStreamingVideoChunk>> getRequestChunkBuffer() {
		return requestChunkBuffer;
	}

	public void setRequestChunkBuffer(
			ArrayList<ArrayList<CoolStreamingVideoChunk>> requestChunkBuffer) {
		this.requestChunkBuffer = requestChunkBuffer;
	}
	
	/**
	 * Funzione per richiedere in ordine i chunk ai fornitori 
	 * @param k 
	 */
	public void requestChunk(){
		
	//	System.out.println("richiedo chunk 1");
		
//		if(this.getKey() == 531129312)
//		{
//			System.out.println("AAAAAAAAAAAAAAAAaaa");
//		for(int i = 0; i < this.getRequestChunkBuffer().size() ; i++)		
//			this.printVideoBuffer(this.getRequestChunkBuffer().get(i));
//		}
//		
//		if(this.getKey() == 531129312)
//		System.out.println("Entro");
		
		ArrayList<ArrayList<CoolStreamingVideoChunk>> app = null;
		app = this.sortRequestBuffer();
		
		for(int i=0; i<this.k_value; i++)
			for(int j=0; j<app.get(i).size(); j++)
			{
//				if(this.getKey() == 639036909)
//					System.out.println("Chiedo " + app.get(i).get(j).getChunkIndex() + " da " + app.get(i).get(j).getSourceNode());
//				
				if(this.getServerByPeer().get(i) != null)
					this.getServerByPeer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
				else 
					if(this.getServerByServer().get(i) != null)
						this.getServerByServer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
			}
		
		this.getRequestChunkBuffer().clear();
		
		for(int i=0;i<this.getK_value();i++)
			this.requestChunkBuffer.add(i,new ArrayList<CoolStreamingVideoChunk>());
		
//		for(int i = 0; i < app.size() ; i++)
//			this.printVideoBuffer(app.get(i));
	}

	
	public void requestChunk2(int i){
		
		//System.out.println("richiedo chunk 2");
//		if(this.getKey() == 531129312)
//		{
//			System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBbbbbbbbbbb");
//			for(int j = 0; j < this.getRequestChunkBuffer().size() ; j++)
//			this.printVideoBuffer(this.getRequestChunkBuffer().get(j));
//		}
//		if(this.getKey() == 531129312)
//		System.out.println("Entro");
		
		ArrayList<ArrayList<CoolStreamingVideoChunk>> app = null;
		app = this.sortRequestBuffer();
		
		//for(int i=0; i<this.k_value; i++)
			for(int j=0; j<app.get(i).size(); j++)
			{
//				if(this.getKey() == 639036909)
//					System.out.println("Chiedo " + app.get(i).get(j).getChunkIndex() + " da " + app.get(i).get(j).getSourceNode());
				
				if(this.getServerByPeer().get(i) != null)
					this.getServerByPeer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
				else 
					if(this.getServerByServer().get(i) != null)
						this.getServerByServer().get(i).getSendBuffer().get(i).add(app.get(i).get(j));
			}
		
		this.getRequestChunkBuffer().get(i).clear();
		
		//for(int l=0;l<this.getK_value();l++)
			this.requestChunkBuffer.add(i,new ArrayList<CoolStreamingVideoChunk>());
		
//		for(int i = 0; i < app.size() ; i++)
//			this.printVideoBuffer(app.get(i));
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
			 CoolStreamingPeer peer = (CoolStreamingPeer)this.getNeighbors().get(i);
				for(int k = 0 ; k < peer.k_value ; k++ )
					for( int j = 0; j < peer.getK_buffer().get(k).size(); j++)												
					{
						//System.out.print(peer.getK_buffer().get(k).get(j).getChunkIndex() + " ");
						
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
	
	public boolean isIncentiveBased() {
		return incentiveBased;
	}

	public void setIncentiveBased(boolean incentiveBased) {
		this.incentiveBased = incentiveBased;
	}

//	public ArrayList<NeighborTrust> getNeighborTrust() {
//		return neighborTrust;
//	}
//
//	public void setNeighborTrust(ArrayList<NeighborTrust> neighborTrust) {
//		this.neighborTrust = neighborTrust;
//	}

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
	
//	private ArrayList<Peer> orderNeighbors(CoolStreamingPeer associatedStreamingNode) {
//
//		ArrayList<Node> nodes = Engine.getDefault().getNodes();
//		
//		ArrayList<Peer> appList = new ArrayList<Peer>();		
//				 
//		for(int i = 1 ; i < nodes.size() ; i++)
//		 {
//			 CoolStreamingPeer peer = (CoolStreamingPeer)nodes.get(i);
//		  
//			 if(appList.size() == 0){				 
//				 appList.add(peer);
//			 }
//						
//			 else
//			 {
//				 for(int j = 0 ; j < appList.size(); j++)
//				 {		    
//					 CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);	    					 
//					 
//					 // Ordino in base alla presenza del chunk tra i miei vicini
//					 if( calculateGeographicDistance(associatedStreamingNode,peer) < calculateGeographicDistance(associatedStreamingNode,peerApp)/* peer + vicino di peerApp*/)
//					 {	 					
//						 appList.add(j, peer);//add(j,chunkOriginal);
//						 break;
//					 }// Se alla fine non ho trovato un elemento minore aggiungo l'elemento in coda
//					 else if( j == appList.size() - 1)
//					 {					
//						 appList.add(peer);
//						 break;
//					 }
//				 }
//			 }
//		  }
//		 
//		return appList;
//	}

	
	private int calculateGeographicDistance(CoolStreamingPeer myPeer, CoolStreamingPeer otherPeer) {
		
		if(myPeer.getIsp() == otherPeer.getIsp() 
				&& myPeer.getCity() == otherPeer.getCity())
			return 0;
		
		if(myPeer.getIsp() == otherPeer.getIsp() 
				&& myPeer.getCity() != otherPeer.getCity())
			return 1;
		
		if(myPeer.getIsp() != otherPeer.getIsp() 
				&& myPeer.getCity() == otherPeer.getCity())
			return 2;
		
		if(myPeer.getIsp() != otherPeer.getIsp() 
				&& myPeer.getCity() != otherPeer.getCity())
			return 3;
			
		return 4;
	}

	
}

