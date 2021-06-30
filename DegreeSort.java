package candys;

import java.util.Comparator;

public class DegreeSort implements Comparator<Vnode> {

	@Override
	public int compare(Vnode node1, Vnode node2) {
		// TODO Auto-generated method stub
		int degree1 = node1.degree();
		int degree2 = node2.degree();
		return degree2 - degree1;
	}

}
