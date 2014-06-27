package de.lehrbaum.keycounter;

public class Portal {
	private String name;
	private short keys;
	
	public Portal(String name, int keys) {
		this.name = name;
		this.keys = (short) keys;
	}
	
	public short getKeyCount() {
		return keys;
	}
	
	public void setKeys(short keys) {
		this.keys = keys;
	}
	
	public String getName() {
		return name;
	}
	
	public void decreaseCount() {
		if (keys > 0)
			keys--;
	}
	
	public void increaseCount() {
		if (keys < MainActivity.MAX_KEYS)
			keys++;
	}
}