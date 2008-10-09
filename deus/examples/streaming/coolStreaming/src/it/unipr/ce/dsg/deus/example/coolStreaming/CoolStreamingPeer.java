package it.unipr.ce.dsg.deus.example.coolStreaming;


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
 */
public class CoolStreamingPeer extends Peer {

	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String UPLOAD_SPEED = "uploadSpeed";
	private static final String DOWNLOAD_SPEED = "downloadSpeed";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String VIDEO_RESOURCE_BUFFER_LIMIT = "videoResourceBufferLimit";
	private static final String FITNESS_SORT = "fitnessSort"; 
	
	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";
	public static final String G2 = "3g"; 
	public static final String GPRS = "gprs";
	
	private int battery = 0;
	private String connectionType = "";
	private double uploadSpeed = 0.0;
	private double downloadSpeed = 0.0;
	private int videoResourceBufferLimit = 10;
	private double fitnessValue = 0.0;
	private boolean fitnessSort = false;
	private int maxPartnersNumber = 20;

	private long time1;
	private long time2;
	
	private int maxAcceptedConnection = 0;
	private int activeConnection = 0;
	private double onlineTime = 0.0;
	
	private CoolStreamingPeer sourceStreamingNode = null;
	private CoolStreamingServerPeer serverNode = null;
	
	
	private ArrayList<CoolStreamingPeer> servedPeers = new ArrayList<CoolStreamingPeer>();
	private ArrayList<CoolStreamingVideoChunk> videoResource = new ArrayList<CoolStreamingVideoChunk>();
	
	
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
		
		if (params.containsKey(FITNESS_SORT))
			fitnessSort = Boolean.parseBoolean(params.getProperty(FITNESS_SORT));
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
		
		//Aggiorno il valore di fitness del nodo
		this.updateFitnessValue();
		
