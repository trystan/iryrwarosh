package iryrwarosh;

import java.util.Hashtable;

public class MapExplorationHandler implements Handler {
	private Hashtable<Creature, boolean[][]> exploration;
	
	public MapExplorationHandler(){
		exploration = new Hashtable<Creature, boolean[][]>();
	}
	
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

		explored(message.world, message.creature);
	}
	
	public void handle(WorldCreated message) {
		exploration.clear();
		explored(message.world, message.player);
	}
	
	private void explored(World world, Creature creature){
		int sx = creature.position.x / 19;
		int sy = creature.position.y / 9;
		
		if (creature.isPlayer())
			world.map().markAsExplored(sx, sy);

		if (!exploration.containsKey(creature))
			exploration.put(creature, new boolean[world.map().width()][world.map().height()]);
		
		if (exploration.get(creature)[sx][sy] == false){
			exploration.get(creature)[sx][sy] = true;
			MessageBus.publish(new ExploredNewLocation(world, creature, ""));
		}
	}
}
