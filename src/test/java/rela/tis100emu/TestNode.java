package rela.tis100emu;

import org.junit.Test;
import junit.framework.TestCase;
import rela.tis100emu.Node;


public class TestNode extends TestCase {

	@Test
	public void testNodeCreation() {
		String name = "1";
		Node a = new Node(name);

		assertEquals(name, a.getName());
	}

}
