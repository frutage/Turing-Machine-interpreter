//package turing;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class turing {
	String tape;
	Set<String> states;
	Set<Character> inputSyms;
	Set<Character> tapeSyms;
	int middleOfTape;
	int head;
	String stateCur;
	String state0;
	final String empty = "_";
	HashSet<String> fstates;
	enum direction {LEFT, RIGHT, HOLD};
	
	private class Tuple {
		public String newState;
		public Character oldSym;
		public Character newSym;
	    public direction dir;
	    public Tuple() {}
	    public Tuple(String[] str) {
	    	this.newState = str[4];
	    	this.oldSym = str[1].charAt(0);
	    	this.newSym = str[2].charAt(0);
	    	if (str[3].equals("l"))
	    		this.dir = direction.LEFT;
	    	else if (str[3].equals("r"))
	    		this.dir = direction.RIGHT;
	    	else
	    		this.dir = direction.HOLD;
	    }
	}
	
	Map<String, ArrayList<Tuple>> transition = new HashMap<>();
	FileInputStream finTM;
	FileInputStream fin;
	FileOutputStream fopC;
	FileOutputStream fopR;
	
	turing(String path) {
		try {
			finTM = new FileInputStream(path + "/test.tm");
			fin = new FileInputStream(path + "/input.txt");
			fopC = new FileOutputStream(path + "/console.txt");
			fopR = new FileOutputStream(path + "/result.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void turingParser() {
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(finTM));
			String line = "";
			// State sets
			while (buffer.ready()) {
				int i, j;
				line = buffer.readLine();
				if (line.startsWith("#Q")) {
					// states
					i = 0;
					j = line.length() - 1;
					while (line.charAt(j) != '}') j--;
					while (line.charAt(i) != '{') i++;
					line = line.substring(i+1, j);
					states = new HashSet<String>(Arrays.asList(line.split(",")));
//					System.out.println(states.toString());
				} else if (line.startsWith("#S")) {
					// input symbols
					i = 0;
					j = line.length() - 1;
					while (line.charAt(j) != '}') j--;
					while (line.charAt(i) != '{') i++;				
					inputSyms = new HashSet<>();
					for (++i; i < j; i += 2) {
						inputSyms.add(line.charAt(i));
					}
					
				} else if (line.startsWith("#q0")) {
					// start state
					state0 = "";
					for (int k = 6; k < line.length(); k++) {
						if (line.charAt(k) == ';' || line.charAt(k) == ' ')
							break;
						state0 += line.charAt(k);
					}
				} else if (line.startsWith("#F")) {
					// final states
					i = 0; 
					j = line.length() - 1;
					while (line.charAt(j) != '}') j--;
					while (line.charAt(i) != '{') i++;
					line = line.substring(i+1, j);
					fstates = new HashSet<String>(Arrays.asList(line.split(",")));
				} else if (!line.equals("") && !line.startsWith("#") && !line.startsWith(";")) {
					// transitions
					String[] tmp = line.split(" ");
					if (transition.containsKey(tmp[0]))
					{
						transition.get(tmp[0]).add(new Tuple(tmp));
					} else {
						ArrayList<Tuple> list = new ArrayList<>();
						list.add(new Tuple(tmp));
						transition.put(tmp[0], list);
					}
				}
			}
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void turingSimulator() {
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(fin));
			OutputStreamWriter writerC = new OutputStreamWriter(fopC, "UTF-8");
			ArrayList<Integer> resultInfo = new ArrayList<>();;
			String input = "";
			while (buffer.ready()) {
				input = buffer.readLine();
				writerC.append("Input: " + input + "\n");
				writerC.append("==================== ");
				if (!isLegal(input)) {
					writerC.append("ERR ====================\n");
					writerC.append("The input \"" + input + "\" is illegal\n");
					writerC.append("==================== END ====================\n");
					resultInfo.add(0);
				} else {
					writerC.append("RUN ====================\n");
					if(input.isEmpty())
						tape = "_";
					else 
						tape = input;
					stateCur = state0;
					head = 0;
					middleOfTape = 0;
					int cnt = 0;
					boolean accepted = false;
					while (true) {
						if (fstates.contains(stateCur)) {
							accepted = true;
							resultInfo.add(1);
							printProcess(cnt, writerC);
							break;
						} else if(!SingleExecutor(cnt++, writerC)) {
							break;
						}
					}
					String result = "";
					int i = 0, j = tape.length() - 1;
					for (; i < tape.length(); i++) {
						if (tape.charAt(i) != '_')
							break;
					}
					for (; j >= i; j--) {
						if (tape.charAt(j) != '_')
							break;
					}
					for (int k = i; k <= j; k++)
						result += tape.charAt(k);

					writerC.append("Result: " + result + "\n");
					writerC.append("==================== END ====================\n");
					if (!accepted) {
						resultInfo.add(2);
					}
				}
			}
			buffer.close();
			writerC.close();
			OutputStreamWriter writerR = new OutputStreamWriter(fopR, "UTF-8");
			for (int t = 0; t < resultInfo.size(); t++) {
				switch(resultInfo.get(t)) {
					case 0: writerR.append("Error\n"); break;
					case 1: writerR.append("True\n"); break;
					case 2: writerR.append("False\n"); break;
					default: System.out.println("Unexpected result = " + resultInfo);
				}
			}
			writerR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isLegal(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (!inputSyms.contains(input.charAt(i)))
				return false;
		}
		return true;
	}
	
	private void printProcess(int step, OutputStreamWriter writer) {
		try {
			writer.append("Step  : " + step + "\n");
			int i = 0, j = tape.length() - 1;
			int headIndex = middleOfTape + head;
			for (; i < tape.length(); i++) {
				if (tape.charAt(i) != '_')
					break;
			}
			int left, right;
			if (i == tape.length()) {
				left = right = headIndex;
			} else if (i <= headIndex){
				left = i;
				for (; j > left; j--) {
					if (tape.charAt(j) != '_') break;
				}
				right = (j > headIndex)? j : headIndex;
			} else {
				left = headIndex;
				for (; j > i; j--) {
					if (tape.charAt(j) != '_') break;
				}
				right = (j > i) ? j : i;
			}
			String tapeInfo = "";
			String Index = "";
			String headPtr = "";
			for (int k = left; k <= right; k++) {
				if (k != left) {
					tapeInfo += " ";
					Index += " ";
					headPtr += " ";
				}
				int index = (k - middleOfTape > 0) ? k - middleOfTape : middleOfTape - k;
				Index += index;
				tapeInfo += tape.charAt(k);
				headPtr += ((k == headIndex) ? "^" : " ");
				if (index / 10 != 0) {
					if (index / 100 == 0) {
						tapeInfo += " ";
						headPtr += " ";
					} else if (index / 1000 == 0) {
						tapeInfo += "  ";
						headPtr += "  ";
					} else if (index / 10000 == 0) {
						tapeInfo += "   ";
						headPtr += "   ";
					}
				}
			}
			
			writer.append("Index : " + Index + "\n");
			writer.append("Tape  : " + tapeInfo + "\n");
			writer.append("Head  : " + headPtr + "\n");
			writer.append("State : " + stateCur + "\n");
			writer.append("---------------------------------------------\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean SingleExecutor(int step, OutputStreamWriter writer) {
		printProcess(step, writer);
		if (transition.containsKey(stateCur)) {
			boolean matchAll = false;
			Tuple t = new Tuple();
			ArrayList<Tuple> list = transition.get(stateCur);
			for (Tuple elem : list) {
				char oldSym = tape.charAt(head + middleOfTape);
				if (elem.oldSym.equals(oldSym)) {
					stateCur = elem.newState;
					if (!elem.newSym.equals('*')) {
						StringBuffer sbuf = new StringBuffer(tape);
						sbuf.setCharAt(head + middleOfTape, elem.newSym);
						tape = sbuf.toString();
					}
					if (elem.dir == direction.LEFT)
						head --;
					else if (elem.dir == direction.RIGHT)
						head ++;
					
					if (head + middleOfTape >= tape.length()) {
						tape += "_";
					} else if (head + middleOfTape < 0) {
						middleOfTape ++;
						tape = "_" + tape;
					}
					return true;
				} else if (elem.oldSym.equals('*')) {
					matchAll = true;
					t = elem;
				}
			}	
			if (matchAll) {
				stateCur = t.newState;
				if (!t.newSym.equals('*')) {
					StringBuffer sbuf = new StringBuffer(tape);
					sbuf.setCharAt(head + middleOfTape, t.newSym);
					tape = sbuf.toString();
				}
				if (t.dir == direction.LEFT)
					head --;
				else if (t.dir == direction.RIGHT)
					head ++;
				
				if (head + middleOfTape >= tape.length()) {
					tape += "_";
				} else if (head + middleOfTape < 0) {
					middleOfTape ++;
					tape = "_" + tape;
				}
				
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		turing tur = new turing(args[0]);
		tur.turingParser();
		tur.turingSimulator();
	}

}
