package iryrwarosh;

public class Evaded extends Message {
	public Creature attacker;
	public Creature evader;
	
	public Evaded(World world, Creature attacker, Creature evader){
		super(world, addArticle("The", evader.name()) + " evaded " + addArticle("the", attacker.name()));
		this.attacker = attacker;
		this.evader = evader;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || evader == player;
	}

}
