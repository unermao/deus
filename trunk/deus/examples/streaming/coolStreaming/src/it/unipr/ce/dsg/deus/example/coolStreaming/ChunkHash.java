package it.unipr.ce.dsg.deus.example.coolStreaming;

public class ChunkHash{
	
	private int chunkIndex;
	private int numberOfSend;
	
	public ChunkHash(int chunkIndex, int numberOfSend) {
		super();
		this.chunkIndex = chunkIndex;
		this.numberOfSend = numberOfSend;
	}

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public int getNumberOfSend() {
		return numberOfSend;
	}

	public void setNumberOfSend(int numberOfSend) {
		this.numberOfSend = numberOfSend;
	}
	
	
}