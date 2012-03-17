package iryrwarosh;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MapExplorationHandler implements Handler {
	private Hashtable<Creature, boolean[][]> exploration;
	private Hashtable<Creature, List<Tile>> biomes;
	
	public MapExplorationHandler(){
		exploration = new Hashtable<Creature, boolean[][]>();
		biomes = new Hashtable<Creature, List<Tile>>();
	}
	
	@Override
	public void handle(Message message) {
		if (Moved.class.isAssignableFrom(message.getClass()))
			handle((Moved)message);

		if (WorldCreated.class.isAssignableFrom(message.getClass()))
			handle((WorldCreated)message);
	}

	public void handle(Moved message) {
		if (!message.creature.isHuman())
			return;
		
		explored(message.world, message.creature);
	}
	
	public void handle(WorldCreated message) {
		exploration.clear();
		biomes.clear();
		
		int sx = message.player.position.x / 19;
		int sy = message.player.position.y / 9;
		
		message.world.map().markAsExplored(sx, sy);
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
			MessageBus.publish(new ExploredNewLocation(world, creature, creature.name() + " has discovered a new location"));
			
			Tile biome = world.map().biome(sx, sy);
			
			if (!biomes.containsKey(creature))
				biomes.put(creature, new ArrayList<Tile>());
			
			if (!biomes.get(creature).contains(biome)){
				biomes.get(creature).add(biome);

				MessageBus.publish(new ExploredNewBiome(world, creature, creature.name() + " has discovered a new region"));
			}
		}
	}
}
