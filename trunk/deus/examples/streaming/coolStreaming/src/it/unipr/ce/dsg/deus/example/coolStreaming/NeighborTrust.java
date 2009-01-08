package it.unipr.ce.dsg.deus.example.coolStreaming;

import it.unipr.ce.dsg.deus.core.Node;

public class NeighborTrust {

	private int key;
	private int trust_value;
		
	
	public NeighborTrust(int key, int trust_value) {
		super();
		this.key = key;
		this.trust_value = trust_value;
	}

	public int getTrust_value() {
		return trust_value;
	}
	
	public void setTrust_value(int trust_value) {
		this.trust_value = trust_value;
	}

	@Override
	public boolean equals(Object obj) {

		Node object = (Node) obj;	
		if(this.key == object.getKey()) 
			return true;
		return false;
	}
	
	
}
