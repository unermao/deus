package it.unipr.ce.dsg.deus.example.revol;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

public class RevolAdaptationEvent extends NodeEvent {
	private static final String PHI_0 = "phi0";
	private static final String PHI_1 = "phi1";
	private static final String PHI_2 = "phi2";
	private static final String SELECTION_STRATEGY = "selectionStrategy";

	private String fitnessFunction = null;
	private double currentFitness = 0;
	private int phi0 = 0;
	private int phi1 = 0;
	private int phi2 = 0;
	private String selectionStrategy = null;
	private double delta = 0.001;

	public RevolAdaptationEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);	
		if (params.containsKey(PHI_0))
			phi0 = Integer.parseInt(params.getProperty(PHI_0));
		if (params.containsKey(PHI_1))
			phi1 = Integer.parseInt(params.getProperty(PHI_1));
		if (params.containsKey(PHI_2))
			phi2 = Integer.parseInt(params.getProperty(PHI_2));
		if (params.containsKey(SELECTION_STRATEGY))
			selectionStrategy = params.getProperty(SELECTION_STRATEGY);
	}

	public Object clone() {
		RevolAdaptationEvent clone = (RevolAdaptationEvent) super.clone();
		return clone;
	}

	private double computeFitness(RevolPeer node) {
		double A = phi0 * node.getFk() + phi1 * node.getTtlMax() + phi2 * node.getDMax();
		getLogger().fine("A = " + A + " 1/A = " + 1/A);
		double qhr = node.getAvgNeighborsQhr();
		getLogger().fine(node + " avg neighbor qhr = " + qhr);
		fitnessFunction = ((RevolPeer) ((Peer) associatedNode)).getFitnessFunction();

		// F1
		if (fitnessFunction.equals("F1.0")) {
			if (qhr < 0.99) 	  
				return 1 / A;
			else
				return A;
		}
		else if (fitnessFunction.equals("F2.0")) {
			return (1-qhr)/(delta*delta*A) + qhr*A;	
		}
		else if (fitnessFunction.equals("F3.0")) {
			return ((1/(delta*delta)) * (1/(qhr + delta) - 1) / A + A * qhr);
		}
		
		return -1; // error
	}

	private double computeFitness(int[] c, double qhr) {
		double A = phi0 * ((double) c[0]) / 6 + phi1 * c[1] + phi2 * c[2] * 2;
		getLogger().fine("A = " + A + " 1/A = " + 1/A);
		getLogger().fine("avg neighbor qhr = " + qhr);
		
		// F1
		
		if (qhr < 0.99) 	  
			return 1 / A;
		else
			return A;
		
		// F2 return (1-qhr)/(delta*delta*A) + qhr*A;
		// F3 
		//return ((1/(delta*delta)) * (1/(qhr + delta) - 1) / A + A * qhr);  
	}

	private RevolPeer selectBestNeighbor() {
		RevolPeer bestNeighbor = null;

		if (selectionStrategy.equals("bestFitness")) {
			RevolPeer currentNeighbor = null;
			double currentNeighborFitness = 0;
			double bestNeighborFitness = 0;
			for (int i = 0; i < ((Peer) associatedNode).getNeighbors().size(); i++) {
				currentNeighbor = (RevolPeer) ((Peer) associatedNode)
						.getNeighbors().get(i);
				currentNeighborFitness = computeFitness(currentNeighbor);
				getLogger().fine("currentNeighborFitness: " + currentNeighborFitness);
				if (i == 0) {
					bestNeighbor = currentNeighbor;
					bestNeighborFitness = currentNeighborFitness;
				} else {
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
			double fitnesses[] = new double[size];
			RevolPeer currentNeighbor = null;
			for (int j = 0; j < size; j++) {
				currentNeighbor = (RevolPeer) ((Peer) associatedNode)
						.getNeighbors().get(j);
				fitnesses[j] = computeFitness(currentNeighbor);
			}
			int pos = getRandomElementWithInverseProbability(fitnesses);
			bestNeighbor = (RevolPeer) ((Peer) associatedNode)
						.getNeighbors().get(pos);
			getLogger().fine("best neighbor = " + pos);
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
		getLogger().fine("numElements " + numElements);
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
			inverseValuesCDF[i] = inverseValuesCDF[i - 1] + inverseValues[i]
					/ sumInverseValues;
			getLogger().fine(i + " " + inverseValuesCDF[i]);
		}
		double randomDouble = Engine.getDefault().getSimulationRandom()
				.nextDouble();
		int i = 0;
		if (randomDouble > inverseValuesCDF[0]) {
			do {
				i++;
			} while (randomDouble > inverseValuesCDF[i]);
		}
		getLogger().fine(
				"random = " + randomDouble + ", thus selected element is " + i);
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
					double epsilon = Engine.getDefault().getSimulationRandom()
							.nextDouble();
					if (epsilon <= pm) {
						getLogger().fine("mutation! " + i + " " + j);
						mutatedOffspring[i][j] = Engine.getDefault()
								.getSimulationRandom().nextInt(6) + 1;
					} else
						mutatedOffspring[i][j] = offspring[i][j];
				}
			}
			return mutatedOffspring;
		}
	}

	public void run() throws RunException {
		RevolPeer associatedRevolNode = (RevolPeer) associatedNode;

		if (associatedRevolNode.getNeighbors().size() == 0)
			return;
		
		getLogger().fine("### adaptation! for node " + associatedNode.getId());
		getLogger().fine(
				"initial genotype: " + associatedRevolNode.getC()[0] + " "
						+ associatedRevolNode.getC()[1] + " "
						+ associatedRevolNode.getC()[2]);

		// evaluate the fitness of current chromosome
		double avgNeighborsQhr = associatedRevolNode.getAvgNeighborsQhr();
		currentFitness = computeFitness(associatedRevolNode.getC(), avgNeighborsQhr);
		
		getLogger().fine("currentFitness = " + currentFitness);

		getLogger().fine("+++ select best neighbor");
		RevolPeer bestNeighbor = selectBestNeighbor();
		if (bestNeighbor.getKey() == associatedNode.getKey())
			return;

		getLogger()
				.fine(
						"best neighbor config: " + bestNeighbor.getC()[0] + " "
								+ bestNeighbor.getC()[1] + " "
								+ bestNeighbor.getC()[2]);

		int g = associatedRevolNode.getG();
		associatedRevolNode.setG(g + 1);
		getLogger().fine("Generation: " + associatedRevolNode.getG());

		getLogger().fine(
				"starting genotype: " + associatedRevolNode.getC()[0] + " "
						+ associatedRevolNode.getC()[1] + " "
						+ associatedRevolNode.getC()[2]);

		getLogger().fine("+++ cross-over between the best neighbor config and local");
		int[][] offspring = crossover(associatedRevolNode.getC(), bestNeighbor
				.getC());

		getLogger().fine("+++ random mutation of the offspring");
		int[][] mutatedOffspring = mutation(offspring, 1 - avgNeighborsQhr);

		getLogger()
				.fine("+++ compare current chromosome with offspring");
		double[] fitnesses = new double[3];
		fitnesses[0] = currentFitness;
		fitnesses[1] = computeFitness(mutatedOffspring[0], avgNeighborsQhr);
		getLogger().fine("fitness of mutated offspring 1: " + fitnesses[1]);
		fitnesses[2] = computeFitness(mutatedOffspring[1], avgNeighborsQhr);
		getLogger().fine("fitness of mutated offspring 2: " + fitnesses[2]);

		int s = getRandomElementWithInverseProbability(fitnesses);
		if (s > 0)
			associatedRevolNode.setC(mutatedOffspring[s - 1]);

		getLogger().fine(
				"new genotype: " + associatedRevolNode.getC()[0] + " "
						+ associatedRevolNode.getC()[1] + " "
						+ associatedRevolNode.getC()[2]);

		associatedRevolNode.dropExceedingResourceAdvs();
	}

}
