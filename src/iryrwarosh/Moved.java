package iryrwarosh;

public class Moved extends Message {
	public Creature creature;
	
	public Moved(World world, Creature creature){
		super(world, creature.glyph() + " moved");
		this.creature = creature;
	}
	
	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}
}
