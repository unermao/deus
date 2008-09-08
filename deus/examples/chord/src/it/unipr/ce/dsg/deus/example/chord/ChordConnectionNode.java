package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

//import peersim.core.Network;

public class ChordConnectionNode extends NodeEvent {
		private static final String IS_BIDIRECTIONAL = "isBidirectional";
		private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
		
		private boolean isBidirectional = false;
		private int numInitialConnections = 0;
		private int numFingerTableElements = 160;
		public ArrayList<String> fingerTable = new ArrayList<String>();
		
		BigInteger currentKeyNode = null;
		BigInteger keyNodeToCompare = null;
		int comparison_result = -1;
		int mod_comparison = -1;
		int out_of_range=-1;
		BigInteger maxKey = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
		BigInteger incrementKeyNode = new BigInteger("0", 16);
		
		
		public ChordConnectionNode(String id, Properties params, Process parentProcess)
				throws InvalidParamsException {
			super(id, params, parentProcess);
			initialize();
		}

		public void initialize() throws InvalidParamsException {
//			System.out.println("CHORD_CONNECTION_NODE");
			if (params.containsKey(IS_BIDIRECTIONAL))
				isBidirectional = Boolean.parseBoolean(params.getProperty(IS_BIDIRECTIONAL)); 
			if (params.containsKey(NUM_INITIAL_CONNECTIONS))
				numInitialConnections = Integer.parseInt(params.getProperty(NUM_INITIAL_CONNECTIONS));
		}

		public Object clone() {
			ChordConnectionNode clone = (ChordConnectionNode) super.clone();
			return clone;
		}

		public void run() throws RunException {
			
			System.out.println("CHORD_CONNECTION_NODE");
//			if (!(associatedNode instanceof Peer))
//				throw new RunException("The associated node is not a Peer!");
			int n = Engine.getDefault().getNodes().size();
			if (n == 0)
				return;
			int m = 0;
			if (n <= numInitialConnections)
				m = Engine.getDefault().getNodes().size() - 1;
			else
				m = numInitialConnections;
			
				
				int initializedNode = Engine.getDefault().getNodes().size();
				System.out.println(initializedNode + " initializedNode");
				ChordPeer chordPeer = null;
		
				//estraggo l'indice del nodo corrente nella rete ordinata
				int i = Engine.getDefault().getNodes().indexOf(this);
				System.out.println(i + " i");
				 int step = 1;
				 BigInteger stepBigInt;
				 boolean mod = false;
				 int index = 0;

				 //clono il nodo della lista
				 chordPeer = (ChordPeer) Engine.getDefault().getNodes().get(i).clone();
				 
				 //estraggo il suo big int e lo salvo
				 currentKeyNode = chordPeer.getBigId();
				 
				 
				 for(int k=1; k<=numFingerTableElements; k++) {

						stepBigInt = BigInteger.valueOf(1);
						if (k == 1)
							stepBigInt = BigInteger.ONE;
						else
							for(int c = 1; c < k; c++)
								stepBigInt = stepBigInt.multiply(BigInteger.valueOf(2));
						
						comparison_result = -1;

						incrementKeyNode = currentKeyNode.add(stepBigInt);
					//VEDO SE E NECESSARIO FARE IL MODULO	

						mod_comparison = maxKey.compareTo(incrementKeyNode);				
						if( mod_comparison < 0 )
						{
							incrementKeyNode = incrementKeyNode.mod(maxKey);	
							if(!mod)
							{
								i=0;
								step=0;
							}
							mod = true;
						}
						
						//CONTROLLO SE HO UN IDENTIFICATORE PIU GRANDE DELL'ULTIMO NODO DELLA RETE
						ChordPeer checkNode = (ChordPeer) Engine.getDefault().getNodes().get(initializedNode-1).clone();
						out_of_range =incrementKeyNode.compareTo(checkNode.getBigId()); 
						
						if( out_of_range > 0 )
						{
							checkNode = (ChordPeer) Engine.getDefault().getNodes().get(0).clone();
							if(!fingerTable.contains(checkNode.getId()))
							{
								String app = checkNode.getId();
								fingerTable.add(app);
								chordPeer.setFingerTable(app);
								i=0;
								step=0;
							}	
							comparison_result=1;
							
						}
						
						while(comparison_result < 0 )
						{
							index = (i + step)%initializedNode;
							checkNode = (ChordPeer) Engine.getDefault().getNodes().get(index).clone();
							keyNodeToCompare = checkNode.getBigId();

							comparison_result = keyNodeToCompare.compareTo(incrementKeyNode);
						//SALVO L'INDICE DEL NODO NELLA FINGER	
						if( comparison_result > 0 )
						{
							checkNode = (ChordPeer) Engine.getDefault().getNodes().get(index).clone();
							if(!fingerTable.contains(checkNode.getId()))
							{
								String app = checkNode.getId(); 
								fingerTable.add(app);
								chordPeer.setFingerTable(app);
							}	
						}
						else 
							step++;
						
						}
					 } 
				for(int c = 0; c< fingerTable.size(); c++)
				 System.out.println("elemento "+ c + ": " + fingerTable.get(c));
		}

		public int getNumInitialConnections() {
			return numInitialConnections;
		}

		public void setNumInitialConnections(int numInitialConnections) {
			this.numInitialConnections = numInitialConnections;
		}

		
		
	}
