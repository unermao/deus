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
import java.util.Random;

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
	private HashMap<String,Integer> KeysSequenceNumbersMap = new HashMap<String,Integer>();
//	private HashMap<String,Integer> KeysSequenceNumbersMap2 = new HashMap<String,Integer>();
//	private HashMap<String,Integer> KeysSequenceNumbersMap3 = new HashMap<String,Integer>();
	private ArrayList<Integer> generatedResourcesKeys = new ArrayList<Integer>();
	private ArrayList<ChordResourceType> app = new ArrayList<ChordResourceType>();
//	private HashMap<String,Integer> listaFilm = new HashMap<String,Integer>();
	
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
		//rendere dinamico anche i nomi e il numero di video(max=5)
		//mettere nell'xml il numero di video e il nome delle risorse
		String videoName = null;
		
			
		if(Engine.getDefault().getNodes().size() == 1)
		{
			int count = 0;
			for(int j = 0; j < birthPeer.videoList.size(); j++)
			{
				
				videoName = birthPeer.videoList.get(j);

			for(int i = 0; i <(Engine.getDefault().getKeySpaceSize())/birthPeer.videoList.size(); i++)
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
				KeysSequenceNumbersMap.putAll(birthPeer.KeyToSequenceNumber);
				count+=((Engine.getDefault().getKeySpaceSize())/birthPeer.videoList.size());
				
			}	
			int cost = 0;
			int resourceDistribution = (Engine.getDefault().getKeySpaceSize())/birthPeer.videoList.size();
			
			if(birthPeer.videoList.size() == 2)
			{
			for(int i = 0; i < birthPeer.chordResources.size()/4; i++)
			{
				app.add(birthPeer.chordResources.get(cost));
				app.add(birthPeer.chordResources.get(cost+1));
				app.add(birthPeer.chordResources.get(resourceDistribution+cost));
				app.add(birthPeer.chordResources.get((resourceDistribution+1)+cost));
				cost= cost +2;
				
			}
			}
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
		birthPeer.KeyToSequenceNumber.putAll(KeysSequenceNumbersMap);	

	}
	
/*int generatedResources_Key(){
	int result;
	Random keyRandom = new Random();
	if(generatedResourcesKeys.size() == 1000000)
		throw new RuntimeException("The Engine is not able to generate new unique key for resource. Increase key space size.");
	do {
			result = keyRandom.nextInt(1000000);
		} while(generatedResourcesKeys.contains(Integer.valueOf(result)));
		
			generatedResourcesKeys.add(result);			
		return result;
	
}*/

}
