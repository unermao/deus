package it.unipr.ce.dsg.deus.example.chordStreaming;

	import it.unipr.ce.dsg.deus.core.InvalidParamsException;
	import it.unipr.ce.dsg.deus.core.NodeEvent;
	import it.unipr.ce.dsg.deus.core.Process;
	import it.unipr.ce.dsg.deus.core.RunException;
	import it.unipr.ce.dsg.deus.core.Engine;
	import java.util.Properties;
	import java.util.Random;


	/**
	 * <p>
	 * This NodeEvent disconnects the associatedNode (which must be a ChordPeer) 
	 * from a specified target Peer (if target is null, the associatedNode 
	 * is disconnected from all its neighbors). 
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author Marco Muro (marco.muro@studenti.ce.unipr.it)
	 *
	 */
	public class ChordDisconnectionEvent extends NodeEvent {

		public ChordDisconnectionEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			initialize();
		}

		public void initialize() throws InvalidParamsException {

		}

		public Object clone() {
			ChordDisconnectionEvent clone = (ChordDisconnectionEvent) super.clone();
			return clone;
		}

		public void run() throws RunException {
			
			Random random = new Random();
			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node = random.nextInt(initialized_nodes);
			ChordPeer disconnectedNode = (ChordPeer) Engine.getDefault().getNodes().get(random_node);
			disconnectedNode.disconnectChordNode();
		}

	}
	
	

