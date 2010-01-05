package it.unipr.ce.dsg.deus.example.nsam;

import java.util.ArrayList;

public class ServiceMatch {
 
	private ArrayList<Boolean> matchingInput = new ArrayList<Boolean>();
	private ArrayList<Boolean> matchingOutput = new ArrayList<Boolean>();
//	private ArrayList<Integer> serviceQuality = new ArrayList<Integer>();
	private int quality = 0;
	
//TODO guarda se mettere un metodo initialize per settare una eventuale array list già esistente a false	
	
 public ServiceMatch(){
	 this.matchingInput=null;
	 this.matchingOutput=null;
 }
 
	public ArrayList<Boolean> getMatchingInput() {
		return matchingInput;
	}

	public ArrayList<Boolean> getMatchingOutput() {
		return matchingOutput;
	}
	
	public int getQuality() {
		return quality;
	}
	
	public void setQuality(int quality){
		this.quality=quality;
	}
	
	public ArrayList<Boolean> updateMatchingInput(boolean isMatching){
		matchingInput.add(isMatching);
		return matchingInput;
	}
	
	public ArrayList<Boolean> updateMatchingOutput(boolean isMatching){
		matchingInput.add(isMatching);
		return matchingOutput;
	}
}


