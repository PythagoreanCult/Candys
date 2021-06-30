package candys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

public class GeneticSearch {
	
				/////////////////////////////////// Fields & Parameters ///////////////////////////////////////////
	
	private int nofnodes;
	private int fitnessRep;
	private int genSize;
	private int type;
	private int generations = 0;
	
	private double probCrossover;
	private double probMutation;
	private double bestFitness = 0;
	
	private boolean optimalyCoded = true;
	private boolean connectedFlag = false;

	private SetBuilder data;
	private CANetwork bestNetwork;
	
	private Random r = new Random();
	private ArrayList<CANetwork> generation = new ArrayList<CANetwork>();

	
	/////////////////////////////////////////////// Constructor Methods ////////////////////////////////////////////////////
	
	public GeneticSearch(SetBuilder dataset, int nnodes, boolean isOptimalyCoded, int halfGenerationSize, double pCrossover,int ntails, int typeNet){
		
		type = typeNet;
		if(!(type == 1 || type == 2 || type == 3 || type == 4)) {
			System.out.print("Type of network has to be 1, 2, 3 or 4: 1 = ER, 2 = BA, 3 = WS, 4 = random mixture.");
			System.exit(1);
		}
		
		genSize = halfGenerationSize * 2 + 2;
		nofnodes = nnodes;
		data = dataset;
		probCrossover = pCrossover;
		fitnessRep = ntails;
		optimalyCoded = isOptimalyCoded;
		probMutation = 1/(double)Math.pow(nofnodes, 2);
		
		CANetwork can;
		double randDensity;
		int randType;
		for(int i = 0; i < genSize; i++) {
			randDensity = Math.random() * 0.3;
			if(type == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
			else if(type == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
			else if(type == 3) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
			else {
				randType = r.nextInt(3) + 1;
				if(randType == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
				else if(randType == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
				else can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
			}
			generation.add(can);
		}
		
	}
	
	
	public GeneticSearch(SetBuilder dataset, CANetwork seed1, int halfGenerationSize, double pCrossover,int ntails, int typeNet){
		
		type = typeNet;
		if(!(type == 1 || type == 2 || type == 3 || type == 4)) {
			System.out.print("Type of network has to be 1, 2, 3 or 4: 1 = ER, 2 = BA, 3 = WS, 4 = random mixture.");
			System.exit(1);
		}
		
		bestNetwork = seed1;
		genSize = halfGenerationSize * 2 + 2;
		generation.add(bestNetwork);
		nofnodes = (int) seed1.adjacency.getSize(0);
		data = dataset;
		probCrossover = pCrossover;
		int driverLength = seed1.drivenIndexes.length;
		if (driverLength == 21) optimalyCoded = false;
		probMutation = 1/(double)Math.pow(nofnodes, 2);
		
		CANetwork can;
		double randDensity;
		int randType;
		for(int i = 1; i < genSize; i++) {
			randDensity = Math.random() * 0.3;
			if(type == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
			else if(type == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
			else if(type == 3) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
			else {
				randType = r.nextInt(3) + 1;
				if(randType == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
				else if(randType == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
				else can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
			}
			generation.add(can);
		}
	}
	
	/////////////////////////////////////////////// GA methods /////////////////////////////////////////////
	
	public void newGeneration() {
		
		ArrayList<CANetwork> newGeneration = new ArrayList<CANetwork>();
		double[] fitnesses = new double[genSize];
		double bestGenerationFitness = 0;
		int bestIndex = 0;
		for(int i = 0; i < genSize; i++) {
			fitnesses[i] = fitness(i);
			if (bestGenerationFitness < fitnesses[i]) {
				bestGenerationFitness = fitnesses[i];
				bestIndex = i;
			}
		}
		if(bestGenerationFitness > bestFitness) {
			bestFitness = bestGenerationFitness;
			bestNetwork = generation.get(bestIndex); 
		}
		newGeneration.add(bestNetwork);
		
		CANetwork can;
		double randDensity;
		int randType;
		
		randDensity = Math.random() * 0.3;
		if(type == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
		else if(type == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
		else if(type == 3) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
		else {
			randType = r.nextInt(3) + 1;
			if(randType == 1) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5);
			else if(randType == 2) can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, true);
			else can = new CANetwork(nofnodes, randDensity, optimalyCoded, 0.5, 0.2);
		}
		newGeneration.add(can);
		
		
		int[] selected = new int[2];
		CANetwork parent1,parent2;
		int randInt, crossover1,crossover2;
		double randDouble;
		int driverLength = bestNetwork.drivenIndexes.length;
		for(int i = 0; i < (genSize - 2)/2; i++) {
			selected = rouletteSelection(fitnesses);
			parent1 = generation.get(selected[0]);
			parent2 = generation.get(selected[1]);
			
			
			int counter = 0;
			Matrix newAdjacency1 = DenseMatrix.Factory.zeros(nofnodes, nofnodes);
			Matrix newAdjacency2 = DenseMatrix.Factory.zeros(nofnodes, nofnodes);
			
						///////////////////////////////// Crossover /////////////////////////////////////
			randDouble = Math.random();
			if(randDouble < probCrossover) {
				while(!connectedFlag) {
					connectedFlag = true;
					crossover1 = r.nextInt(nofnodes - 1);
					crossover2 = r.nextInt(nofnodes) + 1;
					while(crossover1 == crossover2) crossover2 = r.nextInt(nofnodes - 1) + 1;
					if(crossover1 > crossover2) {
						int temp = crossover1;
						crossover1 = crossover2;
						crossover2 = temp;
					}
					newAdjacency1 = parent1.adjacency.clone();
					newAdjacency2 = parent2.adjacency.clone();
					
					for(int k = 0; k < nofnodes; k++) {
						if(k > crossover1 && k < crossover2) {
							for(int j = 0; j < nofnodes; j++) {
								newAdjacency1.setAsDouble(parent2.adjacency.getAsDouble(j, k), j, k);
								newAdjacency2.setAsDouble(parent1.adjacency.getAsDouble(j, k), j, k);
							}
						}
					}
						///////////////////////////////// Mutation //////////////////////////////////////
					
					for(int k = 0; k < nofnodes; k++) {
						for(int j = 0; j < nofnodes; j++) {
							randDouble = Math.random();
							if(randDouble < probMutation) {
								if(newAdjacency1.getAsDouble(j, k) == 0) newAdjacency1.setAsDouble(1, j, k);
								else newAdjacency1.setAsDouble(0, j, k);
							}
							randDouble = Math.random();
							if(randDouble < probMutation) {
								if(newAdjacency2.getAsDouble(j, k) == 0) newAdjacency2.setAsDouble(1, j, k);
								else newAdjacency2.setAsDouble(0, j, k);
							}
						}
					}
						//////////////////////////////////////Connection check/////////////////////////
					Matrix exponentMat1 = newAdjacency1.transpose().plus(newAdjacency1);
					Matrix pathMatrix1 = exponentMat1.clone();
					for(int j = 0; j < (nofnodes - 1); j++) {
						exponentMat1 = exponentMat1.mtimes(newAdjacency1);
						pathMatrix1 = pathMatrix1.plus(exponentMat1);
					}
					
					Matrix exponentMat2 = newAdjacency2.transpose().plus(newAdjacency2);
					Matrix pathMatrix2 = exponentMat2.clone();
					for(int j = 0; j < (nofnodes - 1); j++) {
						exponentMat2 = exponentMat2.mtimes(newAdjacency2);
						pathMatrix2 = pathMatrix2.plus(exponentMat2);
					}
					for(int j = 0; j < (nofnodes - 1); j++) {
						for(int k = 0; k < (nofnodes - 1); k++) {
							if(pathMatrix1.getAsDouble(j, k) == 0) connectedFlag = connectedFlag && false;
							else connectedFlag = connectedFlag && true;
							if(pathMatrix2.getAsDouble(j, k) == 0) connectedFlag = connectedFlag && false;
							else connectedFlag = connectedFlag && true;
						}
					}
					
					counter++;
					if(counter == 1000) {
						System.out.println("Stuck in parent matrixes that produce only non connected children. If this appears often, restart run.");
						newGeneration.add(parent1);
						newGeneration.add(parent2);
						break;
					}
				}

				int[] bothDriven = Arrays.copyOf(parent1.drivenIndexes, parent1.drivenIndexes.length + parent2.drivenIndexes.length);
				System.arraycopy(parent2.drivenIndexes, 0, bothDriven, parent1.drivenIndexes.length, parent2.drivenIndexes.length);
				Arrays.sort(bothDriven);			
				for(int j = 0; j < (bothDriven.length - 1); j++) {
					if(bothDriven[j] == bothDriven[j + 1]) System.arraycopy(bothDriven, j + 1, bothDriven, j, bothDriven.length - 1 - j);
				}
				int repIndex = bothDriven.length;
				for(int j = 0; j < bothDriven.length; j++) {
					if(bothDriven[j] == bothDriven[j + 1]) {
						repIndex = j + 1;
						break;
					}
				}
				bothDriven = Arrays.copyOfRange(bothDriven, 0, repIndex);
				int[] drivenIndexes1 = Arrays.copyOf(bothDriven, repIndex);
				int[] drivenIndexes2 = Arrays.copyOf(bothDriven, repIndex);
				while(drivenIndexes1.length > driverLength) {
					randInt = r.nextInt(drivenIndexes1.length);
					System.arraycopy(drivenIndexes1, randInt + 1, drivenIndexes1, randInt, drivenIndexes1.length - 1 - randInt);
					drivenIndexes1 = Arrays.copyOfRange(drivenIndexes1, 0, drivenIndexes1.length - 1);
					randInt = r.nextInt(drivenIndexes2.length);
					System.arraycopy(drivenIndexes2, randInt + 1, drivenIndexes2, randInt, drivenIndexes2.length - 1 - randInt);
					drivenIndexes2 = Arrays.copyOfRange(drivenIndexes2, 0, drivenIndexes2.length - 1);
				}
				
				CANetwork child1 = new CANetwork(newAdjacency1, drivenIndexes1);
				CANetwork child2 = new CANetwork(newAdjacency2, drivenIndexes2);
				newGeneration.add(child1);
				newGeneration.add(child2);
				
			} else {
				newAdjacency1 = parent1.adjacency.clone();
				newAdjacency2 = parent2.adjacency.clone();
				
				while(!connectedFlag){
					connectedFlag = true;
					for(int k = 0; k < nofnodes; k++) {
						for(int j = 0; j < nofnodes; j++) {
							randDouble = Math.random();
							if(randDouble < probMutation) {
								if(newAdjacency1.getAsDouble(j, k) == 0) newAdjacency1.setAsDouble(1, j, k);
								else newAdjacency1.setAsDouble(0, j, k);
							}
							randDouble = Math.random();
							if(randDouble < probMutation) {
								if(newAdjacency2.getAsDouble(j, k) == 0) newAdjacency2.setAsDouble(1, j, k);
								else newAdjacency2.setAsDouble(0, j, k);
							}
						}
					}
					
					Matrix exponentMat1 = newAdjacency1.transpose().plus(newAdjacency1);
					Matrix pathMatrix1 = exponentMat1.clone();
					for(int j = 0; j < (nofnodes - 1); j++) {
						exponentMat1 = exponentMat1.mtimes(newAdjacency1);
						pathMatrix1 = pathMatrix1.plus(exponentMat1);
					}
					
					Matrix exponentMat2 = newAdjacency2.transpose().plus(newAdjacency2);
					Matrix pathMatrix2 = exponentMat2.clone();
					for(int j = 0; j < (nofnodes - 1); j++) {
						exponentMat2 = exponentMat2.mtimes(newAdjacency2);
						pathMatrix2 = pathMatrix2.plus(exponentMat2);
					}
					for(int j = 0; j < (nofnodes - 1); j++) {
						for(int k = 0; k < (nofnodes - 1); k++) {
							if(pathMatrix1.getAsDouble(j, k) == 0) connectedFlag = connectedFlag && false;
							else connectedFlag = connectedFlag && true;
							if(pathMatrix2.getAsDouble(j, k) == 0) connectedFlag = connectedFlag && false;
							else connectedFlag = connectedFlag && true;
						}
					}
				}
				
				CANetwork child1 = new CANetwork(newAdjacency1, parent1.drivenIndexes);
				CANetwork child2 = new CANetwork(newAdjacency2, parent2.drivenIndexes);
				newGeneration.add(child1);
				newGeneration.add(child2);
				
			}					
		} 
		generation = newGeneration;
		generations++;
	}
	
	private double fitness(int index) {
		CANetwork can = generation.get(index);
		double fitness = 1;
		for(int i = 0; i < this.fitnessRep; i++) {
			data.changeSets();
			can.train(data, false);
			double[] result = can.validate(data, false);
			fitness = fitness * (result[0] + 1);
		}
		return fitness;
	}
	
	int[] rouletteSelection(double[] fitnesses) {
		double totalFitness = 0;
		double[] cumulativeFitness = new double[genSize];
		for(int i = 0; i < genSize; i++) {
			totalFitness = totalFitness + fitnesses[i];
			cumulativeFitness[i] = totalFitness;
		} 
		double randSelector = Math.random();
		int[] selected = new int[2];
		for(int i = 0; i < genSize; i++) {
			if(randSelector < cumulativeFitness[i]/totalFitness) {
				selected[0] = i;
				break;
			}
		}
		boolean notFinished = true;
		while(notFinished) {
			randSelector = Math.random();
			for(int i = 0; i < genSize; i++) {
				if(randSelector < cumulativeFitness[i]/totalFitness) {
					if(selected[0] == i) break;
					else {
						selected[1] = i;
						notFinished = false;
					}
				}
			}
		}
		return selected;
	}
	
	////////////////////////////////////////////// Getters & Setters ////////////////////////////////////////////

	public int getGenerationNumber() {
		return generations;
	}
	
	public CANetwork getBestNetwork() {
		return bestNetwork;
	}
	
	public double getBestFitness() {
		return bestFitness;
	}

}
