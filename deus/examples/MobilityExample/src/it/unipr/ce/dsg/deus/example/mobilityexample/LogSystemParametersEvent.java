package it.unipr.ce.dsg.deus.example.mobilityexample;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LogSystemParametersEvent extends Event{

	private AutomatorLogger a = null;
	private ArrayList<LoggerObject> fileValue = null;

	public LogSystemParametersEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		
		System.out.println("#################################################");
		System.out.println("VT: " + triggeringTime);
		
		a = new AutomatorLogger();
		fileValue = new ArrayList<LoggerObject>();
		
		ArrayList<Integer> carPeerList = Engine.getDefault().getNodeKeysById("CarPeer");
		
		double sumSentData = 0.0;
		
		for(int index=0; index<carPeerList.size(); index++)
		{
			CarPeer cp = (CarPeer)Engine.getDefault().getNodeByKey(carPeerList.get(index));
			sumSentData += cp.getSentByte();
		}
		
		double avgSentByte = 0.0;
		if(carPeerList.size() != 0)
			avgSentByte = ( (sumSentData / 100.0) / (double)carPeerList.size() ) / (double)triggeringTime;
		
		//Log monitored values
		System.out.println("Peers:"+carPeerList.size());
		fileValue.add(new LoggerObject("Peers",carPeerList.size()));
		
		System.out.println("Avg_Sent_Byte:"+avgSentByte);
		fileValue.add(new LoggerObject("Avg_Sent_Byte",avgSentByte));
		
		//Save monitored values at a specific virtual time
		a.write(Engine.getDefault().getVirtualTime(), fileValue);
		
		System.out.println("#################################################");
	}

}
