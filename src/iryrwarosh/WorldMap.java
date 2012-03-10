package iryrwarosh;

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
}
