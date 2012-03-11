package iryrwarosh;

import java.awt.Color;

public class Weapon extends Item {
	public Weapon(String name, char glyph, Color color){
		super(name, glyph, color);
	}
	
	public int comboAttackPercent     = 0;
	public int evadeAttackPercent     = 0;
	public int circleAttackPercent    = 0;
	public int finishingAttackPercent = 0;
	public int distantAttackPercent   = 0;
	public int counterAttackPercent   = 0;
	
	public boolean isImuneToSpikes    = false;
}
