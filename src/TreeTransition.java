import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class TreeTransition implements Comparable {
	int top;
	int bottom;
	
	int myNumber;

	TreeMap<Integer, ECPair> arcs;

	public TreeTransition(Map<Integer, ECPair> someArcs) {
		arcs = new TreeMap<>(someArcs);
		if (arcs.size() > 0) {
			top = arcs.lastKey();
			bottom = arcs.firstKey();
		} else {
			top = Integer.MAX_VALUE;
			bottom = 0;
		}
	}

	public int getSpan() {
		return (top - bottom) + 1;
	}

	public int getProductiveSpan() {
		int firstProd = 0;
		for (int i : arcs.keySet()) {
			ECPair current = arcs.get(i);
			if (current.effect != 0) {
				firstProd = i;
				break;
			}
			if (i == top) return 0;		// totally non-productive case
		}
		return (top - firstProd) + 1;
	}
	
	public int getUniqueSpan(TreeTransition previous) {
		if (this.bottom != previous.bottom) return (top - bottom) + 1;
		
		int current = this.bottom;
		
		do {
			int otherIndex = previous.arcs.floorKey(current);
			int thisIndex = arcs.floorKey(current);
			if (thisIndex < otherIndex) {
				return (top - current) + 1;//(top - thisIndex) + 1;//(t-C)+1?
			}
			if (thisIndex > otherIndex) {
				return (top - otherIndex);
			}
			if (!this.arcs.get(current).equals(previous.arcs.get(current))) {
				return (top - current) + 1;
			}
			Integer next = this.arcs.higherKey(current);
			Integer oNext = previous.arcs.higherKey(current);
			if (next == null) return 0;
			if (oNext == null) return (top - current);
			current = Math.min(next, oNext);
		} while (true);
	}
	
	public void doSwap(int a, int b) {
		ECPair tempA = arcs.remove(a);
		ECPair tempB = arcs.remove(b);
		if (tempA != null) arcs.put(b, tempA);
		if (tempB != null) arcs.put(a, tempB);
		top = arcs.lastKey();
		bottom = arcs.firstKey();
	}


	@Override
	public int compareTo(Object o) {
		TreeTransition other = (TreeTransition)o;
		if (this.bottom < other.bottom) {
			return -1;
		} else if (this.bottom > other.bottom) return 1;

		TreeSet<Integer> levels = new TreeSet<>(this.arcs.keySet());
		levels.addAll(other.arcs.keySet());
		
		for (int i : levels) {
			ECPair mine = arcs.get(i);
			if (mine == null) return 1;
			ECPair theirs = other.arcs.get(i);
			int comp = mine.compareTo(theirs);
			if (comp != 0) return comp;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("TREETRANSITION: (");
		
		for (int level : this.arcs.keySet()) {
			result.append(level + ": " + this.arcs.get(level) + "   ");
		}
		
		result.append(")");
		
		return result.toString();
	}
}