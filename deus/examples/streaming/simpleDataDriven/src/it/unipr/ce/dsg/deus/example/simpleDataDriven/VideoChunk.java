package it.unipr.ce.dsg.deus.example.simpleDataDriven;

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
	
	public VideoChunk(int chunkIndex, int chunkSize) {
		super();
		this.chunkIndex = chunkIndex;
		this.chunkSize = chunkSize;
	}
	
	@Override
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

}
