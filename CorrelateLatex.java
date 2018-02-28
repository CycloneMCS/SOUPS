
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class CorrelateLatex {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// read a file conatining a list of .csv files for correlating results
		ArrayList<String> filenames = new ArrayList<>();
		
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String aFile = br.readLine();
		while (aFile != null) {
			filenames.add(aFile);
			System.out.println("Filename " + aFile + " added.");
			aFile = br.readLine();
		}
		br.close();

		HashSet<String> uniqueOrders = new HashSet<>();
		PrintWriter latexOut = new PrintWriter("CORRCHART.txt");
		latexOut.println("	\\begin{tabular}{|l|c|c|c|c|c|c|}");
		latexOut.println("\\hline");
		latexOut.println("MODEL & SOT & SOS & SOPS & SOUS & SOUPS & Unique Orders \\\\");
		latexOut.println("\\hline");
		
		// for each of those files, run the pearson correlations
		for (String filename : filenames) {
			String tempFile = "C:\\Users\\Ben\\OneDrive\\majorcsv\\" + filename;
			uniqueOrders.clear();
			System.out.println("Working on " + tempFile);
			HashMap<Integer, ArrayList<Double>> columns = new HashMap<>();
			for (int entry = 0; entry < 100; entry++) {
				columns.put(entry, new ArrayList<Double>());
			}
			
			br = new BufferedReader(new FileReader(tempFile + ".SOSRUNS.csv"));
			String line = br.readLine();// first line is column headers
			String[] headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				String orderHash = entries[entries.length - 1];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			br.close();
			
			br = new BufferedReader(new FileReader(tempFile + ".SOTRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				String orderHash = entries[entries.length - 1];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			br.close();
			
			br = new BufferedReader(new FileReader(tempFile + ".SOUPSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				String orderHash = entries[entries.length - 1];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			
			String latexyName = filename.replace("_", " ");
			latexyName = latexyName.replace(".pnml", "");
			latexOut.print(latexyName + " & ");
			for (int metric = 0; metric < 5; metric++) {
				double pearson = DoublePair.getPearson(columns.get(metric), columns.get(8));// 8 is the nodes created count
				String corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
			}
			latexOut.print(uniqueOrders.size());
			latexOut.print(" \\\\\n");
			

		}
		latexOut.println("\\hline");
		latexOut.println("\\end{tabular}");
		latexOut.flush();
		latexOut.close();
	}

}
