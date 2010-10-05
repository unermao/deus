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
	private boolean valueFound = false;

	public SearchResultType(D2VPeerDescriptor peer) {
		foundNodes = new TreeSet<D2VPeerDescriptor>(new GeoDistanceComparator(peer));
	}

	public void add(D2VPeerDescriptor peer) {
		if(!this.foundNodes.contains(peer))
			foundNodes.add(peer);
	}

	public void addAll(ArrayList<D2VPeerDescriptor> collPeer) {
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		String app="";
		Object[] array = foundNodes.toArray();
		ArrayList<D2VPeerDescriptor> arrayList = new ArrayList<D2VPeerDescriptor>();
		for(int i=0; i<foundNodes.size(); i++)
		{
			app=app+"#"+((D2VPeerDescriptor)array[i]).getKey();
			arrayList.add((D2VPeerDescriptor)array[i]);
		}
		
		System.out.println(app);
		
		for(int i=0; i<collPeer.size(); i++)
		{
			D2VPeerDescriptor pd = collPeer.get(i);
			
			if(!arrayList.contains(pd))
			{
				System.out.println("Adding Node: " + pd.getKey());
				foundNodes.add(pd);
			}
			
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		//foundNodes.addAll(collPeer);
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
