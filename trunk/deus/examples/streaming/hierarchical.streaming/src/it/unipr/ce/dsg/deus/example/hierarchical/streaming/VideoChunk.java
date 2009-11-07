package it.unipr.ce.dsg.deus.example.hierarchical.streaming;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipr.ce.dsg.deus.core.Node;

/**
 * 
 * @author Picone Marco
 * 
 */

public class VideoChunk {
	
	private int chunkIndex;
	private int chunkSize;
	private Node sourceNode;
	private float originalTime = 0;
	private ArrayList<ChunkLayer> layers = new ArrayList<ChunkLayer>();
	
	public VideoChunk(int chunkIndex, int chunkSize) {
		super();
		this.chunkIndex = chunkIndex;
		this.chunkSize = chunkSize;
	}
	
	public boolean equals(Object arg0) {
		VideoChunk chunk =(VideoChunk) arg0;
		if(chunk.getChunkIndex() == this.getChunkIndex())
			return true;
		else return false;		
	}

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	
	//TODO: dimensione guardare come ottenerla
	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	public float getOriginalTime() {
		return originalTime;
	}

	public void setOriginalTime(float originalTime) {
		this.originalTime = originalTime;
	}

	public ArrayList<ChunkLayer> getLayers() {
		return layers;
	}

	public void setLayers(ArrayList<ChunkLayer> layers) {
		this.layers = layers;
	}
	
	public boolean isLayerPresent (int l){
		for (Iterator i = this.layers.iterator(); i.hasNext();){
			ChunkLayer c = (ChunkLayer) i.next();
			if (c.getLayerIndex() == l)
				return true;
		}
		return false;
	}

	public ChunkLayer extractLayer (int l){
		for (Iterator i = this.layers.iterator(); i.hasNext();){
			ChunkLayer c = (ChunkLayer) i.next();
			if (c.getLayerIndex() == l)
				return c;
		}
		return null;
	}
	
	public void insertLayer (ChunkLayer l){
		this.layers.add(l);
		
		//TODO: vedere se attivare o no!
		//OrderLayers();
	}
	
	public void OrderLayers(){
		
		for (int i = 0; i<this.layers.size(); i++){
		  for (int j= i+1; j< this.layers.size(); j++){
			  if (this.layers.get(j).getLayerIndex()<this.layers.get(i).getLayerIndex()){
				  ChunkLayer l = this.layers.get(j);
				  this.layers.set(j, this.layers.get(i));
				  this.layers.set(i, l);
			  }
		  }	
		}
	}
}
