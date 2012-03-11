package iryrwarosh;

public class DroppedWeapon extends Message {
	public Creature creature;
	public Weapon weapon;
	
	public DroppedWeapon(World world, Creature creature, Weapon weapon){
		super(world, "The " + creature.name() + " dropped " + weapon.name());
		this.creature = creature;
		this.weapon = weapon;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}
}
