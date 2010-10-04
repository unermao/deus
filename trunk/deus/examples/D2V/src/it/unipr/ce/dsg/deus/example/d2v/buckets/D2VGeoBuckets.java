package it.unipr.ce.dsg.deus.example.d2v.buckets;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.example.d2v.D2VAddPeerInfoEvent;
import it.unipr.ce.dsg.deus.example.d2v.D2VNodeRemoveEvent;
import it.unipr.ce.dsg.deus.example.d2v.D2VPeer;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.example.d2v.util.GeoDistance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

public class D2VGeoBuckets {

	private Vector<ArrayList<D2VPeerDescriptor>> bucket = null;
	private int kValue = 20;
	private double rayDistance = 1.5;
	private int peerCount = 0;

	public D2VGeoBuckets() {
		super();
		this.bucket = new Vector<ArrayList<D2VPeerDescriptor>>();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			this.bucket.add(i, new ArrayList<D2VPeerDescriptor>());
		}
	}

	public D2VGeoBuckets(int kValue, double rayDistance) {
		super();
		this.kValue = kValue;
		this.rayDistance = rayDistance;
		
		this.bucket = new Vector<ArrayList<D2VPeerDescriptor>>();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			this.bucket.add(i, new ArrayList<D2VPeerDescriptor>());
		}
	}

	public D2VGeoBuckets(Vector<ArrayList<D2VPeerDescriptor>> bucket, double rayDistance) {
		super();
		this.bucket = bucket;
		this.rayDistance = rayDistance;
	}
	
	/**
	 * Add newPeer in the right K-Bucket
	 * 
	 * @param newPeer
	 * 
	 */
	public void insertPeer(Properties params,D2VPeerDescriptor myDescr,D2VPeerDescriptor newPeer){
		
		if(!this.containsPeerDescriptor(newPeer))
		{
			try
			{
				D2VPeer peer = (D2VPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
				
				D2VAddPeerInfoEvent nlk = (D2VAddPeerInfoEvent) new D2VAddPeerInfoEvent("node_lookup", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);

				nlk.setOneShot(true);
				nlk.setAssociatedNode(peer);
				nlk.setPeerInfo(myDescr);
				Engine.getDefault().insertIntoEventsList(nlk);
			}
			catch(Exception e)
			{e.printStackTrace();}
			
		}
			
		
		this.removePeer(newPeer);
		
		double distance = GeoDistance.distance(myDescr, newPeer);
		
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<kValue; i++)
		{
			//System.out.println(this.getKey() + "@Distance: " + distance + " IF: " + (double)(i)*rayDistance);
			
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance ) && bucketFounded == false)
			{
					
				//Add the peer in the right bucket
				if(!this.bucket.get(i).contains(newPeer))
				{
					this.bucket.get(i).add(newPeer);
					//D2VPeer peer = (D2VPeer) Engine.getDefault().getNodeByKey(newPeer.getKey());
					//peer.getGb().insertPeer(newPeer, myDescr);
				}
				
				bucketFounded = true;
				
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param peerDescr
	 * @return
	 */
	public D2VPeerDescriptor containsAndReturnPeerDescriptor(D2VPeerDescriptor peerDescr)
	{
		for(int i=0; i<kValue; i++)
			if(this.bucket.get(i).contains(peerDescr))
			{
				int index = this.bucket.get(i).indexOf(peerDescr);
				return this.bucket.get(i).get(index);
			}
		
		return null;
	}
	
	/**
	 * 
	 * @param peerDescr
	 * @return
	 */
	public boolean containsPeerDescriptor(D2VPeerDescriptor peerDescr)
	{
		for(int i=0; i<kValue; i++)
			if(this.bucket.get(i).contains(peerDescr))
				return true;
		
		return false;
	}
	
	
	/**
	 * 
	 * @param distance
	 * @return
	 */
	private int findIndexForDistance(double distance)
	{
		int index = 0;
		boolean bucketFounded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<kValue; i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance) && bucketFounded == false)
			{
				bucketFounded = true;
				index=i;
				break;
			}
		}
		return index;
	}

	/**
	 * 
	 * @param peer
	 * @return
	 */
	public ArrayList<D2VPeerDescriptor> find_node(D2VPeerDescriptor myDesc, D2VPeerDescriptor peer) {
		
		double distance = GeoDistance.distance(myDesc, peer);
		
		int index = findIndexForDistance(distance);
		
		ArrayList<D2VPeerDescriptor> tempResults = new ArrayList<D2VPeerDescriptor>();
		Iterator<D2VPeerDescriptor> it;

		it = this.bucket.get(index).iterator();
		
		while (it.hasNext())
			tempResults.add(it.next());

		int maxSize = kValue;
		
		boolean flag = false;
		int a = 0;
		while (tempResults.size() < maxSize) {
			flag = false;
			try {
				it = this.bucket.get(index + a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				flag = true;
			}

			if (tempResults.size() >= maxSize)
				break;

			
			try {
				it = this.bucket.get(index - a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				if (flag)
					break;
			}
			
			a++;
		}
		if (tempResults.size() > maxSize) {
			tempResults.subList(maxSize, tempResults.size()).clear();
		}
		
		return tempResults;
	}

	/**
	 * @param params 
	 * 
	 */
	public void updateBucketInfo(Properties params, D2VPeerDescriptor myDesc) {
		
		Vector<ArrayList<D2VPeerDescriptor>> localGeoBucketVector = new Vector<ArrayList<D2VPeerDescriptor>>();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			localGeoBucketVector.add(i, new ArrayList<D2VPeerDescriptor>());
		}
		
		for(int i=0; i<kValue; i++)
		{
			for(int j=0; j<this.bucket.get(i).size();j++)
			{
				D2VPeerDescriptor peerInfo = this.bucket.get(i).get(j);
		
				double distance = GeoDistance.distance(myDesc, peerInfo);
					
				boolean bucketFounded = false;
					
					//For each KBucket without the last one that is for all peers out of previous circumferences 
					for(int index=0;index<kValue; index++)
					{
						//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
						if((distance <= (double)(index)*rayDistance) && bucketFounded == false)
						{
							
							//Add the peer in the right bucket
							if(!localGeoBucketVector.get(index).contains(peerInfo))
								localGeoBucketVector.get(index).add(peerInfo);
						
							bucketFounded = true;
						
							break;
						}
					}
					
					//If the peer's distance is very high it will be added in the last available KBucket
					if(bucketFounded == false)
					{
							try
							{
								D2VPeer peer = (D2VPeer) Engine.getDefault().getNodeByKey(myDesc.getKey());
								D2VNodeRemoveEvent nlk = (D2VNodeRemoveEvent) new D2VNodeRemoveEvent("node_lookup", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);

								nlk.setOneShot(true);
								nlk.setAssociatedNode(peer);
								nlk.setPeerInfo(myDesc);
								Engine.getDefault().insertIntoEventsList(nlk);
							}
							catch(Exception e)
							{e.printStackTrace();}
					}

				}
		}
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			this.bucket.set(i, localGeoBucketVector.get(i));
		}
		
	}
	
	/**
	 * 
	 * @param peerInfo
	 */
	public void removePeer(D2VPeerDescriptor peerInfo) {
		for(int i=0; i<kValue; i++)
		{
			for(int j=0; j<this.bucket.get(i).size();j++)
			{
				this.bucket.get(i).remove(peerInfo);
			}
		}
	}
	
	/**
	 * 
	 * @param peerDescriptor
	 * @return
	 */
	public boolean containsPeerInGeoBuckets(D2VPeerDescriptor peerDescriptor) {
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(this.kValue); i++)
		{
			//Find peer index
			int index = this.bucket.get(i).indexOf(peerDescriptor);
			
			if( index != -1)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param peerDescriptor
	 * @return
	 */
	public int indexOfGeoBucketFor(D2VPeerDescriptor peerDescriptor) {
		//Check if the peer is already in some KBuckets
		for(int i=0; i<(this.kValue); i++)
		{
			//Find peer index
			int index = this.bucket.get(i).indexOf(peerDescriptor);
			
			if( index != -1)
			{
				//Set new PeerInfo
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public double evaluatePerMissingNodes(D2VPeerDescriptor myDesc)
	{
		Vector<ArrayList<D2VPeerDescriptor>> localGeoBucketVector = new Vector<ArrayList<D2VPeerDescriptor>>();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			localGeoBucketVector.add(i, new ArrayList<D2VPeerDescriptor>());
		}
		
		for(int peerIndex=0; peerIndex<Engine.getDefault().getNodes().size();peerIndex++)
		{
			Node node = Engine.getDefault().getNodes().get(peerIndex);
			
			if(node.getId().equals("D2VPeer"))
			{
				D2VPeerDescriptor peerInfo = ((D2VPeer)node).createPeerInfo();
				
				double distance = GeoDistance.distance(myDesc, peerInfo);
					
				boolean bucketFounded = false;
					
					//For each KBucket without the last one that is for all peers out of previous circumferences 
					for(int index=0;index<kValue; index++)
					{
						//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
						if((distance <= (double)(index)*rayDistance) && bucketFounded == false)
						{
							
							//Add the peer in the right bucket
							if(!localGeoBucketVector.get(index).contains(peerInfo))
								localGeoBucketVector.get(index).add(peerInfo);
						
							bucketFounded = true;
						
							break;
						}
					}
			}
		}
		
		double optimalNumber = 0.0;
		double realNumber = 0.0;
		
		for (int i = 0; i < kValue; ++i) {
			optimalNumber += localGeoBucketVector.get(i).size();
			realNumber += this.bucket.get(i).size();
		}
		
		return (optimalNumber-realNumber)/optimalNumber;
	}
	
	public Vector<ArrayList<D2VPeerDescriptor>> getBucket() {
		return bucket;
	}

	public void setBucket(Vector<ArrayList<D2VPeerDescriptor>> bucket) {
		this.bucket = bucket;
	}


	public int getK_VALUE() {
		return kValue;
	}


	public void setK_VALUE(int kVALUE) {
		kValue = kVALUE;
	}

	public double getRayDistance() {
		return rayDistance;
	}

	public void setRayDistance(double rayDistance) {
		this.rayDistance = rayDistance;
	}

	public int getPeerCount() {
		this.peerCount = 0;
		
		for(int i=0; i<(this.kValue); i++)
			peerCount += this.bucket.size();
		
		return peerCount;
	}

	public void setPeerCount(int peerCount) {
		this.peerCount = peerCount;
	}
}
