package candys;

import java.util.ArrayList;
import candys.Mlink;


public class Vnode {
	
	////////////////////////////////////////////// Fields and Parameters ///////////////////////////////////////////////////
	
	private int id;
	private byte vstate;
	private byte vstatefuture;
	private boolean nodeUpdated;
	private boolean isDriven = false;
	private IDrive drive;
	private ArrayList<Vnode> nodeNeigbourhood;
	private ArrayList<Mlink> linkNeigbourhood;
	
	private static float vround = (float)0.5;
	
	/////////////////////////////////////////////// Constructor Methods /////////////////////////////////////////////////////
	
	 Vnode(int idArg) {
		id = idArg;
		vstate = 0;
		vstatefuture = 0;
		nodeUpdated = false;
		nodeNeigbourhood = new ArrayList<Vnode>();
		linkNeigbourhood = new ArrayList<Mlink>();
		if(CANetwork.debug >= 1) System.out.println("Created the node with id = " + id);
	}
	
	Vnode(int idArg, int v) {
		this(idArg);
		this.vstate = (byte)v;
	}
	
	Vnode(int idArg, IDrive driveArg) {
		// TODO Auto-generated constructor stub
		this(idArg);
		isDriven = true;
		drive = driveArg;
		if(driveArg == null) {
			System.out.println("Empty drive");
			System.exit(1);
		}
			
		
	}
	
	///////////////////////////////////////////// Auxiliary Methods ////////////////////////////////////////////////
	
	void joinNodeNeigbourhood(Vnode nodeArg) {
		this.nodeNeigbourhood.add(nodeArg);
	}
	
	
	void addNeighbour(Mlink mlink) {
		this.linkNeigbourhood.add(mlink);
	}
		
	void makeDriven(IDrive d, int iterator) {
		this.isDriven = true;
		this.drive = d;
		this.vstate = (byte) d.appliedVoltage(0, iterator);
	}
	
	//////////////////////////////////////////// Time Step Methods /////////////////////////////////////////////////

	void nodeStep(int tArg, int iterator) {
		float v0 = 0;
		
		if(isDriven) {
			v0 = drive.appliedVoltage(tArg, iterator);
		}
		
		else {
			int neigbourhoodSize = this.nodeNeigbourhood.size();
			float sumResistance = 0;
			float sumVoltage = 0;
			float one = 1;
			for(int i = 0; i < neigbourhoodSize; i++) {
				float ri = this.linkNeigbourhood.get(i).getMlinkState();
				float vi = this.nodeNeigbourhood.get(i).getVnodeState();
				sumVoltage = sumVoltage + vi / ri; 
				sumResistance = sumResistance + one / ri;
			}
			v0 = sumVoltage / sumResistance;
		}
		
		byte newState = roundVoltage(v0);
		this.vstatefuture = newState;
		this.nodeUpdated = true;			
	}
	
	private byte roundVoltage(float voltage) {
		if(voltage <= -vround) {
			return (byte) -1;				
		}
		if(voltage >= vround) {
			return (byte) 1;
		}
		return (byte) 0;
	}

	
	void nodeStepFinal() {
		// TODO Auto-generated method stub
		this.vstate = this.vstatefuture;
		this.nodeUpdated = false;			
	}

	
	////////////////////////////////////////////// Info & Getters/Setters ///////////////////////////////////////////////
	
	int getID() {
		return id;
	}
	
	byte getVnodeState() {
		return vstate;
	}
	
	
	void info() {
		System.out.println("The node " + this.getID() 
		+ " has voltage " + this.vstate);
	}
	
	int degree() {
		int degree = this.linkNeigbourhood.size();
		return degree;
	}

}