package it.unipr.ce.dsg.deus.example.chordStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.example.chordStreaming.ChordFindedResourceEvent.MyComp;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;


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
	private static final String MAX_CONNECTIONS_FAST = "maxConnectionsFast";
	private static final String MAX_CONNECTIONS_MEDIUM = "maxConnectionsMedium";
	private static final String MAX_CONNECTIONS_SLOW = "maxConnectionsSlow";
	private static final String NUM_PUBLISH_SERVER = "numPublishServer";
	private static final String BUFFER_DIMENSION = "bufferDimension";
	private static final String TOTAL_RESOURCES = "totalResources";
	private int fingerTableSize = 0;
	private ChordPeer predecessor = null;
	public ChordPeer fingerTable[] = null;
	
	private String videoName = null;
	private boolean serverId = false;
	private int counter= 0;
	private boolean isPublished = false;
	private boolean isStarted = false;
	private Integer sequenceNumber;
	private int arrivalNumber = -1;
	private int numConnections = 0;
	private int lastPlayingResource = -1;
	private int typePeer = 0;
	private int maxConnectionsFast = -1;
	private int maxConnectionsMedium = -1;
	private int maxConnectionsSlow = -1;
	private int numPublishServer = -1;
	private int bufferDimension = -1;
	private int totalResources = -1;
	
	//variabili per statistiche
	private int countFailedDiscovery = 0;
	private int countSearch = 0;
	private int countIndirectServing = 0;
	private int countFindedResource = 0;
	private int countFindedOtherResource = 0;
	private int countMissBuffer = 0;
	private int countFirstVideo = 0;
	private int countSecondVideo = 0;
	private int countThirdVideo = 0;
	private int countFastPeer = 0;
	private int countMediumPeer = 0;
	private int countSlowPeer = 0;
	private int countMissingResources = 0;
	private int countPlayVideo = 0;
	private int countDuplicateResources = 0;
	
	public ArrayList<String> videoList = new ArrayList<String>();
	public ArrayList<ChordResourceType> chordResources = new ArrayList<ChordResourceType>();
	public ArrayList<ChordResourceType> consumableResources = new ArrayList<ChordResourceType>();
	public ArrayList<ChordPeer> servedPeers = new ArrayList<ChordPeer>();
	public ArrayList<ChordPeer> servingPeers = new ArrayList<ChordPeer>();
	public ArrayList<ChordPeer> MyservingPeers = new ArrayList<ChordPeer>();
	public ArrayList<ChordPeer> MyservedPeers = new ArrayList<ChordPeer>();
	public HashMap<String,Integer> KeyToSequenceNumber = new HashMap<String,Integer>();
	public ArrayList<Integer> missingSequenceNumber = new ArrayList<Integer>();
	public ArrayList<ChordResourceType> bufferVideo = new ArrayList<ChordResourceType>();
	static ArrayList<ChordResourceType> publishResources = new ArrayList<ChordResourceType>();
	
	
	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);

		if (params.getProperty(FINGER_TABLE_SIZE) == null)
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " param is expected.");
		if (params.getProperty(MAX_CONNECTIONS_FAST) == null)
			throw new InvalidParamsException(MAX_CONNECTIONS_FAST
					+ " param is expected.");
		if (params.getProperty(MAX_CONNECTIONS_MEDIUM) == null)
			throw new InvalidParamsException(MAX_CONNECTIONS_MEDIUM
					+ " param is expected.");
		if (params.getProperty(MAX_CONNECTIONS_SLOW) == null)
			throw new InvalidParamsException(MAX_CONNECTIONS_SLOW
					+ " param is expected.");
		if (params.getProperty(NUM_PUBLISH_SERVER) == null)
			throw new InvalidParamsException(NUM_PUBLISH_SERVER
					+ " param is expected.");
		if (params.getProperty(BUFFER_DIMENSION) == null)
			throw new InvalidParamsException(BUFFER_DIMENSION
					+ " param is expected.");
		if (params.getProperty(TOTAL_RESOURCES) == null)
			throw new InvalidParamsException(TOTAL_RESOURCES
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
		
		try {
			maxConnectionsFast = Integer.parseInt(params
					.getProperty(MAX_CONNECTIONS_FAST));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(MAX_CONNECTIONS_FAST
					+ " must be a valid int value.");
		}
		try {
			maxConnectionsMedium = Integer.parseInt(params
					.getProperty(MAX_CONNECTIONS_MEDIUM));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(MAX_CONNECTIONS_MEDIUM
					+ " must be a valid int value.");
		}
		try {
			maxConnectionsSlow = Integer.parseInt(params
					.getProperty(MAX_CONNECTIONS_SLOW));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(MAX_CONNECTIONS_SLOW
					+ " must be a valid int value.");
		}
		try {
			numPublishServer = Integer.parseInt(params
					.getProperty(NUM_PUBLISH_SERVER));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(NUM_PUBLISH_SERVER
					+ " must be a valid int value.");
		}
		try {
			bufferDimension = Integer.parseInt(params
					.getProperty(BUFFER_DIMENSION));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(BUFFER_DIMENSION
					+ " must be a valid int value.");
		}
		try {
			totalResources = Integer.parseInt(params
					.getProperty(TOTAL_RESOURCES));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(TOTAL_RESOURCES
					+ " must be a valid int value.");
		}
		
	}

	public Object clone() {
		
		ChordPeer clone = (ChordPeer) super.clone();
		clone.arrivalNumber = -1;
		clone.numConnections = 0;
		clone.isPublished = false;
		clone.serverId = false;
		clone.isStarted = false;
		clone.predecessor = null;
		clone.lastPlayingResource = -1;
		clone.countFailedDiscovery = 0;
		clone.countSearch = 0;
		clone.countFindedResource = 0;
		clone.countIndirectServing = 0;
		clone.countFindedOtherResource= 0;
		clone.countMissBuffer= 0;
		clone.countFirstVideo = 0;
		clone.countSecondVideo = 0;
		clone.countThirdVideo = 0;
		clone.fingerTable = new ChordPeer[fingerTableSize];
		clone.chordResources = new ArrayList<ChordResourceType>();
		clone.consumableResources = new ArrayList<ChordResourceType>();
		clone.servedPeers = new ArrayList<ChordPeer>();
		clone.missingSequenceNumber = new ArrayList<Integer>();
		clone.bufferVideo = new ArrayList<ChordResourceType>();
		clone.KeyToSequenceNumber = new HashMap<String,Integer>();
		clone.servingPeers = new ArrayList<ChordPeer>();
		clone.MyservedPeers = new ArrayList<ChordPeer>();
		clone.MyservingPeers = new ArrayList<ChordPeer>();
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
			
			for(int i = counter; i < counter+getNumPublishServer(); i++)
			{
				resource = chordResources.get(i);
				publishResources.add(resource);
				int resource_key = resource.getResource_key();
				ChordPeer successorKey = findSuccessor(resource_key);
				if(successorKey != this )
					createExchangeResourceEvent(this,successorKey,resource);
				else
				{
					if(resource.getResource_key() != this.getKey())
						createExchangeResourceEvent(this,successorKey.getSuccessor(),resource);
			}
			}
		counter+= getNumPublishServer();
		
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
	public void searchResources(String videoName , int max) {
	
		this.setCountSearch();
		ChordPeer possessorPeer = null;
		ChordResourceType resourceToFind= null;
		max = max+1;
		this.setSequenceNumber(max);
		videoName = this.generateUUID(videoName + max);
		int resourceKey = KeyToSequenceNumber.get(videoName);
		
		try {
			resourceToFind = new ChordResourceType(resourceKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			possessorPeer = this.findSuccessor(resourceKey);

			int max_connections = setMax_connections(possessorPeer);
			
			if(possessorPeer.chordResources.contains(resourceToFind) && possessorPeer.getNumConnections() < max_connections)
			{

				int index = possessorPeer.chordResources.indexOf(resourceToFind);
				resourceToFind = possessorPeer.chordResources.get(index);
				possessorPeer.incrementNumConnections();
					this.setCountFindedResource();
					createFindedResourceEvent(this,possessorPeer,resourceToFind);
				
					if(!possessorPeer.servedPeers.contains(this))
						possessorPeer.servedPeers.add(this);
					if(possessorPeer.servedPeers.size() > max_connections)
					{
						possessorPeer.servedPeers.get(0).servingPeers.remove(this);
						possessorPeer.servedPeers.remove(0);
					}
			}
				else if(possessorPeer.getNumConnections() >= max_connections-1 )
				{

					boolean isfinded = false;	
					for(int i = 0; i < possessorPeer.servedPeers.size();i++)
						{
						if(possessorPeer.servedPeers.get(i).consumableResources.contains(resourceToFind))
							{
								isfinded = true;
								possessorPeer.servedPeers.get(i).incrementNumConnections();
								this.setCountFindedResource();
								createFindedResourceEvent(this,possessorPeer.servedPeers.get(i),resourceToFind);
								break;
							}			
						}
					if(!isfinded)
						{
							if(publishResources.contains(resourceToFind))
							this.setCountFailedDiscovery();
							max++;
							setSequenceNumber(max);
						}
				}
		else
		{	
			if(publishResources.contains(resourceToFind))
			this.setCountFailedDiscovery();
			max--;
			setSequenceNumber(max);
		}	
			
	}

	private void createExchangeResourceEvent(ChordPeer senderNode, ChordPeer receiverNode ,ChordResourceType resourceToExchange) {
		
		double exchange_time = 0;
		if (senderNode.getTypePeer() == 1 && receiverNode.getTypePeer() == 1)
			exchange_time = 1.25;
		else if(senderNode.getTypePeer() == 2 && receiverNode.getTypePeer() == 2)
			exchange_time = 2.25;
		else if (senderNode.getTypePeer() == 3 || receiverNode.getTypePeer() == 3)
			exchange_time = 4.0;
		else if ((senderNode.getTypePeer() == 1 && receiverNode.getTypePeer() == 2) || (senderNode.getTypePeer() == 2 && receiverNode.getTypePeer() == 1) )
			exchange_time = 2.25;
			
		ChordDataExchangeEvent exchangeEv = (ChordDataExchangeEvent) Engine
		.getDefault().createEvent(
				ChordDataExchangeEvent.class,
				Engine.getDefault().getVirtualTime()
						+ expRandom((float) exchange_time));
   
	exchangeEv.setAssociatedNode(senderNode);
	exchangeEv.setHasSameAssociatedNode(true);
	exchangeEv.setOneShot(true);
	exchangeEv.setResourceToExchange(resourceToExchange);
	exchangeEv.setReceiverNode(receiverNode);
	Engine.getDefault().insertIntoEventsList(exchangeEv);
		
	}
	
private void createFindedResourceEvent(ChordPeer searchedNode, ChordPeer servingNode ,ChordResourceType findedResource) {
	
	double exchange_time = 0;
	if (searchedNode.getTypePeer() == 1 && servingNode.getTypePeer() == 1)
		exchange_time = 1.25;
	else if(searchedNode.getTypePeer() == 2 && servingNode.getTypePeer() == 2)
		exchange_time =2.25;
	else if (searchedNode.getTypePeer() == 3 || servingNode.getTypePeer() == 3)
		exchange_time = 4.0;
	else if ((searchedNode.getTypePeer() == 1 && servingNode.getTypePeer() == 2) || (searchedNode.getTypePeer() == 2 && servingNode.getTypePeer() == 1) )
		exchange_time = 2.25;
	
	ChordFindedResourceEvent findedEv = (ChordFindedResourceEvent) Engine
		.getDefault().createEvent(
				ChordFindedResourceEvent.class,
				Engine.getDefault().getVirtualTime()
						+ expRandom((float) exchange_time));
   
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
	
	public void refreshPublish() {
		if(!this.getServerId() && !this.fingerTable[0].getServerId())
		this.fingerTable[0].publishResources();
		
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
	
	public void deathChordNode() {
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
	}

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
		
		boolean flag = false;
		if(this.bufferVideo.size() >=getBufferDimension() && this.isConnected())
		{
			setCountPlayVideo();
			setLastPlayingResource(this.bufferVideo.get(getBufferDimension()-1).getSequenceNumber());
			Collections.sort(this.bufferVideo, new MyComp(null));
			
			for(int i = 0; i < this.bufferVideo.size()-1; i++)
			{
				int diff = this.bufferVideo.get(i+1).getSequenceNumber() - this.bufferVideo.get(i).getSequenceNumber();
					if (diff > 1)
					this.setCountMissBuffer();
				}
				if(!flag)
				{
					for(int d = 0; d < getBufferDimension()/4; d++)
						this.bufferVideo.remove(0);
				}
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

	public int getCountMissBuffer() {
		return countMissBuffer;
	}

	public void setCountMissBuffer() {
		this.countMissBuffer = countMissBuffer+1;
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

	public int getTypePeer() {
		return typePeer;
	}

	public void setTypePeer(int typePeer) {
		this.typePeer = typePeer;
	}

	public int getCountFastPeer() {
		return countFastPeer;
	}

	public void setCountFastPeer() {
		this.countFastPeer = countFastPeer+1;
	}

	public int getCountMediumPeer() {
		return countMediumPeer;
	}

	public void setCountMediumPeer() {
		this.countMediumPeer = countMediumPeer+1;
	}

	public int getCountSlowPeer() {
		return countSlowPeer;
	}

	public void setCountSlowPeer() {
		this.countSlowPeer = countSlowPeer+1;
	}

	public int getMaxConnectionsFast() {
		return maxConnectionsFast;
	}

	public int getNumPublishServer() {
		return numPublishServer;
	}

	public int getBufferDimension() {
		return bufferDimension;
	}

	public int getTotalResources() {
		return totalResources;
	}

	public int getMaxConnectionsMedium() {
		return maxConnectionsMedium;
	}

	public int getMaxConnectionsSlow() {
		return maxConnectionsSlow;
	}

	public void setMissingResources() {
		this.countMissingResources = countMissingResources+1;
		
	}

	public int getCountMissingResources() {
		return countMissingResources;
	}

	public int getCountPlayVideo() {
		return countPlayVideo;
	}

	public void setCountPlayVideo() {
		this.countPlayVideo = countPlayVideo+1;
	}
	
	class MyComp implements Comparator<ChordResourceType>{
		public MyComp(Object object) {
		}

		public int compare(ChordResourceType o1, ChordResourceType o2) {
			return o1.compareTo(o2);
		}
	}

	public int getCountDuplicateResources() {
		return countDuplicateResources;
	}

	public void setCountDuplicateResources() {
		this.countDuplicateResources = countDuplicateResources+1;
	}

	public void propagationVideoBuffer(int max_connections) {
		
		for(int c = 0; c < servedPeers.size(); c++)
		{
			if(servedPeers.get(c).getVideoName() == this.getVideoName())
			{
				if(!servedPeers.get(c).servingPeers.contains(this))
				servedPeers.get(c).servingPeers.add(this);
				for(int d = 0; d < consumableResources.size(); d++)
				{
					if(!servedPeers.get(c).consumableResources.contains(consumableResources.get(d)) && this.getNumConnections() <= max_connections)
						{
						this.setCountFindedOtherResource();
						this.incrementNumConnections();
						createFindedResourceEvent(servedPeers.get(c),this,consumableResources.get(d));
						}
				}
			}
		}	
	}

	public void updateVideoBuffer() {
		Collections.sort(this.consumableResources, new MyComp(null));
		
			for(int i = 0; i < this.consumableResources.size()-1; i++)
			{
				int diff = this.consumableResources.get(i+1).getSequenceNumber() - this.consumableResources.get(i).getSequenceNumber();
				if( diff > 1){
					if(this.servingPeers.size()>0){						
						int indexNode = Engine.getDefault().getSimulationRandom().nextInt(this.servingPeers.size());
						ChordPeer servingNode = this.servingPeers.get(indexNode);
						String hash = this.generateUUID(this.getVideoName() + (this.consumableResources.get(i).getSequenceNumber()+1));
						int resourceKey = KeyToSequenceNumber.get(hash);
						ChordResourceType resourceToFind = null;
						try {
							resourceToFind = new ChordResourceType(resourceKey);
						} catch (Exception e) {
							e.printStackTrace();
						}
					int max_connections = setMax_connections(servingNode);
					
					if(servingNode.consumableResources.contains(resourceToFind) && servingNode.getNumConnections() < max_connections)
					{  
						int index = servingNode.consumableResources.indexOf(resourceToFind);
//						for (int k = index ; k < servingNode.consumableResources.size(); k++)
//						{
							resourceToFind = servingNode.consumableResources.get(index);
							this.setCountFindedOtherResource();
							servingNode.incrementNumConnections();
							createFindedResourceEvent(this,servingNode,resourceToFind);
//						}
//					}
					}
				}
			}
		}
	}
	
public int setMax_connections(ChordPeer Peer){
	int max_connections = 0;
	if(Peer.getTypePeer() == 1)
		max_connections = getMaxConnectionsFast();
	else if (Peer.getTypePeer() == 2)
		max_connections = getMaxConnectionsMedium();
	else 
		max_connections = getMaxConnectionsSlow();
	
	return max_connections;
	}

	
}
