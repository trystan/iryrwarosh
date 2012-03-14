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
	
	public ChooseStartingItemsScreen(){
		factory = new Factory();
		items = new Item[]{
			factory.sword(),
			factory.club(),
			factory.knife(),
			factory.spear(),
			factory.staff(),
			factory.shield(),
			factory.snorkel(),
			factory.bow(),
			factory.firstAidKit(),
			factory.crystalBall(),
			factory.spellBook(),
			factory.jumpingBoots(),
			factory.heavyArmor(),
		};
		
		picked = new boolean[items.length];
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.clear();
		terminal.write(" Choose your two items. Each item has at least one passive ability and an", 1, 1);
		terminal.write("active ability that cost rupees to use. You should start with at least one", 1, 2);
		terminal.write("melee weapon since they increase the damage done when bumping into others", 1, 3);
		terminal.write("and give you chances to make free attacks.", 1, 4);
		
		
		for (int i = 0; i < picked.length; i++)
			writeChoice(terminal, i);
	}
	
	private void writeChoice(AsciiPanel terminal, int i){
		Color fg = picked[i] ? AsciiPanel.brightWhite : AsciiPanel.white;
		
		char key = (char)('a' + i);
		terminal.write("[" + key + "] ", 1, 6 + i, fg);
		terminal.write(items[i].name(), items[i].color());
		terminal.write(". " + items[i].description(), fg);
		
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		
		switch (key.getKeyChar()){
		case 'a': picked[0] = !picked[0]; break;
		case 'b': picked[1] = !picked[1]; break;
		case 'c': picked[2] = !picked[2]; break;
		case 'd': picked[3] = !picked[3]; break;
		case 'e': picked[4] = !picked[4]; break;
		case 'f': picked[5] = !picked[5]; break;
		case 'g': picked[6] = !picked[6]; break;
		case 'h': picked[7] = !picked[7]; break;
		case 'i': picked[8] = !picked[8]; break;
		case 'j': picked[9] = !picked[8]; break;
		case 'k': picked[10] = !picked[10]; break;
		case 'l': picked[11] = !picked[11]; break;
		case 'm': picked[11] = !picked[11]; break;
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
		World world = new Worldgen(80 / 3, 24 / 3).build();
		
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

		for (Point screen : world.map().getDeadEnds()){
			if (Math.random() < 0.5)
				factory.miniboss(world, screen.x, screen.y);
			else
				world.addToScreen(factory.heartIncrease(), screen.x, screen.y);
		}
		
		world.update();
		
		return new PlayScreen(world, factory, player);
	}
}
