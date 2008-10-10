package it.unipr.ce.dsg.deus.example.simpleDataDriven;

public class VideoChunk {
	
	private int chunkIndex;
	private int chunkSize;

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

}
