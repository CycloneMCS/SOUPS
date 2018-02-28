
import java.math.BigInteger;
import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureNode {

	int[] edgeIndex;
	PureNode[] child;

	private static PureNode omega;
	byte[] bigHash;

	public static PureTable theTable;
	public static Utilities theUtil;
	public static long preCacheFireCalls = 0l;
	public static long unionWithSubset = 0l;

	private PureNode(Map<Integer, PureNode> e) {

		edgeIndex = new int[e.size()];
		child = new PureNode[e.size()];

		TreeMap<Integer, PureNode> temp = new TreeMap<>(e);

		int index = 0;
		for (Integer i : temp.keySet()) {
			edgeIndex[index] = i;
			child[index] = temp.get(i);
			index++;
		}

		bigHash = theUtil.getPureNodeHashSorted(edgeIndex, child);
	}

	public static PureNode getNode(Map<Integer, PureNode> edges) {
		return theTable.checkIn(new PureNode(edges));
	}

	public PureNode getChild(int index) {
		int found = Arrays.binarySearch(this.edgeIndex, index);
		if (found >= 0) {
			return this.child[found];
		}
		return null;
	}

	public int[] getKeys() {
		return this.edgeIndex;
	}

	public static PureNode createInit(int[] init) {
		// only omega at level 0
		PureNode result = getOmega();
		HashMap<Integer, PureNode> nodeMap = new HashMap<Integer, PureNode>();
		for (int index = 1; index < init.length; index++) {
			nodeMap.put(init[index], result);
			PureNode temp = PureNode.getNode(nodeMap);
			result = temp;
			nodeMap.clear();
		}

		return result;
	}

	public static PureNode getOmega() {
		if (omega != null) {
			return omega;
		}

		PureNode result = theTable.checkIn(new PureNode(new HashMap<Integer, PureNode>()));
		omega = result;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(bigHash);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		PureNode other = (PureNode) obj;
		return Arrays.equals(bigHash, other.bigHash);
	}

	public static HashSet<Integer> combine(PureNode a, PureNode b) {
		HashSet<Integer> result = new HashSet<>();
		for (int i : a.getKeys()) {
			result.add(i);
		}
		for (int i : b.getKeys()) {
			result.add(i);
		}
		return result;
	}

	public static PureNode union(PureNode a, PureNode b) {
		if (a == null) {
			unionWithSubset++;
			return b;
		}
		if (b == null) {
			unionWithSubset++;
			return a;
		}
		if (a.equals(b)) {
			unionWithSubset++;
			return a;
		}

		PureNode cached = theTable.uCache.find(a, b);
		if (cached != null) {
			if (cached.equals(a) || cached.equals(b)) {
				unionWithSubset++;
			}
			return cached;
		}

		HashSet<Integer> keys = combine(a, b);

		HashMap<Integer, PureNode> resultMap = new HashMap<Integer, PureNode>();

		for (int k : keys) {
			PureNode temp = union(a.getChild(k), b.getChild(k));
			if (temp != null) {
				resultMap.put(k, temp);
			}
		}
		if (resultMap.isEmpty()) {
			return null;
		}
		PureNode result = PureNode.getNode(resultMap);
		if (result.equals(a) || result.equals(b)) {
			unionWithSubset++;
		}
		theTable.uCache.insert(a, b, result);

		return result;
	}

	public static PureNode buildLean(int level, PureNode p) {
		HashMap<Integer, PureNode> resultMap = new HashMap<Integer, PureNode>();
		if (level > 0) {
			for (int i : p.getKeys()) {
				PureNode res = buildLean(level - 1, p.getChild(i));
				if (res != null) {
					resultMap.put(i, res);
				}
			}
		}
		PureNode temp = PureNode.getNode(resultMap);
		resultMap = null;
		PureNode result = saturateLean(level, temp);

		return result;
	}

	public void fillUp(Map<Integer, PureNode> toFill) {
		for (int i = 0; i < edgeIndex.length; i++) {
			toFill.put(edgeIndex[i], child[i]);
		}
	}

	public static PureNode relProdSatLean(int level, PureNode old, PureTransition r) {
		preCacheFireCalls++;
		if (old == null) {
			return null;
		}
		if (level < r.bottom) {
			return old;
		}

		PureNode relID = r.getID(level);

		// check cache
		PureNode cached = theTable.RPSCache.find(old, relID);
		if (cached != null) {
			if (!cached.equals(PureNode.getOmega())) {
				return cached;// omega is standing in for results that should return empty set
			} else {
				return null;
			}
		}

		int adder = r.getEff(level);
		int constraint = r.getCons(level);

		HashMap<Integer, PureNode> resultMap = new HashMap<Integer, PureNode>();

		for (int x : old.getKeys()) {
			if (x >= constraint) {
				PureNode temp = relProdSatLean(level - 1, old.getChild(x), r);
				if (temp != null) {
					int target = x + adder;
					resultMap.put(target, union(temp, resultMap.get(target)));
				}
			}
		}
		if (resultMap.isEmpty()) {
			theTable.RPSCache.insert(old, relID, PureNode.getOmega());
			return null;
		}

		PureNode sat = saturateLean(level, PureNode.getNode(resultMap));

		// enter in cache
		theTable.RPSCache.insert(old, relID, sat);

		return sat;
	}

	public static PureNode saturateLean(int level, PureNode p) {
		if (level == 0) {
			return p;
		}

		// check cache
		PureNode cached = theTable.satCache.find(p);
		if (cached != null) {
			return cached;
		}

		HashMap<Integer, PureNode> resultMap = new HashMap<>();
		p.fillUp(resultMap);

		boolean changed = false;
		ArrayList<PureTransition> sortedTrans = PureTable.theEvents.get(level);
		TreeSet<Integer> sortedKeys = new TreeSet<Integer>(resultMap.keySet());
		ArrayDeque<Integer> childQueue = new ArrayDeque<Integer>();

		do {
			changed = false;

			for (PureTransition a : sortedTrans) {
				int constraint = a.getCons(level);
				int adder = a.getEff(level);

				// fill the queue (possibly in order specified by firing later)
				if (a.topEffect < 0) {
					childQueue.addAll(sortedKeys.descendingSet());
				} else {
					childQueue.addAll(sortedKeys);
				}

				while (!childQueue.isEmpty()) {
					int i = childQueue.removeFirst();
					if (i >= constraint) {
						int j = i + adder;
						PureNode temp = relProdSatLean(level - 1, resultMap.get(i), a);

						if (temp != null) {
							PureNode old = resultMap.get(j);
							PureNode unioned = union(old, temp);
							if (!unioned.equals(old)) {
								changed = true;
								resultMap.put(j, unioned);
								sortedKeys.add(j);
								childQueue.add(j);
							}
						}
					}// end constraint check
				}// end while
			}// end for
		} while (changed);
		if (resultMap.isEmpty()) {
			return null;
		}
		PureNode result = PureNode.getNode(resultMap);
		theTable.satCache.insert(p, result);

		return result;
	}

	public BigInteger countElements() {
		if (this.equals(getOmega())) {
			return BigInteger.ONE;
		}

		BigInteger cached = theTable.cCache.find(this);
		if (cached != null) {
			return cached;
		}

		BigInteger result = BigInteger.ZERO;

		for (int k : this.edgeIndex) {
			result = result.add(this.getChild(k).countElements());
		}

		theTable.cCache.insert(this, result);

		return result;
	}

	public long countNodes(HashSet<PureNode> counted) {
		if (counted.contains(this)) {
			return 0l;
		}

		long result = 1;
		counted.add(this);
		for (PureNode d : this.child) {
			result += d.countNodes(counted);
		}
		return result;
	}

	public long countEdges(HashSet<PureNode> counted) {
		if (counted.contains(this)) {
			return 0l;
		}

		long result = this.edgeIndex.length;
		counted.add(this);
		for (PureNode d : this.child) {
			result += d.countEdges(counted);
		}
		return result;
	}

	public void countNodesLevel(HashSet<PureNode> counted, long[] sums, int level) {
		if (!counted.contains(this)) {
			sums[level]++;
			counted.add(this);
			for (PureNode d : this.child) {
				d.countNodesLevel(counted, sums, level - 1);
			}
		}
	}

	public void countEdgesLevel(HashSet<PureNode> counted, long[] sums, int level) {
		if (!counted.contains(this)) {
			sums[level] += this.edgeIndex.length;
			counted.add(this);
			for (PureNode d : this.child) {
				d.countEdgesLevel(counted, sums, level - 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{nodeHash \t" + theUtil.hexBytes(bigHash) + "\nedges \t{\n");
		for (int k : this.edgeIndex) { // BS edges.keySet()) {
			sb.append("\t" + k + " \t" + theUtil.hexBytes(this.getChild(k).bigHash) + "\n");
		}
		sb.append("}}");
		return sb.toString();
	}
}
