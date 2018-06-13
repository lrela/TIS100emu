package rela.tis100emu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Compiler {

	private static Map<String, E.Reg> regsByName = new HashMap<String, E.Reg>();
	static {
		//UP, DOWN, LEFT, RIGHT, ACC, NIL, (NUMBER)s
		regsByName.put("UP", E.Reg.UP);
		regsByName.put("DOWN", E.Reg.DOWN);
		regsByName.put("LEFT", E.Reg.LEFT);
		regsByName.put("RIGHT", E.Reg.RIGHT);
		regsByName.put("ACC", E.Reg.ACC);
		regsByName.put("NIL", E.Reg.NIL);
	}

	private static Map<String, InstrSpec> specByUCaseName = new HashMap<String, InstrSpec>();
	static {
		
		// constr params:
		// command, has label, has source register, has destination register 
		
		// regs
		specByUCaseName.put("MOV", new InstrSpec(E.Cmd.MOV, false, true, true));
		specByUCaseName.put("SWP", new InstrSpec(E.Cmd.SWP, false, false, false));
		specByUCaseName.put("SAV", new InstrSpec(E.Cmd.SAV, false, false, false));

		// arith
		specByUCaseName.put("ADD", new InstrSpec(E.Cmd.ADD, false, true, false));
		specByUCaseName.put("SUB", new InstrSpec(E.Cmd.SUB, false, true, false));
		specByUCaseName.put("NEG", new InstrSpec(E.Cmd.NEG, false, false, false));

		// jump
		specByUCaseName.put("JMP", new InstrSpec(E.Cmd.JMP, true, false, false));
		specByUCaseName.put("JEZ", new InstrSpec(E.Cmd.JEZ, true, false, false));
		specByUCaseName.put("JNZ", new InstrSpec(E.Cmd.JNZ, true, false, false));
		specByUCaseName.put("JGZ", new InstrSpec(E.Cmd.JGZ, true, false, false));
		specByUCaseName.put("JLZ", new InstrSpec(E.Cmd.JLZ, true, false, false));
		specByUCaseName.put("JRO", new InstrSpec(E.Cmd.JRO, false, true, false));

		// nop
		specByUCaseName.put("NOP", new InstrSpec(E.Cmd.NOP, false, false, false));
	}

	public static List<Instr> compile(String source) {
		List<Instr> p = new ArrayList<Instr>();
		Instr i = null;
		StringTokenizer lines = new StringTokenizer(source, "\n\r");
		while(lines.hasMoreElements()) {
			String line = lines.nextToken().trim();
			if(line.length() >= 2 && line.startsWith("#") == false) {

				// handle label first
				if(line.endsWith(":")) {
					// label
					i = new Instr(E.Cmd.LABEL);
					i.setLabel(line.substring(0, line.length()-1));
				} else {
					// other cmd
					StringTokenizer items = new StringTokenizer(line, " \t,");
					String cmdName = items.nextToken().toUpperCase();
					InstrSpec spec = specByUCaseName.get(cmdName);

					if(E.Cmd.NOP.equals(spec.cmd)) {
						i = new Instr(E.Cmd.ADD);
						i.setDst(E.Reg.NIL);
					} else {
						i = new Instr(spec.cmd);

						if(spec.label) {
							i.setLabel(items.nextToken());
						} else if(spec.src) {
							String srcTmp = items.nextToken().toUpperCase();
							if(regsByName.containsKey(srcTmp)) {
								// reg
								i.setSrc(regsByName.get(srcTmp));
							} else {
								// value
								i.setSrc(E.Reg.NUMBER);
								i.setSrcNumber(Integer.valueOf(srcTmp));
							}
							if(spec.dst) {
								i.setDst(regsByName.get(items.nextToken().toUpperCase()));
							}
						}
					}
				}
				p.add(i);
			}
		}

		return p;
	}


	public static String decompile(List<Instr> program) {
		StringBuilder sb = new StringBuilder();
		for(Instr i : program) {
			if(E.Cmd.LABEL.equals(i.getName())) {
				sb.append(i.getLabel());
				sb.append(':');
			} else {
				sb.append(i.getName());
				if(i.getSrc() != null) {
					sb.append(' ');
					if(E.Reg.NUMBER.equals(i.getSrc())) {
						sb.append(i.getSrcNumber());
					} else {
						sb.append(i.getSrc());
					}

					// dst exist only if src exists
					if(i.getDst() != null) {
						sb.append(',');
						sb.append(' ');
						sb.append(i.getDst());
					}
				} else if(i.getLabel() != null) {
					sb.append(' ');
					sb.append(i.getLabel());
				}
			}
			sb.append('\n');
		}

		return sb.toString();
	}


}
class InstrSpec {
	
	// instruction specification
	
	// cmd name
	public E.Cmd cmd;
	// has label?
	public boolean label;
	// has source?
	public boolean src;
	// has destination?
	public boolean dst;
	
	public InstrSpec(E.Cmd cmd, boolean label, boolean src, boolean dst) {
		super();
		this.cmd = cmd;
		this.label = label;
		this.src = src;
		this.dst = dst;
	}



}
