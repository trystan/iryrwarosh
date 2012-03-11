package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Factory {
	private HashMap<Tile,List<CreatureTrait>> monsterTraits;
	
	public Factory(){
		setMonsterTraits();
	}
	
	private void setMonsterTraits(){
		monsterTraits = new HashMap<Tile,List<CreatureTrait>>();
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.PINE_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE4, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			
			List<CreatureTrait> traits = new ArrayList<CreatureTrait>();
			
			while (traits.size() < 3){
				CreatureTrait trait = CreatureTrait.getRandom();
				if (!traits.contains(trait))
					traits.add(trait);
			}
			
			monsterTraits.put(biome, traits);
			
			for (CreatureTrait trait : traits)
				System.out.println(biome.name() + " monster has " + trait.name());
		}
	}
	
	public Weapon knuckes(){
		Weapon w = new Weapon("Knuckes", ')', AsciiPanel.white);
		w.comboAttackPercent = 33;
		return w;
	}
	
	public Weapon knife(){
		Weapon w = new Weapon("Knife", ')', AsciiPanel.white);
		w.evadeAttackPercent = 66;
		return w;
	}
	
	public Weapon club(){
		Weapon w = new Weapon("Club", ')', AsciiPanel.white);
		w.circleAttackPercent = 50;
		return w;
	}
	
	public Weapon sword(){
		Weapon w = new Weapon("Sword", ')', AsciiPanel.white);
		w.finishingAttackPercent = 80;
		return w;
	}
	
	public Weapon spear(){
		Weapon w = new Weapon("Spear", ')', AsciiPanel.white);
		w.distantAttackPercent = 75;
		return w;
	}
	
	public Weapon staff(){
		Weapon w = new Weapon("Staff", ')', AsciiPanel.white);
		w.counterAttackPercent = 75;
		return w;
	}

	public Weapon weapon() {
		switch ((int)(Math.random() * 6)){
		case 0: return knuckes();
		case 1: return knife();
		case 2: return club();
		case 3: return sword();
		case 4: return spear();
		default: return staff();
		}
	}
	
	public Creature goblin(final World world){
		int hue = 30 + (int)(Math.random() * 90);
		Creature goblin = new Creature("goblin", 'g', Tile.hsv(hue, 50, 50), 2){
			public void update(){
				super.update();
				moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
			}
		};
		goblin.addTrait(CreatureTrait.WALKER);
		
		world.add(goblin);
		goblin.equip(world, weapon());
		return goblin;
	}
	
	public Creature monster(final World world, Tile biome){
		Color color = null;
		List<Point> candidates = null;
		Point candidate = null;
		String name = null;
		
		switch (biome) {
		case GREEN_TREE1:
			name = "evergreen monster";
			color = biome.color();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case PINE_TREE1:
			name = "pine monster";
			color = biome.color().darker();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE1:
			name = "broadleaf monster";
			color = biome.color().darker();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE4:
			name = "forest monster";
			color = biome.color();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_TREE1:
			name = "pale monster";
			color = biome.color().brighter();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case GREEN_ROCK:
			name = "hill monster";
			color = biome.color().brighter();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_ROCK:
			name = "mountan monster";
			color = biome.color().brighter();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_ROCK:
			name = "snow monster";
			color = biome.color().brighter();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case DESERT_SAND1:
			name = "desert monster";
			color = biome.color();
			candidates = world.screensOfType(biome);
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WATER1:
			name = "water monster";
			color = biome.color().brighter();
			break;
		}
		
		Creature monster = new Creature(name, 'm', color, 3){
			public void update(){
				super.update();
				moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
				
				if (hasTrait(CreatureTrait.DOUBLE_MOVE))
					moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
			}
		};
		
		if (biome == Tile.WATER1){
			monster.addTrait(CreatureTrait.SWIMMER);
		} else {
			monster.addTrait(CreatureTrait.WALKER);
		}
		
		for (CreatureTrait trait : monsterTraits.get(biome))
			monster.addTrait(trait);
		
		if (candidate == null)
			world.add(monster);
		else
			world.addToScreen(monster, candidate.x, candidate.y);
		
		return monster;
	}

	public Creature player(World world) {
		Creature player = new Creature("player", '@', AsciiPanel.brightWhite, 10);
		player.addTrait(CreatureTrait.WALKER);
		world.add(player);
		return player;
	}
}
