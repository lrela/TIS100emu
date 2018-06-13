package rela.tis100emu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class TISSystem {

	private static final Logger LOG = Logger.getLogger(TISSystem.class.getName());
	
	private List<Node> nodes;
	private int xSize;
	private int ySize;
	//private Map<Character, List<Integer>> inputsByInputName = new HashMap<Character, List<Integer>>();
	private Map<Character, List<Integer>> outputs = new HashMap<Character, List<Integer>>();
	
	
	public TISSystem(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

	/*
	public void addInput(Character inputName, Integer input) {
		
	}
*/
	/*
			x#.a
			.#.#
			x#.a

			Inputs are on first row and outputs on the last.
			x at first line: Input X
			x at last line: Output X
			etc..

			x = node
			# = disabled (broken) node
	 */
	public static TISSystem fromString(String s) {
		s = s.trim();

		StringTokenizer st = new StringTokenizer(s, "\n\r");
		int ySize = st.countTokens();
		int xSize = s.length() / ySize;
		TISSystem tis = new TISSystem(xSize, ySize);
		System.out.println(xSize + " " + ySize);
		Node n = null;
		int lineIdx = 0;
		int nodeIdx = 0;
		List<Node> nodes = new ArrayList<Node>();
		while(st.hasMoreTokens()) {
			String line = st.nextToken();
			//System.out.println("fromString rivi:"+line);
			for(int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				//System.out.println("saatu:"+c);
				if(c == '.') {
					n = new Node(nodeIdx++);
					n.setHealthy(true);
					//System.out.println("node");
				} else if(c == '#') {
					n = new Node(nodeIdx++);
					n.setHealthy(false);
					//System.out.println("disabled node");
				} else if(lineIdx == 0) {
					// first line : UP ports are inputs
					n = new Node(nodeIdx++);
					n.addExtInput(c);
					//System.out.println("input " + c);
				} else if(lineIdx == tis.ySize-1) {
					// last line: DOWN ports are outputs
					n = new Node(nodeIdx++);
					List<Integer> output = new ArrayList<Integer>();
					n.addExtOutput(c, output);
					tis.outputs.put(c, output);
					//System.out.println("output " + c);

				} else {
					System.err.println("Ignored invalid char '" + c + "'");
				}

				if(n != null) {
					nodes.add(n);
					//System.out.println("lis�tty listaan");
					n = null;
				}

			}
			lineIdx++;
		}

		nodes = addInterNodePorts(nodes, tis.xSize, tis.ySize);
		tis.nodes = nodes;
		//System.out.println(""+nodes.size() + " nodes");
		return tis;
	}
	
	public void setProgram(int nodeIdx, List<Instr> prg) {
		this.nodes.get(nodeIdx).load(prg);
		
	}
	
	public void setInputData(int nodeIdx, Integer[] data) {
		Node n = this.getNodes().get(nodeIdx);
		n.setInputData(data);
	}
	
	public void setProgram(int nodeIdx, String source) {
		List<Instr> prg = Compiler.compile(source);
		this.setProgram(nodeIdx, prg);
	}

	public static final E.Reg[] REGS = new E.Reg[]{E.Reg.UP, E.Reg.RIGHT, E.Reg.DOWN, E.Reg.LEFT};
	public static List<Node> addInterNodePorts(List<Node> nodes, int xLen, int yLen) {		
		List<Node> ret = new ArrayList<Node>();
		for(int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			int[] ai = getAdjacentNodeIndexes(i, xLen, yLen);

			for(int j = 0; j < 4; j++) {
				if(ai[j] != -1) {
					Node dst = nodes.get(ai[j]);
					E.Reg reg = REGS[j];
					node.addConnectionTo(dst, reg);
				}
			}

			ret.add(node);
		}
		return ret;
	}


/*
	public static void main(String[] abcadfs) {
		int[] i = getAdjacentNodeIndexes(7, 5, 3);

	}
*/
	// -1 for empty direction. Order is UP, RIGHT, DOWN, LEFT
	public static int[] getAdjacentNodeIndexes(int idx, int xl, int yl) {
		int[] ret = new int[]{-1,-1,-1,-1};

		int[][] tmp = new int[xl][yl];

		int myX = -1;
		int myY = -1;
		int c = 0;
		for(int yy = 0; yy < yl; yy++) {
			for(int xx = 0; xx < xl; xx++) {
				if(c == idx) {
					myX = xx;
					myY = yy;
				}
				tmp[xx][yy] = c++;
			}
		}

		if(myY > 0)
			ret[0] = tmp[myX][myY-1];

		if(myX < xl-1)
			ret[1] = tmp[myX+1][myY];

		// DOWN
		if(myY < yl-1)
			ret[2] = tmp[myX][myY+1];

		if(myX > 0)
			ret[3] = tmp[myX-1][myY];

		System.out.println(idx + " UP="+ret[0]+ " RI="+ret[1]+" DO="+ret[2]+" LE="+ret[3]);

		return ret;
	}


	public String toString() {
		return this.toString(System.getProperty("line.separator"));
	}
	
	

	public String toString(String lineSeparator) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(int y = 0; y < ySize; y++) {
			for(int x = 0; x < xSize; x++) {
				Node n = this.nodes.get(i);
				if(n.isHealthy() == false) {
					sb.append('#');
					//System.out.println("add #");
				} else if(y == 0 && n.getPortsByReg().containsKey(E.Reg.UP)) {
					// first line, UP reg is output port
					String name = n.getPortsByReg().get(E.Reg.UP).getName();
					sb.append(name);
					//System.out.println("add input node:'"+name+"'");
				} else if(y == ySize-1 && n.getPortsByReg().containsKey(E.Reg.DOWN)) {
					// last line, DOWN reg is output port
					String name = n.getPortsByReg().get(E.Reg.DOWN).getName();
					sb.append(name);
					//System.out.println("add output node:'"+name+"'");
				} else {
					sb.append('.');
					//System.out.println("add .");
				}
				i++;
			}
			sb.append(lineSeparator);
		}
		return sb.toString();
	}

	public List<Node> getNodes() {
		return nodes;
	}
	
	public List<Integer> getOutputForNode(Character shortName) {
		return this.outputs.get(shortName);
	}

}
