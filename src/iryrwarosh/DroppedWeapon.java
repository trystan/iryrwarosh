package iryrwarosh;

public class DroppedWeapon extends Message {
	public Creature creature;
	public Weapon weapon;
	
	public DroppedWeapon(Creature creature, Weapon weapon){
		super(creature.glyph() + " dropped " + weapon.name());
		this.creature = creature;
		this.weapon = weapon;
	}
}
