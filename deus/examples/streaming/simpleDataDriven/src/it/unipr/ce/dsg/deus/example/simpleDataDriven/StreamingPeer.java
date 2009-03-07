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
public class StreamingPeer extends Peer {

	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String VIDEO_RESOURCE_BUFFER_LIMIT = "videoResourceBufferLimit";
	private static final String FITNESS_SORT = "fitnessSort"; 
	private static final String MAX_PARTNERS_NUMBER = "maxPartnersNumber"; 
	
	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";
	public static final String G2 = "2g"; 
	public static final String GPRS = "gprs";
	
	private double battery = 0.0;
	private String connectionType = "";
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int videoResourceBufferLimit = 10;
	private double fitnessValue = 0.0;
	private boolean fitnessSort = false;
	private int maxPartnersNumber = 20;
	private int downloadActiveConnection = 0;
	private int missingChunkNumber = 0; 
	private int totalChunkReceived = 0; 
	private int indexOfLastReceivedChunk;
	private int duplicateChunkNumber = 0;
	private int deadlineNumber = 0;

	private int nodeDepth = 0;

	private long time1;
	private long time2;
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private double onlineTime = 0.0;
	
	private StreamingPeer sourceStreamingNode = null;
	private ServerPeer serverNode = null;
	
	
	private ArrayList<StreamingPeer> servedPeers = new ArrayList<StreamingPeer>();
	private ArrayList<VideoChunk> videoResource = new ArrayList<VideoChunk>();
	private ArrayList<VideoChunk> videoPlayBuffer = new ArrayList<VideoChunk>();
	private ArrayList<Integer> neededChunk = new ArrayList<Integer>();
	
	//Array per le statistiche dei tempi di ricezione
	private ArrayList<Float> arrivalTimes = new ArrayList<Float>();
	private int continuityTrialCount = 0;
	private Integer initChunk = 0;
	
	public StreamingPeer(String id, Properties params, ArrayList<Resource> resources)
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
		
		if (params.containsKey(FITNESS_SORT))
			fitnessSort = Boolean.parseBoolean(params.getProperty(FITNESS_SORT));
		
		if (params.containsKey(MAX_PARTNERS_NUMBER))
			maxPartnersNumber = Integer.parseInt(params.getProperty(MAX_PARTNERS_NUMBER));
		
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
	
		StreamingPeer clone = (StreamingPeer) super.clone();

		clone.activeConnection = this.activeConnection;
		clone.battery = this.battery;
		clone.connectionType = this.connectionType;
		clone.fitnessSort = this.fitnessSort;
		clone.fitnessValue = this.fitnessValue;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.maxPartnersNumber = this.maxPartnersNumber;
		clone.onlineTime = 0;
		clone.time1 = 0;
		clone.time2 = 0;
		clone.servedPeers = new ArrayList<StreamingPeer>();;
		clone.serverNode = this.serverNode;
		clone.sourceStreamingNode = this.sourceStreamingNode;
		clone.videoResource = new ArrayList<VideoChunk>(); 
		clone.arrivalTimes = new ArrayList<Float>(); 
		clone.videoResourceBufferLimit  = this.videoResourceBufferLimit;
		clone.nodeDepth = 0;
		clone.missingChunkNumber = 0;
		clone.videoPlayBuffer = new ArrayList<VideoChunk>();
		clone.totalChunkReceived = 0;
		clone.duplicateChunkNumber = 0;
		clone.neededChunk = new ArrayList<Integer>();
		clone.deadlineNumber = 0;
		clone.isConnected = true;
		clone.initChunk = 0;
		
