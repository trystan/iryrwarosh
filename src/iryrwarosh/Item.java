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

	private String description;
	public String description() { return description; }

	private List<Trait> traits;
	public void addTrait(Trait trait) { traits.add(trait); }
	
	public void removeTrait(Trait trait) { traits.remove(trait); }
	
	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}
	
	private int evasionModifier;
	public int evasionModifier() { return evasionModifier; }
	
	Item(String name, int glyph, Color color, String description){
		this(name, glyph, color, 0, description); 
	}

	Item(String name, int glyph, Color color, int evasionModifier, String description){
		this.name = name;
		this.glyph = (char)glyph;
		this.color = color;
		this.traits = new ArrayList<Trait>();
		this.description = description;
		this.evasionModifier = evasionModifier;
	}
	
	public void update(Creature owner){
		
	}
	
	public void onCollide(World world, Creature colider){
		
	}

	public Screen use(Screen screen, World world, Creature player) {
		return screen;
	}
}
