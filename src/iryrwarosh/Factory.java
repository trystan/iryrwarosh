package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Factory {
	private HashMap<Tile,List<Trait>> monsterTraits;
	
	public Factory(){
		setMonsterTraits();
	}
	
	private void setMonsterTraits(){
		monsterTraits = new HashMap<Tile,List<Trait>>();
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.PINE_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE4, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			
			List<Trait> traits = new ArrayList<Trait>();
			
			while (traits.size() < 3){
				Trait trait = Trait.getRandom();
				if (!traits.contains(trait))
					traits.add(trait);
			}
			
			monsterTraits.put(biome, traits);
		}
	}
	
	public Item knuckles(){
		Item w = new Item("Knuckes", ')', Tile.WHITE_ROCK.background());
		w.addTrait(Trait.COMBO_ATTACK);
		return w;
	}
	
	public Item knife(){
		Item w = new Item("Knife", ')', Tile.WHITE_ROCK.background());
		w.addTrait(Trait.EVADE_ATTACK);
		return w;
	}
	
	public Item club(){
		Item w = new Item("Club", ')', Tile.BROWN_ROCK.background());
		w.addTrait(Trait.CIRCLE_ATTACK);
		return w;
	}
	
	public Item sword(){
		Item w = new Item("Sword", ')', AsciiPanel.white);
		return w;
	}
	
	public Item spear(){
		Item w = new Item("Spear", ')', Tile.BROWN_ROCK.background());
		w.addTrait(Trait.REACH_ATTACK);
		return w;
	}
	
	public Item staff(){
		Item w = new Item("Staff", ')', Tile.BROWN_ROCK.background());
		w.addTrait(Trait.COUNTER_ATTACK);
		return w;
	}

	public Item weapon() {
		switch ((int)(Math.random() * 6)){
		case 0: return knuckles();
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
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		goblin.addTrait(Trait.WALKER);
		
		world.add(goblin);
		goblin.equip(world, weapon());
		return goblin;
	}
	
	private int monstersCreated = 0;
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
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case PINE_TREE1:
			name = "alpine monster";
			color = biome.color().darker();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE1:
			name = "boreal monster";
			color = biome.color().darker();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE4:
			name = "forest monster";
			color = biome.color();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_TREE1:
			name = "pale monster";
			color = biome.color().brighter();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case GREEN_ROCK:
			name = "hill monster";
			color = biome.background().brighter();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_ROCK:
			name = "mountan monster";
			color = biome.background().brighter();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_ROCK:
			name = "snow monster";
			color = biome.background().brighter();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case DESERT_SAND1:
			name = "desert monster";
			color = biome.color();
			candidates = world.screensOfType(biome);
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WATER1:
			name = "water monster";
			color = biome.color().brighter();
			break;
		}
		
		boolean isBigMonster = Math.random() * 1000 < (monstersCreated - 200);
		
		Creature monster = new Creature(name, isBigMonster ? 'M' : 'm', color, 3){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		
		if (biome == Tile.WATER1){
			monster.addTrait(Trait.SWIMMER);
		} else {
			monster.addTrait(Trait.WALKER);
		}
		
		for (Trait trait : monsterTraits.get(biome))
			monster.addTrait(trait);
		
		if (isBigMonster){
			monster.addTrait(Trait.EXTRA_HP);
			
			Trait trait = Trait.getRandom();
			while (monster.hasTrait(trait))
				trait = Trait.getRandom();
			monster.addTrait(trait);
		}
		
		if (candidate == null)
			world.add(monster);
		else
			world.addToScreen(monster, candidate.x, candidate.y);
		
		monstersCreated++;
		
		return monster;
	}

	public Creature player(World world) {
		Creature player = new Creature("player", '@', AsciiPanel.brightWhite, 10);
		player.addTrait(Trait.WALKER);
		world.add(player);
		return player;
	}
	
	public Creature zora(World world){
		Creature zora = new Creature("zora", 'z', Tile.WATER1.color(), 1);
		zora.addTrait(Trait.SWIMMER);
		zora.addTrait(Trait.HIDER);
		zora.addTrait(Trait.TERRITORIAL);
		zora.addTrait(Trait.ROCK_SPITTER);
		world.add(zora);
		return zora;
	}

	public Item heavyArmor() {
		Item item = new Item("heavy armor", '[', AsciiPanel.white);
		item.addTrait(Trait.EXTRA_DEFENSE);
		return item;
	}

	public Item greenTunic() {
		Item item = new Item("green tunic", '[', Tile.GREEN_ROCK.background());
		return item;
	}

	public Item wizardRobe() {
		Item item = new Item("wizard robes", '[', AsciiPanel.white);
		item.addTrait(Trait.DETECT_CAMOUFLAGED);
		return item;
	}

	public Item cloak() {
		Item item = new Item("dark cloak", '[', AsciiPanel.brightBlack);
		item.addTrait(Trait.DEFLECT_RANGED);
		return item;
	}

	public Item gillsuit() {
		Item item = new Item("gillsuit", '[', Tile.WATER1.color());
		item.addTrait(Trait.SWIMMER);
		return item;
	}

	public Item vestments() {
		Item item = new Item("holy vestments", '[', AsciiPanel.white);
		item.addTrait(Trait.REGENERATES);
		return item;
	}
}
