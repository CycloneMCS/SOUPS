import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class SortTrans implements Comparable {
	int bot;
	int top;
	String theCol;
	String name;
	
	public SortTrans(String col) {
		bot = col.length() - 1;
		top = 0;
		theCol = col;
		
		for (int index = 0; index < col.length(); index++) {
			if ((col.charAt(index) == 'X') || (col.charAt(index) == '#') || (col.charAt(index) == '%')) {
				bot = Math.min(col.length() - index, bot);
				top = Math.max(col.length() - index, top);
			}
		}
	}

	public SortTrans(int b, int t, String n) {
		bot = b;
		top = t;
		name = n;
	}
	
	@Override
	public int compareTo(Object o) {
		// sort by top, then by bot if tied
		SortTrans other = (SortTrans)o;
		if (this.top < other.top) {
			return -1;
		} else if (this.top > other.top) {
			return 1;
		}
		if (this.bot < other.bot) {
			return -1;
		} else  if (this.bot > other.bot) {
			return 1;
		}
		return 0;
	}

}
