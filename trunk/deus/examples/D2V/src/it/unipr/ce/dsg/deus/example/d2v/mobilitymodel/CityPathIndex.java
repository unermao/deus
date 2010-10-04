package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

public class CityPathIndex {

	private int index = 0;
	private int maxIndex = 0;;

	public CityPathIndex(int index, int maxIndex) {
		super();
		this.index = index;
		this.maxIndex  = maxIndex-1;
	}

	public boolean hasNextStep()
	{
		//System.out.println("Index: " +  index + " Max: " + maxIndex);
		
		if(index == maxIndex)
			return false;
		
		return true;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


	public int getMaxIndex() {
		return maxIndex;
	}

	public void setMaxIndex(int maxIndex) {
		this.maxIndex = maxIndex;
	}

	public void next() {
		this.index++;
	}
	
}
