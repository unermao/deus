package it.unipr.ce.dsg.deus.example.chord;

	import it.unipr.ce.dsg.deus.core.InvalidParamsException;
	import it.unipr.ce.dsg.deus.core.NodeEvent;
	import it.unipr.ce.dsg.deus.core.Process;
	import it.unipr.ce.dsg.deus.core.RunException;
	import it.unipr.ce.dsg.deus.p2p.node.Peer;
	import it.unipr.ce.dsg.deus.core.Engine;
import java.util.Properties;


	/**
	 * <p>
	 * This NodeEvent disconnects the associatedNode (which must be a Peer) 
	 * from a specified target Peer (if target is null, the associatedNode 
	 * is disconnected from all its neighbors). 
	 * </p>
	 * 
	 * @author Marco Muro (marco.muro@studenti.ce.unipr.it)
	 *
	 */
	public class ChordDisconnectionEvent extends NodeEvent {

		private Peer target = null;

		public ChordDisconnectionEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			initialize();
		}

		public void initialize() throws InvalidParamsException {

		}

		public void setNodeToDisconnectFrom(Peer target) {
			this.target = target;
		}
		
		public Object clone() {
			ChordDisconnectionEvent clone = (ChordDisconnectionEvent) super.clone();
			clone.target = null;
			return clone;
		}

		public void run() throws RunException {
			System.out.println("Disconnection Event");
			int initialized_node = Engine.getDefault().getNodes().size();
			int random_node = Engine.getDefault().getSimulationRandom().nextInt(initialized_node);
			ChordPeer disconnection_peer = (ChordPeer) Engine.getDefault().getNodes().get(random_node);
			
			getLogger().fine(" successore   nodo disconnesso " + disconnection_peer.getSuccessor() );
			getLogger().fine(" predecessore nodo disconnesso " + disconnection_peer.getPredecessor() );
			
			ChordPeer app = null;
			if(random_node == 0)
			{
			app = (ChordPeer) Engine.getDefault().getNodes().get((random_node)%initialized_node);
			getLogger().fine("nodo predecessore " + app.getId()  );
			getLogger().fine("vecchio successore " + app.getSuccessor() );
			app.setSuccessor(disconnection_peer.successor);
			app.setFingerTableAtFirst(disconnection_peer.successor);
			}
			else
			{
				app = (ChordPeer) Engine.getDefault().getNodes().get((random_node-1)%initialized_node);
				getLogger().fine("nodo predecessore " + app.getId()  );
				app.setSuccessor(disconnection_peer.successor);
				app.setFingerTableAtFirst(disconnection_peer.successor);	
			}
			ChordPeer app2 = (ChordPeer) Engine.getDefault().getNodes().get((random_node+1)%initialized_node);
			getLogger().fine("nodo successore " + app2.getId()  );
			getLogger().fine("vecchio predecessore " + app2.getPredecessor()  );
			app2.setPredecessor(disconnection_peer.predecessor);
			
			getLogger().fine("random " + random_node);
			getLogger().fine("nodo disconnesso " + disconnection_peer.getId() );
			getLogger().fine("nuovo successore " + app.getSuccessor() );
			getLogger().fine("nuovo predecessore " + app2.getPredecessor() );
			}
		

	}
	
	

