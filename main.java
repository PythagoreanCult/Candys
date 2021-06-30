package candys;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.ujmp.core.*;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;
import org.ujmp.core.doublematrix.calculation.general.decomposition.DecompositionDoubleCalculations;

import candys.CANetwork;
import candys.Mlink;
import candys.Vnode;

public class main {
	private CANetwork can1,can2;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		
		//simulation1();
		//histogramData();
		//memoryTrain();
		//tryAverageNetwork();
		//simulation5();
		//tryNetwork();
		//accuracyLadder();
		//generaliseLadder();
		//parameterSearch();
		//geneticSearch();
		ensembleLearning();
		//images();
		
	}
	
	

	static void simulation1() throws IOException {
		
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String easyData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder data = new SetBuilder(positiveData, easyData, 5, 100, 25);
		
		
		for(int k = 0; k < 100; k++) {
			data.changeSets();
			int useless = 0;
			String fileName1 = "PeriodAnalysisPresentation" + k; 
			String path1 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\PeriodAnalysis\\" + fileName1;
			File file1 = new File(path1);
			FileWriter csvWriter1 = new FileWriter(file1);
			for(int i = 0; i < 31; i++) {
				CANetwork can2  = new CANetwork(21 + i, 3/(double)(21),false, 0.5);
				double[] result = can2.drivenRelaxationTime(data,5);
				can2.train(data, false);
				//double[] result2 = can2.validate(data, false);
				//System.out.println("Is network unresponsive for : " + can2.unresponsive/200 + "     Is the correlation matrix singular " + can2.singularTraining );
				//System.out.println("The accuracy is " + result2[0] + "	The specificity is " + result2[1] + "	The sensitivity is " + result2[2]);
				
				for(int j = 0; j < 202; j++) {
					if(j == 201) csvWriter1.append(Double.toString(result[j]));
					else csvWriter1.append(Double.toString(result[j]) + ", ");
				}
				csvWriter1.append("\n");
			
			}
			csvWriter1.flush();
			csvWriter1.close();
			
			
		}
		
		
		
		CANetwork can2  = new CANetwork(21, 0.1, false,0.5 , true);
		can2.hubInput();
		IDrive d = new Protein("EACMYGILPDFNURV", 5);
		can2.visualizeRelaxationTime("coverImage1", 75, d);
		/*
		//CANetwork can = new CANetwork(20, 0.15,true,0.5, true);
		//can.visualizeRelaxationTime("undriven", 10);*/
		//can.hubInput();
		//can.visualizeRelaxationTime("BAhubTrial2", 50, d);*/
		
	}

	static void histogramData() throws IOException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String easyData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";

		SetBuilder dataSet = new SetBuilder(positiveData,easyData,5,100,25);
		double[] result;
		int t = 0;
		String path = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\";
		String fileName = "HistogramEnsembleFN21P015";
		File file = new File(path + fileName);
		FileWriter writer = new FileWriter(file);
		while(t < 100000) {
			CANetwork can = new CANetwork(21, 0.15, false, 0.5);
			//can.hubInput();
			dataSet.changeSets();
			can.train(dataSet, false);
			result = can.evaluateEnsemble(dataSet, 15, 21, 0.15); /////////////////////Ensemble
			//result = can.validate(dataSet, false);////////////////////////////////////No ensemble
			writer.append(Double.toString(result[0]) + ", " + Double.toString(result[1]) + ", "
					+ Double.toString(result[2]));
			writer.append("\n");
			t++;
			System.out.println(t);
		}
		writer.flush();
		writer.close();
	}
	
	
	static void tryAverageNetwork() throws FileNotFoundException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String easyData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder data = new SetBuilder(positiveData, easyData, 5, 100, 25);
		double accuracy = 0;
		int average = 200;
		data.changeSets();
		for(int i = 0; i < average; i++) {
			//data.changeSets();
			CANetwork can = new CANetwork(22,0.922,false, 0.5);
			//can.hubInput();
			can.train(data, false);
			double[] result = can.validate(data, false);
			accuracy = accuracy + result[0];
		}
			accuracy = accuracy/average;
			System.out.println(accuracy);
			/*System.out.println(result[1]);
			System.out.println(result[2]);
			double error = can.trainErrors(trialMax, false);
			System.out.println(error);*/
		
	}
	
	static void memoryTrain() {
		/*int rep = 5;
		int memoryLength = 5;
		int size = 100;
		CANetwork can = new CANetwork(50,0.08,true,0.5);
		can.memoryTrain(rep, memoryLength, 25, size);
		double accuracy = can.memoryValidate(rep, memoryLength, 25, size);
		System.out.println(accuracy);*/
		
	}
	
	
	static void simulation5() throws IOException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String easyData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder data = new SetBuilder(positiveData, easyData,"Easy",25);
	}



	static void tryNetwork() throws FileNotFoundException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String negativeData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		int sizeData = 100;
		int repetition = 10;
		double outputs = 0.5;
		int nodes = 8;
		double connectivity = 0.28;
		SetBuilder trialSet = new SetBuilder(positiveData, negativeData, repetition, sizeData, 25);
		CANetwork can = new CANetwork(nodes,connectivity,true, outputs,0.1);
		can.networkSummary();
		can.train(trialSet, false);
		double[] trainingResult = can.accuracyTraining(trialSet, false);
		double[] result = can.validate(trialSet, false);
		System.out.println("Training: \n The accuracy is " + trainingResult[0]);
		System.out.println("The specificity is " + trainingResult[1]);
		System.out.println("The sensitivity is " + trainingResult[2]);
		System.out.println("Validation: \n The accuracy is " + result[0]);
		System.out.println("The specificity is " + result[1]);
		System.out.println("The sensitivity is " + result[2]);
		/*for(int i = 0; i < (2*sizeData); i++) {
			System.out.println(can.trainingErrors[i]);
		}
		System.out.print("Total error is " + totalError);
		*/
	}
	
	static void accuracyLadder() throws IOException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String negativeData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder data = new SetBuilder(positiveData,negativeData,5, 100, 25);
		
		double maxAcc = 0;
		int times = 5;
		int t = 0;
		int counter = 0;
		while(maxAcc < 1) {
			CANetwork can = new CANetwork(50,0.035,false, 0.5);
			double accuracy = 0;
			double error = 0;
			for(int i = 0; i < times; i++) {
				data.changeSets();
				can.train(data, false);
				error = error + can.trainErrors(data, false);
				double[] result = can.validate(data, false);
				accuracy = accuracy + result[0];
			}
			counter++;
			//System.out.println(counter);
			accuracy = accuracy/times;
			error = error/times;
			if (accuracy > maxAcc) {
				maxAcc = accuracy;
				t++;
				System.out.println("Trial number " + counter);
				System.out.println("The accuracy of SecondAccuracyLadder" + t + " is " + maxAcc + 
						" and the error is " + error);
				can.saveData("SecondAccuracyLadder" + t);
			}
		
		}
		
		

	}
	
	static void generaliseLadder() throws NumberFormatException, IOException {
		// read and simulate
		
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String negativeData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder dataSet = new SetBuilder(positiveData, negativeData, 5, 100, 25);
		
		for(int k = 1; k < 15; k++) {
			
			String path1 = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\Saved Networks\\GeneticLadder"
					+ k + "Adjacency.txt";
			String row;
			File file = new File(path1);
			BufferedReader csvReader = new BufferedReader(new FileReader(file));
			int counter = 0;
			Matrix newAdjacency = SparseMatrix2D.Factory.zeros(50,50);
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(", ");
			    int size = data.length;
			    for (int j = 0; j < size; j++) {
			    	newAdjacency.setAsDouble(Double.parseDouble(data[j]),counter, j);
			    }
			    counter++;
			}
			csvReader.close();
			//System.out.println(newAdjacency);
			
			String path2 = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\Saved Networks\\GeneticLadder"
					+ k + "Drivers.txt";
			String row2;
			File file2 = new File(path2);
			BufferedReader csvReader2 = new BufferedReader(new FileReader(file2));
			row2 = csvReader2.readLine();
		    String[] data2 = row2.split(", ");
			int size2 = data2.length;
			int[] newDrivers = new int[size2];
		    for (int j = 0; j < size2; j++) {
		    	newDrivers[j] = Integer.valueOf(data2[j]);
		    }
			csvReader2.close();
			//System.out.println(newDrivers[0]);
			
			CANetwork can = new CANetwork(newAdjacency, newDrivers);
			String fileName = "GL" + k + "DatasetTest.txt";
			String path3 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName;
			File file3 = new File(path3);
			FileWriter csvWriter = new FileWriter(file3);
			for(int i = 0; i < 50 ; i++) {
				double[] result = new double[3];
				dataSet.changeSets();
				can.train(dataSet, false);
				result = can.validate(dataSet, false);
				csvWriter.append(Double.toString(result[0]) + ", " + Double.toString(result[1]) 
				+ ", " + Double.toString(result[2]));
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		}
	}
	
	static void parameterSearch() throws IOException {
		
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String negativeData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder dataset = new SetBuilder(positiveData,negativeData,5, 100, 25);
		
		int[] nodes = {/*5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,*/ 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
				 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};/*, 52, 53 ,54, 55, 56,
				 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
				 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};*/
		int length = nodes.length;
		int partition = 40; ///// CHECK PARTITION
		int average = 20;
		String fileName1 = "FitnessFParameterSearchAverage";            ////////////////CHANGE BOTH NAMES
		String path1 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName1;
		File file1 = new File(path1);
		FileWriter csvWriter1 = new FileWriter(file1);
		String fileName2 = "FitnessFParameterSearchMax";                ////////////////THIS ONE TOO
		String path2 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName2;
		File file2 = new File(path2);
		FileWriter csvWriter2 = new FileWriter(file2);
		int counter = 0;
		
		for(int i = 0; i < length; i++) {
			int nnodes = nodes[i];
			for (int j = 0; j < partition; j++) {
				double connectivity = 1/(double)nnodes + j * (0.3 - 1/(double)nnodes)/(partition - 1);////  CHECK LIMITS
				double accuracy = 1;
				double max = 0;
				for(int k = 0; k < average; k++) {
					CANetwork can = new CANetwork(nnodes, connectivity, false, 0.5);////////// CHECK TYPE OF NETWORK
					dataset.changeSets();
					//can.hubInput();   //////////// HUBS OR NOT HUBS
					can.train(dataset, false);
					double[] result = can.validate(dataset, false);
					if(result[0] > max) max = result[0];
					accuracy = accuracy * (1 + result[0]);
					counter++;
					System.out.println(counter/(double)(length*partition*average)*100 + "%");
				}
				accuracy = accuracy;///average;
				csvWriter1.append(Double.toString(accuracy));
				csvWriter2.append(Double.toString(max));
				if(!(j == (partition - 1))) {
					csvWriter1.append(", ");
					csvWriter2.append(", ");
				}
			}
			if(!(i == (length - 1))) {
				csvWriter1.append("\n");
				csvWriter2.append("\n");
			}
			
		}
		csvWriter1.flush();
		csvWriter1.close();
		csvWriter2.flush();
		csvWriter2.close();
		
		
	}
	
	static void geneticSearch() throws IOException{
		
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String negativeData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";
		SetBuilder dataset = new SetBuilder(positiveData,negativeData,5, 100, 25);
		
		
		
		GeneticSearch ga = new GeneticSearch(dataset, 35, false, 5, 0.65, 10, 1);
	    CANetwork can3;
	    double oldBestFitness = 0;
	    int k = 0;
    	while(k < 10) {
	    	ga.newGeneration();
	    	if (ga.getBestFitness() > oldBestFitness) {
	    		oldBestFitness = ga.getBestFitness();
	    		k++;
	    		ga.getBestNetwork().saveData("Best2GeneticLadder" + k);
	    		System.out.println("The best fitness in generation " + ga.getGenerationNumber() + " is " + ga.getBestFitness());
    		}
    	}
		
	}
	
	
	static void ensembleLearning() throws IOException {
		String positiveData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_pos.fa";
		String easyData = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\dataset_easy.fa";

		
		SetBuilder dataSet = new SetBuilder(positiveData, easyData, 5, 100, 25);
		dataSet.changeSets();
		double[] result;
		int t = 0;
		int ensembleSize = 15;
		CANetwork can = new CANetwork(21, 0.15, false, 0.5);
		result = can.evaluateEnsemble(dataSet,ensembleSize,21, 0.15);
		System.out.println("Validation: \n The accuracy is " + result[0]);
		System.out.println("The specificity is " + result[1]);
		System.out.println("The sensitivity is " + result[2]);
		
		
	}
	
	static void images() {
		CANetwork can = new CANetwork(50,0.2,false,1, 0.2);
		can.adjacency.showGUI();
	}
	
}
