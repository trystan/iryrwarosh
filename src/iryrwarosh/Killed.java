package iryrwarosh;

public class Killed extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public Killed(Creature attacker, Creature attacked){
		super(attacker.glyph() + " killed " + attacked.glyph());
		this.attacker = attacker;
		this.attacked = attacked;
	}
}
