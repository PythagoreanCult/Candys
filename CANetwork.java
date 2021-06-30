package candys;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import javax.imageio.IIOException;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.SparseMatrix2D;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;


import candys.Vnode;
import candys.Mlink;

public class CANetwork {
	
	////////////////////////////////////////////////// Fields and Parameters //////////////////////////////////////////////////
	
	static int debug = 0;
	
	public void setDebugLevel(int dbl) {
		CANetwork.debug = dbl;
	}
	
	private int noflinks = 0;
	private int nofnodes = 0;
	private int nofoutputs;
	private boolean optimalyCoded;
	double unresponsive = 0;
	boolean useless = false;
	boolean singularTraining = false;
	
	private int time = 0;
	
	private DenseDoubleMatrix2D weights;
	private double[][] outputStates;
	private DenseDoubleMatrix2D corrMatrix;
	private DenseDoubleMatrix2D bVector;
	private Random rand = new Random();

	
	Matrix adjacency;
	int[] drivenIndexes;
	String[] outputIDs;
	double[] trainingErrors;
	
	
	
	private ArrayList<Mlink> mlinks = new ArrayList<Mlink>();
	private ArrayList<Vnode> vnodes = new ArrayList<Vnode>();
	private ArrayList<Vnode> drivenNodes = new ArrayList<Vnode>();
	private ArrayList<Mlink> outputLinks = new ArrayList<Mlink>();
	
	
	//////////////////////////////////////////////// Constructor Methods ////////////////////////////////////////////////////////
	
	
	
	public CANetwork(int MaxSize) {
		adjacency = SparseMatrix2D.Factory.zeros(MaxSize,MaxSize);
		System.out.println("creating empty network");
	}
	
