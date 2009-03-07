package it.unipr.ce.dsg.deus.example.simpleDataDriven;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * 
 * @author Picone Marco
 * 
 */
public class ServerPeer extends Peer {

	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String CHUNK_SIZE = "chunkSize";
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private int chunkSize = 0;	
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int nodeDepth = 0;
	
	private double missingChunkNumber   = 0.0; 
	private double totalChunkReceived   = 0.0; 
	private double duplicateChunkNumber = 0.0;
	private double totalDeadine = 0.0;
	private double disconnectedNodes = 0;

	//Array per le statistiche dei tempi di ricezione dei nodi che si sono disconnessi
	private ArrayList<Float> arrivalTimesPcNode = new ArrayList<Float>();
	private ArrayList<Float> arrivalTimesMobileWifiNode = new ArrayList<Float>();
	private ArrayList<Float> arrivalTimesMobile3GNode = new ArrayList<Float>();
	
	
	
	public double getTotalDeadine() {
		return totalDeadine;
	}

	public void setTotalDeadine(double totalDeadine) {
		this.totalDeadine = totalDeadine;
	}

	private ArrayList<StreamingPeer> servedPeers = new ArrayList<StreamingPeer>();
	private ArrayList<VideoChunk> videoResource = new ArrayList<VideoChunk>();
	
