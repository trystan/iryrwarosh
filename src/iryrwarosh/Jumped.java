package iryrwarosh;

public class Jumped extends Message {
	public Creature creature;
	
	public Jumped(World world, Creature creature){
		super(world, "The " + creature.name() + " jumped");
		this.creature = creature;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player || player.position.distanceTo(creature.position) < 6;
	}
}
