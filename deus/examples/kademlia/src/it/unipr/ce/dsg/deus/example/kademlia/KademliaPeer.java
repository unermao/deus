package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.LinkedList;

import it.unipr.ce.dsg.deus.p2p.node.Peer;
import it.unipr.ce.dsg.deus.core.*;
import it.unipr.ce.dsg.deus.example.kademlia.KademliaResourceType;

import java.util.Properties;

public class KademliaPeer extends Peer {
	private static final String K_BUCKET_DIM = "kBucketDim";
	private static final String RESOURCES_NODE = "resourcesNode";
	private static final String ALPHA = "alpha";
	private int alpha = 3;
	private static final String DISCOVERY_MAX_WAIT = "discoveryMaxWait";
	private float discoveryMaxWait = 500;

	private Vector<ArrayList<KademliaPeer>> kbucket = null;

	private int kBucketDim = 0;
	private int resourcesNode = 0;

	public Map<Integer, Integer> logSearch = new HashMap<Integer, Integer>();

	public ArrayList<KademliaResourceType> kademliaResources = new ArrayList<KademliaResourceType>();
	public ArrayList<KademliaResourceType> storedResources = new ArrayList<KademliaResourceType>();
	public HashMap<Integer, SearchResultType> nlResults = new HashMap<Integer, SearchResultType>();
	public ArrayList<KademliaPeer> nlContactedNodes = new ArrayList<KademliaPeer>();

	public KademliaPeer(String id, Properties params,
			ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);

		int size = 100;
		// int size = (int) Math.floor(Math.log
		// (Engine.getDefault().getKeySpaceSize())
		// /Math.log(2) +0.5); Invocation Target Exception!

		// this.kbucket = new Vector<LinkedList<KademliaPeer>>();
		this.kbucket = new Vector<ArrayList<KademliaPeer>>();
		for (int i = 0; i < size; i++) {
			// kbucket.add(i, new LinkedList<KademliaPeer>());
			kbucket.add(i, new ArrayList<KademliaPeer>());
		}
		if (params.getProperty(K_BUCKET_DIM) == null)
			throw new InvalidParamsException(K_BUCKET_DIM
					+ " param is expected");
		try {
			kBucketDim = Integer.parseInt(params.getProperty(K_BUCKET_DIM));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(K_BUCKET_DIM
					+ " must be a valid int value.");
		}
		if (params.containsKey(RESOURCES_NODE))
			this.setResourcesNode(Integer.parseInt(params
					.getProperty(RESOURCES_NODE)));

		if (params.getProperty(ALPHA) != null) {
			alpha = Integer.parseInt(params.getProperty(ALPHA));
		}

