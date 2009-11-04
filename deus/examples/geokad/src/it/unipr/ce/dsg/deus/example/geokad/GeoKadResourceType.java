package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;

public class GeoKadResourceType extends ResourceAdv {
	private int resourceKey = 0;
	
	public GeoKadResourceType()  {

	}
	
	public GeoKadResourceType(int id)  {
		this.resourceKey = id;
	}
	
	public int getResourceKey() {
		return resourceKey;
	}
	
	public void setResourceKey(int key) {
		this.resourceKey = key;
	}
	
	public boolean equals(GeoKadResourceType obj) {
		if (obj.resourceKey == this.resourceKey)
			return true;
		return false;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof GeoKadResourceType)
			return equals( (GeoKadResourceType) obj);
		return false;
	}

}
