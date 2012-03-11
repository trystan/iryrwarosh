package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Creature {
	public Point position;
	public Point homeScreenPosition;
	
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
	public int maxHp() { return maxHp; }

	private int money;
	public int money() { return money; }
	
	private List<CreatureTrait> traits = new ArrayList<CreatureTrait>();

	public void addTrait(CreatureTrait trait) {
		traits.add(trait);
		
		switch (trait){
		case EXTRA_HP: maxHp += 3; hp += 3; break;
		case EXTRA_ATTACK: attack += 1; break;
		case EXTRA_EVADE: evade += 2; break;
		}
	}

	public boolean hasTrait(CreatureTrait trait) {
		return traits.contains(trait);
	}
	
	public String describe(){
		String text = "";
		
		for (CreatureTrait trait : traits)
			text += ", " + trait.description();
		
		text = name + " (" + text.substring(2) + ")";

		if (weapon != null)
			text += " weilding a " + weapon.name();
		
		return text;
	}
	
	public int attack  = 1;
	public int evade   = 5;
	
	public int comboAttackPercent     = 0;
	public int evadeAttackPercent     = 0;
	public int circleAttackPercent    = 0;
	public int finishingAttackPercent = 0;
	public int distantAttackPercent   = 0;
	public int counterAttackPercent   = 0;

	public int comboAttackPercent() { return comboAttackPercent + (weapon == null ? 0 : weapon.comboAttackPercent); }
	public int evadeAttackPercent() { return evadeAttackPercent + (weapon == null ? 0 : weapon.evadeAttackPercent); }
	public int circleAttackPercent() { return circleAttackPercent + (weapon == null ? 0 : weapon.circleAttackPercent); }
	public int finishingAttackPercent() { return finishingAttackPercent + (weapon == null ? 0 : weapon.finishingAttackPercent); }
	public int distantAttackPercent() { return distantAttackPercent + (weapon == null ? 0 : weapon.distantAttackPercent); }
	public int counterAttackPercent() { return counterAttackPercent + (weapon == null ? 0 : weapon.counterAttackPercent); }
	
	private Weapon weapon;
	public Weapon weapon() { return weapon; }
	
	public void equip(World world, Weapon newWeapon) {
		if (weapon != null)
			dropWeapon(world);
		
		world.removeItem(position.x, position.y);
		weapon = newWeapon;
		MessageBus.publish(new EquipedWeapon(world, this, weapon));
	}

	private void dropWeapon(World world) {
		if (weapon == null)
			return;
		
		MessageBus.publish(new DroppedWeapon(world, this, weapon));
		world.add(weapon, position.x, position.y);
	}
	
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
		
		return moveable * evade;
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
		if (hasTrait(CreatureTrait.WALKER) && tile.isGround())
			return true;
		
		if (hasTrait(CreatureTrait.SWIMMER) && tile.isSwimmable())
			return true;
		
		if (hasTrait(CreatureTrait.FLYER) && tile.isFlyable())
			return true;
		
		return false;
	}

	public void moveBy(World world, int x, int y) {
		if (x==0 && y==0)
			return;
		
		if (hasTrait(CreatureTrait.TERRITORIAL)
				&& ((position.x+x) / 19 != homeScreenPosition.x || (position.y+y) / 9 != homeScreenPosition.y))
			return;
		
		Creature other = world.creature(position.x+x, position.y+y);
		
		if (other == null) {
			if (canEnter(world.tile(position.x+x, position.y+y))){
				position.x += x;
				position.y += y;
				MessageBus.publish(new Moved(world, this));
			} else {
				lastWanderX = (int)(Math.random() * 3) - 1;
				lastWanderY = (int)(Math.random() * 3) - 1;
				MessageBus.publish(new BumpedIntoObstical(world, this, position.x+x, position.y+y));
			}
		} else if (isFriend(other)) {
			return;
		} else if (hasDoubleAttackedThisTurn == false 
				&& other.evadeCheck(world)){
			other.evade(world, this);
		} else if (hasDoubleAttackedThisTurn == false){
			attack(world, other, "normal");
		}
	}
	
	public boolean isFriend(Creature other){
		return other.glyph == this.glyph;
	}
	
	private boolean hasDoubleAttackedThisTurn = false;
	public void attack(World world, Creature other, String specialType){
		if (other.hp < 1)
			return;
		
		Boolean isSpecial = specialType != null;

		other.hurt(world, this, attack, specialType);

		if (!isSpecial && other.hasTrait(CreatureTrait.SPIKED)){
			if (weapon == null || !weapon.isImuneToSpikes){
				MessageBus.publish(new HitSpikes(world, this, other));
				hurt(world, other, 1, null);
			}
		}

		if (!isSpecial && hasTrait(CreatureTrait.POISONOUS) && other.hp > 0)
			other.poisonedBy(world, this);
		
		if (!isSpecial && hasTrait(CreatureTrait.DOUBLE_ATTACK)
				&& !hasDoubleAttackedThisTurn
				&& other.hp > 0) {
			hasDoubleAttackedThisTurn = true;
			attack(world, other, "double");
		}
	}
	
	private void poisonedBy(World world, Creature attacker){
		poisonCounter += 10;
		lastPoisonedBy = attacker;
		MessageBus.publish(new Poisoned(world, attacker, this));
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
		
		if (hasTrait(CreatureTrait.REGENERATES) && --regenerateCounter < 0){
			regenerateCounter = 10;
			if (hp < maxHp)
				heal(1);
		}
		
		if (hasTrait(CreatureTrait.HIDER) && hiddenCounter-- < 1){
			hiddenCounter = 5 + (int)(Math.random() * 5);
			if (hiddenPoint == null)
				hide(world);
			else
				unhide(world);
		}
		
		if (hasTrait(CreatureTrait.ROCK_SPITTER) 
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
			if (hasTrait(CreatureTrait.TERRITORIAL)){
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
		if (hasTrait(CreatureTrait.ROCK_SPITTER))
			spitRock(world);
	}

	private void hide(World world) {
		hiddenPoint = new Point(position.x, position.y);
		MessageBus.publish(new Hid(world, this));
		position.x = -100;
		position.y = -100;
	}

	public void wander(World world){
		if (hasTrait(CreatureTrait.AGGRESSIVE))
			fightNearby(world);
		
		if (hasTrait(CreatureTrait.FEARFUL))
			fleeFromNearby(world);
		
		wanderForReal(world);
	}

	private int lastWanderX;
	private int lastWanderY;
	private boolean hasDoubleMovedThisTurn = false;
	private void wanderForReal(World world) {
		if (Math.random() < 0.33)
			lastWanderX = (int)(Math.random() * 3) - 1;
		if (Math.random() < 0.33)
			lastWanderY = (int)(Math.random() * 3) - 1;
		
		moveBy(world, lastWanderX, lastWanderY);
		
		if (hasTrait(CreatureTrait.DOUBLE_MOVE) && !hasDoubleMovedThisTurn){
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
		else
			attack(world, candidates.get((int)(Math.random() * candidates.size())), "aggressive");
	}
	
	public void fleeFromNearby(World world){
		List<Point> candidates = new ArrayList<Point>();
		
		for (Point p : position.neighbors()){
			if (canEnter(world.tile(p.x, p.y)) && world.creature(p.x, p.y) == null)
				candidates.add(new Point(p.x-position.x, p.y-position.y));
		}

		if (candidates.size() == 0)
			wanderForReal(world);
		else {
			Point target = candidates.get((int)(Math.random() * candidates.size()));
			moveBy(world, target.x, target.y);
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
		hp -= i;

		if (specialType != null)
			MessageBus.publish(new Attacked(world, attacker, this, specialType));
		
		if (hp < 1)
			MessageBus.publish(new Killed(world, attacker, this));
		else if (hasTrait(CreatureTrait.SOCIAL))
			MessageBus.publish(new CallForHelp(world, this, attacker));
	}

	public void hunt(Creature prey) {
		lastWanderX = Math.max(-1, Math.min(prey.position.x - position.x,1));
		lastWanderY = Math.max(-1, Math.min(prey.position.y - position.y,1));
	}
}
