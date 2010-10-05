/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v.discovery;

import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistanceComparator;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * @author Marco Picone
 * 
 */
public class SearchResultType {
	
	private TreeSet<D2VPeerDescriptor> foundNodes = null;
	private ArrayList<Integer> storedKeys = null;
	private boolean valueFound = false;

	public SearchResultType(D2VPeerDescriptor peer) {
		this.foundNodes = new TreeSet<D2VPeerDescriptor>(new GeoDistanceComparator(peer));
		this.storedKeys = new ArrayList<Integer>();
	}

	public void add(D2VPeerDescriptor peer) {
		if(!this.storedKeys.contains(peer.getKey()))
		{
			this.storedKeys.add(peer.getKey());
			this.foundNodes.add(peer);
		}
	}

	public void addAll(ArrayList<D2VPeerDescriptor> collPeer) {
		
		for(int i=0; i<collPeer.size(); i++)
		{
			D2VPeerDescriptor pd = collPeer.get(i);
			
			if(!this.storedKeys.contains(pd.getKey()))
			{
				this.storedKeys.add(pd.getKey());
				this.foundNodes.add(pd);
			}
			
		}
		
		//foundNodes.addAll(collPeer);
	}

	public void clearAll()
	{
		this.foundNodes.clear();
		this.storedKeys.clear();
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

	public ArrayList<Integer> getStoredKeys() {
		return storedKeys;
	}

	public void setStoredKeys(ArrayList<Integer> storedKeys) {
		this.storedKeys = storedKeys;
	}
}
