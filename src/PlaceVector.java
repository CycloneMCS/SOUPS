import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PlaceVector implements Comparable {
	public static double [] toProject;
	public final PetriPlace64 myPlace;
	HashMap<Integer, Double> myVector;
	double myLocation;
	
	public PlaceVector(PetriPlace64 pp) {
		myPlace = pp;
		myVector = new HashMap<>();
	}

	public void relocate() {
		double result = 0.0;
		for (int i : myVector.keySet()) {
			result += myVector.get(i) * toProject[i];
		}
		myLocation = result;
	}
	
	
	@Override
	public int compareTo(Object arg0) {
		// compare using the projected value
		PlaceVector other = (PlaceVector)arg0;
		if (this.myLocation < other.myLocation) return -1;
		if (this.myLocation > other.myLocation) return 1;
		return 0;
	}
}
