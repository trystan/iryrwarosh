package iryrwarosh;

public class Unhid extends Message {
	public Creature creature;
	
	public Unhid(World world, Creature creature){
		super(world, "The " + creature.name() + " came out of the " + world.tile(creature.position.x, creature.position.y).description() + ".");
		this.creature = creature;
	}
	
	@Override
	public boolean involves(Creature player) {
		return creature == player || player.canSee(creature);
	}
}
