package it.unipr.ce.dsg.deus.example.coolStreaming;

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
 */
public class CoolStreamingServerPeer extends Peer {

	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String CHUNK_SIZE = "chunkSize";
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private int chunkSize = 0;	
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	
	private ArrayList<CoolStreamingPeer> servedPeers = new ArrayList<CoolStreamingPeer>();
	private ArrayList<CoolStreamingVideoChunk> videoResource = new ArrayList<CoolStreamingVideoChunk>();
	
	public CoolStreamingServerPeer(String id, Properties params, ArrayList<Resource> resources)
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
		
	}
	
	public Object clone() {
		
		CoolStreamingServerPeer clone = (CoolStreamingServerPeer) super.clone();
		
		clone.activeConnection = this.activeConnection;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.servedPeers = this.servedPeers;
		clone.videoResource = this.videoResource;
		clone.chunkSize = this.chunkSize;
		
		return clone;
	}

	public void addNewVideoResource(CoolStreamingVideoChunk newVideoRes){
		
		this.videoResource.add(newVideoRes);
		
		/*
		for(int i = 0; i<this.getServedPeers().size(); i++ ){
			this.getServedPeers().get(i).addNewVideoResource(newVideoRes);
		}
		*/
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
	
	public void addServedPeer(CoolStreamingPeer peer){
		if(!this.getServedPeers().contains(peer) && !this.equals(peer))
			this.getServedPeers().add(peer);
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

	public ArrayList<CoolStreamingPeer> getServedPeers() {
		return servedPeers;
	}

	public void setServedPeers(ArrayList<CoolStreamingPeer> servedPeers) {
		this.servedPeers = servedPeers;
	}

	public ArrayList<CoolStreamingVideoChunk> getVideoResource() {
		return videoResource;
	}

	public void setVideoResource(ArrayList<CoolStreamingVideoChunk> videoResource) {
		this.videoResource = videoResource;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	public  CoolStreamingVideoChunk getLastChunk() {
		return this.getVideoResource().get(this.getVideoResource().size()-1);
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
	
}
