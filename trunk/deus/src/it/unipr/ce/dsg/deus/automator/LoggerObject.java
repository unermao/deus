package it.unipr.ce.dsg.deus.automator;

public class LoggerObject {
	
	private String dataName;
	private double dataValue;
	
	public LoggerObject(String dataName, double dataValue) {
		super();
		this.dataName = dataName;
		this.dataValue = dataValue;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public double getDataValue() {
		return dataValue;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}		

}
