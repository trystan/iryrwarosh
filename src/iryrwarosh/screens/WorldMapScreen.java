package iryrwarosh.screens;

import iryrwarosh.Tile;
import iryrwarosh.WorldMap;
import iryrwarosh.WorldScreen;

import java.awt.Color;
import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class WorldMapScreen implements Screen {
	private Screen previous;
	private WorldMap map;
	
	public WorldMapScreen(Screen previous, WorldMap map){
		this.previous = previous;
		this.map = map;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		for (int x = 0; x < map.width(); x++)
		for (int y = 0; y < map.height(); y++)
			displayScreen(x*3+1, y*3+1, map.screen(x, y), terminal);
	}

	private void displayScreen(int x, int y, WorldScreen screen, AsciiPanel terminal) {
		int wall = WorldScreen.WALL;

		displayTile(terminal, x-1, y-1, screen.nwWater ? Tile.WATER : screen.defaultWall);
		displayTile(terminal, x,   y-1, screen.nWater ? Tile.WATER : (screen.nEdge==wall ? screen.defaultWall : screen.defaultGround));
		displayTile(terminal, x+1, y-1, screen.neWater ? Tile.WATER : screen.defaultWall);

		displayTile(terminal, x-1, y,   screen.wWater ? Tile.WATER : (screen.wEdge==wall ? screen.defaultWall : screen.defaultGround));
		displayTile(terminal, x,   y,   screen.defaultGround);
		displayTile(terminal, x+1, y,   screen.eWater ? Tile.WATER : (screen.eEdge==wall ? screen.defaultWall : screen.defaultGround));

		displayTile(terminal, x-1, y+1, screen.swWater ? Tile.WATER : screen.defaultWall);
		displayTile(terminal, x,   y+1, screen.sWater ? Tile.WATER : (screen.sEdge==wall ? screen.defaultWall : screen.defaultGround));
		displayTile(terminal, x+1, y+1, screen.seWater ? Tile.WATER : screen.defaultWall);

	}
	
	private void displayTile(AsciiPanel terminal, int x, int y, Tile t){
		terminal.write(t.glyph(), x, y, t.color(), t.background());
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return previous;
	}

}
