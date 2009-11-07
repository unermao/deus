package it.unipr.ce.dsg.deus.example.hierarchical.streaming;

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
/**
 * @author Marco
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
	private static final String NUMBER_OF_LAYER = "numberOfLayer";
	
	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";
	public static final String G2 = "2g"; 
	public static final String GPRS = "gprs";
	
	private int battery = 0;
	private String connectionType = "";
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int videoResourceBufferLimit = 10;
	private double fitnessValue = 0.0;
	private boolean fitnessSort = false;
	private int maxPartnersNumber = 20;
	private int downloadActiveConnection = 0;
	private int missingChunkNumber = 0; 
	
	private int totalChunkPlayed = 0;
	private int totalLayerPlayed = 0;
	
	private int totalChunkReceived = 0;
	private int totalLayerReceived = 0;
	private int indexOfLastReceivedChunk;
	private int duplicateLayerNumber = 0;
	private int deadlineNumber = 0;
	private int numberOfLayer = 0;
	
	//private int nodeDepth = 0;
	//private ArrayList<Integer> nodeDepth = null;
	
	private long time1;
	private long time2;
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private double onlineTime = 0.0;
	
	//se diverso da null contiene nodo che mi serve
	//private StreamingPeer sourceStreamingNode = null;
	
	//lo rifaccio con la lista
	private ArrayList<StreamingPeer> sourceStreamingNode = null;
	
	//Se diverso da null contiene server
	//private ServerPeer serverNode = null;
	
	private ArrayList<ServerPeer> serverNode = null;
	
	
	//private ArrayList<StreamingPeer> servedPeers = new ArrayList<StreamingPeer>();
	
	private ArrayList<ServedPeer> servedPeers = new ArrayList<ServedPeer>();
	
	private ArrayList<VideoChunk> videoResource = new ArrayList<VideoChunk>();
	
	private ArrayList<VideoChunk> videoPlayBuffer = new ArrayList<VideoChunk>();
	
	private ArrayList<ChunkLayer> neededLayer = new ArrayList<ChunkLayer>();
	
	
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
			battery = Integer.parseInt(params.getProperty(BATTERY));
		
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
		
		if (params.containsKey(NUMBER_OF_LAYER))
			numberOfLayer = Integer.parseInt(params.getProperty(NUMBER_OF_LAYER));
		
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
		
		//Creo la lista dei nodi fornitori per livello (inizialmente vuota)
		sourceStreamingNode = new ArrayList<StreamingPeer>();
		//creo la lista per livello se mi sta fornendo il server
		serverNode = new ArrayList<ServerPeer>();
				
		this.sourceStreamingNode.clear();
		for (int i = 0; i<5; i++){
			
			//nodeDepth.add(0);
			sourceStreamingNode.add(null);
			serverNode.add(null);
		}
		
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
		clone.totalLayerReceived = totalLayerReceived;
		
		ArrayList<StreamingPeer>  newStreamingPeer = new ArrayList<StreamingPeer>();
		ArrayList<ServerPeer>  newServerPeer = new ArrayList<ServerPeer>();
		
		for (int i = 0; i<5; i++){
			
			newStreamingPeer.add(null);
			newServerPeer.add(null);
		}
		
		clone.servedPeers = new ArrayList<ServedPeer>();
		
		clone.serverNode = newServerPeer;
		clone.sourceStreamingNode = newStreamingPeer;
		
		clone.totalLayerPlayed = totalLayerPlayed;
		clone.totalChunkPlayed = totalChunkPlayed;
		clone.videoResource = new ArrayList<VideoChunk>(); 
		clone.arrivalTimes = new ArrayList<Float>(); 
		clone.videoResourceBufferLimit  = this.videoResourceBufferLimit;
		//clone.nodeDepth = 0;
		clone.missingChunkNumber = 0;
		clone.videoPlayBuffer = new ArrayList<VideoChunk>();
		clone.totalChunkReceived = 0;
		clone.duplicateLayerNumber = 0;
		clone.neededLayer = new ArrayList<ChunkLayer>();
		clone.deadlineNumber = 0;
		clone.isConnected = true;
		clone.initChunk = 0;
		clone.numberOfLayer = numberOfLayer;
		return clone;
	}
	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	
	//Mi restituisce la sottolista dei nodi presenti che desiderano ricevere un certo numero di layer
	private ArrayList<StreamingPeer> GetLayerNode (ArrayList<Peer> list, int layer){
		ArrayList<StreamingPeer> temp = new ArrayList<StreamingPeer>();
		for (int i = 0; i<list.size();i++){
			
			StreamingPeer n = (StreamingPeer) list.get(i);
			if (n.getNumberOfLayer() >= layer)
				temp.add(n);
		}
		return temp;
		
	}
	
	public boolean Contains (ArrayList<ServedPeer> list, StreamingPeer p){
		for (Iterator i = list.iterator(); i.hasNext();){
			ServedPeer s = (ServedPeer) i.next();
			if (s.getPeer().equals(p))
				return true;
		}
		return false;
	}
	
	public void updateParentsList(float triggeringTime, int layer){
		
		int startListNumber = this.neighbors.size();
		
		//Controllo se il nodo non ha piu' un fornitore
		if( this.sourceStreamingNode.get(layer) != null && !this.sourceStreamingNode.get(layer).isConnected()){
		//if( this.sourceStreamingNode.get(layer) == null && this.serverNode.get(layer) == null){
			System.out.println("NON HO UN FORNITORE:" + this.getKey() + " - " + this.getNeighbors().size());
						
			this.findProviderNodeFromLastSegment(triggeringTime,layer);
		}
		
		//Aggiorno la fitness del nodo
		this.updateFitnessValue();
		
		//Attraverso la lista dei nodi
		for(int i = 0; i < this.getNeighbors().size(); i++)
			if(checkDisconnection((StreamingPeer) this.getNeighbors().get(i)))
				this.getNeighbors().remove(i);
		
		//System.out.println(this.getNeighbors().size() + "/" + Engine.getDefault().getNodes().size());
		//Se qualche nodo e' stato rimosso dalla lista dei vicini
		if( this.getNeighbors().size() < (this.getMaxPartnersNumber()*this.numberOfLayer)){
			
			
			//distinguere tra tipi di vicini
			for (int i=0; i<this.numberOfLayer; i++){
			
				ArrayList<StreamingPeer> list = this.GetLayerNode(this.neighbors, i);

				int size = list.size();
				
				if (size < this.maxPartnersNumber){
					
					//Cerco tra i miei vicini qualche nuovo contatto
					for(int index = 0; index < this.neighbors.size() ; index++)
					{

						StreamingPeer peer = (StreamingPeer) this.neighbors.get(index);

						//cerco i vicini dei miei vicini
						for(int j = 0; j < peer.getNeighbors().size(); j++ ){

							StreamingPeer peerApp = (StreamingPeer)peer.neighbors.get(j);

							if(
									peerApp.isConnected() //Se il peer e' connesso
									&& !this.neighbors.contains(peerApp) //Se non e' gia' tra i miei vicni
									&& !peerApp.equals(this) //Se non sono io
									&& !Contains(this.servedPeers,peerApp) //Se non lo sto servendo
									&& peerApp.numberOfLayer >= i	
							)
								{
								  this.addNeighbor(peerApp);
								  size = size+1;
								}

							
							//Se ho raggiunto il limite massimo esco
							if(size == this.maxPartnersNumber)
								break;

						}

						//Se ho raggiunto il limite massimo esco
						if(size == this.maxPartnersNumber)
							break;

					} // end for dei vicini
				}

			}  //end for dei livelli
			
		} //end if che controlla se qualche nodo � stato rimosso dalla lista dei vicini
		
		//Riordino la lista dei vicini in base alla loro fitness  
		if( this.isFitnessSort() )
			this.sortParentsNodes();
		
		while( this.neighbors.size() > this.maxPartnersNumber*this.numberOfLayer ){
			this.neighbors.remove(this.neighbors.size()-1);
		}
		
		if( this.neighbors.size() > this.maxPartnersNumber*this.numberOfLayer )
			System.out.println("ERRORE LISTA TROPPO GRANDE !!!! ("+ this.neighbors.size() + "/" + this.maxPartnersNumber*this.numberOfLayer + ")");
		
		
	}
	
	
	public void RemoveServedPeer(ArrayList<ServedPeer> list, StreamingPeer p){
		for (int j=0; j<list.size();j++){
			ServedPeer s = list.get(j);
			if (s.getPeer().getKey() == p.getKey())
			  list.remove(j);
		}
		
	}
	
	public void disconnection(float triggeringTime){
		
		//Imposto il nodo come disconnesso
		this.setConnected(false);
		
		//Comunico le mie statistiche al Server
		ServerPeer server = (ServerPeer)Engine.getDefault().getNodes().get(0);
		
		server.setMissingChunkNumber(server.getMissingChunkNumber() + this.missingChunkNumber);
		server.setTotalChunkReceived(server.getTotalChunkReceived() + this.totalChunkReceived);
		server.setTotalLayerReceived(server.getTotalLayerReceived() + this.totalLayerReceived);
		server.setDuplicateLayerNumber(server.getDuplicateLayerNumber() + this.duplicateLayerNumber );
		server.setTotalDeadine(server.getTotalDeadine() + this.deadlineNumber); 
		
		//vado a settare sul server il numero di layer e chunk che sono andati in riproduzione per fare una media!
		
		//Aggiungo i tempi di arrivo di questo nodo al server per avere delle statistiche complete
		if(this.getId().equals("pcNode")){
			server.getArrivalTimesPcNode().addAll(this.arrivalTimes);
			server.setTotalLayerPlayedPc(server.getTotalLayerPlayedPc() + this.totalLayerPlayed);
			server.setTotalChunkPlayedPc(server.getTotalChunkPlayedPc() + this.totalChunkPlayed);
		}	
		if(this.getId().equals("mobileNode")){
			server.getArrivalTimesMobileWifiNode().addAll(this.arrivalTimes);
			server.setTotalLayerPlayedMobile(server.getTotalLayerPlayedMobile() + this.totalLayerPlayed);
			server.setTotalChunkPlayedMobile(server.getTotalChunkPlayedMobile() + this.totalChunkPlayed);
			
		}
		if(this.getId().equals("mobile3GNode")){
			server.getArrivalTimesMobile3GNode().addAll(this.arrivalTimes);
			server.setTotalLayerPlayed3G(server.getTotalLayerPlayed3G() + this.totalLayerPlayed);
			server.setTotalChunkPlayed3G(server.getTotalChunkPlayed3G() + this.totalChunkPlayed);
			
		}
		
		//Incremento il numero di disconnessioni
		server.addDisconnectedNode();	
		
		//differenziare per livello
		
		for (int layer=0; layer<this.numberOfLayer; layer++){
			//Mi tolgo dal mio fornitore
			if(this.serverNode.get(layer) != null)
				//this.serverNode.get(layer).getServedPeers().remove(this);
				RemoveServedPeer(this.serverNode.get(layer).getServedPeers(),this);
				
			if(this.sourceStreamingNode.get(layer) != null)
				RemoveServedPeer(this.sourceStreamingNode.get(layer).getServedPeers(),this);
		}
		//Azzero il mio grado di nodo
		//this.nodeDepth = 0;
		
		//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
		for( int i = 0 ; i < this.servedPeers.size(); i++){						
			
			this.servedPeers.get(i).getPeer().getSourceStreamingNode().set(this.servedPeers.get(i).getLayer(), null);
			
			//Decremento il numero di download attivi del nodo che stavo fornendo
			this.servedPeers.get(i).getPeer().setDownloadActiveConnection(this.servedPeers.get(i).getPeer().getDownloadActiveConnection()-1);
			
			//Azzero la profondita' del nodo che si stava fornendo da me
			//this.servedPeers.get(i).resetNodeDepth();
						
			//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
			this.servedPeers.get(i).getPeer().updateParentsList(triggeringTime,this.servedPeers.get(i).getLayer());
			
		}
		
		//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
		for( int i = 0 ; i < this.servedPeers.size(); i++){
		
			//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
			this.servedPeers.get(i).getPeer().getNeededLayer().clear();
			
			//Se il mio fornitore � attivo assegno il mio fornitore al nodo che prima stavo servendo io
			//tengo in pi� in conto del livello a cui lo stavo fornendo						
			if( this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()) != null && this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).isConnected() 
					&& ( this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).getMaxAcceptedConnection() - this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).getActiveConnection() ) > 0){	
				
				
				int Layer = this.servedPeers.get(i).getLayer();
				
				//Imposto nuovo fornitore per chi stavo fornendo
				//this.servedPeers.get(i).setSourceStreamingNode(this.sourceStreamingNode);
				
				this.servedPeers.get(i).getPeer().getSourceStreamingNode().set(Layer, this.sourceStreamingNode.get(Layer));
				
				//Aggiungo alla lista fornitore il nuovo nodo fornito
				
				this.sourceStreamingNode.get(Layer).getServedPeers().add(new ServedPeer(this.servedPeers.get(i).getPeer(),Layer));
				
				
				//Incremento il mio ordine di nodo
				//this.updateNodeDepth();
				
				//Incremento il numero di download attivi
				this.downloadActiveConnection ++;
				
				//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
				this.servedPeers.get(i).getPeer().getBufferNeighbor(this.sourceStreamingNode.get(Layer),triggeringTime,Layer);
			}
			else
				if( this.serverNode.get(this.servedPeers.get(i).getLayer()) != null && this.serverNode.get(this.servedPeers.get(i).getLayer()).isConnected() 
						&& ( this.serverNode.get(this.servedPeers.get(i).getLayer()).getMaxAcceptedConnection() - this.serverNode.get(this.servedPeers.get(i).getLayer()).getActiveConnection() ) > 0 ){
					
					
					int Layer = this.servedPeers.get(i).getLayer();
					
					//Imposto il mio nuovo fornitore
					this.servedPeers.get(i).getPeer().getServerNode().set(Layer, this.serverNode.get(Layer));
					
					
					//Aggiungo alla lista fornitore il nuovo nodo fornito
					this.serverNode.get(Layer).getServedPeers().add(new ServedPeer(this.servedPeers.get(i).getPeer(),Layer));										
					
					//Incremento il mio ordine di nodo
					//this.updateNodeDepth();
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
					
					this.servedPeers.get(i).getPeer().getBufferNeighbor(this.serverNode.get(Layer),triggeringTime,Layer);
				}
				else{
					
					//if(this.servedPeers.get(i).getPeer().sourceStreamingNode == null && this.servedPeers.get(i).serverNode == null)
					int Layer = this.servedPeers.get(i).getLayer();
					
					if(this.servedPeers.get(i).getPeer().getSourceStreamingNode().get(Layer) == null && this.servedPeers.get(i).getPeer().getServerNode().get(Layer) == null)
					
					//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo	
					this.servedPeers.get(i).getPeer().findProviderNodeFromLastSegment(triggeringTime,this.servedPeers.get(i).getLayer());	
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
	/*
	 * ELIMINITATI PERCHE NN LA USO
	 * 
	 * public void resetNodeDepth() {
		
		this.nodeDepth = 0;
		
		for(int i = 0 ; i < this.getServedPeers().size(); i++)
			this.getServedPeers().get(i).resetNodeDepth();
		
	}*/

	/**
	 * Nel caso di un cambio di fornitore aggiorno il grado di nodo di tutti i 
	 * miei serviti
	 */
	/*
	public void updateNodeDepth(int layer) {
		
		int value = 0;
		
		if(this.sourceStreamingNode.get(layer) != null)
			value = this.sourceStreamingNode.get(layer).getNodeDepth().get(layer);
		
		if(this.serverNode.get(layer) != null)
			value = 0;
			//value = this.serverNode.get(layer).getNodeDepth().;
		
		
		this.nodeDepth.set(layer, value +1);
		
		for(int i = 0 ; i < this.getServedPeers().size(); i++)
			this.getServedPeers().get(i).updateNodeDepth(layer);
		
	}*/
	
	public void updateFitnessValue(){
		
		//Campiono il nuovo tempo per determinare il tempo di permanenza OnLine	
		time2 = System.currentTimeMillis();
		
		this.onlineTime = this.time2 - this.time1;
		
		double newFitness = 0.0;
		
		//Batteria[0,100]% , UploadSpeed [0,1000]Kbit/sec , Online Time[0,24]h, Connessioni Attive[0,100]% 
		
		//Determino la velocit� di upload in base alle connessioni attive 
		double speedValue = 0.0;
		
		if(this.activeConnection > 0)
			speedValue = this.uploadSpeed / (double)this.activeConnection;
		else
			speedValue = this.uploadSpeed;
		
		//double depthValue = 1.0/((double)this.nodeDepth +1.0);
		
		
		// modificata e tolta la nodeDepth
		//newFitness = ( 1.5 * (double)this.battery + 0.4*(double)(speedValue) + 2.083*this.onlineTime + 25.0*depthValue) + 0.3*(double)this.numberOfLayer / (10.0 + (double)(this.activeConnection/this.maxAcceptedConnection)); 
		newFitness = ( 1.5 * (double)this.battery + 0.4*(double)(speedValue) + 2.083*this.onlineTime) / (10.0 + (double)(this.activeConnection/this.maxAcceptedConnection));
		
		
		//System.out.println("New Fitness("+ this.getId() + ") : "  + newFitness);
		
		this.fitnessValue = newFitness;
	}
	
	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change3GTo2G( String connType, double uploadSpeed, int maxAcceptedConnection,int NumberOfLayer,float triggeringTime ){
		
		//Verifico il parametro connType
		if( connType.equals(G2) ){
			
			this.connectionType = connType;
			this.uploadSpeed = uploadSpeed;
			this.numberOfLayer = NumberOfLayer;
			this.setNumberOfLayer(NumberOfLayer);
			//Se il protocollo che valuta la fitness � attivo allora devo aggiornarla
			//if(this.isFitnessSort())
			//{
				
				//Imposto al massimo le mie connessioni per evitare di comportarmi come fornitore
				this.maxAcceptedConnection = maxAcceptedConnection;
				
				//Calcolo la nuova fitness
				if (this.isFitnessSort())
				  this.updateFitnessValue();
				
				//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
				for( int i = 0 ; i < this.servedPeers.size(); i++){
					
					//ora devo farlo per livello
					//this.servedPeers.get(i)setSourceStreamingNode(null);
					this.servedPeers.get(i).getPeer().getSourceStreamingNode().set(this.servedPeers.get(i).getLayer(), null);
					
					
					//Decremento il numero di download attivi del nodo che stavo fornendo
					this.servedPeers.get(i).getPeer().setDownloadActiveConnection(this.servedPeers.get(i).getPeer().getDownloadActiveConnection()-1);
					
					//Azzero la profondita' del nodo che si stava fornendo da me
					//this.servedPeers.get(i).resetNodeDepth();
					
					//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
					
				 	this.servedPeers.get(i).getPeer().updateParentsList(triggeringTime,this.servedPeers.get(i).getLayer());
					
				}
				
				//Lancio la funzione di ricerca dei nuovi nodi per quelli che stavo servendo
				for( int i = 0 ; i < this.servedPeers.size(); i++){
				
					//Faccio ripulire al nodo che stavo servendo la lista dei dati richiesti in modo che puo' richiederli ad altri
					this.servedPeers.get(i).getPeer().getNeededLayer().clear();
					
					//Se il mio fornitore � attivo assegno il mio fornitore al nodo che prima stavo servendo io
					//tengo in pi� in conto del livello a cui lo stavo fornendo
					
	
					if( this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()) != null && this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).isConnected() 
							&& ( this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).getMaxAcceptedConnection() - this.sourceStreamingNode.get(this.servedPeers.get(i).getLayer()).getActiveConnection() ) > 0){	
						
						
						int Layer = this.servedPeers.get(i).getLayer();
						
						//Imposto nuovo fornitore per chi stavo fornendo
						//this.servedPeers.get(i).setSourceStreamingNode(this.sourceStreamingNode);
						
						this.servedPeers.get(i).getPeer().getSourceStreamingNode().set(Layer, this.sourceStreamingNode.get(Layer));
//						System.out.println(this.servedPeers.get(i).getPeer().getSourceStreamingNode().get(Layer));
						
						//Aggiungo alla lista fornitore il nuovo nodo fornito
						
						this.sourceStreamingNode.get(Layer).getServedPeers().add(new ServedPeer(this.servedPeers.get(i).getPeer(),Layer));
						
						
						//Incremento il mio ordine di nodo
						//this.updateNodeDepth();
						
						//Incremento il numero di download attivi
						this.downloadActiveConnection ++;
						
						//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
						this.servedPeers.get(i).getPeer().getBufferNeighbor(this.sourceStreamingNode.get(Layer),triggeringTime,Layer);
					}
					else
						if( this.serverNode.get(this.servedPeers.get(i).getLayer()) != null && this.serverNode.get(this.servedPeers.get(i).getLayer()).isConnected() 
								&& ( this.serverNode.get(this.servedPeers.get(i).getLayer()).getMaxAcceptedConnection() - this.serverNode.get(this.servedPeers.get(i).getLayer()).getActiveConnection() ) > 0 ){
							
							
							int Layer = this.servedPeers.get(i).getLayer();
							
							//Imposto il mio nuovo fornitore
							this.servedPeers.get(i).getPeer().getServerNode().set(Layer, this.serverNode.get(Layer));
							
							
							//Aggiungo alla lista fornitore il nuovo nodo fornito
							this.serverNode.get(Layer).getServedPeers().add(new ServedPeer(this.servedPeers.get(i).getPeer(),Layer));
							
							
							
							//Incremento il mio ordine di nodo
							//this.updateNodeDepth();
							
							//Incremento il numero di download attivi
							this.downloadActiveConnection ++;
							
							//Controllo gli ultimi elementi del suo buffer per ricevere eventuali porzioni mancanti
							
							this.servedPeers.get(i).getPeer().getBufferNeighbor(this.serverNode.get(Layer),triggeringTime,Layer);
						}
						else{
							
							//if(this.servedPeers.get(i).getPeer().sourceStreamingNode == null && this.servedPeers.get(i).serverNode == null)
							
							if(this.servedPeers.get(i).getPeer().getSourceStreamingNode().get(i) == null && this.servedPeers.get(i).getPeer().getServerNode().get(i) == null)
							
							//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo	
							this.servedPeers.get(i).getPeer().findProviderNodeFromLastSegment(triggeringTime,this.servedPeers.get(i).getLayer());	
						}
				
				}
				//Svuoto la lista dei nodi che stavo servendo
				this.servedPeers.clear();
				
		}
		else
			System.out.println("Method change3GTo2G : Error in Connection Type ! Inserito: " + connType);
		
	}



	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change2GTo3G( String connType, double uploadSpeed, int maxAcceptedConnection, int NumberOfLayer, float triggeringTime ){
		int oldNumberOfLayer = this.numberOfLayer;
		
		//Verifico il parametro connType
		if( connType.equals(G3)){
			
			this.connectionType = connType;
			this.maxAcceptedConnection = maxAcceptedConnection;
			this.uploadSpeed = uploadSpeed;
			this.numberOfLayer = NumberOfLayer;
			
			//Calcolo la nuova fitness
			if (this.isFitnessSort())
				this.updateFitnessValue();
			
			//Dovr� andare ad aumentare il numero di vicini (dato che ho aumentato il livello)
			//inoltre cerco un fornitore per il nuovo/nuovi livelli
			
			//for (int layer = oldNumberOfLayer+1; layer<=this.numberOfLayer;layer++)
			for (int layer = oldNumberOfLayer+1; layer<this.numberOfLayer;layer++)	
			  this.updateParentsList(triggeringTime, layer);
			
		}
		else
			System.out.println("Method change2GTo3G : Error in Connection Type !");
		
	}

	
	public void addNewVideoResource(ChunkLayer newVideoRes, float triggeringTime){
		
		//System.out.println("Ricevuto layer " + newVideoRes.getLayerIndex()+ "/" + this.getNumberOfLayer());
		//Salvo il tempo in cui e' arrivato il layer
	
		float arrivalValue = triggeringTime - newVideoRes.getOriginalTime(); 
		this.arrivalTimes.add(arrivalValue);
		
		//Controllo se si e' verificata un eventuale deadline (ricezione fuori ordine)
		//non lo posso pi� fare in questa maniera ma devo vedere quando effettivamente ricevo il chunk
		/*if( newVideoRes.getChunkIndex() < this.indexOfLastReceivedChunk )
			this.deadlineNumber++;*/
		
		//if(!this.getVideoResource().contains(newVideoRes))
		if (!isLayerReceived(this.getVideoResource(),newVideoRes.getChunkIndex(),newVideoRes.getLayerIndex()))
		{
			//Incremento il numero totale di chunk ricevuti
			this.totalLayerReceived ++;
		
			//l'add nn posso pi� farlo diretto ma devo usare una funzione che se esiste gi� il chunk di quel livello lo
			//aggiunge senn� crea il nuovo chunk
			addNewLayer(newVideoRes);
			
			this.sortVideoBuffer();
		}
		else{
			//ora nn ha pi� senso mantenere il numero di chunk duplicati perch� sar� quasi impossibile!
			this.duplicateLayerNumber ++; //Incremento il numero di duplicati
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
			
				//Controllo che all'interno del chink ci sia per lo meno il livello base!
				
				
				//Se la diff e' maggiore di 1 significa che non ho continuita' e aspetto prima di riprodurre
				if( (diff > 1) || (!appList.get(index).isLayerPresent(0)) ){
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
//					//Incremento il numero di deadline in quanto non posso riprodurre questi segmenti per mancanza di continuit�
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
			
				//Se la diff e' maggiore di 1 o uno dei segmenti nn ha il livello base significa che non ho continuita' e aspetto prima di riprodurre
				if( (diff > 1) || !this.videoResource.get(index).isLayerPresent(0)){
						
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
				
				//oltre a rimuovere qui vado a far delle statistiche sui layer effettivamente ricevuti
				//devo trovare il numero di layer consecutivi che ho a disposizione!
				int j = 0;
				for (int i=0; i<this.numberOfLayer; i++)
					if (this.videoResource.get(0).isLayerPresent(i))
						j++;
					else break;
						
				
				this.totalChunkPlayed++;
				this.totalLayerPlayed = this.totalLayerPlayed + j;
				this.videoResource.remove(0);
				
			}
			
			//System.out.println("Riproduco! Id: " + this.getKey() + " Primi Elementi.");
				
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
	
	public void addServedPeer(StreamingPeer peer,int layer){
		ServedPeer p = new ServedPeer(peer,layer);
		
		if(!this.getServedPeers().contains(p) && !this.equals(p.getPeer()))
			this.getServedPeers().add(p);
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
	
	//Perci� dovr� aver un fornitore per livello!
	public void findFirstProviderNode(float triggeringTime){
		
		for (int layer = 0; layer < this.getNumberOfLayer(); layer++) {
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho gi� un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode().get(layer) == null && this.getServerNode().get(layer) == null )
		{
			
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() ){
				this.sortParentsNodes();
			}
			
			//Cerco all'interno della mia lista di vicini se trovo un fornitore per il flusso video
			for(int i = 0 ; i < this.getNeighbors().size(); i++){
				
				StreamingPeer appSourceStreamingNode = (StreamingPeer) this.getNeighbors().get(i);
				
				//Mi collego solo, se ha un fornitore se ha le risorse video e se ha la possibilit� di accettare le connessioni e se non � tra la lista di quelli che sto fornendo
				
				if( (appSourceStreamingNode.getServerNode().get(layer) != null || appSourceStreamingNode.getSourceStreamingNode().get(layer) != null)						
						&& ((appSourceStreamingNode.getVideoResource().size() > 0) && (hasLayer(appSourceStreamingNode.getVideoResource(),layer)))  
						&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0
						&& appSourceStreamingNode.getNumberOfLayer() >= layer){
					
					//Imposto il mio fornitore per il layer attuale
					this.sourceStreamingNode.set(layer, appSourceStreamingNode);
					
					//Incremento il mio ordine di nodo 
					//this.updateNodeDepth(layer);
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il nodo fornitore trovato
					appSourceStreamingNode.addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					appSourceStreamingNode.addServedPeer(this,layer);
					
					//Chiamiamo la funzione per avere segmenti mancanti
					
					//ci sar� da tener conto anche dei layer ora!, ho perci� aggiunto il layer
					//
					this.getBufferNeighbor(appSourceStreamingNode,triggeringTime,layer);
					
					break;
				}
				
			}
			
			//Se non trovo nessun nodo da cui fornirmi, e non sono gi� connesso al nodo centrale mi collego al server centrale
			if( this.getSourceStreamingNode().get(layer) == null && this.getServerNode().get(layer) == null )
			{
					
				ServerPeer server = (ServerPeer)Engine.getDefault().getNodes().get(0);
				
					this.serverNode.set(layer, server);
					
					//Incremento il mio ordine di nodo
					//this.updateNodeDepth(layer);
					//this.setNodeDepth(this.getServerNode().getNodeDepth()+1);
					
					//Incremento il numero di download attivi
					this.downloadActiveConnection ++;
					
					//Imposto la connessione attiva con il server centrale
					//this.getServerNode().get(layer).addActiveConnection();
					server.addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					server.addServedPeer(this,layer);
			}
		}
		
		
		//TODO:  guardare ci ho capito poco
		
		//Ordino il buffer dei segmenti richiesti
		this.neededLayer.addAll(this.neededLayer);
		
		//Salvo il primo elemento
		if(this.neededLayer.size() > 0)
			this.initChunk = this.neededLayer.get(0).getChunkIndex();
		}
	}
	
	
	public boolean findProviderNodeFromLastSegment(float triggeringTime, int layer) {
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho gi� un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode().get(layer) == null && this.getServerNode().get(layer) == null )
		{
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() )
				this.sortParentsNodes();
			
			
			if(this.getVideoResource().size() > 0)
			{
				for( int neededVideoIndex = this.getVideoResource().size()-1; neededVideoIndex >= 0; neededVideoIndex-- )
				{

					VideoChunk neededChunk = this.getVideoResource().get(neededVideoIndex);

					if (isLayerReceived(this.getVideoResource(),neededChunk.getChunkIndex(),layer)){
					   //Cerco all'interno della mia lista di vicini se trovo un fornitore partendo dal segmento che gia' posseggo
						for(int i = 0 ; i < this.getNeighbors().size(); i++){

							StreamingPeer appSourceStreamingNode = (StreamingPeer)this.getNeighbors().get(i);

							//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilita' di accettare le connessioni e se non ha tra la lista di quelli che sto fornendo
							if(     (appSourceStreamingNode.getServerNode().get(layer) != null || appSourceStreamingNode.getSourceStreamingNode().get(layer) != null)	
									&& appSourceStreamingNode.isConnected()
									&& !Contains(this.servedPeers,appSourceStreamingNode)
									&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0
									//&& appSourceStreamingNode.getVideoResource().contains(neededChunk)
									//devo vedere se ho un determinato layer di un determinato chunck
									&& isLayerReceived(appSourceStreamingNode.getVideoResource(),neededChunk.getChunkIndex(),layer)
							)
							{
								
								
								//Imposto il mio fornitore
								this.sourceStreamingNode.set(layer,appSourceStreamingNode);

								//Incremento il mio ordine di nodo
								//this.updateNodeDepth();

								//Incremento il numero di download attivi
								this.downloadActiveConnection ++;

								//Imposto la connessione attiva con il nodo fornitore trovato
								appSourceStreamingNode.addActiveConnection();
								
								
								//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
								appSourceStreamingNode.addServedPeer(this,layer);
								
								//Chiamiamo la funzione per avere segmenti mancanti
								this.getBufferNeighbor(appSourceStreamingNode,triggeringTime,layer);

								return true;
							}
						}

					}//end if layer
				}//end for
				
			}
			
			//Se non trovo nessun nodo da cui fornirmi per una certa porzione rilancio la funzione base di ricerca fornitore
			if( this.getSourceStreamingNode().get(layer) == null && this.getServerNode().get(layer) == null)
			{		
				
				//Imposto il server come mio fornitore
				//this.setServerNode((ServerPeer)Engine.getDefault().getNodes().get(0));
			    this.serverNode.set(layer,(ServerPeer) Engine.getDefault().getNodes().get(0));
				
				//Incremento il mio ordine di nodo
				//this.updateNodeDepth();	
			    
				//Incremento il numero di download attivi
				this.downloadActiveConnection ++;
				
				//Imposto la connessione attiva con il server centrale
				this.getServerNode().get(layer).addActiveConnection();
			
				//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
				this.getServerNode().get(layer).addServedPeer(this,layer);
				
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
			
			//La lista ? vuota aggiungo direttamente l'elemento
			if(appList.size() == 0)
				appList.add(peerOriginal);
			else
			{
				//Cerco se c'? un Peer con fitness minore
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
	 * @param layer
	 */
	public void getBufferNeighbor(Peer providerNode, float triggeringTime, int layer)
	{

		if (providerNode.getId().equals("serverNode"))
		{
			ServerPeer source = (ServerPeer)providerNode;
			
			int startIndex = source.getVideoResource().get(source.getVideoResource().size() - 1).getChunkIndex() - 10;
			 
			 if( startIndex < 0 )
			  startIndex = 0;
			  
			 for(int index = startIndex ; index < source.getVideoResource().size(); index++)
			   
			  //Sto decidendo quale richiedere..	 
			  
			  //if(!this.getVideoResource().contains(source.getVideoResource().get(index)))
			  //cerco se il livello layer del cunk index � gi� presente
			  if (!isLayerReceived(this.getVideoResource(),index,layer))
				 
			  {
				  //se nn l'ho gi� richiesto
				  //if( !this.neededChunk.contains(source.getVideoResource().get(index).getChunkIndex()) )
				  if (!isLayerNeeded(index,layer))
				  {
					  
					  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
					  //this.neededChunk.add(source.getVideoResource().get(index).getChunkIndex());
			          this.neededLayer.add(new ChunkLayer(layer,index));
					    
					  source.sendChunkLayer(this, source.getVideoResource().get(index).getLayers().get(layer), triggeringTime);
				  }
			  }
		
		}
		else
		{
			
			StreamingPeer source = (StreamingPeer) providerNode;
			
			ArrayList<ChunkLayer> list = ExtractLayer(source.getVideoResource(),layer,10);
			for(int index = 0 ; index < list.size(); index++)
				  if (!isLayerReceived(this.getVideoResource(),index,layer))
				  {
					  if (!isLayerNeeded(list.get(index).getChunkIndex(),layer))
					  {
						  //Aggiungo l'indice del chunk nella lista di quelli che ho gia' richiesto
						  this.neededLayer.add(new ChunkLayer(layer,index));
						  
						  //controllo se ha livello!!!!
						  source.sendChunkLayer(this, list.get(index), triggeringTime);
					  }
				  }
		
		}
		
	}
	 
	
	public ArrayList<ChunkLayer> ExtractLayer (ArrayList<VideoChunk> list, int layer,int number){
		ArrayList<ChunkLayer> l = new ArrayList<ChunkLayer>();
		for (int j=list.size()-1; j>=0; j--){
			VideoChunk c = list.get(j);
			if (c.isLayerPresent(layer))
				l.add(c.extractLayer(layer));
			if (l.size()==number-1)
				break;
		}
		return l;
	}
	
	
	
	/**
	 * Invia al nodo client di destinazione, la porzione video newResource partendo dal tempo 
	 * triggerTime
	 * 
	 * @param clientNode
	 * @param newResource
	 * @param triggeringTime
	 */
	public void sendChunkLayer(StreamingPeer clientNode,ChunkLayer newResource, float triggeringTime){
		
		//Verifico se devo degradare la velocita' di download del nodo client in base alle
		//sue connessioni in ingresso attive
		double clientDownloadSpeed = 0.0;
		if( clientNode.getDownloadActiveConnection() > 0 )
			clientDownloadSpeed = clientNode.getDownloadSpeed() / (double)clientNode.getDownloadActiveConnection();
		else
			clientDownloadSpeed = clientNode.getDownloadSpeed();
		
		float appTime = nextLayerArrivalTime(this.getUploadSpeed(),clientDownloadSpeed,newResource);
		
		float time = triggeringTime + appTime;
		
		
		// TODO: commentato per velocizzare la simulazione
	    StreamingPeerNewVideoResourceEvent newPeerResEvent = (StreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(StreamingPeerNewVideoResourceEvent.class,time);
		newPeerResEvent.setOneShot(true);
		newPeerResEvent.setAssociatedNode(clientNode);
		newPeerResEvent.setResourceValue(newResource);
		Engine.getDefault().insertIntoEventsList(newPeerResEvent);
		
		
		//TODO: parte aggiunta al posto della precedente per velocizzare simulazione
		/*clientNode.addNewVideoResource(newResource,triggeringTime);
		
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		//devi reinviare la risorsa che ho ottenuto a tutti i peer che sto servendo (tenendo conto del layer)
		
		for(int index = 0 ; index < clientNode.getServedPeers().size(); index++)
		{
			
			
			if (clientNode.getServedPeers().get(index).getLayer()== newResource.getLayerIndex()){
			
				//getLogger().fine("Sono: " + clientNode.getKey() + " Invio a: " + clientNode.getServedPeers().get(index).getPeer().getKey() + " Layer: " + newResource.getLayerIndex() + " chunk: " + newResource.getChunkIndex());
			
          if (!clientNode.getServedPeers().get(index).getPeer().isLayerNeeded(newResource.getChunkIndex(),newResource.getLayerIndex() ) && newResource.getChunkIndex() >  clientNode.getServedPeers().get(index).getPeer().getInitChunk())	
		   	
				//if( newResource.getChunkIndex() >  clientNode.getServedPeers().get(index).getPeer().getInitChunk())		
				
        	  
        	  clientNode.sendChunkLayer(clientNode.getServedPeers().get(index).getPeer(), newResource, triggeringTime);
			}				
			
		}*/
		//END PARTE AGGIUNTA
		
	}
	
	/**
	 * Determina  il tempo in cui dovra' essere schedulato il nuovo arrivo di un chunk al destinatario
	 * in base alla velocita' di Upload del fornitore e quella di Download del cliente.
	 * @param providerUploadSpeed
	 * @param clientDownloadSpeed
	 * @return
	 */
	private float nextLayerArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, ChunkLayer layer) {
		
		double time = 0.0;
		double minSpeed = Math.min(  (providerUploadSpeed  / (double) this.getActiveConnection()) , clientDownloadSpeed);
		double chunkMbitSize = (double)( (double) layer.getLayerSize() / 1024.0 );
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
	
	public boolean findLayerFromProvider(ChunkLayer layer,float triggeringTime){
		
		//Controllo tra i miei fornitori se hanno la porzione che sto cercando
		if( this.sourceStreamingNode.get(layer.getLayerIndex()) != null ){
			
			//!isLayerReceived(this.getVideoResource(),index,layer)
			
			//if(this.sourceStreamingNode.getVideoResource().contains(chunk))
			if (isLayerReceived(this.sourceStreamingNode.get(layer.getLayerIndex()).getVideoResource(),layer.getChunkIndex(),layer.getLayerIndex()))
			{		
				layer.setSourceNode(this.sourceStreamingNode.get(layer.getLayerIndex()));
				layer.setOriginalTime(triggeringTime);
				
				//if( !this.neededChunk.contains(chunk.getChunkIndex()) )
				if (!isLayerNeeded(layer.getChunkIndex(),layer.getLayerIndex()))
					this.sourceStreamingNode.get(layer.getLayerIndex()).sendChunkLayer(this, layer, triggeringTime);
				
				return true;
			}
		}	
		
		//Controllo tra i miei fornitori se hanno la porzione che sto cercando
		if( this.serverNode.get(layer.getLayerIndex()) != null ){
			
			//if(this.serverNode.getVideoResource().contains(chunk))
			//{		
				layer.setSourceNode(this.serverNode.get(layer.getLayerIndex()));
				layer.setOriginalTime(triggeringTime);
				
				if (!isLayerNeeded(layer.getChunkIndex(),layer.getLayerIndex()))
					this.serverNode.get(layer.getLayerIndex()).sendChunkLayer(this, layer, triggeringTime);
				
				return true;
			//}
		}	
		
		return false;

	}
	
	/**
	 * Funzione che controlla periodicamente il Buffer delle porzioni video
	 * per verificare se ci sono dei pezzi mancanti.
	 * 
	 */
	public void updateVideoBufferList(float triggeringTime) {
	
		ArrayList<ChunkLayer> missingLayer = new ArrayList<ChunkLayer>(); 
		
		if(this.getVideoResource().size() >= 5)
		{
			//Prima passo tutti i videochunk interi mancanti poi i singoli livelli
			
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
						
						//aggiungo ai layer che necessito tutti quelli del chunk mancante
						
						/*if( !this.neededChunk.contains(newChunk.getChunkIndex()))
						{
							this.neededChunk.add(newChunk.getChunkIndex());
							missingChunk.add(newChunk);
						}*/
						
						for (int i = 0; i<this.getNumberOfLayer();i++){
							if (!isLayerNeeded(newChunk.getChunkIndex(),i)){
								this.neededLayer.add(new ChunkLayer(i,newChunk.getChunkIndex()));
								missingLayer.add(new ChunkLayer(i,newChunk.getChunkIndex()));
								
							}
							
						}
						
					}
				}
				
				//vado a richiedere i layer mancanti dei chunk ricevuti
				for (int i = 0; i<this.getNumberOfLayer();i++){
					//se nn l'ho gi� ricevuto e nn l'ho gi� richiesto allora lo richiedo
					if ( !this.getVideoResource().get(index).isLayerPresent(i) && !isLayerNeeded(this.getVideoResource().get(index).getChunkIndex(),i)){
						this.neededLayer.add(new ChunkLayer(i,this.getVideoResource().get(index).getChunkIndex()));
						missingLayer.add(new ChunkLayer(i,this.getVideoResource().get(index).getChunkIndex()));
					}
				}
				
			}
		}
		
		
		//String my ="";
		//for(int j = 0 ; j < this.getVideoResource().size(); j++)
			//my = my + " " + this.getVideoResource().get(j).getChunkIndex();
			
		
		//String miss ="";
		//for(int j = 0 ; j < missingChunk.size(); j++)
			//miss = miss + " " + missingChunk.get(j).getChunkIndex();
		
		
		
		
		//System.out.println("Id: "+ this.getKey() + " " + my + " / " + miss );
		

		
		//if(missingChunk.size()>0)
		//System.out.println(missingChunk.size());
		
		
		
		for(int i = 0 ; i < missingLayer.size(); i++)
			this.findLayerFromProvider(missingLayer.get(i), triggeringTime);
	
	}
	

	//Controlla, data una lista di videoChunk se almeno in un chunk � presente il layer desiderato
	public boolean hasLayer(ArrayList<VideoChunk> list,int layer){
		for (Iterator<VideoChunk> i = list.iterator(); i.hasNext();){
			VideoChunk c = (VideoChunk) i.next();
			if (c.isLayerPresent(layer))
				return true;
		}
		return false;
	}
	
	//Controlla, data una lista e l'indice del videoChunk se � presente un determinato layer
	public boolean isLayerReceived(ArrayList<VideoChunk> list,int chunkIndex,int layer){
		for (Iterator<VideoChunk> i = list.iterator(); i.hasNext();){
			VideoChunk c = (VideoChunk) i.next();
			if ((c.getChunkIndex()==chunkIndex) && (c.isLayerPresent(layer)))
				return true;
		}
		return false;
	}
	
	//Controlla se un layer di uno specifico  chunk � gi� stato richiesto
	public boolean isLayerNeeded(int chunkIndex,int layer){
		for (Iterator<ChunkLayer> i = this.neededLayer.iterator(); i.hasNext();){
			ChunkLayer l = (ChunkLayer) i.next();
			if ((l.getChunkIndex()==chunkIndex) && (l.getLayerIndex()==layer))
				return true;
		}
		return false;
	}
	
	
	public void addNewLayer(ChunkLayer layer) {
		boolean found = false;
		
		for (Iterator i = this.getVideoResource().iterator(); i.hasNext();) {
			VideoChunk c = (VideoChunk) i.next();
			if (c.getChunkIndex() == layer.getChunkIndex()) {
				c.getLayers().add(layer);
				found = true;
				//se � il layer base allora considero il chunk come ricevuto
				if (layer.getLayerIndex()==0){
					  this.totalChunkReceived++;
					//Controllo se si e' verificata un eventuale deadline (ricezione fuori ordine dei chunk)
					  if( layer.getChunkIndex() < this.indexOfLastReceivedChunk )
							this.deadlineNumber++;
				}  
				break;
			}
		}

		if (!found) {
			ServerPeer s = (ServerPeer) Engine.getDefault().getNodes().get(0);
			VideoChunk c = new VideoChunk(layer.getChunkIndex(), s.getChunkSize());
			c.setOriginalTime(layer.getOriginalTime());
			c.setSourceNode(layer.getSourceNode());
			c.insertLayer(layer);
			this.videoResource.add(c);
			//se � il layer base allora considero il chunk come ricevuto
			if (layer.getLayerIndex()==0)
			  this.totalChunkReceived++;
			
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

	public ArrayList<VideoChunk> getVideoResource() {
		return videoResource;
	}

	public ArrayList<StreamingPeer> getSourceStreamingNode() {
		return sourceStreamingNode;
	}

	public void setSourceStreamingNode(ArrayList<StreamingPeer> sourceStreamingNode) {
		this.sourceStreamingNode = sourceStreamingNode;
	}

	public ArrayList<ServerPeer> getServerNode() {
		return serverNode;
	}

	public void setServerNode(ArrayList<ServerPeer> serverNode) {
		this.serverNode = serverNode;
	}

	public ArrayList<ServedPeer> getServedPeers() {
		return servedPeers;
	}

	public void setServedPeers(ArrayList<ServedPeer> servedPeers) {
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

	/*
	 * public ArrayList<Integer> getNodeDepth() {
		return nodeDepth;
	}

	public void setNodeDepth(ArrayList<Integer> nodeDepth) {
		this.nodeDepth = nodeDepth;
	}

	*/
	
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

	public int getDuplicateLayerNumber() {
		return duplicateLayerNumber;
	}

	public void setDuplicateLayerNumber(int duplicateLayerNumber) {
		this.duplicateLayerNumber = duplicateLayerNumber;
	}

	public ArrayList<ChunkLayer> getNeededLayer() {
		return neededLayer;
	}

	public void setNeededLayer(ArrayList<ChunkLayer> neededLayer) {
		this.neededLayer = neededLayer;
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

	public int getNumberOfLayer() {
		return numberOfLayer;
	}

	public void setNumberOfLayer(int numberOfLayer) {
		this.numberOfLayer = numberOfLayer;
	}

	public int getTotalChunkPlayed() {
		return totalChunkPlayed;
	}

	public void setTotalChunkPlayed(int totalChunkPlayed) {
		this.totalChunkPlayed = totalChunkPlayed;
	}

	public int getTotalLayerPlayed() {
		return totalLayerPlayed;
	}

	public void setTotalLayerPlayed(int totalLayerPlayed) {
		this.totalLayerPlayed = totalLayerPlayed;
	}

	
}
