package iryrwarosh;

import iryrwarosh.screens.Screen;

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
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.BROWN_TREE1, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			
			List<Trait> traits = new ArrayList<Trait>();

			if (biome == Tile.WATER1){
				traits.add(Trait.SWIMMER);
			} else {
				traits.add(Trait.WALKER);
			}
			
			while (traits.size() < 4){
				Trait trait = Trait.getRandom();
				if (!traits.contains(trait))
					traits.add(trait);
			}
			
			monsterTraits.put(biome, traits);
		}
	}
	
	public Item knuckles(){
		Item w = new Item("knuckles", ')', Tile.WHITE_ROCK.background(), "Allows you to do combo attacks.");
		w.addTrait(Trait.COMBO_ATTACK);
		return w;
	}
	
	public Item knife(){
		Item w = new Item("knife", ')', Tile.WHITE_ROCK.background(), "Allows you to attack when you evade.");
		w.addTrait(Trait.EVADE_ATTACK);
		return w;
	}
	
	public Item club(){
		Item w = new Item("club", ')', Tile.BROWN_ROCK.background(), "Allows you to do a circular attack.");
		w.addTrait(Trait.CIRCLE_ATTACK);
		return w;
	}
	
	public Item sword(){
		Item w = new Item("sword", ')', AsciiPanel.white, "Use to shoot if you are at full health."){
			private Projectile last;
			
			public void use(Screen screen, World world, Creature owner){
				if (owner.hp() != owner.maxHp())
					return;
				
				if (last != null && !last.isDone())
					return;
				
				last = new Projectile(owner, 9, AsciiPanel.brightWhite, 1, owner.position.copy(), owner.lastMovedDirection()){
					public boolean canEnter(Tile tile){
						return tile.isGround() || tile.isWater();
					}
				};
				world.add(last);
			}
		};
		return w;
	}
	
	public Item spear(){
		Item w = new Item("spear", ')', Tile.BROWN_ROCK.background(), "Allows you to attack anything moving within reach.");
		w.addTrait(Trait.REACH_ATTACK);
		return w;
	}
	
	public Item staff(){
		Item w = new Item("staff", ')', Tile.BROWN_ROCK.background(), "Allows you to counter attack.");
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
		Creature goblin = new Creature("goblin", 'g', Tile.hsv(hue, 50, 50), 4){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		goblin.addTrait(Trait.WALKER);
		goblin.addTrait(Trait.HUNTER);
		
		world.add(goblin);
		goblin.equip(world, weapon());
		return goblin;
	}
	
	private int monstersCreated = 0;
	public Creature monster(final World world, Tile biome){
		Color color = null;
		List<Point> candidates = new ArrayList<Point>();
		Point candidate = null;
		String name = null;
		
		switch (biome) {
		case GREEN_TREE1:
			name = "evergreen monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.GREEN_TREE1));
			candidates.addAll(world.screensOfType(Tile.PINE_TREE1));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE1:
			name = "forest monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.BROWN_TREE1));
			candidates.addAll(world.screensOfType(Tile.BROWN_TREE4));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_TREE1:
			name = "boreal monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.WHITE_TREE1));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case GREEN_ROCK:
			name = "hill monster";
			color = biome.background().darker();
			candidates.addAll(world.screensOfType(Tile.GREEN_ROCK));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_ROCK:
			name = "mountain monster";
			color = biome.background().darker();
			candidates.addAll(world.screensOfType(Tile.BROWN_ROCK));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_ROCK:
			name = "glacier monster";
			color = biome.background().darker();
			candidates.addAll(world.screensOfType(Tile.WHITE_ROCK));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case DESERT_SAND1:
			name = "desert monster";
			color = biome.color();
			candidates.addAll(world.screensOfType(Tile.DESERT_SAND1));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WATER1:
			name = "water monster";
			color = biome.color();
			break;
		}
		
		boolean isBigMonster = Math.random() * 1000 < (monstersCreated - 200);
		
		Creature monster = new Creature(name, isBigMonster ? 'M' : 'm', color, isBigMonster ? 5 : 2){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		
		for (Trait trait : monsterTraits.get(biome))
			monster.addTrait(trait);
		
		if (isBigMonster){
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
		Item item = new Item("heavy armor", '[', AsciiPanel.white, "Reduces damage done from heavy hitters.");
		item.addTrait(Trait.EXTRA_DEFENSE);
		return item;
	}

	public Item shield() {
		Item item = new Item("shield", '[', AsciiPanel.yellow, "Deflects projectiles half of the time.");
		item.addTrait(Trait.DEFLECT_RANGED);
		return item;
	}

	public Item crystalBall() {
		Item item = new Item("crystal ball", '+', AsciiPanel.white, "Allows you too see anything camouflaged.");
		item.addTrait(Trait.DETECT_CAMOUFLAGED);
		return item;
	}

	public Item bow() {
		Item item = new Item("bow", ')', AsciiPanel.brightBlack, "Use to shoot arrows. Cost 1 rupee per shot."){
			public void use(Screen screen, World world, Creature owner){
				world.add(new Projectile(owner, 9, AsciiPanel.brightWhite, 1, owner.position.copy(), owner.lastMovedDirection()));
				owner.pay(world, 1);
			}
		};
		return item;
	}

	public Item snorkel() {
		Item item = new Item("snorkel", '/', Tile.WATER1.color(), "Allows you to swim in the water.");
		item.addTrait(Trait.SWIMMER);
		return item;
	}

	public Item firstAidKit() {
		Item item = new Item("first aid kit", '+', AsciiPanel.white, "Use to cure poison and recover health (5 rupees)."){
			public void use(Screen screen, World world, Creature owner){
				if (owner.isPoisoned()){
					owner.curePoison();
					owner.pay(world, 5);
				} else {
					int diff = Math.min(5, owner.maxHp() - owner.hp());
					owner.heal(diff);
					owner.pay(world, diff * 5);
				}
			}
		};
		item.addTrait(Trait.REGENERATES);
		return item;
	}

	public Item spellBook() {
		Item item = new Item("spellbook", '+', AsciiPanel.white, "Use to cast one of 3 spells.");
		item.addTrait(Trait.SPELL_CASTER);
		return item;
	}
}
