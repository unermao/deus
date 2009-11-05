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
	
	private TreeSet<GeoKadPeer> foundNodes = null;
	private boolean valueFound = false;

	public SearchResultType(int key) {
		foundNodes = new TreeSet<GeoKadPeer>(new GeoKadDistanceComparator(key));
	}

	public void add(GeoKadPeer peer) {
		foundNodes.add(peer);
	}

	public void addAll(Collection<? extends GeoKadPeer> collPeer) {
		foundNodes.addAll(collPeer);
	}

	public TreeSet<GeoKadPeer> getFoundNodes() {
		return foundNodes;
	}

	public void setFoundNodes(TreeSet<GeoKadPeer> foundNodes) {
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
