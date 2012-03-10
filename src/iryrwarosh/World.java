package iryrwarosh;

public class World {
	private WorldMap map;
	
	public World(WorldMap map){
		this.map = map;
	}
	
	public WorldMap map(){
		return map;
	}
}
