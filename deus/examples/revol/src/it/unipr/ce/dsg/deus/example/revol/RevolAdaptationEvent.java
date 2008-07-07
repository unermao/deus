package it.unipr.ce.dsg.deus.example.revol;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

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
	//private double delta = 0.001;

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

	private double computeFitness(RevolPeer node) {
		double A = a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax();
		double qhr = node.getAvgNeighborsQhr();
		
		if ( qhr < 0.999 )
			return 1/A;
		else 
			return (1 - qhr)*A;
		
		//return (1 - qhr);
		//return 1 / A;
		//return (1 - qhr + delta) / A;
		//return (qhr + delta) / A;
		//return (1 - qhr) / A;  // bad! may result in fitness = 0
		//return ((1 - qhr) / A + delta * delta * qhr * A);
		//return (qh /(q + delta)) * A;
		//return ((1/(delta*delta)) * (1/qhr - 1) / A  + A * qhr);
		//return (((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta)) + (1/delta) * ((node.getQ()) / (node.getQh() + delta)));
		//return (((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta)) + ((node.getQ()) / (node.getQh() + delta)));
		//return ((a0*node.getFk() + a1*node.getTtlMax() + a2*node.getDMax()) / (node.getQh() + delta));
	}
	
	private double computeFitness(int[] c, double qhr) {
		double A = a0*c[0]/10 + a1*c[1] + a2*c[2]*2;
		
		
		if ( qhr < 0.999 )
			return 1/A;
		else 
			return (1 - qhr)*A;
		
		//return (1 - qhr);
		//return 1 / A;
		//return (1 - qhr + delta) / A;
		//return (qhr + delta) / A;
		//return (1 - qhr) / A;  // bad! may result in fitness = 0
		//return ((1 - qhr) / A + delta * delta * qhr * A);
		//return (qh /(q + delta)) * A;
		//return ((1/(delta*delta)) * (1/qhr - 1) / A  + A * qhr);
		//return (((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta)) + (1/delta) * ((q) / (qh + delta)));
		//return (((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta)) + ((q) / (qh + delta)));
		//return ((a0*c[0]/10 + a1*c[1] + a2*c[2]*2) / (qh + delta));
	}
	
	private RevolPeer selectBestNeighbor() {  
		RevolPeer bestNeighbor = null;
		
		if (selectionStrategy.equals("bestFitness")) {	
			RevolPeer currentNeighbor = null;
			double currentNeighborFitness = 0;
			double bestNeighborFitness = 0;
			for (int i = 0; i < ((Peer) associatedNode).getNeighbors().size(); i++) {
				currentNeighbor = (RevolPeer) ((Peer) associatedNode).getNeighbors().get(i);
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
			int size = ((Peer) associatedNode).getNeighbors().size();
			double[] fitnesses = new double[size];
			for (int j = 0; j < size; j++) {
				fitnesses[j] = computeFitness((RevolPeer) ((Peer) associatedNode).getNeighbors().get(j));
				getLogger().fine("fitness of selected neighbor: " + fitnesses[j]);
			}
							
			int s = getRandomElementWithInverseProbability(fitnesses);
			bestNeighbor = (RevolPeer) ((Peer) associatedNode).getNeighbors().get(s);
		}
		else if (selectionStrategy.equals("tournament")) {
			// TODO
		}
		else if (selectionStrategy.equals("rank-based")) {
			// TODO
		}
	    return bestNeighbor;
	}
	
	private int getRandomElementWithInverseProbability(double[] values) {
		int numElements = values.length;
		getLogger().fine("numEements " + numElements);
		double sumValues = 0;
		for (int i = 0; i < numElements; i++) 
			sumValues += values[i];
		getLogger().fine("sumValues " + sumValues);
		
		double inverseValues[] = new double[numElements];
		double sumInverseValues = 0;
		for (int i = 0; i < numElements; i++) {
			inverseValues[i] = sumValues / values[i];
			sumInverseValues += inverseValues[i];
		}
		
		double inverseValuesCDF[] = new double[numElements];
		inverseValuesCDF[0] = inverseValues[0] / sumInverseValues;
		getLogger().fine("0 " + inverseValuesCDF[0]);
		for (int i = 1; i < numElements; i++) {
			inverseValuesCDF[i] = inverseValuesCDF[i-1] + inverseValues[i] / sumInverseValues;
			getLogger().fine(i + " " + inverseValuesCDF[i]);
		}
		double randomDouble = Engine.getDefault().getSimulationRandom().nextDouble();
		int i = 0;
		if (randomDouble > inverseValuesCDF[0]) {
			do {
				i++;
			} while (randomDouble > inverseValuesCDF[i]);
		}
		getLogger().fine("random = " + randomDouble + ", thus selected element is " + i);
		return i;
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
	
	private int[][] mutation(int[][] offspring, double pm) {	
		int[][] mutatedOffspring = new int[2][3];
		if (pm < 0.5)
			return offspring;
		else {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 3; j++) {
					double epsilon = Engine.getDefault().getSimulationRandom().nextDouble();
					if (epsilon <= pm) {
						getLogger().fine("mutation! " + i + " " + j);
						mutatedOffspring[i][j] = Engine.getDefault().getSimulationRandom().nextInt(10) + 1;
					}
					else
						mutatedOffspring[i][j] = offspring[i][j];
				}
			}
			return mutatedOffspring; 
		}
	}
	
	
	public void run() throws RunException {	
		RevolPeer associatedRevolNode = (RevolPeer) associatedNode;
		
		// la initial population è data dalla config locale e da quelle dei nodi vicini
		if (associatedRevolNode.getNeighbors().size() == 0)
			return;
		
		getLogger().fine("### adaptation! for node " + associatedNode.getId());
		getLogger().fine("initial genotype: " + associatedRevolNode.getC()[0] + 
				" " + associatedRevolNode.getC()[1] +
				" " + associatedRevolNode.getC()[2]);
		
		// valuta la fitness della configurazione corrente
		currentFitness = computeFitness(associatedRevolNode);
		getLogger().fine("currentFitness = " + currentFitness);
		
		// select best neighbor
		RevolPeer bestNeighbor = selectBestNeighbor();
		
		getLogger().fine("best neighbor config: " + bestNeighbor.getC()[0] + 
						 " " + bestNeighbor.getC()[1] +
						 " " + bestNeighbor.getC()[2]);
					
		int g = associatedRevolNode.getG();
		associatedRevolNode.setG(g+1);
		getLogger().fine("Generation: " + associatedRevolNode.getG());
		
		getLogger().fine("starting genotype: " + associatedRevolNode.getC()[0] + 
						 " " + associatedRevolNode.getC()[1] +
						 " " + associatedRevolNode.getC()[2]);
			
		// cross-over tra miglior config vicina e locale 
		int[][] offspring = crossover(associatedRevolNode.getC(), bestNeighbor.getC());
						
		// mutazione casuale dei due individui ottenuti
		int[][] mutatedOffspring = mutation(offspring, 1 - associatedRevolNode.getAvgNeighborsQhr()); 
			
		// cfr i due individui con la config locale vecchia			
		double[] fitnesses = new double[3];
		fitnesses[0] = currentFitness;
		fitnesses[1] = computeFitness(mutatedOffspring[0], associatedRevolNode.getAvgNeighborsQhr());
		getLogger().fine("fitness of mutated offspring 1: " + fitnesses[1]);
		fitnesses[2] = computeFitness(mutatedOffspring[1], associatedRevolNode.getAvgNeighborsQhr());
		getLogger().fine("fitness of mutated offspring 2: " + fitnesses[2]);
			
		int s = getRandomElementWithInverseProbability(fitnesses);
		if (s > 0)
			associatedRevolNode.setC(mutatedOffspring[s - 1]);
		
		getLogger().fine("new genotype: " + associatedRevolNode.getC()[0] + 
				" " + associatedRevolNode.getC()[1] +
				" " + associatedRevolNode.getC()[2]);
			
		associatedRevolNode.dropExceedingResourceAdvs();
	}

}
