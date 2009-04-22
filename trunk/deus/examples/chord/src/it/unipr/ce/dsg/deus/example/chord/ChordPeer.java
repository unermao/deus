package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
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
	private static final String FINGER_TABLE_SIZE = "fingerTableSize";
	private static final String RESOURCES_NODE = "resourcesNode";
	private int fingerTableSize = 0;
	private ChordPeer predecessor = null;
	public ChordPeer fingerTable[] = null;
	private int resourcesNode = 0;
	
	public ArrayList<ChordResourceType> chordResources = new ArrayList<ChordResourceType>();
	public ArrayList<ChordResourceType> searchResults = new ArrayList<ChordResourceType>();
	
	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);

		if (params.getProperty(FINGER_TABLE_SIZE) == null)
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " param is expected.");
		try {
			fingerTableSize = Integer.parseInt(params
					.getProperty(FINGER_TABLE_SIZE));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " must be a valid int value.");
		}
		if (params.containsKey(RESOURCES_NODE))
			this.setNumbersPublishNode(Integer.parseInt(params
					.getProperty(RESOURCES_NODE)));
	}

	public Object clone() {

		ChordPeer clone = (ChordPeer) super.clone();
		clone.predecessor = null;
		clone.fingerTable = new ChordPeer[fingerTableSize];
		clone.chordResources = new ArrayList<ChordResourceType>();
		clone.searchResults = new ArrayList<ChordResourceType>();
		
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
	 * are in his fingerTable using findPredecessor and updateFingerTable
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
		fingerTable[i] = findSuccessor(calculateNextNodeId(getKey(), i));
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
		 
		for(int i = 0; i < chordResources.size(); i++)
		{
			resource = chordResources.get(i);
			int resource_key = resource.getResource_key();

			ChordPeer successorKey = findSuccessor(resource_key);
			if(successorKey != this )
				{
					successorKey.chordResources.add(resource);
					chordResources.remove(resource);
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
	public void searchForAResource(int resourceKey) {
	
		ChordPeer possessorPeer = null;
		ChordResourceType resourceToFind= null;
		try {
			resourceToFind = new ChordResourceType(resourceKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	if(this.chordResources.contains(resourceToFind))
		this.searchResults.add(resourceToFind);
		else
		{
			possessorPeer = this.findSuccessor(resourceKey);
			if(possessorPeer.chordResources.contains(resourceToFind))
				this.searchResults.add(resourceToFind);
		else 
		{
			resourceToFind.setResource_key(-1);
			this.searchResults.add(resourceToFind);
		}
		}
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
		int i = Engine.getDefault().getSimulationRandom().nextInt(Engine.getDefault().getNodes().size());
		ChordPeer prova = (ChordPeer) Engine.getDefault().getNodes().get(i);
		prova.fingerTable[0].publishResources();
		
	}

	public void disconnectChordNode() {
		ChordPeer successorNode = this.getSuccessor();
		ChordPeer predecessorNode = this.getPredecessor();
		
		this.changeOwner(chordResources, successorNode);
		successorNode.chordResources.addAll(this.chordResources);
		this.setConnected(false);
		
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
			getLogger().fine("\tsuccessordisconnectedNode's resource " + d + "\t: " + successorNode.chordResources.get(d).getResource_key());	
	
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

	public void changeOwner(ArrayList<ChordResourceType> ChordResources, ChordPeer peer)
	{
		for(int i = 0; i <ChordResources.size(); i++)
			ChordResources.get(i).setOwner(peer);
	}

	public int getResourcesNode() {
		return resourcesNode;
	}

	public void setNumbersPublishNode(int numbersPublishNode) {
		this.resourcesNode = numbersPublishNode;
	}
	
}