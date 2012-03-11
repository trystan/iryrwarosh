package iryrwarosh;

public class Attacked extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public Attacked(Creature attacker, Creature attacked){
		super(attacker.glyph() + " attacked " + attacked.glyph());
		this.attacker = attacker;
		this.attacked = attacked;
	}
}
