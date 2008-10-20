package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class ChordDataExchangeEvent extends NodeEvent {
	
	private ChordResourceType resourceToExchange = null;
	private ChordPeer receiverNode = null;
	
	public ChordDataExchangeEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}
	
	@Override
	public void run() throws RunException {
	
		ChordPeer senderNode = (ChordPeer) getAssociatedNode();
	
		if(!getReceiverNode().chordResources.contains(getResourceToExchange()))
		{
			getReceiverNode().chordResources.add(getResourceToExchange());
			if(!senderNode.getServerId())
			senderNode.chordResources.remove(getResourceToExchange());
			getReceiverNode().setIsPublished(true);
			getResourceToExchange().addOwners(getReceiverNode());
			getResourceToExchange().removeOwners(senderNode);
		}
		if(getReceiverNode().chordResources.size() < 2 && !getReceiverNode().isStarted())
		{
			getReceiverNode().setSequenceNumber(getResourceToExchange().getSequenceNumber());
			getReceiverNode().setStarted(true);
		}
}

	public ChordResourceType getResourceToExchange() {
		return resourceToExchange;
	}

	public void setResourceToExchange(ChordResourceType resourceToExchange) {
		this.resourceToExchange = resourceToExchange;
	}

	public ChordPeer getReceiverNode() {
		return receiverNode;
	}

	public void setReceiverNode(ChordPeer receiverNode) {
		this.receiverNode = receiverNode;
	}

}