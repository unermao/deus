package it.unipr.ce.dsg.deus.example.chord;

	import it.unipr.ce.dsg.deus.core.Event;
	import it.unipr.ce.dsg.deus.core.SchedulerListener;
	import it.unipr.ce.dsg.deus.impl.event.BirthEvent;
	import it.unipr.ce.dsg.deus.impl.event.DeathEvent;
	import it.unipr.ce.dsg.deus.p2p.event.DisconnectionEvent;
	import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
	import it.unipr.ce.dsg.deus.p2p.node.Peer;


	/**
	 * <p>
	 * This class is used to initialize the events associated to RevolBirthEvent.
	 * </p>
	 * 
	 * @author Marco Muro (marco.muro@studenti.unipr.it)
	 *
	 */
	public class ChordBirthSchedulerListener  implements SchedulerListener {

		public void newEventScheduled(Event parentEvent, Event newEvent) {
			ChordBirthEvent be = (ChordBirthEvent) parentEvent; 
			if (newEvent instanceof ChordConnectionNode) {
				((ChordConnectionNode) newEvent).setAssociatedNode((ChordPeer) be.getAssociatedNode());
			} else if (newEvent instanceof DisconnectionEvent) {
				((DisconnectionEvent) newEvent).setAssociatedNode((ChordPeer) be.getAssociatedNode());
				((DisconnectionEvent) newEvent).setNodeToDisconnectFrom(null);
			} else if (newEvent instanceof DeathEvent) {
				((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
//			} else if (newEvent instanceof ChordDiscoveryEvent) {
//				((ChordDiscoveryEvent) newEvent).setAssociatedNode((ChordPeer) be.getAssociatedNode());
			}
		}
	
}
