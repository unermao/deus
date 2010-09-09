package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.example.d2v.message.Message;
import it.unipr.ce.dsg.deus.example.d2v.message.MessageExchangeEvent;


/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VFirstDiscoveryEvent extends NodeEvent {

	public D2VFirstDiscoveryEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		D2VPeer connectingNode = (D2VPeer) this.getAssociatedNode();
		//System.out.println("VT:"+triggeringTime+" First DiscoveryEvent ---> Peer Key: " + connectingNode.getKey());
		
		/*
		try {
				
			int random = Engine.getDefault().getSimulationRandom().nextInt(Engine.getDefault().getNodes().size());
			
			D2VPeer randomPeer = (D2VPeer)Engine.getDefault().getNodes().get(random);
			
			Message msg = new Message("TEST_MSG", connectingNode.getKey(), randomPeer.getKey(), new String("AHAHAH").getBytes());
			
			MessageExchangeEvent event = (MessageExchangeEvent) new MessageExchangeEvent("message_exchange", params, null).createInstance(triggeringTime+25);
			event.setOneShot(true);
			event.setAssociatedNode(randomPeer);
			event.setMsg(msg);
			Engine.getDefault().insertIntoEventsList(event);
		} catch (InvalidParamsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
