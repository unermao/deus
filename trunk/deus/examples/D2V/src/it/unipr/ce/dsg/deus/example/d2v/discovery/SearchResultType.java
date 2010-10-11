/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v.discovery;

import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * @author Marco Picone
 * 
 */
public class SearchResultType {
	

	private ArrayList<D2VPeerDescriptor> foundNodes = null;
	private D2VPeerDescriptor peer = null;

	
	public SearchResultType(D2VPeerDescriptor peer) {
		this.foundNodes  = new ArrayList<D2VPeerDescriptor>();
		this.peer  = peer;
	}

	public void add(D2VPeerDescriptor newPeer) {
		
		if(!this.foundNodes.contains(newPeer))
			this.foundNodes.add(newPeer);
		else
		{
			int index = this.foundNodes.indexOf(newPeer);
			D2VPeerDescriptor old_peer = this.foundNodes.get(index);
			
			//replace old reference with the new one
			if(newPeer.getTimeStamp() > old_peer.getTimeStamp())
				this.foundNodes.set(index, newPeer);
		}
	}

	public void addAll(ArrayList<D2VPeerDescriptor> collPeer) {
		
		for(int i=0; i<collPeer.size(); i++)
		{
			D2VPeerDescriptor pd = collPeer.get(i);
			this.add(pd);
		}
	}

	public D2VPeerDescriptor first() {
		
		Collections.sort(this.foundNodes, new Comparator<D2VPeerDescriptor>() {

			public int compare(D2VPeerDescriptor o1, D2VPeerDescriptor o2) {
		    
				double dist1 = GeoDistance.distance(o1,peer);
				double dist2 = GeoDistance.distance(o2,peer);
					
				if(dist1 == dist2)
					return 0;
				
				if(dist1 < dist2)
					return -1;
			
				if(dist1 > dist2)
					return 1;
				
				return 0;
		    }});
		
		/*
		System.out.println("#####################################################");
		for(int index=0; index<this.foundNodes.size(); index++)
			System.out.println("Peer: " + this.foundNodes.get(index).getKey() + " Distance: " + GeoDistance.distance(this.peer, this.foundNodes.get(index)));
		System.out.println("#####################################################");
		*/
		return this.foundNodes.get(0);
	}

	public void clearAll() {
		this.foundNodes.clear();
	}
		
	public int size() {
		return foundNodes.size();
	}

	public ArrayList<D2VPeerDescriptor> getFoundNodes() {
		return foundNodes;
	}

	public void setFoundNodes(ArrayList<D2VPeerDescriptor> foundNodes) {
		this.foundNodes = foundNodes;
	}

	public D2VPeerDescriptor getPeer() {
		return peer;
	}

	public void setPeer(D2VPeerDescriptor peer) {
		this.peer = peer;
	}

}
