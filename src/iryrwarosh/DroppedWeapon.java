package iryrwarosh;

public class DroppedWeapon extends Message {
	public Creature creature;
	public Item weapon;
	
	public DroppedWeapon(World world, Creature creature, Item weapon){
		super(world, "The " + creature.name() + " dropped " + weapon.name());
		this.creature = creature;
		this.weapon = weapon;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}
}
