package iryrwarosh.screens;

import iryrwarosh.WorldMap;
import iryrwarosh.WorldScreen;

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
		char wall = '#';
		char open = '.';
		terminal.write(open, x, y);
		terminal.write(wall, x-1, y-1);
		terminal.write(screen.nEdge==WorldScreen.WALL ? wall : open, x, y-1);
		terminal.write(wall, x+1, y-1);
		terminal.write(screen.eEdge==WorldScreen.WALL ? wall : open, x+1, y);
		terminal.write(wall, x+1, y+1);
		terminal.write(screen.sEdge==WorldScreen.WALL ? wall : open, x, y+1);
		terminal.write(wall, x-1, y+1);
		terminal.write(screen.wEdge==WorldScreen.WALL ? wall : open, x-1, y);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return previous;
	}

}
