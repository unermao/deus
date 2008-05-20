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
		System.out.println("## free resource");
		if (this.resOwner == null)
			return;
		System.out.println("res owner: " + resOwner.getId());
		System.out.println("res to be set free: " + resName);
		if (this.resName.equals("cpu")) {
			System.out.println("cpu amount before freedom: " + ((RevolNode) resOwner).getCpu());
			((RevolNode) this.resOwner).setCpu(((RevolNode) this.resOwner)
					.getCpu()
					+ this.resAmount);
			System.out.println("cpu amount after freedom: " + ((RevolNode) resOwner).getCpu());
		}
		else if (this.resName.equals("ram"))
			((RevolNode) this.resOwner).setRam(((RevolNode) this.resOwner)
					.getRam()
					+ this.resAmount);
		else if (this.resName.equals("disk"))
			((RevolNode) this.resOwner).setDisk(((RevolNode) this.resOwner)
					.getDisk()
					+ this.resAmount);
		System.out.println("end free resource ##");
	}

}
