package iryrwarosh;

public class EquipedWeapon extends Message {
	public Creature creature;
	public Weapon weapon;
	
	public EquipedWeapon(World world, Creature creature, Weapon weapon){
		super(world, "The " + creature.name() + " equiped " + weapon.name());
		this.creature = creature;
		this.weapon = weapon;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}
}
