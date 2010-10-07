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
	public boolean insertPeer(Properties params,D2VPeerDescriptor myDescr,D2VPeerDescriptor newPeer){
		
		//System.out.println("D2VGeoBucket ---> Peer:" + myDescr.getKey() + " Adding Peer:"+newPeer.getKey());
		
		boolean isNodeNew = false;
		
		int peerPositionIndex = this.bucketIndexOfPeerDescriptor(newPeer);				
		
		double distance = GeoDistance.distance(myDescr, newPeer);
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<kValue; i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance ))
			{		
				//System.out.println("D2VGeoBucket ---> FOUNDED ! GB: " +  i);
				
				//If Peer Descriptor Already exist in buckets
				if(peerPositionIndex != -1)
				{
					//System.out.println("D2VGeoBucket ---> Already Exist: Index: " + peerPositionIndex);
					
					//Is it was in the same bucket
					if(peerPositionIndex == i)
					{
						int bucketIndex = this.bucket.get(i).indexOf(newPeer);
						this.bucket.get(i).set(bucketIndex, newPeer);
					}
					else //If it was in a diffrent bucket
					{
						//Remove ref from the old bucket
						this.bucket.get(peerPositionIndex).remove(newPeer);
						
						//Add new ref int the righ bucket
						this.bucket.get(i).add(newPeer);
					}
						
				}	
				else
				{
					//System.out.println("D2VGeoBucket ---> NOT EXIST: Index: " + peerPositionIndex);
					
					isNodeNew = true;
					
					//Add new ref int the righ bucket
					this.bucket.get(i).add(newPeer);
					
					//Send Add Peer Info Message
					this.sendAddPeerInfoMessage(params, myDescr, newPeer);
				}
				
				
				break;
			}
			
		}
		
		//System.out.println("Returning: " + isNodeNew);
		return isNodeNew;
	}
	
	public void sendAddPeerInfoMessage(Properties params,D2VPeerDescriptor myDescr,D2VPeerDescriptor newPeer)
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
	
	/**
	 * 
	 * @param peerDescr
	 * @return
	 */
	public D2VPeerDescriptor retrievePeerDescriptor(D2VPeerDescriptor peerDescr)
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
	public int bucketIndexOfPeerDescriptor(D2VPeerDescriptor peerDescr)
	{
		for(int i=0; i<kValue; i++)
			if(this.bucket.get(i).contains(peerDescr))
				return i;
		
		return -1;
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
	 * @param peerDescr
	 * @return
	 */
	public ArrayList<D2VPeerDescriptor> find_node(D2VPeerDescriptor myDesc, D2VPeerDescriptor peerDescr) {
		
		double distance = GeoDistance.distance(myDesc, peerDescr);
		
		int index = findIndexForDistance(distance);
		
		ArrayList<D2VPeerDescriptor> tempResults = new ArrayList<D2VPeerDescriptor>();
		Iterator<D2VPeerDescriptor> it;

		it = this.bucket.get(index).iterator();
		
		while (it.hasNext())
			tempResults.add(it.next());

		D2VPeer peer = (D2VPeer)Engine.getDefault().getNodeByKey(myDesc.getKey());
		
		int maxSize = peer.getDiscoveryMaxPeerNumber();
		
		boolean flag = false;
		int a = 0;
		while (tempResults.size() < maxSize) {
			
			flag = false;
			
			try {
				it = this.bucket.get(index + a).iterator();
				while (it.hasNext())
				{
					D2VPeerDescriptor descr = it.next();
					if(!tempResults.contains(descr) && !descr.equals(myDesc))
						tempResults.add(descr);
						
					//tempResults.add(it.next());
				}
			} catch (IndexOutOfBoundsException e) {
				flag = true;
			}

			if (tempResults.size() >= maxSize)
				break;

			
			try {
				it = this.bucket.get(index - a).iterator();
				while (it.hasNext())
				{
					D2VPeerDescriptor descr = it.next();
					if(!tempResults.contains(descr) && !descr.equals(myDesc))
						tempResults.add(descr);
					
					//tempResults.add(it.next());
				}
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
		
		double percentage = 0.0;
		
		if(realNumber > optimalNumber)
			realNumber = optimalNumber;
		
		if(optimalNumber > 0.0)
			percentage = (optimalNumber-realNumber)/optimalNumber;
		
		//System.out.println("############################### K:"+myDesc.getKey()+ " Optimal: " + optimalNumber + " Real: " + realNumber + " %: " + 100.0*percentage);
		
		return 100.0*percentage;
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
			peerCount += this.bucket.get(i).size();
		
		return peerCount;
	}

	public void setPeerCount(int peerCount) {
		this.peerCount = peerCount;
	}
}