		return clone;
	}
	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	public void updateParentsList(float triggeringTime){
		
		int startListNumber = this.neighbors.size();
		
		//Controllo se il nodo non ha piu' un fornitore
		if( this.sourceStreamingNode != null && !this.sourceStreamingNode.isConnected()){

			System.out.println("NON HO UN FORNITORE:" + this.getKey() + " - " + this.getNeighbors().size());
			
			this.findProviderNodeFromLastSegment(triggeringTime);
		}
		
		//Aggiorno la fitness del nodo
		this.updateFitnessValue();
		
		//Attraverso la lista dei nodi
		for(int i = 0; i < this.getNeighbors().size(); i++)
			if(checkDisconnection((StreamingPeer) this.getNeighbors().get(i)))
				this.getNeighbors().remove(i);
		
		//System.out.println(this.getNeighbors().size() + "/" + Engine.getDefault().getNodes().size());
		//Se qualche nodo e' stato rimosso dalla lista dei vicini
		if( this.getNeighbors().size() < this.getMaxPartnersNumber() ){
			
			//Cerco tra i miei vicini qualche nuovo contatto
			for(int index = 0; index < this.neighbors.size() ; index++)
			{
	
				StreamingPeer peer = (StreamingPeer)this.neighbors.get(index);
				
				for(int j = 0; j < peer.getNeighbors().size(); j++ ){
				
					StreamingPeer peerApp = (StreamingPeer)peer.neighbors.get(j);
					
					if(
							peerApp.isConnected() //Se il peer e' connesso
							&& !this.neighbors.contains(peerApp) //Se non e' gia' tra i miei vicni
							&& !peerApp.equals(this) //Se non sono io
							&& !this.servedPeers.contains(peerApp) //Se non lo sto servendo
					  )
						this.addNeighbor(peerApp);
					
					//Se ho raggiunto il limite massimo esco
					if(this.neighbors.size() == this.maxPartnersNumber)
						break;
				
				}
				
				//Se ho raggiunto il limite massimo esco
				if(this.neighbors.size() == this.maxPartnersNumber)
					break;
				
			}
			
		}
		
		//Riordino la lista dei vicini in base alla loro fitness  
		if( this.isFitnessSort() )
			this.sortParentsNodes();
		
		int endListNumber = this.neighbors.size();
		
		if( endListNumber > this.maxPartnersNumber )
			System.out.println("ERRORE LISTA TROPPO GRANDE !!!! ("+ endListNumber + "/" + this.maxPartnersNumber + ")");
		
		
		
	}
	
	public void disconnection(float triggeringTime){
		
		//Imposto il nodo come disconnesso
		this.setConnected(false);
		
		//Comunico le mie statistiche al Server
		ServerPeer server = (ServerPeer)Engine.getDefault().getNodes().get(0);
		server.setMissingChunkNumber(server.getMissingChunkNumber() + this.missingChunkNumber);
		server.setTotalChunkReceived(server.getTotalChunkReceived() + this.totalChunkReceived);
		server.setDuplicateChunkNumber(server.getDuplicateChunkNumber() + this.duplicateChunkNumber );
		server.setTotalDeadine(server.getTotalDeadine() + this.deadlineNumber);
		
		//Aggiungo i tempi di arrivo di questo nodo al server per avere delle statistiche complete
		if(this.getId().equals("pcNode"))
			server.getArrivalTimesPcNode().addAll(this.arrivalTimes);

		if(this.getId().equals("mobileNode"))
			server.getArrivalTimesMobileWifiNode().addAll(this.arrivalTimes);
		
		if(this.getId().equals("mobile3GNode"))
			server.getArrivalTimesMobile3GNode().addAll(this.arrivalTimes);
		
		//Incremento il numero di disconnessioni
		server.addDisconnectedNode();
		
		//Mi tolgo dal mio fornitore
		if(this.serverNode != null)
			this.serverNode.getServedPeers().remove(this);
		
		if(this.sourceStreamingNode != null)
			this.sourceStreamingNode.getServedPeers().remove(this);
		
		//Azzero il mio grado di nodo
		this.nodeDepth = 0;
		
		//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
		for( int i = 0 ; i < this.servedPeers.size(); i++){
			
			this.servedPeers.get(i).setSourceStreamingNode(null);

			//Decremento il numero di download attivi del nodo che stavo fornendo
			this.servedPeers.get(i).setDownloadActiveConnection(this.servedPeers.get(i).getDownloadActiveConnection()-1);
			
			//Azzero la profondita' del nodo che si stava fornendo da me
			this.servedPeers.get(i).resetNodeDepth();
			
			//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
			this.servedPeers.get(i).updateParentsList(triggeringTime);
			
		}
		
		//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
		for( int i = 0 ; i < this.servedPeers.size(); i++){
		
			//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
			this.servedPeers.get(i).getNeededChunk().clear();
			
			//Se il mio fornitore è attivo assegno il mio fornitore al nodo che prima stavo servendo io
			if( this.sourceStreamingNode != null && this.sourceStreamingNode.isConnected() 
					&& ( this.sourceStreamingNode.getMaxAcceptedConnection() - this.sourceStreamingNode.getActiveConnection() ) > 0){
				
				//Imposto il mio nuovo fornitore
				this.servedPeers.get(i).setSourceStreamingNode(this.sourceStreamingNode);
				
				//Mi aggiungo nella lista dei serviti del mio fornitore
				this.sourceStreamingNode.addServedPeer(this.servedPeers.get(i));
				
				//Incremento il mio ordine di nodo
				this.updateNodeDepth();
				
				//Incremento il numero di download attivi
				this.downloadActiveConnection ++;
				
				//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
				this.servedPeers.get(i).getBufferNeighbor(this.sourceStreamingNode,triggeringTime);
			}
			else
				if( this.serverNode != null && this.serverNode.isConnected() 
						&& ( this.serverNode.getMaxAcceptedConnection() - this.serverNode.getActiveConnection() ) > 0 ){
					
					//Imposto il mio nuovo fornitore
					this.servedPeers.get(i).setServerNode(this.serverNode);
					
					//Mi aggiungo nella lista dei serviti del mio fornitore
					this.serverNode.addServedPeer(this.servedPeers.get(i));
					
					//Incremento il mio ordine di nodo
					this.updateNodeDepth();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
					this.servedPeers.get(i).getBufferNeighbor(this.serverNode,triggeringTime);
				}
				else{
					
					if(this.servedPeers.get(i).sourceStreamingNode == null && this.servedPeers.get(i).serverNode == null)
						//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
						this.servedPeers.get(i).findProviderNodeFromLastSegment(triggeringTime);	
				}
			
		}
		
		//Pulisco la lista dei nodi che stavo fornendo
		this.servedPeers.clear();
		
		//Rimuovo tutti i miei vicini
		this.resetNeighbors();
		
		Engine.getDefault().getNodes().remove(this);
		
	}
	
	/**
	 * Funzione utilizzata per azzerare iterativamente il mio grado di nodo 
	 * e tutti i gradi di nodo dei nodi a me collegati
	 */
	public void resetNodeDepth() {
		
		this.nodeDepth = 0;
		
		for(int i = 0 ; i < this.getServedPeers().size(); i++)
			this.getServedPeers().get(i).resetNodeDepth();
		
	}

	/**
	 * Nel caso di un cambio di fornitore aggiorno il grado di nodo di tutti i 
	 * miei serviti
	 */
	public void updateNodeDepth() {
		
		int value = 0;
		
		if(this.sourceStreamingNode != null)
			value = this.sourceStreamingNode.getNodeDepth();
		
		if(this.serverNode != null)
			value = this.serverNode.getNodeDepth();
		
		
		this.nodeDepth = value + 1;
		
		for(int i = 0 ; i < this.getServedPeers().size(); i++)
			this.getServedPeers().get(i).updateNodeDepth();
		
	}
	
	public void updateFitnessValue(){
		
		//Campiono il nuovo tempo per determinare il tempo di permanenza OnLine	
		time2 = System.currentTimeMillis();
		
		this.onlineTime = this.time2 - this.time1;
		
		double newFitness = 0.0;
		
		//Batteria[0,100]% , UploadSpeed [0,1000]Kbit/sec , Online Time[0,24]h, Connessioni Attive[0,100]% 
		
		//Determino la velocità di upload in base alle connessioni attive 
		double speedValue = 0.0;
		
		if(this.activeConnection > 0)
			speedValue = this.uploadSpeed / (double)this.activeConnection;
		else
			speedValue = this.uploadSpeed;
		
		double depthValue = 1.0/((double)this.nodeDepth +1.0);
		
		newFitness = ( 1.5 * (double)this.battery + 0.4*(double)(speedValue) + 2.083*this.onlineTime + 25.0*depthValue) / (10.0 + (double)(this.activeConnection/this.maxAcceptedConnection)); 
		
		//System.out.println("New Fitness("+ this.getId() + ") : "  + newFitness);
		
		this.fitnessValue = newFitness;
	}
	
	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change3GTo2G( String connType, double uploadSpeed, int maxAcceptedConnection,float triggeringTime ){

		
		//Verifico il parametro connType
		if( connType.equals(G2) ){
			
			this.connectionType = connType;
			this.uploadSpeed = uploadSpeed;
		
			
			//Se il protocollo che valuta la fitness è attivo
			if(this.isFitnessSort())
			{

				
				//Imposto al massimo le mie connessioni per evitare di comportarmi come fornitore
				this.maxAcceptedConnection = maxAcceptedConnection;
				
				//Calcolo la nuova fitness
				this.updateFitnessValue();
				
				//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
				for( int i = 0 ; i < this.servedPeers.size(); i++){
					
					this.servedPeers.get(i).setSourceStreamingNode(null);

					//Decremento il numero di download attivi del nodo che stavo fornendo
					this.servedPeers.get(i).setDownloadActiveConnection(this.servedPeers.get(i).getDownloadActiveConnection()-1);
					
					//Azzero la profondita' del nodo che si stava fornendo da me
					this.servedPeers.get(i).resetNodeDepth();
					
					//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
					this.servedPeers.get(i).updateParentsList(triggeringTime);
					
				}
				
				//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
				for( int i = 0 ; i < this.servedPeers.size(); i++){
				
					//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
					this.servedPeers.get(i).getNeededChunk().clear();
					
					//Se il mio fornitore è attivo assegno il mio fornitore al nodo che prima stavo servendo io
					if( this.sourceStreamingNode != null && this.sourceStreamingNode.isConnected() 
							&& ( this.sourceStreamingNode.getMaxAcceptedConnection() - this.sourceStreamingNode.getActiveConnection() ) > 0){
						
						//Imposto il mio nuovo fornitore
						this.servedPeers.get(i).setSourceStreamingNode(this.sourceStreamingNode);
						
						//Mi aggiungo nella lista dei serviti del mio fornitore
						this.sourceStreamingNode.addServedPeer(this.servedPeers.get(i));
						
						//Incremento il mio ordine di nodo
						this.updateNodeDepth();
						
						//Incremento il numero di download attivi
						this.downloadActiveConnection ++;
						
						//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
						this.servedPeers.get(i).getBufferNeighbor(this.sourceStreamingNode,triggeringTime);
					}
					else
						if( this.serverNode != null && this.serverNode.isConnected() 
								&& ( this.serverNode.getMaxAcceptedConnection() - this.serverNode.getActiveConnection() ) > 0 ){
							
							//Imposto il mio nuovo fornitore
							this.servedPeers.get(i).setServerNode(this.serverNode);
							
							//Mi aggiungo nella lista dei serviti del mio fornitore
							this.serverNode.addServedPeer(this.servedPeers.get(i));
							
							//Incremento il mio ordine di nodo
							this.updateNodeDepth();
							
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
							this.servedPeers.get(i).getBufferNeighbor(this.serverNode,triggeringTime);
						}
						else{
							
							if(this.servedPeers.get(i).sourceStreamingNode == null && this.servedPeers.get(i).serverNode == null)
								//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
								this.servedPeers.get(i).findProviderNodeFromLastSegment(triggeringTime);	
						}
					
				}
				//Svuoto la lista dei nodi che stavo servendo
				this.servedPeers.clear();
				
			}
		}
		else
			System.out.println("Method change3GTo2G : Error in Connection Type ! Inserito: " + connType);
		
	}

	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change2GTo3G( String connType, double uploadSpeed, int maxAcceptedConnection ){
		
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
	
	public void addNewVideoResource(VideoChunk newVideoRes, float triggeringTime){
		
		//Salvo il tempo in cui e' arrivato il chunk
		float arrivalValue = triggeringTime - newVideoRes.getOriginalTime(); 
		this.arrivalTimes.add(arrivalValue);
		
		//Controllo se si e' verificata un eventuale deadline
		if( newVideoRes.getChunkIndex() < this.indexOfLastReceivedChunk )
			this.deadlineNumber++;
		
		if(!this.getVideoResource().contains(newVideoRes))
		{
			//Incremento il numero totale di chunk ricevuti
			this.totalChunkReceived ++;
			this.videoResource.add(newVideoRes);
			this.sortVideoBuffer();
		}
		else{
			this.duplicateChunkNumber ++; //Incremento il numero di duplicati
		}
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
	public boolean playVideoBuffer(){
		
		int difftot = 0;
		
		//Se il numero di tentativi di trovare 5 chunk continui e' fallito
		if( continuityTrialCount >= 3 )
		{
			
			
			//Azzero il contatore
			continuityTrialCount = 0;
			
			//Incremento il numero di missing, ovvero di volte che sono costretto a rimuovere dei segmenti per mancanza di continuita'
			this.missingChunkNumber++;
			
			ArrayList<VideoChunk> appList = new ArrayList<VideoChunk>();
			appList.addAll(this.videoResource);
			
			//Rimuovo i segmenti non continui all'interno dei primi 5 e dopo procedo normalmente nella ricerca
			for(int index = 0 ; index < 5 ; index++){
				
				int diff = appList.get(index+1).getChunkIndex() - appList.get(index).getChunkIndex();
			
				//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
				if( diff > 1 ){
					difftot = difftot + diff -1;
				}
			}
			
			//Memorizzo l'ultimo indice riprodotto
			this.indexOfLastReceivedChunk = this.videoResource.get(4).getChunkIndex();
			
			//String list = "";
			
			for(int k = 0 ; k < 5 ; k++)
			{	
				//list = list + "," + this.videoResource.get(0).getChunkIndex();
				this.videoResource.remove(0);
			}
			
			this.deadlineNumber += difftot;
			this.missingChunkNumber += difftot;
			
			
			
//			//Azzero il contatore
//			continuityTrialCount = 0;
//			
//			//Incremento il numero di missing, ovvero di volte che sono costretto a rimuovere dei segmenti per mancanza di continuita'
//			this.missingChunkNumber++;
//			
//			//ArrayList<VideoChunk> appList = new ArrayList<VideoChunk>();
//			//appList.addAll(this.videoResource);
//			
//			//Rimuovo i segmenti non continui all'interno dei primi 5 e dopo procedo normalmente nella ricerca
//			for(int index = 0 ; index < 5 ; index++){
//				
//				int diff = this.videoResource.get(index+1).getChunkIndex() - this.videoResource.get(index).getChunkIndex();
//			
//				//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
//				if( diff > 1 ){
//			
//					//Rimuovo i segmenti non contigui
//					for(int j = 0 ; j < 5 ; j ++)
//						this.videoResource.remove(0);
//				
//					//Incremento il numero di deadline in quanto non posso riprodurre questi segmenti per mancanza di continuità
//					this.deadlineNumber += 5;
//					
//					break;
//				}
//			}
		}
		
		//Se ci sono un numero sufficente di elementi nel buffer
		if( this.videoResource.size() >= 20 ){
		
			/*
			String origin = "";
			for(int k = 0 ; k < 5 ; k++)
				origin = origin + "," + this.videoResource.get(k).getChunkIndex();
				
			System.out.println("Id: " + this.getKey() + " Primi Elementi:" + origin);
			*/
			
			//Verifichiamo la continuita' dei primi elementi che voglio mandare in esecuzione
			for(int index = 0 ; index < 5 ; index++){
				
				int diff = this.videoResource.get(index+1).getChunkIndex() - this.videoResource.get(index).getChunkIndex();
			
				//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
				if( diff > 1 ){
						
					/*
						String list = "";
						
						for(int k = 0 ; k < 5 ; k++)
							list = list + "," + this.videoResource.get(k).getChunkIndex();
							
						System.out.println("Attendo ! Id: " + this.getKey() + " Primi Elementi:" + list + "\n");
						*/
						//Incremento il numero di tentativi di trovare delle sequenze continue
						continuityTrialCount  ++;
						
						return false;
				}
			}
			
			//Se sono arrivato qua ho la continuita' del primo blocco di elementi e posso riprodurli
			
			//Memorizzo l'ultimo indice riprodotto
			this.indexOfLastReceivedChunk = this.videoResource.get(4).getChunkIndex();
			
			//String list = "";
			
			for(int k = 0 ; k < 5 ; k++)
			{	
				//list = list + "," + this.videoResource.get(0).getChunkIndex();
				this.videoResource.remove(0);
			}
			
			//System.out.println("Riproduco ! Id: " + this.getKey() + " Primi Elementi:" + list + "\n");
				
		}
		
		//Non posso riprodurre
		return false;
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
	
	public ArrayList<Integer> sort(ArrayList<Integer> arrayList) {
		  
		 
		 //System.out.println("Ordino");
		 
		  ArrayList<Integer> appList = new ArrayList<Integer>();
		  
		  //Ordinamento dei vicini in base al loro valore di fitness
		  for(int i = 0 ; i < arrayList.size() ; i++)
		  {
			  Integer chunkOriginal = (Integer)arrayList.get(i);
		   
		   if(appList.size() == 0)
		    appList.add(chunkOriginal);
		   else
		   {
		    for(int j = 0 ; j < appList.size(); j++)
		    {
		     
		    	Integer chunkApp = (Integer)appList.get(j);
		     
		     
		     if(chunkOriginal <= chunkApp)
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
	
	public void findFirstProviderNode(float triggeringTime){
		
		//System.out.println("findFirstProviderNode");
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho già un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode() == null && this.getServerNode() == null )
		{
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() )
				this.sortParentsNodes();
			
			//Cerco all'interno della mia lista di vicini se trovo un fornitore per il flusso video
			for(int i = 0 ; i < this.getNeighbors().size(); i++){
				
				StreamingPeer appSourceStreamingNode = (StreamingPeer)this.getNeighbors().get(i);
				
				//Mi collego solo, se ha un fornitore se ha le risorse video e se ha la possibilità di accettare le connessioni e se non è tra la lista di quelli che sto fornendo
				if( 	(appSourceStreamingNode.getServerNode() != null || appSourceStreamingNode.getSourceStreamingNode() != null)
						&& appSourceStreamingNode.getVideoResource().size() > 0 
						&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0){
					
					//Imposto il mio fornitore
					this.setSourceStreamingNode(appSourceStreamingNode);
					
					//Incremento il mio ordine di nodo
					this.updateNodeDepth();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il nodo fornitore trovato
					this.getSourceStreamingNode().addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					this.getSourceStreamingNode().addServedPeer(this);
					
					//Chiamiamo la funzione per avere segmenti mancanti
					this.getBufferNeighbor(appSourceStreamingNode,triggeringTime);
					
					break;
				}
				
			}
			
			//Se non trovo nessun nodo da cui fornirmi, e non sono già connesso al nodo centrale mi collego al server centrale
			if( this.getSourceStreamingNode() == null && this.getServerNode() == null)
			{
					this.setServerNode((ServerPeer)Engine.getDefault().getNodes().get(0));
					
					//Incremento il mio ordine di nodo
					this.updateNodeDepth();
					//this.setNodeDepth(this.getServerNode().getNodeDepth()+1);
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il server centrale
					this.getServerNode().addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					this.getServerNode().addServedPeer(this);
				
			}
		}
		
		//Ordino il buffer dei segmenti richiesti
		this.neededChunk.addAll(this.sort(this.neededChunk));
		
		//Salvo il primo elemento
		if(this.neededChunk.size() > 0)
			this.initChunk = this.neededChunk.get(0);
	}
	
	public boolean findProviderNodeFromLastSegment(float triggeringTime) {
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho già un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode() == null && this.getServerNode() == null )
		{
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() )
				this.sortParentsNodes();
			
			if(this.getVideoResource().size() > 0)
			{
				for( int neededVideoIndex = this.getVideoResource().size()-1; neededVideoIndex >= 0; neededVideoIndex-- )
				{
					
					VideoChunk neededChunk = this.getVideoResource().get(neededVideoIndex);
					
					//Cerco all'interno della mia lista di vicini se trovo un fornitore partendo dal segmento che gia' posseggo
					for(int i = 0 ; i < this.getNeighbors().size(); i++){
						
						StreamingPeer appSourceStreamingNode = (StreamingPeer)this.getNeighbors().get(i);
						
						//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilita' di accettare le connessioni e se non ha tra la lista di quelli che sto fornendo
						if(     (appSourceStreamingNode.getServerNode() != null || appSourceStreamingNode.getSourceStreamingNode() != null)	
								&& appSourceStreamingNode.isConnected()
								&& !this.servedPeers.contains(appSourceStreamingNode) 
								&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0
							    && appSourceStreamingNode.getVideoResource().contains(neededChunk)
						   )
						{
							
							//Imposto il mio fornitore
							this.setSourceStreamingNode(appSourceStreamingNode);
							
							//Incremento il mio ordine di nodo
							this.updateNodeDepth();
							
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//Imposto la connessione attiva con il nodo fornitore trovato
							appSourceStreamingNode.addActiveConnection();
						
							//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
							appSourceStreamingNode.addServedPeer(this);
							
							//Chiamiamo la funzione per avere segmenti mancanti
							this.getBufferNeighbor(appSourceStreamingNode,triggeringTime);
							
							return true;
						}
				}
				}
			}
			
			//Se non trovo nessun nodo da cui fornirmi per una certa porzione rilancio la funzione base di ricerca fornitore
			if( this.getSourceStreamingNode() == null && this.getServerNode() == null)
			{		
				
				//Imposto il server come mio fornitore
				this.setServerNode((ServerPeer)Engine.getDefault().getNodes().get(0));
			
				//Incremento il mio ordine di nodo
				this.updateNodeDepth();	
				
				//Incremento il numero di download attivi
				this.downloadActiveConnection ++;
				
				//Imposto la connessione attiva con il server centrale
				this.getServerNode().addActiveConnection();
			
				//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
				this.getServerNode().addServedPeer(this);
				
			}	
		}
		
		return true;
	}
	
	public void sortParentsNodes(){
		getLogger().fine("#####################################");
		getLogger().fine("Sort Parents Nodes :" + this.neighbors.size());
		
		ArrayList<Peer> appList = new ArrayList<Peer>();
		
		//Ordinamento dei vicini in base al loro valore di fitness
		for(int i = 0 ; i < this.neighbors.size() ; i++)
		{
			StreamingPeer peerOriginal = (StreamingPeer)this.neighbors.get(i);
			
			//La lista è vuota aggiungo direttamente l'elemento
			if(appList.size() == 0)
				appList.add(peerOriginal);
			else
			{
				//Cerco se c'è un Peer con fitness minore
				for(int j = 0 ; j < appList.size(); j++)
				{
					
					StreamingPeer peerApp = (StreamingPeer)appList.get(j);
					
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
	public void getBufferNeighbor(Peer providerNode, float triggeringTime)
	{

		if(providerNode.getId().equals("serverNode"))
		{
			ServerPeer source = (ServerPeer)providerNode;
			
			int startIndex = source.getVideoResource().get(source.getVideoResource().size() - 1).getChunkIndex() - 10;
			 
			 if( startIndex < 0 )
			  startIndex = 0;
			  
			 for(int index = startIndex ; index < source.getVideoResource().size(); index++)
			  if(!this.getVideoResource().contains(source.getVideoResource().get(index)))
			  {
				  if( !this.neededChunk.contains(source.getVideoResource().get(index).getChunkIndex()) )
				  {
					  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
					  this.neededChunk.add(source.getVideoResource().get(index).getChunkIndex());
					  source.sendVideoChunk(this, source.getVideoResource().get(index), triggeringTime);
				  }
			  }
		
		}
		else
		{
			
			StreamingPeer source = (StreamingPeer)providerNode;
			
			int startIndex = source.getVideoResource().get(source.getVideoResource().size() - 1).getChunkIndex() - 10;
			 
			 if( startIndex < 0 )
			  startIndex = 0;
			  
			 for(int index = startIndex ; index < source.getVideoResource().size(); index++)
			  if(!this.getVideoResource().contains(source.getVideoResource().get(index)))
			  {
				  if( !this.neededChunk.contains(source.getVideoResource().get(index).getChunkIndex()) )
				  {
					  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
					  this.neededChunk.add(source.getVideoResource().get(index).getChunkIndex());
				  
					  source.sendVideoChunk(this, source.getVideoResource().get(index), triggeringTime);
				  }
			  }
		
		}
		
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
		
		//Verifico se devo degradare la velocità di download del nodo client in base alle
		//sue connessioni in ingresso attive
		double clientDownloadSpeed = 0.0;
		if( clientNode.getDownloadActiveConnection() > 0 )
			clientDownloadSpeed = clientNode.getDownloadSpeed() / (double)clientNode.getDownloadActiveConnection();
		else
			clientDownloadSpeed = clientNode.getDownloadSpeed();
		
		float appTime = nextChunkArrivalTime(this.getUploadSpeed(),clientDownloadSpeed,newResource);
		
		float time = triggeringTime + appTime;
		
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
		
		//System.out.println("Server New Chunk Time :"+ time*100 +"-" + floatTime*100);
		
		return floatTime*10;
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
	public boolean checkDisconnection( StreamingPeer peer ){
		
		if(peer == null)
			return true;
		
		if(peer.isConnected() == false)
			return true;
		
		return false;
		
	}
	
	public void sortVideoBuffer() {
		 
		 ArrayList<VideoChunk> appList = new ArrayList<VideoChunk>();
		 
		 //Ordinamento dei vicini in base al loro valore di fitness
		 for(int i = 0 ; i < this.getVideoResource().size() ; i++)
		 {
			 VideoChunk chunkOriginal = (VideoChunk)this.getVideoResource().get(i);
		  
			 if(appList.size() == 0)
				 appList.add(chunkOriginal);
			 else
			 {
				 for(int j = 0 ; j < appList.size(); j++)
				 {
		    
					 VideoChunk chunkApp = (VideoChunk)appList.get(j);
		    
					 //Se il peer che analizzo ha la fitness Maggiore o uguale di quello nel vettore di appoggio
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
		 
		 this.getVideoResource().clear();
		 this.getVideoResource().addAll(appList);
		 
	}
	
	public boolean findChunkFromProvider(VideoChunk chunk,float triggeringTime){
		
		//Controllo tra i miei fornitori se hanno la porzione che sto cercando
		if( this.sourceStreamingNode != null ){
			
			
			if(this.sourceStreamingNode.getVideoResource().contains(chunk))
			{		
				chunk.setSourceNode(this.sourceStreamingNode);
				chunk.setOriginalTime(triggeringTime);
				
				if( !this.neededChunk.contains(chunk.getChunkIndex()) )
					this.sourceStreamingNode.sendVideoChunk(this, chunk, triggeringTime);
				
				return true;
			}
		}	
		
		//Controllo tra i miei fornitori se hanno la porzione che sto cercando
		if( this.serverNode != null ){
			
			
			if(this.serverNode.getVideoResource().contains(chunk))
			{		
				chunk.setSourceNode(this.serverNode);
				chunk.setOriginalTime(triggeringTime);
				
				if( !this.neededChunk.contains(chunk.getChunkIndex()) )
					this.serverNode.sendVideoChunk(this, chunk, triggeringTime);
				
				return true;
			}
		}	
		
		return false;

	}
	
	/**
	 * Funzione che controlla periodicamente il Buffer delle porzioni video
	 * per verificare se ci sono dei pezzi mancanti.
	 * 
	 */
	public void updateVideoBufferList(float triggeringTime) {
	
		ArrayList<VideoChunk> missingChunk = new ArrayList<VideoChunk>(); 
		
		if(this.getVideoResource().size() >= 5)
		{
			//Guardo la prima meta' del mio buffer e vedo se ci sono porzioni mancanti
			for(int index = 0 ; index < this.getVideoResource().size()-1 ; index++){
				
				int diff = this.getVideoResource().get(index+1).getChunkIndex() - this.getVideoResource().get(index).getChunkIndex();
			
				if( diff > 1 ){
					
					VideoChunk chunk = this.getVideoResource().get(index);
					int baseIndex = chunk.getChunkIndex();
					
					for(int k = 0 ; k < diff-1 ; k++)
					{
						baseIndex++;
						
						VideoChunk newChunk = new VideoChunk(baseIndex,chunk.getChunkSize());
						
						if( !this.neededChunk.contains(newChunk.getChunkIndex()))
						{
							this.neededChunk.add(newChunk.getChunkIndex());
							missingChunk.add(newChunk);
						}
					}
				}
				
			}
		}
		
		/*
		String my ="";
		for(int j = 0 ; j < this.getVideoResource().size(); j++)
			my = my + " " + this.getVideoResource().get(j).getChunkIndex();
			
		
		String miss ="";
		for(int j = 0 ; j < missingChunk.size(); j++)
			miss = miss + " " + missingChunk.get(j).getChunkIndex();
		
		
		
		
		System.out.println("Id: "+ this.getKey() + " " + my + " / " + miss );
		*/

		/*
		if(missingChunk.size()>0)
		System.out.println(missingChunk.size());
		*/
		
		
		
		for(int i = 0 ; i < missingChunk.size(); i++)
			this.findChunkFromProvider(missingChunk.get(i), triggeringTime);
	
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

	public static String getWIFI() {
		return WIFI;
	}

	public static String getG3() {
		return G3;
	}

	public static String getG2() {
		return G2;
	}

	public static String getGPRS() {
		return GPRS;
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

	public ArrayList<VideoChunk> getVideoResource() {
		return videoResource;
	}

	public StreamingPeer getSourceStreamingNode() {
		return sourceStreamingNode;
	}

	public void setSourceStreamingNode(StreamingPeer sourceStreamingNode) {
		this.sourceStreamingNode = sourceStreamingNode;
	}

	public ServerPeer getServerNode() {
		return serverNode;
	}

	public void setServerNode(ServerPeer serverNode) {
		this.serverNode = serverNode;
	}

	public ArrayList<StreamingPeer> getServedPeers() {
		return servedPeers;
	}

	public void setServedPeers(ArrayList<StreamingPeer> servedPeers) {
		this.servedPeers = servedPeers;
	}

	public double getFitnessValue() {
		return fitnessValue;
	}

	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}
	
	public int getMaxPartnersNumber() {
		return maxPartnersNumber;
	}

	public boolean isFitnessSort() {
		return fitnessSort;
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

	public ArrayList<VideoChunk> getVideoPlayBuffer() {
		return videoPlayBuffer;
	}

	public void setVideoPlayBuffer(ArrayList<VideoChunk> videoPlayBuffer) {
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
		return indexOfLastReceivedChunk;
	}

	public void setIndexOfLastReceivedChunk(int indexOfLastReceivedChunk) {
		this.indexOfLastReceivedChunk = indexOfLastReceivedChunk;
	}

	public int getDuplicateChunkNumber() {
		return duplicateChunkNumber;
	}

	public void setDuplicateChunkNumber(int duplicateChunkNumber) {
		this.duplicateChunkNumber = duplicateChunkNumber;
	}

	public ArrayList<Integer> getNeededChunk() {
		return neededChunk;
	}

	public void setNeededChunk(ArrayList<Integer> neededChunk) {
		this.neededChunk = neededChunk;
	}

	public int getDeadlineNumber() {
		return deadlineNumber;
	}

	public void setDeadlineNumber(int deadlineNumber) {
		this.deadlineNumber = deadlineNumber;
	}

	public Integer getInitChunk() {
		return initChunk;
	}

	public void setInitChunk(Integer initChunk) {
		this.initChunk = initChunk;
	}

	

}
