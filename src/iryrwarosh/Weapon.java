package iryrwarosh;

import java.awt.Color;

public class Weapon {

	private String name;
	public String name() { return name; }

	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	
	public Weapon(String name, char glyph, Color color){
		this.name = name;
		this.glyph = glyph;
		this.color = color;
	}
	
	public int comboAttackPercent     = 0;
	public int evadeAttackPercent     = 0;
	public int circleAttackPercent    = 0;
	public int finishingAttackPercent = 0;
	public int distantAttackPercent   = 0;
	public int counterAttackPercent   = 0;
}
