package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashMap;
import java.util.Set;

public class LogNsamServicesDistributionEvent extends Event {

	

		public LogNsamServicesDistributionEvent(String id, Properties params,
				Process parentProcess) throws InvalidParamsException {
			super(id, params, parentProcess);
			initialize();
		}

		public void initialize() throws InvalidParamsException {
		}

		public void run() throws RunException {
			
			HashMap<String, Integer> serviceMap = new HashMap<String, Integer>();
			ArrayList<NsamService> nodeServices = new ArrayList<NsamService>();
			
			AutomatorLogger a = new AutomatorLogger("./temp/logger");
			
			ArrayList<LoggerObject> fileValue = new ArrayList<LoggerObject>();
			
			for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it.hasNext();) {
				NsamPeer n = (NsamPeer)it.next();
				if (!(n instanceof NsamPeer))
					continue;
				nodeServices=n.getServiceList();
				if (nodeServices.isEmpty())
				{
					System.out.println(" No services available in the peer!!!");
					continue;
				}
				for (int j=0; j<nodeServices.size(); j++)
				{
					if (serviceMap.containsKey(nodeServices.get(j).getServiceId()))
					{
					Integer numService = (Integer)serviceMap.get(nodeServices.get(j).getServiceId());
					serviceMap.put(nodeServices.get(j).getServiceId(), numService++);
					}
					else serviceMap.put(nodeServices.get(j).getServiceId(), 1);	
				}
			}	
			
			System.out.println("Writing file with service IDs and number...");
			Set<String> keySet = serviceMap.keySet();
			for(String k:keySet){
			     Integer value = serviceMap.get(k);
			     fileValue.add(new LoggerObject(k, value));
			}
			a.write(Engine.getDefault().getVirtualTime(), fileValue);
			
		}
				
		}
