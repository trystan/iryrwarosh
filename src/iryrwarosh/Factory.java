package iryrwarosh;

import iryrwarosh.screens.CastAdvancedSpellScreen;
import iryrwarosh.screens.CastSpellScreen;
import iryrwarosh.screens.Screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Factory {
	private HashMap<Tile,List<Trait>> monsterTraits;
	private List<Item> minibossLoot;
	
	public Factory(){
		setMonsterTraits();
		minibossLoot = new ArrayList<Item>();
		minibossLoot.add(this.ringOfRegeneration());
		minibossLoot.add(this.magicCape());
		minibossLoot.add(this.advancedSpellBook());
		minibossLoot.add(this.heartIncrease());
		minibossLoot.add(this.heartIncrease());
		minibossLoot.add(this.heartIncrease());
		minibossLoot.add(this.heartIncrease());
		minibossLoot.add(this.heartIncrease());
		Collections.shuffle(minibossLoot);
	}
	
	private void setMonsterTraits(){
		monsterTraits = new HashMap<Tile,List<Trait>>();
		
		for (Tile biome : new Tile[]{ Tile.GREEN_TREE1, Tile.BROWN_TREE1, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 }){
			
			List<Trait> traits = new ArrayList<Trait>();
			
			switch ((int)(Math.random() * 8)){
			case 0: traits.add(Trait.POISONOUS); break;
			case 1: traits.add(Trait.AGGRESSIVE); break;
			case 2: traits.add(Trait.DOUBLE_ATTACK); break;
			case 3: traits.add(Trait.DOUBLE_MOVE); break;
			}
			
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
		Item item = new Item("knuckles", ')', Tile.WHITE_ROCK.background(), "Allows you to do combo attacks.");
		item.addTrait(Trait.COMBO_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item knife(){
		Item item = new Item("knife", ')', Tile.WHITE_ROCK.background(), "Allows you to attack when you evade.");
		item.addTrait(Trait.EVADE_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item club(){
		Item item = new Item("club", ')', Tile.BROWN_ROCK.background(), "Allows you to do a circular attack.");
		item.addTrait(Trait.CIRCLE_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item sword(){
		Item item = new Item("sword", ')', AsciiPanel.white, "Use to shoot if you are at full health."){
			private Projectile last;
			
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.hearts() != owner.maxHearts())
					return screen;
				
				if (last != null && !last.isDone())
					return screen;
				
				Point dir = owner.lastMovedDirection();
				char glyph = 9;
				if (dir.x == -1 && dir.y == -1 || dir.x == 1 && dir.y == 1)
					glyph = '\\';
				else if (dir.x == 1 && dir.y == -1 || dir.x == -1 && dir.y == 1)
					glyph = '/';
				else if (dir.x == 0 && (dir.y == -1 || dir.y == 1))
					glyph = 179;
				else if ((dir.x == -1 || dir.x == 1) && dir.y == 0)
					glyph = 196;
				
				last = new Projectile(owner, glyph, AsciiPanel.brightWhite, 1, owner.position.copy(), dir){
					public boolean canEnter(Tile tile){
						return tile.isGround() || tile.isWater();
					}
				};
				world.add(last);
				return screen;
			}
		};
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item spear(){
		Item item = new Item("spear", ')', Tile.BROWN_ROCK.background(), "Allows you to attack anything moving within reach.");
		item.addTrait(Trait.REACH_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item staff(){
		Item item = new Item("staff", ')', Tile.BROWN_ROCK.background(), "Allows you to counter attack.");
		item.addTrait(Trait.COUNTER_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
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
	
	public Creature miniboss(World world, int sx, int sy){
		char[] glyphs = { 130, 131, 132, 133, 134, 136, 137, 138, 139, 140, 141, 
				          147, 148, 149, 150, 151, 152, 160, 161, 162, 163 };
		
		char glyph = glyphs[(int)(Math.random() * glyphs.length)];
		
		int hue = (int)(Math.random() * 360);
		int hp = 5 + (int)(Math.random() * 6);
		
		List<Trait> traits = new ArrayList<Trait>();
		traits.add(Trait.WALKER);
		traits.add(Trait.TERRITORIAL);
		traits.add(Trait.HUNTER);
		traits.add(Trait.REGENERATES);
		traits.add(Trait.MYSTERIOUS);
		
		while (traits.size() < 8){
			Trait trait = Trait.getRandom();
			if (!traits.contains(trait))
				traits.add(trait);
		}

		Creature boss = new Creature("miniboss", glyph, Tile.hsv(hue, 33, 66), hp){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};

		for (Trait trait : traits)
			boss.addTrait(trait);
		
		world.addToScreen(boss, sx, sy);
		
		if (Math.random() < 0.25)
			boss.swapLeftHand(world, weapon());
		if (Math.random() < 0.25)
			boss.swapRightHand(world, weapon());
		
		if (minibossLoot.size() > 0)
			boss.setLoot(minibossLoot.remove(0));
		else
			boss.setLoot(weapon());
		
		return boss;
	}
	
	public Creature goblin(final World world){
		int hue = 30 + (int)(Math.random() * 90);
		Creature goblin = new Creature("goblin", 'g', Tile.hsv(hue, 50, 50), 3){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		goblin.addTrait(Trait.WALKER);
		goblin.addTrait(Trait.HUNTER);
		
		world.add(goblin);
		goblin.swapLeftHand(world, weapon());
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
		
		Creature monster = new Creature(name, isBigMonster ? 'M' : 'm', color, isBigMonster ? 7 : 3){
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
		player.addTrait(Trait.LOOTLESS);
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
		Item item = new Item("crystal ball", '+', AsciiPanel.white, "Allows you too see anything camouflaged."){
			public Screen use(Screen screen, World world, Creature player) {
				String text = "You see nothing special";
				
				MessageBus.publish(new Note(world, player, "You look into the crystal ball: " + text));
				return screen;
			}
		};
		item.addTrait(Trait.DETECT_CAMOUFLAGED);
		return item;
	}

	public Item bow() {
		Item item = new Item("bow", ')', AsciiPanel.brightBlack, "Use to shoot arrows. Cost 1 rupee per shot."){
			public Screen use(Screen screen, World world, Creature owner){
				world.add(new Projectile(owner, 9, AsciiPanel.brightWhite, 1, owner.position.copy(), owner.lastMovedDirection()));
				owner.pay(world, 1);
				return screen;
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
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.money() < 5)
					return screen;
				
				if (owner.isPoisoned()){
					owner.curePoison();
					owner.pay(world, 5);
				} else {
					int diff = Math.min(owner.money() / 5, owner.maxHearts() - owner.hearts());
					owner.heal(diff);
					owner.pay(world, diff * 5);
				}
				return screen;
			}
		};
		return item;
	}
	
	public Item ringOfRegeneration() {
		Item item = new Item("ring of regeneration", '=', Tile.LAVA1.color(), "Automatically recover health.");
		item.addTrait(Trait.REGENERATES);
		return item;
	}

	public Item spellBook() {
		Item item = new Item("spellbook", '+', AsciiPanel.white, "Use to cast one of 3 spells."){
			public Screen use(Screen screen, World world, Creature owner){
				return new CastSpellScreen(screen, world, owner);
			}
		};
		return item;
	}
	
	public Item magicCape() {
		Item item = new Item("magic cape", '[', AsciiPanel.red, "You can fly with this.");
		item.addTrait(Trait.FLIER);
		return item;
	}

	public Item advancedSpellBook() {
		Item item = new Item("anvanced spellbook", '+', AsciiPanel.white, "Use to cast one of 3 advanced spells."){
			public Screen use(Screen screen, World world, Creature owner){
				return new CastAdvancedSpellScreen(screen, world, owner);
			}
		};
		return item;
	}
	
	public Item heartIncrease(){
		Item item = new Item("heart increase", 3, AsciiPanel.brightRed, "increases your max hearts."){
			public void onCollide(World world, Creature collider){
				if (collider.glyph() != '@')
					return;
				
				collider.increaseMaxHearts(1);
				collider.heal(100);
				world.removeItem(collider.position.x, collider.position.y);
			}
		};
		return item;
	}
}
