package it.unipr.ce.dsg.deus.example.chordStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.example.chordStreaming.LogChordRingStatsEvent.MyComp;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;


/**
 * <p>
 * ChordPeers are characterized by different kinds of resources.
 * Every Peers have a own fingerTable in which there are a groups of successors of the node.
 * the first entry of the fingerTable is called successor of the Peer.
 * Every Peers have also a parameters with his predecessor in the Chord Ring.
 * a group of methods create and refresh the fingerTable providing stabilization of the network.
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 *
 */


public class ChordPeer extends Peer {
	
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };
	private static final String FINGER_TABLE_SIZE = "fingerTableSize";
	private static final String VIDEO_TITLE_1 = "videoTitle1";
	private static final String VIDEO_TITLE_2 = "videoTitle2";
	private static final String VIDEO_TITLE_3 = "videoTitle3";
	private int fingerTableSize = 0;
	private ChordPeer predecessor = null;
	public ChordPeer fingerTable[] = null;
	
	private String videoName = "Matrix";
	private boolean serverId = false;
	private int counter= 0;
//	private int check = 0;
	private boolean isPublished = false;
	private boolean isStarted = false;
	private Integer sequenceNumber = -1;
	private int arrivalNumber = -1;
	@SuppressWarnings("unused")
	private int numMinResources = 0;
	private int numMaxResources = 0;
	private int numConnections = -1;
	private int lastPlayingResource = -1;
	
	//variabili per statistiche
	private int countFailedDiscovery = 0;
	private int countSearch = 0;
	private int countIndirectServing = 0;
	private int countFindedResource = 0;
	private int countFindedOtherResource =0;
	private int countCorrectBuffer =0;
	private int countFirstVideo = 0;
	private int countSecondVideo = 0;
	private int countThirdVideo = 0;
	
	public ArrayList<String> videoList = new ArrayList<String>();
	public ArrayList<ChordResourceType> chordResources = new ArrayList<ChordResourceType>();
	public ArrayList<ChordResourceType> consumableResources = new ArrayList<ChordResourceType>();
	public ArrayList<ChordPeer> servedPeers = new ArrayList<ChordPeer>();
	public HashMap<String,Integer> KeyToSequenceNumber = new HashMap<String,Integer>();
	public ArrayList<Integer> missingSequenceNumber = new ArrayList<Integer>();
	public ArrayList<ChordResourceType> bufferVideo = new ArrayList<ChordResourceType>();
	public ArrayList<ChordResourceType> rePublish = new ArrayList<ChordResourceType>();
	
	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);

		if (params.getProperty(FINGER_TABLE_SIZE) == null)
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " param is expected.");
		if (params.containsKey(VIDEO_TITLE_1))
			this.videoList.add((params.getProperty(VIDEO_TITLE_1)));
		if (params.containsKey(VIDEO_TITLE_2))
		this.videoList.add((params.getProperty(VIDEO_TITLE_2)));
		if (params.containsKey(VIDEO_TITLE_3))
			this.videoList.add((params.getProperty(VIDEO_TITLE_3)));

		try {
			fingerTableSize = Integer.parseInt(params
					.getProperty(FINGER_TABLE_SIZE));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " must be a valid int value.");
		}
		setNumMaxResources(Math.pow(2, fingerTableSize));
		
	}

	public Object clone() {
		
		ChordPeer clone = (ChordPeer) super.clone();
		clone.sequenceNumber = -1;
		clone.arrivalNumber = -1;
		clone.numConnections = -1;
		clone.isPublished = false;
		clone.serverId = false;
		clone.isStarted = false;
		clone.predecessor = null;
		clone.lastPlayingResource = -1;
		clone.countFailedDiscovery = 0;
		clone.countSearch = 0;
		clone.countFindedResource = 0;
		clone.countIndirectServing =0;
		clone.countFindedOtherResource=0;
		clone.countCorrectBuffer=0;
		clone.countFirstVideo = 0;
		clone.countSecondVideo = 0;
		clone.countThirdVideo = 0;
		clone.fingerTable = new ChordPeer[fingerTableSize];
		clone.chordResources = new ArrayList<ChordResourceType>();
		clone.consumableResources = new ArrayList<ChordResourceType>();
		clone.servedPeers = new ArrayList<ChordPeer>();
		clone.missingSequenceNumber = new ArrayList<Integer>();
		clone.bufferVideo = new ArrayList<ChordResourceType>();
	
		return clone;
	}

	public ChordPeer getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(ChordPeer predecessor) {
		this.predecessor = predecessor;
	}

	public ChordPeer getSuccessor() {
		return fingerTable[0];
	}

	public void setSuccessor(ChordPeer successor) {
		fingerTable[0] = successor;
	}

	public ChordPeer[] getFingerTable() {
		return fingerTable;
	}

	public void initFirstFingerTable() {
		for (int i = 0; i < fingerTableSize; i++)
			fingerTable[i] = this;
	}

	/**
	 * <p>
	 * This method is called by an arrival peer that ask to an another peer on the network
	 * what's his successor, his predecessor and his initial fingerTable
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void initFingerTable(ChordPeer gatewayNode) {
		setSuccessor(gatewayNode
				.findSuccessor(calculateNextNodeId(getKey(), 0)));
		setPredecessor(getSuccessor().getPredecessor());
		getSuccessor().setPredecessor(this);

		for (int i = 0; i < fingerTableSize - 1; i++) {
			if (isInInterval(calculateNextNodeId(getKey(), i + 1), getKey(),
					fingerTable[i].getKey(), true, false)) {
				fingerTable[i + 1] = fingerTable[i];
			} else {
				fingerTable[i + 1] = gatewayNode
						.findSuccessor(calculateNextNodeId(getKey(), i + 1));
			}
		}
	}

	/**
	 * <p>
	 * This method is used to find the successor of the peer identified by the argument
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public ChordPeer findSuccessor(int nodeId) {
		return findPredecessor(nodeId).getSuccessor();
	}

	/**
	 * <p>
	 * This method is used to find the predecessor of the peer identified by the argument
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public ChordPeer findPredecessor(int nodeId) {
		ChordPeer p = this;
		while (!isInInterval(nodeId, p.getKey(), p.getSuccessor().getKey(),
				false, true)) {
			p = p.closestPrecedingFinger(nodeId);
			if (p.equals(this))
				return getPredecessor();
		}
		return p;
	}

	/**
	 * <p>
	 * This method is used to find the peer that have the identificator more similar to the
	 * identifcator passed the argument
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public ChordPeer closestPrecedingFinger(int nodeId) {
		for (int i = fingerTableSize; i > 0; i--) {
			if (isInInterval(fingerTable[i - 1].getKey(), getKey(), nodeId,
					false, false)) {
				return fingerTable[i - 1];
			}

		}
		return this;
	}

	/**
	 * <p>
	 * This method is used to refresh the Peer's fingerTables that
	 * are in his fingerTable using findPredecessor and updateFingers
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void updateOthers() {
		ChordPeer p = null;
		for (int i = 0; i < fingerTableSize; i++) {
			p = findPredecessor(calculateNextNodeId(getKey(), i, true));
			p.updateFingerTable(this, i);
		}

	}

	/**
	 * <p>
	 * This method is used to update the Peer's fingerTables of the Peer that are
	 * in the new peer's fingerTable
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void updateFingerTable(ChordPeer s, int entry) {
		if (isInInterval(s.getKey(), getKey(), fingerTable[entry].getKey(),
				true, false)) {
			fingerTable[entry] = s;
			getPredecessor().updateFingerTable(s, entry);
		}

	}

	/**
	 * <p>
	 * This method is used to determine if an identificator is in an specificated 
	 * interval of the Chord Ring
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */	
	private boolean isInInterval(int nodeId, int a, int b, boolean isAIncluded,
			boolean isBIncluded) {

		if (a == b)
			return true;

		if (a < b) {
			if (nodeId == a && isAIncluded)
				return true;

			if (isAIncluded && isBIncluded) {
				if (nodeId >= a && nodeId <= b)
					return true;
			} else if (isAIncluded && !isBIncluded) {
				if (nodeId >= a && nodeId < b)
					return true;
			} else if (!isAIncluded && isBIncluded) {
				if (nodeId > a && nodeId <= b)
					return true;
			} else if (!isAIncluded && !isBIncluded) {
				if (nodeId > a && nodeId < b)
					return true;
			}
		} else {
			if (nodeId == b && isBIncluded)
				return true;

			if (isAIncluded && isBIncluded) {
				if ((nodeId >= a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId <= b))
					return true;
			} else if (isAIncluded && !isBIncluded) {
				if ((nodeId >= a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId < b))
					return true;
			} else if (!isAIncluded && isBIncluded) {
				if ((nodeId > a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId <= b))
					return true;
			} else if (!isAIncluded && !isBIncluded) {
				if ((nodeId > a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId < b))
					return true;
			}
		}
		return false;
	}

	private int calculateNextNodeId(int nodeId, int step) {
		return calculateNextNodeId(nodeId, step, false);
	}

	/**
	 * <p>
	 * This method is used to move forward and back on the key space and calculate
	 * the correct identificator for the operations 
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	private int calculateNextNodeId(int nodeId, int step, boolean subtract) {

		if (subtract) {
			nodeId = (int) (((nodeId - Math.pow(2, step)) % Engine.getDefault()
					.getKeySpaceSize()));
		} else {
			nodeId = (int) (((nodeId + Math.pow(2, step)) % Engine.getDefault()
					.getKeySpaceSize()));
		}

		if (nodeId < 0)
			nodeId += Engine.getDefault().getKeySpaceSize();
		if (nodeId == Engine.getDefault().getKeySpaceSize())
			nodeId = 0;

		return nodeId;
	}

	/**
	 * <p>
	 * This method is used to stabilize the successors of the Chord Ring
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void stabilize() {
		ChordPeer x = getSuccessor().getPredecessor();
		if (isInInterval(x.getKey(), getKey(), getSuccessor().getKey(), false,
				false))
			setSuccessor(x);
		getSuccessor().notify(this);
	}

	/**
	 * <p>
	 * This method is used to refresh in a random way the peer's fingerTables
	 * of every peer of the network
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void fixFingers() {
		int i = Engine.getDefault().getSimulationRandom().nextInt(
				fingerTableSize - 1) + 1;
		fingerTable[i] = findSuccessor(calculateNextNodeId(getKey(), i + 1));
	}

	/**
	 * <p>
	 * This method is used to stabilize the predecessors of the Chord Ring
	 * when a successor is changed
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void notify(ChordPeer stabilizingNode) {
		if (getPredecessor() == null
				|| isInInterval(stabilizingNode.getKey(), getPredecessor()
						.getKey(), getKey(), false, false))
			setPredecessor(stabilizingNode);

	}

	/**
	 * <p>
	 * This method is used to publish the Peer's resources given a resource's reference
	 * to the node
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void publishResources() {
		
		ChordResourceType resource = null;
		
		if(this.getServerId())
		{
			for(int i = counter; i < counter+4; i++)
			{
				resource = chordResources.get(i);
				int resource_key = resource.getResource_key();
				ChordPeer successorKey = findSuccessor(resource_key);
				if(successorKey != this )
					{
					//if(!successorKey.chordResources.contains(resource))
					createExchangeResourceEvent(this,successorKey,resource);
					}
				else
				{
					//System.out.println(successorKey);
					if(resource.getResource_key() != 85)
						createExchangeResourceEvent(this,successorKey.getSuccessor(),resource);
					//this.rePublish.add(resource);
					//creare evento che la ripubblica in un secondo momento vicino pero..
//					ChordPeer successor = this.getSuccessor();
//					successor.chordResources.add(resource);
//					successor.setIsPublished(true);
//					resource.addOwners(successor);
//					resource.removeOwners(this);
			}
			}
		counter+= 4;
		
		}
		else{
		 
			for(int i = 0; i < chordResources.size(); i++)
			{
				resource = chordResources.get(i);
				int resource_key = resource.getResource_key();

				ChordPeer successorKey = findSuccessor(resource_key);
				if(successorKey != this )
					{
					if(!successorKey.chordResources.contains(resource))
						createExchangeResourceEvent(this,successorKey,resource);
					}			
			}
		}
	}



	/**
	 * <p>
	 * This method is used to search the Peer's resources situated on a node of the 
	 * chordPeer
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	public void searchForAResource(String videoName , int max) {
	
		this.setCountSearch();
		ChordPeer possessorPeer = null;
		ChordResourceType resourceToFind= null;
//		System.out.println(videoName+max);
		max++;
		this.setSequenceNumber(max);
		videoName = this.generateUUID(videoName + max);
		int resourceKey = KeyToSequenceNumber.get(videoName);
		
		try {
			resourceToFind = new ChordResourceType(resourceKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			possessorPeer = this.findSuccessor(resourceKey);
			if(possessorPeer.chordResources.contains(resourceToFind) && possessorPeer.getNumConnections() < 7)
			{
				int index = possessorPeer.chordResources.indexOf(resourceToFind);
				resourceToFind = possessorPeer.chordResources.get(index);
				//this.searchResults.add(resourceToFind);
				possessorPeer.incrementNumConnections();
				createFindedResourceEvent(this,possessorPeer,resourceToFind);
				
					if(!possessorPeer.servedPeers.contains(this))
						possessorPeer.servedPeers.add(this);
					if(possessorPeer.servedPeers.size() > 7)
						possessorPeer.servedPeers.remove(0);
			}
				else if(possessorPeer.getNumConnections() >= 5)
				{
					boolean test = false;	
					for(int i = 0; i < possessorPeer.servedPeers.size();i++)
						{
						if(possessorPeer.servedPeers.get(i).consumableResources.contains(resourceToFind))
						{
							test = true;
						possessorPeer.servedPeers.get(i).incrementNumConnections();
						createFindedResourceEvent(this,possessorPeer,resourceToFind);
						break;
						}			
				}
					if(!test)
						{
							resourceToFind.setResource_key(-1);
							//this.consumableResources.add(resourceToFind);
							this.setCountFailedDiscovery();
							max--;
							setSequenceNumber(max);
						}
				}
		else
		{	
			resourceToFind.setResource_key(-1);
			//this.consumableResources.add(resourceToFind);
			this.setCountFailedDiscovery();
			max--;
			setSequenceNumber(max);
		}
	}

	private void createExchangeResourceEvent(ChordPeer senderNode, ChordPeer receiverNode ,ChordResourceType resourceToExchange) {
		
		ChordDataExchangeEvent exchangeEv = (ChordDataExchangeEvent) Engine
		.getDefault().createEvent(
				ChordDataExchangeEvent.class,
				Engine.getDefault().getVirtualTime()
						+ expRandom(1));
	//getLogger().fine("ExchangeResource event: " + exchangeEv);
   
	exchangeEv.setAssociatedNode(senderNode);
	exchangeEv.setHasSameAssociatedNode(true);
	exchangeEv.setOneShot(true);
	exchangeEv.setResourceToExchange(resourceToExchange);
	exchangeEv.setReceiverNode(receiverNode);
	Engine.getDefault().insertIntoEventsList(exchangeEv);
		
	}
	
private void createFindedResourceEvent(ChordPeer searchedNode, ChordPeer servingNode ,ChordResourceType findedResource) {
		
	ChordFindedResourceEvent findedEv = (ChordFindedResourceEvent) Engine
		.getDefault().createEvent(
				ChordFindedResourceEvent.class,
				Engine.getDefault().getVirtualTime()
						+ expRandom(1));
//	getLogger().fine("findedEv event: " + findedEv);
   
	findedEv.setAssociatedNode(searchedNode);
	findedEv.setHasSameAssociatedNode(true);
	findedEv.setOneShot(true);
	findedEv.setFindedResource(findedResource);
	findedEv.setServingNode(servingNode);
	Engine.getDefault().insertIntoEventsList(findedEv);
		
	}

	/**
	 * <p>
	 * This method is used to refresh periodically the operation of publish resources make sure
	 * resources's position on the chordRing 
	 * chordPeer
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	
	public void refreshpublish() {
		if(!this.getServerId() && !this.fingerTable[0].getServerId())
		this.fingerTable[0].publishResources();
		
	}

	public int getNumMaxResources() {
		return numMaxResources;
	}

	public void setNumMaxResources(double d) {
		this.numMaxResources = (int) d;
	}

	public void disconnectChordNode() {

		ChordPeer successorNode = this.getSuccessor();
		ChordPeer predecessorNode = this.getPredecessor();
		
		for(int i = 0; i < chordResources.size(); i++)
		createExchangeResourceEvent(this,successorNode,chordResources.get(i));
		this.setConnected(false);
		this.setIsPublished(false);
		
		int pos = Engine.getDefault().getNodes().indexOf(this);
			if (pos > -1)
			Engine.getDefault().getNodes().remove(pos);
		
		predecessorNode.setSuccessor(successorNode);
		predecessorNode.fingerTable[0] = successorNode;
		successorNode.setPredecessor(predecessorNode);
		
		getLogger().fine("\tdisconnectedNode: " + this.getKey() + "\tsuccessorNode: " + this.getSuccessor().getKey() + "\tpredecessorNode: " + this.getPredecessor().getKey());
		for(int c = 0; c < this.chordResources.size(); c++)
		getLogger().fine("\tdisconnectedNode's resource " + c + "\t: " + this.chordResources.get(c).getResource_key());
		
		getLogger().fine("\tnew predecessor's successor: " + successorNode.getPredecessor().getKey() + "\tnew successor's predecessor: " + predecessorNode.getSuccessor().getKey());
		
		for(int d = 0; d < successorNode.chordResources.size(); d++)
			getLogger().fine("\tsuccessortDisconnectedNode's resource " + d + "\t: " + successorNode.chordResources.get(d).getResource_key());	
	}

	/**
	 * <p>
	 * This method is called when a node go out to the ChordRing. He gives its resources to the successor's node
	 * and update predecessor's successor and successor's predecessor
	 * </p>
	 * 
	 * @author  Matteo Agosti (matteo.agosti@unipr.it)
	 * @author  Marco Muro (marco.muro@studenti.unipr.it)
	 */
	
	public void dyingNode() {
		ChordPeer successorNode = this.getSuccessor();
		ChordPeer predecessorNode = this.getPredecessor();
		
		this.setConnected(false);
		this.setIsPublished(false);
		
		int pos = Engine.getDefault().getNodes().indexOf(this);
			if (pos > -1)
			Engine.getDefault().getNodes().remove(pos);
		
		predecessorNode.setSuccessor(successorNode);
		predecessorNode.fingerTable[0] = successorNode;
		successorNode.setPredecessor(predecessorNode);
		
		getLogger().fine("\tdyingNode: " + this.getKey() + "\tsuccessorNode: " + this.getSuccessor().getKey() + "\tpredecessorNode: " + this.getPredecessor().getKey());
		for(int c = 0; c < this.chordResources.size(); c++)
		getLogger().fine("\tdyingNode's resource " + c + "\t: " + this.chordResources.get(c).getResource_key());
		
		getLogger().fine("\tnew predecessor's successor: " + successorNode.getPredecessor().getKey() + "\tnew successor's predecessor: " + predecessorNode.getSuccessor().getKey());
		
		for(int d = 0; d < successorNode.chordResources.size(); d++)
			getLogger().fine("\tsuccessorDyingNode's resource " + d + "\t: " + successorNode.chordResources.get(d).getResource_key());	
	}

