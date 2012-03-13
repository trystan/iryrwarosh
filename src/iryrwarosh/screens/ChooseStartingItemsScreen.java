package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Factory;
import iryrwarosh.Item;
import iryrwarosh.Point;
import iryrwarosh.Tile;
import iryrwarosh.World;
import iryrwarosh.Worldgen;

import java.awt.Color;
import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class ChooseStartingItemsScreen implements Screen {
	private boolean[] picked;
	private Item[] items;
	private Factory factory;
	private int descriptionX;
	
	public ChooseStartingItemsScreen(){
		factory = new Factory();
		items = new Item[]{
			factory.sword(),
			factory.shield(),
			factory.spellBook(),
			factory.bow(),
			factory.firstAidKit(),
			factory.crystalBall(),
			factory.snorkel(),
			factory.heavyArmor(),
			factory.club()
		};
		
		picked = new boolean[items.length];
		
		for (Item item : items)
			descriptionX = Math.max(descriptionX, 6 + item.name().length());
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.clear();
		terminal.write("Choose your two items.", 1, 1);
		
		for (int i = 0; i < picked.length; i++)
			writeChoice(terminal, i);
	}
	
	private void writeChoice(AsciiPanel terminal, int i){
		Color fg = picked[i] ? AsciiPanel.brightWhite : AsciiPanel.white;
		
		terminal.write("[" + (i+1) + "] " + items[i].name(), 1, 3 + i, fg);
		terminal.write(items[i].description(), descriptionX, 3 + i, fg);
		
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		
		switch (key.getKeyChar()){
		case '1': picked[0] = !picked[0]; break;
		case '2': picked[1] = !picked[1]; break;
		case '3': picked[2] = !picked[2]; break;
		case '4': picked[3] = !picked[3]; break;
		case '5': picked[4] = !picked[4]; break;
		case '6': picked[5] = !picked[5]; break;
		case '7': picked[6] = !picked[6]; break;
		case '8': picked[7] = !picked[7]; break;
		case '9': picked[8] = !picked[8]; break;
		}
		
		Item item1 = null;
		Item item2 = null;
		for (int i = 0; i < picked.length; i++){
			if (picked[i] && item1 == null)
				item1 = items[i];
			else if (picked[i] && item2 == null)
				item2 = items[i];
		}
		
		if (item1 == null || item2 == null)
			return this;
		else
			return newGame(factory, item1, item2);
	}
	
	private Screen newGame(Factory factory, Item item1, Item item2){
		World world = new Worldgen(48 / 3, 24 / 3).build();
		
		Creature player = factory.player(world);
		player.swapLeftHand(world, item1); 
		player.swapRightHand(world, item2);
		
		for (int i = 0; i < 30; i++)
			factory.zora(world);
		
		for (int i = 0; i < 100; i++)
			factory.goblin(world);
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.BROWN_TREE1, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			for (int i = 0; i < 20; i++)
				factory.monster(world, biome);
		}

		for (Point screen : world.map().getDeadEnds())
			factory.miniboss(world, screen.x, screen.y);
		
		world.update();
		
		return new PlayScreen(world, factory, player);
	}
}
