
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class Utilities {

	static final int HASHBYTES = 20;
	static final int INDEXBYTES = 4;
	public static MessageDigest crypto;

	public Utilities() {
		try {
			crypto = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static byte[] getPureNodeHash(Map<Integer, PureNode> nodeMap) {
		int edgeCountBytes = nodeMap.size() * (HASHBYTES + INDEXBYTES);
		byte[] nodeBytes = new byte[edgeCountBytes];
		int current = 0;
		TreeSet<Integer> sortedKeys = new TreeSet<Integer>(nodeMap.keySet());

		for (int k : sortedKeys) {
			PureNode temp = nodeMap.get(k);

			for (int index = 0; index < HASHBYTES; index++) {
				nodeBytes[current] = temp.bigHash[index];
				current++;
			}

			nodeBytes[current] = (byte) (k & 255);
			nodeBytes[current + 1] = (byte) ((k >>> 8) & 255);
			nodeBytes[current + 2] = (byte) ((k >>> 16) & 255);
			nodeBytes[current + 3] = (byte) ((k >>> 24) & 255);
			current += INDEXBYTES;
		}
		return crypto.digest(nodeBytes);
	}

	public static byte[] getStringHash(String toHash) {
		byte[] byteString = toHash.getBytes();
		return crypto.digest(byteString);
	}

	public static byte[] getPureNodeHashSorted(int[] indexes, PureNode[] children) {
		int edgeCountBytes = indexes.length * (HASHBYTES + INDEXBYTES);
		byte[] nodeBytes = new byte[edgeCountBytes];
		int current = 0;

		for (int index = 0; index < indexes.length; index++) {
			PureNode temp = children[index];
			for (int x = 0; x < HASHBYTES; x++) {
				nodeBytes[current] = temp.bigHash[x];
				current++;
			}
			int k = indexes[index];
			nodeBytes[current] = (byte) (k & 255);
			nodeBytes[current + 1] = (byte) ((k >>> 8) & 255);
			nodeBytes[current + 2] = (byte) ((k >>> 16) & 255);
			nodeBytes[current + 3] = (byte) ((k >>> 24) & 255);
			current += INDEXBYTES;
		}
		return crypto.digest(nodeBytes);
	}

	public static String hexBytes(byte[] theBytes) {
		StringBuilder sb = new StringBuilder();
		String vals = "0123456789ABCDEF";

		for (byte b : theBytes) {
			int lower = b & 15;
			int upper = (b >> 4) & 15;
			sb.append(vals.substring(lower, lower + 1) + vals.substring(upper, upper + 1) + "-");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	public static ArrayList<PetriPlace64> breadthFirst(PetriModel64 theModel) {
		ArrayList<PetriPlace64> sorted = new ArrayList<PetriPlace64>(theModel.thePlaces);
		Collections.sort(sorted);

		ArrayList<PetriPlace64> result = new ArrayList<PetriPlace64>();
		HashSet<String> used = new HashSet<String>();
		HashMap<String, PetriPlace64> namePlace = new HashMap<String, PetriPlace64>();

		for (PetriPlace64 p : theModel.thePlaces) {
			namePlace.put(p.name, p);

		}

		HashMap<String, HashSet<String>> connections = new HashMap<String, HashSet<String>>();
		for (PetriPlace64 p : theModel.thePlaces) {
			connections.put(p.name, new HashSet<String>());
		}
		for (PetriTransition pt : theModel.theTrans) {
			connections.put(pt.id, new HashSet<String>());
		}
		for (PetriArc64 pa : theModel.theArcs) {
			connections.get(pa.source).add(pa.target);
		}

		ArrayDeque<String> theQueue = new ArrayDeque<String>();

		for (PetriPlace64 p : sorted) {
			theQueue.clear();	// this outer loop exists because some place(s) might not be connected
			if (!used.contains(p.name)) {
				result.add(p);
				used.add(p.name);

				theQueue.addLast(p.name);

				while (!theQueue.isEmpty()) {
					String c = theQueue.removeFirst();

					for (String t : connections.get(c)) {
						for (String n : connections.get(t)) {
							if (!used.contains(n)) {
								result.add(namePlace.get(n));
								theQueue.addLast(n);
								used.add(n);
							}
						}
					}
				}
			}
		}

		return result;
	}

	public static ArrayList<PetriPlace64> cuthill(PetriModel64 theModel) {
		ArrayList<PetriPlace64> sorted = new ArrayList<PetriPlace64>(theModel.thePlaces);
		Collections.sort(sorted);

		ArrayList<PetriPlace64> result = new ArrayList<PetriPlace64>();
		HashSet<String> used = new HashSet<String>();
		HashMap<String, PetriPlace64> namePlace = new HashMap<String, PetriPlace64>();

		for (PetriPlace64 p : theModel.thePlaces) {
			namePlace.put(p.name, p);
		}

		HashMap<String, HashSet<String>> connections = new HashMap<String, HashSet<String>>();
		for (PetriPlace64 p : theModel.thePlaces) {
			connections.put(p.name, new HashSet<String>());
		}
		for (PetriTransition pt : theModel.theTrans) {
			connections.put(pt.id, new HashSet<String>());
		}
		for (PetriArc64 pa : theModel.theArcs) {
			connections.get(pa.source).add(pa.target);
			connections.get(pa.target).add(pa.source);
		}

		ArrayDeque<String> theQueue = new ArrayDeque<String>();
		ArrayList<PetriPlace64> toSort = new ArrayList<>();

		HashSet<String> transSet = new HashSet<>();
		HashSet<String> tpSet = new HashSet<>();
		HashSet<String> searching = new HashSet<>();
		int index = 0;
		PetriPlace64 current = sorted.get(index);
		while (result.size() < theModel.getNumPlaces()) {
			while (used.contains(current.name)) {
				current = sorted.get(++index);
			}
			toSort.clear();
			result.add(current);
			used.add(current.name);
			searching.add(current.name);
			while (!searching.isEmpty()) {
				transSet.clear();
				for (String s : searching) {
					transSet.addAll(connections.get(s));
				}
				for (String t : transSet) {
					for (String p : connections.get(t)) {
						if (!used.contains(p)) {
							toSort.add(namePlace.get(p));
							used.add(p);
						}
					}
				}

				Collections.sort(toSort);
				searching.clear();
				for (PetriPlace64 pp : toSort) {
					result.add(pp);
					searching.add(pp.name);
				}
				toSort.clear();
			}
		}

		Collections.reverse(result);
		return result;
	}

	public static void arcSetupDegreeAsc(PetriModel64 theModel) {
		HashMap<String, PetriPlace64> byName = new HashMap<>();
		for (PetriPlace64 pp : theModel.thePlaces) {
			pp.sortScoreA = 0.0;
			pp.sortScoreB = 0.0;
			byName.put(pp.name, pp);
		}
		for (PetriArc64 pa : theModel.theArcs) {
			if (theModel.placeSet.contains(pa.source)) {
				// source is place
				byName.get(pa.source).sortScoreA += -1.0;
			} else {
				byName.get(pa.target).sortScoreA += -1.0;
			}
		}
	}

	public static void arcSetup(PetriModel64 theModel) {
		HashSet<String> placeSet = new HashSet<>();
		HashMap<String, Integer> levelByName = new HashMap<>();
		int lev = 1;
		HashMap<Integer, HashSet<ECPair>> uniques = new HashMap<>();
		for (PetriPlace64 pp : theModel.thePlaces) {
			placeSet.add(pp.name);
			levelByName.put(pp.name, lev);
			uniques.put(lev, new HashSet<ECPair>());
			lev++;
		}
		HashMap<String, TreeTransition> treeByName = new HashMap<>();
		int transCounter = 0;
		for (PetriTransition pt : theModel.theTrans) {
			TreeTransition temp = new TreeTransition(new TreeMap<Integer, ECPair>());
			temp.myNumber = transCounter;
			treeByName.put(pt.id, temp);
			transCounter++;
		}

		// Include all arcs
		for (PetriArc64 pa : theModel.theArcs) {
			if (placeSet.contains(pa.source)) {
				// source is place (constraint)
				lev = levelByName.get(pa.source);
				TreeTransition tempTrans = treeByName.get(pa.target);
				ECPair tempPair = tempTrans.arcs.get(lev);
				if (tempPair == null) {
					tempPair = new ECPair(-pa.cardinality, pa.cardinality);
				} else {
					tempPair = new ECPair(tempPair.effect - pa.cardinality, pa.cardinality);
				}
				tempTrans.arcs.put(lev, tempPair);
			} else {
				// source is transition (partial effect)
				lev = levelByName.get(pa.target);
				TreeTransition tempTrans = treeByName.get(pa.source);
				ECPair tempPair = tempTrans.arcs.get(lev);
				if (tempPair == null) {
					tempPair = new ECPair(pa.cardinality, 0l);
				} else {
					tempPair = new ECPair(tempPair.effect + pa.cardinality, tempPair.constraint);
				}
				tempTrans.arcs.put(lev, tempPair);
			}
		}
		for (String s : treeByName.keySet()) {
			TreeTransition temp = treeByName.get(s);
			for (int i : temp.arcs.keySet()) {
				ECPair ecTemp = temp.arcs.get(i);
				if (ecTemp.effect == 0l) {
					// non-productive
					theModel.thePlaces.get(i - 1).sortScoreA += 1.0;
				} else {
					// productive
					if (!uniques.get(i).contains(ecTemp)) {
						// unique
						uniques.get(i).add(ecTemp);
					} else {
						theModel.thePlaces.get(i - 1).sortScoreB += 1.0;
					}
				}
			}
		}
		// finalize the "SCORE"s
		for (PetriPlace64 pp : theModel.thePlaces) {
			double toSort = pp.sortScoreA + pp.sortScoreB;
			if (toSort > 0.0) {
				pp.sortScoreA = pp.sortScoreA / toSort;
				pp.sortScoreB = pp.sortScoreB / toSort;
			} else {
				pp.sortScoreA = 0.0;
				pp.sortScoreB = 0.0;
			}
		}

	}

	public static void psOrder(PetriModel64 theModel, ArrayList<String> order, String filename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(filename));
		
		/*
		pw.println("newpath");
		pw.println("0 0 moveto");
		pw.println("0 72 lineto");
		pw.println("72 72 lineto");
		pw.println("72 0 lineto");
		pw.println("closepath");
		pw.println(".5 setgray");
		pw.println("stroke showpage");
		*/
		
		
		// get information to output
		HashMap<String, Integer> tops = new HashMap<>();
		HashMap<String, Integer> bots = new HashMap<>();
		HashMap<String, Integer> toLevel = new HashMap<>();
		HashMap<String, HashSet<PetriArc64>> byLevel = new HashMap<>();
		HashMap<String, HashSet<PetriArc64>> byTrans = new HashMap<>();
		int l = 1;
		for (String s : order) {
			toLevel.put(s, l);
			l++;
			byLevel.put(s, new HashSet<PetriArc64>());
		}
		HashMap<Integer, Long> initials = new HashMap<>();
		for (PetriPlace64 pp : theModel.thePlaces) {
			initials.put(toLevel.get(pp.name), pp.marking);
		}
		for (PetriTransition pt : theModel.theTrans) {
			tops.put(pt.id, 0);
			bots.put(pt.id, theModel.getNumPlaces());
			byTrans.put(pt.id, new HashSet<PetriArc64>());
		}
		for (PetriArc64 pa : theModel.theArcs) {
			if (theModel.placeSet.contains(pa.source)) {
				// source is place
				byLevel.get(pa.source).add(pa);
				byTrans.get(pa.target).add(pa);
			
				int level = toLevel.get(pa.source);
				tops.put(pa.target, Math.max(tops.get(pa.target), level));
				bots.put(pa.target, Math.min(bots.get(pa.target), level));
			} else {
				byLevel.get(pa.target).add(pa);
				byTrans.get(pa.source).add(pa);
				int level = toLevel.get(pa.target);
				tops.put(pa.source, Math.max(tops.get(pa.source), level));
				bots.put(pa.source, Math.min(bots.get(pa.source), level));
			}
		}
		int paperW = (theModel.getNumTrans() * (18 + 6)) + 1;// 6 is num digits initial?
		int paperH = (theModel.getNumPlaces() * 18) + 1;
		pw.println("<< /PageSize [" + paperW + " " + paperH + "] >> setpagedevice");
		int squareSize = 18;//(72 * 8) / Math.max(theModel.getNumTrans(), theModel.getNumPlaces());
		int gridSizeW = squareSize * (theModel.getNumTrans() + 1);
		int gridSizeH = squareSize * theModel.getNumPlaces();
		pw.println("newpath .8 setgray");
		pw.println("0 " + squareSize + " " + gridSizeW + " {0 moveto 0 " + gridSizeH + " rlineto stroke} for");
		pw.println("0 " + squareSize + " " + gridSizeH + " {0 exch moveto " + gridSizeW + " 0 rlineto stroke} for");
		
		int left = 0;
		ArrayList<SortTrans> sortedTrans = new ArrayList<>();
		for (PetriTransition pt : theModel.theTrans) {
			sortedTrans.add(new SortTrans(bots.get(pt.id), tops.get(pt.id), pt.id));
		}
		Collections.sort(sortedTrans);
		//Collections.reverse(sortedTrans);
		
		
		HashSet<MetaNode> counter = new HashSet<>();
		for (SortTrans st : sortedTrans) {
			
			HashMap<Integer, MetaNode> inTrans = new HashMap<>();
			for (PetriArc64 pa : byTrans.get(st.name)) {
				if (pa.source.equals(st.name)) {
					// source is transition (effect arc)
					int lev = toLevel.get(pa.target);
					if (!inTrans.containsKey(lev)) {
						MetaNode add = new MetaNode(0, (int) pa.cardinality,null);
						inTrans.put(lev, add);
					} else {
						MetaNode mod = inTrans.get(lev);
						mod.effect = (int) (pa.cardinality) - mod.constraint;
						mod.reCalcHash();
					}
				} else {
					// source is place (constraint arc)
					int lev = toLevel.get(pa.source);
					if (!inTrans.containsKey(lev)) {
						MetaNode add = new MetaNode((int) pa.cardinality,0 - (int) pa.cardinality,null);
						inTrans.put(lev, add);
					} else {
						MetaNode mod = inTrans.get(lev);
						mod.constraint = (int) pa.cardinality;
						mod.effect = mod.effect - (int)pa.cardinality;
						mod.reCalcHash();
					}
				}
			}
			MetaNode currNode = null;
			boolean seenEffect = false;
			boolean seenUnique = false;
			int startEffect = 1;
			int startUnique = 1;
			for (int lev = 1; lev <= tops.get(st.name); lev++) {
				// recalc and add metanodes
				MetaNode toAdd = inTrans.get(lev);
				if (toAdd != null) {
					toAdd.child = currNode;
					toAdd.reCalcHash();
				} else {
					toAdd = new MetaNode(0,0,currNode);
				}
				currNode = toAdd;
				if (toAdd.effect != 0) {
					if (!seenEffect) startEffect = lev;
					seenEffect = true;
				}
				if (seenEffect) {
					if (!counter.contains(toAdd)) {
						//score += 1.0;
						if (!seenUnique) startUnique = lev;
						seenUnique = true;
					}
				}
				if (!seenUnique) {
					startUnique = st.top;//Math.max(startUnique, startEffect);
				}
				counter.add(toAdd);
				
			}
			//System.out.println("BOT " + st.bot + " \tEFF " + startEffect + " \tUNIQUE " + startUnique);
			
			// color no effects (bot to top)
			pw.println("newpath 1 1 .25 setrgbcolor");
			int right = left + squareSize;
			int bot = (st.bot - 1) * squareSize;//(bots.get(pt.id) - 1) * squareSize;
			int top = st.top * squareSize;//(tops.get(pt.id)) * squareSize;
			int up = top - bot;
			pw.println(left + " " + bot + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + up + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("fill stroke");
			pw.println("newpath 0 setgray");
			pw.println(left + " " + bot + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + up + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("stroke");
			
			// color non-unique
			pw.println("newpath .75 .75 1 setrgbcolor");
			
			int botNU = (startEffect - 1) * squareSize;//(bots.get(pt.id) - 1) * squareSize;
			int topNU = startUnique * squareSize;//(tops.get(pt.id)) * squareSize;
			int upNU = topNU - botNU;
			pw.println(left + " " + botNU + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + upNU + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("fill stroke");
			pw.println("newpath 0 setgray");
			pw.println(left + " " + botNU + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + upNU + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("stroke");
			
			// color unique
			pw.println("newpath 1 .25 .25 setrgbcolor");
			
			int botU = (startUnique - 1) * squareSize;//(bots.get(pt.id) - 1) * squareSize;
			int topU = st.top * squareSize;//(tops.get(pt.id)) * squareSize;
			int upU = topU - botU;
			pw.println(left + " " + botU + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + upU + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("fill stroke");
			pw.println("newpath 0 setgray");
			pw.println(left + " " + botU + " moveto");
			pw.println(squareSize + " 0 rlineto");
			pw.println("0 " + upU + " rlineto");
			pw.println("-" + squareSize + " 0 rlineto closepath");
			pw.println("stroke");
			
			// draw emblems
			for (PetriArc64 pa : byTrans.get(st.name)) {
				int ll = 0;
				boolean down = false;
				if (toLevel.containsKey(pa.source)) {
					// source is place
					down = true;
					ll = toLevel.get(pa.source);
				}
				else ll = toLevel.get(pa.target);
				ll = (ll - 1) * squareSize;
				
				pw.println("newpath 0 setgray");
				pw.println(left + " " + ll + " moveto ");
				pw.println(squareSize + " 0 rlineto");
				pw.println("0 " + squareSize + " rlineto");
				pw.println("-" + squareSize + " 0 rlineto");
				pw.println("closepath stroke");
				pw.println("newpath 0 setgray");
				pw.println(left + " " + (ll + (squareSize / 2)) + " moveto");
				if (!down) {
				pw.println((squareSize / 2) + " " + (squareSize / 2) + " rlineto");
				pw.println((squareSize / 2) + " -" + (squareSize / 2) + " rlineto");
				} else {
					pw.println((squareSize / 2) + " -" + (squareSize / 2) + " rlineto");
					pw.println((squareSize / 2) + " " + (squareSize / 2) + " rlineto");
				}
				pw.println("stroke");
				pw.println("newpath 0 setgray");
				pw.println((left + (squareSize / 2)) + " " + ll + " moveto 0 " + squareSize + " rlineto stroke");
				pw.println("/Courier findfont");
				pw.println("8 scalefont setfont");
				if (!down) {
					pw.println((left + (squareSize / 4)) + " " + (ll + (squareSize / 3) + 1) + " moveto (" + pa.cardinality + ") show");
				} else {
					pw.println((left + (squareSize / 2) + 1) + " " + (ll + (squareSize / 3) + 1) + " moveto (" + pa.cardinality + ") show");
				}
			}
			
			left += squareSize;
			
		}
		for (int i = 0; i < theModel.getNumPlaces(); i++) {
			pw.println("newpath 0 setgray");
			pw.println((left + (squareSize / 3)) + " " + ((i * squareSize) + (squareSize / 3)) + " moveto 0 ");
			pw.println("/Courier findfont");
			pw.println("14 scalefont setfont");
			pw.println("(" + initials.get(i + 1) + ") show");
		}
		
		
		pw.println("showpage");
		
		pw.flush();
		pw.close();
	}
	
}



