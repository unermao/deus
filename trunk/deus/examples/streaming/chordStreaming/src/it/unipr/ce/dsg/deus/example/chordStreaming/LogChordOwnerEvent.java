package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
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
 * This class is used to print all the predecessor, the successor
 * and the fingerTables of all ChordPeers of the network
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */
public class LogChordOwnerEvent extends Event {

	public LogChordOwnerEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("########## ChordPeer time Simulation:" + Engine.getDefault().getVirtualTime());
		
		Collections.sort(Engine.getDefault().getNodes());
		getLogger().info("nodes: " + Engine.getDefault().getNodes().size());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor());
			
			for (int y = 0; y < n.chordResources.size(); y++)
					getLogger().info("\towner of resource: " + n.chordResources.get(y).getResource_key() + " seqNumber: " + n.chordResources.get(y).getSequenceNumber());		
	
			getLogger().info("\tnumero nodi serviti: " + n.servedPeers.size());
			for(int j = 0; j < n.servedPeers.size(); j++)
				getLogger().info("\tservito : " + n.servedPeers.get(j).getKey());
		
			double sumSearches = 0;
			double sumFaleidSearches = 0 ;
			double sumFindedResource = 0;
			double sumFindedOtherResource = 0;
			double sumCorrectBuffer = 0;
			double sumFirstVideo = 0;
			double sumSecondVideo = 0;
			double sumThirdVideo = 0;
			
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
		}
			getLogger().info(
					"N¡ of searches: " + sumSearches + " failed Searches: "
							+ sumFaleidSearches + " findedResource: "
							+ sumFindedResource + " findedOtherResources: "
							+ sumFindedOtherResource + " correctBuffer: "
							+ sumCorrectBuffer + "\n MatrixVideo: "
							+ sumFirstVideo + " KillBillVideo: "
							+ sumSecondVideo + " ArmaggeddonVideo: "
							+ sumThirdVideo);
			if(sumSearches!=0)
			getLogger().info("% of Failed Searches: " + (sumFaleidSearches/sumSearches)*100 + "%");
			getLogger().info("% of Finded Indirect Resource : " + (sumFindedOtherResource/sumFindedResource)*100 + "%");
			getLogger().info("% of firstVideo Resource : " + (sumFirstVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");
			getLogger().info("% of secondVideo Resource : " + (sumSecondVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");
			getLogger().info("% of thirdVideo Resource : " + (sumThirdVideo/(sumFirstVideo+sumSecondVideo+sumThirdVideo))*100 + "%");

		getLogger().info("##########");
		}
		
	}
}
