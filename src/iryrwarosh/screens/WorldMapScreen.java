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
		char wall = screen.defaultWall.glyph();
		char open = screen.defaultGround.glyph();
		char water = Tile.WATER.glyph();
		Color wallColor = screen.defaultWall.color();
		Color openColor = screen.defaultGround.color();
		Color waterColor = Tile.WATER.color();
		terminal.write(open, x, y, openColor);
		terminal.write(screen.nwWater ? water : wall, x-1, y-1, screen.nwWater ? waterColor : wallColor);
		terminal.write(screen.nEdge==WorldScreen.WALL ? wall : open, x, y-1, screen.nEdge==WorldScreen.WALL ? wallColor : openColor);
		terminal.write(screen.neWater ? water : wall, x+1, y-1, screen.neWater ? waterColor : wallColor);
		terminal.write(screen.eEdge==WorldScreen.WALL ? wall : open, x+1, y, screen.eEdge==WorldScreen.WALL ? wallColor : openColor);
		terminal.write(screen.seWater ? water : wall, x+1, y+1, screen.seWater ? waterColor : wallColor);
		terminal.write(screen.sEdge==WorldScreen.WALL ? wall : open, x, y+1, screen.sEdge==WorldScreen.WALL ? wallColor : openColor);
		terminal.write(screen.swWater ? water : wall, x-1, y+1, screen.swWater ? waterColor : wallColor);
		terminal.write(screen.wEdge==WorldScreen.WALL ? wall : open, x-1, y, screen.wEdge==WorldScreen.WALL ? wallColor : openColor);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return previous;
	}

}
