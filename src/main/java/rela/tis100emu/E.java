package rela.tis100emu;

public class E {

	public static enum StepResult {BLOCKED, WAITING, NOP}
	
	public static enum Block {READ, WRITE, NONE}
	
	public static enum State {IDLE, READ, WRTE, RUN}
	
	public static enum Cmd {LABEL, NOP, MOV, SWP, SAV, ADD, SUB, NEG, 
		JMP, JEZ, JNZ, JGZ, JLZ, JRO};

	public static enum Reg {UP, DOWN, LEFT, RIGHT, ACC, NIL, NUMBER};

}
