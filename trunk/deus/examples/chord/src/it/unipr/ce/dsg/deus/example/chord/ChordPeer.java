package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

/**
 * <p>
 * ChordPeers are characterized by three kinds of consumable resources: CPU,
 * RAM, DISK. Moreover, each RevolPeer has a chromosome, i.e. a set of
 * parameters whose values are randomly initialized when the RevolPeer is
 * instantiated, and may change during its lifetime, depending on external
 * events. The RevolPeer keeps track of the number of sent queries (Q) and of
 * the number of query hits (QH). The query hit ratio (QHR = QH/Q) is
 * initialized to 0.
 * </p>
 * 
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordPeer extends Peer {
	private static int counter = 0;
	public static final int NUMBITS = 4;
	private ChordPeer predecessor = null;
	public ChordPeer fingerTable[] = null;
	private int lastFixedFinger = 0;

	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		initialize();
	}

	public void initialize() throws InvalidParamsException {

	}

	public Object clone() {
		ChordPeer clone = (ChordPeer) super.clone();
		clone.predecessor = null;
		clone.fingerTable = new ChordPeer[NUMBITS];
		clone.lastFixedFinger = 0;
		return clone;
	}

	public ChordPeer getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(ChordPeer predecessor) {
		this.predecessor = predecessor;
	}

	public ChordPeer getSuccessor() {
		return fingerTable[0];
	}

	public void setSuccessor(ChordPeer successor) {
		fingerTable[0] = successor;
	}

	public ChordPeer[] getFingerTable() {
		return fingerTable;
	}
	
	public void initFirstFingerTable() {
		getLogger().info("\tIl nodo: " + this.id + "\tchiama la initfingerTable ");
		for (int i = 0; i < NUMBITS; i++)
			fingerTable[i] = this;
		getLogger().info("\tPredecessore: " + getPredecessor().id + "\tSuccessore: " + getSuccessor().id);
		getLogger().info("\tFingerTable del nodo: " + this.id + "\t: ");
		for(int e = 0; e <fingerTable.length; e++)	
			getLogger().info("\t " + fingerTable[e].getId());
	}

	//il nodo che chiama questo metodo si fa comunicare da un nodo scelto a caso all'interno della rete
	//qualè il suo successore e la sua fingerTable iniziale
	//il suo successore viene cercato a partire dall'identificatore appena più grande di lui
	//il predecessore viene settato con il predecessore del successore del nodo diventato suo successore
	//in seguito scorre la fingerTable e aggiorna la tabella: se la chiave generata è compresa tra il nodo chiamante e l'elemento i-esimo
	//della sua fingerTable allora l'elemento i+1 della tabella diventa l'elemento i
	//altrimenti cerco un successore per l'elemento i-esimo della tabella e lo inserisco nella posizione i+1
	
	public void initFingerTable(ChordPeer gatewayNode) {
		getLogger().info("\tINIT_FINGER_TABLE");
		getLogger().info("\t********IL NODO: " + this.id + "\tSI COLLEGA A: " + gatewayNode.id);
		getLogger().info("\t*****************************************************************");
		getLogger().info("\t il gatewayNode esegue findSuccessor e findPredecessor sull'id: " + calculateNextNodeId(getId(), 0));
		setSuccessor(gatewayNode.findSuccessor(calculateNextNodeId(getId(), 0)));
		setPredecessor(gatewayNode.findPredecessor(calculateNextNodeId(getId(), 0)));
		getLogger().info("\tPREDECESSORE: " + getPredecessor().getId() + "\te SUCCESSORE trovati: " + getSuccessor().getId());
		for (int i = 0; i < NUMBITS - 1; i++) {
			getLogger().info("\tModifico la fingerTable: indice i = " + i);
			getLogger().info("\tVerifico se: " + calculateNextNodeId(getId(), i + 1) + "\tè compreso tra il nodo arrivato: " + getId() + "\t e cosa cè nella posizione: " +i+ "della fingerTable:" + fingerTable[i].getId());
			if (isInInterval(calculateNextNodeId(getId(), i + 1), getId(),fingerTable[i].getId()))
			{
				getLogger().info("\t E' NELL'INTERVALLO imposto la posizione " + (i+1) + "\t della finger con quella: " + i + "\t fingerTable["+i+"] " + fingerTable[i].getId());
				fingerTable[i + 1] = fingerTable[i];
			}
				else
				{
				getLogger().info("\t NON E' NELL'INTERVALLO il gatewayNode lancia findSuccessor su " + calculateNextNodeId(getId(), i+1) + "\t e mette il risultato in fingerTable["+(i+1)+"] ");
				fingerTable[i + 1] = gatewayNode.findSuccessor(calculateNextNodeId(getId(), i+1));
				}
		}
		
		getLogger().info("\tFINE INIT_FINGER_TABLE: FingerTable del nodo: " + this.id + "\t= ");
		for(int e = 0; e <fingerTable.length; e++)	
			getLogger().info("\t " + fingerTable[e].getId());
		
	}

	//chiede al nodo chiamante di trovare il successore del nodo identificato dall'identificatore passato
	//chiama il metodo findPredecessor passandogli l'identificatore del nodo
	//restituirà un nodo che è il precedenti di quello cercato
	//basterà applicare il metodo getSuccessor() per ottenere il nodo cercato
	
	public ChordPeer findSuccessor(String nodeId) {
		getLogger().info("\tFIND_SUCCESSOR");
		getLogger().info("\t "+this.id +" chiama la findPredecessor con argomento: " + nodeId);
		return findPredecessor(nodeId).getSuccessor();
		
	}

	//il nodo che chiama questo metodo cerca il predecessore del nodo identificato dall'id passato
	//finchè l'identificatore passato non è compreso tra chi chiama il metodo e il successore di chi chiama il metodo,
	//viene eseguito sul nodo chiamante il metodo di ricerca del nodo predecessore piu vicino all'identificatore passato
	//
	
	public ChordPeer findPredecessor(String nodeId) {
		getLogger().info("\tFIND_PREDECESSOR eseguita da " + this.id );
		ChordPeer predecessor = this;
		getLogger().info("\t l'argomento: " + nodeId + "\t è compreso tra il chiamante " + predecessor.getId() + "\t e il suo successore " +  predecessor.getSuccessor().getId() + "\t? ");
		while (!isInInterval(nodeId, predecessor.getId(), predecessor.getSuccessor().getId())) {
			getLogger().info("\t NON E' COMPRESO");
			getLogger().info("\t il nodo " + predecessor.getId() + "chiama closestPrecedingFinger con argomento : "  + nodeId);
			predecessor = predecessor.closestPrecedingFinger(nodeId);
			getLogger().info("\t il metodo closestPrecedingFinger ritorna come risultato : " + predecessor.getId() );
			getLogger().info("\t il successore del nodo ritornato è: " + predecessor.getSuccessor().getId() );
		}
		getLogger().info("\t E' COMPRESO NELL'INTERVALLO il nodo predecessore è: " + predecessor.getId());
		getLogger().info("\t Mentre il successore del predecessore, ossia il successore cercato è: " + predecessor.getSuccessor().getId());
		return predecessor;
	}

	//Ritorna il nodo che ha l'identificatore più vicino all'identificatore passato e che lo precede
	//Scorre la fingerTable del nodo che chiama questo metodo dall'ultima entry
	//e controlla se l'identificatore presente è compreso tra l'identificatore di chi chiama e l'identificatore passato
	//se è compreso restituisce il nodo con l'identificatore presente nella fingerTable altrimenti restituisce se stesso
	//perchè significa che lui è quello piu vicino
	
	public ChordPeer closestPrecedingFinger(String nodeId) {
		getLogger().info("\tCLOSEST_PRECEDING_FINGER eseguita da " + this.id );
		for (int i = NUMBITS; i > 0; i--) {
			getLogger().info("\t controllo se il nodo presente alla posizione: " +(i-1)+ ": " + fingerTable[i - 1].getId() + "\t è compreso tra il nodo chiamante" + getId() + "\t e l'argomento " + nodeId);
			//System.out.println("fingerTable[i - 1].getId() " + fingerTable[i - 1].getId() + " getId " + getId() +" nodeId " + nodeId);
			if (isInInterval(fingerTable[i - 1].getId(), getId(), nodeId))
			{
				getLogger().info("\t E' IN QUESTO INTERVALLO ritorno l'elemento della fingerTable " + (i-1));
				return fingerTable[i - 1];
			}
			
		}
		getLogger().info("\t NON E' NELL'INTERVALLO ritorno il chiamante");
		return this;
	}

	//il metodo aggiorna tutte le tabelle dei nodi che hanno qualche riferimento con il nodo arrivato
	//trova tutti i nodi che precedono e quindi hanno nell'i-esima entry il nodo chiamante e aggiorna la sua tabella
	//lancia updateFingerTable passandogli il nodo che è arrivato e l'indice della finger dove è presente il nodo che deve modificare 
	//la sua fingerTable
	
	public void updateOthers() {
		getLogger().info("\tUPDATE_OTHERS:" );
		ChordPeer predecessor = null;
		for (int i = 0; i < NUMBITS; i++) {
			getLogger().info("\tlancio findPrecessor da update su : " + calculateNextNodeId(getId(), i, true) + "\t ");
			 predecessor = findPredecessor(calculateNextNodeId(getId(), i, true));
			getLogger().info("\til risultato è: " + predecessor.getId());
			getLogger().info("\tlancio updateFingerTable su: " + this.getId() + "\t passandogli come parametro: "+ i);
			predecessor.updateFingerTable(this, i);
			getLogger().info("\tAggiorno la tabella del nodo: " + predecessor.getId() + "\t: ");
			for(int e = 0; e <predecessor.fingerTable.length; e++)	
				getLogger().info("\t " + predecessor.fingerTable[e].getId());
		}
		
	}

	//aggiorna la fingerTable di un nodo, controllando se il suo identificatore è compreso tra il nodo arrivato e il nodo che si trova
	//nella posizione individuata in updateOthers. se è cosi aggiorna la sua fingerTable con il nodo che è arrivato precedentemente
	//invoca l'aggiornamento del predecessore del nodo che ha modificato la sua fingerTable
	
	public void updateFingerTable(ChordPeer node, int entry) {
		getLogger().info("\tverifico se  " + node.getId() + "\t è compreso tra "  + getId() + "\t e " + fingerTable[entry].getId() + "\t? ");
		getLogger().info("\tla risposta è: " + isInInterval(node.getId(), getId(), fingerTable[entry].getId()));
		if (isInInterval(node.getId(), getId(), fingerTable[entry].getId())) {
			getLogger().info("\t aggiorno fingerTable["+entry+"] con " + node.getId() );
			fingerTable[entry] = node;
			if (!getPredecessor().equals(this))
			{
				getLogger().info("\t se ho un predecessore chiamo updateFingerTable su " + getPredecessor().getId() + "\t passandogli " + node.getId() + "\t e la posizione " + entry);
				getPredecessor().updateFingerTable(node, entry);
			}
		}

	}

	//permette di capire se l'identificatore del nodo passato è compreso tra altri 2 identificatori (a e b)
	
	private boolean isInInterval(String nodeId, String a, String b) {
		String min = "00000000000000000000000000000000";
		String max = "ffffffffffffffffffffffffffffffff";
		if (a.equals(b))
			return true;

		//TODO bisogna verificare se questa cosa è corretta...
		if (nodeId.equals(a) || nodeId.equals(b))
		{
			//System.out.println("true");
			return false;
			
		}
		if (a.compareTo(b) < 0) {
			if (nodeId.compareTo(a) > 0 && nodeId.compareTo(b) < 0)
				return true;
		} else {
			if ((nodeId.compareTo(a) > 0 && nodeId.compareTo(max) < 0)
					|| (nodeId.compareTo(min) > 0 && nodeId.compareTo(b) < 0))
				return true;
		}

		return false;

	}

	public ChordPeer notify(ChordPeer stabilizingNode) { 
		if (predecessor == null || (stabilizingNode.getId().compareTo(predecessor.getId()) > 0 &&
		  stabilizingNode .getId().compareTo(this.getId()) < 0)) 
		 { 
			predecessor = stabilizingNode; 
			return predecessor; 
		 } 
		return null; 
		}
		  
	public void fixFingers() { 
		int i = (int) (Math.random()*fingerTable.length);
		  String Nodeid = fingerTable[i].getId(); 
		  fingerTable[i] =  findSuccessor(calculateNextNodeId(Nodeid, i));
		  setLastFixedFinger(i);
		  //System.out.println("i " + i + " fingerTable[i] " + fingerTable[i] + "index " + getLastFixedFinger());		
		}
		 
		 
		 public int getLastFixedFinger() { 
			 return lastFixedFinger; 
			 }
		 
		 public void setLastFixedFinger(int lastFixedFinger) { 
			 this.lastFixedFinger = lastFixedFinger; 
			 }
	
	
	private String calculateNextNodeId(String startId, int step) {
		return calculateNextNodeId(startId, step, false);
	}

	private String calculateNextNodeId(String startId, int step,
			boolean subtract) {
		BigInteger minKey = BigInteger.valueOf(0);
		BigInteger maxKey = new BigInteger("ffffffffffffffffffffffffffffffff",
				16);
		BigInteger nodeBigId = new BigInteger(startId, 16);
		BigInteger stepBigId = null;

		if (step == 0)
			stepBigId = BigInteger.valueOf(1);
		else {
			stepBigId = BigInteger.valueOf(2);
			for (int i = 1; i < step; i++) {
				stepBigId = stepBigId.multiply(BigInteger.valueOf(2));
			}
		}

		if (subtract)
			nodeBigId = nodeBigId.subtract(stepBigId);
		else
			nodeBigId = nodeBigId.add(stepBigId);

		if (nodeBigId.compareTo(minKey) < 0)
			nodeBigId = maxKey.add(nodeBigId);
		if (nodeBigId.compareTo(maxKey) > 0)
			nodeBigId = nodeBigId.subtract(maxKey);
		return Engine.getDefault().bytesToHex(nodeBigId.toByteArray());
	}

}
