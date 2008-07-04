package it.unipr.ce.dsg.deus.example.revol;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;


/**
 * <p>
 * This event is related to the release of a previously 
 * consumed resource, by updating the corresponding value 
 * on the resource owner.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
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
		getLogger().fine("## free resource");
		if (this.resOwner == null)
			return;
		getLogger().fine("res owner: " + resOwner.getId());
		getLogger().fine("res to be set free: " + resName);
		if (this.resName.equals("cpu")) {
			getLogger().fine("cpu amount before freedom: " + ((RevolPeer) resOwner).getCpu());
			((RevolPeer) this.resOwner).setCpu(((RevolPeer) this.resOwner)
					.getCpu()
					+ this.resAmount);
			getLogger().fine("cpu amount after freedom: " + ((RevolPeer) resOwner).getCpu());
		}
		else if (this.resName.equals("ram")) {
			getLogger().fine("ram amount before freedom: " + ((RevolPeer) resOwner).getRam());
			((RevolPeer) this.resOwner).setRam(((RevolPeer) this.resOwner)
					.getRam()
					+ this.resAmount);
			getLogger().fine("ram amount after freedom: " + ((RevolPeer) resOwner).getRam());
		}
		else if (this.resName.equals("disk")) {
			getLogger().fine("disk amount before freedom: " + ((RevolPeer) resOwner).getDisk());
			((RevolPeer) this.resOwner).setDisk(((RevolPeer) this.resOwner)
					.getDisk()
					+ this.resAmount);
			getLogger().fine("disk amount after freedom: " + ((RevolPeer) resOwner).getDisk());
		}
		getLogger().fine("end free resource ##");
	}

}
