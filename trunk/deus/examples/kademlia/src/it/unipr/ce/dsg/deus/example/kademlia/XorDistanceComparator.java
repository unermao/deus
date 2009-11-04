package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Comparator;

public class XorDistanceComparator implements Comparator<GeoKadPeer> {

	private int currKey = 0;

	public XorDistanceComparator(int resourceKey) {
		currKey = resourceKey;
	}

	public int compare(GeoKadPeer arg0, GeoKadPeer arg1) {
		return ((arg0.getKey() ^ currKey) - (arg1.getKey() ^ currKey));
	}
}
