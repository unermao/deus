package it.unipr.ce.dsg.deus.example.energy;
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
public class EnergyFreeResourceEvent extends Event {

	private Node resOwner = null;
	private String resName = null;
	private int resAmount = 0;

	public EnergyFreeResourceEvent(String id, Properties params,
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
		EnergyFreeResourceEvent clone = (EnergyFreeResourceEvent) super.clone();
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
		if (this.resName.equals("energy")) {
			getLogger().fine("cpu amount before freedom: " + ((EnergyPeer) resOwner).getPower());
			((EnergyPeer) this.resOwner).setPower(((EnergyPeer) this.resOwner)
					.getPower()
					+ this.resAmount);
			getLogger().fine("cpu amount after freedom: " + ((EnergyPeer) resOwner).getPower());
		}
		getLogger().fine("end free resource ##");
	}

}
