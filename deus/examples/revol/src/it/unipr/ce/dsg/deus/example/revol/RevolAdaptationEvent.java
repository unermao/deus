package it.unipr.ce.dsg.deus.example.revol;

import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class RevolAdaptationEvent extends Event {
	private static final String A_0 = "a0";
	private static final String A_1 = "a1";
	private static final String A_2 = "a2";
	private static final String A_3 = "a3";
	
	private RevolNode associatedNode = null;
	private double currentFitness = 0;
	private int a0 = 0;
	private int a1 = 0;
	private int a2 = 0;
	private int a3 = 0;
	private double delta = 0.001;

	public RevolAdaptationEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(A_0))
			a0 = Integer.parseInt(params.getProperty(A_0));
		if (params.containsKey(A_1))
			a1 = Integer.parseInt(params.getProperty(A_1));
		if (params.containsKey(A_2))
			a2 = Integer.parseInt(params.getProperty(A_2));
		if (params.containsKey(A_3))
			a3 = Integer.parseInt(params.getProperty(A_3));
	}

	public void setNodeToAdapt(RevolNode associatedNode) {
		this.associatedNode = associatedNode;
	}

	public Object clone() {
		RevolAdaptationEvent clone = (RevolAdaptationEvent) super.clone();
		//clone.associatedNode = null; // attenzione! in questo modo l'evento eredita l'associated node dell'evento clonato
		return clone;
	}

	private double computeFitness(RevolNode node) {
		return ((a0*node.getKMax() + a1*node.getFk() + a2*node.getTtlMax() + a3*node.getDMax()) / (node.getQhr() + delta));
	}
	
	private double computeFitness(int[] c, double qhr) {
		return ((a0*c[0]*2 + a1*c[1]/10 + a2*c[2] + a3*c[3]*2) / (qhr + delta));
	}
	
	private int[][] crossover(int[] c1, int[] c2) {
		int[][] offspring = new int[2][4];
		// the crosspoint may be 1,2, or 3
		int crosspoint = Engine.getDefault().getSimulationRandom().nextInt(3) + 1; 
		for (int i = 0; i < crosspoint; i++) {
			offspring[0][i] = c1[i];
			offspring[1][i] = c2[i];
		}
		for (int i = crosspoint; i < 4; i++) {
			offspring[1][i] = c1[i];
			offspring[0][i] = c2[i];
		}
		return offspring;
	}
	
	private int[][] mutate(int[][] offspring) {
		return offspring;  // FIXME implementare la mutazione!
	}
	
	@Override
	public void run() throws RunException {	
		// la initial population è data dalla config locale e da quelle dei nodi vicini
		if (associatedNode.getNeighbors().size() == 0)
			return;
		
		//System.out.println("### \n adaptation: go!");
		// valuta la fitness della configurazione corrente
		currentFitness = computeFitness(associatedNode);
		
		// valuta la fitness delle configurazioni dei vicini
		RevolNode bestNeighbor = null;
		double bestNeighborFitness = 0;
		RevolNode temp = null;
		double tempFitness = 0;
	    for (Iterator<Node> it = associatedNode.getNeighbors().iterator(); it.hasNext(); ) {
	    	temp = (RevolNode) it.next(); 
	    	tempFitness = computeFitness(temp);
	    	if (tempFitness > bestNeighborFitness) {
	    		bestNeighbor = temp;
	    		bestNeighborFitness = tempFitness;
	    	}
	    }
				
		// se la fitness dei vicini è peggiore di quella del nodo, mantieni la config attuale
	    if (currentFitness >= bestNeighborFitness)
	    	return;
	    else {		
			int g = ((RevolNode)associatedNode).getG();
			((RevolNode)associatedNode).setG(g+1);
			/*
			System.out.println(((RevolNode)associatedNode).getG() + " adaptation of node " + associatedNode);
			System.out.println("adaptation: previous gen: " + associatedNode.getC()[0] + 
								" " + associatedNode.getC()[1] +
								" " + associatedNode.getC()[2] +
								" " + associatedNode.getC()[3]);
			*/
			// cross-over tra miglior config vicina e locale 
			//(in futuro la selez. deve essere probabilistica)
			int[][] offspring = crossover(associatedNode.getC(), bestNeighbor.getC());
			
			// mutazione casuale dei due individui ottenuti
			int[][] mutatedOffspring = mutate(offspring);
			
			// cfr i due individui con la config locale vecchia
			// la migliore delle 3 config. viene settata come nuova config	
			double firstFitness = computeFitness(mutatedOffspring[0], associatedNode.getQhr());
			double secondFitness = computeFitness(mutatedOffspring[1], associatedNode.getQhr());
			if ((firstFitness > secondFitness) && (currentFitness < firstFitness))
				associatedNode.setC(mutatedOffspring[0]);
			else if ((firstFitness < secondFitness) && (currentFitness < secondFitness))
				associatedNode.setC(mutatedOffspring[1]);
			else if ((firstFitness == secondFitness) && (currentFitness < firstFitness))
				associatedNode.setC(mutatedOffspring[Engine.getDefault().getSimulationRandom().nextInt(1)]);
			else
				return;
			
			/*
			System.out.println("adaptation: new gen: " + associatedNode.getC()[0] + 
					" " + associatedNode.getC()[1] +
					" " + associatedNode.getC()[2] +
					" " + associatedNode.getC()[3]);
			*/
			associatedNode.dropExceedingNeighbors();
			associatedNode.dropExceedingResourceAdvs();
	    }		
	}

}
