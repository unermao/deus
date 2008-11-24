package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class ChordFoundResourceEvent extends NodeEvent{

	private ChordPeer servingNode = null;
	private ChordResourceType findedResource = null;
	
	public ChordFoundResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		ChordPeer searchedNode = (ChordPeer) getAssociatedNode();
		
		if(getServingNode().isConnected())
		{
			if(searchedNode.consumableResources.contains(getFindedResource()))
					searchedNode.setCountDuplicateResources();
			if (!searchedNode.consumableResources.contains(getFindedResource()))	
			{
			searchedNode.setCountReceivedResources();
			if(searchedNode.isPropResources())
				searchedNode.setCountFindedOtherResource();
			else
				searchedNode.setCountFindedResource();
			searchedNode.consumableResources.add(getFindedResource());
			searchedNode.setFindedResources(true);
			}
			orderResources(searchedNode);
			getServingNode().decrementNumConnections();
			
			if(searchedNode.consumableResources.size() >= searchedNode.getBufferDimension())
			{
				for(int c = 0; c<searchedNode.consumableResources.size(); c++)
				{
					if(!searchedNode.bufferVideo.contains(searchedNode.consumableResources.get(c)))
						searchedNode.bufferVideo.add(searchedNode.consumableResources.get(c));
				}
			}
		}
		else
			searchedNode.setMissingResources();
		}

	private void orderResources(ChordPeer searchedNode) {
		
		Collections.sort(getServingNode().consumableResources, new MyComp(null));
		Collections.sort(searchedNode.consumableResources, new MyComp(null));	
	}

	public ChordPeer getServingNode() {
		return servingNode;
	}

	public void setServingNode(ChordPeer servingNode) {
		this.servingNode = servingNode;
	}

	public ChordResourceType getFindedResource() {
		return findedResource;
	}

	public void setFindedResource(ChordResourceType findedResource) {
		this.findedResource = findedResource;
	}
	
	class MyComp implements Comparator<ChordResourceType>{
		public MyComp(Object object) {
		}

		public int compare(ChordResourceType o1, ChordResourceType o2) {
			return o1.compareTo(o2);
		}
	}	

}
