package iryrwarosh;

import iryrwarosh.screens.Screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item {
	private String name;
	public String name() { return name; }

	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	

	private List<Trait> traits;
	public void addTrait(Trait trait) { traits.add(trait); }

	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}
	
	Item(String name, int glyph, Color color){
		this.name = name;
		this.glyph = (char)glyph;
		this.color = color;
		this.traits = new ArrayList<Trait>();
	}
	
	public void onCollide(World world, Creature colider){
		
	}

	public void use(Screen screen, World world, Creature player) {
		
	}
}
