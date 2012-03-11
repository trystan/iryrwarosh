package iryrwarosh;

public class Hid extends Message {
	public Creature creature;
	
	public Hid(World world, Creature creature){
		super(world, "The " + creature.name() + " hid in the " + world.tile(creature.position.x, creature.position.y).description() + ".");
		this.creature = creature;
	}
	
	@Override
	public boolean involves(Creature player) {
		return creature == player 
			|| player.position.distanceTo(creature.position) < 6;
	}
}
