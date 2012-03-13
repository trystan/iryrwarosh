package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Creature {
	public Point position;
	public Point homeScreenPosition;
	
	public Point lastMovedDirection = new Point(0,0);
	public Point lastMovedDirection() { return lastMovedDirection.copy(); }
	
	private int regenerateCounter;
	private int poisonCounter;
	private Creature lastPoisonedBy;
	public boolean isPoisoned() { return poisonCounter > 0; }

	private String name;
	public String name() { return name; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }

	private int hp;
	public int hp() { return hp; }

	private int maxHp;
	public int maxHp() { return maxHp + (hasTrait(Trait.EXTRA_HP) ? 3 : 0); }

	private int money;
	public int money() { return money; }

	private int lastWanderX;
	private int lastWanderY;
	private boolean hasDoubleMovedThisTurn = false;
	private boolean hasDoubleAttackedThisTurn = false;
	
	private List<Trait> traits = new ArrayList<Trait>();

	public void addTrait(Trait trait) { traits.add(trait); }

	public boolean hasTrait(Trait trait) {
		return traits.contains(trait)
				|| leftHand != null && leftHand.hasTrait(trait)
				|| rightHand != null && rightHand.hasTrait(trait);
	}
	
	public String describe(){
		String text = "";
		
		if (hasTrait(Trait.MYSTERIOUS))
			text += ", ???";
		else {
			for (Trait trait : traits)
				text += ", " + trait.description();
		}
		
		text = name + " (" + text.substring(2) + ")";

		if (leftHand != null)
			text += " " + leftHand.name();
		
		if (rightHand != null)
			text += " " + rightHand.name();
		
		return text;
	}
	
	private Item leftHand;
	public Item leftHand() { return leftHand; }

	private Item rightHand;
	public Item rightHand() { return rightHand; }
	
	private Item loot;
	public Item loot() { return loot; }
	public void setLoot(Item loot) { this.loot = loot; }
	
	public int evadePercent(World world){
		int moveable = 0;
		
		for (int ox = -1; ox < 2; ox++)
		for (int oy = -1; oy < 2; oy++){
			if (ox==0 && oy==0)
				continue;
			
			if (canEnter(world.tile(position.x+ox, position.y+oy))
					&& world.creature(position.x+ox, position.y+oy) == null)
				moveable++;
		}
		
		return moveable * (hasTrait(Trait.EXTRA_EVADE) ? 8 : 5);
	}
	
	public boolean evadeCheck(World world){
		return Math.random() * 100 < evadePercent(world);
	}
	
	public Creature(String name, char glyph, Color color, int maxHp){
		this.name = name;
		this.glyph = glyph;
		this.color = color;
		this.maxHp = maxHp;
		this.hp = maxHp;
	}
	
	public boolean canEnter(Tile tile){
		if (hasTrait(Trait.WALKER) && tile.isGround())
			return true;
		
		if (hasTrait(Trait.SWIMMER) && tile.isSwimmable())
			return true;
		
		if (hasTrait(Trait.FLYER) && tile.isFlyable())
			return true;
		
		return false;
	}

	public void moveBy(World world, int x, int y) {
		if (x==0 && y==0)
			return;
		
		if (hasTrait(Trait.TERRITORIAL)
				&& ((position.x+x) / 19 != homeScreenPosition.x || (position.y+y) / 9 != homeScreenPosition.y))
			return;
		
		Creature other = world.creature(position.x+x, position.y+y);
		
		if (other == null) {
			if (canEnter(world.tile(position.x+x, position.y+y))){
				position.x += x;
				position.y += y;
				lastMovedDirection.x = x;
				lastMovedDirection.y = y;
				MessageBus.publish(new Moved(world, this));
			} else {
				lastWanderX = (int)(Math.random() * 3) - 1;
				lastWanderY = (int)(Math.random() * 3) - 1;
				MessageBus.publish(new BumpedIntoObsticle(world, this, position.x+x, position.y+y));
			}
		} else if (isFriend(other)) {
			return;
		} else if (hasDoubleAttackedThisTurn == false 
				&& other.evadeCheck(world)){
			other.evade(world, this);
		} else if (hasDoubleAttackedThisTurn == false){
			attack(world, other, "");
		}
	}
	
	public boolean isFriend(Creature other){
		return other.glyph == this.glyph;
	}
	
	public void attack(World world, Creature other, String specialType){
		if (other.hp < 1)
			return;
		
		Boolean isSpecial = false; // specialType != null && !specialType.equals("normal");

		other.hurt(world, this, hasTrait(Trait.STRONG_ATTACK) ? 2 : 1, specialType);

		if (!isSpecial && other.hasTrait(Trait.SPIKED)){
			MessageBus.publish(new HitSpikes(world, this, other));
			hurt(world, other, 1, null);
		}

		if (!isSpecial && hasTrait(Trait.POISONOUS) && other.hp > 0)
			other.poisonedBy(world, this);
		
		if (!isSpecial && hasTrait(Trait.DOUBLE_ATTACK)
				&& !hasDoubleAttackedThisTurn
				&& other.hp > 0) {
			hasDoubleAttackedThisTurn = true;
			attack(world, other, "again");
		}
	}
	
	private void poisonedBy(World world, Creature attacker){
		poisonCounter += 10;
		lastPoisonedBy = attacker;
		MessageBus.publish(new Poisoned(world, attacker, this));
	}
	
	public void curePoison() {
		poisonCounter = 0;
	}
	
	public void evade(World world, Creature other){
		List<Point> evadeTo = new ArrayList<Point>();
		
		for (int ox = -1; ox < 2; ox++)
		for (int oy = -1; oy < 2; oy++){
			if (ox==0 && oy==0)
				continue;
			
			if (canEnter(world.tile(position.x+ox, position.y+oy))
					&& world.creature(position.x+ox, position.y+oy) == null)
				evadeTo.add(new Point(position.x+ox, position.y+oy));
		}
		
		Collections.shuffle(evadeTo);
		position = evadeTo.get(0);
		MessageBus.publish(new Evaded(world, other, this));
	}
	
	private Point hiddenPoint;
	private int hiddenCounter;
	private int projectileCooldown;
	
	public void update(World world){
		hasDoubleAttackedThisTurn = false;
		hasDoubleMovedThisTurn = false;
		
		if (homeScreenPosition == null)
			homeScreenPosition = new Point(position.x / 19, position.y / 9); 
				
		if (poisonCounter > 0){
			if (poisonCounter-- % 5 == 0)
				hurt(world, lastPoisonedBy, 1, null);
		}
		
		if (hasTrait(Trait.REGENERATES) && --regenerateCounter < 0){
			regenerateCounter = 10;
			if (hp < maxHp)
				heal(1);
		}
		
		if (hasTrait(Trait.HIDER) && hiddenCounter-- < 1){
			hiddenCounter = 5 + (int)(Math.random() * 5);
			if (hiddenPoint == null)
				hide(world);
			else
				unhide(world);
		}
		
		if (hasTrait(Trait.ROCK_SPITTER) 
				&& projectileCooldown-- < 1 
				&& Math.random() < 0.1)
			spitRock(world);
	}

	private void spitRock(World world) {
		while (lastWanderX == 0 && lastWanderY == 0){
			lastWanderX = (int)(Math.random() * 3) - 1;
			lastWanderY = (int)(Math.random() * 3) - 1;
		}
		
		projectileCooldown += 10 + (int)(Math.random() * 10);
		world.add(new Projectile(this,   7, AsciiPanel.brightYellow, 1, position.plus(lastWanderX, lastWanderY), new Point(lastWanderX, lastWanderY)));
		world.add(new Projectile(this, 250, AsciiPanel.brightYellow, 0, position.copy(), new Point(lastWanderX, lastWanderY)));
	}

	private void unhide(World world) {
		int x = 0;
		int y = 0;
		while (hiddenPoint != null){
			if (hasTrait(Trait.TERRITORIAL)){
				x = homeScreenPosition.x * 19 + (int)(Math.random() * 19);
				y = homeScreenPosition.y *  9 + (int)(Math.random() * 9);	
			} else {
				x = hiddenPoint.x + (int)(Math.random() * 7) - 3;
				y = hiddenPoint.y + (int)(Math.random() * 7) - 3;
			}
			
			if (!canEnter(world.tile(x, y)) || world.creature(x, y) != null)
				continue;
			
			position.x = x;
			position.y = y;
			hiddenPoint = null;
		}
		
		MessageBus.publish(new Unhid(world, this));
		if (hasTrait(Trait.ROCK_SPITTER))
			spitRock(world);
	}

	private void hide(World world) {
		hiddenPoint = new Point(position.x, position.y);
		MessageBus.publish(new Hid(world, this));
		position.x = -100;
		position.y = -100;
	}

	public void wander(World world){
		if (prey != null && prey.hp < 1)
			prey = null;
		else if (prey != null && prey.hp > 0)
			moveTo(world, prey.position);

		if (preditor != null && preditor.hp < 1)
			preditor = null;
		else if (preditor != null && preditor.hp > 0)
			moveFrom(world, preditor.position);
		
		else if (hasTrait(Trait.AGGRESSIVE))
			fightNearby(world);
		else
			wanderForReal(world);
	}

	private void moveTo(World world, Point target) {
		if (target.x < 0 || target.y < 0) {
			wanderForReal(world);
		} else {
			int mx = (int)Math.signum(target.x - position.x);
			int my = (int)Math.signum(target.y - position.y);
			
			if (canEnter(world.tile(position.x+mx, position.y+my)))
				moveBy(world, mx, my);
			else
				wanderForReal(world);
		}
	}
	
	private void moveFrom(World world, Point target) {
		if (target.x < 0 || target.y < 0) {
			wanderForReal(world);
		} else {
			int mx = -(int)Math.signum(target.x - position.x);
			int my = -(int)Math.signum(target.y - position.y);
			
			if (canEnter(world.tile(position.x+mx, position.y+my)))
				moveBy(world, mx, my);
			else
				wanderForReal(world);
		}
	}

	
	private void wanderForReal(World world) {
		if (Math.random() < 0.33)
			lastWanderX = (int)(Math.random() * 3) - 1;
		if (Math.random() < 0.33)
			lastWanderY = (int)(Math.random() * 3) - 1;
		
		moveBy(world, lastWanderX, lastWanderY);
		
		if (hasTrait(Trait.DOUBLE_MOVE) && !hasDoubleMovedThisTurn){
			hasDoubleMovedThisTurn = true;
			wanderForReal(world);
		}
	}
	
	public void fightNearby(World world){
		List<Creature> candidates = new ArrayList<Creature>();
		
		for (Point p : position.neighbors()){
			Creature other = world.creature(p.x, p.y);
			if (other != null && !isFriend(other))
				candidates.add(other);
		}

		if (candidates.size() == 0)
			wanderForReal(world);
		else {
			Creature other = candidates.get((int)(Math.random() * candidates.size()));
			hunt(other);
			attack(world, other, "aggressively");
		}
	}

	public void finishingKill(World world, Creature other) {
		other.hurt(world, this, 1000, null);
	}

	public void heal(int i) {
		hp = Math.min(hp + i, maxHp);
	}

	public void gainMoney(int i) {
		money = Math.min(money + i, 255);
	}

	public void hurt(World world, Creature attacker, int i, String specialType) {
		if (i > 1 && hasTrait(Trait.EXTRA_DEFENSE)){
			i--;
			MessageBus.publish(new BlockSomeDamage(world, this, rightHand));
		}
		
		hp -= i;

		if (specialType != null)
			MessageBus.publish(new Attacked(world, attacker, this, specialType));
		
		if (hp < 1)
			MessageBus.publish(new Killed(world, attacker, this));
		else if (hasTrait(Trait.SOCIAL))
			MessageBus.publish(new CallForHelp(world, this, attacker));
	}

	private Creature prey;
	public void hunt(Creature prey) {
		this.prey = prey;
	}

	private Creature preditor;
	public void fleeFrom(Creature preditor) {
		this.preditor = preditor;
	}
	
	public void pay(World world, int i) {
		money -= i;
		
		if (money < 0) {
			hurt(world, this, 0 - money, null);
			money = 0;
		}
	}

	public void swapLeftHand(World world, Item item) {
		world.removeItem(position.x, position.y);
		world.add(leftHand, position.x, position.y);
		if (leftHand != null)
			MessageBus.publish(new DroppedWeapon(world, this, leftHand));
		leftHand = item;
		MessageBus.publish(new EquipedItem(world, this, item));
	}

	public void swapRightHand(World world, Item item) {
		world.removeItem(position.x, position.y);
		world.add(rightHand, position.x, position.y);
		if (rightHand != null)
			MessageBus.publish(new DroppedWeapon(world, this, rightHand));
		rightHand = item;
		MessageBus.publish(new EquipedItem(world, this, item));
	}
}
