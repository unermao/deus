package it.unipr.ce.dsg.deus.util;

import java.util.Random;


/**
 * A collection of functions that return numbers with different statistical distributions
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class Distributions {
	
	// DISCRETE R.V.
	
	// returns true with probabilty p, false with probability (1-p)
	public static boolean bernoulli(Random random, double p) {
		return random.nextDouble() < p;
	}
	
	// returns an integer according to the geometric distribution
	public static int geometric(Random random, double p) {
		return (int) Math.ceil(Math.log(random.nextDouble()) / Math.log(1.0 - p));
	}
	
	// returns an integer chosen according to the Poisson distribution with average value alpha
	public static int poisson(Random random, double alpha) {
		int k = 0;
		double p = 1.0;
		double L = Math.exp(-alpha);
		do {
			k++;
			p *= random.nextDouble();
		} while (p >= L);
		return k-1;
	}
	
	
	// CONTINUOUS R.V.
	
	// returns a double in [a,b], chosen according to the uniform distribution
	public static double uniform(Random random, double a, double b) {
		return a + random.nextDouble() * (b-a);
	} 
	
	// returns a double, chosen according to the normal distribution
	public static double gaussian(Random random, double mean, double stddev) {
		double r, x, y;
		do {
			x = uniform(random, -1.0, 1.0);
			y = uniform(random, -1.0,1-0);
			r = x*x + y*y;
		} while (r >= 1 || r == 0);
		return mean + stddev * (x * Math.sqrt(-2 * Math.log(r) / r)); 
	}
	
	// returns a double, chosen according to the exponential distribution with arrival rate lambda
	public static double exp(Random random, double lambda) {
		return -Math.log(1 - random.nextDouble()) / lambda;
	}
	
	// returns a double, chosen according to the Pareto distribution with Pareto index k
	public static double pareto(Random random, double k) {
		return Math.pow(1 - random.nextDouble(), -1.0/k) - 1.0;
	}
}
