package iryrwarosh;

public class MapExplorationHandler implements Handler {
	
	@Override
	public void handle(Message message) {
		if (Moved.class.isAssignableFrom(message.getClass()))
			handle((Moved)message);

		if (WorldCreated.class.isAssignableFrom(message.getClass()))
			handle((WorldCreated)message);
	}

	public void handle(Moved message) {
		if (message.creature.glyph() != '@')
			return;

		explored(message.world, message.creature.position);
	}
	
	public void handle(WorldCreated message) {
		explored(message.world, message.player.position);
	}
	
	private void explored(World world, Point position){
		world.map().markAsExplored(position.x / 19, position.y / 9);
	}
}
