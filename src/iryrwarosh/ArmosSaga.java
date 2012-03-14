package iryrwarosh;

public class ArmosSaga implements Handler {

	@Override
	public void handle(Message message) {
		if (BumpedIntoObsticle.class.isAssignableFrom(message.getClass()))
			handle((BumpedIntoObsticle)message);
	}

	public void handle(BumpedIntoObsticle message) {
		if (message.world.tile(message.x, message.y) == Tile.STATUE 
				&& message.creature.name().equals("player"))
			makeArmos(message.world, message.x, message.y);
	}

	private void makeArmos(World world, int x, int y) {
		Tile statue = Tile.STATUE;
		world.setTile(Tile.WHITE_DIRT, x, y);
		Creature armos = new Creature("armos", statue.glyph(), statue.color(), 5){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		
		armos.position = new Point(x, y);
		armos.addTrait(Trait.WALKER);
		armos.addTrait(Trait.DOUBLE_MOVE);
		world.add(armos);
	}
}
