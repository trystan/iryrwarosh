package iryrwarosh;

public class BlockSomeDamage extends Message {
	public Creature creature;
	public Item armor;
	
	public BlockSomeDamage(World world, Creature creature, Item armor) {
		super(world, "The " + (armor != null ? armor.name() : creature.name()) + " deflects some damage");
		this.creature = creature;
		this.armor = armor;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}

}
