package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.message.TrafficInformationMessage;
import it.unipr.ce.dsg.deus.example.d2v.mobilitymodel.SwitchStationController;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;


/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VConfigurationEvent extends Event{
	
	public D2VConfigurationEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
	}

	
	public void run() throws RunException {
		DebugLog log = new DebugLog();
		log.printStart(0,this.getClass().getName(),triggeringTime);
		
		D2VPeer.globalMessageKnowledge = new ArrayList<TrafficInformationMessage>();
		
		D2VPeer.ssc = new SwitchStationController("examples/D2V/SwitchStation_Parma.csv","examples/D2V/paths_result_mid_Parma.txt");
		D2VPeer.ssc.readSwitchStationFile();
		D2VPeer.ssc.readPathFile();
		D2VPeer.ssc.addMultipleBadSurfaceCondition(5);
		
		log.printEnd(0,this.getClass().getName(),triggeringTime);
	}

}
