package it.unipr.ce.dsg.deus.example.chordStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This event represents the birth of a simulation ChordPeer. During its execution an
 * instance of the node associated to the event will be created.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordBirthEvent extends NodeEvent {
	
	private static int birth_sequence = 1;
	static HashMap<String,Integer> KeysSequenceNumbersMap1 = new HashMap<String,Integer>();
	static HashMap<String,Integer> KeysSequenceNumbersMap2 = new HashMap<String,Integer>();
	static HashMap<String,Integer> KeysSequenceNumbersMap3 = new HashMap<String,Integer>();
	private ArrayList<ChordResourceType> app = new ArrayList<ChordResourceType>();
	
	public ChordBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {

		if (getParentProcess() == null)
			throw new RunException(
					"A parent process must be set in order to run "
							+ getClass().getCanonicalName());
		// create a node (the type is randomly chosen among those which are
		// associated to the process)
		Node n = (Node) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateKey());
		
		Engine.getDefault().getNodes().add(n);
		associatedNode = n;
		
		ChordPeer birthPeer = (ChordPeer) n;
		birthPeer.setArrival(birth_sequence);
		birth_sequence+=1;
		String videoName = null;

		if(Engine.getDefault().getNodes().size() == 1)
		{
			System.out.println("SERVERRRRRR");
			int count = 0;
			for(int j = 0; j < birthPeer.videoList.size(); j++)
			{
				
				videoName = birthPeer.videoList.get(j);

			for(int i = 0; i <(birthPeer.getTotalResources())/birthPeer.videoList.size(); i++)
				try {
					
					birthPeer.chordResources.add(new ChordResourceType(Engine.getDefault().generateResourceKey()));
						birthPeer.chordResources.get(i + count)
								.setSequenceNumber(i);
						birthPeer.chordResources.get(i + count).setHash(
								birthPeer.generateUUID(videoName + i));
						birthPeer.chordResources.get(i + count).setVideoName(
								videoName);
						birthPeer.KeyToSequenceNumber.put(
								birthPeer.chordResources.get(i + count)
										.getHash(), birthPeer.chordResources
										.get(i + count).getResource_key());

				} catch (Exception e) {
					e.printStackTrace();
				}

				if(j == 0)
				{
					KeysSequenceNumbersMap1.putAll(birthPeer.KeyToSequenceNumber);	
					birthPeer.KeyToSequenceNumber.clear();
				}
				else if(j == 1)
				{
					KeysSequenceNumbersMap2.putAll(birthPeer.KeyToSequenceNumber);	
					birthPeer.KeyToSequenceNumber.clear();
				}
				else if(j == 2)
				{					
					KeysSequenceNumbersMap3.putAll(birthPeer.KeyToSequenceNumber);	
					birthPeer.KeyToSequenceNumber.clear();
				}
				
				count+=(birthPeer.getTotalResources()/birthPeer.videoList.size());
			}	
			int cost = 0;
			int resourceDistribution = (birthPeer.getTotalResources()/birthPeer.videoList.size());
			
			//diffusione dello streaming per 3 video in modo che in chordResources non ci siano di fila tutte le parti di un unico video
			if(birthPeer.videoList.size() == 3)
			{
			for(int i = 0; i < birthPeer.chordResources.size()/6; i++)
			{
				app.add(birthPeer.chordResources.get(cost));
				app.add(birthPeer.chordResources.get(cost+1));
				app.add(birthPeer.chordResources.get(resourceDistribution+cost));
				app.add(birthPeer.chordResources.get((resourceDistribution+1)+cost));
				app.add(birthPeer.chordResources.get(resourceDistribution*2+cost));
				app.add(birthPeer.chordResources.get((resourceDistribution*2+1)+cost));
				cost= cost +2;
				
			}
			}
			birthPeer.chordResources.clear();
			birthPeer.chordResources.addAll(app);
		}
	}
}
