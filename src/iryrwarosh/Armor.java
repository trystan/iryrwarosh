package iryrwarosh;

import java.awt.Color;

public class Armor extends Item {

	public boolean ignoreWebbing;
	public boolean detectCreatures;
	public boolean deflectRanged;
	public boolean swimInWater;
	public boolean regenerateHp;
	public boolean defenseBoost;
	
	Armor(String name, int glyph, Color color) {
		super(name, glyph, color);
	}
}
