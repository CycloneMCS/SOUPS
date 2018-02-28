
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Benjamin Smith bensmith@iastate.edu
 * @see <a href="http://orcid.org/0000-0003-2607-9338">My ORCID is: 0000-0003-2607-9338</a>
 *
 */
public class BigRegularPlot {
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
		PrintWriter latexOut = new PrintWriter("BIGREGULAR_PLUS-ALL5-NORM2.csv");
		latexOut.println("SOT,SOS,SOUPS,SOPS,SOUS,P+U,SOS - SOUS,NODES");
		HashMap<Integer, ArrayList<Double>> bigcolumns = new HashMap<>();
			for (int entry = 0; entry < 100; entry++) {
				bigcolumns.put(entry, new ArrayList<Double>());
			}
		
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
					double ups = Double.parseDouble(entries[2]) + Double.parseDouble(entries[3]);
					columns.get(21).add(ups);// sops + sous
					double su = Double.parseDouble(entries[1]) - Double.parseDouble(entries[3]);
					columns.get(22).add(su);// sos - sous
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
					double ups = Double.parseDouble(entries[2]) + Double.parseDouble(entries[3]);
					columns.get(21).add(ups);// sops + sous
					double su = Double.parseDouble(entries[1]) - Double.parseDouble(entries[3]);
					columns.get(22).add(su);// sos - sous
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			br.close();
			
			br = new BufferedReader(new FileReader(tempFile + ".SOPSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				if (entries.length < 20) break;
				String orderHash = entries[entries.length - 2];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					double ups = Double.parseDouble(entries[2]) + Double.parseDouble(entries[3]);
					columns.get(21).add(ups);// sops + sous
					double su = Double.parseDouble(entries[1]) - Double.parseDouble(entries[3]);
					columns.get(22).add(su);// sos - sous
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			br.close();
			
			br = new BufferedReader(new FileReader(tempFile + ".SOUSRUNS.csv"));
			line = br.readLine();// first line is column headers
			headers = line.split(",");
			
			line = br.readLine();// this is the actual first run line

			while (line != null) {
				String[] entries = line.split(",");
				if (entries.length < 20) break;
				String orderHash = entries[entries.length - 2];
				if (!uniqueOrders.contains(orderHash)) {
					for (int col = 0; col <= 20; col++) {
						columns.get(col).add(Double.parseDouble(entries[col]));
					}
					double ups = Double.parseDouble(entries[2]) + Double.parseDouble(entries[3]);
					columns.get(21).add(ups);// sops + sous
					double su = Double.parseDouble(entries[1]) - Double.parseDouble(entries[3]);
					columns.get(22).add(su);// sos - sous
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
					double ups = Double.parseDouble(entries[2]) + Double.parseDouble(entries[3]);
					columns.get(21).add(ups);// sops + sous
					double su = Double.parseDouble(entries[1]) - Double.parseDouble(entries[3]);
					columns.get(22).add(su);// sos - sous
					uniqueOrders.add(orderHash);
				}
				line = br.readLine();
			}
			
			String latexyName = filename.replace("_", " ");
			latexyName = latexyName.replace(".pnml", "");
			//latexOut.print(latexyName + " & ");
			//for (int metric = 0; metric < 5; metric++) {
				//double pearson = DoublePair.getPearson(columns.get(metric), columns.get(8));// 8 is the nodes created count
				double maxSOT = DoublePair.getHigh(columns.get(0));//DoublePair.getLowWhenLow(columns.get(0), columns.get(8));
				double maxSOS = DoublePair.getHigh(columns.get(1));
				double maxSOUPS = DoublePair.getHigh(columns.get(4));
				double maxNodes = DoublePair.getHigh(columns.get(8));
				double maxSOPS = DoublePair.getHigh(columns.get(2));
				double maxSOUS = DoublePair.getHigh(columns.get(3));
				double maxUPS = DoublePair.getHigh(columns.get(21));
				double maxSU = DoublePair.getHigh(columns.get(22));
				
				double minSOT = DoublePair.getLow(columns.get(0));//DoublePair.getLowWhenLow(columns.get(0), columns.get(8));
				double minSOS = DoublePair.getLow(columns.get(1));
				double minSOUPS = DoublePair.getLow(columns.get(4));
				double minNodes = DoublePair.getLow(columns.get(8));
				double minSOPS = DoublePair.getLow(columns.get(2));
				double minSOUS = DoublePair.getLow(columns.get(3));
				double minUPS = DoublePair.getLow(columns.get(21));
				double minSU = DoublePair.getLow(columns.get(22));
				
				//System.out.println("MAXSU is " + maxSU);
				
				for (int index = 0; index < columns.get(0).size(); index++) {
					//latexOut.print(corrOut + " & ");
					double sotreg = columns.get(0).get(index) / maxSOT;
					double sosreg = columns.get(1).get(index) / maxSOS;
					double soupsreg = columns.get(4).get(index) / maxSOUPS;
					
					double sopsreg = columns.get(2).get(index) / maxSOPS;
					double sousreg = columns.get(3).get(index) / maxSOUS;
					double upsreg = columns.get(21).get(index) / maxUPS;
					if (maxSU < .1) maxSU = 1.0;
					double sureg = columns.get(22).get(index) / maxSU;
					
					
					double nodesreg = columns.get(8).get(index) / maxNodes;
					latexOut.println(sotreg + "," + sosreg + "," + soupsreg + "," +  sopsreg + "," + sousreg + "," + upsreg + "," + sureg + "," + nodesreg);
					bigcolumns.get(0).add(sotreg);
					bigcolumns.get(1).add(sosreg);
					bigcolumns.get(2).add(sopsreg);
					bigcolumns.get(3).add(sousreg);
					bigcolumns.get(4).add(soupsreg);
					bigcolumns.get(8).add(nodesreg);
					bigcolumns.get(21).add(upsreg);
					bigcolumns.get(22).add(sureg);
				}
				
				
				
			//}
			
			

		}
		ArrayList<Double> shuffler = new ArrayList<Double>(bigcolumns.get(8));
		
		double pcor;
		int iter = 1000;
		double pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(1), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(1), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("sos corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(0), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(0), shuffler);
			if (Math.abs(tempCorr) > Math.abs(pcor)) pcount++;
		}
		System.out.println("sot corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(2), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(2), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("sops corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(3), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(3), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("sous corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(4), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(4), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("soups corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(21), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(21), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("so(u+p)s corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		pcount = 0.0;
		pcor = DoublePair.getPearson(bigcolumns.get(22), bigcolumns.get(8));
		for (int p = 0; p < iter; p++) {
			Collections.shuffle(shuffler);
			double tempCorr = DoublePair.getPearson(bigcolumns.get(22), shuffler);
			if (tempCorr > pcor) pcount++;
		}
		System.out.println("sos - sous corr = " + pcor + " \twith pvalue = " + (pcount / iter));
		
		
		latexOut.flush();
		latexOut.close();
		
		
	}

}
