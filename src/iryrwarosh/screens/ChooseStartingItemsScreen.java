package iryrwarosh.screens;

import iryrwarosh.Common;
import iryrwarosh.Creature;
import iryrwarosh.Factory;
import iryrwarosh.Item;
import iryrwarosh.MessageBus;
import iryrwarosh.Point;
import iryrwarosh.Tile;
import iryrwarosh.World;
import iryrwarosh.WorldCreated;
import iryrwarosh.Worldgen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class ChooseStartingItemsScreen implements Screen {
	private Item[] items;
	private Factory factory;
	private List<Item> chosen = new ArrayList<Item>();
	
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
			factory.spectacles(),
			factory.spellBook(),
			factory.jumpingBoots(),
			factory.heavyArmor(),
		};
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultForegroundColor(Common.guiForeground);
		terminal.setDefaultBackgroundColor(Common.guiBackground);
		terminal.clear();
		terminal.write(" Choose your two items. Most have at least one passive ability and an active", 1, 1);
		terminal.write("ability that will cost a few rupees to use. Start with a melee weapon since", 1, 2);
		terminal.write("they increase the damage done when bumping into others and often allow extra", 1, 3);
		terminal.write("attacks when evading, moving, or defending. Even items that don't seem useful", 1, 4);
		terminal.write("have their place. The world has many creatures who guard other items.", 1, 5);
		
		
		for (int i = 0; i < items.length; i++)
			writeChoice(terminal, i);
	}
	
	private void writeChoice(AsciiPanel terminal, int i){
		Color fg = chosen.contains(items[i]) ? AsciiPanel.brightWhite : Common.guiForeground;
		
		char key = (char)('a' + i);
		terminal.write("[" + key + "] ", 1, 7 + i, fg);
		terminal.write(items[i].name(), items[i].color());
		terminal.write(". " + items[i].description(), fg);
		
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		int i = key.getKeyChar() - 'a';
		if (i < 0 || i >= items.length)
			return this;
		
		if (chosen.contains(items[i]))
			chosen.remove(items[i]);
		else
			chosen.add(items[i]);
		
		if (chosen.size() == 2)
			return newGame(factory, chosen.get(0), chosen.get(1));
		else 
			return this;
	}
	
	private Screen newGame(Factory factory, Item item1, Item item2){
		World world = new Worldgen(80 / 3, 24 / 3).build();
		
		Creature player = factory.player(world);
		player.swapLeftHand(world, item1); 
		player.swapRightHand(world, item2);
		
		for (int i = 0; i < 80; i++)
			factory.zora(world);
		
		for (int i = 0; i < 100; i++)
			factory.goblin(world);

		for (int i = 0; i < 4; i++)
			factory.rival(world);
		
		for (Tile biome : new Tile[]{ 
				Tile.GREEN_TREE1, Tile.GREEN_TREE1, Tile.GREEN_TREE1,
				Tile.BROWN_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE1,  
				Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.GREEN_ROCK, Tile.GREEN_ROCK, 
				Tile.BROWN_ROCK, Tile.BROWN_ROCK, Tile.BROWN_ROCK, 
				Tile.WHITE_ROCK,  
				Tile.GREEN_TREE1, Tile.GREEN_TREE1, Tile.GREEN_TREE1,
				Tile.BROWN_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE1,  
				Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.GREEN_ROCK, Tile.GREEN_ROCK, 
				Tile.BROWN_ROCK, Tile.BROWN_ROCK, Tile.BROWN_ROCK, 
				Tile.WHITE_ROCK, 
				Tile.DESERT_SAND1, Tile.WATER1 }){
			for (int i = 0; i < 10; i++)
				factory.monster(world, biome);
		}

		for (Point screen : world.map().getDeadEnds()){
			if (world.map().screen(screen.x, screen.y).defaultWall == Tile.WHITE_WALL){
				factory.miniboss(world, screen.x, screen.y);
				world.add(factory.lostArtifact(), screen.x * 19 + 19 / 2, screen.y * 9 + 9 / 2);
			} else {
				switch ((int)(Math.random() * 5)){
				case 0: world.addToScreen(factory.evasionPotion(), screen.x, screen.y); break;
				case 1: world.addToScreen(factory.heartContainer(), screen.x, screen.y); break;
				default: factory.miniboss(world, screen.x, screen.y); break;
				}
			}
		}
		
		world.update();
		MessageBus.publish(new WorldCreated(world, player, "The world has been created."));
		
		return new PlayScreen(world, factory, player);
	}
}
