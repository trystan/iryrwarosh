package iryrwarosh;

public class ArmosSaga implements Handler {

	@Override
	public void handle(Message message) {
		if (BumpedIntoObstical.class.isAssignableFrom(message.getClass()))
			handle((BumpedIntoObstical)message);
	}

	public void handle(BumpedIntoObstical message) {
		if (message.world.tile(message.x, message.y) == Tile.STATUE)
			makeArmos(message.world, message.x, message.y);
	}

	private void makeArmos(final World world, int x, int y) {
		Tile statue = Tile.STATUE;
		world.setTile(Tile.WHITE_DIRT, x, y);
		Creature armos = new Creature("armos", statue.glyph(), statue.color(), 2){
			public void update(){
				super.update();
				wander(world);
			}
		};
		
		armos.position = new Point(x, y);
		armos.addTrait(CreatureTrait.WALKER);
		armos.addTrait(CreatureTrait.DOUBLE_MOVE);
		world.add(armos);
	}
}
