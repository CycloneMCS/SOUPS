import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class PureCache2 {
	final int HARDSIZE = 1 << 24;
	final int HASHMASK = HARDSIZE - 1;
	final int BLOCKSIZE = Utilities.HASHBYTES * 2;
	final int HALFBLOCK = Utilities.HASHBYTES;
	
	byte [] opBlocks;
	byte [] checkBlock;
	byte [] checkBlock2;
	PureNode [] resBlocks;
	
	long seeks = 0l;
	long saves = 0l;
	long hits = 0l;
	
	public PureCache2() {
		opBlocks = new byte [HARDSIZE * BLOCKSIZE];
		resBlocks = new PureNode[HARDSIZE];
		checkBlock = new byte[BLOCKSIZE];
		checkBlock2 = new byte[BLOCKSIZE];
	}
	
	public PureNode find(PureNode a, PureNode b) {
		int lookup = (a.hashCode() * b.hashCode() ) & HASHMASK;
		int opIndex = lookup * BLOCKSIZE;
		seeks++;
		
		System.arraycopy(opBlocks, opIndex, checkBlock, 0, BLOCKSIZE);
		System.arraycopy(a.bigHash, 0, checkBlock2, 0, HALFBLOCK);
		System.arraycopy(b.bigHash, 0, checkBlock2, HALFBLOCK, HALFBLOCK);
		if (Arrays.equals(checkBlock, checkBlock2)) {
			hits++;
			return resBlocks[lookup];
		}
		
		return null;
	}
	
	public void insert(PureNode a, PureNode b, PureNode result) {
		int lookup = (a.hashCode() * b.hashCode()) & HASHMASK;
		saves++;
		int opIndex = lookup * BLOCKSIZE;
		
		System.arraycopy(a.bigHash, 0, opBlocks, opIndex, HALFBLOCK);
		System.arraycopy(b.bigHash, 0, opBlocks, opIndex + HALFBLOCK, HALFBLOCK);
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
