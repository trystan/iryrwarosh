package iryrwarosh;

public class CreatureAiHandler implements Handler {

	@Override
	public void handle(Message message) {
		if (CallForHelp.class.isAssignableFrom(message.getClass()))
			handle((CallForHelp)message);
	}

	public void handle(CallForHelp message) {
		for (Creature c : message.world.creatures()){
			if (!message.attacked.isFriend(c) || c == message.attacked)
				continue;
			
			if (c.position.distanceTo(message.attacked.position) > 12)
				continue;
			
			c.hunt(message.attacker);
		}
	}
}
