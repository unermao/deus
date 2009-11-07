package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;
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
 * 
 * @author Picone Marco
 * 
 */
public class StreamingConnectionEvent extends NodeEvent {
	
	public StreamingConnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
		System.out.println("Connection Event !");
		
		initialize();
		
		
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		StreamingConnectionEvent clone = (StreamingConnectionEvent) super.clone();
		
		return clone;
	}

	public void run() throws RunException {
		
		StreamingPeer p = (StreamingPeer) associatedNode;
		getLogger().fine("## Connection Event of: " +  associatedNode.getId() +" (" + associatedNode.getKey()  + ") " + p.getNumberOfLayer());
	
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		associatedStreamingNode.setConnected(true);

		for (int layer = associatedStreamingNode.getNumberOfLayer(); layer>0; layer--)
		{
		
			getLogger().fine("Livello: "+ layer);
			ArrayList<Node> layerNode = this.GetLayerNode(Engine.getDefault().getNodes(), layer);
			
		//Se il numero di peer presenti nel sistema (per il mio livello) è maggiore alla dimensione max della lista vicini per il livello 
		if( (layerNode.size() - 1 - associatedStreamingNode.getNeighbors().size()) > associatedStreamingNode.getMaxPartnersNumber())
		{	
			
			int index = 0;
			int cont = 0;
			StreamingPeer peer = null;
			
			int size = (layerNode.size() - 1 );
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (StreamingPeer)layerNode.get(index);
			
			//Associo Casualmente i Vicini per quel livello fino al massimo consentito!
			while( cont < associatedStreamingNode.getMaxPartnersNumber() )
			{

				//Se è connesso, e nn ce l'ho già e nn sono io allora lo aggiungo alla lista dei vicini
				if(		peer.isConnected() 
						&& !associatedStreamingNode.getNeighbors().contains((Peer)peer) 
						&& !associatedStreamingNode.equals(Engine.getDefault().getNodes().get(index))
				  )
				{	
					associatedStreamingNode.addNeighbor((Peer)peer);
					cont++;
					getLogger().fine("Nodo:" + peer.getKey() + " (" + peer.isConnected() + ")");
				}
				
				index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
				peer = (StreamingPeer) layerNode.get(index);
			}
			
				
		}
		else 
		//Se i peer presenti per il livello sono meno del massimo consentito li inserisco tutti come vicini
		//0 è il server	
		{
			
			for(int i = 0; i<layerNode.size(); i++)
			{
					
				StreamingPeer peer = (StreamingPeer) layerNode.get(i);
				
				if(!associatedStreamingNode.equals(layerNode.get(i)) && peer.isConnected() && !associatedStreamingNode.getNeighbors().contains((Peer)peer)){
					getLogger().fine("Nodo:" + peer.getKey() + " (" + peer.isConnected() + ")");
					associatedStreamingNode.addNeighbor((Peer) peer);
				}
				
			}
		}
		
		}
		getLogger().fine("end Connection ##\n");
	}
	
	//Mi restituisce la sottolista dei nodi presenti che desiderano ricevere un certo numero di layer
	private ArrayList<Node> GetLayerNode (ArrayList<Node> list, int layer){
		ArrayList<Node> temp = new ArrayList<Node>();
		for (int i = 1; i<list.size();i++){
			
			StreamingPeer n = (StreamingPeer) list.get(i);
			if (n.getNumberOfLayer() >= layer)
				temp.add(n);
		}
		return temp;
		
	}

}
