 import java.util.*;
 import java.math.BigInteger;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class UnaryOpPureCache {
	final int HARDSIZE = 1 << 23;
	final int HASHMASK = HARDSIZE - 1;
	final int BLOCKSIZE = Utilities.HASHBYTES;
	
	byte [] opBlocks;
	byte [] checkBlock;
	BigInteger [] resBlocks;
	
	public UnaryOpPureCache() {
		opBlocks = new byte [HARDSIZE * BLOCKSIZE];
		resBlocks = new BigInteger[HARDSIZE];
		checkBlock = new byte[BLOCKSIZE];
	}
	
	public BigInteger find(PureNode a) {
		int lookup = a.hashCode() & HASHMASK;
		int opIndex = lookup * BLOCKSIZE;
		
		System.arraycopy(opBlocks, opIndex, checkBlock, 0, BLOCKSIZE);
		if (Arrays.equals(checkBlock, a.bigHash)) {
			return resBlocks[lookup];
		}
		
		return null;
	}

	
	public void insert(PureNode a, BigInteger result) {
		int lookup = a.hashCode() & HASHMASK;
		int opIndex = lookup * BLOCKSIZE;
		
		System.arraycopy(a.bigHash, 0, opBlocks, opIndex, BLOCKSIZE);
		resBlocks[lookup] = result;
	}
}
