package it.unipr.ce.dsg.deus.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogEntryFormatter extends Formatter {

	public String format(LogRecord record) {
		return record.getMessage() + "\r\n";
	}	

}
