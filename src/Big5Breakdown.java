
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */public class Big5Breakdown {
	
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
		PrintWriter latexOut = new PrintWriter("BIG5BREAKDOWN.txt");
		//latexOut.println("	\\begin{tabular}{|l|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|c|}");
		latexOut.println("	\\begin{tabular}{|l|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|r|}");
		latexOut.println("\\hline");
		latexOut.println("\\multirow{2}{*}{Model} & \\multirow{2}{*}{Ord} &");
		//latexOut.println("Model & Orders & ");
		latexOut.println("\\multicolumn{5}{c|}{SOT} &");
		latexOut.println("\\multicolumn{5}{c|}{SOS} &");
		latexOut.println("\\multicolumn{5}{c|}{SOPS} &");
		latexOut.println("\\multicolumn{5}{c|}{SOUS} &");
		latexOut.println("\\multicolumn{5}{c|}{SOUPS} \\\\");
		latexOut.println("\\cline{3-27}");
		latexOut.println(" & & \\multicolumn{1}{c|}{$R$} & \\multicolumn{1}{c|}{Min} & \\multicolumn{1}{c|}{Avg} & \\multicolumn{1}{c|}{Max} & \\multicolumn{1}{c|}{Tie} ");
		latexOut.println(" & \\multicolumn{1}{c|}{$R$} & \\multicolumn{1}{c|}{Min} & \\multicolumn{1}{c|}{Avg} & \\multicolumn{1}{c|}{Max} & \\multicolumn{1}{c|}{Tie} ");
		latexOut.println(" & \\multicolumn{1}{c|}{$R$} & \\multicolumn{1}{c|}{Min} & \\multicolumn{1}{c|}{Avg} & \\multicolumn{1}{c|}{Max} & \\multicolumn{1}{c|}{Tie} ");
		latexOut.println(" & \\multicolumn{1}{c|}{$R$} & \\multicolumn{1}{c|}{Min} & \\multicolumn{1}{c|}{Avg} & \\multicolumn{1}{c|}{Max} & \\multicolumn{1}{c|}{Tie} ");
		latexOut.println(" & \\multicolumn{1}{c|}{$R$} & \\multicolumn{1}{c|}{Min} & \\multicolumn{1}{c|}{Avg} & \\multicolumn{1}{c|}{Max} & \\multicolumn{1}{c|}{Tie} \\\\");
		//latexOut.println(" & & R & Min & Avg & Max & L & R & Min & Avg & Max & L & R & Min & Avg & Max & L \\\\");
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
			
			br = new BufferedReader(new FileReader(tempFile + ".SOPSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				if (entries.length > 20) {
				String orderHash = entries[entries.length - 1];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					uniqueOrders.add(orderHash);
				}
				}
				line = br.readLine();
			}
			
			br = new BufferedReader(new FileReader(tempFile + ".SOUSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				if (entries.length > 20) {
				String orderHash = entries[entries.length - 1];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					uniqueOrders.add(orderHash);
				}
				}
				line = br.readLine();
			}
			
			String latexyName = filename.replace("_", " ");
			latexyName = latexyName.replace(".pnml", "");
			latexOut.print(latexyName + " & " + uniqueOrders.size() + " & ");
			//for (int metric = 0; metric < 5; metric++) {
				double pearson = DoublePair.getPearson(columns.get(0), columns.get(8));// 8 is the nodes created count
				String corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
				
				double value = DoublePair.getLowWhenLow(columns.get(0), columns.get(8));
				corrOut = Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getAveWhenLow(columns.get(0), columns.get(8));
				corrOut = Integer.toString((int)Math.ceil(value));
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getHighWhenLow(columns.get(0), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getCountWhenLow(columns.get(0));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				
				pearson = DoublePair.getPearson(columns.get(1), columns.get(8));// 8 is the nodes created count
				corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getLowWhenLow(columns.get(1), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getAveWhenLow(columns.get(1), columns.get(8));
				corrOut = Integer.toString((int)Math.ceil(value));
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getHighWhenLow(columns.get(1), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getCountWhenLow(columns.get(1));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				
				pearson = DoublePair.getPearson(columns.get(2), columns.get(8));// 8 is the nodes created count
				corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getLowWhenLow(columns.get(2), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getAveWhenLow(columns.get(2), columns.get(8));
				corrOut = Integer.toString((int)Math.ceil(value));
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getHighWhenLow(columns.get(2), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getCountWhenLow(columns.get(2));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				
				pearson = DoublePair.getPearson(columns.get(3), columns.get(8));// 8 is the nodes created count
				corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getLowWhenLow(columns.get(3), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getAveWhenLow(columns.get(3), columns.get(8));
				corrOut = Integer.toString((int)Math.ceil(value));
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getHighWhenLow(columns.get(3), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getCountWhenLow(columns.get(3));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				
				pearson = DoublePair.getPearson(columns.get(4), columns.get(8));// 8 is the nodes created count
				corrOut = Double.toString(pearson);
				if (pearson < 0.0) {
					corrOut = corrOut.substring(0, 5);
				} else {
					corrOut = corrOut.substring(0, 4);
				}
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getLowWhenLow(columns.get(4), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getAveWhenLow(columns.get(4), columns.get(8));
				corrOut = Integer.toString((int)Math.ceil(value));
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getHighWhenLow(columns.get(4), columns.get(8));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut + " & ");
				
				value = DoublePair.getCountWhenLow(columns.get(4));
				corrOut =  Double.toString(value).replace(".0", "");
				latexOut.print(corrOut);
			//}
			
			latexOut.print(" \\\\\n");
			

		}
		latexOut.println("\\hline");
		latexOut.println("\\end{tabular}");
		latexOut.flush();
		latexOut.close();
	}

}
