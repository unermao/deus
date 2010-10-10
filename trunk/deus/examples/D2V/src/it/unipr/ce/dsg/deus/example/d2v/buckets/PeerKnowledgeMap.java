package it.unipr.ce.dsg.deus.example.d2v.buckets;

import java.util.TreeMap;

public class PeerKnowledgeMap {

	private TreeMap<Integer,PeerKnowledge> peerMap = null;

	public PeerKnowledgeMap() {
		super();
		this.peerMap = new TreeMap<Integer, PeerKnowledge>();
	}
	
	public TreeMap<Integer, PeerKnowledge> getPeerMap() {
		return peerMap;
	}

	public void setPeerMap(TreeMap<Integer, PeerKnowledge> kMap) {
		this.peerMap = kMap;
	}
	
}
