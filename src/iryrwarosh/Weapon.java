package iryrwarosh;

import java.awt.Color;

public class Weapon extends Item {
	public Weapon(String name, char glyph, Color color){
		super(name, glyph, color);
	}
	public boolean isImuneToSpikes    = false;
}
