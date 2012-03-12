package iryrwarosh;

public class BlockSomeDamage extends Message {
	public Creature creature;
	public Armor armor;
	
	public BlockSomeDamage(World world, Creature creature, Armor armor) {
		super(world, "The " + armor.name() + " deflects some damage");
		this.creature = creature;
		this.armor = armor;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player;
	}

}
