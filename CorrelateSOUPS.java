
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
public class CorrelateSOUPS {
	public static void main(String [] args) throws FileNotFoundException, IOException {
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
		
		// for each of those files, run the pearson correlations
		for (String filename : filenames) {
			System.out.println("Working on " + filename);
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();// first line is column headers
			String [] headers = line.split(",");
			line = br.readLine();
			
			HashMap<Integer, ArrayList<Double>> columns = new HashMap<>();
			for (int entry = 0; entry < 100; entry++) {
				columns.put(entry, new ArrayList<Double>());
			}
			while (line != null) {
				String [] entries = line.split(",");
				for (int col = 0; col <= 20; col++) {
					columns.get(col).add(Double.parseDouble(entries[col]));
				}
				line = br.readLine();
			}
			
			PrintWriter pwPear = new PrintWriter(new File(filename + ".pearson.csv"));
			PrintWriter pwRank = new PrintWriter(new File(filename + ".rank.csv"));
			pwPear.print(" ,");// first column is row names (metric)
			pwRank.print(" ,");
			for (int obs = 5; obs <= 20; obs++) {
				// print column headers at top
				pwPear.print(headers[obs] + ",");// first column is row names (metric)
				pwRank.print(headers[obs] + ",");
			}
			pwPear.print("\n");// first row done
			pwRank.print("\n");
			for (int metric = 0; metric < 5; metric++) {
				pwPear.print(headers[metric] + ",");// first column is row names (metric)
				pwRank.print(headers[metric] + ",");
				for (int obs = 5; obs <= 20; obs++) {
					double pearson = DoublePair.getPearson(columns.get(metric), columns.get(obs));
					//double rank = DoublePair.getRankCorr(columns.get(metric), columns.get(obs));
					pwPear.print(pearson + ",");
					//pwRank.print(rank + ",");
				}
				pwPear.print("\n");// row done
				pwRank.print("\n");
			}
			pwPear.flush();
			pwRank.flush();
			pwPear.close();
			pwRank.close();
		}
	}
}
