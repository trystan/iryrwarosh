package iryrwarosh;

public class SaidOutLoud extends Message {
	public Creature creature;
	
	public SaidOutLoud(Creature creature, String text) {
		super(null, creature.name() + ": " + text);
		this.creature = creature;
	}

	@Override
	public boolean involves(Creature player) {
		return player != creature && player.canHear(creature);
	}

}
