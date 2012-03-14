package iryrwarosh;

import iryrwarosh.screens.CastAdvancedSpellScreen;
import iryrwarosh.screens.CastSpellScreen;
import iryrwarosh.screens.JumpScreen;
import iryrwarosh.screens.Screen;
import iryrwarosh.screens.ThrowItemScreen;

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
		minibossLoot.add(this.magicCarpet());
		minibossLoot.add(this.advancedSpellBook());
		minibossLoot.add(this.ringOfEvasion());
		minibossLoot.add(this.evasionPotion());
		minibossLoot.add(this.evasionPotion());
		minibossLoot.add(this.evasionPotion());
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
	
	public Item knife(){
		Item item = new Item("knife", ')', Tile.WHITE_ROCK.background(), "Melee weapon that attacks when you evade. Can be poisoned."){
			int poisonCounter = 0;
			World world;
			Creature owner;
			
			public void update(){
				super.update();
				
				if (poisonCounter > 0)
					poisonCounter--;
				
				if (poisonCounter == 0 && hasTrait(Trait.POISONOUS)) {
					removeTrait(Trait.POISONOUS);
					MessageBus.publish(new Note(world, owner, "Your knife is no longer poisonous."));
				}
			}

			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 5) {
					MessageBus.publish(new Note(world, owner, "You need more than 5 rupees or hearts to poison your knife."));
					return screen;
				}
				
				this.world = world;
				this.owner = owner;
				
				if (!hasTrait(Trait.POISONOUS))
					addTrait(Trait.POISONOUS);

				owner.loseRupees(world, 5);
				poisonCounter += 10;
				MessageBus.publish(new Note(world, owner, "Your knife is now poisonous for " + poisonCounter + " turns."));
				
				return screen;
			}
		};
		item.addTrait(Trait.EVADE_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item club(){
		Item item = new Item("club", ')', Tile.BROWN_ROCK.background(), "Melee weapon with knockback. Can do a circle attack."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 3) {
					MessageBus.publish(new Note(world, owner, "You need more than 3 rupees or hearts to shoot arrows."));
					return screen;
				}
				
				for (Point p : owner.position.neighbors()){
					Creature other = world.creature(p.x, p.y);
					if (other == null || owner.isFriendlyTo(other))
						continue;
					
					owner.attack(world, other, "with a wide swing");
				}
				return screen;
			}
		};
		item.addTrait(Trait.KNOCKBACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item sword(){
		Item item = new Item("sword", ')', AsciiPanel.white, "Simple melee weapon. Can also shoot at no cost to you."){
			private Projectile last;
			
			public Screen use(Screen screen, World world, Creature owner){
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
				
				last = new Projectile("sword", owner, glyph, AsciiPanel.brightWhite, 1, owner.position.copy(), dir){
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
		Item item = new Item("spear", ')', Tile.BROWN_ROCK.background(), "Melee weapon that auto-attacks near you. Can be thrown."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 2) {
					MessageBus.publish(new Note(world, owner, "You need more than 2 rupees or hearts to throw a spear."));
					return screen;
				} else
					return new ThrowItemScreen(screen, world, owner, this);
			}
		};
		item.addTrait(Trait.REACH_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item staff(){
		Item item = new Item("staff", ')', Tile.BROWN_ROCK.background(), "Melee weapon that can parry attacks and will counter attack.");
		item.addTrait(Trait.COUNTER_ATTACK);
		item.addTrait(Trait.DEFLECT_MELEE);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}

	public Item weapon() {
		switch ((int)(Math.random() * 5)){
		case 0: return knife();
		case 1: return club();
		case 2: return sword();
		case 3: return spear();
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
		
		boss.setIsMiniboss(true);
		
		world.addToScreen(boss, sx, sy);
		
		if (Math.random() < 0.25)
			boss.swapLeftHand(world, weapon());
		if (Math.random() < 0.25)
			boss.swapRightHand(world, weapon());
		
		if (minibossLoot.size() > 0)
			boss.setLoot(minibossLoot.remove(0));
		else if (Math.random() < 0.5)
			boss.setLoot(heartIncrease());
		else
			boss.setLoot(bigMoney());
		
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
			name = "green forest monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.GREEN_TREE1));
			candidates.addAll(world.screensOfType(Tile.PINE_TREE1));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_TREE1:
			name = "brown forest monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.BROWN_TREE1));
			candidates.addAll(world.screensOfType(Tile.BROWN_TREE4));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_TREE1:
			name = "white forest monster";
			color = biome.color().brighter();
			candidates.addAll(world.screensOfType(Tile.WHITE_TREE1));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case GREEN_ROCK:
			name = "green hill monster";
			color = biome.background().darker();
			candidates.addAll(world.screensOfType(Tile.GREEN_ROCK));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case BROWN_ROCK:
			name = "brown mountain monster";
			color = biome.background().darker();
			candidates.addAll(world.screensOfType(Tile.BROWN_ROCK));
			if (candidates.size() == 0)
				return null;
			candidate = candidates.get((int)(Math.random() * candidates.size()));
			break;
		case WHITE_ROCK:
			name = "white mountain monster";
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

	public Creature rival(World world, String name){
		int hue = (int)(Math.random() * 360);
		
		Creature rival = new Creature(name, '@', Tile.hsv(hue, 75, 75), 10){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		
		rival.addTrait(Trait.WALKER);
		rival.addTrait(Trait.HUNTER);
		rival.addTrait(Trait.REGENERATES);
		
		Trait trait = Trait.getRandom();
		while (rival.hasTrait(trait)){
			trait = Trait.getRandom();
		}
		rival.addTrait(trait);
		
		world.add(rival);
		
		rival.swapRightHand(world, weapon());
		
		if (minibossLoot.size() > 0) {
			rival.setLoot(minibossLoot.remove(0));
			rival.swapLeftHand(world, rival.loot());
		} else if (Math.random() < 0.5)
			rival.setLoot(heartIncrease());
		else
			rival.setLoot(bigMoney());
		
		return rival;
	}

	public Item heavyArmor() {
		Item item = new Item("heavy armor", '[', AsciiPanel.yellow, -2, "Reduces damage done from heavy hitters.");
		item.addTrait(Trait.EXTRA_DEFENSE);
		return item;
	}

	public Item shield() {
		Item item = new Item("shield", '[', AsciiPanel.yellow, -1, "Deflects projectiles half of the time.");
		item.addTrait(Trait.DEFLECT_RANGED);
		return item;
	}

	public Item crystalBall() {
		Item item = new Item("crystal ball", '+', Tile.hsv(220, 25, 25), "With this you will see anything camouflaged."){
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
		Item item = new Item("bow", ')', Tile.hsv(45, 50, 50), "Shoots arrows."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 1){
					MessageBus.publish(new Note(world, owner, "You need more than 1 rupee or heart to shoot arrows."));
					return screen;
				}
				
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
				
				world.add(new Projectile("arrow", owner, glyph, AsciiPanel.white, 1, owner.position.copy(), owner.lastMovedDirection()));
				owner.loseRupees(world, 1);
				return screen;
			}
		};
		return item;
	}

	public Item snorkel() {
		Item item = new Item("snorkel", '/', Tile.WATER1.background(), "Allows you to swim in the water.");
		item.addTrait(Trait.SWIMMER);
		return item;
	}

	public Item firstAidKit() {
		Item item = new Item("first aid kit", '+', Tile.hsv(20, 50, 50), "Use to cure poison or recover health."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() < 5){
					MessageBus.publish(new Note(world, owner, "You need at least 5 rupees to cure poison or heal yourself."));
					return screen;
				}
				
				if (owner.isPoisoned()){
					owner.curePoison();
					owner.loseRupees(world, 5);
				} else {
					int amount = Math.min(owner.rupees() / 5, owner.maxHearts() - owner.hearts());
					owner.recoverHearts(amount);
					owner.loseRupees(world, amount * 5);
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
		Item item = new Item("spellbook", '+', Tile.hsv(200, 50, 50), "Used to cast fireball, blink, and heart to rupees."){
			public Screen use(Screen screen, World world, Creature owner){
				return new CastSpellScreen(screen, world, owner);
			}
		};
		return item;
	}
	
	public Item magicCarpet() {
		Item item = new Item("magic carpet", Tile.WATER1.glyph(), AsciiPanel.red, +1, "You can fly on this.");
		item.addTrait(Trait.FLIER);
		return item;
	}

	public Item advancedSpellBook() {
		Item item = new Item("advanced spellbook", '+', AsciiPanel.white, "Use to cast one of 3 advanced spells."){
			public Screen use(Screen screen, World world, Creature owner){
				return new CastAdvancedSpellScreen(screen, world, owner);
			}
		};
		return item;
	}
	
	public Item heartIncrease(){
		Item item = new Item("heart increase", 3, AsciiPanel.brightRed, "Increases your max hearts."){
			public void onCollide(World world, Creature collider){
				if (collider.glyph() != '@')
					return; // only the player should be able to get these
				
				collider.increaseMaxHearts(1);
				collider.recoverHearts(100);
				world.removeItem(collider.position.x, collider.position.y);
			}
		};
		return item;
	}
	
	public Item bigMoney(){
		return new Item("rupees", 4, Tile.hsv(210, 25, 90), "Rupees are used for special actions."){
			public void onCollide(World world, Creature collider){
				if (collider.glyph() != '@')
					return; // only the player should be able to get these
				
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainRupees(50);
			}
		};
	}

	public Item evasionPotion(){
		return new Item("evasion potion", '!', Tile.hsv(90, 33, 66), "Permanently boost your evasion."){
			public void onCollide(World world, Creature collider){
				if (collider.glyph() != '@')
					return; // only the player should be able to get these
				
				world.removeItem(collider.position.x, collider.position.y);
				collider.modifyEvasion(1);
			}
		};
	}
	
	public Item heartPotion(){
		return new Item("heart potion", '!', Tile.hsv(0, 33, 66), "Refill all your hearts."){
			public void onCollide(World world, Creature collider){
				if (collider.glyph() != '@')
					return; // only the player should be able to get these
				
				world.removeItem(collider.position.x, collider.position.y);
				collider.recoverHearts(100);
			}
		};
	}
	
	public Item jumpingBoots() {
		Item item = new Item("jumping boots", '[', Tile.hsv(180, 50, 50), "Makes you more evasive and can be used to jump."){
			public Screen use(Screen screen, World world, Creature owner){
				return new JumpScreen(screen, world, owner);
			}
		};
		return item;
	}

	public Item ringOfEvasion() {
		Item item = new Item("ring of evasion", '=', Tile.hsv(90, 33, 66), +4, "Makes you much more evasive.");
		return item;
	}
}
