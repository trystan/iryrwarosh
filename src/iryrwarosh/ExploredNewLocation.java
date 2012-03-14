package iryrwarosh;

public class ExploredNewLocation extends Message {
	public Creature creature;
	
	public ExploredNewLocation(World world, Creature creature, String text) {
		super(world, text);
		this.creature = creature;
	}

	@Override
	public boolean involves(Creature player) {
		return false;
	}

}
