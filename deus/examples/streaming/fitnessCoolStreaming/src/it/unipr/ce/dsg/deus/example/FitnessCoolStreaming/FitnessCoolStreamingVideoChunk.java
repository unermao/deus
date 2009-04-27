package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

import it.unipr.ce.dsg.deus.core.Node;

public class FitnessCoolStreamingVideoChunk {
	
	private int chunkIndex;
	private int chunkSize;
	private Node sourceNode;
	private Node destNode;
	private float originalTime = 0;

	public FitnessCoolStreamingVideoChunk(int chunkIndex, int chunkSize) {
		super();
		this.chunkIndex = chunkIndex;
		this.chunkSize = chunkSize;
	}
	
	@Override
	public boolean equals(Object arg0) {
		FitnessCoolStreamingVideoChunk chunk =(FitnessCoolStreamingVideoChunk) arg0;
		if(chunk.getChunkIndex() == this.getChunkIndex() /*&& chunk.getSourceNode() == this.getSourceNode()
				&& chunk.getDestNode() == this.getDestNode()*/)
			return true;
		else return false;		
	}

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

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

	public Node getDestNode() {
		return destNode;
	}

	public void setDestNode(Node destNode) {
		this.destNode = destNode;
	}

}
