package candys;

import java.util.ArrayList;

import Jama.Matrix;

public class TestWAlg {

private ArrayList<Integer> labels = new ArrayList<Integer>();
private ArrayList<Double[][]> states = new ArrayList<Double[][]>();
private double[][] bVectorCheck;
private double[][] corrMatrixCheck;

public void appendState(Double[][] state,int label){
	states.add(state);
	labels.add(label);
}


public void checkWeights(Matrix weights) {
	int totalSize = labels.size();
	int nofWeights = states.get(0).length; 
	if(nofWeights == 1) {
		System.out.println("wrong array of arrays in line 22 of testwalg");
	}
	bVectorCheck = new double[nofWeights][1];
	corrMatrixCheck = new double[nofWeights][nofWeights];
	
	for(int k = 0; k < totalSize; k++) {
		Double[][] kthState = states.get(k);
		for(int i = 0; i < nofWeights; i++) {
			for(int j = 0; j < nofWeights; j++) {
				corrMatrixCheck[i][j] = corrMatrixCheck[i][j] + kthState[i][0] * kthState[j][0];
			}
			bVectorCheck[i][0] = bVectorCheck[i][0] + kthState[i][0] * labels.get(k);
		}
	}
	Matrix correlationsCheck = new Matrix(corrMatrixCheck);
	Matrix bVectorCheck2 = new Matrix(bVectorCheck);
	//bVectorCheck2.timesEquals(-nofWeights);
	Matrix invcorr = correlationsCheck.inverse();
	Matrix weightscheck = new Matrix(nofWeights, 1);
    weightscheck = invcorr.times(bVectorCheck2);
    System.out.println(weightscheck.minus(weights).norm2());
}

public void printWeights() {
	
}
}
