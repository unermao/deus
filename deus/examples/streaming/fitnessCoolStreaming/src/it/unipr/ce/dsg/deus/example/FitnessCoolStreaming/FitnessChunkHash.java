package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

public class FitnessChunkHash{
	
	private int chunkIndex;
	private int numberOfSend;
	
	public FitnessChunkHash(int chunkIndex, int numberOfSend) {
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