package it.unipr.ce.dsg.deus.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulationObject {

	private String loggerPathPrefix = Engine.DEFAULT_LOGGER_PATH_PREFIX;

	private Level loggerLevel = Engine.DEFAULT_LOGGER_LEVEL;

	public Logger getLogger() {
		return Engine.getDefault().getLogger(this, loggerPathPrefix,
				loggerLevel);
	}

	public void setLoggerPathPrefix(String loggerPathPrefix) {
		if (loggerPathPrefix == null)
			return;

		this.loggerPathPrefix = loggerPathPrefix;
	}

	public void setLoggerLevel(String loggerLevel) {
		if (loggerLevel == null)
			return;

		if (loggerLevel.equals("OFF"))
			this.loggerLevel = Level.OFF;
		else if (loggerLevel.equals("SEVERE"))
			this.loggerLevel = Level.SEVERE;
		else if (loggerLevel.equals("WARNING"))
			this.loggerLevel = Level.WARNING;
		else if (loggerLevel.equals("INFO"))
			this.loggerLevel = Level.INFO;
		else if (loggerLevel.equals("INFO"))
			this.loggerLevel = Level.INFO;
		else if (loggerLevel.equals("CONFIG"))
			this.loggerLevel = Level.CONFIG;
		else if (loggerLevel.equals("FINE"))
			this.loggerLevel = Level.FINE;
		else if (loggerLevel.equals("FINER"))
			this.loggerLevel = Level.FINER;
		else if (loggerLevel.equals("FINEST"))
			this.loggerLevel = Level.FINEST;
		else if (loggerLevel.equals("ALL"))
			this.loggerLevel = Level.ALL;
	}

}
