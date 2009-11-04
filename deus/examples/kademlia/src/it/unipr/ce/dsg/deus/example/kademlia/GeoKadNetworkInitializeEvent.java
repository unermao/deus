package it.unipr.ce.dsg.deus.example.geokad;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


import it.unipr.ce.dsg.deus.core.AutomatorParser;
import it.unipr.ce.dsg.deus.core.Deus;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class GeoKadNetworkInitializeEvent extends Event {

	public GeoKadNetworkInitializeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	public void run() throws RunException {
		HashMap<Integer, ArrayList<Integer> > knownNodes = new HashMap<Integer, ArrayList<Integer>>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("/home/vittorio/workspace/deus/log/test.log"));
			String s = new String();
			String[] strtok;
			Properties params = new Properties();
			
			while ((s = in.readLine()) != null) {
				if (s.startsWith("#"))
					continue;
				if (s.startsWith("Properties")) {
					strtok = s.split("\\s");
					params.setProperty("kBucketDim", strtok[2]);
					params.setProperty("resourcesNode", strtok[3]);
					params.setProperty("alpha", strtok[4]);
					params.setProperty("discoveryMaxWait", strtok[5]);
					continue;
				}
				strtok = s.split("\\s");
				GeoKadPeer peer = new GeoKadPeer(s, params, null);
				
				// Creation of all nodes.
				peer.setKey(Integer.parseInt(strtok[0]));
				ArrayList<Integer> list = new ArrayList<Integer>();
				
				for (int i = 2; i<strtok.length ; i++) {
						list.add(Integer.parseInt(strtok[i]));
					}
				
				knownNodes.put(peer.getKey(), list);
				Engine.getDefault().addNode(peer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) { } 
		catch (InvalidParamsException e) {
			e.printStackTrace();
		}
		
		// Filling of the kbuckets
		for (Integer key : knownNodes.keySet() ) {
			GeoKadPeer p = (GeoKadPeer) Engine.getDefault().getNodeByKey(key);
			for (Integer node : knownNodes.get(key)) {
				p.rawInsertPeer((GeoKadPeer)Engine.getDefault().getNodeByKey(node));
			}
		}
	}
}
