package iryrwarosh;

public class WorldScreen {
	public static final int WALL = 0,
	                        CENTER = 2;

	public Tile defaultGround = Tile.GREEN_DIRT;
	public Tile defaultWall   = Tile.GREEN_ROCK;
	public int nEdge;
	public int sEdge;
	public int wEdge;
	public int eEdge;
}
