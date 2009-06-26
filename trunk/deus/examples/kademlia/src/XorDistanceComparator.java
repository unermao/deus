package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Comparator;

public class XorDistanceComparator implements Comparator<KademliaPeer> {
	
	private int currKey = 0;
	
	
	public XorDistanceComparator(int resourceKey) {
		currKey = resourceKey; 
		
	}

	
	public int compare(KademliaPeer arg0, KademliaPeer arg1) {
		return ((arg0.getKey()^currKey) - (arg1.getKey()^currKey));
	}
	

}
