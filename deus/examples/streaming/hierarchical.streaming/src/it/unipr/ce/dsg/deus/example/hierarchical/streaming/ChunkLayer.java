package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;

import it.unipr.ce.dsg.deus.core.Node;

public class ChunkLayer {

	private int layerIndex;
	private double layerSize;
	
	//TODO: Guardare se servirà
	private int chunkIndex;
	
	
	private Node sourceNode;
	private float originalTime = 0;
	
	public ChunkLayer(int LayerIndex, double LayerSize,int chunkIndex) {
		super();
		this.layerIndex = LayerIndex;
		this.layerSize = LayerSize;
		this.chunkIndex = chunkIndex;
	}
	
	public ChunkLayer(int LayerIndex, int chunkIndex) {
		this.layerIndex = LayerIndex;
		this.chunkIndex = chunkIndex;
	}
	
	public boolean equals(Object arg0) {
		ChunkLayer layer =(ChunkLayer) arg0;
		if((layer.getLayerIndex() == this.getLayerIndex()) && (layer.getChunkIndex() == this.getChunkIndex()))
			return true;
		else return false;		
	}

	public int getLayerIndex() {
		return layerIndex;
	}

	public void setLayerIndex(int layerIndex) {
		this.layerIndex = layerIndex;
	}

	public double getLayerSize() {
		return layerSize;
	}

	public void setLayerSize(double layerSize) {
		this.layerSize = layerSize;
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

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}
	
}
