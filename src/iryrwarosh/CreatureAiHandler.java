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
			if (!message.attacked.isFriendlyTo(c) || c == message.attacked)
				continue;
			
			if (c.position.distanceTo(message.attacked.position) > 12)
				continue;
			
			c.hunt(message.attacker);
		}
	}
	
	public void handle(Attacked message) {
		if (message.attacked.hasTrait(Trait.HUNTER))
			message.attacked.hunt(message.attacker);
		else if (message.attacked.hasTrait(Trait.FEARFUL))
			message.attacked.fleeFrom(message.attacker);
	}
}
