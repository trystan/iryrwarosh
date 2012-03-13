package iryrwarosh;

import asciiPanel.AsciiPanel;

public class LootSaga implements Handler {

	@Override
	public void handle(Message message) {
		if (Killed.class.isAssignableFrom(message.getClass()))
			handle((Killed)message);
		
		if (Moved.class.isAssignableFrom(message.getClass()))
			handle((Moved)message);
	}
	
	public void handle(Moved message){
		Item item = message.world.item(message.creature.position.x, message.creature.position.y);
		if (item != null)
			item.onCollide(message.world, message.creature);
	}
	
	public void handle(Killed message){
		if (message.attacked.hasTrait(Trait.LOOTLESS))
			return;
		
		if (message.attacked.loot() != null)
			message.world.add(message.attacked.loot(), message.attacked.position.x, message.attacked.position.y);
		else
			dropRandomLoot(message.world, message.attacked.position);
	}
	
	private void dropRandomLoot(World world, Point point){
		switch ((int)(Math.random() * 4)){
		case 0: world.add(heart(), point.x, point.y); break;
		case 1: world.add(heart(), point.x, point.y); break;
		case 2: world.add(rupees_1(), point.x, point.y); break;
		case 3: world.add(rupees_5(), point.x, point.y); break;
		}
	}
	
	public Item heart(){
		return new Item("health", 3, AsciiPanel.red, "A heart that refills health."){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.heal(1);
			}
		};
	}
	
	public Item rupees_1(){
		return new Item("rupees", 4, Tile.hsv(60, 50, 75), "Rupees used for special actions."){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainMoney(1);
			}
		};
	}
	
	public Item rupees_5(){
		return new Item("rupees", 4, Tile.hsv(240, 50, 75), "Rupees used for special actions."){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainMoney(5);
			}
		};
	}
}
