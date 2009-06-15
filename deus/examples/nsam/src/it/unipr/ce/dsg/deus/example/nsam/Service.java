package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;

public class Service {
	
	private ArrayList<Integer> serviceInput = new ArrayList<Integer>();
	private ArrayList<Integer> serviceOutput = new ArrayList<Integer>();
	private Peer interestedNode = null;
	
	
	
	
	public Service (int maxInput, int maxOutput, int inRange, int outRange){
		int numIn = Engine.getDefault().getSimulationRandom().nextInt(maxInput);
		for (int i=0; i<numIn; i++)
			serviceInput.add(Engine.getDefault().getSimulationRandom().nextInt(inRange));
		int numOut = Engine.getDefault().getSimulationRandom().nextInt(maxOutput);
		for (int i=0; i<numOut; i++)
			serviceOutput.add(Engine.getDefault().getSimulationRandom().nextInt(outRange));
	}
	
	
	public ArrayList<Integer> getServiceInput() {
		return serviceInput;
	}

	public ArrayList<Integer> getServiceOuput() {
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
	public void setInterestedNode(Peer interestedNode) {
		this.interestedNode = interestedNode;
	}
}