	public CANetwork(int nnodes, double density, boolean isOptimalyCoded, double outputs) {
		
		
		double randState;
		this.randomNode();
		int randInt;
		int randInt2;
		Vnode ithNode;
		String linkId;
		boolean repeatedLink;
		Mlink ithLink;
		adjacency = SparseMatrix.Factory.zeros(nnodes, nnodes);
		
		for(int i = 1; i < nnodes; i++) {
			ithNode = this.randomNode();
			randInt = (int)(Math.random() * i);
			this.randomLink(vnodes.get(randInt),ithNode);
		}
		while(this.noflinks < density * nofnodes * (nofnodes - 1)) {
			randInt = (int)(Math.random() * nofnodes);
			randInt2 = (int)(Math.random() * nofnodes);
			if(randInt == randInt2) {}
			else {
				linkId = Integer.toString(randInt + 1) + "-" + Integer.toString(randInt2 + 1);
				repeatedLink = false;
				for(int i = 0; i < noflinks; i++) {
					ithLink = mlinks.get(i);
					repeatedLink = repeatedLink | ithLink.checkID(linkId);
				}
				if(repeatedLink) {}
				else {
					randState = Math.random();
					if(randState < 0.5) this.newMlink(vnodes.get(randInt), vnodes.get(randInt2), -1);
					else {this.newMlink(vnodes.get(randInt), vnodes.get(randInt2), 1);}
				}
			}
		}
		/*System.out.println("Creating random Erdös-Renyi network with " + connectivity * 100 + 
				"% connectivity and " + nnodes + " nodes.");*/
		optimalyCoded = isOptimalyCoded;
		
		if(optimalyCoded) {
			drivenNodes = chooseNodes(5);
		}
		else {
			drivenNodes = chooseNodes(21);
		}
		
		for(int i = 0; i < drivenNodes.size();i++) {
			int drivenIndex = vnodes.indexOf(drivenNodes.get(i));
			drivenIndexes[i] = drivenIndex;
		}
		
		if(outputs < (1/nnodes) || outputs > 1) {
			System.out.println("Number of outputs has to be between 1/(#nodes) and 1.");
			System.exit(1);
		}
		nofoutputs = (int) Math.floor(outputs * noflinks);
		corrMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1,nofoutputs + 1);
		bVector = DenseMatrix.Factory.zeros(nofoutputs + 1,1);
		outputLinks = this.chooseLinks(nofoutputs);
		for(int i = 0; i < nofoutputs; i++) {
			//outputLinks.get(i).info();
			int index1 = outputLinks.get(i).indexNode1();
			int index2 = outputLinks.get(i).indexNode2();
			adjacency.setAsDouble(2, index1 - 1, index2 - 1);
		}
	}

	public CANetwork(int nnodes, double density, boolean isOptimalyCoded, double outputs, boolean preferentialAttachment){
		
		optimalyCoded = isOptimalyCoded;
		adjacency = SparseMatrix2D.Factory.zeros(nnodes,nnodes);
		rand = new Random();
		double randState;
		int initialNodes;
		if(density < 0.25) {
			double b = nnodes + 1;
			initialNodes = (int) Math.floor((b - Math.sqrt(b * b - 4 * (density * nnodes * (nnodes - 1) + 1)))/2);
		} else {
			double b = (2*nnodes + 1);
			initialNodes = (int) Math.floor((b - Math.sqrt(b * b - 8 * (density * nnodes * (nnodes - 1) + 1))) / 4);
		}
		
		
		for(int i = 0; i < initialNodes; i++) {
			this.randomNode();
			if(i > 0) this.randomLink(vnodes.get(i - 1), vnodes.get(i));
		}
		
		int steps = nnodes - initialNodes;
		int linksPerStep;
		if(density < 0.25) {
			linksPerStep = initialNodes;
		}
		else{
			linksPerStep = 2 * initialNodes;
		}
		
		
		for(int t = 0; t < steps; t++) {
			int links = 0;
			Vnode node = this.randomNode();
			int nodeIndex = vnodes.indexOf(node);
			if (initialNodes == 1 && t == 0) {
				randState = Math.random();
				if(randState < 0.5)	this.newMlink(node, vnodes.get(0), -1);
				else this.newMlink(node, vnodes.get(0), 1);
				randState = Math.random();
				if(randState < 0.5)	this.newMlink(vnodes.get(0),node, -1);
				else this.newMlink(vnodes.get(0), node, 1);
			}
			else {
				while(links < linksPerStep) {
					ArrayList<String> nonUniformList = new ArrayList<String>();
					
					for(int i = 0; i < mlinks.size(); i++) {
						Mlink link = mlinks.get(i);
						nonUniformList.add(Integer.toString(link.indexNode1()));
						nonUniformList.add(Integer.toString(link.indexNode2()));
					}
					
					int nonUniformLength = nonUniformList.size();
					int randIndex = rand.nextInt(nonUniformLength);
					String futureID1 = Integer.toString(nodeIndex) + "-" + nonUniformList.get(randIndex);
					String futureID2 = nonUniformList.get(randIndex) + "-" + Integer.toString(nodeIndex);
					boolean existsID1 = false;
					boolean existsID2 = false;
					
					for (int i = 0; i < mlinks.size(); i++) {
						Mlink link = mlinks.get(i);
						existsID1 = link.checkID(futureID1) || existsID1;
						existsID2 = link.checkID(futureID2) || existsID2;
					}
					
					if(futureID1==futureID2) {
						}
					else if (!existsID1 && !existsID2) {
						this.randomLink(node, vnodes.get(Integer.parseInt(nonUniformList.get(randIndex)) - 1));
						links++;
					} else if (existsID1 && !existsID2) {
						randState = Math.random();
						if (randState < 0.5) this.newMlink(vnodes.get(Integer.parseInt(nonUniformList.get(randIndex)) - 1), node, -1);
						else this.newMlink(vnodes.get(Integer.parseInt(nonUniformList.get(randIndex)) - 1), node, 1);
						links++;
					} else if (!existsID1 && existsID2) {
						randState = Math.random();
						if (randState < 0.5) this.newMlink(node,vnodes.get(Integer.parseInt(nonUniformList.get(randIndex)) - 1),-1);
						else this.newMlink(node, vnodes.get(Integer.parseInt(nonUniformList.get(randIndex)) - 1), 1);
						links++;
					} 
			    }
			}
		}
		
		/*System.out.println("Creating random Albert-Barabasi network with " + connectivity * 100 + 
				"% connectivity and " + nnodes + " nodes.");*/
		
		if(optimalyCoded) {
			drivenNodes = chooseNodes(5);
		}
		else {
			drivenNodes = chooseNodes(21);
		}
		
		for(int i = 0; i < drivenNodes.size();i++) {
			int drivenIndex = vnodes.indexOf(drivenNodes.get(i));
			drivenIndexes[i] = drivenIndex;
		}
		
		if(outputs < (1/nnodes) || outputs > 1) {
			System.out.println("Number of outputs has to be between 1/(#nodes) and 1.");
			System.exit(1);
		}
		nofoutputs = (int) Math.floor(outputs * noflinks);
		corrMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, nofoutputs + 1);
		bVector = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
		outputLinks = this.chooseLinks(nofoutputs);
		for(int i = 0; i < nofoutputs; i++) {
			int index1 = outputLinks.get(i).indexNode1();
			int index2 = outputLinks.get(i).indexNode2();
			adjacency.setAsDouble(2, index1 - 1, index2 - 1);
		}
		
	}
	
	public CANetwork(int nnodes, double density, boolean isOptimalyCoded, double outputs, double beta) {
		
		optimalyCoded = isOptimalyCoded;
		adjacency = SparseMatrix2D.Factory.zeros(nnodes,nnodes);
		double degreeHalf = Math.floor(density * (nnodes - 1));
		int links = (int) Math.floor(nnodes*(nnodes-1)*density);
		for(int i = 0; i < nnodes; i++) {
			this.randomNode();
		}
		Vnode node1;
		Vnode node2;
		int index1;
		double randState;
		int state;
		double r;
		int maxIndex = (int) (links - degreeHalf * nofnodes);
		degreeHalf++;
		for(int i = 0; i < nofnodes; i++) {
			node1 = vnodes.get(i);
			if(i == maxIndex) degreeHalf--;
			for(int j = 1; j < (degreeHalf + 1); j++) {
				index1 = (i + j) % nofnodes;
				node2 = vnodes.get(index1);
				r = Math.random();
				if(r < 0.5) state = -1;
				else state = 1;
				this.newMlink(node1, node2, state);
			}
		}

		degreeHalf++;
		int correctIndex = 0;
		for(int i = 0; i < nofnodes; i++) {
			if(i == maxIndex) degreeHalf--;
			for(int j = 0; j < degreeHalf; j++) {
				r = Math.random();
				if(r < beta) {
					mlinks.remove(correctIndex);
					noflinks--;
					node1 = vnodes.get(i);
					index1 = rand.nextInt(nofnodes);
					while(index1 == i) index1 = rand.nextInt(nofnodes);
					boolean replaced = false;
					while(!replaced) {
						String futureID1 = Integer.toString(index1 + 1) + "-" + Integer.toString(i + 1);
						String futureID2 = Integer.toString(i + 1) + "-" + Integer.toString(index1 + 1);
						boolean existsID1 = false;
						boolean existsID2 = false;
						
						for (int k = 0; k < mlinks.size(); k++) {
							Mlink link = mlinks.get(k);
							existsID1 = link.checkID(futureID1) || existsID1;
							existsID2 = link.checkID(futureID2) || existsID2;
						}
						
						if (!existsID1 && !existsID2) {
							this.randomLink(node1, vnodes.get(index1),correctIndex);
							replaced = true;
						} else if (existsID1 && !existsID2) {
							randState = Math.random();
							if (randState < 0.5) this.newMlink(node1, vnodes.get(index1), -1, correctIndex);
							else this.newMlink(node1, vnodes.get(index1), 1, correctIndex);
							replaced = true;
						} else if (!existsID1 && existsID2) {
							randState = Math.random();
							if (randState < 0.5) this.newMlink(vnodes.get(index1), node1, -1, correctIndex);
							else this.newMlink(vnodes.get(index1), node1, 1, correctIndex);
							replaced = true;
						} else {
							index1 = rand.nextInt(nofnodes);
							while(index1 == i) index1 = rand.nextInt(nofnodes);
						}
					}
				}
				correctIndex++;
			}
		}
		if(optimalyCoded) {
			drivenNodes = chooseNodes(5);
		}
		else {
			drivenNodes = chooseNodes(21);
		}
		
		for(int i = 0; i < drivenNodes.size(); i++) {
			int drivenIndex = vnodes.indexOf(drivenNodes.get(i));
			drivenIndexes[i] = drivenIndex;
		}
		
		if(outputs < (1/nnodes) || outputs > 1) {
			System.out.println("Number of outputs has to be between 1/(#nodes) and 1.");
			System.exit(1);
		}
		nofoutputs = (int) Math.floor(outputs * noflinks);
		corrMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, nofoutputs + 1);
		bVector = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
		outputLinks = this.chooseLinks(nofoutputs);
		for(int i = 0; i < nofoutputs; i++) {
			index1 = outputLinks.get(i).indexNode1();
			int index2 = outputLinks.get(i).indexNode2();
			adjacency.setAsDouble(2, index1 - 1, index2 - 1);
		}
		
	}
	
	public CANetwork(Matrix adjacencyMatrix, int[] driven) {
		
		double randState;
		int nnodes = (int) adjacencyMatrix.getSize(0);
		for(int i = 0; i < nnodes; i++) {
			this.randomNode();
		}
		adjacency = SparseMatrix.Factory.zeros(nnodes, nnodes);
		drivenIndexes = driven;
		
		int drivenSize = driven.length;
		optimalyCoded = false;
		if (drivenSize == 5){
			optimalyCoded = true;
		}
		Vnode node;
		for(int i = 0; i < drivenSize; i++) {
			int index = driven[i];
			node = vnodes.get(index);
			drivenNodes.add(node);
		}
		Mlink link;
		for(int i = 0; i < nofnodes; i++) {
			for(int j = 0; j < nofnodes; j++) {
				if(adjacencyMatrix.getAsDouble(i,j)==1) {
					randState = Math.random();
					if(randState < 0.5) this.newMlink(vnodes.get(i), vnodes.get(j), -1);
					else this.newMlink(vnodes.get(i), vnodes.get(j), 1);
				} else if(adjacencyMatrix.getAsDouble(i,j) == 2){
					randState = Math.random();
					if(randState < 0.5) link = this.newMlink(vnodes.get(i), vnodes.get(j), -1);
					else link = this.newMlink(vnodes.get(i), vnodes.get(j), 1);
					outputLinks.add(link);
				}
			}
		}
		nofoutputs = outputLinks.size();
		corrMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1,nofoutputs + 1);
		bVector = DenseMatrix.Factory.zeros(nofoutputs + 1,1);
	}

	//////////////////////////////////////////// Auxiliary Methods for Constructors /////////////////////////////////////////////////
	
	private ArrayList<Vnode> chooseNodes(int intArg) {
		int randInt;
		ArrayList<Vnode> chosen = new ArrayList<>(vnodes);
		ArrayList<Integer> drivenChosen = new ArrayList<>();
		if(nofnodes < intArg) {
			System.out.println("Not enough nodes for this coding of the protein.");
			System.exit(1);
		}
		for(int i = 0; i < nofnodes; i++) {
			drivenChosen.add(i);
		}
		for(int i = 0; i < nofnodes - intArg; i++) {
			randInt = (int)(Math.random() * (nofnodes - i));
			chosen.remove(randInt);
			drivenChosen.remove(randInt);
		}
		
		drivenIndexes = new int[intArg];
		for(int i = 0; i < intArg; i++) {
			drivenIndexes[i] = drivenChosen.get(i);
		}
		return chosen;
	}
	
	private ArrayList<Mlink> chooseLinks(int intArg) {
		int randInt;
		ArrayList<Mlink> chosen = new ArrayList<>(mlinks);
		for(int i = 0; i < noflinks - intArg; i++) {
			randInt = (int)(Math.random() * (noflinks - i));
			chosen.remove(randInt);
		}
		return chosen;
	}
	
	private void randomLink(Vnode vnode1, Vnode vNode2) {
		double randState;
		double orientation;
		orientation = Math.random();
		randState = Math.random();
		if(orientation < 0.5) {
			if(randState < 0.5) this.newMlink(vnode1, vNode2,-1);
			else {this.newMlink(vnode1, vNode2, 1);}
			}
		else {
			if(randState < 0.5) this.newMlink(vNode2, vnode1,-1);
			else {this.newMlink(vNode2, vnode1, 1);}
		}
	}
	
	private void randomLink(Vnode vnode1, Vnode vNode2, int index) {
		double randState;
		double orientation;
		orientation = Math.random();
		randState = Math.random();
		if(orientation < 0.5) {
			if(randState < 0.5) this.newMlink(vnode1, vNode2,-1, index);
			else {this.newMlink(vnode1, vNode2, 1, index);}
			}
		else {
			if(randState < 0.5) this.newMlink(vNode2, vnode1,-1, index);
			else {this.newMlink(vNode2, vnode1, 1, index);}
		}
	}

	private Vnode randomNode() {
		double randState;
		randState = Math.random();
		if(randState < 0.333333333333333) {
			return this.newVnode(-1);
		}
		else if(randState < 0.666666666666666) {
			return this.newVnode(0);
		}
		else {
			return this.newVnode(1);
		}
	}
	
	private boolean nodeBelongsToNetwork(Vnode vnArg) {
		boolean belongs = vnodes.contains(vnArg);
		return belongs;
	}
	
	public Mlink newMlink(Vnode node1Arg, Vnode node2Arg) {
		boolean ok1 = this.nodeBelongsToNetwork(node1Arg);
		boolean ok2 = this.nodeBelongsToNetwork(node2Arg);
		boolean ok = ok1 && ok2;
		if (!ok) {
			System.out.println("Can not link to an external node");
			System.exit(1);
		}	
		Mlink newlink = new Mlink(node1Arg, node2Arg);
		int index1 = vnodes.indexOf(node1Arg);
		int index2 = vnodes.indexOf(node2Arg);
		this.adjacency.setAsDouble(1, index1, index2);
		mlinks.add(newlink);
		noflinks++;
		return newlink;
	}
	
	public Mlink newMlink(Vnode node1Arg, Vnode node2Arg, int r) {
		boolean ok1 = this.nodeBelongsToNetwork(node1Arg);
		boolean ok2 = this.nodeBelongsToNetwork(node2Arg);
		boolean ok = ok1 && ok2;
		if (!ok) {
			System.out.println("Can not link to an external node");
			System.exit(1);
		}	
		Mlink link = new Mlink(node1Arg, node2Arg,r);
		int index1 = vnodes.indexOf(node1Arg);
		int index2 = vnodes.indexOf(node2Arg);
		this.adjacency.setAsDouble(1, index1, index2);
		mlinks.add(link);
		noflinks++;
		return link;
	}
	
	public Mlink newMlink(Vnode node1Arg, Vnode node2Arg, int r,int index) {
		boolean ok1 = this.nodeBelongsToNetwork(node1Arg);
		boolean ok2 = this.nodeBelongsToNetwork(node2Arg);
		boolean ok = ok1 && ok2;
		if (!ok) {
			System.out.println("Can not link to an external node");
			System.exit(1);
		}	
		Mlink link = new Mlink(node1Arg, node2Arg,r);
		int index1 = vnodes.indexOf(node1Arg);
		int index2 = vnodes.indexOf(node2Arg);
		this.adjacency.setAsDouble(1, index1, index2);
		mlinks.add(index,link);
		noflinks++;
		return link;
	}
	
	public Vnode newVnode() {
		nofnodes++;
		Vnode newnode = new Vnode(nofnodes);
		vnodes.add(newnode);
		return newnode;
	}
	
	public Vnode newVnode(int v) {
		nofnodes++;
		Vnode newnode = new Vnode(nofnodes, v);
		vnodes.add(newnode);
		return newnode;
	}
	
	public Vnode newVnode(IDrive driveArg) {
		nofnodes++;
		Vnode newnode = new Vnode(nofnodes, driveArg);
		vnodes.add(newnode);
		return newnode;
	}
	
	public void hubInput() {
		ArrayList<Vnode> sortNodes = new ArrayList<>(vnodes);
		Collections.sort(sortNodes, new DegreeSort());
		int drivers = drivenNodes.size();
		drivenNodes.clear();
		for(int i = 0; i < drivers; i++) {
			drivenNodes.add(sortNodes.get(i));
		}
	}
	
	///////////////////////////////////////// Time Step, Training and Classifying Methods //////////////////////////////////////////////
	
	
	
	public void step(boolean training, boolean testing, TestWAlg tester, int target) { 
		Mlink link;
		Vnode node;
		
		//Time step for the network
		 for(int i = 0; i < mlinks.size(); i++) {
			 link = mlinks.get(i);
			 link.linkStep();  
		 }
		 
		 for(int i = 0; i < vnodes.size(); i++) {
			 node = vnodes.get(i);
			 node.nodeStep(time, 0);
		 }
		 
		 for(int i = 0; i < drivenNodes.size(); i++) {
			 node = drivenNodes.get(i);
			 node.nodeStep(time, i);
		 }
		 
		 for(int i = 0; i < mlinks.size(); i++) {
			 link = mlinks.get(i);
			 link.linkStepFinal();
		 }
		 for(int i = 0; i < vnodes.size(); i++) {
			 node = vnodes.get(i);
			 node.nodeStepFinal();
		 }
		 
		 time++;
		 if(training) {
			 //Output optimization
			 outputStates = new double[nofoutputs + 1][1];
			 outputStates[0][0] = 1;
			 DenseDoubleMatrix2D outputMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
			 outputMatrix.setAsDouble(1, 0, 0);
			 for(int i = 0; i < nofoutputs; i++) {
				 link = outputLinks.get(i);
				 outputStates[i + 1][0] = link.stateOutput();
				 outputMatrix.setAsDouble(link.stateOutput(), i + 1, 0);
			 }
			 /////// Take x(t) for debugging /////////////////////
			 if(testing) {
				 Double[][] dbl = new Double[nofoutputs + 1][1];
				 for(int i = 0; i < nofoutputs + 1; i++) {
					 dbl[i][0] =outputStates[i][0];
				 }
				 tester.appendState(dbl,target);
			 }
			 
			 ////////////////////////////////////////////////////
			 
			 // = new Matrix(outputStates);
			 bVector = (DenseDoubleMatrix2D) bVector.plus(outputMatrix.times(target));
			 corrMatrix = (DenseDoubleMatrix2D) corrMatrix.plus(outputMatrix.mtimes(outputMatrix.transpose()));
		 }
		 
		 
	}
	
	
	public void trainStep(String prot, IDrive d, int repetition, int target, boolean testing,TestWAlg tester) {
		
		this.initialState(d);
		while(this.time < (repetition * prot.length())) {
			if (useless) this.networkSummary();
			this.step(true,testing, tester,target);
		}
		
	}
	
	
	public void train(SetBuilder data, boolean testing){
		
		String prot;
		//int maxLength = data.getMaxLength();
		int maxLength = data.getRepetition();
		TestWAlg tester = new TestWAlg();
		
		
		int dataSize =  data.getPosTrainSize();
		for(int i = 0; i < dataSize; i++) {
			prot = data.getPositiveTrainingProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			this.trainStep(prot, drive, maxLength, 1, testing, tester);
		}
		
		dataSize = data.getNegTrainSize();
		for(int i = 0; i < dataSize; i++) {
			prot = data.getNegativeTrainingProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			this.trainStep(prot, drive, maxLength, -1, testing, tester);
		}
		
		this.computeWeights();
		
	}
	
	double trainErrors(SetBuilder data, boolean testing) {
		
		//int maxLength = data.getMaxLength();
		int maxLength = data.getRepetition();
		int dataSize1 =  data.getPosTrainSize();
		int dataSize2 = data.getNegTrainSize();
		double totalError = 0;
		String prot;
		trainingErrors = new double[dataSize1 + dataSize2];
		
		for(int i = 0; i < dataSize1; i++) {
			prot = data.getPositiveTrainingProtein(i);
			trainingErrors[i] = this.getError(prot, maxLength, 1, testing);
			totalError = totalError + trainingErrors[i];
		}
		
		
		for(int i = 0; i < dataSize2; i++) {
			prot = data.getNegativeTrainingProtein(i);
			trainingErrors[i + dataSize1] = this.getError(prot, maxLength, -1, testing);
			totalError = totalError + trainingErrors[i + dataSize1];
		}
		return totalError;
	}
	
	double validationErrors(SetBuilder data, boolean testing) {
		
		//int maxLength = data.getMaxLength();
		int maxLength = data.getRepetition();
		int dataSize1 =  data.getPosValSize();
		int dataSize2 = data.getNegValSize();
		double totalError = 0;
		String prot;
		trainingErrors = new double[dataSize1 + dataSize2];
		
		for(int i = 0; i < dataSize1; i++) {
			prot = data.getPositiveValidationProtein(i);
			trainingErrors[i] = this.getError(prot, maxLength, 1, testing);
			totalError = totalError + trainingErrors[i];
		}
		
		
		for(int i = 0; i < dataSize2; i++) {
			prot = data.getNegativeValidationProtein(i);
			trainingErrors[i + dataSize1] = this.getError(prot, maxLength, -1, testing);
			totalError = totalError + trainingErrors[i + dataSize1];
		}
		return totalError;
	}
		
	public double getError(String prot,int maxLength, int target, boolean testing) {
		IDrive d;
		Mlink link;
		double output;
		/////// debugging/////
		
		TestWAlg tester = new TestWAlg();
		//int target = 1;
				
		////////////////////
		
		if(optimalyCoded) {
			d = new CodedProtein(prot, maxLength);
		}
		else {
			d = new Protein(prot, maxLength);
		}
		this.initialState(d);
		
		while(this.time < maxLength * prot.length()) {
			this.step(false, testing, tester, target);
		}
		
		outputStates = new double[nofoutputs + 1][1]; /////////////////How it was before, is wrong but lets see first
		DenseDoubleMatrix2D statesMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
		outputStates[0][0] = 1;
		for(int i = 0; i < nofoutputs; i++) {
			link = outputLinks.get(i);
			outputStates[i + 1][0] = link.stateOutput();
			statesMatrix.setAsDouble(link.stateOutput(), i + 1, 0);
		}
		
		output = weights.transpose().mtimes(statesMatrix).getAsDouble(0, 0);
		return Math.pow(output - target, 2);
		
	}

	double[] validate(SetBuilder data, boolean testing) {
		
		int truePositive;
		int trueNegative;
		int falseNegative = 0;
		int falsePositive = 0;
		int totalData = 0;
		//int maxLength = data.getMaxLength();
		int maxLength = data.getRepetition();
		double specificity;
		double sensitivity;
		double accuracy;
		String prot;
		double[] results;
		int dataSize;
		dataSize = data.getPosValSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = data.getPositiveValidationProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			falseNegative = falseNegative - (this.classifyProtein(prot, maxLength, testing) - 1);
		}
		falseNegative = falseNegative/2;
		truePositive = dataSize - falseNegative;
		
		dataSize = data.getNegValSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = data.getNegativeValidationProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			falsePositive = falsePositive + this.classifyProtein(prot, maxLength, testing) + 1;
		}
		falsePositive = falsePositive/2;
		trueNegative = dataSize - falsePositive;
		
		accuracy = (double)(truePositive + trueNegative)/totalData;
		sensitivity = (double)truePositive/(truePositive + falseNegative);
		specificity = (double)trueNegative/(trueNegative + falsePositive);
		results = new double[] {accuracy, specificity, sensitivity};
		return results;
	}
	
	double[] accuracyTraining(SetBuilder data, boolean testing) {
		
		int truePositive;
		int trueNegative;
		int falseNegative = 0;
		int falsePositive = 0;
		int totalData = 0;
		//int maxLength = data.getMaxLength();
		int maxLength = data.getRepetition();
		double specificity;
		double sensitivity;
		double accuracy;
		String prot;
		double[] results;
		int dataSize;
		dataSize = data.getPosTrainSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = data.getPositiveTrainingProtein(i);
			/*IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}*/
			falseNegative = falseNegative - (this.classifyProtein(prot, maxLength, testing) - 1);
		}
		falseNegative = falseNegative/2;
		truePositive = dataSize - falseNegative;
		
		dataSize = data.getNegTrainSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = data.getNegativeTrainingProtein(i);
			/*IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}*/
			falsePositive = falsePositive + this.classifyProtein(prot, maxLength, testing) + 1;
		}
		falsePositive = falsePositive/2;
		trueNegative = dataSize - falsePositive;
		
		accuracy = (double)(truePositive + trueNegative)/totalData;
		sensitivity = (double)truePositive/(truePositive + falseNegative);
		specificity = (double)trueNegative/(trueNegative + falsePositive);
		results = new double[] {accuracy, specificity, sensitivity};
		return results;
	}
	
	public int classifyProtein(String prot,int maxLength, boolean testing) {

		IDrive d;
		Mlink link;
		int output;
		/////// debugging/////
		
		TestWAlg tester = new TestWAlg();
		int target = 1;
				
		////////////////////
		
		if(optimalyCoded) {
			d = new CodedProtein(prot, maxLength);
		}
		else {
			d = new Protein(prot, maxLength);
		}
		this.initialState(d);
		
		outputStates = new double[nofoutputs + 1][1];
		DenseDoubleMatrix2D statesMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
		outputStates[0][0] = 1;
		for(int i = 0; i < nofoutputs; i++) {
			outputStates[i + 1][0] = 0;
		}
		while(this.time < maxLength * prot.length()) {
			this.step(false, testing, tester, target);
			if(this.time > ((maxLength - 1)*prot.length())){
				for(int i = 0; i < nofoutputs; i++) {
				link = outputLinks.get(i);
				outputStates[i + 1][0] = outputStates[i + 1][0] + link.stateOutput();
				}
			}
		}
		statesMatrix.setAsDouble(1, 0, 0);
		for(int i = 0; i < nofoutputs; i++) {
			outputStates[i + 1][0] = outputStates[i + 1][0]/(double)prot.length();
			statesMatrix.setAsDouble(outputStates[i + 1][0], i + 1, 0);
		}
			
		
		output = (int) Math.signum(weights.transpose().mtimes(statesMatrix).getAsDouble(0, 0));
		return output;
		
	}
	
	private void computeWeights() {
		
		if(corrMatrix.det() == 0) singularTraining = true;
		DenseDoubleMatrix2D invcorr = (DenseDoubleMatrix2D) corrMatrix.pinv();
		weights = (DenseDoubleMatrix2D) invcorr.mtimes(bVector); 
		
	}
	
	public void initialState(IDrive d) {
		this.time = 0;
		int size = this.drivenNodes.size();
		if(optimalyCoded && size == 21) {
			System.out.println("Network for protein coded using class CodedProtein");
			System.exit(1);
		}
		if(!optimalyCoded && size == 5) {
			System.out.println("Network for protein coded using class Protein");
			System.exit(1);
		}
		Vnode node;
		for(int i = 0; i < size; i++) {
			node = this.drivenNodes.get(i);
			node.makeDriven(d, i);
		}
	}
	
	///////////////////////////////////////////////// Memory Test ////////////////////////////////////////////////////
	
	public void memoryTrain(int rep, int memoryLength, int max, int trainingSize){
		
		for(int i = 0; i < trainingSize; i++) {
			MemoryCheck drive = new MemoryCheck(rep, memoryLength, max, this.optimalyCoded);
			this.initialState(drive);
			this.memoryTrainStep(drive, rep, max);
		}
		
		this.computeWeights();
		
	}
	
	double memoryValidate(int rep, int memoryLength, int max, int validationSize) {
		double accuracy = 0;
		Mlink link;
		for(int i =0; i < validationSize; i++) {
			MemoryCheck drive = new MemoryCheck(rep, memoryLength, max, this.optimalyCoded);
			this.initialState(drive);
			for(int j = 0; j < (max * rep); j++) {
				this.memoryStep(drive, false);
			}
			
			outputStates = new double[nofoutputs + 1][1];
			outputStates[0][0] = 1;
			DenseDoubleMatrix2D outputMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
			outputMatrix.setAsDouble(1, 0, 0);
			for(int k = 0; k < nofoutputs; k++) {
				link = outputLinks.get(k);
				outputStates[k + 1][0] = link.stateOutput();
				outputMatrix.setAsDouble(link.stateOutput(), k + 1, 0);
			}
			int output = (int) Math.signum(weights.transpose().mtimes(outputMatrix).getAsDouble(0, 0));
			if(output == drive.targets[max * rep - 1]) accuracy++;
		}
		accuracy = accuracy/validationSize;
		
		return accuracy;
		
	}
	
	private void memoryTrainStep(MemoryCheck drive, int rep, int max) {
	// TODO Auto-generated method stub
		this.initialState(drive);
		for(int j = 0; j < (max * rep-1); j++) {
			this.memoryStep(drive, true);
		}
	
	}
	
	private void memoryStep(MemoryCheck drive, boolean training){
		Mlink link;
		Vnode node;
		
		//Time step for the network
		 for(int i = 0; i < mlinks.size(); i++) {
			 link = mlinks.get(i);
			 link.linkStep();  
		 }
		 
		 for(int i = 0; i < vnodes.size(); i++) {
			 node = vnodes.get(i);
			 node.nodeStep(time, 0);
		 }
		 
		 for(int i = 0; i < drivenNodes.size(); i++) {
			 node = drivenNodes.get(i);
			 node.nodeStep(time, i);
		 }
		 
		 for(int i = 0; i < mlinks.size(); i++) {
			 link = mlinks.get(i);
			 link.linkStepFinal();
		 }
		 for(int i = 0; i < vnodes.size(); i++) {
			 node = vnodes.get(i);
			 node.nodeStepFinal();
		 }
		 
		 time++;
		 
		 if(training) {
		 //Output optimization
			 outputStates = new double[nofoutputs + 1][1];
			 outputStates[0][0] = 1;
			 DenseDoubleMatrix2D outputMatrix = DenseMatrix.Factory.zeros(nofoutputs + 1, 1);
			 outputMatrix.setAsDouble(1, 0, 0);
			 for(int i = 0; i < nofoutputs; i++) {
				 link = outputLinks.get(i);
				 outputStates[i + 1][0] = link.stateOutput();
				 outputMatrix.setAsDouble(link.stateOutput(), i + 1, 0);
			 }
			 
			 bVector = (DenseDoubleMatrix2D) bVector.plus(outputMatrix.times(drive.targets[this.time]));
			 corrMatrix = (DenseDoubleMatrix2D) corrMatrix.plus(outputMatrix.mtimes(outputMatrix.transpose()));
		 }
	}


	
	
	///////////////////////////////////////////////// Info & Save ///////////////////////////////////////////////
	
	public void networkSummary() {
		for(int i = 0; i < nofnodes; i++) {
			Vnode node = this.vnodes.get(i);
			node.info();
		}
		for(int i = 0; i < noflinks; i++) {
			Mlink link = this.mlinks.get(i);
			link.info();
		}
	}
	
	public void saveData(String fileName) throws IOException {
		
		int nnodes = this.nofnodes;
		int drivenSize = this.drivenNodes.size();
		String path = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\Saved Networks\\" + fileName + "Adjacency.txt";
		File file = new File(path);
		FileWriter csvWriter = new FileWriter(file);
		
		for(int i = 0; i < nnodes; i++) {
			for(int j = 0; j < nnodes; j++) {
				csvWriter.append(this.adjacency.getAsString(i,j));
				if(!(j == (nnodes - 1))) csvWriter.append(", ");
			}
			csvWriter.append("\n");
		}
		
		csvWriter.flush();
		csvWriter.close();
		
		String path2 = "C:\\Users\\user\\eclipse-workspace\\candys\\src\\candys\\Saved Networks\\" + fileName + "Drivers.txt";
		File file2 = new File(path2);
		FileWriter csvWriter2 = new FileWriter(file2);
		
		for(int i = 0; i < drivenSize; i++) {
			csvWriter2.append(Integer.toString(this.drivenIndexes[i]));
			if(!(i == (drivenSize - 1))) csvWriter2.append(", ");

		}
		
		csvWriter2.flush();
		csvWriter2.close();
	}

