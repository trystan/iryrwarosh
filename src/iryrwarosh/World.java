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

    public void update(){
    	updateWater();
    }
    
    private void updateWater(){
    	for (int x = 0; x < tiles.length; x++)
        for (int y = 0; y < tiles[0].length; y++){
        	if (!tiles[x][y].isWater())
        		continue;
        	
        	if (x < tiles.length-1 && tiles[x+1][y].isWater())
        		tiles[x][y] = tiles[x+1][y];
        	else
        		tiles[x][y] = Tile.WATER1.variation();
        }
    }
}
