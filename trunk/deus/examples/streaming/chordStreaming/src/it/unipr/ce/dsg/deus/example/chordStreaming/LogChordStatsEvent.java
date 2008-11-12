package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This class is used to print all the predecessor, the successor and the
 * fingerTables of all ChordPeers of the network
 * </p>
 * 
 * @author Matteo Agosti (matteo.agosti@unipr.it)
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 */
public class LogChordStatsEvent extends Event {

	public LogChordStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("########## ChordPeer Stats:" + Engine.getDefault().getVirtualTime());
		getLogger().info("Number of nodes in the Network: " + Engine.getDefault().getNodes().size());
			double sumSearches = 0;
			double sumFaleidSearches = 0 ;
			double sumFindedResource = 0;
			double sumFindedOtherResource = 0;
			double sumCorrectBuffer = 0;
			double sumFirstVideo = 0;
			double sumSecondVideo = 0;
			double sumThirdVideo = 0;
			double sumFastPeer = 0;
			double sumMediumPeer = 0;
			double sumSlowPeer = 0;
			double sumMissingResources = 0;
			
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer t = (ChordPeer) Engine.getDefault().getNodes().get(i);
				sumSearches+= t.getCountSearch();
				sumFaleidSearches+=t.getCountFailedDiscovery();
				sumFindedResource+=t.getCountFindedResource();
				sumFindedOtherResource+=t.getCountFindedOtherResource();
				sumCorrectBuffer+=t.getCountCorrectBuffer();
				sumFirstVideo+=t.getCountFirstVideo();
				sumSecondVideo+=t.getCountSecondVideo();
				sumThirdVideo+=t.getCountThirdVideo();
				sumFastPeer+=t.getCountFastPeer();
				sumMediumPeer+=t.getCountMediumPeer();
				sumSlowPeer+=t.getCountSlowPeer();
				sumMissingResources+=t.getCountMissingResources();
		}
			getLogger().info(
					"N¡ of searches: " + sumSearches + " failed Searches: "
							+ sumFaleidSearches + " findedResource: "
							+ sumFindedResource + " findedOtherResources: "
							+ sumFindedOtherResource + " correctBuffer: "
							+ sumCorrectBuffer + "\n MatrixVideo: "
							+ sumFirstVideo + " KillBillVideo: "
							+ sumSecondVideo + " ArmaggeddonVideo: "
							+ sumThirdVideo  + "FastPeer: " 
							+ sumFastPeer 	+ "MediumPeer: "
							+ sumMediumPeer + "SlowPeer: " 
							+ sumSlowPeer  	+ "MissingResources: " + sumMissingResources );
			if(sumSearches!=0)
			getLogger().info("% of Failed Searches: " + (sumFaleidSearches/sumSearches)*100 + "%");
			getLogger().info("% of Finded Indirect Resource : " + (sumFindedOtherResource/sumFindedResource)*100 + "%");
			getLogger().info("% of firstVideo Resource : " + (sumFirstVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");
			getLogger().info("% of secondVideo Resource : " + (sumSecondVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");
			getLogger().info("% of thirdVideo Resource : " + (sumThirdVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");
			getLogger().info("% of fastPeer: " + (sumFastPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + "%");
			getLogger().info("% of mediumPeer: " + (sumMediumPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + "%");
			getLogger().info("% of slowPeer: " + (sumSlowPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + "%");
			getLogger().info("% of missingResources: " + (sumMissingResources/sumSearches)*100 + "%");
			getLogger().info("% of ResourceIndirect: " + (sumFindedResource/sumFindedOtherResource)*100 + "%");
			
		getLogger().info("##########");
		}
		
	}

