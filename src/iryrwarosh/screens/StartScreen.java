package iryrwarosh.screens;

import iryrwarosh.ArmosSaga;
import iryrwarosh.Tile;
import iryrwarosh.Common;
import iryrwarosh.CreatureAiHandler;
import iryrwarosh.LootSaga;
import iryrwarosh.MapExplorationHandler;
import iryrwarosh.MessageBus;
import iryrwarosh.ItemSpecialsSaga;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class StartScreen implements Screen {

	Tile wall = Tile.GREEN_TREE1;
	Tile floor = Tile.GREEN_DIRT;
	Tile special = Tile.GREEN_TREE1;
	Tile statue = Tile.STATUE;
	Tile liquid = Tile.WATER1;
	Tile[][] tiles = new Tile[19][9];
	
	public StartScreen(){
		MessageBus.subscribe(new ItemSpecialsSaga());
		MessageBus.subscribe(new LootSaga());
		MessageBus.subscribe(new ArmosSaga());
		MessageBus.subscribe(new MapExplorationHandler());
		MessageBus.subscribe(new CreatureAiHandler());
		
		addTheme();
		buildRoom();
	}

	private void addTheme() {
		statue = Tile.STATUE;
		liquid = Math.random() < 0.1 ? Tile.LAVA1 : Tile.WATER1;
		
		switch ((int)(Math.random() * 10)){
		case 0:
			wall = Tile.GREEN_ROCK;
			floor = Tile.GREEN_DIRT;
			break;
		case 1:
			wall = Tile.GREEN_TREE1;
			floor = Tile.GREEN_DIRT;
			break;
		case 2:
			wall = Tile.PINE_TREE1;
			floor = Tile.GREEN_DIRT;
			break;
		case 3:
			wall = Tile.BROWN_ROCK;
			floor = Tile.BROWN_DIRT;
			break;
		case 4:
			wall = Tile.GREEN_TREE1;
			floor = Tile.BROWN_DIRT;
			break;
		case 5:
			wall = Tile.BROWN_TREE4;
			floor = Tile.BROWN_DIRT;
			break;
		case 6:
			wall = Tile.WHITE_TREE1;
			floor = Tile.WHITE_DIRT;
			break;
		case 7:
			wall = Tile.WHITE_ROCK;
			floor = Tile.WHITE_DIRT;
			break;
		case 8:
			wall = Tile.BROWN_ROCK;
			floor = Tile.DESERT_SAND1;
			break;
		case 9:
			wall = Tile.WHITE_WALL;
			floor = Tile.WHITE_TILE1;
			break;
		}
		
		if (floor == Tile.WHITE_TILE1) {
			special = Tile.WHITE_TILE1;
			statue = Tile.STATUE_WHITE;
			liquid = Math.random() < 0.5 ? Tile.LAVA1 : Tile.WATER1;
		} else if (floor == Tile.DESERT_SAND1) {
			special = Tile.DESERT_SAND1;
			liquid = Tile.DESERT_SAND1;
			statue = Tile.DESERT_SAND1;
		} else if (Math.random() < 0.5){
			Tile[] specials = { Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, 
					Tile.GREEN_TREE1, Tile.PINE_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE4, Tile.WHITE_TREE1 };
			
			special = specials[(int)(Math.random() * specials.length)];
		} else {
			special = wall;
		}
	}
	
	private void buildRoom(){
		int y = 0;
		makeRow("###################", y++);
		makeRow("#.................#", y++);
		makeRow("#.x.....&.&.....x.#", y++);
		makeRow("#....&.......&....#", y++);
		makeRow("#.x....~~~~~....x.#", y++);
		makeRow("#....&.~~~~~.&....#", y++);
		makeRow("#.x....~~~~~....x.#", y++);
		makeRow("#.................#", y++);
		makeRow("#########.#########", y++);
	}
	
	private void makeRow(String data, int y) {
		
		for (int x = 0; x < data.length(); x++){
			Tile tile = null;
			switch (data.charAt(x)){
			case '#': tile = wall; break;
			case '~': tile = liquid; break;
			case '.': tile = floor; break;
			case '&': tile = statue; break;
			default : tile = special;
			}
			
			tiles[x][y] = tile.variation(x, y); 
		}
		
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultForegroundColor(Common.guiForeground);
		terminal.setDefaultBackgroundColor(Common.guiBackground);
		terminal.clear();
		
		terminal.writeCenter("I rule, you rule, we all rule old-school Hyrule", 1, AsciiPanel.brightWhite);
		terminal.writeCenter("a 2012 seven day roguelike by Trystan Spangler", 2);
		
		int left = 30;
		int top  = 10;
		
		for (int x = 0; x < tiles.length; x++)
		for (int y = 0; y < tiles[0].length; y++){
			terminal.write(tiles[x][y].glyph(), left+x, top+y, tiles[x][y].color(), tiles[x][y].background());
		}
		
		terminal.writeCenter("-- press [enter] to start --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
		case KeyEvent.VK_ENTER: 
			return new ChooseStartingItemsScreen();
		case KeyEvent.VK_SPACE:
			addTheme();
			buildRoom();
			return this;
		default: return this;
		}
	}
}
