package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;

public class NsamService {
	
	private ArrayList<Integer> serviceInput = new ArrayList<Integer>();
	private ArrayList<Integer> serviceOutput = new ArrayList<Integer>();
	private NsamPeer interestedNode = null;
	private boolean found = false;
	private String id = null;
	private Peer owner = null;
	
	
	public NsamService (int maxInput, int maxOutput, int inRange, int outRange){
		int numIn = Engine.getDefault().getSimulationRandom().nextInt(maxInput);
		for (int i=0; i<numIn; i++){
			int  nextInput = Engine.getDefault().getSimulationRandom().nextInt(inRange);
			id += nextInput;
			serviceInput.add(nextInput);
			if (i!=(numIn-1))
				id+="-";
			else id+="+";			
		}
		int numOut = Engine.getDefault().getSimulationRandom().nextInt(maxOutput);
		for (int i=0; i<numOut; i++){
			int nextOutput = Engine.getDefault().getSimulationRandom().nextInt(outRange);
			serviceOutput.add(nextOutput);
			id += nextOutput;
			if (i!=(numOut-1))
				id+="-";	
		}
	//	getLogger().fine("generated service ID: " + id);
		System.out.println("generated service ID: " + id);
	}
	
	public NsamService(ArrayList<Integer> serviceInput, ArrayList<Integer> serviceOutput){
		this.serviceInput=  serviceInput;
		this.serviceOutput= serviceOutput;
		
		if(!serviceInput.isEmpty()){
		id = serviceInput.get(0).toString();
		for (int index =1; index<serviceInput.size(); index++)
			id += serviceInput.get(index);
		id+="-";
		}
		else id="-";
		if(!serviceOutput.isEmpty()){
		for (int index =0; index<serviceOutput.size(); index++)
			id += serviceOutput.get(index);  
		}
		System.out.println("ServiceID = " + id);
	}  
	
	public String getServiceId(){
		return id;
	}
	
	
	public ArrayList<Integer> getServiceInput() {
		return serviceInput;
	}

	public ArrayList<Integer> getServiceOutput() {
		return serviceOutput;
	}
	
	public void setServiceInput(ArrayList<Integer> serviceInput) {
		this.serviceInput = serviceInput;
	}
	
	public void setServiceOutput(ArrayList<Integer> serviceOutput) {
		this.serviceOutput = serviceOutput;
	}
	
	public Peer getInterestedNode() {
		return interestedNode;
	}
	public void setInterestedNode(NsamPeer interestedNode) {
		this.interestedNode = interestedNode;
	}
	
	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
	
	public Peer getOwner() {
		return owner;
	}

	public void setOwner(Peer owner) {
		this.owner = owner;
	}
}
