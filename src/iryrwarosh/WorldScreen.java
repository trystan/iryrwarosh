package iryrwarosh;

public class WorldScreen {
	public static final int WALL = 0,
	                        TOP_LEFT = 1,
	                        CENTER = 2,
	                        BOTTOM_RIGHT = 3,
	                        WIDE = 4;

	public Tile defaultGround = Tile.GREEN_DIRT;
	public Tile defaultWall   = Tile.GREEN_ROCK;
	public int nEdge = WALL;
	public int sEdge = WALL;
	public int wEdge = WALL;
	public int eEdge = WALL;
	
	public Boolean nWater = false;
	public Boolean sWater = false;
	public Boolean wWater = false;
	public Boolean eWater = false;
	public Boolean nwWater = false;
	public Boolean neWater = false;
	public Boolean swWater = false;
	public Boolean seWater = false;
}
