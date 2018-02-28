 import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class DoublePair implements Comparable {
	double ranker;
	double value;

	public DoublePair(double r, double v) {
		ranker = r;
		value = v;
	}
	
	@Override
	public int compareTo(Object o) {
		DoublePair other = (DoublePair)o;
		if (this.value < other.value) return -1;
		if (this.value > other.value) return 1;
		return 0;
	}
	
	public static double getAverage(ArrayList<Double> toAV) {
		double result = 0.0;
		for (double d : toAV) {
			result += d;
		}
		result /= (double) toAV.size();
		return result;
	}
	
	public static double getLow(ArrayList<Double> toLow) {
		double result = Double.POSITIVE_INFINITY;
		for (double d : toLow) {
			result = Math.min(result, d);
		}
		return result;
	}
	
	public static double getHigh(ArrayList<Double> toHigh) {
		double result = 0.0;
		for (double d : toHigh) {
			result = Math.max(result, d);
		}
		return result;
	}
	
	public static double getAveWhenLow(ArrayList<Double> toLow, ArrayList<Double> toAV) {
		double theLow = getLow(toLow);
		double sum = 0.0;
		double count = 0.0;
		for (int index = 0; index < toLow.size(); index++) {
			if (toLow.get(index) <= theLow) {
				sum += toAV.get(index);
				count += 1.0;
			}
		}
		return sum / count;
	}
	
	public static double getCountWhenLow(ArrayList<Double> toLow) {
		double theLow = getLow(toLow);
		double count = 0.0;
		for (int index = 0; index < toLow.size(); index++) {
			if (toLow.get(index) <= theLow) {
				count += 1.0;
			}
		}
		return count;
	}
	
	public static double getLowWhenLow(ArrayList<Double> toLow, ArrayList<Double> toAV) {
		double theLow = getLow(toLow);
		double result = Double.POSITIVE_INFINITY;
		for (int index = 0; index < toLow.size(); index++) {
			if (toLow.get(index) <= theLow) {
				result = Math.min(result, toAV.get(index));
			}
		}
		return result;
	}
	
	public static double getHighWhenLow(ArrayList<Double> toLow, ArrayList<Double> toAV) {
		double theLow = getLow(toLow);
		double result = 0.0;
		for (int index = 0; index < toLow.size(); index++) {
			if (toLow.get(index) <= theLow) {
				result = Math.max(result, toAV.get(index));
			}
		}
		return result;
	}
	
	
	public static ArrayList<DoublePair> getPairs(ArrayList<Double> vals) {
		ArrayList<DoublePair> result = new ArrayList<>();
		int r = 1;
		for (double d : vals) {
			result.add(new DoublePair(r++, d));
		}
		Collections.sort(result);
		
		return result;
	}
	
	public static double sumDiffProd(ArrayList<Double> x, ArrayList<Double> y) {
		double aveX = getAverage(x);
		double aveY = getAverage(y);
		double result = 0.0;
		for (int index = 0; index < x.size(); index++) {
			result += (x.get(index) - aveX) * (y.get(index) - aveY);
		}
		return result;
	}
	
	public static double sumSqrDiff(ArrayList<Double> x) {
		return sumDiffProd(x, x);
	}
	
	public static double getPearson(ArrayList<Double> x, ArrayList<Double> y) {
		double result = sumDiffProd(x, y);
		double bot = Math.sqrt(sumSqrDiff(x)) * Math.sqrt(sumSqrDiff(y));
		result /= bot;
		return result;
	}
	
	
	public static double getDecision(ArrayList<Double> x, ArrayList<Double> y) {
		double right = 0.0;
		double wrong = 0.0;
		double tie = 0.0;
		for (int a = 0; a < x.size(); a++) {
			double aa = x.get(a);
			double bb = y.get(a);
			for (int b = 0; b < y.size(); b++) {
				double cc = x.get(b);
				double dd = y.get(b);
				if (aa < cc) {
					if (bb < dd) right += 1.0;
					else wrong += 1.0;
				} else if (aa > cc) {
					if (bb > dd) right += 1.0;
					else wrong += 1.0;
				} else tie += 1.0;
			}
		}
		return right / (right + wrong + tie);
	}
	
	public static void main(String [] args) {
		ArrayList<Double> tests = new ArrayList<>();
		tests.add(.01);
		tests.add(.02);
		tests.add(.03);
		tests.add(.04);
		tests.add(.05);
		tests.add(.06);
		System.out.println(DoublePair.getAverage(tests));
		
		ArrayList<Double> tests2 = new ArrayList<>();
		tests2.add(.5201);
		tests2.add(.4202);
		tests2.add(.3203);
		tests2.add(.2204);
		tests2.add(.2205);
		tests2.add(.1206);
		System.out.println(DoublePair.getAverage(tests2));
		
		System.out.println(DoublePair.sumSqrDiff(tests));
		System.out.println(DoublePair.getPearson(tests, tests));
		System.out.println(DoublePair.getPearson(tests, tests2));
		System.out.println(DoublePair.getPearson(tests2, tests));
		System.out.println(DoublePair.getPearson(tests2, tests2));
		
	}
}
