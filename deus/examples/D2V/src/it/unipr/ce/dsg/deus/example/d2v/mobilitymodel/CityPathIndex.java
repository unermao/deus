package it.unipr.ce.dsg.deus.example.d2v.mobilitymodel;

public class CityPathIndex {

	private int index = 0;
	private int maxIndex = 0;
	private boolean isBackward = false;

	public CityPathIndex(int index, int maxIndex) {
		super();
		this.index = index;
		this.maxIndex  = maxIndex-1;
	}

	public boolean hasNextStep()
	{
		//System.out.println("Index: " +  index + " Max: " + maxIndex);
		
		if( (isBackward== false && index == maxIndex) || (isBackward==true && index == -1) || (isBackward==true && index == 0))
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
		if(isBackward == false)
			this.index++;
		else
			this.index--;
	}

	public boolean isBackward() {
		return isBackward;
	}

	public void setBackward(boolean isBackward) {
		this.isBackward = isBackward;
	}
	
}
