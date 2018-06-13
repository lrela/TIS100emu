package rela.tis100emu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.minlog.Log;

import rela.tis100emu.E.Reg;

public class Node {

	// TODO any last pseudo ports
	
	private static final List<Instr> EMPTY_PROG = new ArrayList<Instr>();
		
	private Integer[] input = null;
	private int inputIdx;
	private List<Integer> output = null;
	
	private String name;
	private int acc = 0;
	private int bak = 0;
	private boolean healthy = true;
	private E.State state = E.State.IDLE;
	private Map<E.Reg, Port> portsByReg = new HashMap<E.Reg, Port>(4);
	private Map<String, Integer> instrLocByLabel;
	private List<Instr> program;
	private int pc;
	private boolean isOutput = false;
	private boolean isInput = false;

	//private Node blockedBy = null;
	
	public Node(int nodeIdx) {
		this.name = Integer.toString(nodeIdx);
	}
	
	public Node(String name) {
		super();
		this.name = name;
		this.program = EMPTY_PROG;
		this.instrLocByLabel = new HashMap<String, Integer>();
		this.reset();
		Log.debug("Node " + name + " is created");
	}

	public void reset() {
		this.acc = 0;
		this.bak = 0;
		this.pc = 0;
		this.state = E.State.IDLE;
		this.inputIdx = 0;
		Log.debug("Node " + this.name + " is reset. Program and input data is kept.");
	}

	public void setInputData(Integer[] data) {
		this.input = data;
		this.inputIdx = 0;
		Log.debug("Set node " + this.name + " input data as " + Arrays.asList(data));
		Port p = this.getPortsByReg().get(E.Reg.UP);
		p.write(this.getNextInputData());
	}
/*	
	private static char getPortShortName(E.Reg reg) {
		if(reg.equals(E.Reg.UP))
			return 'U';

		if(reg.equals(E.Reg.DOWN))
			return 'D';

		if(reg.equals(E.Reg.LEFT))
			return 'L';

		if(reg.equals(E.Reg.RIGHT))
			return 'R';
		
		Log.error("Invalid reg, assigning port short name to 'x'");
		return 'x';
	}
	*/
	private static E.Reg rev(E.Reg reg) {
		if(reg.equals(E.Reg.UP))
			return E.Reg.DOWN;

		if(reg.equals(E.Reg.DOWN))
			return E.Reg.UP;

		if(reg.equals(E.Reg.LEFT))
			return E.Reg.RIGHT;

		if(reg.equals(E.Reg.RIGHT))
			return E.Reg.LEFT;

		return null;

	}
		
	public Port addConnectionTo(Node dst, E.Reg reg) {
		if(this.getPortsByReg().containsKey(reg)) {
			return this.getPortsByReg().get(reg);
		}
		
		String portName = this.name + "-" + dst.name;
		Port p = new Port(this, dst, portName);
		this.portsByReg.put(reg, p);
		
		E.Reg otherSide = rev(reg);
		dst.portsByReg.put(otherSide, p);
		Log.debug("Conn " + this.name +":"+reg.name()+ " <-> " + 
				dst.name + ":" + otherSide.name());
		return p;
	}

	public Port addExtInput(char shortName) {
		String name = this.name+"-"+shortName;
		Port p = new Port(null, this, name);
		Log.debug("Added external input '"+shortName+"' to node " + this.name + " Port name is " + name);
		this.isInput = true;
		this.portsByReg.put(E.Reg.UP, p);
		return p;
	}

	public Port addExtOutput(char shortName, List<Integer> output) {
		String name = this.name+"-"+shortName;
		Port p = new Port(this, null, name);
		Log.debug("Added external output '"+shortName+"' to node " + this.name + " Port name is " + name);
		this.isOutput = true;
		this.output = output;
		this.portsByReg.put(E.Reg.DOWN, p);
		return p;
	}

	public boolean isHealthy() {
		return healthy;
	}

	public void setHealthy(boolean healthy) {
		this.healthy = healthy;
	}

	public Map<E.Reg, Port> getPortsByReg() {
		return this.portsByReg;
	}

	public void load(List<Instr> program) {
		for(int c = 0; c < program.size(); c++) {
			Instr i = program.get(c);
			if(i.getLabel() != null)
				this.instrLocByLabel.put(i.getLabel(), c);
		}
		this.program = program;

	}
	
