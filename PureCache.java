import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureCache {
	final int HARDSIZE = 1 << 23;
	final int HASHMASK = HARDSIZE - 1;
	final int BLOCKSIZE = Utilities.HASHBYTES;
	
	byte [] opBlocks;
	byte [] checkBlock;
	PureNode [] resBlocks;
	
	long seeks = 0l;
	long saves = 0l;
	long hits = 0l;
	
	public PureCache() {
		opBlocks = new byte [HARDSIZE * BLOCKSIZE];
		resBlocks = new PureNode[HARDSIZE];
		checkBlock = new byte[BLOCKSIZE];
	}
	
	public PureNode find(PureNode a) {
		int lookup = a.hashCode() & HASHMASK;
		int opIndex = lookup * BLOCKSIZE;
		seeks++;
		
		System.arraycopy(opBlocks, opIndex, checkBlock, 0, BLOCKSIZE);
		if (Arrays.equals(checkBlock, a.bigHash)) {
			hits++;
			return resBlocks[lookup];
		}
		
		return null;
	}
	
	public void insert(PureNode a, PureNode result) {
		int lookup = a.hashCode() & HASHMASK;
		int opIndex = lookup * BLOCKSIZE;
		saves++;
		
		System.arraycopy(a.bigHash, 0, opBlocks, opIndex, BLOCKSIZE);
		resBlocks[lookup] = result;
	}
	
	public void clear() {
		Arrays.fill(resBlocks, null);
		Arrays.fill(opBlocks, (byte)0);
		seeks = 0l;
		saves = 0l;
		hits = 0l;
	}
}
