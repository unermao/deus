package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class ChordFindedResourceEvent extends NodeEvent{

	private ChordPeer servingNode = null;
	private ChordResourceType findedResource = null;
	
	public ChordFindedResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		ChordPeer searchedNode = (ChordPeer) getAssociatedNode();
		
		if(getServingNode().isConnected())
		{
			searchedNode.setCountFindedResource();
			searchedNode.consumableResources.add(getFindedResource());
			addOthersResources(searchedNode);
			getServingNode().decrementNumConnections();
			
		if(searchedNode.consumableResources.size() >= searchedNode.getBufferDimension())
			{
			for(int c = 0; c<searchedNode.getBufferDimension()/4; c++)
			{
				if(!searchedNode.bufferVideo.contains(searchedNode.consumableResources.get(c)))
				searchedNode.bufferVideo.add(searchedNode.consumableResources.get(c));
			}
			for(int i = 0; i < searchedNode.consumableResources.size()/4; i++)	
				searchedNode.consumableResources.remove(0);
			}
		}
		else
			searchedNode.setMissingResources();
		}

	private void addOthersResources(ChordPeer searchedNode) {
		
		//Collections.sort(getServingNode().consumableResources, new MyComp(null));
		if(getServingNode().getVideoName() == searchedNode.getVideoName())
		{
			for(int i = 0; i < getServingNode().consumableResources.size()-1; i++)
			{
				int diff = getServingNode().consumableResources.get(i+1).getSequenceNumber() - getServingNode().consumableResources.get(i).getSequenceNumber();
				if( diff == 1 ){
					
					if(!searchedNode.consumableResources.contains(getServingNode().consumableResources.get(i)))
					{	
						searchedNode.consumableResources.add(getServingNode().consumableResources.get(i));
						searchedNode.setCountFindedOtherResource();
					}
				}
		}
		Collections.sort(searchedNode.consumableResources, new MyComp(null));	
		
		}	
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