	public E.StepResult step() {
		if(this.program == null || this.program.isEmpty() || this.healthy == false)
			return E.StepResult.NOP;
	
		Instr i = this.program.get(this.pc);
		E.Reg dst = null;
		E.Reg src = null;
		E.Cmd cmd = i.getName();
		Integer srcNum = null;
		Log.debug("---");
		Log.debug("Node " + this.name +  " step " + i.getName().name()+ ", pc=" + this.pc );		
		Log.info(dump());

		Log.debug("srcR="+i.getSrc() + " srcV="+i.getSrcNumber() + " dst=" + i.getDst());

		if(i.getSrc() != null) {
			src = i.getSrc();
			Log.debug("src is " + src);
		}

		if(i.getDst() != null) {
			dst = i.getDst();
			//Log.debug("dst is " + dst);
		}
		
		if(i.getSrcNumber() != null) {
			srcNum = i.getSrcNumber();
			//Log.debug("src num is " + srcNum);
		}
		
		Integer val = null;

		if(E.Cmd.MOV.equals(cmd)) {
			if(srcNum != null) {
				val = srcNum;
			} else {
				val = localRegRead(src);
				if(val == null) {
					Log.debug("Blocked to read");
					this.state = E.State.READ;
					return E.StepResult.BLOCKED;
				}
			}
						
			if(localRegWrite(dst, val) == false) {
				Log.debug("Blocked to write");
				this.state = E.State.WRTE;
				return E.StepResult.BLOCKED;
			}
			this.pc++;
		} else if(E.Cmd.SWP.equals(cmd)) {
			int tmp = this.bak;
			this.bak = this.acc;
			this.acc = tmp;
			this.pc++;

		} else if(E.Cmd.SAV.equals(cmd)) {
			this.bak = this.acc;
			this.pc++;

		} else if(E.Cmd.ADD.equals(cmd)) {
			if(srcNum != null)
				val = srcNum;
			else {
				val = localRegRead(src);
				if(val == null) {
					Log.debug("Blocked to read");
					this.state = E.State.READ;
					return E.StepResult.BLOCKED;
				}
			}

			this.acc += val;
			this.pc++;
			
		} else if(E.Cmd.SUB.equals(cmd)) {
			if(srcNum != null)
				val = srcNum;
			else {
				val = localRegRead(src);
				if(val == null) {
					Log.debug("Blocked to read");
					this.state = E.State.READ;
					return E.StepResult.BLOCKED;
				}
			}

			this.acc = this.acc - val;
			this.pc++;

		} else if(E.Cmd.NEG.equals(cmd)) {
			this.acc = this.acc * -1;
			this.pc++;

		} else if(E.Cmd.JMP.equals(cmd)) {
			this.pc = this.instrLocByLabel.get(i.getLabel());
			this.pc++;

		} else if(E.Cmd.JEZ.equals(cmd)) {
			if(this.acc == 0)
				this.pc = this.instrLocByLabel.get(i.getLabel());
			this.pc++;

		} else if(E.Cmd.JNZ.equals(cmd)) {
			if(this.acc != 0)
				this.pc = this.instrLocByLabel.get(i.getLabel());
			this.pc++;

		} else if(E.Cmd.JGZ.equals(cmd)) {
			if(this.acc > 0)
				this.pc = this.instrLocByLabel.get(i.getLabel());
			this.pc++;

		} else if(E.Cmd.JLZ.equals(cmd)) {
			if(this.acc < 0)
				this.pc = this.instrLocByLabel.get(i.getLabel());
			this.pc++;

		} else if(E.Cmd.JRO.equals(cmd)) {
			if(srcNum != null) {
				this.pc += srcNum;
			} else if(E.Reg.ACC.equals(src)) {
				this.pc = this.acc;
			}
		}

		this.state = E.State.IDLE;
		
		if(pc >= this.program.size())
			pc = 0;
		
		Log.info(dump());
		return E.StepResult.WAITING;
	}	

	public List<Integer> getOutput() {
		return output;
	}

	public boolean isOutput() {
		return isOutput;
	}

	public boolean isInput() {
		return isInput;
	}

	private boolean localRegWrite(E.Reg r, int val) {
		Log.debug("Node " + this.name + ": try write " + val + " to " + r.name());		
		if(E.Reg.ACC.equals(r)) {
			// acc kirjoitus onnistuu aina
			Log.debug("Write " + val + " to ACC");
			this.acc = val;
			return true;
		} else if(E.Reg.NIL.equals(r)) { 
			Log.debug("Reg is NIL, do nothing");
			// nil kirjoitus ei tee mitään
			return true;
		} else {
			// muutoin kohteena on portti
			Port target = this.getPortsByReg().get(r);
			//if(p == null) System.out.println(name + " " + r);
			
			if(this.isOutput && r.equals(E.Reg.DOWN)) {
				Log.debug("Write to output: " + val);
				this.output.add(val);
				return true;
			}
			
			if(target.hasData()) {
				// jos portissa dataa niin blokataan
				this.state = E.State.WRTE;
				Log.debug("Blocking. Target port " + target.getName() + " has already data. Node state is now " + this.state);
				return false;
			} else {
				// jos portissa ei dataa niin kirjoita
				Log.debug("Write successful, port " + target.getName() + " data is now " + val);
				target.write(val);
				return true;
				
			}
		}
		
	}

	private boolean hasMoreInputData() {
		return this.inputIdx < this.input.length;
	}
	
	private int getNextInputData() {
		int val = this.input[inputIdx];
		Log.debug("Got next input data " + val + " idx=" + this.inputIdx);
		this.inputIdx++;
		return val;
	}

	private Integer localRegRead(E.Reg r) {
		Log.debug("Requested to read val from local reg " + r.name());
		
		if(E.State.IDLE.equals(this.state) == false) {
			Log.debug("Read cancelled, this node is blocked. S=" + this.state);
			return null;
		}
			
		
		if(E.Reg.ACC.equals(r)) {
			Log.debug("Read " + this.acc + " from ACC");
			return this.acc;
		} else {
			Port p = this.portsByReg.get(r);			
			Log.debug("Port is " + p.getName());

			if(this.isInput && r.equals(Reg.UP) && this.hasMoreInputData() && p.hasData() == false) {
				p.write(this.getNextInputData());
			}
			
			if(p.hasData()) {
				int val = p.read();
				Log.debug("Read " + val + " from " + r);
				return val;
			} else {
				// no data
				Log.debug("No data on " + r + ", blocking");
				return null;
			}
		}
	}

	public String getName() {
		return name;
	}

	public E.State getState() {
		return state;
	}
	
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("nod:"+this.name + " ACC:" + this.acc + " BAK:" + this.bak + " S="+this.state.name());
		for(Reg r : this.portsByReg.keySet()) {
			Port p = this.portsByReg.get(r);
			sb.append(' ');
			sb.append(r.name()).append('/');
			
			if(p == null)
				sb.append('-');
			else if(p.hasData()) {
				sb.append(p.getName()).append(':');
				sb.append(p.getData());	
			} else {
				sb.append(p.getName()).append(':');
				sb.append('_');
			}
		}
		return sb.toString();
	}
	
}