	public ServerPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(CHUNK_SIZE))
			chunkSize = Integer.parseInt(params.getProperty(CHUNK_SIZE));
		
		if (params.containsKey(UPLOAD_SPEED))
			uploadSpeed = Double.parseDouble(params.getProperty(UPLOAD_SPEED));
	
		if (params.containsKey(DOWNLOAD_SPEED))
			downloadSpeed = Double.parseDouble(params.getProperty(DOWNLOAD_SPEED));	
		
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
		
		this.setConnected(true);
		
	}
	
	
	
	public Object clone() {
		
		ServerPeer clone = (ServerPeer) super.clone();
		
		clone.activeConnection = this.activeConnection;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.servedPeers = this.servedPeers;
		clone.videoResource = this.videoResource;
		clone.nodeDepth = 0;
		
		return clone;
	}

	/**
	 * Invia al nodo client di destinazione, la porzione video newResource partendo dal tempo 
	 * triggerTime
	 * 
	 * @param clientNode
	 * @param newResource
	 * @param triggeringTime
	 */
	public void sendVideoChunk(StreamingPeer clientNode,VideoChunk newResource, float triggeringTime){
		
		//Verifico se devo degradare la velocitˆ di download del nodo client in base alle
		//sue connessioni in ingresso attive
		double clientDownloadSpeed = 0.0;
		if( clientNode.getDownloadActiveConnection() > 0 )
			clientDownloadSpeed = clientNode.getDownloadSpeed() / (double)clientNode.getDownloadActiveConnection();
		else
			clientDownloadSpeed = clientNode.getDownloadSpeed();
		
		
		float time = triggeringTime + nextChunkArrivalTime(this.getUploadSpeed(),clientDownloadSpeed,newResource);
			
		//System.out.println("Server Invia: " + newResource.getChunkIndex() + " At: " + time + " To:" + clientNode.getKey());
		
		StreamingPeerNewVideoResourceEvent newPeerResEvent = (StreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(StreamingPeerNewVideoResourceEvent.class,time);
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
	 * @return
	 */
	private float nextChunkArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, VideoChunk chunk) {
		
		double time = 0.0;
		double minSpeed = Math.min(  (providerUploadSpeed  / (double) this.getActiveConnection()) , clientDownloadSpeed);
		double chunkMbitSize = (double)( (double) chunk.getChunkSize() / 1024.0 );
		time = (chunkMbitSize / minSpeed);
		
		float floatTime = expRandom((float)time);
		
		return floatTime*10;
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}
	
	public void addNewVideoResource(VideoChunk newVideoRes){
		
		this.videoResource.add(newVideoRes);

	}
	
	public void removeActiveConnection(){
		
		if( this.activeConnection >= 1 )
		 this.activeConnection--;
		else
			System.out.println("ERRORE SERVER PEER ! Connessioni Attive = 0 non posso decrementare");
	}
	
	public void addActiveConnection(){
		
		if( this.activeConnection < this.maxAcceptedConnection )
		 this.activeConnection++;
		else
			System.out.println("ERRORE SERVER PEER ! Connessioni Attive = "+ this.maxAcceptedConnection  +" non posso incrementare");
	}
	
	public void addServedPeer(StreamingPeer peer){
		if(!this.getServedPeers().contains(peer) && !this.equals(peer))
			this.getServedPeers().add(peer);
	}
	
	public VideoChunk getLastChunk() {
		return this.getVideoResource().get(this.getVideoResource().size()-1);
	}
	
	public void setActiveConnection(int activeConnection) {
		this.activeConnection = activeConnection;
	}


	public static String getMAX_ACCEPTED_CONNECTION() {
		return MAX_ACCEPTED_CONNECTION;
	}

	public int getMaxAcceptedConnection() {
		return maxAcceptedConnection;
	}

	public int getActiveConnection() {
		return activeConnection;
	}

	public ArrayList<StreamingPeer> getServedPeers() {
		return servedPeers;
	}

	public void setServedPeers(ArrayList<StreamingPeer> servedPeers) {
		this.servedPeers = servedPeers;
	}

	public ArrayList<VideoChunk> getVideoResource() {
		return videoResource;
	}

	public void setVideoResource(ArrayList<VideoChunk> videoResource) {
		this.videoResource = videoResource;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public double getUploadSpeed() {
		return uploadSpeed;
	}

	public void setUploadSpeed(double uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}

	public double getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(double downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	public int getNodeDepth() {
		return nodeDepth;
	}

	public void setNodeDepth(int nodeDepth) {
		this.nodeDepth = nodeDepth;
	}

	public double getMissingChunkNumber() {
		return missingChunkNumber;
	}

	public void setMissingChunkNumber(double missingChunkNumber) {
		this.missingChunkNumber = missingChunkNumber;
	}

	public double getTotalChunkReceived() {
		return totalChunkReceived;
	}

	public void setTotalChunkReceived(double totalChunkReceived) {
		this.totalChunkReceived = totalChunkReceived;
	}

	public double getDuplicateChunkNumber() {
		return duplicateChunkNumber;
	}

	public void setDuplicateChunkNumber(double duplicateChunkNumber) {
		this.duplicateChunkNumber = duplicateChunkNumber;
	}

	public double getDisconnectedNodes() {
		return disconnectedNodes;
	}

	public void setDisconnectedNodes(double disconnectedNodes) {
		this.disconnectedNodes = disconnectedNodes;
	}
	
	public void addDisconnectedNode(){
		this.disconnectedNodes++;
	}

	public ArrayList<Float> getArrivalTimesPcNode() {
		return arrivalTimesPcNode;
	}

	public void setArrivalTimesPcNode(ArrayList<Float> arrivalTimesPcNode) {
		this.arrivalTimesPcNode = arrivalTimesPcNode;
	}

	public ArrayList<Float> getArrivalTimesMobileWifiNode() {
		return arrivalTimesMobileWifiNode;
	}

	public void setArrivalTimesMobileWifiNode(
			ArrayList<Float> arrivalTimesMobileWifiNode) {
		this.arrivalTimesMobileWifiNode = arrivalTimesMobileWifiNode;
	}

	public ArrayList<Float> getArrivalTimesMobile3GNode() {
		return arrivalTimesMobile3GNode;
	}

	public void setArrivalTimesMobile3GNode(
			ArrayList<Float> arrivalTimesMobile3GNode) {
		this.arrivalTimesMobile3GNode = arrivalTimesMobile3GNode;
	}
}
