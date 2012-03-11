package iryrwarosh;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {
	private WorldScreen[][] screens;

	public int width() { return screens.length; }

	public int height() { return screens[0].length; }
	
	public WorldMap(WorldScreen[][] screens){
		this.screens = screens;
	}
	
	public WorldScreen screen(int x, int y) {
		return screens[x][y];
	}

	public List<Point> scrensOfType(Tile biome) {
		List<Point> candidates = new ArrayList<Point>();
		
    	for (int x = 0; x < screens.length; x++)
        for (int y = 0; y < screens[0].length; y++)
        	if (screens[x][y].defaultWall == biome || screens[x][y].defaultGround == biome)
        		candidates.add(new Point(x,y));
    	
    	return candidates;
	}
}
