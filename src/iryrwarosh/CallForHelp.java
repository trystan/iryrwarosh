package iryrwarosh;

public class CallForHelp extends Message {
	public Creature attacked;
	public Creature attacker;
	
	public CallForHelp(World world, Creature attacked, Creature attacker){
		super(world, "The " + attacked.name() + " calls for help");
		this.attacked = attacked;
		this.attacker = attacker;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}
