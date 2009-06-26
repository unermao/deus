package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;

public class KademliaResourceType extends ResourceAdv {
	private int resourceKey = 0;
	
	public KademliaResourceType()  {

	}
	
	public KademliaResourceType(int id)  {
		this.resourceKey = id;
	}
	
	public int getResourceKey() {
		return resourceKey;
	}
	
	public void setResourceKey(int key) {
		this.resourceKey = key;
	}
	
	public boolean equals(KademliaResourceType obj) {
		if (obj.resourceKey == this.resourceKey)
			return true;
		return false;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof KademliaResourceType)
			return equals( (KademliaResourceType) obj);
		return false;
	}

}
