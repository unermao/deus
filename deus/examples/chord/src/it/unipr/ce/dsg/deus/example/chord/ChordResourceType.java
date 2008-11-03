package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.impl.resource.ResourceAdv;

/**
 * This class represent the generic resource associated to a chordpeer.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordResourceType extends ResourceAdv {
	
	private int resourceKey = 0;
	
	public ChordResourceType() throws Exception{
	}
	
	public ChordResourceType(int id) throws Exception{
		this.resourceKey = id;
	}

	@Override
	public boolean equals(Object o) {
		int app = ((ChordResourceType) o).getResource_key();
		if(app == this.resourceKey)
			return true;
		else
			return false;
	}

	public int getResource_key() {
		return resourceKey;
	}

	public void setResource_key(int resourceKey) {
		this.resourceKey = resourceKey;
	}
	
}

