
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
public class FileMultiplier {
	public static void main (String [] args) throws FileNotFoundException, IOException {
		// read one file and write a bunch of indexed copies
		String inputFile = args[0];
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		ArrayList<PrintWriter> outs = new ArrayList<>();
		
		for (int index = 1; index <= 100; index++) {
			outs.add(new PrintWriter(inputFile + "." + index + ".COPY.pnml"));
		}
		
		String line = br.readLine();
		
		while (line != null) {
			for (PrintWriter pw : outs) {
				pw.println(line);
			}
			line = br.readLine();
		}
		for (PrintWriter pw : outs) {
			pw.flush();
			pw.close();
		}
		
	}
}
