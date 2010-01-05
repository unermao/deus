package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.impl.resource.AllocableResource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;


public class NsamPeer extends Peer {

	
	private static final String BATTERY = "battery";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String BANDWIDTH = "bandwidth";
	private static final String IS_RANDOM_INIT = "isRandomInit";
	private static final String QUALITY_LEVEL = "qos";
	private static final String MAX_SERVICE_NUMBER ="maxServiceNum";
	private static final String MAX_ACCEPTED_CONNECTION = "maxAcceptedConnection";
	private static final String MAX_SERVICE_INPUT_NUM = "maxServiceInputNum";
	private static final String MAX_SERVICE_OUTPUT_NUM = "maxServiceOutputNum";
	private static final String SERVICE_INPUT_RANGE = "serviceInputRange";
	private static final String SERVICE_OUTPUT_RANGE="serviceOutputRange";
	
	private boolean isRandomInit = false;

	public static final String ADSL = "adsl";
	public static final String WIFI = "wifi";
	public static final String G3 = "3g";

	private static final float SEARCH_TIMEOUT = 18000; //con VT= 100 --> 1 sec, sono 3 minuti
	private static final int MIN_BATTERY = 20;
	private static final int EXEC_TIME = 10;   //tempo di esecuzione di un servizio atomico 
	//TODO differenziare il tempo di esecuzione a seconda del nodo che sto impiegando!!!!
	//posso calcolarlo come evento casuale expRandom(this.getEventRandom()
	
	
	private int qos = 0;
	private double battery = 0.0;
	private String connectionType = "";


	private int bandwidth = 0;
	private int maxServiceNum =0;
	private int maxServiceInputNum = 0;
	private int maxServiceOutputNum = 0;
	private int serviceInputRange = 0;
	private int serviceOutputRange = 0;

	
	// query log
	private double q = 0;
	private double qh = 0;
	
	private int maxAcceptedConnection = 0;
	private int activeConnections = 0;

	private  ArrayList<NsamService> serviceList = new ArrayList<NsamService>();  //lista di servizi sul nodo 
	private ArrayList<ServiceDiscoveryStructure> serviceSearchList = new ArrayList<ServiceDiscoveryStructure>();  //lista di servizi che sto cercando
	private ArrayList<CompositionElement> requestedServiceList = new ArrayList<CompositionElement>(); //lista dei servizi che mi sono stati chiesti
	private ArrayList<CacheElement> cache = new ArrayList<CacheElement>();  //lista di servizi che ho cercato e che ho usato
	
	

	public NsamPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		serviceList=createServiceList();
		initialize();
	}

	
		
	
