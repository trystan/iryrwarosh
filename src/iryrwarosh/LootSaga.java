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
		switch ((int)(Math.random() * 4)){
		case 0: message.world.add(heart(), message.attacked.position.x, message.attacked.position.y); break;
		case 1: message.world.add(rupies(), message.attacked.position.x, message.attacked.position.y); break;
		}
	}
	
	public Item heart(){
		return new Item("health", 3, AsciiPanel.red){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.heal(1);
			}
		};
	}
	
	public Item rupies(){
		return new Item("rupies", 4, AsciiPanel.red){
			public void onCollide(World world, Creature collider){
				world.removeItem(collider.position.x, collider.position.y);
				collider.gainMoney(1);
			}
		};
	}
}
