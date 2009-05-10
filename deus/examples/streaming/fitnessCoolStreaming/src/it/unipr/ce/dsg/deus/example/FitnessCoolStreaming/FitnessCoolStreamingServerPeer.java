package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * <p>
 * EnergyPeers are characterized by one kind of consumable resource: energy.
 * Moreover, each EnergyPeer has a chromosome, i.e.
 * a set of parameters whose values are randomly initialized when the
 * RevolPeer is instantiated, and may change during its lifetime, depending
 * on external events. The EnergyPeer keeps track of the number of sent queries (Q)
 * and of the number of query hits (QH). The query hit ratio (QHR = QH/Q) is 
 * initialized to 0.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 * */
public class FitnessCoolStreamingServerPeer extends Peer {

	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String CHUNK_SIZE = "chunkSize";
	private static final String K_VALUE = "k_value";
	private static final String ISP = "isp"; 
	private static final String CITY = "city"; 
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private int chunkSize = 0;	
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int nodeDepth = 0;
	private int k_value = 0;
	private int isp = 0;
	private int city = 0;
	
	private double missingChunkNumber   = 0.0; 
	private double totalChunkReceived   = 0.0; 
	private double duplicateChunkNumber = 0.0;
	private double totalDeadline = 0.0;
	private double disconnectedNodes = 0;
	private float totalstartUpTime = 0;
	private double connectionChanged = 0.0;
	private int batteryDisconnectionCount = 0;
	
	private ArrayList<Double> istogrammaBatteria = new ArrayList<Double>();
	
	//Array per le statistiche dei tempi di ricezione dei nodi che si sono disconnessi
	private ArrayList<Float> arrivalTimesPcNode = new ArrayList<Float>();
	private ArrayList<Float> arrivalTimesPcNodeHigh = new ArrayList<Float>();
	private ArrayList<Float> arrivalTimesMobile3GNode = new ArrayList<Float>();
	//K-Buffer
	private ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> k_buffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
	private ArrayList<ArrayList<FitnessCoolStreamingPeer>> servedPeers = new ArrayList<ArrayList<FitnessCoolStreamingPeer>>();
	
	
	//Lista contenente le richieste di chunk
	private ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> sendBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
	//private ArrayList<ArrayList<ChunkHash>> numOfChunkSended = new ArrayList<ArrayList<ChunkHash>>();  
	
	public void addBatteryForBarChart( double battery)
	{
		this.istogrammaBatteria.add(battery);
	}
	
	public double getTotalDeadline() {
		return totalDeadline;
	}

	public void setTotalDeadline(double totalDeadine) {
		this.totalDeadline = totalDeadine;
	}

	//private ArrayList<FitnessCoolStreamingPeer> servedPeers = new ArrayList<FitnessCoolStreamingPeer>();
	private ArrayList<FitnessCoolStreamingVideoChunk> videoResource = new ArrayList<FitnessCoolStreamingVideoChunk>();
	private boolean init_bool = false;
	
	
	public FitnessCoolStreamingServerPeer(String id, Properties params, ArrayList<Resource> resources)
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
		
		if (params.containsKey(K_VALUE))
			k_value = Integer.parseInt(params.getProperty(K_VALUE));		
		
		if (params.containsKey(ISP))
			isp = Integer.parseInt(params.getProperty(ISP));		
		
		if (params.containsKey(CITY))
			city = Integer.parseInt(params.getProperty(CITY));
		
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
		
		FitnessCoolStreamingServerPeer clone = (FitnessCoolStreamingServerPeer) super.clone();
		
