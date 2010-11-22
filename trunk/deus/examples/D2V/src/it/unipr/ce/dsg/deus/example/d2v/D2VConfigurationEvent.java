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
import it.unipr.ce.dsg.deus.example.d2v.networkmodel.NetworkStationController;
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
		
		//Loading Switch Stations
		D2VPeer.ssc = new SwitchStationController("examples/D2V/SwitchStation_Parma.csv","examples/D2V/paths_result_mid_Parma.txt");
		D2VPeer.ssc.readSwitchStationFile();
		D2VPeer.ssc.readPathFile();
		D2VPeer.ssc.addMultipleBadSurfaceCondition(5);
		
		//Loading Mobile 3g and WiFi Stations
		NetworkStationController nsc = new NetworkStationController("examples/D2V/3GStation_Parma.csv", "examples/D2V/WiFiStation_Parma.csv");
		D2VPeer.mobile3GStationList = nsc.read3GStationFile();
		D2VPeer.wiFiStationList = nsc.readWiFiStationFile();
		
		log.printEnd(0,this.getClass().getName(),triggeringTime);
	}

}
