
import java.io.BufferedReader;
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
public class MetricBreakdown {
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
		PrintWriter latexOut = new PrintWriter("BreakdownCHART-Nodes-created.txt");
		int col = 8;// nodes created column
		latexOut.println("	\\begin{tabular}{|l|c|c|c|c|c|c|c|c|c|}");
		latexOut.println("\\hline");
		latexOut.println("MODEL & SOT LOW & AVE & HIGH & SOS LOW & AVE & HIGH &  SOUPS LOW & AVE & HIGH & \\\\");
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
			
			br = new BufferedReader(new FileReader(tempFile + ".SOTRUNS.csv"));
			String line = br.readLine();// first line is column headers
			String[] headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				String orderHash = entries[entries.length - 1];
				if (true){//(!uniqueOrders.contains(orderHash)) {
					//for (int col = 0; col <= 20; col++) {
					//int col = 7;// Time
					int colSOT = 0;// SOT will be 0
						columns.get(colSOT).add(Double.parseDouble(entries[col]));
					//}
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			br.close();
			
			br = new BufferedReader(new FileReader(tempFile + ".SOSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				String orderHash = entries[entries.length - 1];
				if (true){//(!uniqueOrders.contains(orderHash)) {
					//for (int col = 0; col <= 20; col++) {
					//int col = 7;// Time
					int colSOS = 1;// SOS will be 1
						columns.get(colSOS).add(Double.parseDouble(entries[col]));
					//}
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
				if (true){//(!uniqueOrders.contains(orderHash)) {
					//for (int col = 0; col <= 20; col++) {
					//int col = 7;// Time
					int colSOUPS = 2;// SOUPS will be 2
						columns.get(colSOUPS).add(Double.parseDouble(entries[col]));
					//}
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			
			String latexyName = filename.replace("_", " ");
			latexyName = latexyName.replace(".pnml", "");
			latexOut.print(latexyName + " & ");
			//for (int metric = 0; metric < 5; metric++) {
				double sotLow = DoublePair.getLow(columns.get(0));
				double sotAve = DoublePair.getAverage(columns.get(0));
				double sotHigh = DoublePair.getHigh(columns.get(0));
				double sosLow = DoublePair.getLow(columns.get(1));
				double sosAve = DoublePair.getAverage(columns.get(1));
				double sosHigh = DoublePair.getHigh(columns.get(1));
				double soupsLow = DoublePair.getLow(columns.get(2));
				double soupsAve = DoublePair.getAverage(columns.get(2));
				double soupsHigh = DoublePair.getHigh(columns.get(2));
				//double pearson = DoublePair.getPearson(columns.get(metric), columns.get(8));// 8 is the nodes created count
				String numOut = Integer.toString((int)(sotLow));//Double.toString(sotLow);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(sotAve));//Double.toString(sotAve);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(sotHigh));//Double.toString(sotHigh);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				
				numOut = Integer.toString((int)(sosLow));//Double.toString(sotLow);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(sosAve));//Double.toString(sotAve);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(sosHigh));//Double.toString(sotHigh);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				
				numOut = Integer.toString((int)(soupsLow));//Double.toString(sotLow);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(soupsAve));//Double.toString(sotAve);//.substring(0,4);
				latexOut.print(numOut + " & ");
				
				numOut = Integer.toString((int)(soupsHigh));//Double.toString(sotHigh);//.substring(0,4);
				latexOut.print(numOut);
				
			//}
			//latexOut.print(uniqueOrders.size());
			latexOut.print(" \\\\\n");
			
		}
		latexOut.println("\\hline");
		latexOut.println("\\end{tabular}");
		latexOut.flush();
		latexOut.close();
	}

}
