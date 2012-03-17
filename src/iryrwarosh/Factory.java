package iryrwarosh;

import iryrwarosh.screens.CastSpellScreen;
import iryrwarosh.screens.DiveScreen;
import iryrwarosh.screens.JumpScreen;
import iryrwarosh.screens.Screen;
import iryrwarosh.screens.SneakScreen;
import iryrwarosh.screens.WorldMapScreen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Factory {
	private HashMap<Tile,List<Trait>> monsterTraits;
	private List<Item> minibossLoot;
	private List<Creature> rivals;
	private int defaultHearts;
	
	public Factory(){
		defaultHearts = 15;
		setMonsterTraits();
		
		minibossLoot = new ArrayList<Item>();
		minibossLoot.add(this.ringOfRegeneration());
		minibossLoot.add(this.magicCarpet());
		minibossLoot.add(this.mutationRing());
		minibossLoot.add(this.ringOfEvasion());
		minibossLoot.add(this.darkCloak());
		minibossLoot.add(this.rupeeMachine());
		minibossLoot.add(this.rupeeAmulet());
		minibossLoot.add(this.spikedArmor());
		minibossLoot.add(this.bagOfImps());
		minibossLoot.add(this.magicWand());
		minibossLoot.add(this.bombs());
		Collections.shuffle(minibossLoot);
		
		rivals = new ArrayList<Creature>();
		rivals.add(rival("Lame",   0, knife(), bow(), new RivalAi(0.01, 1.0)));
		rivals.add(rival("Dork",  30, staff(), null, new RivalAi(0.00, 1.0)));
		rivals.add(rival("Max" + (char)4, 60, knife(), ringOfEvasion(), new RivalAi(0.005, 0.5)));
		rivals.add(rival("Lulz",  90, club(), null, new RivalAi(0.05, 1.0)));
		rivals.add(rival("Link", 120, sword(), shield(), new RivalAi(0.005, 0.1)));
		rivals.add(rival("Bela", 150, spear(), null, new RivalAi(0.01, 0.5)));
		rivals.add(rival("L33t", 180, knife(), club(), new RivalAi(0.01, 0.5)));
		rivals.add(rival("Mr X", 210, darkCloak(), knife(), new RivalAi(0.005, 0.0)));
		rivals.add(rival("Toad", 240, club(), snorkel(), new RivalAi(0.01, 0.5)));
		rivals.add(rival("Tify", 270, spear(), spectacles(), new RivalAi(0.01, 0.5)));
		rivals.add(rival("Muto", 130, staff(), mutationRing(), new RivalAi(0.01, 0.5)));
		rivals.add(rival("Al  ", 330, magicCarpet(), null, new RivalAi(0.01, 0.5)));
		Collections.shuffle(rivals);
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
			
			switch ((int)(Math.random() * 4)){
			case 0: traits.add(Trait.POISONOUS); break;
			case 1: traits.add(Trait.AGGRESSIVE); break;
			case 2: traits.add(Trait.DOUBLE_ATTACK); break;
			case 3: traits.add(Trait.DOUBLE_MOVE); break;
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
			
			public void update(World world, Creature owner){
				super.update(world, owner);
				
				if (poisonCounter > 0)
					poisonCounter--;
				
				if (poisonCounter == 0 && hasTrait(Trait.POISONOUS)) {
					removeTrait(Trait.POISONOUS);
					if (owner != null)
						MessageBus.publish(new Note(world, owner, "Your knife is no longer poisonous."));
				}
			}

			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 1) {
					MessageBus.publish(new Note(world, owner, "You need more than 1 rupee or heart to poison your knife."));
					return screen;
				}
				
				if (!hasTrait(Trait.POISONOUS))
					addTrait(Trait.POISONOUS);

				owner.loseRupees(world, 1);
				poisonCounter += 3;
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
				if (owner.rupees() + owner.hearts() <= 1) {
					MessageBus.publish(new Note(world, owner, "You need more than 1 rupee or heart to do a circle attack."));
					return screen;
				}
				
				for (Point p : owner.position.neighbors()){
					Creature other = world.creature(p.x, p.y);
					if (other == null)
						continue;
					
					owner.attack(world, other, "with a wide swing");
				}
				owner.loseRupees(world, 1);
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
				if (dir.x == 0 && dir.y == 0)
					dir = new Point(0, 1);
				
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
		Item item = new Item("spear", ')', Tile.BROWN_ROCK.background(), "Long melee weapon that auto-attacks near you. Can be thrown."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 2) {
					MessageBus.publish(new Note(world, owner, "You need more than 2 rupees or hearts to throw a spear."));
					return screen;
				}

				Point dir = owner.lastMovedDirection();
				if (dir.x == 0 && dir.y == 0)
					dir = new Point(0, 1);
				
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
		item.addTrait(Trait.REACH_ATTACK);
		item.addTrait(Trait.STRONG_ATTACK);
		return item;
	}
	
	public Item staff(){
		Item item = new Item("long staff", ')', Tile.BROWN_ROCK.background(), "Long melee weapon that can also parry and counter attack.");
		item.addTrait(Trait.COUNTER_ATTACK);
		item.addTrait(Trait.REACH_ATTACK);
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
		int hp = defaultHearts / 2 + (int)(Math.random() * defaultHearts / 2);
		
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
		
		Creature boss = new Creature(makeMinibossName(traits), glyph, Common.hsv(hue, 33, 66), hp){
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
		else {
			switch ((int)(Math.random() * 4)){
			case 0: boss.setLoot(heartContainer()); break;
			case 1: boss.setLoot(bigMoney()); break;
			case 2: boss.setLoot(evasionPotion()); break;
			case 3: boss.setLoot(this.lostArtifact()); break;
			}
		}
		
		return boss;
	}

	private String makeMinibossName(List<Trait> traits) {
		
		String[] first = { "giant", "ugly", "malformed", "putrid", "rotten", "infested", "ancient", "crazed", "wild", "hated" };

		String[] second = { "spider", "squid", "jellyfish", "bird", "urchin", "demon", "scorpion", "insect", "blob",
				"evil", "best", "serpent", "monster" };
		
		String name = first[(int)(Math.random() * first.length)]
		            + " " + second[(int)(Math.random() * second.length)];
		
		return name;
	}
	
	public Creature goblin(final World world){
		int hue = 30 + (int)(Math.random() * 90);
		Creature goblin = new Creature("goblin", 'g', Common.hsv(hue, 50, 50), 3){
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
		
		boolean isBigMonster = Math.random() * 1000 < (monstersCreated - 300);
		
		if (isBigMonster)
			name = "giant " + name;
		
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
		Creature player = new Creature("player", '@', AsciiPanel.brightWhite, defaultHearts);
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

	public Creature rival(World world){
		Creature rival = rivals.remove(0);
		world.add(rival);
		
		return rival;
	}
	
	public Creature rival(String name, int hue, Item item1, Item item2, final RivalAi ai){
		Creature rival = new Creature(name, '@', Common.hsv(hue, 80, 80), defaultHearts){
			public void update(World world){
				super.update(world);
				ai.update(world, this);
			}
		};
		
		rival.addTrait(Trait.WALKER);
		rival.addTrait(Trait.REGENERATES);

		if (item1 != null)
			rival.swapLeftHand(null, item1);
		
		if (item2 != null)
			rival.swapRightHand(null, item2);
		
		if (Math.random() < 0.5)
			rival.setLoot(heartContainer());
		else
			rival.setLoot(bigMoney());
		
		return rival;
	}

	public Item heavyArmor() {
		Item item = new Item("heavy armor", '[', AsciiPanel.yellow, -2, "Reduces damage from heavy hitters.");
		item.addTrait(Trait.EXTRA_DEFENSE);
		return item;
	}
	
	public Item spikedArmor() {
		Item item = new Item("spiked armor", '[', AsciiPanel.yellow, -2, "Reduces damage from heavy hitters. Also covered in spikes.");
		item.addTrait(Trait.EXTRA_DEFENSE);
		item.addTrait(Trait.SPIKED);
		return item;
	}

	public Item shield() {
		Item item = new Item("large shield", '[', AsciiPanel.yellow, -1, "Deflects projectiles. Can be used to scare others nearby."){
			public Screen use(Screen screen, World world, Creature player) {
				if (player.rupees() + player.hearts() <= 1){
					MessageBus.publish(new Note(world, player, "You need more than 1 rupee or heart to intimidate others."));
					return screen;
				}

				int number = 0;
				
				for (Creature c : world.creaturesNear(player)){
					if (c.isFriendlyTo(player) || c.isMiniboss())
						continue;
					
					number++;
					c.fleeFrom(player);
					player.loseRupees(world, 1);
				}
				
				switch(number){
				case 0:
					MessageBus.publish(new Note(world, player, "You swing wildly but didn't scare anyone away."));
					break;
				case 1:
					MessageBus.publish(new Note(world, player, "You swing wildly and scare 1 nearby creature."));
					break;
				default:
					MessageBus.publish(new Note(world, player, "You swing wildly and scare " + number + " nearby creatures."));
					break;
				}
				return screen;
			}
		};
		item.addTrait(Trait.DEFLECT_RANGED);
		return item;
	}

	public Item spectacles() {
		Item item = new Item("spectacles", '/', Common.hsv(220, 15, 50), "Allows you to see camouflaged creatures. Use to see far away."){
			public Screen use(Screen screen, World world, Creature player) {
				if (player.rupees() + player.hearts() <= 1){
					MessageBus.publish(new Note(world, player, "You need more than 1 rupee or heart to look far away."));
					return screen;
				}
				
				player.loseRupees(world, 1);
				return new WorldMapScreen(screen, world.map(), player.position, true);
			}
		};
		item.addTrait(Trait.DETECT_CAMOUFLAGED);
		return item;
	}

	public Item bow() {
		Item item = new Item("bow", ')', Common.hsv(45, 50, 50), "Shoots deady arrows."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 1){
					MessageBus.publish(new Note(world, owner, "You need more than 1 rupee or heart to shoot arrows."));
					return screen;
				}
				
				Point dir = owner.lastMovedDirection();
				if (dir.x == 0 && dir.y == 0)
					dir = new Point(0, 1);
				
				char glyph = 9;
				if (dir.x == -1 && dir.y == -1 || dir.x == 1 && dir.y == 1)
					glyph = '\\';
				else if (dir.x == 1 && dir.y == -1 || dir.x == -1 && dir.y == 1)
					glyph = '/';
				else if (dir.x == 0 && (dir.y == -1 || dir.y == 1))
					glyph = 179;
				else if ((dir.x == -1 || dir.x == 1) && dir.y == 0)
					glyph = 196;
				
				world.add(new Projectile("arrow", owner, glyph, AsciiPanel.white, 3, owner.position.copy(), owner.lastMovedDirection()));
				owner.loseRupees(world, 1);
				return screen;
			}
		};
		return item;
	}

	public Item bombs() {
		Item item = new Item("bombs", ')', Common.hsv(220, 75, 75), "Pay 3" + (char)4 + " to place a bomb."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 3){
					MessageBus.publish(new Note(world, owner, "You need more than 3 rupees and hearts to place a bomb."));
					return screen;
				}
				
				Point where = owner.position;
				
				world.add(bomb(owner, where.x, where.y), where.x, where.y);
				owner.loseRupees(world, 1);
				return screen;
			}
		};
		return item;
	}
	
	private Item bomb(final Creature owner, final int x, final int y) {
		Item item = new Item("bomb", 248, Common.hsv(220, 75, 75), "Stand back!"){
			int countdown = 3;
			
			public void update(World world, Creature owner){
				if (countdown-- == 0)
					explode(world);
			}
			
			private void explode(World world){
				Creature here = world.creature(x, y);
				if (here != null)
					here.loseHearts(world, owner, 5, "with a bomb", "You have been blown up by one of " + owner.name() + "'s bomb");
				
				for (Point p : new Point(x, y).neighbors()){
					here = world.creature(p.x, p.y);
					if (here != null)
						here.loseHearts(world, owner, 2, "with a bomb", "You have been blown up by one of " + owner.name() + "'s bomb");
				}
				world.removeItem(x, y);
				world.add(smoke(x, y), x, y);
				for (Point p : new Point(x, y).neighbors()){
					world.removeItem(p.x, p.y);
					world.add(smoke(p.x, p.y), p.x, p.y);
				}
			}
		};
		item.setCanBePickedUp(false);
		return item;
	}
	
	private Item smoke(final int x, final int y) {
		Item item = new Item("smoke", '*', Common.hsv(220, 0, 75), "Smoke from a bomb"){
			
			public void update(World world, Creature owner){
				world.removeItem(x, y);
			}
		};
		item.setCanBePickedUp(false);
		return item;
	}
	
	public Item snorkel() {
		Item item = new Item("snorkel", '/', Tile.WATER2.color(), "Allows you to swim. Can be used to swim underwater."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 1){
					MessageBus.publish(new Note(world, owner, "You need more than 1 rupee or heart to dive underwater."));
					return screen;
				} if (!world.tile(owner.position.x, owner.position.y).isSwimmable()){
					MessageBus.publish(new Note(world, owner, "You must be on a bridge or water to dive underwater."));
					return screen;
				}
				
				return new DiveScreen(screen, world, owner);
			}
		};
		item.addTrait(Trait.SWIMMER);
		return item;
	}

	public Item firstAidKit() {
		Item item = new Item("first aid kit", '+', Common.hsv(20, 50, 50), "Use to cure poison or recover health."){
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

	public Item rupeeMachine() {
		Item item = new Item("rupee machine", '/', Common.hsv(210, 25, 90), "Automatically creates rupees."){
			public void update(World world, Creature owner){
				super.update(world, owner);
				
				if (owner != null && Math.random() < 0.1)
					owner.gainRupees(1);
			}
		};
		return item;
	}
	
	public Item rupeeAmulet() {
		Item item = new Item("rupee amulet", '=', Common.hsv(210, 25, 90), "Can be used to convert your hearts into rupees."){
			public Screen use(Screen screen, World world, Creature owner){
				owner.loseHearts(world, owner, 1, null, "You turned the last of your hearts into rupees");
				owner.gainRupees(20);
				return screen;
			}
		};
		return item;
	}
	
	public Item spellBook() {
		Item item = new Item("spellbook", '+', Common.hsv(200, 50, 50), "Used to cast one of 3 basic spells."){
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
	
	public Item heartContainer(){
		Item item = new Item("heart container", 3, AsciiPanel.brightRed, "Increases your max hearts."){
			public void onCollide(World world, Creature collider){
				if (!collider.isHuman())
					return;
				
				collider.increaseMaxHearts(1);
				collider.recoverHearts(10);
				world.removeItem(collider.position.x, collider.position.y);
			}
		};
		item.collectableValue(15);
		return item;
	}
	
	public Item bigMoney(){
		Item item = new Item("rupees", 4, Common.hsv(210, 25, 90), "Rupees are used for special actions."){
			public void onCollide(World world, Creature collider){
				if (!collider.isHuman())
					return;
				
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainRupees(50);
			}
		};
		item.collectableValue(5);
		return item;
	}

	public Item evasionPotion(){
		Item item = new Item("evasion potion", '!', Common.hsv(90, 33, 66), "Permanently boost your evasion."){
			public void onCollide(World world, Creature collider){
				if (!collider.isHuman())
					return;
				
				world.removeItem(collider.position.x, collider.position.y);
				collider.modifyEvasion(1);
			}
		};
		item.collectableValue(5);
		return item;
	}

	public Item lostArtifact(){
		String[] first = { "ancient", "historic", "old", "lost", "antique", "famous", "missing", "forgotten", "stolen" };
		String[] second = { "heirloom", "artifact", "treasure", "item" };
		
		String name = first[(int)(Math.random() * first.length)]
		            + " " + second[(int)(Math.random() * second.length)];
		
		int hue = (int)(Math.random() * 360);
		Item item = new Item(name, '?', Common.hsv(hue, 33, 66), "A " + name + " from the past."){
			public void onCollide(World world, Creature collider){
				if (!collider.isHuman())
					return;
				
				world.removeItem(collider.position.x, collider.position.y);
				MessageBus.publish(new DiscoveredLostArtifact(world, collider, this));
			}
		};
		item.collectableValue(10);
		return item;
	}
	
	public Item jumpingBoots() {
		Item item = new Item("jumping boots", '[', Common.hsv(180, 50, 50), "Makes you more evasive and can be used to jump."){
			public Screen use(Screen screen, World world, Creature owner){
				return new JumpScreen(screen, world, owner);
			}
		};
		return item;
	}

	public Item ringOfEvasion() {
		Item item = new Item("ring of evasion", '=', Common.hsv(90, 33, 66), +5, "Makes you much more evasive.");
		return item;
	}
	
	public Item darkCloak() {
		Item item = new Item("dark cloak", '[', Common.hsv(180, 5, 25), +2, "Makes you harder to hit and see. Can be used to sneak."){
			public Screen use(Screen screen, World world, Creature owner){
				if (owner.rupees() < 5){
					MessageBus.publish(new Note(world, owner, "You need at least 5 rupees to sneak."));
					return screen;
				}
				
				return new SneakScreen(screen, world, owner);
			}
		};
		item.addTrait(Trait.CAMOUFLAGED);
		return item;
	}
	
	public Item bagOfImps() {
		Item item = new Item("bag of imps", '+', Common.hsv(350, 90, 75), "Costs 5" + (char)4 + " to pull an imp out."){
			public Screen use(Screen previous, World world, Creature owner){
				if (owner.rupees() + owner.hearts() <= 5){
					MessageBus.publish(new Note(world, owner, "You need more than 5 rupees and hearts to summon an imp."));
					return previous;
				}
				
				summonImp(world, owner);
				owner.loseRupees(world, 5);
				return previous;
			}
		};
		return item;
	}
	
	private void summonImp(World world, final Creature master) {
		Creature imp = new Creature("imp", (char)139, Common.hsv(350, 90, 75), 5){
			public void update(World world){
				super.update(world);
				if (position.distanceTo(master.position) > 3){
					int dx = 0;
					int dy = 0;
					if (master.position.x < position.x)
						dx = -1;
					else if (master.position.x > position.x)
						dx = 1;
					
					if (master.position.y < position.y)
						dy = -1;
					else if (master.position.y > position.y)
						dy = 1;
					
					moveBy(world, dx, dy);
				} else {
					wander(world);
				}
			}
			
			public boolean isFriendlyTo(Creature other){
				return other == master || other.name().equals(name());
			}
		};
		imp.addTrait(Trait.AGGRESSIVE);
		imp.addTrait(Trait.HUNTER);
		imp.addTrait(Trait.FLIER);
		imp.addTrait(Trait.REGENERATES);
		world.add(imp);
		Point dir = master.lastMovedDirection();
		imp.position = master.position.plus(dir.x, dir.y).plus(dir.x, dir.y);
	}
	
	public Item magicWand() {
		Item item = new Item("Magic Missile wand", '/', Common.hsv(0, 0, 75), "Use to shoot 8 weak magic missiles."){
			public Screen use(Screen previous, World world, Creature owner){
				magicMissiles(world, owner);
				owner.loseRupees(world, 8);
				return previous;
			}
		};
		return item;
	}
	
	private void magicMissiles(World world, Creature caster) {
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(1, 0), new Point( 1, 0)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(-1, 0), new Point(-1, 0)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(0, 1), new Point( 0, 1)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(0,-1), new Point( 0,-1)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(-1,-1), new Point(-1,-1)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(-1, 1), new Point(-1, 1)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(1,-1), new Point( 1,-1)));
		world.add(new Projectile("magic missile", caster, 7, Common.hsv(210, 33, 66), 2, caster.position.plus(1, 1), new Point( 1, 1)));
	}

	public Item mutationRing() {
		Item item = new Item("mutation ring", '=', Common.hsv(60, 33, 90), "A slight chance of random mutations when wearing this."){
			public void update(World world, Creature owner){
				super.update(null, owner);
				
				if (owner != null && Math.random() < 0.005)
					mutateSelf(world, owner);
			}
		};
		return item;
	}
	
	private void mutateSelf(World world, Creature self) {
		Trait[] traits = {
				Trait.CAMOUFLAGED, Trait.COUNTER_ATTACK, Trait.DEFLECT_MELEE, Trait.DEFLECT_RANGED, Trait.DETECT_CAMOUFLAGED,
				Trait.EVADE_ATTACK, Trait.EXTRA_DEFENSE, Trait.EXTRA_EVADE, Trait.EXTRA_HP, Trait.FLIER, Trait.KNOCKBACK, 
				Trait.POISONOUS, Trait.REACH_ATTACK, Trait.REGENERATES, Trait.SLOWING_ATTACK, Trait.SPIKED, Trait.STRONG_ATTACK,
				Trait.SWIMMER
		};
		
		int tries = 0;
		while (tries++ < 100){
			Trait trait = traits[(int)(Math.random() * traits.length)];
			if (self.hasTrait(trait))
				continue;
			
			self.addTrait(trait);
			MessageBus.publish(new Note(null, self, "You have gained the trait \"" + trait.description() + "\""));
			self.loseRupees(world, 10);
			break;
		}
	}
}

