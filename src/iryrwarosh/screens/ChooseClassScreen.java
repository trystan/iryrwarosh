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
		terminal.write("    reduce damage or even avoid it all.", 1, y++);

		y++;
		terminal.write("[2] An adventurer with a sword to dispatch your weaker enemies and a tunic", 1, y++);
		terminal.write("    that leaves you unencumbered and able to jump.", 1, y++);
		
		y++;
		terminal.write("[3] A monk with knuckles for combos and a special suit that allows you to", 1, y++);
		terminal.write("    swim and even cleanse yourself of poison.", 1, y++);

		y++;
		terminal.write("[4] A wizard with a spear to hit anyone who comes near you and magic robes", 1, y++);
		terminal.write("    that detect invisible creatures and allow you to releport nearby.", 1, y++);

		y++;
		terminal.write("[5] A rogue with a knife to attack while evading and a cloak that can deflect", 1, y++);
		terminal.write("    some projectiles and let you sneak by others.", 1, y++);

		y++;
		terminal.write("[6] A priest with a staff to counterattack anyone who attacks you and holy", 1, y++);
		terminal.write("    vestments that can heal you and allow you to pray.", 1, y++);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		Factory f = new Factory();
		
		switch (key.getKeyChar()){
		case '1': return newGame(f, f.club(), f.heavyArmor());
		case '2': return newGame(f, f.sword(), f.greenTunic());
		case '3': return newGame(f, f.knuckles(), f.gillsuit());
		case '4': return newGame(f, f.spear(), f.wizardRobe());
		case '5': return newGame(f, f.knife(), f.cloak());
		case '6': return newGame(f, f.staff(), f.vestments());
		}
		
		return this;
	}
	
	private Screen newGame(Factory factory, Item weapon, Item armor){
		World world = new Worldgen(48 / 3, 24 / 3).build();
		
		Creature player = factory.player(world);
		player.equip(world, weapon); 
		player.equip(world, armor);
		
		for (int i = 0; i < 30; i++)
			factory.zora(world);
		
		for (int i = 0; i < 100; i++)
			factory.goblin(world);
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.PINE_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE4, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			for (int i = 0; i < 20; i++)
				factory.monster(world, biome);
		}

		world.update();
		
		return new PlayScreen(world, factory, player);
	}
}
