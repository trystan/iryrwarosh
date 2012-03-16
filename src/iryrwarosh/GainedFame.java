package iryrwarosh;

public class GainedFame extends Message {
	public Creature creature;
	
	public GainedFame(World world, Creature creature, String text) {
		super(world, text);
		this.creature = creature;
	}

	@Override
	public boolean involves(Creature player) {
		return true;
	}

}
