package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * <p>
 * ChordPeers are characterized by three kinds of consumable resources: CPU,
 * RAM, DISK. Moreover, each RevolPeer has a chromosome, i.e. a set of
 * parameters whose values are randomly initialized when the RevolPeer is
 * instantiated, and may change during its lifetime, depending on external
 * events. The RevolPeer keeps track of the number of sent queries (Q) and of
 * the number of query hits (QH). The query hit ratio (QHR = QH/Q) is
 * initialized to 0.
 * </p>
 * 
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordPeer extends Peer {
	private static int counter = 0;
	public static final int NUMBITS = 64;
	private ChordPeer predecessor = null;
	private ChordPeer fingerTable[] = null;
	private int lastFixedFinger = 0;

	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	public void initialize() throws InvalidParamsException {

	}

	public Object clone() {
		ChordPeer clone = (ChordPeer) super.clone();
		clone.predecessor = null;
		clone.fingerTable = new ChordPeer[NUMBITS];
		clone.lastFixedFinger = 0;
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

	/*
	 * public ChordPeer findSuccessor(ChordPeer connectingNode) { if
	 * (fingerTable[0] != null) if
	 * (connectingNode.getId().compareTo(fingerTable[0].getId()) < 0) return
	 * fingerTable[0];
	 * 
	 * if (getNeighbors().size() == 0) return this; else { ArrayList<ChordPeer>
	 * tempList = new ArrayList<ChordPeer>(); System.arraycopy(getNeighbors(),
	 * 0, tempList, 0, getNeighbors() .size()); tempList.add(connectingNode);
	 * Collections.sort(tempList);
	 * 
	 * int successorIndex = tempList.indexOf(connectingNode.getId()) + 1; if
	 * (successorIndex < tempList.size()) return
	 * tempList.get(successorIndex).findSuccessor( connectingNode); else return
	 * this; } }
	 * 
	 * public ChordPeer notify(ChordPeer stabilizingNode) { if (predecessor ==
	 * null || (stabilizingNode.getId().compareTo(predecessor.getId()) > 0 &&
	 * stabilizingNode .getId().compareTo(this.getId()) < 0)) { predecessor =
	 * stabilizingNode; return predecessor; } return null; }
	 * 
	 * public void fixFingers() { lastFixedFinger++; if (lastFixedFinger > 64)
	 * lastFixedFinger = 0;
	 * 
	 * ChordPeer nextNode = calculateNextNode(lastFixedFinger); if (nextNode !=
	 * null) { if (lastFixedFinger > getNeighbors().size())
	 * getNeighbors().add(findSuccessor(nextNode)); else
	 * getNeighbors().set(lastFixedFinger, findSuccessor(nextNode)); }
	 * 
	 * }
	 * 
	 * 
	 * public int getLastFixedFinger() { return lastFixedFinger; }
	 */

	public ChordPeer[] getFingerTable() {
		return fingerTable;
	}

	/*************************************/

	public void initFirstFingerTable() {
		for (int i = 0; i < NUMBITS; i++)
			fingerTable[i] = this;
	}

	public void initFingerTable(ChordPeer gatewayNode) {
		setSuccessor(gatewayNode.findSuccessor(calculateNextNodeId(getId(), 0)));
		setPredecessor(getSuccessor().getPredecessor());
		for (int i = 0; i < NUMBITS - 1; i++) {
			if (isInInterval(calculateNextNodeId(getId(), i + 1), getId(),
					fingerTable[i].getId()))
				fingerTable[i + 1] = fingerTable[i];
			else
				fingerTable[i + 1] = gatewayNode
						.findSuccessor(calculateNextNodeId(getId(), i+1));
		}
	}

	public ChordPeer findSuccessor(String nodeId) {
		return findPredecessor(nodeId).getSuccessor();
	}

	public ChordPeer findPredecessor(String nodeId) {
		ChordPeer predecessor = this;
		while (!isInInterval(nodeId, predecessor.getId(), predecessor
				.getSuccessor().getId())) {
			predecessor = predecessor.closestPrecedingFinger(nodeId);
		}
		return predecessor;
	}

	public ChordPeer closestPrecedingFinger(String nodeId) {
		for (int i = NUMBITS; i > 0; i--) {
			if (isInInterval(fingerTable[i - 1].getId(), getId(), nodeId))
				return fingerTable[i - 1];
		}
		return this;
	}

	public void updateOthers() {
		for (int i = 0; i < NUMBITS; i++) {
			ChordPeer predecessor = findPredecessor(calculateNextNodeId(
					getId(), i, true));
			predecessor.updateFingerTable(this, i);
		}
	}

	public void updateFingerTable(ChordPeer node, int entry) {
		if (isInInterval(node.getId(), getId(), fingerTable[entry].getId())) {
			fingerTable[entry] = node;
			if (!getPredecessor().equals(this))
				getPredecessor().updateFingerTable(node, entry);
		}

	}

	private boolean isInInterval(String nodeId, String a, String b) {
		String min = "00000000000000000000000000000000";
		String max = "ffffffffffffffffffffffffffffffff";
		if (a.equals(b))
			return true;

		if (nodeId.equals(a) || nodeId.equals(b))
			return true;

		if (a.compareTo(b) < 0) {
			if (nodeId.compareTo(a) > 0 && nodeId.compareTo(b) < 0)
				return true;
		} else {
			if ((nodeId.compareTo(a) > 0 && nodeId.compareTo(max) < 0)
					|| (nodeId.compareTo(min) > 0 && nodeId.compareTo(b) < 0))
				return true;
		}

		return false;

	}

	private String calculateNextNodeId(String startId, int step) {
		return calculateNextNodeId(startId, step, false);
	}

	private String calculateNextNodeId(String startId, int step,
			boolean subtract) {
		BigInteger minKey = BigInteger.valueOf(0);
		BigInteger maxKey = new BigInteger("ffffffffffffffffffffffffffffffff",
				16);
		BigInteger nodeBigId = new BigInteger(startId, 16);
		BigInteger stepBigId = null;

		if (step == 0)
			stepBigId = BigInteger.valueOf(1);
		else {
			stepBigId = BigInteger.valueOf(2);
			for (int i = 1; i < step; i++) {
				stepBigId = stepBigId.multiply(BigInteger.valueOf(2));
			}
		}

		if (subtract)
			nodeBigId = nodeBigId.subtract(stepBigId);
		else
			nodeBigId = nodeBigId.add(stepBigId);

		if (nodeBigId.compareTo(minKey) < 0)
			nodeBigId = maxKey.add(nodeBigId);
		if (nodeBigId.compareTo(maxKey) > 0)
			nodeBigId = nodeBigId.subtract(maxKey);

		return Engine.getDefault().bytesToHex(nodeBigId.toByteArray());
	}
}
