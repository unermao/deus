package it.unipr.ce.dsg.deus.example.mobilityexample;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.mobility.util.SwitchStationController;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class ConfigurationEvent extends Event{
	
	public ConfigurationEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
	}

	
	public void run() throws RunException {
		//Loading Switch Stations
		CarPeer.switchStationController = new SwitchStationController("examples/MobilityExample/SwitchStation_Parma.csv","examples/MobilityExample/paths_result_mid_Parma.txt");
		CarPeer.switchStationController.readSwitchStationFile();
		CarPeer.switchStationController.readPathFile();
	}

}
