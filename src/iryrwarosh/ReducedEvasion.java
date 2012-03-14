package iryrwarosh;

public class ReducedEvasion extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public ReducedEvasion(World world, Creature attacker, Creature attacked){
		super(world, "The " + attacker.name() + "'s attack reduced the " + attacked.name() + " ability to evade");
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}
