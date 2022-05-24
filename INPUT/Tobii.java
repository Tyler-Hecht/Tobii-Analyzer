import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Tobii{
	ArrayList<ArrayList<Integer>> data;
	String[] dataNames;
	String fileLocation = "test4.csv";
	BufferedReader br;

	public static void main(String args[]){
		try{
			Tobii m = new Tobii(args);
		}
		catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}

	public Tobii(String [] args)throws Exception{
		if (args.length != 4) {
			System.out.println();
			System.out.println("ERROR! Command must be in the form: java Tobii a b c d");
			System.out.println("where");
			System.out.println(" 'a' is testing or training");
			System.out.println(" 'b' is input file name (eg: test.csv)");
			System.out.println(" 'c' is output file name (eg: out.txt)");
			System.out.println(" 'd' is onset delay in milliseconds (eg: 500)");
			System.out.println();
		} else {
			data = new ArrayList<ArrayList<Integer>>();
			dataNames = parseInput(data, args[1]);
			PrintStream stream = new PrintStream(args[2]);
			PrintStream console = System.out;
			System.setOut(stream);
			double ignore = Double.parseDouble(args[3]);

			int i = 1;
			System.out.println("Trial,Proportion,Frames,Time (ms)");
			while(i < data.size()){
				if (args[0].equals("testing")) {
					printPropWR(data.get(i), data.get(i+1), dataNames[i], dataNames[i+1], ignore);
					i = i+2;
				} else if (Objects.equals("training", args[0])){
					printPropPD(data.get(i), dataNames[i], 0.0);
					i++;
				} else if (Objects.equals("NPD", args[0])){
					int sil = dataNames[i].indexOf("silent");
					int noth = dataNames[i].indexOf("nothing");
					if (sil != -1){
						printPropWR(data.get(i), data.get(i+1), dataNames[i], dataNames[i+1], 0.0);
						i = i+2;
					} else if (dataNames[i].matches(".*\\d.*")){
						if (noth != -1){
							printPropPD(data.get(i), dataNames[i], ignore);
							i++;
						} else {
							printPropWR(data.get(i), data.get(i+1), dataNames[i], dataNames[i+1], ignore);
							i = i+2;
						}
					}
					else
						i++;
				}
				else {
					System.setOut(console);
					System.out.println("Error! Unrecognized task. Type either testing or training");
					i = data.size();
				}
			}

			System.setOut(console);
		}
	}

	String[] parseInput(ArrayList<ArrayList<Integer>> set, String fileLoc) throws Exception{
		br = new BufferedReader(new FileReader(fileLoc));
		String line = "";
		String csvSplitby = "\t";
		line = br.readLine();

		String[] attNames = line.split(csvSplitby);
		for(String s: attNames){
			set.add(new ArrayList<Integer>());
			s.length();
		}

		int commaonly = 0;
		while((line = br.readLine()) != null){
			//this section of code gets rid of the first column
			char[] chars = line.toCharArray();
			line = "";
			boolean passed = false;
			for(char c : chars) {
				//tabs appear past the row numbers
				if (c == '\t') {
					passed = true;
				}
				if(passed) {
					line += Character.toString(c);
				}
			}

			int count = 0;
			for(int i = 0; i < line.length(); i++){
				if (line.charAt(i) == '0' || line.charAt(i) == '1'){
					set.get(i+count).add(Character.getNumericValue(line.charAt(i)));
					count--;
				}
			}
		}
		br.close();
		return attNames;
	}

	void printPropWR(ArrayList<Integer> a, ArrayList<Integer> b, String aName, String bName, double ignore){
		if (a.size() != b.size())
			return;
		else {
			int acount = 0;
			int bcount = 0;
			int total = 0;
			int i = 0;

			i = (int) ((ignore / 1000) * 120);
			while(i < a.size()){
				if (!(a.get(i) == 0 && b.get(i) == 0)){
					total++;
					if (a.get(i) == 1)
						acount++;
					else if (b.get(i) == 1)
						bcount++;
				}
				i++;
			}


			double size = (double) total;
			double propA = acount/size;
			double propB = bcount/size;
			double milliA = 1000*acount/120.0;
			double milliB = 1000*bcount/120.0;
			System.out.println(aName + " , " + propA + " , " + acount + " , " + milliA);
			System.out.println(bName + " , " + propB + " , " + bcount + " , " + milliB + "\n");
			//System.out.println();
			//System.out.println();
		}
	}

	void printPropPD(ArrayList<Integer> a, String name, double ignore) {
		int count = 0;
		int count0 = 0;
		int i = 0;

		i = (int) ((ignore / 1000) * 120);
		while (i < a.size()) {
			if (a.get(i) == 1)
				count++;
			else if (a.get(i) == 0)
				count0++;
			i++;
		}

		double size = (double) count + count0;
		double prop = count / size;
		double milli = 1000*count / 120;
		if (size != 0)
			System.out.println(name + " , " + prop + " , " + count + " , " + milli + "\n");
		else
			System.out.println(name + " , " + 0.0 + " , " + 0 + " , " + 0.0 + "\n");

		//System.out.println();
	}


}