//	public void changeOwner(ArrayList<ChordResourceType> ChordResources, ChordPeer newOwner)
//	{
//		
//		for(int i = 0; i <ChordResources.size(); i++)
//			ChordResources.get(i).removeOwners(this);
//		for(int i = 0; i <ChordResources.size(); i++)
//			ChordResources.get(i).addOwners(newOwner);
//	}

	public boolean getServerId() {
		return serverId;
	}

	public void setServerId(boolean serverId) {
		this.serverId = serverId;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setIsPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getArrival() {
		return arrivalNumber;
	}

	public void setArrival(int arrival) {
		this.arrivalNumber = arrival;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public int getNumConnections() {
		return numConnections;
	}

	public void incrementNumConnections() {
		this.numConnections = numConnections+1;
	}
	public void decrementNumConnections() {
		this.numConnections = numConnections-1;
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}
	
	/**
	 * Generate a random Universally Unique Identifier (UUID).
	 * 
	 * @return a random UUID.
	 */
	public String generateUUID(String videoName) {
		
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(videoName.getBytes());
			return bytesToHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convert a byte array into an hex string.
	 * 
	 * @param hash
	 *            the byte array to convert.
	 * @return the hex string representation of the given byte array.
	 */
	private String bytesToHex(byte hash[]) {
		char buf[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(buf);
	}

	public void playVideoBuffer() {

		if(this.bufferVideo.size() >=7)
		{
			setLastPlayingResource(this.bufferVideo.get(6).getSequenceNumber());
			if (this.bufferVideo.get(this.bufferVideo.size() - 1)
					.getSequenceNumber() == this.consumableResources.get(0)
					.getSequenceNumber() || this.bufferVideo.get(this.bufferVideo.size() - 1)
					.getSequenceNumber()+1 == this.consumableResources.get(0)
					.getSequenceNumber())
				this.setCountCorrectBuffer();
			for(int i = 0; i < 7; i++)
				this.bufferVideo.remove(0);
		}
		
	}
	
	public String getVideoName() {	
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public int getCountFailedDiscovery() {
		return countFailedDiscovery;
	}

	public void setCountFailedDiscovery() {
		this.countFailedDiscovery = countFailedDiscovery+1;
	}

	public int getCountSearch() {
		return countSearch;
	}

	public void setCountSearch() {
		this.countSearch = countSearch+1;
	}

	public int getCountIndirectServing() {
		return countIndirectServing;
	}

	public void setCountIndirectServing() {
		this.countIndirectServing = countIndirectServing+1;
	}

	public int getCountFindedResource() {
		return countFindedResource;
	}

	public void setCountFindedResource() {
		this.countFindedResource = countFindedResource+1;
	}

	public int getCountFindedOtherResource() {
		return countFindedOtherResource;
	}

	public void setCountFindedOtherResource() {
		this.countFindedOtherResource = countFindedOtherResource+1;
	}

	public int getLastPlayingResource() {
		return lastPlayingResource;
	}

	public void setLastPlayingResource(int lastPlayingResource) {
		this.lastPlayingResource = lastPlayingResource;
	}

	public int getCountCorrectBuffer() {
		return countCorrectBuffer;
	}

	public void setCountCorrectBuffer() {
		this.countCorrectBuffer = countCorrectBuffer+1;
	}

	public int getCountFirstVideo() {
		return countFirstVideo;
	}

	public void setCountFirstVideo() {
		this.countFirstVideo = countFirstVideo+1;
	}

	public int getCountSecondVideo() {
		return countSecondVideo;
	}

	public void setCountSecondVideo() {
		this.countSecondVideo = countSecondVideo+1;
	}

	public int getCountThirdVideo() {
		return countThirdVideo;
	}

	public void setCountThirdVideo() {
		this.countThirdVideo = countThirdVideo+1;
	}
	
}
