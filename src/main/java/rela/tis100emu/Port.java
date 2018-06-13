package rela.tis100emu;

import java.util.Queue;

import com.esotericsoftware.minlog.Log;

public class Port {

	private Node nodeA = null;
	private Node nodeB = null;
	private boolean hasData;
	private int data;
	private String name;
	
	public Port(Node a, Node b, String name) {
		this.hasData = false;
		this.name = name;
		if(a != null) {
			Log.debug("add node " + a.getName());
			this.nodeA = a;
		}
		
		if(b != null) {
			Log.debug("add node " + b.getName());
			this.nodeB = b;
		}
	}
	
	public void clearData() {
		Log.debug("Clean " + data + " from port");
		this.hasData = false;
	}
	
	public boolean hasData() {
		return hasData;
	}

	public int read() {
		Log.debug("Read " + data + " from port ");
		this.hasData = false;
		return data;
	}
	
	public void write(int data) {
		Log.debug("Write " + data + " to port ");
		this.data = data;
		this.hasData = true;
	}
	
	public Node getSrc() {
		return nodeA;
	}

	public Node getDst() {
		return nodeB;
	}

	public int getData() {
		return this.data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
