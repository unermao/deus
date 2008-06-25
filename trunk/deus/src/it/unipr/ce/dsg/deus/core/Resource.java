package it.unipr.ce.dsg.deus.core;

import java.util.Properties;

public abstract class Resource {

	protected Properties params = null;
	
	public Resource(Properties params) throws InvalidParamsException {
		this.params = params;
	}

	public abstract void initialize() throws InvalidParamsException;
	
	public abstract boolean equals(Object o);
	
}
