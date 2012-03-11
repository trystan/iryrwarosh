package iryrwarosh;

public class HitSpikes extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public HitSpikes(World world, Creature attacker, Creature attacked){
		super(world, "The " + attacker.name() + " hit the spikes of the " + attacked.name());
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}