		clone.activeConnection = this.activeConnection;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.servedPeers = new ArrayList<ArrayList<FitnessCoolStreamingPeer>>();
	//	clone.numOfChunkSended = new ArrayList<ArrayList<ChunkHash>>();
	//	clone.servedPeers = this.servedPeers;
		clone.videoResource = this.videoResource;
		clone.k_buffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		clone.nodeDepth = 0;
		clone.init_bool = this.init_bool;
		clone.sendBuffer = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();		
		
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
	public void sendVideoChunk(FitnessCoolStreamingPeer clientNode,FitnessCoolStreamingVideoChunk newResource, float triggeringTime){
		
		//Verifico se devo degradare la velocitˆ di download del nodo client in base alle
		//sue connessioni in ingresso attive
		double clientDownloadSpeed = 0.0;
		if( clientNode.getDownloadActiveConnection() > 0 )
			clientDownloadSpeed = clientNode.getDownloadSpeed() / (double)clientNode.getDownloadActiveConnection();
		else
			clientDownloadSpeed = clientNode.getDownloadSpeed();
		
		
		float time = triggeringTime + nextChunkArrivalTime(this.getUploadSpeed(),clientDownloadSpeed,newResource);
		
		int index = this.calculate_buffer_index(newResource);
		int pos = newResource.getChunkIndex()%this.k_value + newResource.getChunkIndex()/this.k_value - index;
		
		//this.getNumOfChunkSended().get(index).set(pos,new ChunkHash(newResource.getChunkIndex(),this.getNumOfChunkSended().get(index).get(pos).getNumberOfSend()+ 1));
		
		//System.out.println("Server Invia: " + newResource.getChunkIndex() + " At: " + time + " To:" + clientNode.getKey());
		
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
	 * @return
	 */
	private float nextChunkArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, FitnessCoolStreamingVideoChunk chunk) {
		
		double time = 0.0;
		double minSpeed = Math.min(  (providerUploadSpeed  / (double) this.getActiveConnection()) , clientDownloadSpeed);

		double chunkMbitSize = (double)( (double) chunk.getChunkSize() / 1024.0 );
		time = (chunkMbitSize / minSpeed);
		
		float floatTime = (float)( time + Engine.getDefault().getSimulationRandom().nextDouble()*time);

		float sec=0;
				
		
		//TODO ISP aggiungere latenza in base all'ISP e città
		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2);
		}
		
		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 5;
		}
		
		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 1;
		}
		
		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
		{
			// IMPOSTO UNA LATENZA
			sec = Engine.getDefault().getSimulationRandom().nextInt(2) + 6;
		}
		
