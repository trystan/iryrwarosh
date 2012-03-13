package iryrwarosh;

public class BumpedIntoObstical extends Message {
	public Creature creature;
	public int x;
	public int y;
	
	public BumpedIntoObstical(World world, Creature creature, int x, int y) {
		super(world, creature.name() + " bumped into the " + world.tile(x, y).description());
		this.creature = creature;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}
}
