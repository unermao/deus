package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
import java.util.Properties;

public class FitnessCoolStreamingSendVideoChunkEvent extends NodeEvent {
	
	public FitnessCoolStreamingSendVideoChunkEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		System.out.println("SendChunk !");
	}

	public Object clone() {
		FitnessCoolStreamingSendVideoChunkEvent clone = (FitnessCoolStreamingSendVideoChunkEvent) super.clone();

		return clone;
	}


	public void run() throws RunException {
		
		FitnessCoolStreamingServerPeer server = (FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0);
		
		if(!server.isInit_bool())
			server.init();
		
		ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>> app = new ArrayList<ArrayList<FitnessCoolStreamingVideoChunk>>();
		
		//Ordino la lista di invio
		//app.addAll(server.sortSendBuffer());
		
		app.addAll(server.getSendBuffer());
		
		//Invio tutti i chunk in ordine
		for(int i = 0; i < app.size(); i++)
			for(int j = 0; j < app.get(i).size(); j++)
				{
				server.sendVideoChunk((FitnessCoolStreamingPeer)app.get(i).get(j).getDestNode(), app.get(i).get(j), triggeringTime);
			//	server.getSendBuffer().get(i).remove(app.get(i).get(j));
				}
		server.getSendBuffer().clear();
		
		for(int i=0;i<server.getK_value();i++)
			server.getSendBuffer().add(i,new ArrayList<FitnessCoolStreamingVideoChunk>());
		
		
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)Engine.getDefault().getNodes().get(i);
			
			app.clear();
			
			if(peer.isConnected())
			{
			
				if(!peer.isInit_bool())
					peer.init();
				
				//Ordino la lista di invio
				//app.addAll(peer.sortSendBuffer());
				app.addAll(peer.getSendBuffer());
				
				//Invio tutti i chunk in ordine
				for(int k = 0; k < app.size(); k++)
					for(int j = 0; j < app.get(k).size(); j++)
						{
						 if((peer.getK_buffer().get(peer.calculate_buffer_index(app.get(k).get(j)))).contains(app.get(k).get(j)))
						 peer.sendVideoChunk((FitnessCoolStreamingPeer)app.get(k).get(j).getDestNode(), app.get(k).get(j), triggeringTime);
			//			 peer.getSendBuffer().get(k).remove(app.get(i).get(j));
						 //else
						//peer.getSendBuffer().get(k).remove(j);	 
						}
			
				peer.getSendBuffer().clear();
			
				for(int l=0;l<peer.getK_value();l++)
					peer.getSendBuffer().add(l,new ArrayList<FitnessCoolStreamingVideoChunk>());
			
			
			}
		}
			
		
			
		getLogger().fine("end Update Parents List Event ##");
	}

}