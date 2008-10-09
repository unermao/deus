package it.unipr.ce.dsg.deus.example.simpleDataDriven;

import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This Event logs a number of statistics related to the current "snapshot" of
 * the StreamingPeer network:
 * <ol>
 * <li>mean value and variance of the chromosomes</li>
 * <li>initial and mean Streaming value</li>
 * <li>number of searchers (i.e. peers with at least 1 query sent)</li>
 * <li>average QHR (query hit ratio)</li>
 * </ol>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class LogStreamingPeerConnectionStatsEvent extends Event {

	public LogStreamingPeerConnectionStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {

		getLogger().info("###########################");
		getLogger().info("Node Connection Stats:");
		
		double activeConnectionTotal = 0.0;
		double activeConnectionPercentTotalPcNode = 0.0;
		double activeConnectionPercentTotalMobile = 0.0;
		double activeConnectionPercentTotalMobile3g = 0.0;
		double activeConnectionServer = 0.0;
		
		double totalPcNode = 0.0;
		double totalmobileNode = 0.0;
		double totalmobile3GNode = 0.0;
		
		ServerPeer serverPeer = (ServerPeer) Engine.getDefault().getNodes().get(0);

		activeConnectionServer = (double)serverPeer.getActiveConnection();
		
		//Controllo le connessioni attie dei diversi nodi
		for(int index = 1; index < Engine.getDefault().getNodes().size(); index++ ){
			
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(index);
			
			activeConnectionTotal = activeConnectionTotal + ( (double)peer.getActiveConnection());
			
			if(peer.getId().equals("pcNode"))
			{
				activeConnectionPercentTotalPcNode = activeConnectionPercentTotalPcNode + ( (double)peer.getActiveConnection());
				totalPcNode++;
			}
		
			if(peer.getId().equals("mobileNode"))
			{
				activeConnectionPercentTotalMobile = activeConnectionPercentTotalMobile + ( (double)peer.getActiveConnection());
				totalmobileNode++;
			}
			
			if(peer.getId().equals("mobile3GNode"))
			{
				activeConnectionPercentTotalMobile3g = activeConnectionPercentTotalMobile3g + ( (double)peer.getActiveConnection());
				totalmobile3GNode++;
			}

		}
		
		    getLogger().info("Totale Connessioni attive Totale               : " + activeConnectionTotal + "\n");
		
		    getLogger().info("Media Connessioni attive SERVER             (%): " + 100.0*(activeConnectionServer)/(activeConnectionTotal + activeConnectionServer));  
		    
		if(totalPcNode!=0.0)
			getLogger().info("Media Connessioni attive PC-NODE            (%): " + 100.0*(activeConnectionPercentTotalPcNode/(activeConnectionTotal + activeConnectionServer)));
	
		if(totalmobileNode!=0.0)
			getLogger().info("Media Connessioni attive MOBILE Wifi NODE   (%): " + 100.0*(activeConnectionPercentTotalMobile/(activeConnectionTotal + activeConnectionServer)));
		
		if(totalmobile3GNode!=0.0)
			getLogger().info("Media Connessioni attive MOBILE 3G NODE     (%): " + 100.0*(activeConnectionPercentTotalMobile3g/(activeConnectionTotal + activeConnectionServer)));
		
		getLogger().info("\n");
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime() + "\n");
		
		getLogger().info("Total Node           : " + (double)( totalPcNode + totalmobileNode + totalmobile3GNode));
		getLogger().info("Total PcNode         : " + totalPcNode);
		getLogger().info("Total mobileWifiNode : " + totalmobileNode);
		getLogger().info("Total mobile3GNode   : " + totalmobile3GNode);		
		getLogger().info("###########################");
		getLogger().info("\n");
		
	}

}
