import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class ECPair implements Comparable {
    final long effect;
    final long constraint;

    public ECPair (long aEffect, long aConstraint) {
        effect = aEffect;
        constraint = aConstraint;
    }


    @Override
    public boolean equals(Object o) {
    	ECPair ecPair = (ECPair) o;

    	if (effect != ecPair.effect) return false;
    	return constraint == ecPair.constraint;
    }

    @Override
    public int hashCode() {
    	int result = (int) (effect ^ (effect >>> 32));
    	result = 31 * result + (int) (constraint ^ (constraint >>> 32));
    	return result;
    }

    @Override
    public int compareTo(Object o) {
        ECPair other = (ECPair)o;
        if (other == null) return -1;
        if (this.effect < other.effect) {
            return -1;
        } else if (this.effect > other.effect) return 1;

        if (this.constraint < other.constraint) {
            return -1;
        } else if (this.constraint > other.constraint) return 1;
        return 0;
    }
    
    @Override
    public String toString() {
    		return "EC(" + effect + " " + constraint + ")";
    }
}
