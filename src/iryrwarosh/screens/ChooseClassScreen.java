package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Factory;
import iryrwarosh.Item;
import iryrwarosh.Tile;
import iryrwarosh.World;
import iryrwarosh.Worldgen;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class ChooseClassScreen implements Screen {
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.clear();
		terminal.write("Choose your class.", 1, 1);
		
		int y = 3;
		terminal.write("[1] A warrior with a club to hit all who surround you and heavy armor to", 1, y++);
		terminal.write("    reduce damage.", 1, y++);

		y++;
		terminal.write("[2] An adventurer with a sword that shoots when you're at full health and", 1, y++);
		terminal.write("    a shield that blocks incoming projectiles half of the time.", 1, y++);
		
		y++;
		terminal.write("[3] A monk with knuckles for combos and a snorkel for swimming.", 1, y++);
		y++;

		y++;
		terminal.write("[4] A wizard with a book of spells and a crystal ball to detect creatures", 1, y++);
		terminal.write("    who are hiding from you.", 1, y++);

		y++;
		terminal.write("[5] A rogue with a knife to attack while evading and a bow that can shoot", 1, y++);
		terminal.write("    arrows, if you can afford it.", 1, y++);

		y++;
		terminal.write("[6] A priest with a staff to counterattack anyone who attacks you and a first", 1, y++);
		terminal.write("    aid kit that auto-heals you and can unpoison and heal more, for a price.", 1, y++);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		Factory f = new Factory();
		
		switch (key.getKeyChar()){
		case '1': return newGame(f, f.club(), f.heavyArmor());
		case '2': return newGame(f, f.sword(), f.shield());
		case '3': return newGame(f, f.knuckles(), f.snorkel());
		case '4': return newGame(f, f.spellBook(), f.crystalBall());
		case '5': return newGame(f, f.knife(), f.bow());
		case '6': return newGame(f, f.staff(), f.firstAidKit());
		}
		
		return this;
	}
	
	private Screen newGame(Factory factory, Item item1, Item item2){
		World world = new Worldgen(48 / 3, 24 / 3).build();
		
		Creature player = factory.player(world);
		player.equip(world, item1); 
		player.equip(world, item2);
		
		for (int i = 0; i < 30; i++)
			factory.zora(world);
		
		for (int i = 0; i < 100; i++)
			factory.goblin(world);
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.BROWN_TREE1, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			for (int i = 0; i < 20; i++)
				factory.monster(world, biome);
		}

		world.update();
		
		return new PlayScreen(world, factory, player);
	}
}
