package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

public class GeoKadFindNodeEvent extends NodeEvent {

	private GeoKadPeer reqNode = null;

	private int resourceKey = -1;

	public GeoKadFindNodeEvent(String id, Properties params,
			Process parentProcess, GeoKadPeer peer)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		reqNode = peer;

		initialize();
	}

	public Object clone() {
		GeoKadFindNodeEvent clone = (GeoKadFindNodeEvent) super.clone();
		clone.resourceKey = -1;
		clone.reqNode = null;
		return clone;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	public void initialize() {
	}

	public void run() throws RunException {
		GeoKadPeer currentNode = (GeoKadPeer) getAssociatedNode();
		if (resourceKey == -1) {
			Random random = new Random();
			resourceKey = random.nextInt(Engine.getDefault().getKeySpaceSize());
		}

		reqNode.nlResults.get(resourceKey).addAll(
				currentNode.find_node(resourceKey));
		if (currentNode.getKey() != reqNode.getKey()) {
			currentNode.insertPeer(reqNode);
		}

	}

	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

	public GeoKadPeer getRequestingNode() {
		return reqNode;
	}

	public void setRequestingNode(GeoKadPeer reqNode) {
		this.reqNode = reqNode;
	}
}
