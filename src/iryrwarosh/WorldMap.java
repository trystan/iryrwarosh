package iryrwarosh;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {
	private WorldScreen[][] screens;
	private int[][] exploration;

	public int width() { return screens.length; }

	public int height() { return screens[0].length; }
	
	public WorldMap(WorldScreen[][] screens){
		this.screens = screens;
		this.exploration = new int[screens.length][screens[0].length];
	}
	
	public WorldScreen screen(int x, int y) {
		return screens[x][y];
	}

	public int explorationStatus(int x, int y){
		return exploration[x][y];
	}
	
	public List<Point> scrensOfType(Tile biome) {
		List<Point> candidates = new ArrayList<Point>();
		
    	for (int x = 0; x < screens.length; x++)
        for (int y = 0; y < screens[0].length; y++)
        	if (screens[x][y].defaultWall == biome || screens[x][y].defaultGround == biome)
        		candidates.add(new Point(x,y));
    	
    	return candidates;
	}

	public List<Point> getDeadEnds() {
		List<Point> candidates = new ArrayList<Point>();
		
    	for (int x = 0; x < screens.length; x++)
        for (int y = 0; y < screens[0].length; y++){
        	int connections = 0;
        	
        	if (screens[x][y].nEdge != WorldScreen.WALL) connections++;
        	if (screens[x][y].eEdge != WorldScreen.WALL) connections++;
        	if (screens[x][y].sEdge != WorldScreen.WALL) connections++;
        	if (screens[x][y].wEdge != WorldScreen.WALL) connections++;
        	
        	if (connections == 1)
        		candidates.add(new Point(x,y));
        }
    	
    	return candidates;
	}

	public void markAsExplored(int sx, int sy) {
		exploration[sx][sy] = 2;
		
		for (Point p : new Point(sx,sy).neighbors()){
			if (p.x < 0 || p.y < 0 || p.x >= exploration.length || p.y >= exploration[0].length)
				continue;
				
			if (exploration[p.x][p.y] == 0)
				exploration[p.x][p.y] = 1; 
		}
	}

	public Tile biome(int sx, int sy) {
		if (screens[sx][sy].defaultGround == Tile.DESERT_SAND1)
			return Tile.DESERT_SAND1;
		else
			return screens[sx][sy].defaultWall;
	}
}