		if (params.getProperty(DISCOVERY_MAX_WAIT) != null) {
			try {
				setDiscoveryMaxWait(Float.parseFloat(params
						.getProperty(DISCOVERY_MAX_WAIT)));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(DISCOVERY_MAX_WAIT
						+ " must be a valid float value.");
			}
		}
	}

	public Object clone() {
		KademliaPeer clone = (KademliaPeer) super.clone();
		// clone.kbucket = new Vector<LinkedList<KademliaPeer>>();
		clone.kbucket = new Vector<ArrayList<KademliaPeer>>();
		int size = (int) Math.floor(Math.log(Engine.getDefault()
				.getKeySpaceSize())
				/ Math.log(2) + 1.5);
		for (int i = 0; i < size; ++i) {
			// clone.kbucket.add(i, new LinkedList<KademliaPeer>());
			clone.kbucket.add(i, new ArrayList<KademliaPeer>());
		}
		clone.kademliaResources = new ArrayList<KademliaResourceType>();
		clone.storedResources = new ArrayList<KademliaResourceType>();
		clone.nlResults = new HashMap<Integer, SearchResultType>();
		clone.nlContactedNodes = new ArrayList<KademliaPeer>();
		clone.logSearch = new HashMap<Integer, Integer>();

		return clone;
	}

	public int getResourcesNode() {
		return resourcesNode;
	}

	public void setResourcesNode(int i) {
		this.resourcesNode = i;
	}

	public void deathKademliaNode() {
		this.setConnected(false);
	}

	public void insertPeer(KademliaPeer newPeer) {
		if (this.getKey() == newPeer.getKey())
			return;
		int distance = this.getKey() ^ newPeer.getKey();

		// Get the k-bucket index for current distance (log2 distance)
		int index = (int) (Math.log(distance) / Math.log(2));

		if (kbucket.get(index).size() == 0) {
			// There is no list yet!
			// kbucket.setElementAt(new LinkedList<KademliaPeer>(), index);
			kbucket.setElementAt(new ArrayList<KademliaPeer>(), index);
			// Insert the new Peer in the correct kbucket
			kbucket.get(index).add(newPeer);
		} else if (kbucket.get(index).size() < kBucketDim) {
			// Insert the new Peer in the correct kbucket
			if (kbucket.get(index).contains(newPeer)) {
				kbucket.get(index).remove(newPeer);
			}
			kbucket.get(index).add(newPeer);
		} else {
			// Current peer pings last-recently seen peer (POP)
			// if it doesn't respond, the new one is inserted at the tail.
			// Otherwise the last-recently seen peer is moved at the tail and
			// the new is ignored
			// KademliaPeer lastRecentlySeen = kbucket.get(index).removeFirst();
			KademliaPeer lastRecentlySeen = kbucket.get(index).remove(0);

			if (this.ping(lastRecentlySeen)) {
				// kbucket.get(index).addLast(lastRecentlySeen);
				kbucket.get(index).add(lastRecentlySeen);
			} else {
				// kbucket.get(index).addLast(newPeer);
				kbucket.get(index).add(newPeer);
			}
			lastRecentlySeen = null;
		}

		// After a new node is inserted in the kbucket
		// the node checks if it is closer to any stored resources
		// than the node itself. If it is, it stores <key,value> couples in the
		// closer node
		int dist, newdist;
		for (KademliaResourceType r : storedResources) {
			dist = this.getKey() ^ r.getResourceKey();
			newdist = newPeer.getKey() ^ r.getResourceKey();
			if (newdist < dist) {
				newPeer.store(r);
			}
		}

		// the node checks if it is close to any Resource
		for (KademliaResourceType r : kademliaResources) {
			dist = this.getKey() ^ r.getResourceKey();
			newdist = newPeer.getKey() ^ r.getResourceKey();
			if (newdist < dist) {
				newPeer.store(r);
			}
		}

	}

	public boolean ping(KademliaPeer peer) {
		if (Engine.getDefault().getNodes().contains(peer)) {
			return true;
		}
		return false;
	}

	public ArrayList<KademliaPeer> find_node(int key) {
		int distance = this.getKey() ^ key;
		// Get the k-bucket index for current distance (log2 distance)
		int index;
		if (distance < 1) {
			index = 0;
		} else {
			index = (int) (Math.log(distance) / Math.log(2));
		}

		ArrayList<KademliaPeer> tempResults = new ArrayList<KademliaPeer>();
		Iterator<KademliaPeer> it;

		it = kbucket.get(index).iterator();
		while (it.hasNext())
			tempResults.add(it.next());

		boolean flag = false;
		int a = 1;
		while (tempResults.size() < kBucketDim) {
			flag = false;
			try {
				it = kbucket.get(index + a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				flag = true;
			}

			if (tempResults.size() >= kBucketDim)
				break;

			try {
				it = kbucket.get(index - a).iterator();
				while (it.hasNext())
					tempResults.add(it.next());
			} catch (IndexOutOfBoundsException e) {
				if (flag)
					break;
			}
			a++;
		}
		if (tempResults.size() > kBucketDim) {
			tempResults.subList(kBucketDim, tempResults.size()).clear();
		}

		return tempResults;
	}

	public void store(KademliaResourceType res) {
		if (!this.storedResources.contains(res)) {
			this.storedResources.add(res);
		}
	}

	public Object find_value(int key) {
		int idx = this.storedResources.indexOf(new KademliaResourceType(key));

		if (idx != -1) {
			return this.storedResources.get(idx);
		}

		return this.find_node(key);
	}

	public int getAlpha() {
		return alpha;
	}

	public int getKBucketDim() {
		return kBucketDim;
	}

	public void setDiscoveryMaxWait(float discoveryMaxWait) {
		this.discoveryMaxWait = discoveryMaxWait;
	}

	public float getDiscoveryMaxWait() {
		return discoveryMaxWait;
	}

	public Vector<ArrayList<KademliaPeer>> getKbucket() {
		return kbucket;
	}

	public void rawInsertPeer(KademliaPeer newPeer) {
		if (this.getKey() == newPeer.getKey())
			return;
		int distance = this.getKey() ^ newPeer.getKey();

		// Get the k-bucket index for current distance (log2 distance)
		int index = (int) (Math.log(distance) / Math.log(2));

		if (kbucket.get(index).size() == 0) {
			// There is no list yet!
			// kbucket.setElementAt(new LinkedList<KademliaPeer>(), index);
			kbucket.setElementAt(new ArrayList<KademliaPeer>(), index);
			// Insert the new Peer in the correct kbucket
			kbucket.get(index).add(newPeer);
		} else if (kbucket.get(index).size() < kBucketDim) {
			// Insert the new Peer in the correct kbucket
			kbucket.get(index).add(newPeer);
		}

	}
}
