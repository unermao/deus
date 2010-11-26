package it.unipr.ce.dsg.deus.example.d2v.util;

public class ReconnectionStat {
	
	private double disconnectionPeriod = 0.0;
	private double reconnectionPeriod = 0;
	private double pnm;
	
	public ReconnectionStat(double disconnectionPeriod, double reconnectionPeriod, double pnm) {
		super();
		this.disconnectionPeriod = disconnectionPeriod;
		this.reconnectionPeriod = reconnectionPeriod;
		this.pnm = pnm;
	}

	public double getDisconnectionPeriod() {
		return disconnectionPeriod;
	}

	public void setDisconnectionPeriod(double disconnectionPeriod) {
		this.disconnectionPeriod = disconnectionPeriod;
	}

	public double getReconnectionPeriod() {
		return reconnectionPeriod;
	}

	public void setReconnectionPeriod(double reconnectionPeriod) {
		this.reconnectionPeriod = reconnectionPeriod;
	}

	public double getPnm() {
		return pnm;
	}

	public void setPnm(double pnm) {
		this.pnm = pnm;
	}
	
	
	
}
