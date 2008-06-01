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

	@Override
	public void initialize() throws InvalidParamsException {
		// TODO Auto-generated method stub

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

	@Override
	public void run() throws RunException {
		getLogger().fine("## free resource");
		if (this.resOwner == null)
			return;
		getLogger().fine("res owner: " + resOwner.getId());
		getLogger().fine("res to be set free: " + resName);
		if (this.resName.equals("cpu")) {
			getLogger().fine("cpu amount before freedom: " + ((RevolNode) resOwner).getCpu());
			((RevolNode) this.resOwner).setCpu(((RevolNode) this.resOwner)
					.getCpu()
					+ this.resAmount);
			getLogger().fine("cpu amount after freedom: " + ((RevolNode) resOwner).getCpu());
		}
		else if (this.resName.equals("ram")) {
			getLogger().fine("ram amount before freedom: " + ((RevolNode) resOwner).getRam());
			((RevolNode) this.resOwner).setRam(((RevolNode) this.resOwner)
					.getRam()
					+ this.resAmount);
			getLogger().fine("ram amount after freedom: " + ((RevolNode) resOwner).getRam());
		}
		else if (this.resName.equals("disk")) {
			getLogger().fine("disk amount before freedom: " + ((RevolNode) resOwner).getDisk());
			((RevolNode) this.resOwner).setDisk(((RevolNode) this.resOwner)
					.getDisk()
					+ this.resAmount);
			getLogger().fine("disk amount after freedom: " + ((RevolNode) resOwner).getDisk());
		}
		getLogger().fine("end free resource ##");
	}

}
