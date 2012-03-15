package iryrwarosh;

public class ExploredNewBiome extends Message {
	public Creature creature;
	
	public ExploredNewBiome(World world, Creature creature, String text) {
		super(world, text);
		this.creature = creature;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}

}
