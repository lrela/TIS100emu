package rela.tis100emu;

import java.util.HashMap;
import java.util.Map;

public class NodeState {

	private int acc = 0;
	private int bak = 0;
	private E.State state = E.State.IDLE;
	private Map<E.Reg, Port> portsByReg = new HashMap<E.Reg, Port>(4);
	private int pc;
	public int getAcc() {
		return acc;
	}
	public void setAcc(int acc) {
		this.acc = acc;
	}
	public int getBak() {
		return bak;
	}
	public void setBak(int bak) {
		this.bak = bak;
	}
	public E.State getState() {
		return state;
	}
	public void setState(E.State state) {
		this.state = state;
	}
	public Map<E.Reg, Port> getPortsByReg() {
		return portsByReg;
	}
	public void setPortsByReg(Map<E.Reg, Port> portsByReg) {
		this.portsByReg = portsByReg;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}

	
}
