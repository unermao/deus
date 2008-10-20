package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Collections;
import java.util.Comparator;
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
public class LogChordRingStatsEvent extends Event {

	private int numGeneratedResource = 0;
	private Integer conteggio[] = new Integer[500];
	private int sum = 0;
	private int average = 0;
	
	public LogChordRingStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		getLogger().info("######################### ChordPeer stats:" + Engine.getDefault().getVirtualTime());
		
//		for(int b = 0; b < 500; b++)
//			conteggio[b]=0;
		
		Collections.sort(Engine.getDefault().getNodes());
		getLogger().info("nodes: " + Engine.getDefault().getNodes().size());
		for (Iterator<Node> it = Engine.getDefault().getNodes().iterator(); it
				.hasNext();) {
			ChordPeer n = (ChordPeer) it.next();
			getLogger().info(
					"n: " + n + "\tp: " + n.getPredecessor() + "\ts: "
							+ n.getSuccessor() + "\t server?: " + n.getServerId() +  "\tarriva: " + n.getArrival());
			for (int i = 0; i < n.getFingerTable().length; i++)
				getLogger().info("\ti: " + i + "\tn: " + n.getFingerTable()[i]);
			numGeneratedResource +=  n.chordResources.size();
			getLogger().info(
					"\tnumber of resources: " + n.chordResources.size()
							+ "\tnum connections: " + n.getNumConnections()
							+ "\tvideo search: " + n.getVideoName());
			
//			for(int e = 0; e< 500; e++)
//			{
//				Integer f = 0;
//				if(n.chordResources.size() == e)
//				{
//					f = conteggio[n.chordResources.size()];
//					f++;
//					conteggio[e]=f;
//					f=0;
//				}
//				
//			}
			
			
			
			for (int c = 0; c < n.chordResources.size(); c++) {
				getLogger().info(
						"\ti: " + c + "\tresourceKey: "
								+ n.chordResources.get(c).getResource_key()
								+ "\tsequence number: "
								+ n.chordResources.get(c).getSequenceNumber()
								+ "\tfilm: "
								+ n.chordResources.get(c).getVideoName()
								);
				// + "\t name: "
//								+ n.chordResources.get(c).getName()
				

			}
			Collections.sort(n.chordResources, new MyComp(null));
//			for (int y = 0; y < n.chordResources.size(); y++) {
//				for(int s = 0; s<n.chordResources.get(y).getOwners().size(); s++)
//				{
//					getLogger().info(
//							"\ti: " + "\towner: "
//							+ n.chordResources.get(y).getOwners().get(s).getKey() );		
//				}
//						
//				}		
			for (int d = 0; d < n.consumableResources.size(); d++)
				getLogger().info(
						"\t searchResults: "
								+ n.consumableResources.get(d).getResource_key()
								+ "\tsequence number: "
								+ n.consumableResources.get(d).getSequenceNumber() + "\tfilm: "
								+ n.consumableResources.get(d).getVideoName());
			
			for (int r = 0; r < n.bufferVideo.size(); r++)
				getLogger().info(
						"\t\t bufferVideo: "
								+ n.bufferVideo.get(r).getResource_key()
								+ "\tsequence number: "
								+ n.bufferVideo.get(r).getSequenceNumber() + "\tfilm: "
								+ n.bufferVideo.get(r).getVideoName() + "\tUltima risorsa vista: "
								+ n.getLastPlayingResource());
	
		}
		
		
		getLogger().info("\t generatedResources: = " + numGeneratedResource);
		
		
//		for(int g=0; g<conteggio.length; g++)
//			{
//			getLogger().info("\tin a node ther are " + g + "\tresources in a number of occurences: " + conteggio[g]);
//			sum += g*conteggio[g];
//			
//			}
//		average = sum/Engine.getDefault().getNodes().size();
//		getLogger().info("\taverage is " + average);
		
		getLogger().info("################################");
	}

	class MyComp implements Comparator<ChordResourceType>{
		public MyComp(Object object) {
			
		}

		public int compare(ChordResourceType o1, ChordResourceType o2) {
			return o1.compareTo(o2);
		}
		}
	
}
