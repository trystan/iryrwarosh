package iryrwarosh;

public class Poisoned extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public Poisoned(World world, Creature attacker, Creature attacked){
		super(world, "The " + attacker.name() + " poisoned the " + attacked.name());
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}
