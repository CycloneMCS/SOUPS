 import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class MetaNode implements Comparable {
	int constraint;
	int effect;
	MetaNode child;
	
	byte [] bigHash;
	
	public static Utilities theUtil;
	
	public MetaNode(int con, int eff, MetaNode aChild) {
		constraint = con;
		effect = eff;
		child = aChild;
		reCalcHash();
	}
	
	public void reCalcHash() {
		byte [] toHash = new byte[(2 * Utilities.INDEXBYTES) + Utilities.HASHBYTES];
		Arrays.fill(toHash, (byte)0);
		if (child != null) {
			for (int index = 0; index < Utilities.HASHBYTES; index++) {
				toHash[index] = child.bigHash[index];
			}
		}
			toHash[Utilities.HASHBYTES] = (byte)(constraint & 255);
			toHash[Utilities.HASHBYTES + 1] = (byte)((constraint >>> 8) & 255);
			toHash[Utilities.HASHBYTES + 2] = (byte)((constraint >>> 16) & 255);
			toHash[Utilities.HASHBYTES + 3] = (byte)((constraint >>> 24) & 255);
			toHash[Utilities.HASHBYTES + 4] = (byte)(effect & 255);
			toHash[Utilities.HASHBYTES + 5] = (byte)((effect >>> 8) & 255);
			toHash[Utilities.HASHBYTES + 6] = (byte)((effect >>> 16) & 255);
			toHash[Utilities.HASHBYTES + 7] = (byte)((effect >>> 24) & 255);
		
		bigHash = Utilities.crypto.digest(toHash);
	}
	
	public String toString() {
		String result = "METANODE C:" + constraint + " \tE:" + effect + " \tHASH:" + Integer.toHexString(this.hashCode());//Arrays.toString(bigHash);
		return result;
	}
	
	
	@Override
	public int hashCode() {
		int result = Arrays.hashCode(bigHash);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		MetaNode other = (MetaNode) obj;
		return Arrays.equals(bigHash, other.bigHash);
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
