package candys;

import candys.Vnode;

public class Mlink {
	
	///////////////////////////////////////////////// Fields and Parameters /////////////////////////////////////////////////
	
	private Vnode node1;
	private Vnode node2;
	private String id;
	private byte mstate;
	private byte mstatefuture;
	private boolean linkUpdated;
	
	private static byte rmin = 1; 
	private static byte rmax = 5; 
	private static float alpha = 1;
	private static float beta = 2;
	private static float vthreshold = (float) 1.5;

	
	float rMiddle = (rmax - rmin) / 2;
	
	float[][] vchange = {
			{-2 * alpha, -2 * beta - (alpha - beta) * vthreshold, -2 * beta - (alpha - beta) * vthreshold},
			{-alpha, -alpha, -beta - (alpha - beta) * vthreshold},
			{0, 0, 0},
			{alpha, alpha, beta + (alpha - beta) * vthreshold},
			{2 * alpha, 2 * beta + (alpha - beta) * vthreshold, 2 * beta + (alpha - beta) * vthreshold}
			};
	
	/////////////////////////////////////////////// Constructor Methods ////////////////////////////////////////////////////////
	
	Mlink(Vnode node1Arg, Vnode node2Arg) {
		node1 = node1Arg;
		int id1 = this.node1.getID();
		node2 = node2Arg;
		int id2 = this.node2.getID();
		mstate = rmin;
		linkUpdated = false;
		node1.joinNodeNeigbourhood(node2);
		node2.joinNodeNeigbourhood(node1);
		node1.addNeighbour(this);
		node2.addNeighbour(this);
		this.id = Integer.toString(id1) + "-" + Integer.toString(id2);
		if(CANetwork.debug >= 1) System.out.println("Created the link with id = " + this.id);
		
	}
	
	Mlink(Vnode node1Arg, Vnode node2Arg, int r) {
		this(node1Arg, node2Arg);
		if(r==-1) this.mstate = rmin;
		else if(r==1) this.mstate = rmax;
		else {
			System.out.print("Input to Mlink constructor has to be 1 or -1");
			System.exit(1);
		}
	}
	
	///////////////////////////////////////////////////// Auxiliary Methods ///////////////////////////////////////////////

	
	public boolean checkID(String stringArg) {
		boolean isID;
		String theID = this.getMlinkID();
		isID = stringArg.equals(theID);
		return isID;
	}

	
	////////////////////////////////////////////////////// Step Methods /////////////////////////////////////////////////
	
	void linkStep() {
		byte newState;
		Vnode firstNode = this.getNode1();
		Vnode secondNode = this.getNode2();
		byte state1 = firstNode.getVnodeState();
		byte state2 = secondNode.getVnodeState();
		int vdifference = state2 - state1;
		double fdot;
		fdot = ffdot(vdifference);
		double vdistance = this.mstate + fdot;
		double dist2min = vdistance - rmin;
		if (dist2min == Math.min(rmax - vdistance, vdistance - rmin)){
			newState = rmin;
		}
		else{
			newState = rmax;
		}
		this.mstatefuture = newState;
		this.linkUpdated = true;
	}

	private double ffdot(int vdifference) { 
		double fdot;
		if(vthreshold > 2) {
			fdot = vchange[vdifference + 2][0];
		}
		else if(2 >= vthreshold && vthreshold > 1) {
			fdot = vchange[vdifference + 2][1];
		}
		else if(1>= vthreshold && vthreshold >= 0) {
			fdot = vchange[vdifference + 2][2];
		}
		else {
			fdot = 0;
			System.out.println("Threshold voltage should be greater or equal to 0");
			System.exit(1);
		}
		return fdot;
	}
	
	private double ffdot2(int vdifference) {
		double fdot;
		if(vdifference< -vthreshold) {
			double fdot1 = beta * vdifference  ;
			double fdot2 = (alpha - beta) * vthreshold;
			fdot = fdot1 - fdot2;
			return fdot;
		}
		if(vdifference > vthreshold) {
			double fdot1 = beta * vdifference  ;
			double fdot2 = (alpha - beta) * vthreshold;
			fdot = fdot1 + fdot2;
			return fdot;
		}
		fdot = 	alpha * vdifference;			
		return fdot;
	}
	
	
	 void linkStepFinal() {
		// TODO Auto-generated method stub
		this.mstate = this.mstatefuture;
		this.linkUpdated = false;
	}

	
	/////////////////////////////////////////////////// Output layer Methods /////////////////////////////////////////////////
	
	double weightedLink(double arg1) {
		double weightedState = this.mstate * arg1;
		return weightedState;
		
	}
	
	int stateOutput() {
		if(this.mstate==rmin) return -1;
		else return 1;
	}
	
	////////////////////////////////////////////////// Info & getters/setters //////////////////////////////////////////////////
	
	void info() {
		System.out.println("The link " + this.getMlinkID() 
	    + " is in state " + this.mstate);
	}	
	
	int indexNode1() {
		String thisID = this.id;
		int indexChar = thisID.indexOf('-');
		String indexString = thisID.substring(0, indexChar);
		int index = Integer.parseInt(indexString);
		return index;
	}
	
	int indexNode2() {
		String thisID = this.id;
		int indexChar = thisID.indexOf('-');
		String indexString = thisID.substring(indexChar + 1);
		int index = Integer.parseInt(indexString);
		return index;
	}
	
	byte getMlinkState() {
		return mstate;
	}
			
	private Vnode getNode1() {
		return node1;
	}
	
	private Vnode getNode2() {
		return node2;
	}
	
	private String getMlinkID() {
		return id;
	}	
	
	byte getMlinkStateRelaxation() {
		if(mstate == rmin) return 0;
		else return 1;
	}
}
