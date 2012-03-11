package iryrwarosh;

public class EquipedWeapon extends Message {
	public Creature creature;
	public Weapon weapon;
	
	public EquipedWeapon(Creature creature, Weapon weapon){
		super(creature.glyph() + " equiped " + weapon.name());
		this.creature = creature;
		this.weapon = weapon;
	}
}
