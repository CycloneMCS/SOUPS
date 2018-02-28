
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureGen {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String filename = args[0];
		String csvOut = filename + "." + "SOUPSRUNS" + ".csv";// or whatever name
		long seed = System.nanoTime();
		Random arand = new Random(seed);
		
		PrintWriter pw = new PrintWriter(new File(csvOut));
		StringBuilder outputLine = new StringBuilder();
		outputLine.append("bestSOT");
		outputLine.append("," + "bestSOS");
		outputLine.append("," + "bestSOPS");
		outputLine.append("," + "bestSOUS");
		outputLine.append("," + "bestSOUPS");
		outputLine.append("," + "numNodes");
		outputLine.append("," + "numEdges");
		outputLine.append("," + "inSECONDS");
		outputLine.append("," + "ut.createdCount");
		outputLine.append("," + "ut.createdEdges");
		outputLine.append("," + "ut.uCache.seeks");
		outputLine.append("," + "ut.uCache.hits");
		outputLine.append("," + "ut.uCache.saves");
		outputLine.append("," + "ut.satCache.seeks");
		outputLine.append("," + "ut.satCache.hits");
		outputLine.append("," + "ut.satCache.saves");
		outputLine.append("," + "ut.RPSCache.seeks");
		outputLine.append("," + "ut.RPSCache.hits");
		outputLine.append("," + "ut.RPSCache.saves");
		outputLine.append("," + "PureNode.preCacheFireCalls");
		outputLine.append("," + "PureNode.unionWithSubset");
		outputLine.append("," + "recordString");
		pw.println(outputLine.toString());
		
		for (int BIGRUNS = 0; BIGRUNS < 100; BIGRUNS++) {
			seed = System.nanoTime();
			arand = new Random(seed);
			double [] combo = new double[5];
			Arrays.fill(combo, 0.0);
			
						// Select here the preferred metric (default = 0)
						combo[0] = 1.0;// soups
						//combo[1] = 1.0;// sops
						//combo[2] = 1.0;// sous
						//combo[3] = 1.0;// sos
						//combo[4] = 1.0;// sot
		PureTable ut = new PureTable();
		PureNode.theUtil = new Utilities();
		PureNode.theTable = ut;
		PureNode myOmega = PureNode.getOmega();
		
		// load a REAL model
		System.out.println("\nStarting timer:");
		long startTime = System.nanoTime();
		
		PetriModel64 theModel = FastPNML64.getModel64(filename);
		
		
		// Create the structure for grouping transitions by level
		
		ut.theEvents.clear();
		for (int numLevels = 1; numLevels <= theModel.getNumPlaces(); numLevels++) {
			ArrayList<PureTransition> simple = new ArrayList<PureTransition>();
			ut.theEvents.put(numLevels, simple);
		}
		
		// Compute an order by various means
		ArrayList<PetriPlace64> given = new ArrayList<>(theModel.thePlaces);
		
		HashSet<String> placeSet = new HashSet<>();
		for (PetriPlace64 pp : given) {
			placeSet.add(pp.name);
			//System.out.println(pp.name);
		}
		
		ArrayList<PetriPlace64> order = new ArrayList<>(given);
		//Utilities.arcSetup(theModel);// experimental
		
		//order = Utilities.cuthill(theModel);
		Collections.shuffle(order, arand);
		//Collections.sort(order);
		//Collections.reverse(order);
		
		ArrayList<String> orderOut = new ArrayList<>();
		for (PetriPlace64 pp : order) orderOut.add(pp.name);
		
		// To find an order, create the tree transitions
		HashMap<String, TreeTransition> treeByName = new HashMap<>();
		int transCounter = 0;
		for (PetriTransition pt : theModel.theTrans) {
			TreeTransition temp = new TreeTransition(new TreeMap<Integer, ECPair>());
			temp.myNumber = transCounter;
			treeByName.put(pt.id, temp);
			transCounter++;
		}
		HashMap<String, Integer> levelByName = new HashMap<>();
		int lev = 1;
		for (PetriPlace64 pp : order) {
			levelByName.put(pp.name, lev);
			lev++;
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
				tempTrans.arcs.put(lev,  tempPair);
			}
		}
		//System.out.println("ALL THOSE TRANSITIONS:");
		
		PlaceVector.toProject = new double[treeByName.size()];
		Arrays.fill(PlaceVector.toProject, 0.0);
		for (PetriPlace64 pp : theModel.thePlaces) {
		}
		for (String s : treeByName.keySet()) {
			treeByName.get(s).doSwap(0, 0);
			TreeTransition tt = treeByName.get(s);
			for (int i : tt.arcs.keySet()) {
				ECPair ec = tt.arcs.get(i);
				if (ec.effect != 0) {
					// add this to the location for place i
				}
			}
		}
		
		
		
		// Search for a better order
		TreeMap<Integer, PetriPlace64> placeTree = new TreeMap<>();
		lev = 1;
		for (PetriPlace64 pp : order) {
			placeTree.put(lev, pp);
			lev++;
		}
		
		TreeMap<Integer, PetriPlace64> bestOrder = new TreeMap<>(placeTree);
		double bestScore = Double.POSITIVE_INFINITY;//Integer.MAX_VALUE;
		double prevScore = bestScore;
		int bestSOT = Integer.MAX_VALUE;
		int bestSOS = Integer.MAX_VALUE;
		int bestSOUS = Integer.MAX_VALUE;
		int bestSOPS = Integer.MAX_VALUE;
		int bestSOUPS = Integer.MAX_VALUE;
		
		double SCORE = 0.0;
		
		
		int toughCount = 0;
		int maxiter = 50000;	// 50000 iterations of Simulated annealing
		for (int iter = 0; iter < maxiter; iter++) {
			int sos = 0;
			int sops = 0;
			int sot = 0;
			
			
			ArrayList<TreeTransition> toUnique = new ArrayList<>(treeByName.values());
			Collections.sort(toUnique);
			int sous = toUnique.get(0).getSpan();
			sos = sous;
			sops = toUnique.get(0).getProductiveSpan();
			int soups = sops;
			sot = toUnique.get(0).top;
			int x = 1;
			for (x = 1; x < toUnique.size(); x++) {
				TreeTransition tt = toUnique.get(x);
				int unSpan = tt.getUniqueSpan(toUnique.get(x - 1));
				sous += unSpan;
				int prodSpan = tt.getProductiveSpan();
				
				
				sot += tt.top;
				sos += tt.getSpan();
				sops += prodSpan;
				soups += Math.min(unSpan, prodSpan);
				SCORE = soups * combo[0];
				SCORE += sops * combo[1];
				SCORE += sous * combo[2];
				SCORE += sos * combo[3];
				SCORE +=  sot * combo[4];
				if (SCORE > prevScore) break;
			}
			double enChange = (((double) (toUnique.size() - x)) / (double)toUnique.size());
			
			if (SCORE < bestScore) {//((soups < bestScore) || ((soups == bestScore) && (sos < bestSOS))) {// various tiebreakers possible
				bestOrder = new TreeMap(placeTree);
				bestSOS = sos;//(sops == bestScore) ? sos : bestSOS;
				bestSOPS = sops;
				bestSOUPS = soups;
				bestSOUS = sous;
				bestSOT = sot;
				SCORE = soups * combo[0];
				SCORE += sops * combo[1];
				SCORE += sous * combo[2];
				SCORE += sos * combo[3];
				SCORE +=  sot * combo[4];
				bestScore = SCORE;//soups;
				
				System.out.println("AT ITER " + iter + " NEW BEST:: SOT " + sot + "  SOS " + sos + " HAS SOPS " + sops + "  HAS SOUS " + sous + " HAS SOUPS " + soups + " WITH SCORE " + SCORE);
				toughCount = 0;
			} else {
				toughCount++;
				double temperature = (double)(iter) / (double)(maxiter);
				
				if ((arand.nextDouble() < Math.exp(-enChange * temperature))) {//placeTree.size() )) {
					placeTree = new TreeMap(bestOrder);
					resetTreeTrans(bestOrder, treeByName, theModel);
					toughCount = 0;
					SCORE = bestScore;
				}
				
			}
			prevScore = SCORE;// * (1.0 / (1.0 - enChange));
			prevScore = Math.max(prevScore, bestScore);// preserve early breakout invariant
			
			int from = arand.nextInt(placeTree.size()) + 1;
			int to = arand.nextInt(placeTree.size()) + 1;
			
			PetriPlace64 tempPlace = placeTree.get(from);
			placeTree.put(from, placeTree.get(to));
			placeTree.put(to, tempPlace);
			for (TreeTransition tt : treeByName.values()) {
				tt.doSwap(from, to);
			}
			
			
			//*/
		}
		order.clear();
		StringBuilder runHash = new StringBuilder();
		runHash.append(filename);
		for (int l : bestOrder.keySet()) {
			order.add(bestOrder.get(l));
			runHash.append("," + bestOrder.get(l).name);
		}
		
		byte [] theRunID = Utilities.getStringHash(runHash.toString());
		System.out.println("RUNID: " + Utilities.hexBytes(theRunID));
		String recordString = runHash.toString() + "," + Utilities.hexBytes(theRunID);
		
		
		orderOut = new ArrayList<>();
		for (PetriPlace64 pp : order) orderOut.add(pp.name);
		//Utilities.psOrder(theModel, orderOut, filename + "." + Utilities.hexBytes(theRunID) + ".ANNEAL_CM.ps");
		
		// output the order stats, in case it doesn't finish
		outputLine = new StringBuilder();
		outputLine.append(bestSOT);
		outputLine.append("," + bestSOS);
		outputLine.append("," + bestSOPS);
		outputLine.append("," + bestSOUS);
		outputLine.append("," + bestSOUPS);
		pw.print(outputLine.toString());
		pw.flush();
		
		
		// Start timer for generation
		long genStart = System.nanoTime();
		
		// prepare everything for generation 
		HashMap<String, Integer> placeMap = new HashMap<String, Integer>();
		HashMap<String, Integer> transMap = new HashMap<String, Integer>();
		lev = 1;
		for (PetriPlace64 p : order) {
			placeMap.put(p.name, lev);
			lev++;
		}
		int TID = 0;
		for (PetriTransition pt : theModel.theTrans) {
			transMap.put(pt.id, TID);
			TID++;
		}
		
		// Create the transitions
		HashMap<String, PureTransition> holder = new HashMap<>();
		for (PetriTransition pt : theModel.theTrans) {
			PureTransition tx = new PureTransition("NONAME");
			holder.put(pt.id, tx);
		}
		for (PetriArc64 pa : theModel.theArcs) {
			if (placeSet.contains(pa.source)) {
				// source is place
				int lev2 = placeMap.get(pa.source);
				holder.get(pa.target).addCons(lev2, (int) pa.cardinality);
				holder.get(pa.target).addEff(lev2, (int) -pa.cardinality);
			} else {
				// source is transition, target is place
				int lev2 = placeMap.get(pa.target);
				holder.get(pa.source).addEff(lev2, (int) pa.cardinality);
			}
		}
		for (PureTransition at : holder.values()) {
			at.reIdentify();
			ut.theEvents.get(at.top).add(at);
		}

		ut.RPSCache.clear();
		ut.satCache.clear();
		ut.uCache.clear();
		
		// sort the events, group by level
		for (int e : ut.theEvents.keySet()) {
			Collections.sort(ut.theEvents.get(e));
		}
		
		//System.out.println(ut.createdCount + " nodes created for TRANSITION STRUCTURE (APPROX. 3x Unique Lower Arc Patterns)");
		
		ut.resetCount();		// Don't count the "implicit forest" as created nodes
		
		// set up initial state
		int[] modelInit = new int[theModel.getNumPlaces() + 1];
		for (PetriPlace64 p : theModel.thePlaces) {
			int placeLevel = placeMap.get(p.name);

			modelInit[placeLevel] = (int) p.marking;
		}
		PureNode petriInit = PureNode.createInit(modelInit);

		System.out.println("\nPetri Model init:\n" + petriInit);
		
		PureNode bigOne = PureNode.buildLean(theModel.getNumPlaces(), petriInit);// big saturation call!!
		
		long endTime = System.nanoTime();
		
		long gentime = (endTime - genStart);
		System.out.println("Completed in " + (endTime - startTime) + " nanoseconds.");
		double inSECONDS = gentime * .000000001;
		System.out.println("Generation took " + gentime + " nanoseconds. (" + inSECONDS + " seconds)");
		System.out.println(ut.createdCount + " nodes were created");
		
		long numNodes = bigOne.countNodes(new HashSet<>());
		long numEdges = bigOne.countEdges(new HashSet<>());
		System.out.println("Final Nodes " + numNodes);
		System.out.println("Final Edges " + numEdges);
		//BigInteger stateCount = bigOne.countElements();// no state count for these experiments
		//System.out.println("State Count is: " + stateCount);
		
		outputLine = new StringBuilder();
		outputLine.append("," + numNodes);
		outputLine.append("," + numEdges);
		outputLine.append("," + inSECONDS);
		outputLine.append("," + ut.createdCount);
		outputLine.append("," + ut.createdEdges);
		outputLine.append("," + ut.uCache.seeks);
		outputLine.append("," + ut.uCache.hits);
		outputLine.append("," + ut.uCache.saves);
		outputLine.append("," + ut.satCache.seeks);
		outputLine.append("," + ut.satCache.hits);
		outputLine.append("," + ut.satCache.saves);
		outputLine.append("," + ut.RPSCache.seeks);
		outputLine.append("," + ut.RPSCache.hits);
		outputLine.append("," + ut.RPSCache.saves);
		outputLine.append("," + PureNode.preCacheFireCalls);
		outputLine.append("," + PureNode.unionWithSubset);
		outputLine.append("," + recordString);
		
		outputLine.append("," + seed);// record initial seed to reproduce results later
		
		System.out.println(outputLine.toString());
		
		pw.println(outputLine.toString());
		pw.flush();
		
		//Utilities.psOrder(theModel, orderOut, filename + ".ps");
		
		}// end bigruns
		pw.close();
	}

	public static void resetTreeTrans(TreeMap<Integer, PetriPlace64> theOrder, HashMap<String, TreeTransition> treeByName, PetriModel64 theModel) {
		treeByName.clear();
		HashSet<String> placeSet = new HashSet<>();
		for (PetriTransition pt : theModel.theTrans) {
			treeByName.put(pt.id, new TreeTransition(new TreeMap<Integer, ECPair>()));
		}
		HashMap<String, Integer> levelByName = new HashMap<>();
		for (int lev = 1; lev <= theModel.getNumPlaces(); lev++) {
			levelByName.put(theOrder.get(lev).name, lev);
			placeSet.add(theOrder.get(lev).name);
		}
		
		// Include all arcs
		for (PetriArc64 pa : theModel.theArcs) {
			if (placeSet.contains(pa.source)) {
				// source is place (constraint)
				int lev = levelByName.get(pa.source);
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
				int lev = levelByName.get(pa.target);
				TreeTransition tempTrans = treeByName.get(pa.source);
				ECPair tempPair = tempTrans.arcs.get(lev);
				if (tempPair == null) {
					tempPair = new ECPair(pa.cardinality, 0l);
				} else {
					tempPair = new ECPair(tempPair.effect + pa.cardinality, tempPair.constraint);
				}
				tempTrans.arcs.put(lev,  tempPair);
			}
		}
	}
	
}
