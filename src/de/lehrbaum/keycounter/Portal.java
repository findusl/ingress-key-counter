package de.lehrbaum.keycounter;

public class Portal implements Comparable<Portal> {
	private String name;
	private short keys;
	
	/**
	 * Create a new portal.
	 * 
	 * @param name The name of the portal.
	 * @param keys The number of keys existing for the portal.
	 */
	public Portal(String name, int keys) {
		this.name = name;
		this.keys = (short) keys;
	}
	
	/**
	 * @return The number of keys for this portal.
	 */
	public short getKeyCount() {
		return keys;
	}
	
	/**
	 * @return The name of this portal.
	 */
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
	
	@Override
	public int compareTo(Portal another) {
		return name.compareTo(another.name);
	}
}