package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
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
			
				if(getReceiverNode().KeyToSequenceNumber.isEmpty())
				{
				if(getReceiverNode().getVideoName().equals(getReceiverNode().videoList.get(0)))
					getReceiverNode().KeyToSequenceNumber.putAll(ChordBirthEvent.KeysSequenceNumbersMap1);
				if(getReceiverNode().getVideoName().equals(getReceiverNode().videoList.get(1)))
					getReceiverNode().KeyToSequenceNumber.putAll(ChordBirthEvent.KeysSequenceNumbersMap2);
				if(getReceiverNode().getVideoName().equals(getReceiverNode().videoList.get(2)))
					getReceiverNode().KeyToSequenceNumber.putAll(ChordBirthEvent.KeysSequenceNumbersMap3);	
				}
			
			getReceiverNode().setIsPublished(true);
			
//			connectingNode.setCountArrivalTime(Engine.getDefault().getVirtualTime());
			getResourceToExchange().addOwners(getReceiverNode());
			getResourceToExchange().removeOwners(senderNode);
		}
		if(getReceiverNode().chordResources.size() < 2 && !getReceiverNode().isStarted())
		{
			getReceiverNode().setSequenceNumber(getResourceToExchange().getSequenceNumber());
			getReceiverNode().setStarted(true);
			getReceiverNode().setCountStartingTime(Engine.getDefault().getVirtualTime());
//			if(getResourceToExchange().getVideoName().equals(getReceiverNode().getVideoName()))
//				getReceiverNode().consumableResources.add(getResourceToExchange());
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