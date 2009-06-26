/**
 * 
 */
package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Vittorio Sozzi
 *
 */
public class SearchResultType {
	private TreeSet<KademliaPeer> foundNodes = null;
	private boolean valueFound = false;
	
	public SearchResultType(int key) {
		foundNodes = new TreeSet<KademliaPeer>(
				new XorDistanceComparator(key));
	}

	
	public void add(KademliaPeer peer) {
		foundNodes.add(peer);
	}
	
	public void addAll(Collection<? extends KademliaPeer> collPeer) {
		foundNodes.addAll(collPeer);
	}
	
	public TreeSet<KademliaPeer> getFoundNodes() {
		return foundNodes;
	}
	public void setFoundNodes(TreeSet<KademliaPeer> foundNodes) {
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
