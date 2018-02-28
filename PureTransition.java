import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureTransition implements Comparable {

	int top;
	int bottom;
	int topEffect;
	
	String name;
	
	TreeMap<Integer, Integer> constraints;
	TreeMap<Integer, Integer> effects;
	TreeMap<Integer, PureNode> theIds;
	
	public PureTransition(String aName) {
		constraints = new TreeMap<>();
		effects = new TreeMap<>();
		theIds = new TreeMap<>();
		
		name = aName;
		
		top = 0;
		bottom = Integer.MAX_VALUE;
		topEffect = 0;
		
	}
	
	public PureNode getID(int index) {
		return theIds.floorEntry(index).getValue();
	}
	
	
	public void addCons(int level, int value) {
		top = Math.max(top, level);
		bottom = Math.min(bottom, level);
		
		constraints.put(level, value);
	}
	
	public void addEff(int level, int value) {
		top = Math.max(top, level);
		bottom = Math.min(bottom, level);
		
		if (effects.containsKey(level)) {
			int old = effects.get(level);
			effects.put(level, old + value);
		} else {
			effects.put(level, value);
		}
		topEffect = effects.get(top);
	}
	
	public int getCons(int index) {
		if (!constraints.containsKey(index)) return 0;
		return constraints.get(index);
	}
	
	public int getEff(int index) {
		if (!effects.containsKey(index)) return 0;
		return effects.get(index);
	}
	
	public void reIdentify() {
		theIds.clear();
		
		TreeSet<Integer> arced = new TreeSet<>();	// to contain all levels with an arc
		arced.addAll(constraints.keySet());
		arced.addAll(effects.keySet());
		
		PureNode current = PureNode.getOmega();
		
		HashMap<Integer, PureNode> temp = new HashMap<>();
		
		for (int level : arced) {
			int aCon = getCons(level);
			int aEff = getEff(level);
			temp.clear();
			temp.put(aCon, current);
			current = PureNode.getNode(temp);
			temp.clear();
			temp.put(aEff, current);
			current = PureNode.getNode(temp);
			temp.clear();
			temp.put(level, current);
			current = PureNode.getNode(temp);
			this.theIds.put(level, current);
		}
		
	}
	
	
	@Override
	public int compareTo(Object o) {
		PureTransition other = (PureTransition)o;
		
		if (this.bottom < other.bottom) {
			return 1;// lower bottoms first?
		} else if (this.bottom > other.bottom) return -1;
		if (this.topEffect < other.topEffect) {
			return -1;
		} else if (this.topEffect > other.topEffect) return 1;
		return 0;
	}

}
