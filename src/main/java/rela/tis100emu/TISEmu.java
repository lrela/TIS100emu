package rela.tis100emu;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.minlog.Log;

public class TISEmu {

	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		/*
		String selfTest = "a###\n.###\nb###\n";
		TISSystem tis = TISSystem.fromString(selfTest);
		tis.setProgram(0, "MOV UP, DOWN");
		tis.setProgram(4, "MOV UP, DOWN");
		tis.setProgram(8, "MOV UP, DOWN");
		Integer[] input = {1,2,3};
		List<Node> nodes = tis.getNodes();		
		nodes.get(0).setInputData(input);
		Node output = nodes.get(8);
		*/
		
		/*
		String sigAmp = 
				".a.#\n"+
				"....\n"+
				"#.o.\n";

		TISSystem tis = TISSystem.fromString(sigAmp);
		tis.setProgram(1, "MOV UP, ACC\nADD ACC\nMOV ACC, DOWN");
		tis.setProgram(5, "MOV UP, DOWN");
		tis.setProgram(9, "MOV UP, RIGHT");
		tis.setProgram(10, "MOV LEFT, DOWN");
		Integer[] input = {66,34,88,91,53,96,96,47};

		List<Node> nodes = tis.getNodes();		
		nodes.get(1).setInputData(input);
		Node output = nodes.get(10);

		while(true) {
			int stepCount = 0;
			for(Node n : nodes) {
				while(n.step()) {
					stepCount++;
				}
			}
			if(stepCount == 0)
				break;
		}
		
		/*
		for(int i = 0; i < 20; i++)
			for(Node n : nodes)
				if(n.step());
		*/
		
		

		String diffConv = 
				".ab.\n"+
				"...#\n"+
				".pn.\n";
		
		TISSystem tis = TISSystem.fromString(diffConv);
		tis.setProgram(0, "MOV RIGHT, DOWN");
		tis.setProgram(1, 
				"MOV UP, ACC\n"+
				"SUB RIGHT\n"+
				"SAV\n"+
				"MOV ACC, LEFT\n"+
				"SWP\n"+
				"NEG\n"+
				"MOV ACC, DOWN"
				);
		tis.setProgram(2, "MOV UP, LEFT");
		
		tis.setProgram(4, "MOV UP, DOWN");
		tis.setProgram(5, "MOV UP, RIGHT");
		tis.setProgram(6, "MOV LEFT, DOWN");
		
		tis.setProgram(8, "MOV UP, RIGHT");
		tis.setProgram(9, "MOV LEFT, DOWN");
		tis.setProgram(10, "MOV UP, DOWN");
		
		Integer[] inputA = {44, 78, 88, 95, 65, 63, 41, 26, 87};
		Integer[] inputB = {93, 60, 92, 68, 56, 30, 90, 65, 94};

		List<Node> nodes = tis.getNodes();		
		nodes.get(1).setInputData(inputA);
		nodes.get(2).setInputData(inputB);

		for(Node n: nodes) {
			Map<E.Reg, Port> m = n.getPortsByReg();
			System.out.print("\n" + n.getName() + ":");
			for(E.Reg r : m.keySet()) {
				Port p = m.get(r);
				System.out.print(r.name() + " -> " + p.getName() + ", ");
			}
		}
		
		//while(true) {
		for(int i = 0; i < 20; i++) {
			for(Node n : nodes) {
				//n.run();
				int count = 0;
				while(E.StepResult.WAITING.equals(n.step()))
					count++;
				Log.debug(count + " steps executed");
				count = 0;
			}
		}
		System.out.print("P:\t");
		for(Integer val : tis.getOutputForNode('p')) {
			System.out.print(val+", ");
		}		

		System.out.print("\nN:\t");
		for(Integer val : tis.getOutputForNode('n')) {
			System.out.print(val+", ");
		}		

		
		//System.out.println(tis.toString());
		
		Integer[] outputP = {-49, 18, -4, 27, 9, 33, -49, -39, -7};
		Integer[] outputN = {49, -18, 4, -27, -9, -33, 49, 39, 7};
		System.out.println("\nOnko N OK : " + outputN.equals(tis.getOutputForNode('n')));
		System.out.println("Onko P OK : " + outputP.equals(tis.getOutputForNode('p')));


	}

}
