package iryrwarosh;

public class Note extends Message {
	public Creature creature;
	
	public Note(World world, Creature creature, String text){
		super(world, text);
		this.creature = creature;
	}
	
	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}

}
