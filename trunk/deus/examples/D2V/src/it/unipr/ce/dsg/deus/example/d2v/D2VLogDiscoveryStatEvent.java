/**
 * 
 */
package it.unipr.ce.dsg.deus.example.d2v;

import java.util.ArrayList;
import java.util.Properties;
import it.unipr.ce.dsg.deus.automator.AutomatorLogger;
import it.unipr.ce.dsg.deus.automator.LoggerObject;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 * 
 */
public class D2VLogDiscoveryStatEvent extends Event {

	public D2VLogDiscoveryStatEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		
		int SAMPLE_LIMIT = 100;
		
		System.out.println("VT:" + triggeringTime + " LOG_DISCOVERY_STAT_EVENT");

		ArrayList<Double> discoveryList = new ArrayList<Double>();
		ArrayList<Integer> discoveryCountList = new ArrayList<Integer>();
		
		for(int i=0;i<SAMPLE_LIMIT;i++)
		{
			discoveryList.add(0.0);
			discoveryCountList.add(0);
			
			for(int index=0; index<Engine.getDefault().getNodes().size(); index++)
			{
				Node node = Engine.getDefault().getNodes().get(index);
				
				if(node.getId().equals("D2VPeer"))
				{
					D2VPeer peer = (D2VPeer)node;
					
					if(peer.getDiscoveryStatistics().size() == SAMPLE_LIMIT )
					{							
						discoveryCountList.set(i, discoveryCountList.get(i)+1);
						discoveryList.set(i, discoveryList.get(i)+peer.getDiscoveryStatistics().get(i));
					}
				}
			}
		}
		
		System.out.println(0+" "+100.0);	
		for(int i=0;i<SAMPLE_LIMIT;i++)
		{
			System.out.println((i+1)+" "+(double)(discoveryList.get(i)/(double)discoveryCountList.get(i)));
		}
		
	}
	

}
