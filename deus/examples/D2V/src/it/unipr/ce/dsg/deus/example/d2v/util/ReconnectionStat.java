package it.unipr.ce.dsg.deus.example.d2v.util;

public class ReconnectionStat {
	
	private double disconnectionPeriod = 0.0;
	private double pnmReconnectionPeriod = 0;
	private double pnm;
	
	public ReconnectionStat(double disconnectionPeriod, double reconnectionPeriod, double pnm) {
		super();
		this.disconnectionPeriod = disconnectionPeriod;
		this.pnmReconnectionPeriod = reconnectionPeriod;
		this.pnm = pnm;
	}

	public double getDisconnectionPeriod() {
		return disconnectionPeriod;
	}

	public void setDisconnectionPeriod(double disconnectionPeriod) {
		this.disconnectionPeriod = disconnectionPeriod;
	}

	public double getPNMReconnectionPeriod() {
		return pnmReconnectionPeriod;
	}

	public void setPNMReconnectionPeriod(double reconnectionPeriod) {
		this.pnmReconnectionPeriod = reconnectionPeriod;
	}

	public double getPnm() {
		return pnm;
	}

	public void setPnm(double pnm) {
		this.pnm = pnm;
	}
	
	
	
}
