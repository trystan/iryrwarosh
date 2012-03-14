package iryrwarosh;

public class EquipedItem extends Message {
	public Creature creature;
	public Item item;
	
	public EquipedItem(World world, Creature creature, Item item){
		super(world, "The " + creature.name() + " equipped " + item.name());
		this.creature = creature;
		this.item = item;
	}

	@Override
	public boolean involves(Creature player) {
		return creature == player || player.isInterestedIn(creature);
	}
}
