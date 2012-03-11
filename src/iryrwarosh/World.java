package iryrwarosh;

import java.util.ArrayList;
import java.util.List;

public class World {
	private WorldMap map;
	private Tile[][] tiles;
	private List<Creature> creatures;
	private Item[][] items;
	
	public List<Creature> creatures() { return creatures; }
	
	public int width() { return tiles.length; }
	
	public int height() { return tiles[0].length; }
	
	public World(Tile[][] tiles, WorldMap map){
		this.tiles = tiles;
		this.map = map;
		this.creatures = new ArrayList<Creature>();
		this.items = new Item[tiles.length][tiles[0].length];
	}
	
	public WorldMap map(){
		return map;
	}
	
	public Tile tile(int x, int y){
		if (x < 0 || x >= tiles.length || y < 0 || y >= tiles[0].length)
			return Tile.OUT_OF_BOUNDS;
		else
			return tiles[x][y];
 	}
	
	public Item item(int x, int y){
		return items[x][y];
 	}
	
	public Creature creature(int x, int y){
		for (Creature c : creatures)
    		if (c.position.x == x && c.position.y == y)
    			return c;
		
		return null;
	}

    public void update(){
    	updateWater();
    	
    	for (Creature c : creatures)
    		if (c.hp() > 0)
    			c.update();
    	
    	List<Creature> stillAlive = new ArrayList<Creature>();
    	
    	for (Creature c : creatures)
    		if (c.hp() > 0)
    			stillAlive.add(c);
    	
    	creatures = stillAlive;
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

	public void add(Creature creature) {
		while (creature.position == null){
			int x = (int)(Math.random() * tiles.length);
			int y = (int)(Math.random() * tiles[0].length);
			
			if (creature.canEnter(tile(x,y)))
				creature.position = new Point(x, y);
		}
		creatures.add(creature);
	}

	public void addToScreen(Creature creature, int sx, int sy) {
		while (creature.position == null){
			int x = sx * 19 + (int)(Math.random() * 19);
			int y = sy *  9 + (int)(Math.random() * 9);
			
			if (creature.canEnter(tile(x,y)))
				creature.position = new Point(x, y);
		}
		creatures.add(creature);
	}
	
	public void add(Item item, int x, int y){
	    ArrayList<Point> candidates = new ArrayList<Point>();
        candidates.add(new Point(x,y));

        int tries = 0;
        while (candidates.size() > 0 && tries++ < 25){
            Point dest = candidates.remove(0);

            if (item(dest.x, dest.y) == null && tile(dest.x, dest.y).isGround()){
                items[dest.x][dest.y] = item;
                return;
            } else {
                candidates.addAll(dest.neighbors());
            }
        }
    }

	public void add(Item item) {
		int x = (int)(Math.random() * tiles.length);
		int y = (int)(Math.random() * tiles[0].length);
		
		while (!tile(x,y).isGround()){
			x = (int)(Math.random() * tiles.length);
			y = (int)(Math.random() * tiles[0].length);
		}
		items[x][y] = item;
	}

	public void removeItem(int x, int y) {
		items[x][y] = null;
	}
	
	public List<Point> screensOfType(Tile biome){
		return map.scrensOfType(biome);
	}
}
