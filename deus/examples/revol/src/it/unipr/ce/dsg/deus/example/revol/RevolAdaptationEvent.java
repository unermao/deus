package it.unipr.ce.dsg.deus.example.revol;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class RevolAdaptationEvent extends Event {
	private static final String HAS_SAME_ASSOCIATED_NODE = "hasSameAssociatedNode";
	private boolean hasSameAssociatedNode = false;
	private static final String A_0 = "a0";
	private static final String A_1 = "a1";
	private static final String A_2 = "a2";
	private static final String SELECTION_STRATEGY = "selectionStrategy";
	
	private RevolNode associatedNode = null;
	private double currentFitness = 0;
	private int a0 = 0;
	private int a1 = 0;
	private int a2 = 0;
	private String selectionStrategy = null;
	private double delta = 0.001;

	public RevolAdaptationEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(HAS_SAME_ASSOCIATED_NODE)) 
			hasSameAssociatedNode = Boolean.parseBoolean(params.getProperty(HAS_SAME_ASSOCIATED_NODE));
		if (params.containsKey(A_0))
			a0 = Integer.parseInt(params.getProperty(A_0));
		if (params.containsKey(A_1))
			a1 = Integer.parseInt(params.getProperty(A_1));
		if (params.containsKey(A_2))
			a2 = Integer.parseInt(params.getProperty(A_2));
		if (params.containsKey(SELECTION_STRATEGY))
			selectionStrategy = params.getProperty(SELECTION_STRATEGY);		
	}

	public void setAssociatedNode(RevolNode associatedNode) {
		this.associatedNode = associatedNode;
	}
	
	public boolean hasSameAssociatedNode() {
		return hasSameAssociatedNode;
	}

	public void setHasSameAssociatedNode(boolean hasSameAssociatedNode) {
		this.hasSameAssociatedNode = hasSameAssociatedNode;
	}

	public Object clone() {
		RevolAdaptationEvent clone = (RevolAdaptationEvent) super.clone();
		if (!hasSameAssociatedNode) 
			clone.associatedNode = null; 
		return clone;
	}

	private double computeFitness(RevolNode node) {
		//return ((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQhr() + delta));
		//return (node.getQhr() / (a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()));
		return (((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta)) 
				+ 1000 * ((node.getQ()) / (node.getQh() + delta)));
	}
	
	private double computeFitness(int[] c, double q, double qh) {
		//return ((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qhr + delta));
		//return (qhr / a0*c[0]/10 + a1*c[1] + a2*c[2]*2);
		return (((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta)) 
				+ 1000 * ((q) / (qh + delta)));
	}
	
	private RevolNode selection() {
		RevolNode bestNeighbor = null;
		if (selectionStrategy.equals("bestFitness")) {	
			/* vecchia soluzione
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
		    */
			RevolNode currentNeighbor = null;
			double currentNeighborFitness = 0;
			double bestNeighborFitness = 0;
			for (int i = 0; i < associatedNode.getNeighbors().size(); i++) {
				currentNeighbor = (RevolNode) associatedNode.getNeighbors().get(i);
				currentNeighborFitness = computeFitness(currentNeighbor);
				if (i == 0) {
					bestNeighbor = currentNeighbor;
					bestNeighborFitness = currentNeighborFitness;
				}
				else {
					if (currentNeighborFitness < bestNeighborFitness) {
						bestNeighborFitness = currentNeighborFitness;
						bestNeighbor = currentNeighbor;
					}
				}					
			}
		}
		else if (selectionStrategy.equals("random")) {
			// TODO
		}
		else if (selectionStrategy.equals("proportional")) {
			// sommo le fitness di tutti i cromosomi
			int numNeighbors = associatedNode.getNeighbors().size();
			double sumOfFitnesses = 0;
			for (int i = 0; i < numNeighbors; i++) {
				getLogger().fine(i + " 's fitness is: " + computeFitness((RevolNode) associatedNode.getNeighbors().get(i)));
				sumOfFitnesses += 1 / computeFitness((RevolNode) associatedNode.getNeighbors().get(i));
			}
			double fitnessCDF[] = new double[numNeighbors];
			fitnessCDF[0] = 1 / (computeFitness((RevolNode) associatedNode.getNeighbors().get(0)) * sumOfFitnesses);
			getLogger().fine("0 " + fitnessCDF[0]);
			for (int i = 1; i < numNeighbors; i++) {
				fitnessCDF[i] = fitnessCDF[i-1] + 1 / (computeFitness((RevolNode) associatedNode.getNeighbors().get(i)) * sumOfFitnesses);
				getLogger().fine(i + " " + fitnessCDF[i]);
			}
			double randomDouble = Engine.getDefault().getSimulationRandom().nextDouble();
			int i = 0;
			if (randomDouble > fitnessCDF[0]) {
				do {
					i++;
				} while (randomDouble > fitnessCDF[i]);
			}
			getLogger().fine("random = " + randomDouble + ", thus selected neighbor is " + i);
			bestNeighbor = (RevolNode) associatedNode.getNeighbors().get(i);
		}
		else if (selectionStrategy.equals("tournament")) {
			// TODO
		}
		else if (selectionStrategy.equals("rank-based")) {
			// TODO
		}
	    return bestNeighbor;
	}
	
	private int[][] crossover(int[] c1, int[] c2) {
		int[][] offspring = new int[2][3];
		// the crosspoint may be 1 or 2
		int crosspoint = Engine.getDefault().getSimulationRandom().nextInt(2) + 1; 
		for (int i = 0; i < crosspoint; i++) {
			offspring[0][i] = c1[i];
			offspring[1][i] = c2[i];
		}
		for (int i = crosspoint; i < 3; i++) {
			offspring[1][i] = c1[i];
			offspring[0][i] = c2[i];
		}
		return offspring;
	}
	
	private int[][] mutation(int[][] offspring) {
		return offspring;  // TODO implementare la mutazione!
	}
	
	@Override
	public void run() throws RunException {	
		// la initial population è data dalla config locale e da quelle dei nodi vicini
		if (associatedNode.getNeighbors().size() == 0)
			return;
		
		getLogger().fine("### adaptation! for node " + associatedNode.getId());
		// valuta la fitness della configurazione corrente
		currentFitness = computeFitness(associatedNode);
		
		// valuta la fitness delle configurazioni dei vicini
		RevolNode bestNeighbor = selection();
				
		// se la fitness dei vicini è peggiore di quella del nodo, mantieni la config attuale
	    if (currentFitness >= computeFitness(bestNeighbor))
	    	return;
	    else {		
			int g = ((RevolNode)associatedNode).getG();
			((RevolNode)associatedNode).setG(g+1);
			
			getLogger().fine("Generation: " + ((RevolNode)associatedNode).getG());
			getLogger().fine("adaptation: previous gen: " + associatedNode.getC()[0] + 
								" " + associatedNode.getC()[1] +
								" " + associatedNode.getC()[2]);
			
			// cross-over tra miglior config vicina e locale 
			int[][] offspring = crossover(associatedNode.getC(), bestNeighbor.getC());
			
			// mutazione casuale dei due individui ottenuti
			int[][] mutatedOffspring = mutation(offspring);
			
			// cfr i due individui con la config locale vecchia
			// la migliore delle 3 config. viene settata come nuova config	
			double firstFitness = computeFitness(mutatedOffspring[0], associatedNode.getQ(), associatedNode.getQh());
			double secondFitness = computeFitness(mutatedOffspring[1], associatedNode.getQ(), associatedNode.getQh());
			if ((firstFitness > secondFitness) && (currentFitness < firstFitness))
				associatedNode.setC(mutatedOffspring[0]);
			else if ((firstFitness < secondFitness) && (currentFitness < secondFitness))
				associatedNode.setC(mutatedOffspring[1]);
			else if ((firstFitness == secondFitness) && (currentFitness < firstFitness))
				associatedNode.setC(mutatedOffspring[Engine.getDefault().getSimulationRandom().nextInt(1)]);
			else
				return;
			
			getLogger().fine("adaptation: new gen: " + associatedNode.getC()[0] + 
					" " + associatedNode.getC()[1] +
					" " + associatedNode.getC()[2]);
			
			//associatedNode.dropExceedingNeighbors();
			associatedNode.dropExceedingResourceAdvs();
	    }		
	}

}
