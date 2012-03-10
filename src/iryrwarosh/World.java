package iryrwarosh;

public class World {
	private WorldMap map;
	private Tile[][] tiles;
	
	public int width() { return tiles.length; }
	
	public int height() { return tiles[0].length; }
	
	public World(Tile[][] tiles, WorldMap map){
		this.tiles = tiles;
		this.map = map;
	}
	
	public WorldMap map(){
		return map;
	}
	
	public Tile tile(int x, int y){
		return tiles[x][y];
	}
}
