package iryrwarosh;

public class BlockSomeDamage extends Message {
	public Creature creature;
	public Creature attacker;
	public Item armor;
	
	public BlockSomeDamage(World world, Creature attacker, Creature creature, Item armor) {
		super(world, addArticle("The", (armor != null ? armor.name() : creature.name())) + " deflects some damage");
		this.creature = creature;
		this.attacker = attacker;
		this.armor = armor;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player || attacker == player;
	}

}
