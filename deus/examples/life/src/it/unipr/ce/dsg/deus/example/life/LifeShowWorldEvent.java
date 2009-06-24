package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeShowWorldEvent extends NodeEvent {

	public LifeShowWorldEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws RunException {
		// TODO Auto-generated method stub

		if(LifeRegion.l == null)
			LifeRegion.l = new LifeGUI(Engine.getDefault().getNodes().size());
		
		for(int i=0; i< Engine.getDefault().getNodes().size(); ++i) {
			((LifeRegion) Engine.getDefault().getNodes().get(i)).updateRegionPanel();
		}
		
		LifeRegion.l.vtLabelValue.setText(String.valueOf(Engine.getDefault().getVirtualTime()));
		
	}

}