//		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
//				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
//		{
//			// IMPOSTO UNA LATENZA
//			sec = Engine.getDefault().getSimulationRandom().nextInt(2);
//		}
//		
//		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
//				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
//		{
//			// IMPOSTO UNA LATENZA
//			sec = Engine.getDefault().getSimulationRandom().nextInt(2);
//		}
//		
//		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
//				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() == ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
//		{
//			// IMPOSTO UNA LATENZA
//			sec = Engine.getDefault().getSimulationRandom().nextInt(7) + 2;
//		}
//		
//		if(((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getIsp() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getIsp()
//				&& ((FitnessCoolStreamingServerPeer)chunk.getSourceNode()).getCity() != ((FitnessCoolStreamingPeer)chunk.getDestNode()).getCity())
//		{
//			// IMPOSTO UNA LATENZA
//			sec = Engine.getDefault().getSimulationRandom().nextInt(7) + 2;
//		}
		
		return (float) (floatTime*(20)+sec);

	}
		
	public void addNewVideoResource(FitnessCoolStreamingVideoChunk newVideoRes){
		
		//Inserisco il chunk nel k_buffer appropriato e lo ordino
		int index = this.calculate_buffer_index(newVideoRes);
		this.k_buffer.get(index).add(newVideoRes);		
		
		this.videoResource.add(newVideoRes);

	}
	
	
	public void printK_Buffer()
	{
		String el = "";
		for(int i=0; i<this.k_value; i++)
		 for(int j=0; j<this.k_buffer.get(i).size(); j++)
		 { el = el + " " + this.k_buffer.get(i).get(j).getChunkIndex(); 
			 if(j==this.k_buffer.get(i).size()-1)
				 { System.out.println("buffer " + i + " elementi " + el); el = ""; }
			 
		 }
	}
	
	public void removeActiveConnection(){
		
		if( this.activeConnection >= 1 )
		 this.activeConnection--;
		else
			System.out.println("ERRORE SERVER PEER ! Connessioni Attive = 0 non posso decrementare");
	}
	
	public int calculate_buffer_index(FitnessCoolStreamingVideoChunk chunk)
	{	 	
	 return (chunk.getChunkIndex()%this.getK_value());	
	}
	
	public void init()
	{
		for(int i=0;i<this.getK_value();i++)
		{
			this.getK_buffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
			this.getServedPeers2().add(i,new ArrayList<FitnessCoolStreamingPeer>());
			this.getSendBuffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
		}
		
		new FitnessChunkHash(2*this.k_value + 1,0);
		
		this.init_bool  = true;
	}
	
	public void addActiveConnection(){
		
		if( this.activeConnection < this.maxAcceptedConnection )
		 this.activeConnection++;
		else
			System.out.println("ERRORE SERVER PEER ! Connessioni Attive = "+ this.maxAcceptedConnection  +" non posso incrementare");
	}
	
	public FitnessCoolStreamingVideoChunk getLastChunk() {
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

	public ArrayList<FitnessCoolStreamingVideoChunk> getVideoResource() {
		return videoResource;
	}

	public void setVideoResource(ArrayList<FitnessCoolStreamingVideoChunk> videoResource) {
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

	public ArrayList<Float> getArrivalTimesPcNodeHigh() {
		return arrivalTimesPcNodeHigh;
	}

	public void setArrivalTimesPcNodeHigh(
			ArrayList<Float> arrivalTimesPcNodeHigh) {
		this.arrivalTimesPcNodeHigh = arrivalTimesPcNodeHigh;
	}

	public ArrayList<Float> getArrivalTimesMobile3GNode() {
		return arrivalTimesMobile3GNode;
	}

	public void setArrivalTimesMobile3GNode(
			ArrayList<Float> arrivalTimesMobile3GNode) {
		this.arrivalTimesMobile3GNode = arrivalTimesMobile3GNode;
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

	public ArrayList<ArrayList<FitnessCoolStreamingPeer>> getServedPeers2() {
		return servedPeers;
	}

	public void setServedPeers2(ArrayList<ArrayList<FitnessCoolStreamingPeer>> servedPeers2) {
		this.servedPeers = servedPeers2;
	}

	public boolean isInit_bool() {
		return init_bool;
	}

	public void setInit_bool(boolean init_bool) {
		this.init_bool = init_bool;
	}

	public ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> getSendBuffer() {
		return sendBuffer;
	}

	public void setSendBuffer(
			ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> sendBuffer) {
		this.sendBuffer = sendBuffer;
	}

	public float getStartUpTime() {
		return totalstartUpTime;
	}

	public void setStartUpTime(float f) {
		this.totalstartUpTime = f;
		
	}
	public void printVideoBuffer(ArrayList<FitnessCoolStreamingVideoChunk> arrayList)
	{
		String my ="";
		for(int j = 0 ; j < arrayList.size(); j++)
			my = my + " " + arrayList.get(j).getChunkIndex();
				
		System.out.println("Id: "+ this.getKey() + " " + my);
	}

	public int getIsp() {
		return isp;
	}

	public void setIsp(int isp) {
		this.isp = isp;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public double getConnectionChanged() {
		return connectionChanged;
	}

	public void setConnectionChanged(double connectionChanged) {
		this.connectionChanged = connectionChanged;
	}
	
	public void addConnectionChanged(){
		this.connectionChanged ++;
	}
	
	
	
	public void incrementBatteryDisconnectionCount(){
		this.batteryDisconnectionCount ++;
	}

	public int getBatteryDisconnectionCount() {
		return batteryDisconnectionCount;
	}

	public void setBatteryDisconnectionCount(int batteryDisconnectionCount) {
		this.batteryDisconnectionCount = batteryDisconnectionCount;
	}

	public ArrayList<Double> getIstogrammaBatteria() {
		return istogrammaBatteria;
	}

	public void setIstogrammaBatteria(ArrayList<Double> istogrammaBatteria) {
		this.istogrammaBatteria = istogrammaBatteria;
	}
}
