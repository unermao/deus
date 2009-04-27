package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Properties;


/**
 * <p>
 * This event is related to the release of a previously 
 * consumed resource, by updating the corresponding value 
 * on the resource owner.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class FitnessCoolStreamingConnectionEvent extends NodeEvent {

	public FitnessCoolStreamingConnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
		System.out.println("Connection Event !");
		
		initialize();
		
		
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		FitnessCoolStreamingConnectionEvent clone = (FitnessCoolStreamingConnectionEvent) super.clone();
		
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## Connection Event of: " +  associatedNode.getId() +" (" + associatedNode.getKey()  + ")");
		
		FitnessCoolStreamingPeer associatedStreamingNode = (FitnessCoolStreamingPeer) associatedNode;
		associatedStreamingNode.setConnected(true);
			
		//System.out.println(((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getIsp());
		//System.out.println(((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getCity());
		//Assegno casualmente al nodo un ISP e la CITTA		
//		if(((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getCity()!=0)
//			associatedStreamingNode.setIsp(Engine.getDefault().getSimulationRandom().nextInt(((CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getIsp()));
//		else 	
//			associatedStreamingNode.setIsp(0);
		
		//BATTERY
		if(associatedStreamingNode.getId().equals("pcNodeHigh"))
		{
			double randomBattey = 100.0*Engine.getDefault().getSimulationRandom().nextDouble() + 80.0;
			if(randomBattey > 100.0)
				randomBattey = 100.0;
			
			associatedStreamingNode.setBattery(randomBattey);
		}
		
		if(associatedStreamingNode.getId().equals("pcNode"))
		{
			double randomBattey = 100.0*Engine.getDefault().getSimulationRandom().nextDouble() + 80.0;
			if(randomBattey > 100.0)
				randomBattey = 100.0;
			
			associatedStreamingNode.setBattery(randomBattey);
		}
		
		
		if(((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getCity()!=0)
			associatedStreamingNode.setCity(Engine.getDefault().getSimulationRandom().nextInt(((FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0)).getCity()));
		else 	
			associatedStreamingNode.setCity(0);
		
		
		if(associatedStreamingNode.getId().equals("pcNode")){
			// con probablità 0.84 va in ISP 0 / 0.77
			// con probablità 0.16 va in ISP 1 / 0.23
			int random = Engine.getDefault().getSimulationRandom().nextInt(100);
			
			//0.9
//			if( random < 84 )
//				associatedStreamingNode.setIsp(0);
//			else associatedStreamingNode.setIsp(1);
			
			//0.3
//			if( random < 73 )
//				associatedStreamingNode.setIsp(0);
//			else associatedStreamingNode.setIsp(1);
			
			//0.2
//			if(random < 70)
//			associatedStreamingNode.setIsp(0);
//		else if(random >= 70 && random <= 98)
//			associatedStreamingNode.setIsp(1);
//		else associatedStreamingNode.setIsp(2);
			
			//0.6
			if( random < 80 )
				associatedStreamingNode.setIsp(0);
			else associatedStreamingNode.setIsp(1);
			
		}
		if(associatedStreamingNode.getId().equals("pcNodeHigh")){			
			// con probablità 0.78 va in ISP 0 / 0.58
			// con probablità 0.21 va in ISP 1 / 0.41
			// con probablità 0.01 va in ISP 2 / 0.01
			int random = Engine.getDefault().getSimulationRandom().nextInt(100);
			
			//0.9
//			if(random < 78)
//				associatedStreamingNode.setIsp(0);
//			else if(random >= 78 && random <= 98)
//				associatedStreamingNode.setIsp(1);
//			else associatedStreamingNode.setIsp(2);
			
			//0.3
//			if(random < 53)
//				associatedStreamingNode.setIsp(0);
//			else //if(random >= 53 )
//				associatedStreamingNode.setIsp(1);
			//else associatedStreamingNode.setIsp(2);
			
			//0.6
			if(random < 34)
			associatedStreamingNode.setIsp(0);
		else if(random >= 34 && random <= 93)
			associatedStreamingNode.setIsp(1);
		else associatedStreamingNode.setIsp(2);
//			
			
		}
		if(associatedStreamingNode.getId().equals("superNode")){
			// con probablità 0.93 va in ISP 1 / 0.73
			// con probablità 0.07 va in ISP 2 / 0.27
			int random = Engine.getDefault().getSimulationRandom().nextInt(100);
			
			//0.9
//			if(random < 93)
//				associatedStreamingNode.setIsp(1);
//			else associatedStreamingNode.setIsp(2);
//			
			//0.3
//			associatedStreamingNode.setIsp(2);
			
			//0.6
			if(random < 41)
				associatedStreamingNode.setIsp(1);
			else associatedStreamingNode.setIsp(2);
			
		}
		//TODO ISP Decidere come assegnare i vicini in base all'ISP e alla città
		if(associatedStreamingNode.isIncentiveBased())
		{
		 	ArrayList<Peer> neighbors = bootstrap(associatedStreamingNode);		 			 	
		 			 		 			 	
		 	if( neighbors.size() - 1 > associatedStreamingNode.getMaxPartnersNumber())
			{
				getLogger().fine("Ci sono : " + Engine.getDefault().getNodes().size() +" elementi");
			
				int index = 0;
								
				FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)neighbors.get(index);								
				
				//Associo Casualmente i Vicini fino al massimo consentito!
				while( associatedStreamingNode.getNeighbors().size() < associatedStreamingNode.getMaxPartnersNumber() )
				{

					if(		peer.isConnected() 
							&& !associatedStreamingNode.getNeighbors().contains((Peer)peer) 
							&& !associatedStreamingNode.equals(Engine.getDefault().getNodes().get(index))
					  )
					{	
						associatedStreamingNode.addNeighbor((Peer)peer);
						getLogger().fine("Nodo:" + peer.getKey() + " (" + peer.isConnected() + ")");
					}
					
					index ++ ;
					peer = (FitnessCoolStreamingPeer)neighbors.get(index);
				}
				
					
			}
			else //Se i peer presenti sono meno del massimo consentito li inserisco tutti come vicini
			{
				for(int i = 1; i<neighbors.size(); i++)
				{
						
					FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer) Engine.getDefault().getNodes().get(i);
					
					if(!associatedStreamingNode.equals(Engine.getDefault().getNodes().get(i)) && peer.isConnected()){
						
						getLogger().fine("Nodo:" + Engine.getDefault().getNodes().get(i).getKey() + " (" + peer.isConnected() + ")");
						
						associatedStreamingNode.addNeighbor((Peer) Engine.getDefault().getNodes().get(i));
					}
					
				}
			}			
		}
		
		else
		{
		//Se ci sono almeno un numero di peer superiore al massimo 
		if( Engine.getDefault().getNodes().size() - 1 > associatedStreamingNode.getMaxPartnersNumber())
		{
			getLogger().fine("Ci sono : " + Engine.getDefault().getNodes().size() +" elementi");
		
			int index = 0;
			FitnessCoolStreamingPeer peer = null;
			
			int size = (Engine.getDefault().getNodes().size() - 1 );
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (FitnessCoolStreamingPeer)Engine.getDefault().getNodes().get(index);								
			
			//Associo Casualmente i Vicini fino al massimo consentito!
			while( associatedStreamingNode.getNeighbors().size() < associatedStreamingNode.getMaxPartnersNumber() )
			{

				if(		peer.isConnected() 
						&& !associatedStreamingNode.getNeighbors().contains((Peer)peer) 
						&& !associatedStreamingNode.equals(Engine.getDefault().getNodes().get(index))
				  )
				{	
					associatedStreamingNode.addNeighbor((Peer)peer);
					getLogger().fine("Nodo:" + peer.getKey() + " (" + peer.isConnected() + ")");
				}
				
				index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
				peer = (FitnessCoolStreamingPeer)Engine.getDefault().getNodes().get(index);
			}
			
				
		}
		else //Se i peer presenti sono meno del massimo consentito li inserisco tutti come vicini
		{
			for(int i = 1; i<Engine.getDefault().getNodes().size(); i++)
			{
					
				FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer) Engine.getDefault().getNodes().get(i);
				
				if(!associatedStreamingNode.equals(Engine.getDefault().getNodes().get(i)) && peer.isConnected()){
					getLogger().fine("Nodo:" + Engine.getDefault().getNodes().get(i).getKey() + " (" + peer.isConnected() + ")");
					associatedStreamingNode.addNeighbor((Peer) Engine.getDefault().getNodes().get(i));
				}
				
			}
		}			
		
		}
		
		getLogger().fine("end Connection ##");
		
		
		associatedStreamingNode.gossipProtocol(associatedStreamingNode,1);
		
	}

	
	private ArrayList<Peer> bootstrap(FitnessCoolStreamingPeer associatedStreamingNode) {

		ArrayList<Node> nodes = Engine.getDefault().getNodes();
		
		ArrayList<Peer> appList = new ArrayList<Peer>();		
				 
		for(int i = 1 ; i < nodes.size() ; i++)
		 {
			 FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)nodes.get(i);
		  
			 if(appList.size() == 0){				 
				 appList.add(peer);
			 }
						
			 else
			 {
				 for(int j = 0 ; j < appList.size(); j++)
				 {		    
					 FitnessCoolStreamingPeer peerApp = (FitnessCoolStreamingPeer)appList.get(j);	    					 					
					  
					 // Ordino in base alla presenza del chunk tra i miei vicini
					 if( calculateGeographicDistance(associatedStreamingNode,peer) <= calculateGeographicDistance(associatedStreamingNode,peerApp)
						&& peer.getUploadSpeed()/peer.getActiveConnection() >= peerApp.getUploadSpeed()/peerApp.getActiveConnection() 	 )
					 {	 					
						 appList.add(j, peer);
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
		 
		return appList;
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

}
