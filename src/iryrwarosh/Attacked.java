package iryrwarosh;

public class Attacked extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public Attacked(World world, Creature attacker, Creature attacked){
		super(world, attacker.glyph() + " attacked " + attacked.glyph());
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}