		time1 = System.currentTimeMillis();
	}
	
	public Object clone() {
	
		CoolStreamingPeer clone = (CoolStreamingPeer) super.clone();

		clone.activeConnection = this.activeConnection;
		clone.battery = this.battery;
		clone.connectionType = this.connectionType;
		clone.fitnessSort = this.fitnessSort;
		clone.fitnessValue = this.fitnessValue;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.maxPartnersNumber = this.maxPartnersNumber;
		clone.onlineTime = this.onlineTime;
		clone.servedPeers = new ArrayList<CoolStreamingPeer>();;
		clone.serverNode = this.serverNode;
		clone.sourceStreamingNode = this.sourceStreamingNode;
		clone.videoResource = new ArrayList<CoolStreamingVideoChunk>(); ;
		clone.videoResourceBufferLimit  = this.videoResourceBufferLimit;
		
		return clone;
	}
	
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}

	public void updateParentsList(){
		
		int startListNumber = this.neighbors.size();
		
		//Aggiorno la fitness del nodo
		this.updateFitnessValue();
		
		//Attraverso la lista dei nodi
		for(int i = 0; i < this.getNeighbors().size(); i++){
			//Se il nodo si e' morto lo rimuovo
			if(this.getNeighbors().get(i) == null){
				this.getNeighbors().remove(i);
			}
			else{
				//Se il nodo si e' disconnesso
				if( !this.getNeighbors().get(i).isConnected() ){
					//Rimuovo il nodo disconnesso
					this.getNeighbors().remove(i);
				}
			}			
		}
		
		//System.out.println(this.getNeighbors().size() + "/" + Engine.getDefault().getNodes().size());
		//Se qualche nodo e' stato rimosso dalla lista dei vicini
		if( this.getNeighbors().size() < this.getMaxPartnersNumber() ){
			

			/*
			int index = 0;
			StreamingPeer peer = null;
			
			int count = 0;
			
			int size = (Engine.getDefault().getNodes().size() - 1 );
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
			
			while( count < 20 ){
				
				if(
						( peer.getServerNode() != null || peer.getSourceStreamingNode() != null )
						&& peer.isConnected() //Se il peer e' connesso
						&& !this.neighbors.contains(peer) //Se non e' gi� tra i miei vicni
						&& !peer.equals(this) //Se non sono io
						&& !this.servedPeers.contains(peer) //Se non lo sto servendo
				  )
					this.addNeighbor(peer);
				
				//Se ho raggiunto il limite massimo esco
				if(this.neighbors.size() == this.maxPartnersNumber)
					break;
				
				index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
				peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
				
				count++;
				
			}
			*/
			
			
			//Cerco tra i miei vicini qualche nuovo contatto
			for(int index = 0; index < this.neighbors.size() ; index++)
			{
	
				
				CoolStreamingPeer peer = (CoolStreamingPeer)this.neighbors.get(index);
				
				if(
						peer.isConnected() //Se il peer e' connesso
						&& !this.neighbors.contains(peer) //Se non e' gi� tra i miei vicni
						&& !peer.equals(this) //Se non sono io
						&& !this.servedPeers.contains(peer) //Se non lo sto servendo
				  )
					this.addNeighbor(peer);
				
				//Se ho raggiunto il limite massimo esco
				if(this.neighbors.size() == this.maxPartnersNumber)
					break;
				
			}
			
			
			/*
			//Se ci sono almeno un numero di peer superiore al massimo 
			if( Engine.getDefault().getNodes().size() - 1 - this.neighbors.size() -1 > this.getMaxPartnersNumber())
			{
				
				int index = 0;
				StreamingPeer peer = null;
				
				int size = (Engine.getDefault().getNodes().size() - 1 );
				
				index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
				peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
				
				//Associo Casualmente i Vicini fino al massimo consentito!
				while( this.getNeighbors().size() < this.getMaxPartnersNumber() )
				{
					
					if(
							peer.isConnected() 
							&& !this.getNeighbors().contains((Peer)peer) 
							&& !this.equals(Engine.getDefault().getNodes().get(index)))
					{	
						this.addNeighbor((Peer)peer);
					}
					
					//Se ho raggiunto il limite massimo esco
					if(this.neighbors.size() == this.maxPartnersNumber)
						break;
					
					index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
					peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
				}

			}
			else
			{

				for(int i = 1; i<Engine.getDefault().getNodes().size(); i++)
				{
					
					StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(i);
					
					if(
						 !this.equals(Engine.getDefault().getNodes().get(i)) 
						 && peer.isConnected()
					   )
					{
						this.addNeighbor((Peer) Engine.getDefault().getNodes().get(i));
					}
					
					//Se ho raggiunto il limite massimo esco
					if(this.neighbors.size() == this.maxPartnersNumber)
						break;
					
				}
			}
			*/
			
		}
		
		//Riordino la lista dei vicini in base alla loro fitness  
		if( this.isFitnessSort() )
			this.sortParentsNodes();
		
		int endListNumber = this.neighbors.size();
		
		if( endListNumber > this.maxPartnersNumber )
			System.out.println("ERRORE LISTA TROPPO GRANDE !!!! ("+ endListNumber + "/" + this.maxPartnersNumber + ")");
		
		
		
	}
	
	public void disconnection(){
		
		//Imposto il nodo come disconnesso
		this.setConnected(false);
		
		//Mi tolgo dal mio fornitore
		if(this.serverNode != null)
			this.serverNode.getServedPeers().remove(this);
		
		if(this.sourceStreamingNode != null)
			this.sourceStreamingNode.getServedPeers().remove(this);
		
		//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
		for( int i = 0 ; i < this.servedPeers.size(); i++){
			
			this.servedPeers.get(i).setSourceStreamingNode(null);
			
			//Lancio l'evento per l'aggiornamento delle liste sul quel nodo
			this.servedPeers.get(i).updateParentsList();
			
		}
		
		//Lancio la funzione di ricerca dei nuovi nodi per quelli ceh stavo servendo
		for( int i = 0 ; i < this.servedPeers.size(); i++){
		
			//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
			this.servedPeers.get(i).findProviderNodeFromLastSegment();	
			//this.servedPeers.get(i).setServerNode((ServerPeer)Engine.getDefault().getNodes().get(0));
		}
		
		//Pulisco la lista dei nodi che stavo fornendo
		this.servedPeers.clear();
		
		Engine.getDefault().getNodes().remove(this);
		
		//Rimuovo tutti i miei vicini
		this.resetNeighbors();
	}
	
	public void updateFitnessValue(){
		
		double newFitness = 0.0;
		
		//Batteria[0,100]% , UploadSpeed [0,1000]Kbit/sec , Online Time[0,24]h, Connessioni Attive[0,100]% 
		newFitness = ( 1.5 * (double)this.battery + 0.4*(double)this.uploadSpeed + 2.083*(double)this.onlineTime ) / (10.0 + (double)(this.activeConnection/this.maxAcceptedConnection)); 
		
		//System.out.println("New Fitness("+ this.getId() + ") : "  + newFitness);
		
		this.fitnessValue = newFitness;
	}
	
	//Metodo utilizzato per simulare il passaggio da una rete 3G ad una 2G
	public void change3GTo2G( String connType, double uploadSpeed, int maxAcceptedConnection ){
		
		//Verifico il parametro connType
		if( connType.equals(G2) ){
			
			this.connectionType = connType;
			this.maxAcceptedConnection = maxAcceptedConnection;
			this.uploadSpeed = uploadSpeed;
			
			//Calcolo la nuova fitness
			this.updateFitnessValue();
			
			//Se il protocollo che valuta la fitness � attivo
			if(this.isFitnessSort())
			{
				//Scollego i nodi che stavo servendo in modo che possano cercare altri fornitori
				for( int i = 0 ; i < this.servedPeers.size(); i++){
					this.servedPeers.get(i).setSourceStreamingNode(null);
					
					//Lancio l'evento per l'aggiornamento dei fornitori per quel nodo
					this.servedPeers.get(i).findProviderNodeFromLastSegment();
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
	
	public void addNewVideoResource(CoolStreamingVideoChunk newVideoRes){
		
		//System.out.println("Parents Size: " + this.neighbors.size());
		
		this.videoResource.add(newVideoRes);
		
		//Se le porzioni di video che ho in memoria superano il limite del buffer rimuoso un elemento
		if(this.videoResource.size() > videoResourceBufferLimit)
			this.videoResource.remove(0);
		
		/*
		for(int i = 0; i<this.getServedPeers().size(); i++ ){
			this.getServedPeers().get(i).addNewVideoResource(newVideoRes);
		}*/
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
	
	public void findFirstProviderNode(){
		
		//System.out.println("findFirstProviderNode");
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho gi� un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode() == null && this.getServerNode() == null )
		{
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() )
				this.sortParentsNodes();
			
			//Cerco all'interno della mia lista di vicini se trovo un fornitore per il flusso video
			for(int i = 0 ; i < this.getNeighbors().size(); i++){
				
				CoolStreamingPeer appSourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(i);
				
				//Mi collego solo, se ha un fornitore se ha le risorse video e se ha la possibilit� di accettare le connessioni e se non � tra la lista di quelli che sto fornendo
				if( 	(appSourceStreamingNode.getServerNode() != null || appSourceStreamingNode.getSourceStreamingNode() != null)
						&& appSourceStreamingNode.getVideoResource().size() > 0 
						&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0){
					
					this.setSourceStreamingNode(appSourceStreamingNode);
					
					//Imposto la connessione attiva con il nodo fornitore trovato
					this.getSourceStreamingNode().addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					this.getSourceStreamingNode().addServedPeer(this);
					
					break;
				}
				
			}
			
			//Se non trovo nessun nodo da cui fornirmi, e non sono gi� connesso al nodo centrale mi collego al server centrale
			if( this.getSourceStreamingNode() == null && this.getServerNode() == null)
			{
					this.setServerNode((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0));
					
					//Imposto la connessione attiva con il server centrale
					this.getServerNode().addActiveConnection();
					
					//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
					this.getServerNode().addServedPeer(this);
				
			}
		}
		
	}
	
	public boolean findProviderNodeFromLastSegment() {
		
		//Devo cercare un fornitore per il filmato soltanto se nn ho gi� un un nodo come fornitore e non mi sto rifornendo dal server centrale
		if( this.getSourceStreamingNode() == null && this.getServerNode() == null )
		{
			//Riordino la lista dei vicini in base alla loro fitness  
			if( this.isFitnessSort() )
				this.sortParentsNodes();
			
			if(this.getVideoResource().size() > 0)
			{
				//Cerco all'interno della mia lista di vicini se trovo un fornitore partendo dal segmento che già posseggo
				for(int i = 0 ; i < this.getNeighbors().size(); i++){
					
					CoolStreamingPeer appSourceStreamingNode = (CoolStreamingPeer)this.getNeighbors().get(i);
					
					//Mi collego solo, se ha un fornitore, se ha le risorse video e se ha la possibilit� di accettare le connessioni e se non � tra la lista di quelli che sto fornendo
					if(     (appSourceStreamingNode.getServerNode() != null || appSourceStreamingNode.getSourceStreamingNode() != null)	
							&& appSourceStreamingNode.isConnected()
							&& !this.servedPeers.contains(appSourceStreamingNode) 
							&& (appSourceStreamingNode.getMaxAcceptedConnection() - appSourceStreamingNode.getActiveConnection())>0)
					{
						
						for(int j = 0; j < appSourceStreamingNode.getVideoResource().size() ;j++)
						{
							for( int myVideoIndex = 0 ; myVideoIndex < this.getVideoResource().size(); myVideoIndex++){

								if(appSourceStreamingNode.getVideoResource().get(j) == this.getVideoResource().get(myVideoIndex))
								{
									
									//System.out.println("Sono: " + this.getKey() + " Ho trovato: " + appSourceStreamingNode.getKey());
									
									this.setSourceStreamingNode(appSourceStreamingNode);
								
									//Imposto la connessione attiva con il nodo fornitore trovato
									appSourceStreamingNode.addActiveConnection();
								
									//Aggiungo il nodo che si sta connettendo alla lista di quelli da fornire
									appSourceStreamingNode.addServedPeer(this);
								
									return true;
								}
							}
						}
					}
			}
			
		}
			
			//Se non trovo nessun nodo da cui fornirmi per una certa porzione rilancio la funzione base di ricerca fornitore
			if( this.getSourceStreamingNode() == null && this.getServerNode() == null)
			{		
				this.setServerNode((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0));
			
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
			CoolStreamingPeer peerOriginal = (CoolStreamingPeer)this.neighbors.get(i);
			
			//La lista � vuota aggiungo direttamente l'elemento
			if(appList.size() == 0)
				appList.add(peerOriginal);
			else
			{
				//Cerco se c'� un Peer con fitness minore
				for(int j = 0 ; j < appList.size(); j++)
				{
					
					CoolStreamingPeer peerApp = (CoolStreamingPeer)appList.get(j);
					
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

	public ArrayList<CoolStreamingPeer> getServedPeers() {
		return servedPeers;
	}

	public void setServedPeers(ArrayList<CoolStreamingPeer> servedPeers) {
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
	
}
