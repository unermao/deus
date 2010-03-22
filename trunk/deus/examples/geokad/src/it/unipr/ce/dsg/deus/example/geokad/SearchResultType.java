/**
 * 
 */
package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Vittorio Sozzi
 * 
 */
public class SearchResultType {
	
	private TreeSet<GeoKadPeerInfo> foundNodes = null;
	private boolean valueFound = false;

	public SearchResultType(GeoKadPeerInfo peer) {
		foundNodes = new TreeSet<GeoKadPeerInfo>(new GeoKadDistanceComparator(peer));
	}

	public void add(GeoKadPeerInfo peer) {
		foundNodes.add(peer);
	}

	public void addAll(Collection<? extends GeoKadPeerInfo> collPeer) {
		foundNodes.addAll(collPeer);
	}

	public TreeSet<GeoKadPeerInfo> getFoundNodes() {
		return foundNodes;
	}

	public void setFoundNodes(TreeSet<GeoKadPeerInfo> foundNodes) {
		this.foundNodes = foundNodes;
	}

	public boolean isValueFound() {
		return valueFound;
	}

	public void setValueFound(boolean valueFound) {
		this.valueFound = valueFound;
	}

	public int size() {
		return foundNodes.size();
	}
}
