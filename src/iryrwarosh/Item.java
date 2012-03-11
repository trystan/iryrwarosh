package iryrwarosh;

import java.awt.Color;

public class Item {
	private String name;
	public String name() { return name; }

	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	
	Item(String name, int glyph, Color color){
		this.name = name;
		this.glyph = (char)glyph;
		this.color = color;
	}
	
	public void onCollide(World world, Creature colider){
		
	}
}
