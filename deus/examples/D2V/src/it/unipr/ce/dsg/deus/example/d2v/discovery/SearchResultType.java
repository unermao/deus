/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v.discovery;

import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistanceComparator;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Vittorio Sozzi
 * 
 */
public class SearchResultType {
	
	private TreeSet<D2VPeerDescriptor> foundNodes = null;
	private boolean valueFound = false;

	public SearchResultType(D2VPeerDescriptor peer) {
		foundNodes = new TreeSet<D2VPeerDescriptor>(new GeoDistanceComparator(peer));
	}

	public void add(D2VPeerDescriptor peer) {
		foundNodes.add(peer);
	}

	public void addAll(Collection<? extends D2VPeerDescriptor> collPeer) {
		foundNodes.addAll(collPeer);
	}

	public TreeSet<D2VPeerDescriptor> getFoundNodes() {
		return foundNodes;
	}

	public void setFoundNodes(TreeSet<D2VPeerDescriptor> foundNodes) {
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