public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(BATTERY))
			battery = Double.parseDouble(params.getProperty(BATTERY));
		
		if (params.containsKey(CONNECTION_TYPE))
			connectionType = new String(params.getProperty(CONNECTION_TYPE));
		
		if (params.containsKey(QUALITY_LEVEL))
			qos = Integer.parseInt(params.getProperty(QUALITY_LEVEL));
		
		if (params.containsKey(BANDWIDTH))
			bandwidth = Integer.parseInt(params.getProperty(BANDWIDTH));
		
		if (params.containsKey(QUALITY_LEVEL))
			qos = Integer.parseInt(params.getProperty(QUALITY_LEVEL));
				
		if (params.containsKey(MAX_SERVICE_NUMBER))
		maxServiceNum = Integer.parseInt(params.getProperty(MAX_SERVICE_NUMBER));
		
		if (params.containsKey(MAX_SERVICE_INPUT_NUM))
			maxServiceInputNum = Integer.parseInt(params.getProperty(MAX_SERVICE_INPUT_NUM));
		
		if (params.containsKey(MAX_SERVICE_OUTPUT_NUM))
			maxServiceOutputNum = Integer.parseInt(params.getProperty(MAX_SERVICE_OUTPUT_NUM));
		
		if (params.containsKey(SERVICE_INPUT_RANGE))
			serviceInputRange = Integer.parseInt(params.getProperty(SERVICE_INPUT_RANGE));
		
		if (params.containsKey(SERVICE_OUTPUT_RANGE))
			serviceOutputRange = Integer.parseInt(params.getProperty(SERVICE_OUTPUT_RANGE));
		
		if (params.containsKey(IS_RANDOM_INIT))
			isRandomInit = Boolean.parseBoolean(params.getProperty(IS_RANDOM_INIT));
		
		for (Iterator<Resource> it = resources.iterator(); it.hasNext(); ) {
			Resource r = it.next();
			if (!(r instanceof AllocableResource))
				continue;
			if ( ((AllocableResource) r).getType().equals(MAX_ACCEPTED_CONNECTION) )
				maxAcceptedConnection = (int) ((AllocableResource) r).getAmount();
		}	
		
	}
	
	public Object clone() {
		NsamPeer clone = (NsamPeer) super.clone();
		clone.battery = this.battery;
		clone.connectionType = this.connectionType;
		clone.maxAcceptedConnection = this.maxAcceptedConnection;
		clone.isConnected = true;
		clone.serviceList = this.serviceList;
		clone.qos = this.qos;
	//	clone.cache = new ArrayList<ResourceAdv>();
	//	clone.cachedQueries = new ArrayList<ResourceAdv>();
		return clone;
	}
	

	//gestisce l'arrivo di una notifica di servizio trovato
	//se ero io che lo cercavo lo aggiungo nella mia search list
	//se la ricerca era associata ad un altro servizio (composto) lo associo al mio componente locale e notifico il peer interessato
	
	public void manageNotification(String serviceId, ArrayList<CompositionElement> compo){
		ArrayList<CompositionElement> composition = new ArrayList<CompositionElement>();
		
		if (!serviceSearchList.isEmpty())
			for (int i=0; i<serviceSearchList.size(); i++)
			{ 
				if (serviceSearchList.get(i).getRequestService().getServiceId()== serviceId)
				{
					float searchTime=serviceSearchList.get(i).getRequestTime();
					if ((Engine.getDefault().getVirtualTime()-searchTime) > SEARCH_TIMEOUT)
					{
						getLogger().fine("the discovery has expired...");
						serviceSearchList.remove(i);
						return;
					}
					else {
						if (serviceSearchList.get(i).getAssociatedSearch()==null){
							serviceSearchList.get(i).getCompositionAlternatives().add(compo);
						}
						else if(!requestedServiceList.isEmpty()){
							for (int j=0; j<requestedServiceList.size(); j++)
								if ((requestedServiceList.get(j).getService().getServiceId()==serviceSearchList.get(i).getAssociatedSearch().getServiceId())&&
										!(requestedServiceList.get(j).getPeer()== this))
								{
									CompositionElement comp = new CompositionElement(serviceSearchList.get(i).getLocalComponent(), this);
									composition.add(comp);
									composition.addAll(compo);
									NotifyMessage compNotif = new NotifyMessage(serviceSearchList.get(i).getAssociatedSearch().getServiceId(), composition);
									System.out.println("Sto notificando al peer richiedente che ho trovato il suo servizio!");
									try {
										NsamNotifyMessageEvent nme =(NsamNotifyMessageEvent)Engine.getDefault().createEvent(NsamNotifyMessageEvent.class,Engine.getDefault().getVirtualTime());			
									//	NsamNotifyMessageEvent nme =(NsamNotifyMessageEvent)new NsamNotifyMessageEvent("notify", params, null, notif).createInstance(triggeringTime);
										nme.setNotifyMsg(compNotif);
										nme.setOneShot(true);
										nme.setAssociatedNode(requestedServiceList.get(j).getPeer());
										Engine.getDefault().insertIntoEventsList(nme);
									}catch(Exception e1){
										e1.printStackTrace();
									}
									//FIXME elimino la ricerca che ho appena notificato??
									requestedServiceList.remove(j);
								}
							}
						}
					}
				}
		}
	
	
	//controllo periodico della lista dei servizi trovati; 
	// per ciascuna composizione scelgo la migliore in termini di qos e la eseguo
	//salvo nella cache la composizione che ho eseguito 
	
	public void checkDiscoveredList()
	{
		int quality = 0;
		float arrayQoS[]=null;
		
		for (int i= 0; i<serviceSearchList.size(); i++)
		{
			if (!serviceSearchList.get(i).getCompositionAlternatives().isEmpty())
				for (int j=0; j<serviceSearchList.get(i).getCompositionAlternatives().size(); j++){
					ArrayList<CompositionElement> comp = serviceSearchList.get(i).getCompositionAlternatives().get(i);
					for (int k=0; k<comp.size(); k++) 
						quality =+ comp.get(k).getPeer().getQoS();
					float avgQoS = quality/comp.size();
					arrayQoS[j]=avgQoS;	
					System.out.println("ho il vettore di QoS con num elementi:" + arrayQoS.length);
				}
			//calcola il massimo del vettore di QoS		
		//	int maxIndex = computeMaxQoS(arrayQoS);
			
			//ordino il vettore arrayQoS e prendo il primo elemento
			int[] idxQoS =sortQoS(arrayQoS);
			boolean newChoice = false;
			int maxIndex = 0;
			while ((!newChoice)&&(maxIndex<arrayQoS.length)){
				ArrayList<CompositionElement> composition = new ArrayList<CompositionElement>(serviceSearchList.get(i).getCompositionAlternatives().get(idxQoS[maxIndex]));
				int execTime = EXEC_TIME;
				if (!isCompositionAvailable(composition)){			
					maxIndex++;
					newChoice=false;
				}
				else {	
					
				for (int k= 0; k<composition.size(); k++){
				//impegno le risorse batteria e aumento le connessioni attive
				composition.get(k).getPeer().setBattery(composition.get(k).getPeer().getBattery()-10);
				composition.get(k).getPeer().addActiveConnection();
				//schedulo un evento free resource fra un tempo pari ad uno slot di esecuzione e lo associo al nodo k della lista
				try{
					NsamFreeResourceEvent  free = (NsamFreeResourceEvent)new NsamFreeResourceEvent("free_res", params, null).createInstance(Engine.getDefault().getVirtualTime()+execTime);
					free.setAssociatedNode(composition.get(k).getPeer());
					free.setOneShot(true);
					free.setServ(composition.get(k).getService());
					Engine.getDefault().insertIntoEventsList(free);
				}catch (Exception e2) {
					e2.printStackTrace();
				}
				execTime =+ EXEC_TIME; 		
			}
				}				
			addToCache(serviceSearchList.get(i).getRequestService(), serviceSearchList.get(i).getCompositionAlternatives().get(maxIndex));
			//TODO ogni tanto ripulisci la cache...
	}
			
		}
	}
	
		
	public ArrayList<NsamService> createServiceList (){
		System.out.println("Inizializzo num servizi sul peer corrente ");
		 /*creo una array list che al max ha maxServiceNum elementi */
		 int numServices = Engine.getDefault().getSimulationRandom().nextInt(maxServiceNum);
		 System.out.println("Numero servizi sul peer corrente: " + numServices);
		 for (int i=0; i<numServices; i++)
		 {
			 NsamService service = new NsamService(maxServiceInputNum, maxServiceOutputNum,serviceInputRange, serviceOutputRange);
			 serviceList.add(service); 
		 } 
		 return serviceList;
	}
	
	
