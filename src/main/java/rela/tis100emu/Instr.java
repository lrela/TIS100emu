package rela.tis100emu;

public class Instr {

		
	private E.Cmd name;
	private String label;
	private E.Reg src;
	private E.Reg dst;
	private Integer srcNumber;

	public Instr(E.Cmd name) {
		super();
		this.name = name;
	}

	public E.Cmd getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public E.Reg getSrc() {
		return src;
	}

	public E.Reg getDst() {
		return dst;
	}

	public Integer getSrcNumber() {
		return srcNumber;
	}

	public void setName(E.Cmd name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSrc(E.Reg src) {
		this.src = src;
	}

	public void setDst(E.Reg dst) {
		this.dst = dst;
	}

	public void setSrcNumber(Integer srcNumber) {
		this.srcNumber = srcNumber;
	}

	
}
