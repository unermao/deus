package it.unipr.ce.dsg.deus.example.d2v.buckets;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.example.d2v.D2VAddPeerInfoEvent;
import it.unipr.ce.dsg.deus.example.d2v.D2VNodeRemoveEvent;
import it.unipr.ce.dsg.deus.example.d2v.D2VPeer;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.GeoLocation;
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
	private PeerKnowledgeMap pm = null;

	public D2VGeoBuckets() {
		super();
		this.bucket = new Vector<ArrayList<D2VPeerDescriptor>>();
		this.pm = new PeerKnowledgeMap();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; ++i) {
			this.bucket.add(i, new ArrayList<D2VPeerDescriptor>());
		}
	}

	public D2VGeoBuckets(int kValue, double rayDistance) {
		super();
		this.kValue = kValue;
		this.rayDistance = rayDistance;
		this.pm = new PeerKnowledgeMap();
		
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
		this.pm = new PeerKnowledgeMap();
		this.rayDistance = rayDistance;
	}
	
	/**
	 * Add newPeer in the right K-Bucket
	 * 
	 * @param newPeer
	 * 
	 */
	public boolean insertPeer(Properties params,D2VPeerDescriptor myDescr,D2VPeerDescriptor newPeer){
		
		boolean isNodeNew = false;
		
		int peerBucketPositionIndex = this.bucketIndexOfPeerDescriptor(newPeer);				
		
		//If peer already exist in bucket
		if(peerBucketPositionIndex != -1)
		{
			int peerIndex = this.bucket.get(peerBucketPositionIndex).indexOf(newPeer);
			D2VPeerDescriptor storedDescriptor = this.bucket.get(peerBucketPositionIndex).get(peerIndex);
			
			if(storedDescriptor.getTimeStamp() > newPeer.getTimeStamp())
				return false;
		}
		
		double distance = GeoDistance.distance(myDescr, newPeer);
		
		boolean founded = false;
		
		//For each KBucket without the last one that is for all peers out of previous circumferences 
		for(int i=0; i<kValue; i++)
		{
			//If the distance is in the circumference with a ray of (numOfKBuckets-1)*rayDistance
			if((distance <= (double)(i)*rayDistance ))
			{		
				founded = true;
				
				//System.out.println("D2VGeoBucket ---> FOUNDED ! GB: " +  i);
				
				//If Peer Descriptor Already exist in buckets
				if(peerBucketPositionIndex != -1)
				{
					//System.out.println("D2VGeoBucket ---> Already Exist: Index: " + peerPositionIndex);
					
					//Is it was in the same bucket
					if(peerBucketPositionIndex == i)
					{
						int bucketIndex = this.bucket.get(i).indexOf(newPeer);
						this.bucket.get(i).set(bucketIndex, newPeer);
					}
					else //If it was in a diffrent bucket
					{
						//Remove ref from the old bucket
						this.bucket.get(peerBucketPositionIndex).remove(newPeer);
						
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
		
		//If Node doesn't exist in peerList send a remove message to remote node
		if(founded==false && peerBucketPositionIndex != -1)
		{
			//Remove Peer references from GB
			this.bucket.get(peerBucketPositionIndex).remove(newPeer);
			
			//Send a remove message to newPeer to remove my reference
			this.sendRemovePeerMessage(params,newPeer,myDescr);			
		}
		
		//System.out.println("Returning: " + isNodeNew);
		return isNodeNew;
	}
	
	public void sendRemovePeerMessage(Properties params,D2VPeerDescriptor destPeer,D2VPeerDescriptor myDescr)
	{
		try
		{
			D2VPeer peer = (D2VPeer) Engine.getDefault().getNodeByKey(destPeer.getKey());
			D2VNodeRemoveEvent nlk = (D2VNodeRemoveEvent) new D2VNodeRemoveEvent("node_remove", params, null).createInstance(Engine.getDefault().getVirtualTime()+1);

			nlk.setOneShot(true);
			nlk.setAssociatedNode(peer);
			nlk.setPeerInfo(myDescr);
			Engine.getDefault().insertIntoEventsList(nlk);
		}
		catch(Exception e)
		{e.printStackTrace();}
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
				
				if(bucketFounded == false)
					this.sendRemovePeerMessage(params, peerInfo, myDesc);	
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
	public void removePeer(D2VPeerDescriptor peerInfo, float timeStamp) {
		
		for(int i=0; i<kValue; i++)
		{
			for(int j=0; j<this.bucket.get(i).size();j++)
			{
				if(this.bucket.get(i).contains(peerInfo))
				{
					//Remove Peer Info
					this.bucket.get(i).remove(peerInfo);
					
					//Save Operation Information in the peer Knowledge 
					this.pm.getPeerMap().put(peerInfo.getKey(), new PeerKnowledge(timeStamp, PeerKnowledge.REMOVE_ACTION));
				}
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
	
	/**
	 * 
	 * Return an array list that contains the percentage of missing node for each GB and as last element the total 
	 * percentage of missing node.
	 * 
	 * @param myDesc
	 * @return
	 */
	public ArrayList<Double> evaluateCompletePerMissingNodes(D2VPeerDescriptor myDesc)
	{
		Vector<ArrayList<D2VPeerDescriptor>> localGeoBucketVector = new Vector<ArrayList<D2VPeerDescriptor>>();
		
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<Double> missingPerGB = new ArrayList<Double>();
		
		//Create the list of KBuckets
		for (int i = 0; i < kValue; i++) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			localGeoBucketVector.add(i, new ArrayList<D2VPeerDescriptor>());
			result.add(0.0);
			missingPerGB.add(0.0);
		}
		
		for(int peerIndex=0; peerIndex<Engine.getDefault().getNodes().size();peerIndex++)
		{
			Node node = Engine.getDefault().getNodes().get(peerIndex);
			
			if(node.getId().equals("D2VPeer"))
			{
				
				D2VPeerDescriptor peerInfo = ((D2VPeer)node).createPeerInfo();
				
				if(peerInfo.getKey() != myDesc.getKey())
				{
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
		}
		
		int totalMissingNumber = 0;
		int totalSum = 0;
		double totalPerMissing = 0.0;
		
		for(int gbIndex=0; gbIndex<localGeoBucketVector.size();gbIndex++)
		{
			totalSum += localGeoBucketVector.get(gbIndex).size();
			
			for(int nodeIndex=0; nodeIndex < localGeoBucketVector.get(gbIndex).size();nodeIndex++)
			{
				D2VPeerDescriptor pd = localGeoBucketVector.get(gbIndex).get(nodeIndex);
				int position = this.indexOfGeoBucketFor(pd);
				
				if(position==-1)
				{
					totalMissingNumber++;
					missingPerGB.set(gbIndex,missingPerGB.get(gbIndex)+1);
				}
			}
		}
		
		if(totalSum > 0)
			totalPerMissing = 100.0*(double)totalMissingNumber/(double)totalSum;
		
		for (int i = 0; i < kValue; i++) 	
			if(totalMissingNumber > 0)
				result.set(i,100.0*(double)((double)missingPerGB.get(i)/(double)totalMissingNumber));
			
		//Add the total percentage of missing nodes
		result.add(totalPerMissing);
		
		/*
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		for (int i = 0; i < result.size(); i++)
		{
			System.out.println("Result("+i+"):" + result.get(i));
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		*/
		
		return result;
	}
	
	public ArrayList<D2VPeerDescriptor> findNodeNearPoint(GeoLocation location,double range) {
		
		ArrayList<D2VPeerDescriptor> resultList = new ArrayList<D2VPeerDescriptor>();
		
		for(int gbIndex=0; gbIndex < this.bucket.size(); gbIndex++)
		{
			for(int peerIndex=0; peerIndex<this.bucket.get(gbIndex).size(); peerIndex++)
			{
				D2VPeerDescriptor pd = this.bucket.get(gbIndex).get(peerIndex);
				double distance = GeoDistance.distance(location, pd.getGeoLocation());
				if(distance<=range)
					resultList.add(pd);
			}
		}
		
		return resultList;
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

	public PeerKnowledgeMap getPm() {
		return pm;
	}

	public void setPm(PeerKnowledgeMap pm) {
		this.pm = pm;
	}

}
