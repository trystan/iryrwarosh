package iryrwarosh;

public class CreatureAiHandler implements Handler {

	@Override
	public void handle(Message message) {
		if (CallForHelp.class.isAssignableFrom(message.getClass()))
			handle((CallForHelp)message);
		
		if (Attacked.class.isAssignableFrom(message.getClass()))
			handle((Attacked)message);
	}

	public void handle(CallForHelp message) {
		for (Creature c : message.world.creatures()){
			if (!c.isFriendlyTo(message.attacked) || c == message.attacked)
				continue;
			
			if (!c.canHear(message.attacked))
				continue;
			
			c.hunt(message.attacker);
		}
	}
	
	public void handle(Attacked message) {
		if (message.attacked.hasTrait(Trait.HUNTER))
			message.attacked.hunt(message.attacker);
	}
}
