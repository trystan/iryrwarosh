package iryrwarosh;

public class DeflectRanged extends Message {
	public Creature creature;
	public Projectile projectile;
	
	public DeflectRanged(World world, Creature creature, Projectile projectile) {
		super(world, "The " + creature.name() + " deflected the projectile");
		this.creature = creature;
		this.projectile = projectile;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}

}
