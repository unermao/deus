package it.unipr.ce.dsg.deus.example.revol;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class RevolFreeResourceEvent extends Event {

	private Node resOwner = null;
	private String resName = null;
	private int resAmount = 0;

	public RevolFreeResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public void setResOwner(Node resOwner) {
		this.resOwner = resOwner;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public void setResAmount(int resAmount) {
		this.resAmount = resAmount;
	}

	public Object clone() {
		RevolFreeResourceEvent clone = (RevolFreeResourceEvent) super.clone();
		clone.resOwner = null;
		clone.resName = null;
		clone.resAmount = 0;
		return clone;
	}

	public void run() throws RunException {
		getLogger().info("## free resource");
		if (this.resOwner == null)
			return;
		getLogger().info("res owner: " + resOwner.getId());
		getLogger().info("res to be set free: " + resName);
		if (this.resName.equals("cpu")) {
			getLogger().info("cpu amount before freedom: " + ((RevolNode) resOwner).getCpu());
			((RevolNode) this.resOwner).setCpu(((RevolNode) this.resOwner)
					.getCpu()
					+ this.resAmount);
			getLogger().info("cpu amount after freedom: " + ((RevolNode) resOwner).getCpu());
		}
		else if (this.resName.equals("ram")) {
			getLogger().info("ram amount before freedom: " + ((RevolNode) resOwner).getRam());
			((RevolNode) this.resOwner).setRam(((RevolNode) this.resOwner)
					.getRam()
					+ this.resAmount);
			getLogger().info("ram amount after freedom: " + ((RevolNode) resOwner).getRam());
		}
		else if (this.resName.equals("disk")) {
			getLogger().info("disk amount before freedom: " + ((RevolNode) resOwner).getDisk());
			((RevolNode) this.resOwner).setDisk(((RevolNode) this.resOwner)
					.getDisk()
					+ this.resAmount);
			getLogger().info("disk amount after freedom: " + ((RevolNode) resOwner).getDisk());
		}
		getLogger().info("end free resource ##");
	}

}
