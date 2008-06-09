package it.unipr.ce.dsg.deus.example.revol;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class RevolAdaptationEvent extends NodeEvent {
	private static final String A_0 = "a0";
	private static final String A_1 = "a1";
	private static final String A_2 = "a2";
	private static final String SELECTION_STRATEGY = "selectionStrategy";
	
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
		super.initialize(); // important!
		if (params.containsKey(A_0))
			a0 = Integer.parseInt(params.getProperty(A_0));
		if (params.containsKey(A_1))
			a1 = Integer.parseInt(params.getProperty(A_1));
		if (params.containsKey(A_2))
			a2 = Integer.parseInt(params.getProperty(A_2));
		if (params.containsKey(SELECTION_STRATEGY))
			selectionStrategy = params.getProperty(SELECTION_STRATEGY);		
	}

	public Object clone() {
		RevolAdaptationEvent clone = (RevolAdaptationEvent) super.clone();
		return clone;
	}

	private double computeFitness(RevolNode node) {
		double q = node.getQ();
		double qh = node.getQh();
		double A = a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax();
		return ((1 - qh/(q + delta)) / A + (qh /(q + delta)) * A);
		//return ((1/(delta*delta)) * (q/(qh + delta) - 1) / A  + A * (qh / (q + delta)));
		//return (((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta)) + (1/delta) * ((node.getQ()) / (node.getQh() + delta)));
		//return (((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta)) + ((node.getQ()) / (node.getQh() + delta)));
		//return ((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta));
	}
	
	private double computeFitness(int[] c, double q, double qh) {
		double A = a0*c[0]/10 + a1*c[1] + a2*c[2]*2;
		return ((1 - qh/(q + delta)) / A + (qh /(q + delta)) * A);
		//return ((1/(delta*delta)) * (q/(qh + delta) - 1) / A  + A * (qh / (q + delta)));
		//return (((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta)) + (1/delta) * ((q) / (qh + delta)));
		//return (((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta)) + ((q) / (qh + delta)));
		//return ((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta));
	}
	
	private RevolNode selection() {
		RevolNode bestNeighbor = null;
		if (selectionStrategy.equals("bestFitness")) {	
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
			double fitness[] = new double[numNeighbors];
			double sumFitnesses = 0;
			for (int i = 0; i < numNeighbors; i++) {
				fitness[i] = computeFitness((RevolNode) associatedNode.getNeighbors().get(i));
				getLogger().fine(i + " 's fitness is: " + fitness[i]);
				sumFitnesses += fitness[i];
			}
			double inverseFitness[] = new double[numNeighbors];
			double sumInverseFitnesses = 0;
			for (int i = 0; i < numNeighbors; i++) {
				inverseFitness[i] = sumFitnesses / fitness[i];
				sumInverseFitnesses += inverseFitness[i];
			}
			
			double inverseFitnessCDF[] = new double[numNeighbors];
			inverseFitnessCDF[0] = inverseFitness[0] / sumInverseFitnesses;
			getLogger().fine("0 " + inverseFitnessCDF[0]);
			for (int i = 1; i < numNeighbors; i++) {
				inverseFitnessCDF[i] = inverseFitnessCDF[i-1] + inverseFitness[i] / sumInverseFitnesses;
				getLogger().fine(i + " " + inverseFitnessCDF[i]);
			}
			double randomDouble = Engine.getDefault().getSimulationRandom().nextDouble();
			int i = 0;
			if (randomDouble > inverseFitnessCDF[0]) {
				do {
					i++;
				} while (randomDouble > inverseFitnessCDF[i]);
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
		int[][] mutatedOffspring = new int[2][3];
		
		return mutatedOffspring;  // TODO implementare la mutazione!
	}
	
	@Override
	public void run() throws RunException {	
		RevolNode associatedRevolNode = (RevolNode) associatedNode;
		
		// la initial population è data dalla config locale e da quelle dei nodi vicini
		if (associatedRevolNode.getNeighbors().size() == 0)
			return;
		
		getLogger().fine("### adaptation! for node " + associatedNode.getId());
		// valuta la fitness della configurazione corrente
		currentFitness = computeFitness(associatedRevolNode);
		getLogger().fine("currentFitness = " + currentFitness);
		
		// valuta la fitness delle configurazioni dei vicini
		RevolNode bestNeighbor = selection();
		getLogger().fine("best neighbor config: " + bestNeighbor.getC()[0] + 
				" " + bestNeighbor.getC()[1] +
				" " + bestNeighbor.getC()[2]);
				
		// se la fitness dei vicini è peggiore di quella del nodo, mantieni la config attuale
	    if (currentFitness <= computeFitness(bestNeighbor))
	    	return;
	    else {		
			int g = associatedRevolNode.getG();
			associatedRevolNode.setG(g+1);
			
			getLogger().fine("Generation: " + associatedRevolNode.getG());
			getLogger().fine("adaptation: previous gen: " + associatedRevolNode.getC()[0] + 
								" " + associatedRevolNode.getC()[1] +
								" " + associatedRevolNode.getC()[2]);
			
			// cross-over tra miglior config vicina e locale 
			int[][] offspring = crossover(associatedRevolNode.getC(), bestNeighbor.getC());
			
			
			// mutazione casuale dei due individui ottenuti
			int[][] mutatedOffspring = mutation(offspring);
			
			// cfr i due individui con la config locale vecchia
			// la migliore delle 3 config. viene settata come nuova config	
			double firstFitness = computeFitness(mutatedOffspring[0], associatedRevolNode.getQ(), associatedRevolNode.getQh());
			double secondFitness = computeFitness(mutatedOffspring[1], associatedRevolNode.getQ(), associatedRevolNode.getQh());
			if ((currentFitness <= firstFitness) && (firstFitness <= secondFitness))
				return;
			else if ( ((firstFitness <= currentFitness) && (currentFitness <= secondFitness))
					|| ((firstFitness <= secondFitness) && (secondFitness <= currentFitness)) )
				associatedRevolNode.setC(mutatedOffspring[0]);
			else if ( ((secondFitness <= currentFitness) && (currentFitness <= firstFitness))
					|| ((secondFitness <= firstFitness) && (firstFitness <= currentFitness)) )
				associatedRevolNode.setC(mutatedOffspring[1]);			
			getLogger().fine("adaptation: new gen: " + associatedRevolNode.getC()[0] + 
					" " + associatedRevolNode.getC()[1] +
					" " + associatedRevolNode.getC()[2]);
			
			//associatedNode.dropExceedingNeighbors();
			associatedRevolNode.dropExceedingResourceAdvs();
	    }		
	}

}
