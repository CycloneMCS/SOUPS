import java.util.*;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureTable {
	WeakHashMap<PureNode, WeakReference<PureNode>> theTable;
	
	PureCacheU uCache;
	PureCacheU iCache;
	
	PureCache satCache;
	PureCache2 RPSCache;
	
	UnaryOpPureCache cCache;
	public MessageDigest crypto;
	
	long createdCount = 0l;
	long createdEdges = 0l;
	
	public static HashMap<Integer, ArrayList<PureTransition>> theEvents;	// to be grouped by "top"
	
	public PureTable() {
        theTable = new WeakHashMap<>();// classic BS

		uCache = new PureCacheU();
		iCache = new PureCacheU();
		satCache = new PureCache();
		RPSCache = new PureCache2();
	
		cCache = new UnaryOpPureCache();
		
		try {
			crypto = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		theEvents = new HashMap<Integer, ArrayList<PureTransition>>();
	}
	
	public PureNode checkIn(PureNode aNode) {
		if (theTable.containsKey(aNode)) return theTable.get(aNode).get();
		theTable.put(aNode, new WeakReference<PureNode>(aNode));
		createdCount++;
		createdEdges += aNode.bigHash.length;
		return aNode;
	}
	
	public PureNode checkInLevel(PureNode aNode, int level) {
		if (theTable.containsKey(aNode)) return theTable.get(aNode).get();
		theTable.put(aNode, new WeakReference<PureNode>(aNode));
		return aNode;
	}
	
	public void resetCount() {
		createdCount = 0l;
		createdEdges = 0l;
		uCache.clear();
		satCache.clear();
		RPSCache.clear();
		PureNode.preCacheFireCalls = 0l;
		PureNode.unionWithSubset = 0l;
	}
}
