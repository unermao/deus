package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.ArrayList;
import java.util.Properties;

public class ChordPeer extends Peer {
	private static final String FINGER_TABLE_SIZE = "fingerTableSize";
	private int fingerTableSize = 0;
	private ChordPeer predecessor = null;
	public ChordPeer fingerTable[] = null;

	public ChordPeer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);

		if (params.getProperty(FINGER_TABLE_SIZE) == null)
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " param is expected.");

		try {
			fingerTableSize = Integer.parseInt(params
					.getProperty(FINGER_TABLE_SIZE));
		} catch (NumberFormatException ex) {
			throw new InvalidParamsException(FINGER_TABLE_SIZE
					+ " must be a valid int value.");
		}
	}

	public Object clone() {
		ChordPeer clone = (ChordPeer) super.clone();
		clone.predecessor = null;
		clone.fingerTable = new ChordPeer[fingerTableSize];
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
		for (int i = 0; i < fingerTableSize; i++)
			fingerTable[i] = this;
	}

	// il nodo che chiama questo metodo si fa comunicare da un nodo scelto a
	// caso all'interno della rete
	// qual il suo successore e la sua fingerTable iniziale
	// il suo successore viene cercato a partire dall'identificatore appena pi
	// grande di lui
	// il predecessore viene settato con il predecessore del successore del nodo
	// diventato suo successore
	// in seguito scorre la fingerTable e aggiorna la tabella: se la chiave
	// generata compresa tra il nodo chiamante e l'elemento i-esimo
	// della sua fingerTable allora l'elemento i+1 della tabella diventa
	// l'elemento i
	// altrimenti cerco un successore per l'elemento i-esimo della tabella e lo
	// inserisco nella posizione i+1

	public void initFingerTable(ChordPeer gatewayNode) {
		setSuccessor(gatewayNode
				.findSuccessor(calculateNextNodeId(getKey(), 0)));
		setPredecessor(getSuccessor().getPredecessor());
		getSuccessor().setPredecessor(this);

		for (int i = 0; i < fingerTableSize - 1; i++) {
			if (isInInterval(calculateNextNodeId(getKey(), i + 1), getKey(),
					fingerTable[i].getKey(), true, false)) {
				fingerTable[i + 1] = fingerTable[i];
			} else {
				fingerTable[i + 1] = gatewayNode
						.findSuccessor(calculateNextNodeId(getKey(), i + 1));
			}
		}
	}

	// chiede al nodo chiamante di trovare il successore del nodo identificato
	// dall'identificatore passato
	// chiama il metodo findPredecessor passandogli l'identificatore del nodo
	// restituirˆ un nodo che il precedenti di quello cercato
	// basterˆ applicare il metodo getSuccessor() per ottenere il nodo cercato

	public ChordPeer findSuccessor(int nodeId) {
		return findPredecessor(nodeId).getSuccessor();
	}

	// il nodo che chiama questo metodo cerca il predecessore del nodo
	// identificato dall'id passato
	// finch l'identificatore passato non compreso tra chi chiama il metodo e il
	// successore di chi chiama il metodo,
	// viene eseguito sul nodo chiamante il metodo di ricerca del nodo
	// predecessore piu vicino all'identificatore passato
	//

	public ChordPeer findPredecessor(int nodeId) {
		ChordPeer p = this;
		while (!isInInterval(nodeId, p.getKey(), p.getSuccessor().getKey(),
				false, true)) {
			p = p.closestPrecedingFinger(nodeId);
			if (p.equals(this))
				return getPredecessor();
		}
		return p;
	}

	// Ritorna il nodo che ha l'identificatore pi vicino all'identificatore
	// passato e che lo precede
	// Scorre la fingerTable del nodo che chiama questo metodo dall'ultima entry
	// e controlla se l'identificatore presente compreso tra l'identificatore di
	// chi chiama e l'identificatore passato
	// se compreso restituisce il nodo con l'identificatore presente nella
	// fingerTable altrimenti restituisce se stesso
	// perch significa che lui quello piu vicino

	public ChordPeer closestPrecedingFinger(int nodeId) {
		for (int i = fingerTableSize; i > 0; i--) {
			if (isInInterval(fingerTable[i - 1].getKey(), getKey(), nodeId,
					false, false)) {
				return fingerTable[i - 1];
			}

		}
		return this;
	}

	// il metodo aggiorna tutte le tabelle dei nodi che hanno qualche
	// riferimento con il nodo arrivato
	// trova tutti i nodi che precedono e quindi hanno nell'i-esima entry il
	// nodo chiamante e aggiorna la sua tabella
	// lancia updateFingerTable passandogli il nodo che arrivato e l'indice
	// della finger dove presente il nodo che deve modificare
	// la sua fingerTable

	public void updateOthers() {
		ChordPeer p = null;
		for (int i = 0; i < fingerTableSize; i++) {
			p = findPredecessor(calculateNextNodeId(getKey(), i, true));
			p.updateFingerTable(this, i);
		}

	}

	// aggiorna la fingerTable di un nodo, controllando se il suo identificatore
	// compreso tra il nodo arrivato e il nodo che si trova
	// nella posizione individuata in updateOthers. se cosi aggiorna la sua
	// fingerTable con il nodo che arrivato precedentemente
	// invoca l'aggiornamento del predecessore del nodo che ha modificato la sua
	// fingerTable

	public void updateFingerTable(ChordPeer s, int entry) {
		if (isInInterval(s.getKey(), getKey(), fingerTable[entry].getKey(),
				true, false)) {
			fingerTable[entry] = s;
			getPredecessor().updateFingerTable(s, entry);
		}

	}

	// permette di capire se l'identificatore del nodo passato compreso tra
	// altri 2 identificatori (a e b)

	private boolean isInInterval(int nodeId, int a, int b, boolean isAIncluded,
			boolean isBIncluded) {

		if (a == b)
			return true;

		if (a < b) {
			if (nodeId == a && isAIncluded)
				return true;

			if (isAIncluded && isBIncluded) {
				if (nodeId >= a && nodeId <= b)
					return true;
			} else if (isAIncluded && !isBIncluded) {
				if (nodeId >= a && nodeId < b)
					return true;
			} else if (!isAIncluded && isBIncluded) {
				if (nodeId > a && nodeId <= b)
					return true;
			} else if (!isAIncluded && !isBIncluded) {
				if (nodeId > a && nodeId < b)
					return true;
			}
		} else {
			if (nodeId == b && isBIncluded)
				return true;

			if (isAIncluded && isBIncluded) {
				if ((nodeId >= a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId <= b))
					return true;
			} else if (isAIncluded && !isBIncluded) {
				if ((nodeId >= a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId < b))
					return true;
			} else if (!isAIncluded && isBIncluded) {
				if ((nodeId > a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId <= b))
					return true;
			} else if (!isAIncluded && !isBIncluded) {
				if ((nodeId > a && nodeId <= Engine.getDefault()
						.getKeySpaceSize())
						|| (nodeId >= 0 && nodeId < b))
					return true;
			}
		}
		return false;
	}

	private int calculateNextNodeId(int nodeId, int step) {
		return calculateNextNodeId(nodeId, step, false);
	}

	private int calculateNextNodeId(int nodeId, int step, boolean subtract) {

		if (subtract) {
			nodeId = (int) (((nodeId - Math.pow(2, step)) % Engine.getDefault()
					.getKeySpaceSize()));
		} else {
			nodeId = (int) (((nodeId + Math.pow(2, step)) % Engine.getDefault()
					.getKeySpaceSize()));
		}

		if (nodeId < 0)
			nodeId += Engine.getDefault().getKeySpaceSize();
		if (nodeId == Engine.getDefault().getKeySpaceSize())
			nodeId = 0;

		return nodeId;
	}

	public void stabilize() {
		ChordPeer x = getSuccessor().getPredecessor();
		if (isInInterval(x.getKey(), getKey(), getSuccessor().getKey(), false,
				false))
			setSuccessor(x);
		getSuccessor().notify(this);
	}

	public void fixFingers() {
		int i = Engine.getDefault().getSimulationRandom().nextInt(
				fingerTableSize - 1) + 1;
		fingerTable[i] = findSuccessor(calculateNextNodeId(getKey(), i + 1));
	}

	public void notify(ChordPeer stabilizingNode) {
		if (getPredecessor() == null
				|| isInInterval(stabilizingNode.getKey(), getPredecessor()
						.getKey(), getKey(), false, false))
			setPredecessor(stabilizingNode);

	}

}
