package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Creature {
	public Point position;
	
	private int regenerateCounter;
	private int poisonCounter;
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
		case EXTRA_DEFENSE: defense += 1; break;
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
	
	public int attack  = 2;
	public int defense = 1;
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
			
			if (world.tile(position.x+ox, position.y+oy).isGround()
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
		
		Creature other = world.creature(position.x+x, position.y+y);
		
		if (other == null) {
			if (canEnter(world.tile(position.x+x, position.y+y))){
				position.x += x;
				position.y += y;
				MessageBus.publish(new Moved(world, this));
			} else {
				MessageBus.publish(new BumpedIntoObstical(world, this, position.x+x, position.y+y));
			}
		} else if (isFriend(other)) {
			return;
		} else if (hasDoubleAttackedThisTurn == false 
				&& other.evadeCheck(world)){
			other.evade(world, this);
		} else if (hasDoubleAttackedThisTurn == false){
			attack(world, other, null);
			
			if (other.hp < 1)
				other.dropWeapon(world);
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
		
		other.hp -= Math.max(1, attack - other.defense);
		
		if (!isSpecial && other.hasTrait(CreatureTrait.SPIKED)){
			this.hp--;
			MessageBus.publish(new HitSpikes(world, this, other));
		}
		
		if (other.hp < 1)
			MessageBus.publish(new Killed(world, this, other));
		else
			MessageBus.publish(new Attacked(world, this, other, specialType));

		if (!isSpecial && hasTrait(CreatureTrait.POISONOUS) && other.hp > 0) {
			other.poisonCounter += 10;
			MessageBus.publish(new Poisoned(world, this, other));
		}
		
		if (!isSpecial && hasTrait(CreatureTrait.DOUBLE_ATTACK)
				&& !hasDoubleAttackedThisTurn
				&& other.hp > 0) {
			hasDoubleAttackedThisTurn = true;
			attack(world, other, null);
		}
	}

	public void evade(World world, Creature other){
		List<Point> evadeTo = new ArrayList<Point>();
		
		for (int ox = -1; ox < 2; ox++)
		for (int oy = -1; oy < 2; oy++){
			if (ox==0 && oy==0)
				continue;
			
			if (world.tile(position.x+ox, position.y+oy).isGround()
					&& world.creature(position.x+ox, position.y+oy) == null)
				evadeTo.add(new Point(position.x+ox, position.y+oy));
		}
		
		Collections.shuffle(evadeTo);
		position = evadeTo.get(0);
		MessageBus.publish(new Evaded(world, other, this));
	}
	
	public void update(){
		hasDoubleAttackedThisTurn = false;
		
		if (poisonCounter > 0){
			if (poisonCounter-- % 5 == 0)
				hp--;
		}
		
		if (hasTrait(CreatureTrait.REGENERATES) && --regenerateCounter < 0){
			regenerateCounter = 10;
			if (hp < maxHp)
				hp++;
		}
	}

	public void wander(World world){
		moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
		
		if (hasTrait(CreatureTrait.DOUBLE_MOVE))
			moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
	}
	
	public void finishingKill(World world, Creature other) {
		if (other.hp < 1)
			return;
		
		other.hp = -1;
		MessageBus.publish(new Killed(world, this, other));
	}

	public void heal(int i) {
		hp = Math.min(hp + i, maxHp);
	}

	public void gainMoney(int i) {
		money = Math.min(money + i, 255);
	}
}
