package it.unipr.ce.dsg.deus.impl.node;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;

public class BasicNode extends Node {

	public BasicNode(String id, Properties params)
			throws InvalidParamsException {
		super(id, params);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		// nothing to be done
	}
	
	public Object clone() {
		BasicNode clone = (BasicNode) super.clone();
		return clone;
	}
}
