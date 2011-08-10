package it.unipr.ce.dsg.deus.core;

import java.util.Properties;

/**
 * <p>
 * Abstract class for all the Events which exist if and only if they are
 * associated with a Node.
 * 
 *</p>
 * <p>
 * The "hasSameAssociatedNode" param must be set to true if we want that each
 * clone has the same associated node of the cloned NodeEvent.
 * </p>
 * 
 * @see it.unipr.ce.dsg.deus.core.NodeEvent
 * 
 * @author Matteo Agosti 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public abstract class NodeEvent extends Event {

	protected static final String HAS_SAME_ASSOCIATED_NODE = "hasSameAssociatedNode";
	protected boolean hasSameAssociatedNode = false;
	protected Node associatedNode = null;

	/**
	 * Class constructor that builds the event with its minimal set of
	 * properties. Each implementing class should call the super constructor and
	 * immediately after invoke the initialize method to check that the event
	 * parameters are correct. This class constructor is an example of how it
	 * should be implemented, since NodeEvent extends the Event class itself.
	 * 
	 * @param id
	 *            the identifier of the event as specified in the configuration
	 *            file.
	 * @param params
	 *            the properties that will be handled by the event.
	 * @param parentProcess
	 *            the parent process of the event.
	 * @throws InvalidParamsException
	 *             if the given parameter are wrong.
	 */
	public NodeEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		
		if (params.containsKey(HAS_SAME_ASSOCIATED_NODE))
			hasSameAssociatedNode = Boolean.parseBoolean(params
					.getProperty(HAS_SAME_ASSOCIATED_NODE));
	}

	/**
	 * Returns the node associated to the event.
	 * 
	 * @return the node associated to the event.
	 */
	public Node getAssociatedNode() {
		return associatedNode;
	}

	/**
	 * Sets the node associated to the event.
	 * 
	 * @param associatedNode
	 *            the node to be associated to the event.
	 */
	public void setAssociatedNode(Node associatedNode) {
		this.associatedNode = associatedNode;
	}

	/**
	 * Returns <code>true</code> if the associated node will be cloned with the
	 * event, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the associated node will be cloned with the
	 *         event, <code>false</code> otherwise.
	 */
	public boolean hasSameAssociatedNode() {
		return hasSameAssociatedNode;
	}

	/**
	 * Sets whether the node associated to the event will be cloned with the
	 * event.
	 * 
	 * @param hasSameAssociatedNode
	 *            <code>true</code> if the node associated to the event will be
	 *            cloned with the event as well, <code>false</code> otherwise.
	 */
	public void setHasSameAssociatedNode(boolean hasSameAssociatedNode) {
		this.hasSameAssociatedNode = hasSameAssociatedNode;
	}

	/**
	 * Clone the event and if the property of cloning the associated node is
	 * true it will clone it as well otherwise it will set it to null.
	 */
	public Object clone() {
		NodeEvent clone = (NodeEvent) super.clone();		
		if (!hasSameAssociatedNode)
			clone.associatedNode = null;
		return clone;
	}
}
