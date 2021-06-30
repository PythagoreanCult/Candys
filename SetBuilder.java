package candys;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class SetBuilder {
	
	/////////////////////////////////////////// Parameters and fields ////////////////////////////////////////////////

	private ArrayList<String> trainingSetPositive = new ArrayList<String>();
	private ArrayList<String> trainingSetNegative = new ArrayList<String>();
	private ArrayList<String> validationSetPositive = new ArrayList<String>();
	private ArrayList<String> validationSetNegative = new ArrayList<String>();
	private ArrayList<String> filteredPositive = new ArrayList<String>();
	private ArrayList<String> filteredNegative = new ArrayList<String>();
	
	private int maxLength;
	private int repetition;
	private int positiveTrainingSize; 
	private int negativeTrainingSize;
	private int positiveValidationSize;
	private int negativeValidationSize;
	
	////////////////////////////////////////// Constructor methods //////////////////////////////////////////////////
	
	public SetBuilder() {
		
	}
	
	public SetBuilder(String filePositive,String fileNegative, int rep) throws FileNotFoundException {
		
		int length;
		maxLength = 0;
		this.repetition = rep;
        File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                	length = line1.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetPositive.add(line1);
                }
            }
        }
        
        File file2 = new File(fileNegative);
        if(!file2.exists()) {
        	System.out.println("The file " + fileNegative + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc2 = new Scanner(new File(fileNegative))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                	length = line2.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetNegative.add(line2);
                }
            }
        }
        
        int trainingSize = trainingSetPositive.size();
        Random r = new Random();
        int rand;
        String prot;
        for(int i = 0; i < trainingSize; i++) {
        	rand = r.nextInt(trainingSize);
        	prot = trainingSetPositive.get(rand);
        	trainingSetPositive.remove(rand);
        	validationSetPositive.add(prot);
        	trainingSize--;
        }
        positiveTrainingSize = trainingSetPositive.size();
        positiveValidationSize = validationSetPositive.size();
        
        trainingSize = trainingSetNegative.size();
        for(int i = 0; i < trainingSize; i++) {
        	rand = r.nextInt(trainingSize);
        	prot = trainingSetNegative.get(rand);
        	trainingSetNegative.remove(rand);
        	validationSetNegative.add(prot);
        	trainingSize--;
        }
        negativeTrainingSize = trainingSetNegative.size();
        negativeValidationSize = validationSetNegative.size();
	}
	
	public SetBuilder(String filePositive, String fileEasy, String fileModerate, String fileHard, int rep) throws FileNotFoundException {
		
		int length;
		this.repetition = rep;
		File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                	length = line1.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetPositive.add(line1);
                }
            }
        }
        
        File file2 = new File(fileEasy);
        if(!file2.exists()) {
        	System.out.println("The file " + fileEasy + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc2 = new Scanner(new File(fileEasy))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                	length = line2.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetNegative.add(line2);
                }
            }
        }
        
        File file3 = new File(fileModerate);
        if(!file3.exists()) {
        	System.out.println("The file " + fileModerate + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc3 = new Scanner(new File(fileModerate))) {
            while (sc3.hasNextLine()) {
                String line3 = sc3.nextLine().trim();
                if (line3.charAt(0) == '>') {
                } else {
                	length = line3.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetNegative.add(line3);
                }
            }
        }
        
        File file4 = new File(fileHard);
        if(!file4.exists()) {
        	System.out.println("The file " + fileHard + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc4 = new Scanner(new File(fileHard))) {
            while (sc4.hasNextLine()) {
                String line4 = sc4.nextLine().trim();
                if (line4.charAt(0) == '>') {
                } else {
                	length = line4.length();
                	if(length > maxLength) maxLength = length;
                	trainingSetNegative.add(line4);
                }
            }
        }
        
        int trainingSize = trainingSetPositive.size();
        Random r = new Random();
        int rand;
        String prot;
        for(int i = 0; i < trainingSize; i++) {
        	rand = r.nextInt(trainingSize);
        	prot = trainingSetPositive.get(rand);
        	trainingSetPositive.remove(rand);
        	validationSetPositive.add(prot);
        	trainingSize--;
        }
        positiveTrainingSize = trainingSetPositive.size();
        positiveValidationSize = validationSetPositive.size();
        
        trainingSize = trainingSetNegative.size();
        for(int i = 0; i < trainingSize; i++) {
        	rand = r.nextInt(trainingSize);
        	prot = trainingSetNegative.get(rand);
        	trainingSetNegative.remove(rand);
        	validationSetNegative.add(prot);
        	trainingSize--;
        }
        negativeTrainingSize = trainingSetNegative.size();
        negativeValidationSize = validationSetNegative.size();
	}
	
	public SetBuilder(String filePositive, String fileNegative, int size, int rep) throws FileNotFoundException {
		
		int length;
		this.repetition = rep;
		File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                   	trainingSetPositive.add(line1);
                }
            }
        }
        
        Random random = new Random();
		int index;
		String prot;
		
		for(int i = 0; i < size; i++) {
			index = random.nextInt(trainingSetPositive.size());
			prot = trainingSetPositive.get(index);
        	trainingSetPositive.remove(index);
        	validationSetPositive.add(prot);
        	length = prot.length();
        	if(length > maxLength) maxLength = length;
		}
        while (trainingSetPositive.size() > size) {
        	index = random.nextInt(trainingSetPositive.size());
        	trainingSetPositive.remove(index);
        }
        
        File file2 = new File(fileNegative);
        if(!file2.exists()) {
        	System.out.println("The file " + fileNegative + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc2 = new Scanner(new File(fileNegative))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                	trainingSetNegative.add(line2);
                }
            }
        }
        
        for(int i = 0; i < size; i++) {
			index = random.nextInt(trainingSetNegative.size());
			prot = trainingSetNegative.get(index);
        	trainingSetNegative.remove(index);
        	validationSetNegative.add(prot);
        	length = prot.length();
        	if(length > maxLength) maxLength = length;
		}
        while (trainingSetNegative.size() > size) {
        	index = random.nextInt(trainingSetNegative.size());
        	trainingSetNegative.remove(index);
        }
        for(int i = 0; i < size; i++) {
        	prot = trainingSetPositive.get(i);
        	length = prot.length();
        	if(length > maxLength) maxLength = length;
        	prot = trainingSetNegative.get(i);
        	length = prot.length();
        	if(length > maxLength) maxLength = length;
        }
        positiveTrainingSize = size;
        negativeTrainingSize = size;
        positiveValidationSize = size;
        negativeValidationSize = size;
	}
	
	public SetBuilder(String filePositive, String fileNegative, int rep, int size, int maxLength) throws FileNotFoundException {
		int length;
		this.repetition = rep;
		File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                   	if(line1.length() < (maxLength + 1)) filteredPositive.add(line1);
                }
            }
        }
        
        File file2 = new File(fileNegative);
        if(!file2.exists()) {
        	System.out.println("The file " + fileNegative + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc2 = new Scanner(new File(fileNegative))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                	if(line2.length() < (maxLength + 1)) filteredNegative.add(line2);
                }
            }
        }

        positiveTrainingSize = size;
        negativeTrainingSize = size;
        positiveValidationSize = size;
        negativeValidationSize = size;
	}
	
	public SetBuilder(String filePositive, String fileNegative, String name1, int filter) throws IOException{
		
		String filteredpath1 = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\Pos" + filter + ".fa";
		File filteredFile1 = new File(filteredpath1);
		FileWriter csvPos = new FileWriter(filteredFile1);
		File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                   	if(line1.length() < (filter + 1)) {
                   		csvPos.append(line1);
                   		csvPos.append("\n");
                   	}
                }
            }
        }
        csvPos.flush();
        csvPos.close();
       
       String filteredpath2 = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\" + name1 + filter + ".fa";
       File filteredFile2 = new File(filteredpath2);
       FileWriter csvEasy = new FileWriter(filteredFile2);
       File file2 = new File(fileNegative);
        if(!file2.exists()) {
        	System.out.println("The file " + fileNegative + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        try (Scanner sc2 = new Scanner(new File(fileNegative))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                   	if(line2.length() < (filter + 1)) {
                   		csvEasy.append(line2);
                   		csvEasy.append("\n");
                   	}
                }
            }
        }
        csvEasy.flush();
        csvEasy.close();
       
	}

	
	///////////////////////////////////////////// Length computing ////////////////////////////////////////////////
	
	public SetBuilder(String filePositive, String fileEasy, String fileModerate, String fileHard) throws IOException {
		
		int length;
		File file1 = new File(filePositive);
        if(!file1.exists()) {
        	System.out.println("The file " + filePositive + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        String path = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\PositiveLenght"; 
        File lenghtFile1 = new File(path);
        FileWriter writer1 = new FileWriter(lenghtFile1);
        try (Scanner sc1 = new Scanner(new File(filePositive))) {
            while (sc1.hasNextLine()) {
                String line1 = sc1.nextLine().trim();
                if (line1.charAt(0) == '>') {
                } else {
                	length = line1.length();
                	writer1.append(Integer.toString(length));
                	writer1.append(", 0");
                }
            }
            writer1.flush();
            writer1.close();
        }
        
        File file2 = new File(fileEasy);
        if(!file2.exists()) {
        	System.out.println("The file " + fileEasy + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        String path1 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\EasyLenght"; 
        File lenghtFile2 = new File(path1);
        FileWriter writer2 = new FileWriter(lenghtFile2);
        try (Scanner sc2 = new Scanner(new File(fileEasy))) {
            while (sc2.hasNextLine()) {
                String line2 = sc2.nextLine().trim();
                if (line2.charAt(0) == '>') {
                } else {
                	length = line2.length();
                	writer2.append(Integer.toString(length));
                	writer2.append(", 0");

                }
            }
            writer2.flush();
            writer2.close();
        }
        
        File file3 = new File(fileModerate);
        if(!file3.exists()) {
        	System.out.println("The file " + fileModerate + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        String path2 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\ModLenght"; 
        File lenghtFile3 = new File(path2);
        FileWriter writer3 = new FileWriter(lenghtFile3);
        try (Scanner sc3 = new Scanner(new File(fileModerate))) {
            while (sc3.hasNextLine()) {
                String line3 = sc3.nextLine().trim();
                if (line3.charAt(0) == '>') {
                } else {
                	length = line3.length();
                	writer3.append(Integer.toString(length));
                	writer3.append(", 0");
                }
            }
            writer3.flush();
            writer3.close();
        }
        
        File file4 = new File(fileHard);
        if(!file4.exists()) {
        	System.out.println("The file " + fileHard + " does not exist or the path is wrong.");
        	System.exit(1);
        }
        String path3 = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\HardLenght"; 
        File lenghtFile4 = new File(path3);
        FileWriter writer4 = new FileWriter(lenghtFile4);
        try (Scanner sc4 = new Scanner(new File(fileHard))) {
            while (sc4.hasNextLine()) {
                String line4 = sc4.nextLine().trim();
                if (line4.charAt(0) == '>') {
                } else {
                	length = line4.length();
                	writer4.append(Integer.toString(length));
                	writer4.append(", 0");
                }
                
            }
            writer4.flush();
            writer4.close();
        }
    }
	
	
	////////////////////////////////////////////// Getters & Setters //////////////////////////////////////////////

	public void changeSets() {
		Random random = new Random();
		int index;
		String prot;
		trainingSetPositive = new ArrayList<>(filteredPositive);
		
		for(int i = 0; i < positiveTrainingSize; i++) {
			index = random.nextInt(trainingSetPositive.size());
			prot = trainingSetPositive.get(index);
        	trainingSetPositive.remove(index);
        	validationSetPositive.add(prot);
		}
        while (trainingSetPositive.size() > positiveTrainingSize) {
        	index = random.nextInt(trainingSetPositive.size());
        	trainingSetPositive.remove(index);
        }
        
        trainingSetNegative = new ArrayList<>(filteredNegative);
        for(int i = 0; i < negativeTrainingSize; i++) {
			index = random.nextInt(trainingSetNegative.size());
			prot = trainingSetNegative.get(index);
        	trainingSetNegative.remove(index);
        	validationSetNegative.add(prot);
        }
        while (trainingSetNegative.size() > negativeTrainingSize) {
        	index = random.nextInt(trainingSetNegative.size());
        	trainingSetNegative.remove(index);
        }
	}

	String getPositiveTrainingProtein(int index){
		return trainingSetPositive.get(index);
	}

	String getNegativeTrainingProtein(int index){
		return trainingSetNegative.get(index);
	}

	String getPositiveValidationProtein(int index){
		return validationSetPositive.get(index);
	}

	String getNegativeValidationProtein(int index){
		return validationSetNegative.get(index);
	}

	int getPosTrainSize() {
		return positiveTrainingSize;
	}
	int getPosValSize() {
		return positiveValidationSize;
	}

	int getNegTrainSize() {
		return negativeTrainingSize;
	}

	int getNegValSize() {
		return negativeValidationSize;
	}

	int getMaxLength() {
		return maxLength;
	}

	int getRepetition() {
		return repetition;
	}
}