///////////////////////////////////////////// Relaxation Time Estimation ///////////////////////////////////////
	
	private double currentState(){
		
		Mlink link;
		byte state;
		double globalState = 0;
		for(int i = 0; i < nofoutputs; i++) {/////////////////////////change for all links later
			link = outputLinks.get(i);
			state = link.getMlinkStateRelaxation();
			globalState = (double) (globalState + state * Math.pow(2, -i));
		}
		return globalState;
	}
	
	void saveEvolution(String fileName, int time) throws IOException {
		
		TestWAlg tester = new TestWAlg();
		double state = this.currentState();
		String path = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName;
		File file = new File(path);
		FileWriter csvWriter = new FileWriter(file);
		csvWriter.append(Double.toString(state));
		csvWriter.append(", ");
		
		for(int i = 0; i < time; i++) {
			this.networkSummary();
			this.step(false, false, tester, 1);
			state = this.currentState();
			csvWriter.append(Double.toString(state));
			if(!(i == (time - 1))) csvWriter.append(", ");
					
		}
		csvWriter.flush();
		csvWriter.close();
	}
		
	int[] relaxationTime(int time) {
		
		double[] trajectory = new double[time + 1];
		trajectory[0] = this.currentState();
		TestWAlg tester = new TestWAlg();
		for(int i = 0; i < time; i++) {
			//this.networkSummary();
			this.step(false, false, tester, 1);
			trajectory[i + 1] = this.currentState();
		}
		
		int relaxationTime = 0;
		int period = 0;
		if(Arrays.equals(Arrays.copyOfRange(trajectory, 0, time), Arrays.copyOfRange(trajectory, 1, time + 1))) period = 1;
		else {
		boolean ended = false;
			for(int j = 0; j < (time + 1); j++) {
				for(int i = 0; i < (time*0.9 - j); i++) {
					double[] array1 = Arrays.copyOfRange(trajectory, i, time - j);
					double[] array2 = Arrays.copyOfRange(trajectory, 1 + i + j, time + 1);
					boolean difference = Arrays.equals(array1, array2);
					if(difference) {
						relaxationTime = i + 1;
						period = j + 1;
						ended = true;
						break;
					}	
				}
				if (ended) break;
			}
		}
		int[] result = new int[]{relaxationTime, period};
		return result;
		
	}
	
	double[] drivenRelaxationTime(SetBuilder dataset, int rep) { 
		
		int size = dataset.getPosTrainSize();
		double[] result = new double[2 * size + 2];
		String prot;
		IDrive d;
		int protLength;
		double relaxationTime = 0;
		double period = 0;
		int onePeriod = 0;
		for(int k = 0; k < (2 * size); k++) {
			
			if(k < size) prot = dataset.getPositiveTrainingProtein(k);
			else prot = dataset.getPositiveTrainingProtein(k - size);
			protLength = prot.length();
			if(this.optimalyCoded) d = new CodedProtein(prot, rep);
			else d = new Protein(prot,rep);
			this.initialState(d);
						
			double[] trajectory = new double[protLength * rep + 1];
			trajectory[0] = this.currentState();
			TestWAlg tester = new TestWAlg();
			for(int i = 0; i < (protLength * rep); i++) {
				this.step(false, false, tester, 1);
				trajectory[i + 1] = this.currentState();
			}
			
			
			if(Arrays.equals(Arrays.copyOfRange(trajectory, 0, protLength * rep), Arrays.copyOfRange(trajectory, 1, protLength * rep + 1))) {
				period++;
				onePeriod++;
				unresponsive++;
			}
			else {
				boolean ended = false;
				for(int j = 0; j < (protLength * rep + 1); j++) {
					for(int i = 0; i < (protLength * (rep - 1) - j); i++) {
						double[] array1 = Arrays.copyOfRange(trajectory, i, protLength * rep - j);
						double[] array2 = Arrays.copyOfRange(trajectory, 1 + i + j, protLength * rep + 1);
						boolean difference = Arrays.equals(array1, array2);
						if(difference) {
							relaxationTime = relaxationTime + i + 1;
							period = period + j + 1;
							ended = true;
							double scale;
							if(j == 0) {
								onePeriod++;
								unresponsive++;
								scale = 1;
								result[k + 2] = scale;
							}
							//else if((j + 1) == protLength) coincides++;
							//else if((j	+ 1) == (2 * protLength)) periodDoubling++;
							else {
								scale = (j + 1)/(double)protLength;
								result[k + 2] = scale;
								//System.out.println(scale);
							}
							break;
						}	
					}
					if (ended) break;
				}
			}
		}
		relaxationTime = relaxationTime/(2 * size);
		period = period/(2 * size);
		result[0] = relaxationTime;
		result[1] = period;
		if (unresponsive == 200) useless = true;
		return result;
		
	}
	
	void visualizeRelaxationTime(String fileName, int time) throws IOException {
		Mlink link;
		byte state;
		TestWAlg tester = new TestWAlg();
		String path = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName;
		File file = new File(path);
		FileWriter writer = new FileWriter(file);
		for(int i = 0; i < time; i++) {
			for(int j = 0; j < noflinks; j++) {
				link = mlinks.get(j);
				state = link.getMlinkStateRelaxation();
				writer.append(Integer.toString(state));
				if (!(j == (noflinks - 1))) writer.append(", ");
			}
			if(!(i == (time - 1))) writer.append("\n");
			//this.networkSummary();
			this.step(false, false, tester, 1);		
		}
		writer.flush();
		writer.close();
		
	}
	
	void visualizeRelaxationTime(String fileName, int time, IDrive d) throws IOException {
		Mlink link;
		byte state;
		this.initialState(d);
		TestWAlg tester = new TestWAlg();
		String path = "C:\\Users\\user\\Documents\\MATLAB\\Master Thesis\\" + fileName;
		File file = new File(path);
		FileWriter writer = new FileWriter(file);
		for(int i = 0; i < time; i++) {
			for(int j = 0; j < noflinks; j++) {
				link = mlinks.get(j);
				state = link.getMlinkStateRelaxation();
				writer.append(Integer.toString(state));
				if (!(j == (noflinks - 1))) writer.append(", ");
			}
			if(!(i == (time - 1))) writer.append("\n");
			//this.networkSummary();
			this.step(false, false, tester, 1);		
		}
		writer.flush();
		writer.close();
		
	}
	////////////////////////////////////// Ensemble Learning ////////////////////////////////////////
	
	public double[] evaluateEnsemble(SetBuilder dataSet, int ensembleSize, int nnodes, double density){
		
		
		ArrayList<CANetwork> ensemble = new ArrayList<CANetwork>();
		int truePositive;
		int trueNegative;
		int falseNegative = 0;
		int falsePositive = 0;
		for(int i1 = 0; i1 < ensembleSize; i1++) {
			CANetwork can = new CANetwork(nnodes, density, false, 0.5);
			ensemble.add(can);
		}
		for(int i1 = 0; i1 < ensembleSize; i1++) {
			ensemble.get(i1).train(dataSet, false);
		}
		
		int maxLength = dataSet.getRepetition();
		int dataSize;
		String prot;
		int totalData = 0;
		dataSize = dataSet.getPosValSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = dataSet.getPositiveValidationProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			int classification = 0;
			boolean testing = false;
			for(int i2 = 0; i2 < ensembleSize; i2++) {
				classification = classification + ensemble.get(i2).classifyProtein(prot, maxLength, testing);
			}
			if(classification > 0);
			else if(classification < 0) falseNegative++;
			else {
				System.out.print("Choose ensemble size to be an odd number");
				System.exit(1);
			}
		}
		truePositive = dataSize - falseNegative;
		
		dataSize = dataSet.getNegValSize();
		totalData = totalData + dataSize;
		for(int i = 0; i < dataSize; i++) {
			prot = dataSet.getNegativeValidationProtein(i);
			IDrive drive;
			if(this.optimalyCoded) {
				drive = new CodedProtein(prot, maxLength);
			} else {
				drive = new Protein(prot, maxLength);
			}
			int classification = 0;
			boolean testing = false;
			for(int i2 = 0; i2 < ensembleSize; i2++) {
				classification = classification + ensemble.get(i2).classifyProtein(prot, maxLength, testing);
			}
			if(classification < 0);
			else if(classification > 0) falsePositive++;
			else {
				System.out.print("Choose ensemble size to be an odd number");
				System.exit(1);
			}
			
		}
		trueNegative = dataSize - falsePositive;
		
		double accuracy = (double)(truePositive + trueNegative)/totalData;
		double sensitivity = (double)truePositive/(truePositive + falseNegative);
		double specificity = (double)trueNegative/(trueNegative + falsePositive);
		double[] results = new double[] {accuracy, specificity, sensitivity};
		return results;
		
		
	}
	
}


