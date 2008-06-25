package it.unipr.ce.dsg.deus.core;

import it.unipr.ce.dsg.deus.example.revol.RevolNode;

import java.util.Properties;

/**
 * Abstract class for all the Events which exist if and only if they are 
 * associated with a Node.
 * The "hasSameAssociatedNode" param must be set to true if we eant that each clone
 * has the same associated node of the cloned NodeEvent.
 *
 * @see         Event
 */
public abstract class NodeEvent extends Event {

	protected static final String HAS_SAME_ASSOCIATED_NODE = "hasSameAssociatedNode";
	protected boolean hasSameAssociatedNode = false;
	protected Node associatedNode = null;
	
	public NodeEvent(String id, Properties params, Process parentProcess)
	throws InvalidParamsException { 
		super(id, params, parentProcess);
		initialize();
	}
	
	public Node getAssociatedNode() {
		return associatedNode;
	}
	
	public void setAssociatedNode(Node associatedNode) {
		this.associatedNode = associatedNode;
	}
	
	public boolean hasSameAssociatedNode() {
		return hasSameAssociatedNode;
	}

	public void setHasSameAssociatedNode(boolean hasSameAssociatedNode) {
		this.hasSameAssociatedNode = hasSameAssociatedNode;
	}
	
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(HAS_SAME_ASSOCIATED_NODE)) 
			hasSameAssociatedNode = Boolean.parseBoolean(params.getProperty(HAS_SAME_ASSOCIATED_NODE));
	}
	
	public Object clone() {
		NodeEvent clone = (NodeEvent) super.clone();
		if (!hasSameAssociatedNode) 
			clone.associatedNode = null; 
		return clone;
	}
}
