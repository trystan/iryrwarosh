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
		
		switch ((int)(Math.random() * 2)){
		case 0: message.world.add(heart(), message.attacked.position.x, message.attacked.position.y); break;
		case 1: message.world.add(rupees(), message.attacked.position.x, message.attacked.position.y); break;
		}
	}
	
	public Item heart(){
		return new Item("health", 3, AsciiPanel.red, "A heart that refils health."){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.heal(1);
			}
		};
	}
	
	public Item rupees(){
		return new Item("rupees", 4, AsciiPanel.red, "Rupees used for special actions."){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainMoney(1);
			}
		};
	}
}