public void removeActiveConnection(){
		
		if( this.activeConnections >= 1 )
		 this.activeConnections--;
		else
			System.out.println("ERRORE PROVIDER PEER ! Connessioni Attive = 0 non posso decrementare");
	}
	
	public void addActiveConnection(){
		
		if( this.activeConnections < this.maxAcceptedConnection )
		 this.activeConnections++;
		else
			System.out.println("ERRORE PROVIDER PEER ! Connessioni Attive = "+ this.maxAcceptedConnection  +" non posso incrementare");
	}
	
	
	
public boolean isCompositionAvailable(ArrayList<CompositionElement> composition){
	for (int k=0; k< composition.size(); k++) {				
	//controllo se i nodi della composizione sono ancora connessi e se non hanno raggiunto il max numero di connessioni	
		NsamPeer providerPeer = (NsamPeer)composition.get(k).getPeer();
		if(providerPeer.isConnected)
			if (providerPeer.getActiveConnections()< providerPeer.getMaxAcceptedConnection())
				if(checkBatteryValue(providerPeer))
					return true;
			}
	return false;		
	} 
	
	
	public boolean checkBatteryValue(NsamPeer peer){
		
		if ((peer.getId().equals("mobileNode")) || (peer.getId().equals("mobile3GNode"))){
			if (peer.getBattery()<MIN_BATTERY)
		{
			System.out.println("Nodo mobile e batteria insufficiente!!!");
		/*	if (peer.getBattery()<5){
				peer.setConnected(false);	
				System.out.println("Batteria esaurita, il nodo si disconnette!!!");
			}  */
			return false;
		}
		}
		return true;
	}
	
	public ArrayList<NsamService> getServiceList() {	
		return serviceList;
	}
	
	public void setServiceList(ArrayList<NsamService> serviceList) {
		this.serviceList = serviceList;
	
	}
		
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public void setMaxAcceptedConnection(int maxAcceptedConnection) {
		this.maxAcceptedConnection = maxAcceptedConnection;
	}
	
	public int getMaxAcceptedConnection() {
			return maxAcceptedConnection;
		}
	
	public int getQoS(){
		return qos;
	}
	
	public int getTtlMax() {
		return 7;
	}

	
	public double getQ() {
		return q;
	}

	public void setQ(double q) {
		this.q = q;
	}

	public double getQh() {
		return qh;
	}

	public void setQh(double qh) {
		this.qh = qh;
	}

	public double getQhr() {
		if (this.q == 0)
			return 0.5;
		else
			return this.qh / this.q;
	}
	

	
	public void addToCache(NsamService service, ArrayList<CompositionElement> selectedComp) {
		CacheElement selection = new CacheElement(service.getServiceId(), selectedComp);
		if (cache.size() < getCacheDim(this))    //è la dimensione della cache???
										//TODO trova un modo per imposta
			cache.add(selection);
		else if (cache.size() == getCacheDim(this)) {
			cache.remove(0);
			cache.add(selection);
		}
	} 
	
	/*public int computeMaxQoS(float qos[]){
		int maxIndex = 0;
		float maxQoS = 0;
		maxQoS=qos[0];
		maxIndex=0;
		for(int index=0; index<qos.length; index++) {
			if( qos[index]>maxQoS ) {
			    	  maxQoS=qos[index];
			    	  maxIndex = index;
			}
			   }
		System.out.println("Il massimo e' "+maxQoS+ "all'indice" + maxIndex);
		return maxIndex;
	}  */

	
	/*	
	public void removeServiceFromCache(NsamService currentService) {
		ArrayList<NsamService> newCache = new ArrayList<NsamService>();
		for (Iterator<NsamService> it = cache.iterator(); it.hasNext();) {
			NsamService s = it.next();
			if (!s.equals(currentService))
				newCache.add(s);
		}
		cache = newCache;
	}   */
	
	
	public int[] sortQoS(float[] qos) {
		float temp = 0;
		int[] indexArray = null;
		for(int j=0;j<qos.length;j++) {
			for(int i=j;i<qos.length;i++) {
		if(qos[j]>qos[i]) {
		temp=qos[j];
		qos[j]=qos[i];
		indexArray[j] = i;
		qos[i]=temp;
		}
		else indexArray[j] =j;
		}
			}
		return indexArray;
		}

public int getCacheDim(NsamPeer peer){
	int dim = 0;
	if (peer.getId().equals("pcNode"))
		dim = 10;
	else if (peer.getId().equals("mobileNode"))
		dim= 6;
	else if (peer.getId().equals("mobile3GNode"))
		dim = 4;		
	return dim;
}

public boolean checkDisconnection( NsamPeer peer ){
	
	if(peer == null)
		return true;
	
	if(peer.isConnected() == false)
		return true;
	
	return false;
	
}
	public int getActiveConnections() {
		return activeConnections;
	}

	public void setActiveConnections(int activeConnections) {
		this.activeConnections = activeConnections;
	}
	
	
	public ArrayList<ServiceDiscoveryStructure> getServiceSearchList() {
		return serviceSearchList;
	}

	public void setServiceSearchList(ArrayList<ServiceDiscoveryStructure> serviceSearchList) {
		this.serviceSearchList = serviceSearchList;
	}
	
	public ArrayList<CompositionElement> getRequestedServiceList() {
		return requestedServiceList;
	}
	public double getBattery() {
		return battery;
	}

	public void setBattery(double battery) {
		this.battery = battery;
	}
	
}
