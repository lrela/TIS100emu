package rela.tis100emu;

import java.util.List;
import org.junit.Test;
import junit.framework.TestCase;
import rela.tis100emu.Compiler;
import rela.tis100emu.Instr;

public class TestCompilers extends TestCase {

	@Test
	public void testCompileAndDecompile() {
		String src = "MOV UP, DOWN\n"
		+ "MOV RIGHT, DOWN\n"
		+ "MOV UP, ACC\n"
		+ "MOV UP, DOWN\n"
		+ "MOV UP, DOWN\n"
		+ "MOV UP, DOWN\n"
		+ "MOV UP, RIGHT\n"
		+ "MOV LEFT, DOWN\n";
		
		List<Instr> tgt = Compiler.compile(src);
		String dst = Compiler.decompile(tgt);

		assertEquals(src, dst);			
	}


}
