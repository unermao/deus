package it.unipr.ce.dsg.deus.impl.resource;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;

public class Service extends Resource {
	private static final String NUM_NAMES_PARAM = "numNames";

	int numNames = 0;	
	
	public Service(Properties params) throws InvalidParamsException {
		super(params);
		initialize();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Service))
			return false;
		return true;  // TODO codice per confrontare due servizi e decidere se sono uguali
	}
	
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(NUM_NAMES_PARAM))
			numNames = Integer.parseInt(params.getProperty(NUM_NAMES_PARAM));
	}
	
}
