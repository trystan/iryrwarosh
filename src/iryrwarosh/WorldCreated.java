package iryrwarosh;


public class WorldCreated extends Message {
	public Creature player;
	
	public WorldCreated(World world, Creature player, String text) {
		super(world, text);
		this.player = player;
	}

	@Override
	public boolean involves(Creature player) {
		return false;
	}

}
