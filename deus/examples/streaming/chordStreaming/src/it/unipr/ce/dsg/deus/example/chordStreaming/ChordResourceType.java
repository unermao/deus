package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.ArrayList;

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
	private Integer sequenceNumber = -1;
	private ArrayList<ChordPeer> Owners = new ArrayList<ChordPeer>();
	private String hash = null;
	private String videoName = null;
	
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

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void addOwners(ChordPeer owner) {
		Owners.add(owner);
	}
	
	public void removeOwners(ChordPeer owner) {
		Owners.remove(owner);
	}

	public int compareTo(ChordResourceType o2) {
		return sequenceNumber.compareTo(o2.sequenceNumber);
		
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	
}
