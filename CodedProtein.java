package candys;

public class CodedProtein implements IDrive {
	public float[][] drivers;
	
	public CodedProtein() {
		
		
	}
	
	public CodedProtein(String sequence, int maxsize) {
		
		String prot = processSequence2(sequence, maxsize);
		
		int length = maxsize * sequence.length() - 1;
		drivers = new float[5][length + 1];
		String ithchar;
		
		
		String aminoacid1 = "A";
		String aminoacid2 = "C";
		String aminoacid3 = "D";
		String aminoacid4 = "E";
		String aminoacid5 = "F";
		String aminoacid6 = "G";
		String aminoacid7 = "H";
		String aminoacid8 = "I";
		String aminoacid9 = "K";
		String aminoacid10 = "L";
		String aminoacid11 = "M";
		String aminoacid12 = "N";
		String aminoacid13 = "P";
		String aminoacid14 = "Q";
		String aminoacid15 = "R";
		String aminoacid16 = "S";
		String aminoacid17 = "T";
		String aminoacid18 = "V";
		String aminoacid19 = "W";
		String aminoacid20 = "Y";
		//String wildCard = "X";		
		
		for(int i = length; i >= 0; i--) {
			ithchar = Character.toString(prot.charAt(i));
			
			if(aminoacid1.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid2.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid3.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid4.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid5.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid6.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid7.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid8.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid9.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid10.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid11.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid12.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid13.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid14.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid15.equals(ithchar)) {
				drivers[0][length-i] = (float)-1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid16.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid17.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid18.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else if(aminoacid19.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)1;
				drivers[4][length-i] = (float)-1;
			}
			
			else if(aminoacid20.equals(ithchar)) {
				drivers[0][length-i] = (float)1;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)1;
			}
			
			else {
				drivers[0][length-i] = (float)0;
				drivers[1][length-i] = (float)-1;
				drivers[2][length-i] = (float)-1;
				drivers[3][length-i] = (float)-1;
				drivers[4][length-i] = (float)-1;
			}
			
		}
	}

	private String processSequence(String prot,int maxlength) {
		int protLength = prot.length();
		int repetition = (int) Math.ceil((maxlength/protLength));
		String procesProt = prot.concat(prot);
		for(int i = 1; i < repetition; i++) {
			procesProt = procesProt.concat(prot);
		}
		procesProt = procesProt.substring(0, maxlength);
		return procesProt;
	}
	
	private String processSequence2(String prot, int repetition) {
		String procesProt = prot.concat(prot);
		for(int i = 0; i < (repetition - 1); i++) {
			procesProt = procesProt.concat(prot);
		}
		return procesProt;
	}
	
	@Override
	public float appliedVoltage(int t, int iterator) {
		// TODO Auto-generated method stub
		return drivers[iterator][t];
	}




}
