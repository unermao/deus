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
		getLogger().info("Number of NODES in the Network: " + Engine.getDefault().getNodes().size());
			double sumSearches = 0;
			double sumFaleidSearches = 0 ;
			double sumFindedResource = 0;
			double sumFindedOtherResource = 0;
			double sumMissBuffer = 0;
			double sumFirstVideo = 0;
			double sumSecondVideo = 0;
			double sumThirdVideo = 0;
			double sumFastPeer = 0;
			double sumMediumPeer = 0;
			double sumSlowPeer = 0;
			double sumMissingResources = 0;
			double sumPlayVideo = 0;
			double sumDuplicateResources = 0;
			
			for (int i = 0; i < Engine.getDefault().getNodes().size(); i++)
			{
				ChordPeer t = (ChordPeer) Engine.getDefault().getNodes().get(i);
				sumSearches+= t.getCountSearch();
				sumFaleidSearches+=t.getCountFailedDiscovery();
				sumFindedResource+=t.getCountFindedResource();
				sumFindedOtherResource+=t.getCountFindedOtherResource();
				sumMissBuffer+=t.getCountMissBuffer();
				sumFirstVideo+=t.getCountFirstVideo();
				sumSecondVideo+=t.getCountSecondVideo();
				sumThirdVideo+=t.getCountThirdVideo();
				sumFastPeer+=t.getCountFastPeer();
				sumMediumPeer+=t.getCountMediumPeer();
				sumSlowPeer+=t.getCountSlowPeer();
				sumMissingResources+=t.getCountMissingResources();
				sumPlayVideo+=t.getCountPlayVideo();
				sumDuplicateResources+=t.getCountDuplicateResources();
		}
			getLogger().info("SEARCHES: ");
			getLogger().info("N¡ of Total searches: " + sumSearches);
			getLogger().info("Failed Searches: " + sumFaleidSearches);
			if(sumSearches!=0)
			getLogger().info("% of Failed Searches: " + (sumFaleidSearches/sumSearches)*100 + " %");
			getLogger().info("RESOURCES: ");
			getLogger().info("FindedResource: "+ sumFindedResource);
			getLogger().info("DuplicateResources: "+ sumDuplicateResources);
			getLogger().info("FindedOtherResource: "+ sumFindedOtherResource);
			getLogger().info("% of IndirectResources: " + (sumFindedOtherResource/sumFindedResource)*100 + " %");
			getLogger().info("% of IndirectResourcesCorrect: " + (sumFindedOtherResource/(sumFindedResource+sumFindedOtherResource))*100 + " %");
			getLogger().info("% of missingResourcesForDisconnection: " + (sumMissingResources/sumSearches)*100 + " %");
			getLogger().info("% of numFindedResources/sumSearches: " + (sumSearches/(sumFindedResource+sumFindedOtherResource))*100 + " %");
			getLogger().info("% of numDuplicateResources: "+ sumDuplicateResources/(sumFindedResource+sumFindedOtherResource)*100 + " %");
			getLogger().info("BUFFER: ");
			getLogger().info("MissBuffer: " + sumMissBuffer);
			getLogger().info("NumPlayingVideo: " + sumPlayVideo);
			getLogger().info("% of ErrorBuffer: " + (sumMissBuffer/sumPlayVideo)*100 + " %");
			getLogger().info("% of ContinuityIndex: " + (100.00000000000000 - (sumMissBuffer/sumPlayVideo)*100) + " %");
			getLogger().info("TYPE_VIDEO: ");
			getLogger().info("Video KillBillVideo: " +sumFirstVideo);
			getLogger().info("Video ArmaggeddonVideo: " +sumSecondVideo);
			getLogger().info("Video Matrix: " +sumThirdVideo);
			getLogger().info("% of firstVideo Resource : " + (sumFirstVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + " %");
			getLogger().info("% of secondVideo Resource : " + (sumSecondVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + " %");
			getLogger().info("% of thirdVideo Resource : " + (sumThirdVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + " %");
			getLogger().info("TYPE_NODE: ");
			getLogger().info("FastPeer: "+sumFastPeer);
			getLogger().info("MediumPeer: "+sumMediumPeer);
			getLogger().info("SlowPeer: "+sumSlowPeer);
			getLogger().info("% of fastPeer: " + (sumFastPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + " %");
			getLogger().info("% of mediumPeer: " + (sumMediumPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + " %");
			getLogger().info("% of slowPeer: " + (sumSlowPeer/(sumFastPeer+sumMediumPeer+sumSlowPeer))*100 + " %");
			
			
		getLogger().info("##########");
		}
		
	}

